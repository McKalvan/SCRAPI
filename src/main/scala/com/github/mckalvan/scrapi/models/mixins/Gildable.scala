package com.github.mckalvan.scrapi.models.mixins

import com.github.mckalvan.scrapi.utils.RedditConstants.USER

// Describes an object which allows for a user to "gild" an object using a post based on it's fullname
trait Gildable extends Postable {
  val fullname: String
  def gild: EmptyResponse = {
    postForm[EmptyResponse]("gild_thing", Seq(), endpointParams = Map(USER ->  fullname))(extractEmpty)
  }
}

// Encapsulates a "Gildings" object, where each argument specifies the number of a specific type of gilding
final case class Gildings(gid_1: Option[Int], gid_2: Option[Int], gid_3: Option[Int])
