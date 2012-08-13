import collection.mutable
import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import models._
import controllers._

/**
 * Tests for Application object (Controller)
 */

class ControllerSpec extends Specification  {

  "The index action" should {

    "return status 404" in {

      val Some(result) = routeAndCall(FakeRequest(GET,"/"))
      status(result) must beEqualTo(404)
    }

  }

  "The shortener action" should {

    "return status 401 if authorization fails" in {

      running(FakeApplication())  {
        val authHeader = FakeHeaders(Map(AUTHORIZATION -> List("Basic cm9CvdDBpwYAd29yZA==")))
        val body = Map("originalUrl"->Seq("http://www.yahoo.com/"))
        val Some(result) = routeAndCall(FakeRequest("POST","/shorten",authHeader,body))

        status(result) must beEqualTo(401)
      }

    }

    "return status 200 if request is serviced successfully" in  {

      running(FakeApplication())  {
        val authHeader = FakeHeaders(Map(AUTHORIZATION -> List("Basic cm9vdDpwYXNzd29yZA==")))
        val body = Map("originalUrl"->Seq("http://www.playframework.com"))
        val Some(result) = routeAndCall(FakeRequest("POST","/shorten",authHeader,body))
        val bodycontent = contentAsString(result)
        status(result) must beEqualTo(200)

        val body1 = Map("originalUrl"->Seq("http://www.playframework.com/"))
        val Some(result1) = routeAndCall(FakeRequest("POST","/shorten",authHeader,body1))
        val bodycontent1 = contentAsString(result1)
        status(result1) must beEqualTo(200)

        bodycontent must beEqualTo(bodycontent1)

        val body2 = Map("originalUrl"->Seq("https://www.playframework.com"))
        val Some(result2) = routeAndCall(FakeRequest("POST","/shorten",authHeader,body2))
        val bodycontent2 = contentAsString(result2)
        status(result2) must beEqualTo(200)

        bodycontent2 must be_!=(bodycontent)
      }

    }
  }

  "The reroute action" should {

    "return status 302 if short url is associated with a long url" in {

      running(FakeApplication())  {
        val validShortUrl = AppDB.getByFullUrl("http://www.playframework.com").shortUrl
        val Some(result) = routeAndCall(FakeRequest("GET","/"+validShortUrl))
        status(result) must beEqualTo(302)
      }

    }

    "return status 404 if short url is not associated with a long url" in {

      running(FakeApplication())  {
        val url = new Url(fullUrl = "http://www.nymets.com")
        val Some(result) = routeAndCall(FakeRequest("GET","/"+url.shortUrl))
        status(result) must beEqualTo(404)
      }

    }

  }

}