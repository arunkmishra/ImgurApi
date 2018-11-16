package com.leadiq.service

import java.io.File

import com.google.inject.{Inject, Singleton}
import com.leadiq.models.ApplicationConfiguration
import com.leadiq.service.ProcessResponse.ExtractResponse._
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}
trait ImageUploader {
    def upload(image: File): Future[Either[String,String]]
}

@Singleton
class ImgurImageUploader @Inject() (conf: ApplicationConfiguration, ws: WSClient) extends ImageUploader {

  override def upload(image: File): Future[Either[String, String]] = {
    val request = ws.url(conf.imgurUrl)
    .addHttpHeaders("Authorization" -> s"Client-ID ${conf.imgurClientId}" )
    .withRequestTimeout(conf.imgurApiTimeout seconds)
    processResponse(request.post(image))
  }

  private def processResponse(futureResponse: Future[WSResponse]): Future[Either[String, String]] = {
    val promise = Promise[Either[String, String]]
    futureResponse.onComplete {
      case Success(response) => promise.success(extractData(response))
      case Failure(exception) => promise.success(Left(exception.getMessage))
    }
    promise.future
  }

  def extractData(response: WSResponse): Either[String, String] =
    if(response.status == 200)
      Right(extractSuccessResponse(response))
    else
      Left(extractErrorMessage(response))




}


