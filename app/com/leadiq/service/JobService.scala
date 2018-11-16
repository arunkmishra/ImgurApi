package com.leadiq.service

import com.leadiq.models.{Image, Job, JobStatistics, JobStatus}
import com.leadiq.utils.Dates
import play.api.Logger

trait JobService {
    def create(urls: List[String]): Job
    def all:List[Job]
    def update(job: Job): Unit
    def update(job: Option[Job], downloadUrl: String, imgurUrl: String)
    def reportFailure(job: Option[Job],downloadUrl: String, error: String, logger: Logger)
    def find(id: String): Option[Job]
}

object InMemoryJobService extends JobService {

  private var store: Map[String, Job] = Map.empty[String, Job]

  private def calculateStatus(jobStat: JobStatistics): String =
    (jobStat.pending.size, jobStat.completed.size, jobStat.failed.size) match {
      case (pending, _, _) if pending > 0 => JobStatus.InProgress
      case (0, _, _) => JobStatus.Completed
    }

  def getStore: Map[String, Job] = store

  override def create(urls: List[String]): Job = {
    val id = Job.generateId()
    val creationDate = Dates.getDateWithTime
    val uploadStat = JobStatistics(
      pending = urls,
      completed = List.empty[String],
      failed = List.empty[String]
    )
    val finished = None
    val images = urls.map(downloadUrl => Image(None, downloadUrl))
    val job = Job(id,creationDate,finished,JobStatus.Pending,images, uploadStat)
    store = store + (job.jobId.toString -> job)
    job
  }
  override def all: List[Job] = store.values.toList

  def find(id: String): Option[Job] = store.get(id)

  override def update(job: Job): Unit = store = store + (job.jobId.toString -> job)

  override def update(jobOpt: Option[Job], downloadUrl: String, imgurUrl: String): Unit = {
    val job = jobOpt.get
    val image = Image(Some(imgurUrl), downloadUrl)
    val newImages = job.images.filter(_.downloadUrl != downloadUrl) :+ image

    val newPending = job.statistic.pending.filter(_ != downloadUrl )
    val finished = if(newPending.isEmpty) Some(Dates.getDateWithTime) else None
    val newCompleted = job.statistic.completed :+ imgurUrl

    val newStat = job.statistic.copy(pending = newPending, completed = newCompleted)
    val newStatus = calculateStatus(newStat)
    update(job.copy(finished = finished, images = newImages, statistic = newStat, status = newStatus))
  }

  override def reportFailure(jobOpt: Option[Job], downloadUrl: String, error: String, logger: Logger): Unit = {
    val job = jobOpt.get
    val newPending = job.statistic.pending.filter(_ != downloadUrl )
    val finished = if(newPending.isEmpty) Some(Dates.getDateWithTime) else None
    val newFailed = job.statistic.failed :+ downloadUrl
    val newStat = job.statistic.copy(pending = newPending, failed = newFailed)
    logger.error(error)
    val newStatus = calculateStatus(newStat)

    update(job.copy(finished= finished, status = newStatus, statistic = newStat))
  }
}