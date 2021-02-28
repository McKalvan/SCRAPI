package com.github.mckalvan.scrapi.models.mixins

// Trait which allows users to reply to an object based on its fullname.
trait Repliable extends Postable {
  val fullname: String
  def reply(body: String): EmptyResponse = {
    postForm[EmptyResponse]("comment", Seq(("thing_id" -> fullname), ("text", body)) , Map[String,String]())(extractEmpty)
  }
}
