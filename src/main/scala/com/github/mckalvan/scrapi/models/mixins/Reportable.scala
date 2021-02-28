package com.github.mckalvan.scrapi.models.mixins

import com.github.mckalvan.scrapi.utils.RedditConstants.ID

// Trait which allows users to report an object based on its fullname
trait Reportable extends Postable {
  val fullname: String
  def report(reason: String): EmptyResponse = {
    postForm[EmptyResponse]("report", Seq((ID -> fullname), ("reason" -> reason)), Map[String,String]())(extractEmpty)
  }
}
