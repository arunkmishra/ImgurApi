package com.leadiq.models

import java.util.UUID

import play.api.libs.json.{Json, Writes}

case class JobStatus(value: String)

object JobStatus {
  val InProgress = "In-progress"
  val Completed = "Completed"
  val Pending = "Pending"

  implicit val jsonFormat = Json.format[JobStatus]
}

case class JobStatistics(pending: List[String], completed: List[String], failed: List[String])

object JobStatistics {
  implicit val jsonFormat = Json.format[JobStatistics]
  def empty = JobStatistics(List.empty[String], List.empty[String], List.empty[String])
}

case class Job(jobId: UUID, created: String, finished: Option[String], status: String, images: List[Image], statistic: JobStatistics)

object Job {
  //implicit val jsonFormatStat = Json.format[JobStatistics]
  //implicit val jsonFormat = Json.format[Job]

  implicit val userWrites = new Writes[Job] {
    def writes(job: Job) = Json.obj(
      "id" -> job.jobId,
      "created" -> job.created,
      "finished" -> job.finished,
      "status" -> job.status,
      "uploaded" -> job.statistic
    )
  }

  def generateId(): UUID = UUID.randomUUID()
}