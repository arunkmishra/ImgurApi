package com.leadiq.service.ProcessResponse

import play.api.libs.ws.WSResponse
object ExtractResponse {

  def extractSuccessResponse(response: WSResponse): String = (response.json \ "data" \ "link").as[String]

  def extractErrorMessage(response: WSResponse): String = (response.json \ "data" \ "error").as[String]

}
