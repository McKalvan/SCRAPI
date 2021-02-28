package com.github.mckalvan.scrapi.models.mixins

import com.github.mckalvan.scrapi.scrapi.Params
import com.github.mckalvan.scrapi.utils.RedditConstants.{ID }

import scala.annotation.tailrec
import scala.math.min
import scala.util.Random

// TODO: Stream support for Scala 2.11

/*
 * Trait used to enable "streaming" events from Reddit API by polling a given endpoint every so often
 * and parsing the resulting response to a lazily evaluated stream. Any submission id seen in a microbatch will be
 * added to a BoundedSet instance, which is configured to hold the last 301 observed submission ids processed
 * Any new ids will be added to the stream, while submission ids that already exist in the list get dropped on the floor
 */
trait Streamable extends Listable {
  type streamableFunc[A] = (String, Seq[Params], Map[String,String]) => Seq[A]
  val EXPONENTIAL_COUNTER_START = 16
  val MAX_BOUNDED_SET_SIZE = 301

  protected def streamGenerator[A <: {val id: String}](target: String,
                                                       params: Seq[Params],
                                                       endpointParams: Map[String,String] = Map[String,String](),
                                                       pauseAfter: Int = 0,
                                                       skipExisting: Boolean = true,
                                                       attributeName: String = ID
                                                      )
                                                      (f: streamableFunc[A])
                                                      (implicit m: Manifest[A]): Stream[A] = {

    val exponentialCounter: ExponentialCounter = ExponentialCounter(EXPONENTIAL_COUNTER_START)

    def streamHelper(skipExisting: Boolean,
                     responsesWithoutNew: Integer,
                     seenAttributes: BoundedSet[String],
                     exponentialCounter: ExponentialCounter,
                     params: Seq[Params]
                    ): Stream[Seq[A]] = {
      val rawResponses = f(target, params, endpointParams)
      val newResponses = rawResponses.filterNot(x => seenAttributes.contains(x.id.toString))
      val updatedSeen = seenAttributes.addAll(newResponses.map(x => x.id.toString):_*)

      if(!skipExisting && newResponses.nonEmpty) {
        newResponses #:: streamHelper(skipExisting = false, 0, updatedSeen, exponentialCounter.reset, params)}
      else {
        if (responsesWithoutNew > pauseAfter) Thread.sleep(exponentialCounter.value)
        Nil #:: streamHelper(skipExisting = false, responsesWithoutNew + 1, updatedSeen, exponentialCounter.counter, params)
      }
    }

    streamHelper(skipExisting, 0, BoundedSet(MAX_BOUNDED_SET_SIZE), exponentialCounter, params).flatten
  }

  // Used for exponential backoff if a batch does not yield new records
  private case class ExponentialCounter(maxCounter: Int, base: Int = 1, value: Long = 0){
    private val r: Random = Random

    def counter: ExponentialCounter = {
      val maxJitter: Float = base/16F
      val value: Long = (base + r.nextFloat() * maxJitter - maxJitter/2).toLong * 1000
      ExponentialCounter(maxCounter, min(base*2, maxCounter), value)
    }
    def reset: ExponentialCounter = ExponentialCounter(maxCounter)
  }

  /*
   * Implementation of a Set object which has an upper bound to the number of elements that it can store
   * The set will continue to grow until it reaches maxNumber of elements, after which the last element will need to be
   * popped off for a new element to take its place
   *
   * // TODO: Remove cruft
   */
  abstract class BoundedSet[+A](maxItems: Int){
    def contains[B >: A](elem: B): Boolean
    def isEmpty: Boolean
    def tail: BoundedSet[A]
    def head: A
    def apply[B >: A](elem: B): Boolean = contains(elem)
    def add[B >: A](elem: B): BoundedSet[B]
    def addAll[B >: A](elems: B*): BoundedSet[B]
    def +[B >: A](elem: B): BoundedSet[B] = add(elem)
    def foreach(f: A => Unit): Unit
    def length: Int
    def reverse: BoundedSet[A]
  }

  private class EmptyBoundedSet[Nothing](maxItems: Int) extends BoundedSet[Nothing](maxItems){
    def contains[B >: Nothing](elem: B): Boolean = false
    def isEmpty: Boolean = true
    def tail: BoundedSet[Nothing] = this
    def head: Nothing = throw new NoSuchElementException("head method does not exist for EmptyBoundedSet")
    def add[B >: Nothing](elem: B): BoundedSet[B] = new NonEmptyBoundedSet[B](maxItems, elem, this)
    def addAll[B >: Nothing](elems: B*): BoundedSet[B] = new NonEmptyBoundedSet[B](maxItems, elems.head, this).addAll(elems.tail:_*)
    def foreach(f: Nothing => Unit): Unit = ()
    def length: Int = 0
    def reverse: BoundedSet[Nothing] = this
  }

  private class NonEmptyBoundedSet[+A](maxItems: Int, currentHead: A, currentTail: BoundedSet[A]) extends BoundedSet[A](maxItems){
    def contains[B >: A](elem: B): Boolean = (elem == head) || tail.contains(elem)
    def isEmpty: Boolean = false
    def tail: BoundedSet[A] = currentTail
    def head: A = currentHead

    def foreach(f: A => Unit): Unit = {
      f(head)
      tail.foreach(f)
    }

    def length: Int = {
      @tailrec
      def lengthHelper(bs: BoundedSet[A], acc: Integer): Int = {
        if (bs.isEmpty) acc
        else lengthHelper(bs.tail, acc + 1)
      }
      lengthHelper(this, 0)
    }

    def reverse: BoundedSet[A] = {
      @tailrec
      def reverseHelper(bs: BoundedSet[A], acc: BoundedSet[A]): BoundedSet[A] = {
        if(bs.isEmpty) acc.add(head)
        else reverseHelper(bs.tail, acc.add(bs.head))
      }
      reverseHelper(this, new EmptyBoundedSet[A](maxItems))
    }

    def removeLast: BoundedSet[A] = this.reverse.tail.reverse

    def add[B >: A](elem: B): BoundedSet[B] = {
      if (this.contains(elem)) this
      else if (this.length == maxItems) new NonEmptyBoundedSet[B](maxItems, elem, this.removeLast )
      else new NonEmptyBoundedSet[B](maxItems, elem, this)
    }

    def addAll[B >: A](elems: B*): BoundedSet[B] = if(elems.isEmpty) this else this.add(elems.head).addAll(elems.tail:_*)
  }

  private object BoundedSet {
    def apply[A](maxItems: Int, valSeq: A*): BoundedSet[A] = {
      @tailrec
      def buildSet(valSeq: Seq[A], acc: BoundedSet[A]): BoundedSet[A] = {
        if (valSeq.isEmpty) acc
        else buildSet(valSeq.tail, acc + valSeq.head)
      }
      buildSet(valSeq.toSeq, new EmptyBoundedSet[A](maxItems))
    }
  }
}

