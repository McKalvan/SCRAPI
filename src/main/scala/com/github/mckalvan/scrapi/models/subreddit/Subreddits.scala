package com.github.mckalvan.scrapi.models.subreddit

import com.github.mckalvan.scrapi.utils.RedditConstants.LIMIT
import com.github.mckalvan.scrapi.models.mixins.GettableObj.Gettable
import com.github.mckalvan.scrapi.models.mixins.Streamable
import com.github.mckalvan.scrapi.scrapi.Params

object Subreddits extends Streamable {
  def default(params: Params*): Seq[ParsedSubreddit] = paginateResponse[ParsedSubreddit]("default_subreddits", params)
  def trending(params: Params*): SubredditsListing = getSubmitted("trending_subreddits", params)(extractNothing[SubredditsListing])
  def popular(params: Params*): Seq[ParsedSubreddit] = paginateResponse[ParsedSubreddit]("popular_subreddits", params)
  def new_subreddits(params: Params*): Seq[ParsedSubreddit] = paginateResponse[ParsedSubreddit]("new_subreddits", params)
  def stream(params: Params*): Stream[ParsedSubreddit] = {
    streamGenerator[ParsedSubreddit]("new_subreddits", (LIMIT, "100") +: params, Map())(paginateResponse[ParsedSubreddit])
  }
}

final case class SubredditsListing(subreddit_names: List[String], comment_count: Int, comment_url: String) extends Gettable {
  def listedSubreddits(params: Params*): Seq[ParsedSubreddit] = subreddit_names.map(x => Subreddit(x, params:_*))
}
