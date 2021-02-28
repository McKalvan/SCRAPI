package com.github.mckalvan.scrapi.utils

import scala.annotation.tailrec

// Object for mapping endpoint names to their respective URL components
object EndpointUtil {
    val OAUTH_ENDPOINT = "https://www.reddit.com/api/v1/access_token"
    private val BASE_URL = "https://oauth.reddit.com"
    private val JSON_POSTFIX = ".json"

    def setEndpoint(target: String, endpointSettings: Map[String,String] = Map[String,String]()): String = {
      val endpoint: String = target match {
        case "best_submissions" => "/best"
        case "new_submissions" => "/r/$SUBREDDIT$/new"
        case "random_submissions" => "/r/$SUBREDDIT$/random"
        case "rising_submissions" => "/r/$SUBREDDIT$/rising"
        case "sort_submissions" => "/r/$SUBREDDIT$/sort"
        case "top_submissions" => "/r/$SUBREDDIT$/top"
        case "hot_submissions" => "/r/$SUBREDDIT$/hot"
        case "controversial_submissions" => "/r/$SUBREDDIT$/controversial"

        case "submission" => "/comments/$ID$/"
        case "comments" => "/r/$SUBREDDIT$/comments/$ARTICLE$"
        case "subreddit_comments" => "/r/$SUBREDDIT$/comments"
        case "extended_comments" => "/r/$ARTICLE$/$ID$"
        case "more_children" => "/api/morechildren"
        case "comment_submission" => "/comments/$ARTICLE$/_/$ID$"
        case "info" => "/api/info/"
        case "subreddit" => "/r/$SUBREDDIT$"
        case "about" => "/r/$SUBREDDIT$/about"
        case "edit" => "/r/$SUBREDDIT$/about/edit"
        case "rules" => "/r/$SUBREDDIT$/about/rules"
        case "traffic" => "/r/$SUBREDDIT$/about/traffic"
        case "sidebar" => "/r/$SUBREDDIT$/sidebar"
        case "sticky" => "/r/$SUBREDDIT$/sticky"

        case "trending_subreddits" => "/api/trending_subreddits"
        case "gold_subreddits" => "/subreddits/gold"
        case "popular_subreddits" => "/subreddits/popular"
        case "new_subreddits" => "/subreddits/new"
        case "default_subreddits" => "/subreddits/default"
        case "search_subreddits" => "/subreddits/search"
        case "gold_subreddits" => "/subreddits/gold"

        case "user" => "/user/$USER$/"
        case "user_about" => "/user/$USER$/about"

        case "vote" => "/api/vote"
        case "gild_thing" => "/api/v1/gold/give/$USER$"
        case "report" => "/api/report"
        case "comment" => "/api/comment"
      }
      setEndpointParams(BASE_URL + endpoint + JSON_POSTFIX, endpointSettings)
    }

      @tailrec
      private def setEndpointParams(endpoint: String, settings: Map[String, String]): String = {
        if (settings.isEmpty) endpoint
        else setEndpointParams(endpoint.replace("$" + settings.head._1.toUpperCase + "$", settings.head._2  ), settings.drop(1))
      }

}
