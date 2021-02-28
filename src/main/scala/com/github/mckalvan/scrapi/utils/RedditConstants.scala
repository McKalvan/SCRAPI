package com.github.mckalvan.scrapi.utils

object RedditConstants {
  val DATA = "data"
  val CHILDREN = "children"
  val SUBREDDIT = "subreddit"
  val ID = "id"
  val ARTICLE = "article"
  val KIND = "kind"
  val MORE = "more"
  val MORE_CHILDREN = "more_children"
  val REPLIES = "replies"
  val PARENT_ID = "parent_id"
  val AFTER = "after"
  val DIST = "dist"
  val LIMIT = "limit"
  val USER = "user"
  val THINGS = "things"
  val JSON = "json"
  val COMMENT_SUBMISSION = "comment_submission"
  val SORT = "sort"

  val T1 = "t1_"
  val T2 = "t2_"
  val T3 = "t3_"

  // Rate-limiting constants
  val RATELIMIT_USED = "x-ratelimit-used"
  val RATELIMIT_REMAINING = "x-ratelimit-remaining"
  val RATELIMIT_RESET = "x-ratelimit-reset"
}
