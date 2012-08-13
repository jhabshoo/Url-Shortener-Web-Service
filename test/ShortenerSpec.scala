import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import models._
import controllers._

/**
 * Tests for Url Object
 */

class ShortenerSpec extends Specification {

  "The Url object" should {

    "create a shortened Url" in  {
      val url1 = "https://www.google.com/"
      val url2 = "https://www.playframework.com"

      val Url1 = Url(fullUrl = url1)
      val Url2 = Url(fullUrl = url2)

      Url1 should not be (None)
      Url1.fullUrl must be equalTo(url1)
      Url1.shortUrl must beMatching("""[a-zA-Z0-9]+""")
      Url1.shortUrl.length must be_<(Url1.fullUrl.length)


      Url2 should not be (None)
      Url2.fullUrl must be equalTo(url2)
      Url2.shortUrl must beMatching("""[a-zA-Z0-9]+""")
      Url2.shortUrl.length must be_<(Url2.fullUrl.length)
    }

  }

}