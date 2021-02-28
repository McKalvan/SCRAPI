package com.github.mckalvan.scrapi.models.mixins

import com.github.mckalvan.scrapi.models.mixins.GettableObj.Gettable
import com.github.mckalvan.scrapi.scrapi.Params
import com.github.mckalvan.scrapi.utils.RedditConstants.{LIMIT, AFTER, DIST}

import scala.annotation.tailrec
import scala.math.min

// The Listable trait describes gettable objects which are capable of pagination (IE submissions in a subreddit, comments in a submission)
trait Listable extends Gettable {

  val DEFAULT_LIMIT = 100

  protected def paginateResponse[A](target: String,
                                 params: Seq[Params],
                                 endpointParams: Map[String,String] = Map[String,String]()
                                )
                                (implicit m: Manifest[A]): Seq[A] = {

    val limit = params.toMap.getOrElse(LIMIT, DEFAULT_LIMIT).toString.toInt

    @tailrec
    def paginateHelper(after: String, yielded: Integer, responses: Seq[A]): Seq[A] = {
      if(yielded >= limit) responses
      else {
        val param = params ++ Seq((AFTER, after) , (LIMIT, min(DEFAULT_LIMIT, limit - yielded).toString))
        val parsedBody = getSubmitted(target, param, endpointParams)(parseDC)
        val flatSubmissions = parsedBody.children.map(extractD)
        val parsedAfter = (parsedBody  \ AFTER).toString
        val parsedYield = (parsedBody \ DIST).toInt
        if (parsedAfter.isEmpty || parsedAfter.equals(after)) responses ++ flatSubmissions
        else paginateHelper(parsedAfter, yielded + parsedYield, responses ++ flatSubmissions)
      }
    }
    paginateHelper("", 0, Seq())
  }
}
