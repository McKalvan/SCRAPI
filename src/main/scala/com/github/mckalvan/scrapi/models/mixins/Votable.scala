package com.github.mckalvan.scrapi.models.mixins

import com.github.mckalvan.scrapi.utils.RedditConstants.ID
// Mixin interface to allow for an object to be up/downvoted
trait Votable extends Postable{
    val fullname: String
    private val RANK_KV = ("rank" -> "1")
    def upvote: EmptyResponse = postForm[EmptyResponse]("vote", Seq("dir" -> "1", ID -> fullname, RANK_KV))(extractEmpty)
    def downvote: EmptyResponse = postForm[EmptyResponse]("vote",  Seq("dir" -> "-1", ID -> fullname, RANK_KV))(extractEmpty)
}

