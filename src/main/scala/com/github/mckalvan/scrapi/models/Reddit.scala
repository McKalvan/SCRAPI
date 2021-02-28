package com.github.mckalvan.scrapi.models

import com.github.mckalvan.scrapi.models.comments.{Comment, MoreComments, ParsedComment}
import com.github.mckalvan.scrapi.models.submission.{ParsedSubmission, Submission}
import com.github.mckalvan.scrapi.models.subreddit.{ParsedSubreddit, Subreddit, Subreddits}
import com.github.mckalvan.scrapi.models.user.{ParsedUser, User}
import com.github.mckalvan.scrapi.scrapi.Params
import com.github.mckalvan.scrapi.utils.Session

//case class Reddit() {}

/*
 * Main facade through which a client can interact w/ the rest of the SCRAPI library
 */

object Reddit {
    // TODO: Can session be made immutable?
    var session: Session = new Session()

    def tokenize(userName: String, password: String, clientId: String, clientSecret: String, userAgent: String): Unit = {
        session = Session.authorize(userName, password, clientId, clientSecret, userAgent)
    }

    def subreddit(name: String, params: Params*): ParsedSubreddit = Subreddit(name, params: _*)
    def subreddits: Subreddits.type = Subreddits
    def submission(name: String, params: Params*): ParsedSubmission = Submission(name, params: _*)
    def comment(name: String, params: Params*): ParsedComment = Comment(name, params: _*)
    def comments(submissionId: String, subreddit: String, sort: String, params: Params*): MoreComments = MoreComments(submissionId, subreddit, sort, params: _*)
    def user(name: String, params: Params*): ParsedUser = User(name)
}
