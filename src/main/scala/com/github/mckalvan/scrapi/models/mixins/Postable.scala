package com.github.mckalvan.scrapi.models.mixins

import com.github.mckalvan.scrapi.models.mixins.ResponseHandler.ResponseHandler
import com.github.mckalvan.scrapi.utils.SCRAPIExceptions.{ExtractFromJSONException, ParseFailureException}
import com.github.mckalvan.scrapi.models.Reddit
import com.github.mckalvan.scrapi.scrapi.Params
import com.github.mckalvan.scrapi.utils.EndpointUtil.setEndpoint
import org.json4s.MappingException
import org.json4s.ParserUtil.ParseException

// Trait which describes an object which can post to the Reddit API in some way (IE commenting, upvoting, etc)
trait Postable extends ResponseHandler {

  def postForm[A](target: String, params: Seq[Params], endpointParams: Map[String, String] = Map())
              (f: JsonParser[A])
              (implicit m: Manifest[A]): A = {
    val endpoint = setEndpoint(target, endpointParams)
    try{
      val parsedBody = Reddit.session.post(endpoint, params)
      f(parsedBody)
    } catch {
      case e: ParseException => throw ParseFailureException(e.toString, m)
      case f: MappingException => throw ExtractFromJSONException(f.toString, m)
    }
  }

  /*
    Posts to certain Reddit API endpoints return an empty body in the return, so the body gets parsed to an empty case class w/ no fields
   */
  case class EmptyResponse()
  protected def extractEmpty: JsonParser[EmptyResponse] = (json: ResponseObj) => EmptyResponse()
}


