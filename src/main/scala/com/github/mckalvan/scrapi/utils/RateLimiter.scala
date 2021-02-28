package com.github.mckalvan.scrapi.utils

import com.github.mckalvan.scrapi.models.mixins.ResponseHandler.HeaderObj
import com.github.mckalvan.scrapi.utils.RedditConstants.{RATELIMIT_REMAINING, RATELIMIT_USED, RATELIMIT_RESET}
import com.github.mckalvan.scrapi.utils.SCRAPILogger.logger
import scala.math.min

// Used to parse header of responses to keep usage of Reddit's API to a responsible level
final case class RateLimiter(used: Option[Int], remaining: Option[Float], nextRequestTimestamp: Option[Double], resetTimestamp: Option[Double]){

  def update(headers: HeaderObj): RateLimiter = {
    if (headers.contains(RATELIMIT_REMAINING)) {
      val currentTime = System.currentTimeMillis()
      val prevRemaining = remaining.getOrElse(Float.MaxValue)
      val parsedUsed = headers.getHeader[Vector[String]](RATELIMIT_USED).get.head.toInt
      val parsedRemaining = headers.getHeader[Vector[String]](RATELIMIT_REMAINING).get.head.toFloat
      val secondsUntilReset = headers.getHeader[Vector[String]](RATELIMIT_RESET).get.head.toInt

      val resetTimestamp = currentTime + secondsUntilReset
      val estimatedClients = if (used.isEmpty && prevRemaining > parsedRemaining) prevRemaining - parsedRemaining
                             else 1f

      val adjRemaining = currentTime + (estimatedClients * secondsUntilReset) / parsedRemaining
      if (parsedRemaining <= 0) RateLimiter(Some(parsedUsed), Some(parsedRemaining), Some(resetTimestamp), Some(resetTimestamp))
      else RateLimiter(Some(parsedUsed), Some(parsedRemaining), Some(resetTimestamp), Some(min(adjRemaining,resetTimestamp)))

    } else {
      if (used.nonEmpty) new RateLimiter(Some(used.get + 1), Some(remaining.get - 1), nextRequestTimestamp, resetTimestamp)
      else this
    }
  }

  def delay: Unit = {
    if (nextRequestTimestamp.isEmpty) ()
    else {
      val sleepSeconds = nextRequestTimestamp.get - System.currentTimeMillis()
      if (sleepSeconds > 0 && remaining.get < 10) {
        logger.info(s"rate limited exceeded, sleeping for $sleepSeconds seconds")
        Thread.sleep(sleepSeconds.toLong*1000)
      }
      else ()
    }
  }
}
