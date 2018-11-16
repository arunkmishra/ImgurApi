package controllers

import play.api.test.Helpers.{GET, status}
import play.api.test.Helpers._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.libs.json.Json
import play.api.test.{FakeRequest, Injecting}

class ImageControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting with MockitoSugar {


    "GET call" should {
      "return the json from a new instance for job id search api" in {
        val controller = inject[ImageController]
        val home = controller.stats("sss").apply(FakeRequest(GET, "/v1/images/upload/sss"))

        status(home) mustBe OK
        contentType(home) mustBe Some("application/json")
        contentAsString(home) must include ("ss")
      }

      "return json response from new instance for all imgur links" in {
        val controller = inject[ImageController]
        val home = controller.getAllLinks.apply(FakeRequest(GET, "/v1/images"))

        status(home) mustBe OK
        contentType(home) mustBe Some("application/json")
        contentAsString(home) must include ("[]")
      }
    }

}
