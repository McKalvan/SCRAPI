package com.github.mckalvan.scrapi.models.submission

import com.github.mckalvan.scrapi.models.comments.MoreComments
import com.github.mckalvan.scrapi.models.mixins.GettableObj.Gettable
import com.github.mckalvan.scrapi.models.mixins.{Awardable, Awardings, Gildable, Gildings, Repliable, Reportable, Votable}
import com.github.mckalvan.scrapi.models.subreddit.{ParsedSubreddit, Subreddit}
import com.github.mckalvan.scrapi.models.user.{GenericSubmission, ParsedUser, User}
import com.github.mckalvan.scrapi.scrapi.Params
import com.github.mckalvan.scrapi.utils.RedditConstants.{ID, T3}


object Submission extends Gettable {
	def apply(id: String, params: (String,String)*): ParsedSubmission = {
        getSubmitted[ParsedSubmission]("submission", params, Map(ID -> id))(extractHead)
    }
}

case class ParsedSubmission(all_awardings: Awardings,
														allow_live_comments: Boolean,
		                        approved_at_utc: Option[Double],
		                        approved_by: String,
		                        archived: Boolean,
		                        author: String,
		                        author_flair_background_color: String,
		                        author_flair_css_class: String,
		                        author_flair_richtext: Option[String],
		                        author_flair_template_id: String,
		                        author_flair_text: String,
		                        author_flair_text_color: String,
		                        author_flair_type: Option[String],
		                        author_fullname: Option[String],
		                        author_patreon_flair: Option[Boolean],
		                        author_premium: Option[Boolean],
		                        banned_at_utc: Option[Double],
		                        banned_by: Option[String],
		                        can_gild: Boolean,
		                        can_mod_post: Boolean,
		                        category: String,
		                        clicked: Boolean,
		                        contest_mode: Boolean,
		                        created: Double,
		                        created_utc: Double,
		                        discussion_type: String,
		                        domain: String,
		                        downs: Int,
		                        edited: Option[Double],
		                        gilded: Int,
		                        gildings: Gildings,
		                        hidden: Boolean,
		                        hide_score: Boolean,
		                        id: String,
		                        is_crosspostable: Boolean,
		                        is_meta: Boolean,
		                        is_original_content: Boolean,
		                        is_reddit_media_domain: Boolean,
		                        is_robot_indexable: Boolean,
		                        is_self: Boolean,
		                        is_video: Boolean,
		                        likes: Option[Int],
		                        link_flair_background_color: String,
		                        link_flair_css_class: String,
		                        link_flair_text: String,
		                        link_flair_text_color: String,
		                        link_flair_type: String,
		                        locked: Boolean,
		                        media_only: Boolean,
		                        mod_note: String,
		                        mod_reason_by: String,
		                        mod_reason_title: String,
		                        name: String,
		                        no_follow: Boolean,
		                        num_comments: Int,
		                        num_crossposts: Int,
		                        num_reports: String,
		                        over_18: Boolean,
		                        parent_whitelist_status: String,
		                        permalink: String,
		                        pinned: Boolean,
		                        post_hint: Option[String],
		                        pwls: Int,
		                        quarantine: Boolean,
		                        removal_reason: String,
		                        removed_by: String,
		                        removed_by_category: String,
		                        report_reasons: String,
		                        saved: Boolean,
		                        score: Int,
		                        selftext: String,
		                        selftext_html: String,
		                        send_replies: Boolean,
		                        spoiler: Boolean,
		                        stickied: Boolean,
		                        subreddit: String,
		                        subreddit_id: String,
		                        subreddit_name_prefixed: String,
		                        subreddit_subscribers: Int,
		                        subreddit_type: String,
		                        suggested_sort: String,
		                        thumbnail: String,
		                        thumbnail_height: Option[Int],
		                        thumbnail_width: Option[Int],
		                        title: String,
		                        total_awards_received: Int,
		                        ups: Int,
		                        url: String,
		                        view_count: Option[Int],
		                        visited: Boolean,
		                        whitelist_status: String,
		                        wls: Int
                      ) extends Gettable with Votable with Gildable with Awardable with Reportable with Repliable {
    val fullname: String = T3.concat(id)
		def userObj: ParsedUser = User(author)
    def subredditObj: ParsedSubreddit = Subreddit(subreddit)
		def newComments(params: Params*): MoreComments = new MoreComments(id, subreddit, "new", params:_*)
		def topComments(params: Params*): MoreComments = new MoreComments(id, subreddit, "top", params:_*)
		def hotComments(params: Params*): MoreComments = new MoreComments(id, subreddit, "hot", params:_*)
		def bestComments(params: Params*): MoreComments = new MoreComments(id, subreddit, "best", params:_*)
}




