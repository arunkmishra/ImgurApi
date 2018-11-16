package com.leadiq.models

import play.api.Configuration

case class ApplicationConfiguration(imgurUrl: String, imgurClientId: String, imgurApiTimeout: Long)

object ApplicationConfiguration {

  def apply(config: Configuration): ApplicationConfiguration = ApplicationConfiguration(
    config.get[String]("imgur.url"),
    config.get[String]("imgur.clientId"),
    config.get[Long]("imgur.timeout")
  )

}