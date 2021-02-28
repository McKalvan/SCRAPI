package com.github.mckalvan.scrapi.utils

import com.github.mckalvan.scrapi.utils.SCRAPIExceptions.{BadRequestException, ClientException, ServerException, parseHTTPError}
import scalaj.http._



object HTTPUtil {

  // Check the status of gettable object implicitly whenever one comes up and throw an exception if there is an error-related status code
  implicit def checkStatus(response: HttpRequest): HttpResponse[String] = {
    val responseStr = response.asString
    if (responseStr.isError) {
      if (responseStr.body.isInstanceOf[String]) throw BadRequestException(responseStr.body, responseStr.code)
      val parsedError = parseHTTPError(responseStr)
      if (responseStr.isClientError) throw ClientException(parsedError)
      else if (responseStr.isServerError) throw ServerException(parsedError)
    }
    responseStr
  }

  def get(endpoint: String, vargs: Seq[(String,String)] = Seq[(String,String)]())(implicit token: String): HttpResponse[String]= {
    Http(endpoint).header("Authorization", "bearer " + token).params(vargs)
  }

  def post(endpoint: String, body: Seq[(String,String)], vargs: Map[String, String] = Map[String,String]())(implicit token: String): HttpResponse[String] = {
    Http(endpoint).header("Authorization", "bearer " + token).postForm(body).params(vargs)
  }
}
