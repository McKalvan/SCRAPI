package com.github.mckalvan.scrapi.models.comments

import com.github.mckalvan.scrapi.models.mixins.{Awardable, Awardings, Gildable, Gildings, Listable, Repliable, Reportable, Votable}
import com.github.mckalvan.scrapi.models.user.{GenericSubmission, ParsedUser, User}
import com.github.mckalvan.scrapi.models.mixins.GettableObj.Gettable
import com.github.mckalvan.scrapi.models.submission.{ParsedSubmission, Submission}
import com.github.mckalvan.scrapi.models.subreddit.{ParsedSubreddit, Subreddit}
import com.github.mckalvan.scrapi.utils.RedditConstants.{ID, T1}

/*
  Classes used to encapsulate a single comment object returned by the API from the comment endpoint.
 */

object Comment extends Gettable{
    def apply(commentId: String, params: (String,String)*): ParsedComment = {
      getSubmitted[ParsedComment]("info", params :+ (ID -> T1.concat(commentId)))(extractHead)
    }
}

final case class ParsedComment(all_awardings: Awardings,
                               approved_at_utc: Option[Double],
                               approved_by: String,
                               archived: Boolean,
                               associated_award: String,
                               author: String,
                               author_flair_background_color: Option[String],
                               author_flair_css_class: Option[String],
                               author_flair_richtext: Option[List[String]],
                               author_flair_template_id: Option[Int],
                               author_flair_text: Option[String],
                               author_flair_text_color: Option[String],
                               author_flair_type: Option[String],
                               author_fullname: Option[String],
                               author_patreon_flair: Option[Boolean],
                               author_premium: Option[Boolean],
                               awarders: List[String],
                               banned_at_utc: Option[Double],
                               banned_by: String,
                               body: String,
                               body_html: String,
                               can_gild: Boolean,
                               can_mod_post: Boolean,
                               collapsed: Boolean,
                               collapsed_because_crowd_control: Option[Boolean],
                               collapsed_reason: String,
                               controversiality: Int,
                               created: Double,
                               created_utc: Double,
                               distinguished: String,
                               downs: Int,
                               edited: Option[Boolean],
                               gilded: Int,
                               gildings: Gildings,
                               id: String,
                               is_submitter: Boolean,
                               likes: Option[Int],
                               link_author: Option[String],
                               link_id: Option[String],
                               link_permalink: Option[String],
                               link_title: Option[String],
                               link_url: Option[String],
                               locked: Boolean,
                               mod_note: String,
                               mod_reason_by: String,
                               mod_reason_title: String,
                               mod_reports: List[String],
                               name: String,
                               no_follow: Boolean,
                               num_comments: Option[Int],
                               num_reports: Option[Int],
                               over_18: Option[Boolean],
                               parent_id: String,
                               permalink: String,
                               quarantine: Option[Boolean],
                               removal_reason: String,
                               report_reasons: String,
                               saved: Boolean,
                               score: Int,
                               score_hidden: Boolean,
                               send_replies: Boolean,
                               steward_reports: List[String],
                               stickied: Boolean,
                               subreddit: String,
                               subreddit_id: String,
                               subreddit_name_prefixed: String,
                               subreddit_type: String,
                               total_awards_received: Int,
                               ups: Int,
                               user_reports: List[String]
                              ) extends Listable with Votable with Gildable with Awardable with Reportable with Repliable {
  val fullname: String = T1.concat(id)

  def userObj: ParsedUser = User(author)
  def subredditObj: ParsedSubreddit = Subreddit(subreddit)
  def submissionObj: ParsedSubmission = Submission(parent_id.split("_").last)
}

