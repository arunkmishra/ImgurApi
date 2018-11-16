package com.leadiq.service

import java.io.{BufferedOutputStream, File, FileOutputStream}

import scala.concurrent.ExecutionContext.Implicits.global
import com.google.inject.{Inject, Singleton}
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.{Future, Promise}

trait ImageDownloader {
  def download(url: String): Future[Either[String, File]]
}

@Singleton
class ImageUrlFileDownloader @Inject() (ws: WSClient) extends ImageDownloader {
  override def download(url: String): Future[Either[String, File]] = {
    val promise = Promise[Either[String, File]]
    val request = ws.url(url)
    val futureResponse: Future[WSResponse] = request.get()
    futureResponse.onComplete {
      case scala.util.Success(value) => {
        val tempFi = File.createTempFile("TES", "")
        tempFi.deleteOnExit
        val byteArray: Array[Byte] = value.body[Array[Byte]]
        val bos = new BufferedOutputStream(new FileOutputStream(tempFi))
        bos.write(byteArray)
        bos.close()
        promise.success(Right(tempFi))
      }
      case scala.util.Failure(exception) => promise.success(Left(exception.getMessage))
    }
    promise.future
  }
}
