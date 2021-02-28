package com.github.mckalvan.scrapi.utils

import com.github.mckalvan.scrapi.utils.EndpointUtil.OAUTH_ENDPOINT
import com.github.mckalvan.scrapi.models.mixins.ResponseHandler.ResponseHandler

import scalaj.http.{Base64, Http, HttpResponse}


object OAuthUtil extends ResponseHandler {

  // Used to authorize a user w/ their Reddit API credentials
  def authenticate(username: String, password: String, clientId: String, clientSecret: String, userAgent: String): RedditOAuthToken = {

    val response: HttpResponse[String]  = Http(OAUTH_ENDPOINT)
      .postForm(Seq(
        "grant_type" -> "password",
        "username" -> username,
        "password" -> password
      ))
      .header("Authorization","Basic " + genSignature(clientId, clientSecret))
      .header("User-Agent", userAgent)
      .asString

    // TODO: Handle invalid login response
    RedditOAuthToken(parseBody(response.body) \ "access_token")
  }

  private def genSignature(key : String, secret : String) : String = { Base64.encodeString(key + ":" + secret) }
}

case class RedditOAuthToken(token: String)