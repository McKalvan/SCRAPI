package com.github.mckalvan.scrapi.utils

import java.nio.file.Path

import com.github.mckalvan.scrapi.utils.OAuthUtil.authenticate
import com.github.mckalvan.scrapi.models.mixins.ResponseHandler.ResponseHandler

// Used to make ratelimited calls against the Reddit API. Carries the user token if the user authorizes their session.
final case class Session(oauth: RedditOAuthToken = RedditOAuthToken("")) extends ResponseHandler {
    // TODO: Make rateLimiter variable immutable
    var rateLimiter: RateLimiter = RateLimiter(None, None, None, None)

    def get(endpoint: String, vargs: Seq[(String,String)]): ResponseObj = {
        rateLimiter.delay
        val response = HTTPUtil.get(endpoint, vargs)(oauth.token)
        val parsedHeaders = parseHeaders(response.headers)
        rateLimiter = rateLimiter.update(parsedHeaders)
        parseBody(response.body)
    }

    def post(endpoint: String, body: Seq[(String,String)], vargs: Map[String, String] = Map[String,String]()): ResponseObj = {
        rateLimiter.delay
        val response = HTTPUtil.post(endpoint,body,vargs)(oauth.token)
        val parsedHeaders = parseHeaders(response.headers)
        rateLimiter = rateLimiter.update(parsedHeaders)
        parseBody(response.body)
    }
}


object Session {
    def authorize(userName: String, password: String, clientId: String, clientSecret: String, userAgent: String): Session = {
        val t: RedditOAuthToken = authenticate(userName, password, clientId, clientSecret, userAgent)
        Session(t)
    }
}



