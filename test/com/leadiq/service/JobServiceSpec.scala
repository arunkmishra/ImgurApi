package com.leadiq.service

import com.leadiq.models.{Image, Job, JobStatistics, JobStatus}
import com.leadiq.utils.Dates
import org.scalatest.{FreeSpec, Matchers}
import play.api.Logger

class JobServiceSpec extends FreeSpec with Matchers {

  "Job Service" - {

    val job = Job(
      Job.generateId(), Dates.getDateWithTime, None, JobStatus.InProgress, List(Image(None, "link1"), Image(None, "link2")), JobStatistics(
        pending = List("link"),
        completed = List.empty[String],
        failed = List.empty[String]
      )
    )
    "create" - {
      "return initialised Job" in {
        val urls = List("link1", "link2")
        val expectedJob: Job = Job(
          Job.generateId(), Dates.getDateWithTime, None, JobStatus.InProgress, List(Image(None, "link1"), Image(None, "link2")), JobStatistics(
            pending = urls,
            completed = List.empty[String],
            failed = List.empty[String]
          )
        )
        InMemoryJobService.create(urls).images shouldBe expectedJob.images
      }

      "after creation store map should be updated" in {
        InMemoryJobService.getStore.size shouldNot be (0)
      }
    }

    "update" - {

      "should update the store" in {
        val jobId = job.jobId
        InMemoryJobService.update(job)
        val store = InMemoryJobService.getStore

        store.get(jobId.toString) shouldBe Some(job)
      }

      "should update link if link is processed" in {
        val updatedLink = Image(Some("updateLink"), "link1")
        InMemoryJobService.update(Some(job), updatedLink.downloadUrl, updatedLink.uploadUrl.get)
        val afterUpdate = InMemoryJobService.getStore (job.jobId.toString).images.filter(i => i.downloadUrl == updatedLink.downloadUrl).head

        afterUpdate.uploadUrl shouldBe updatedLink.uploadUrl
      }

    }

    "reportFailure" - {

      "should update the store" in {
        val logger = Logger(this.getClass.getName)
        val updatedLink = Image(None, "link2")
        InMemoryJobService.reportFailure(Some(job), updatedLink.downloadUrl, "error", logger)
        val afterUpdate = InMemoryJobService.getStore (job.jobId.toString).statistic.failed.head

        afterUpdate shouldBe updatedLink.downloadUrl
      }
    }
    "find" - {

      "should return None for jobId not present" in {
        val jobId = "jobIdThatIsNotPresent"
        InMemoryJobService.find(jobId) shouldBe None
      }

      "should return job for jobId" in {
        val jobId = job.jobId.toString
        InMemoryJobService.update(job)
        InMemoryJobService.find(jobId) shouldBe Some(job)
      }
    }

    "all" - {
      "should return job.size greater than zero after getting updated by job" in {
        InMemoryJobService.update(job)

        InMemoryJobService.all.size shouldNot be (0)
      }
    }

  }

}
