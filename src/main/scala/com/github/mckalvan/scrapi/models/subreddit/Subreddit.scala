package com.github.mckalvan.scrapi.models.subreddit

import com.github.mckalvan.scrapi.models.comments.ParsedComment
import com.github.mckalvan.scrapi.utils.RedditConstants.{SUBREDDIT, LIMIT}
import com.github.mckalvan.scrapi.models.mixins.GettableObj.Gettable
import com.github.mckalvan.scrapi.models.mixins.{Listable, Streamable}
import com.github.mckalvan.scrapi.models.submission.ParsedSubmission
import com.github.mckalvan.scrapi.scrapi.Params

object Subreddit extends Gettable {
  def apply(name: String, params: (String, String)*): ParsedSubreddit = {
    getSubmitted[ParsedSubreddit]("about", params, Map(SUBREDDIT -> name))(extractD)
  }
}

final case class ParsedSubreddit(user_flair_background_color: Option[String],
                           submit_text_html: String,
                           restrict_posting: Boolean,
                           user_is_banned: Option[Boolean],
                           free_form_reports: Boolean,
                           wiki_enabled: Option[Boolean],
                           user_is_muted: Option[Boolean],
                           user_can_flair_in_sr: Option[Boolean],
                           display_name: String,
                           header_img: Option[String],
                           title: String,
                           icon_size: Option[List[Int]],
                           primary_color: String,
                           active_user_count: Option[Int],
                           icon_img: String,
                           display_name_prefixed: String,
                           accounts_active: Option[Int],
                           public_traffic: Boolean,
                           subscribers: Int,
                           name: String,
                           quarantine: Boolean,
                           hide_ads: Boolean,
                           emojis_enabled: Boolean,
                           advertiser_category: String,
                           public_description: String,
                           comment_score_hide_mins: Int,
                           user_has_favorited: Option[Boolean],
                           community_icon: String,
                           banner_background_image: String,
                           original_content_tag_enabled: Boolean,
                           submit_text: String,
                           description_html: String,
                           spoilers_enabled: Boolean,
                           header_title: String,
                           header_size: Option[List[Int]],
                           user_flair_position: String,
                           all_original_content: Boolean,
                           has_menu_widget: Boolean,
                           is_enrolled_in_new_modmail: Option[Boolean] ,
                           key_color: String,
                           can_assign_user_flair: Boolean,
                           created: Long,
                           wls: Option[Int],
                           show_media_preview: Boolean,
                           submission_type: String,
                           user_is_subscriber: Option[Boolean] ,
                           disable_contributor_requests: Boolean,
                           allow_videogifs: Boolean,
                           user_flair_type: String,
                           collapse_deleted_comments: Boolean,
                           emojis_custom_size: Option[Int],
                           public_description_html: String,
                           allow_videos: Boolean,
                           is_crosspostable_subreddit: Option[Boolean],
                           notification_level: Option[String],
                           can_assign_link_flair: Boolean,
                           accounts_active_is_fuzzed: Boolean,
                           submit_text_label: String,
                           link_flair_position: String,
                           user_sr_flair_enabled: Option[Boolean],
                           user_flair_enabled_in_sr: Boolean,
                           allow_discovery: Boolean,
                           user_sr_theme_enabled: Boolean,
                           link_flair_enabled: Boolean,
                           subreddit_type: String,
                           suggested_comment_sort: Option[String],
                           banner_img: String,
                           user_flair_text: Option[String],
                           banner_background_color: String,
                           show_media: Boolean,
                           id: String,
                           user_is_moderator: Option[Boolean],
                           over18: Boolean,
                           description: String,
                           submit_link_label: String,
                           user_flair_text_color: Option[String],
                           restrict_commenting: Boolean,
                           user_flair_css_class: Option[String],
                           allow_images: Boolean,
                           lang: String,
                           whitelist_status: String,
                           url: String,
                           created_utc: Double,
                           banner_size: Option[List[Int]],
                           mobile_banner_image: String,
                           user_is_contributor: Option[Boolean]) extends Listable {
  protected val endpointParams = Map(SUBREDDIT -> display_name)

  // TODO: Create models for these responses
  def about(params: Params*): ResponseObj = getSubmitted("about", params, endpointParams)(parseD)
  def rules(params: Params*): ResponseObj = getSubmitted("rules", params, endpointParams)(parseD)
  def traffic(params: Params*): ResponseObj = getSubmitted("traffic", params, endpointParams)(parseD)
  def sticky(params: Params*): ResponseObj = getSubmitted("sticky", params,  endpointParams)(parseD)
  def sidebar(params: Params*): ResponseObj = getSubmitted("sidebar", params, endpointParams)(parseD)

  def newSubmissions(params: Params*): Seq[ParsedSubmission] = {
    paginateResponse[ParsedSubmission]("new_submissions", params, endpointParams)
  }
  def topSubmissions(params: Params*): Seq[ParsedSubmission] = {
    paginateResponse[ParsedSubmission]("top_submissions", params, endpointParams)
  }
  def hotSubmissions(params: Params*): Seq[ParsedSubmission] = {
    paginateResponse[ParsedSubmission]("top_submissions", params, endpointParams)
  }
  def risingSubmissions(params: Params*): Seq[ParsedSubmission] = {
    paginateResponse[ParsedSubmission]("rising_submissions", params, endpointParams)
  }
  def sortSubmissions(params: Params*): Seq[ParsedSubmission] = {
    paginateResponse[ParsedSubmission]("sort_submissions", params, endpointParams)
  }
  def controversialSubmissions(params: Params*): Seq[ParsedSubmission] = {
    paginateResponse[ParsedSubmission]("controversial_submissions", params, endpointParams)
  }
  def randomSubmission(params: Params*): ParsedSubmission = {
    getSubmitted[ParsedSubmission]("random_submissions", params, endpointParams)(extractD)
  }

  def stream: SubredditStream = SubredditStream(display_name)
}

final case class SubredditStream(subreddit: String) extends Streamable {
  implicit val defaultTuple: (String,String) = ("","")
  def submissions(params: Params*):  Stream[ParsedSubmission] = {
    streamGenerator[ParsedSubmission]("new_submissions", (LIMIT, "100") +: params, Map(SUBREDDIT -> subreddit))(paginateResponse[ParsedSubmission])
  }
  def comments(params: Params*): Stream[ParsedComment] = {
    streamGenerator[ParsedComment]("subreddit_comments", (LIMIT, "100") +: params, Map(SUBREDDIT -> subreddit))(paginateResponse[ParsedComment])
  }
}

