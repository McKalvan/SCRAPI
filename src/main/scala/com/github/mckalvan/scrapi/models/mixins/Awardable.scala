package com.github.mckalvan.scrapi.models.mixins

// TODO: Flesh out awardable trait
trait Awardable {}

// Encapsulates the "Awardings" object returned in certain responses
final case class Awardings(count: List[Int],
                     is_enabled: List[Boolean],
                     subreddit_id: List[String],
                     description: List[String],
                     end_date: List[Double],
                     award_sub_type:  List[String],
                     coin_reward: List[Int],
                     icon_url: List[String],
                     days_of_premium: List[Int],
                     is_new: List[Boolean],
                     id: List[String],
                     icon_height: List[Int],
                     resized_icons: ResizedIcons,
                     days_of_drip_extension: List[Int],
                     award_type: List[String],
                     start_date: List[Double],
                     coin_price: List[Int],
                     icon_width: List[Int],
                     subreddit_coin_reward: List[Int],
                     name: List[String])

// Encapsulates the "ResizedIcons" object returned in the "Awardings" object
case class ResizedIcons(url: List[String], width: List[Int], height: List[Int])
