package com.leadiq.models

import play.api.libs.json.Json

case class Image(uploadUrl: Option[String], downloadUrl: String)

object Image {
  implicit val jsonFormat = Json.format[Image]
}