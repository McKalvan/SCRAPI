package com.github.mckalvan.scrapi.models.comments

import com.github.mckalvan.scrapi.models.mixins.GettableObj.Gettable
import com.github.mckalvan.scrapi.models.mixins.Postable
import com.github.mckalvan.scrapi.scrapi.Params
import com.github.mckalvan.scrapi.utils.RedditConstants.{SUBREDDIT, ARTICLE, DATA, CHILDREN, SORT, T3, MORE_CHILDREN,KIND, MORE, COMMENT_SUBMISSION, ID, PARENT_ID, REPLIES}

import scala.annotation.tailrec


final case class MoreComments(submissionId: String, subreddit: String, sort: String, params: Params*) extends Gettable with Postable {
  private val response: ResponseObj = getSubmitted("comments", params, Map(SUBREDDIT -> subreddit, ARTICLE -> submissionId, "sort" -> sort))(parseNothing)
  private val (flatComments, flatMore) = flattenResponse((response.children.tail.head \ DATA \ CHILDREN).children)
  private val (extendedChildren, baseChildren) = flatMore.partition(x => (x \ DATA).arrayIsEmpty(CHILDREN))
  private val extendedIDs: Seq[String] = getExtendedChildren(extendedChildren)
  val comments: Seq[ParsedComment] = flatComments.map(extractD[ParsedComment])

  private val PARTITION_WINDOW = 32

  // Makes calls against Reddit's "MoreComments" API endpoint to paginate through a comment tree
  def getMore(limit: Int, limitRequests: Int, params: Params*): Seq[ParsedComment] = {
    val bodyParams = params ++ Seq("api_type"  -> "json","link_id" -> T3.concat(submissionId), SORT -> sort)

    @tailrec
    def getMoreHelper(usedRequests: Int, ids: Seq[String], extendedIDs: Seq[String], observed: Seq[String], response: Seq[ParsedComment]): Seq[ParsedComment] = {
      if ((ids ++ extendedIDs).isEmpty || response.length >= limit || usedRequests >= limitRequests && limitRequests != 0) response
      else {

        // Partitions the list of ids into a nested list of ids containing at most 32 ids each
        val prePartitionList = ids.sliding(PARTITION_WINDOW, PARTITION_WINDOW).toList.map(_.mkString(","))
        val partitionList = if (prePartitionList.length + usedRequests < limitRequests && limitRequests != 0) prePartitionList
                            else prePartitionList.take(limitRequests - usedRequests)

        // Posts each list of comment ids to Reddit API sequentially and returns flattened list of unparsed comments
        val responses = partitionList.flatMap(ids => postForm(MORE_CHILDREN, bodyParams :+ (CHILDREN -> ids))(parseJDT).children)

        // Separate responses by if they are a "Comment" object or a "MoreComments" object
        val (flatBaseMore, flatBaseComments) = responses.partition(x => (x \ KIND).toString.equals(MORE))

        // Parses any and all comment objects
        val parsedComments = flatBaseComments.map(x => (x \ DATA).extract[ParsedComment])

        // Gets comments from Reddit API using extended ids and parses them into Comment objects
        val extendedResponses = extendedIDs.map(id => getSubmitted(COMMENT_SUBMISSION, Seq[(String,String)](), Map(ID-> id, ARTICLE -> submissionId))(parseNothing))
        val flatExtended = extendedResponses.map(x => flattenResponse((x.children.tail.head \ DATA \ CHILDREN).children))
        val extendedComments = flatExtended.flatMap(_._1).map(extractD[ParsedComment])

        // Gets new ids from "MoreChildren" response objects
        val (moreEmpty, moreChildren) = flatBaseMore.partition(x => (x \ DATA).arrayIsEmpty(CHILDREN))
        val (extendedEmpty, extendedMore) = flatExtended.flatMap(_._2).partition(x => (x \ DATA).arrayIsEmpty(CHILDREN))
        val parsedIds = getChildren(moreChildren ++ extendedMore)
        val newExtendedIds = getExtendedChildren(moreEmpty ++ extendedEmpty)
        val updatedObservedIds = observed ++ ids ++ extendedIDs

        getMoreHelper(prePartitionList.length + usedRequests, parsedIds.diff(updatedObservedIds), newExtendedIds.diff(updatedObservedIds), updatedObservedIds, response ++ parsedComments ++ extendedComments)
      }
    }
    getMoreHelper(0, getChildren(baseChildren), extendedIDs, Seq(), Seq()) ++ comments
  }

  // These methods are used to submission ids from comment objects
  private def getChildren(response: Seq[ResponseObj]): Seq[String] = response.flatMap(x => (x \ DATA \ CHILDREN).children).map(x => x.toString).distinct
  private def getExtendedChildren(response: Seq[ResponseObj]): Seq[String] = response.map(x => (x \ DATA \ PARENT_ID).toString.split("_")(1)).distinct

  // Methods used to flatten nested comment tree
  private def flattenResponse(s: Seq[ResponseObj]): (Seq[ResponseObj], Seq[ResponseObj]) = filteredBFS(s, (Seq(),Seq()))(f)

  @tailrec
  private def filteredBFS[A <: ResponseObj](s: Seq[A], accum: (Seq[A], Seq[A]))(f: A => Seq[A]): (Seq[A],Seq[A]) = {
    if (s.isEmpty) accum
    else {
      val filtered = filter(s)
      filteredBFS(filtered._1.flatMap(x => f(x)), (filtered._1 ++ accum._1, filtered._2 ++ accum._2))(f)
    }
  }

  private def filter[A <: ResponseObj](s: Seq[A]): (Seq[A], Seq[A]) = s.partition(x => !(x \ KIND).toString.equals(MORE))
  private def f(s: ResponseObj): Seq[ResponseObj] = (s \ DATA \ REPLIES \ DATA \ CHILDREN).children
}


