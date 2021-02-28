package com.github.mckalvan.scrapi.utils

import scalaj.http.HttpResponse
import com.github.mckalvan.scrapi.models.mixins.ResponseHandler.ResponseHandler

object SCRAPIExceptions extends ResponseHandler {

  def parseHTTPError(response: HttpResponse[String]): ParsedException = parseBody(response.body).extract[ParsedException]
  case class ParsedException(message: String, error: Int)

  //noinspection ScalaStyle
  abstract class BaseHTTPException(e: ParsedException) extends Exception(s"Error code ${e.error}:\n${e.message}")
  case class ClientException(e: ParsedException) extends BaseHTTPException(e)
  case class ServerException(e: ParsedException) extends BaseHTTPException(e)

  case class BadRequestException(body: String, code: Int) extends Exception(body +  ": " + code)

  case class ParseFailureException[M: Manifest](e: String, a: M) extends Exception(s"Failed parsing JSON for ${a.getClass.getCanonicalName}:\n${e}")
  case class ExtractFromJSONException[M: Manifest](e: String, a: M) extends Exception(s"Failed extracting JSON to class ${a.getClass.getName}.\n " +
                                                                                      s"Most likely, this means that this field should be an Option\n" +
                                                                                      s"Please contact the developer of this repo @ https://github.com/McKalvan/SCRAPI.git ${e}")
}
