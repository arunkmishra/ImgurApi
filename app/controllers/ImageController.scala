package controllers

import com.leadiq.service._
import javax.inject.Inject
import play.api.Logger
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents, Request}

import scala.concurrent.ExecutionContext.Implicits.global

class ImageController @Inject() (cc: ControllerComponents,
                                jobService: JobService,
                                imgurUploader: ImageUploader,
                                imgDownloader: ImageDownloader
                              ) extends AbstractController(cc) {

  private  val logger = Logger(this.getClass.getName)
  def uploadImages = Action(parse.json) { request: Request[JsValue]  =>
    val input = request.body
    val urls: List[String] = (input \ "urls").as[List[String]].distinct
    val job = jobService.create(urls)
    val jobId = job.jobId

    urls.foreach { url =>
      imgDownloader.download(url)
          .foreach( downloadResult =>
            downloadResult fold(
              error => jobService.reportFailure(jobService.find(jobId.toString), url, error, logger),
              file => imgurUploader.upload(file).foreach(
                _.
                fold(
                  error => jobService.reportFailure(jobService.find(jobId.toString), url, error, logger),
                  imgurlink => jobService.update(jobService.find(jobId.toString), url, imgurlink)
                )
              )
          )
        )
    }
    Ok(Json.obj("jobId" -> job.jobId.toString))
  }


  def stats(jobId: String) = Action {
    jobService.
      find(jobId).
      fold(Ok(Json.obj(jobId -> "Not Found"))) { job =>
        Ok(Json.toJson(job))
      }
  }

  def getAllLinks = Action {
    val imageImgurUrls = jobService.all.flatMap(
        job => job.images.filter(image => image.uploadUrl.isDefined)
      ).map(_.uploadUrl.get)

    Ok(Json.toJson(Json.obj("uploaded" -> imageImgurUrls)))
  }
}
// reasons:
/*
1. Used .get here because it is safe because from where it is being called this will always return a value
2. Yes we  could have used future over the trait. But for time being I have made that function blocking as it is alrady covered in a futrue of Action.
3.Thats the way I have used to download file locally and then upload to Imgur api, instead of sending direclty url to imgur.
  code flow to download file ->
   {
      get image from url which returns future
      and if above futrure is scuccessfull
        create a temp file
        we copy the byte code got from future to temp file.
        we use that file to upload to imgur
        we delete this file on exit
      in case of future failure
        throw the exception
   }


 */