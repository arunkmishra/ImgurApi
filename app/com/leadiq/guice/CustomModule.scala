package com.leadiq.guice

import com.google.inject.AbstractModule
import com.leadiq.models.ApplicationConfiguration
import com.leadiq.service._
import play.api.{Configuration, Environment}

class CustomModule( environment: Environment, config: Configuration) extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[ApplicationConfiguration]).toInstance(ApplicationConfiguration.apply(config))
    bind(classOf[JobService]).toInstance(InMemoryJobService)
    bind(classOf[ImageDownloader]).to(classOf[ImageUrlFileDownloader])
    bind(classOf[ImageUploader]).to(classOf[ImgurImageUploader])
  }
}


