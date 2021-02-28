package com.github.mckalvan.scrapi.models.mixins

import com.github.mckalvan.scrapi.models.mixins.ResponseHandler.ResponseHandler
import com.github.mckalvan.scrapi.utils.SCRAPIExceptions.{ExtractFromJSONException, ParseFailureException}
import com.github.mckalvan.scrapi.models.Reddit
import com.github.mckalvan.scrapi.scrapi.Params
import com.github.mckalvan.scrapi.utils.EndpointUtil.setEndpoint
import com.github.mckalvan.scrapi.utils.Session
import org.json4s.MappingException
import org.json4s.ParserUtil.ParseException

// Holds the Gettable trait w/ its associated implicits
object GettableObj{

  /*
    Allows extended subclasses to "get" objects from Reddit's API.
    Resulting Gettable objects are implicitly writable to any format in any form described in the Writable trait
   */
  trait Gettable extends ResponseHandler   {
      val session: Session = Reddit.session


      protected def getSubmitted[A](target: String,
                                  params: Seq[Params],
                                  endpointParams: Map[String, String] = Map[String, String]())
                                 (f: JsonParser[A])
                                 (implicit m: Manifest[A]): A = {
        val endpoint = setEndpoint(target, endpointParams)
        try {
            val response = session.get(endpoint, params)
            f(response)
        }
        catch {
            case e: ParseException => throw ParseFailureException(e.toString, m)
            case f: MappingException => throw ExtractFromJSONException(f.toString, m)
        }
      }
  }

  implicit class GettableOpts[T <: Gettable](t: T) extends Writable(t)
}
