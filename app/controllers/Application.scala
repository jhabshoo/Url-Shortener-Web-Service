package controllers

import play.api.mvc._
import play.api.mvc.SimpleResult
import com.codahale.jerkson.Json
import org.squeryl.PrimitiveTypeMode._
import models.{AppDB, Url}
import play.api.data._
import play.api.data.Forms._
import java.security._
import play.api.libs.iteratee.Enumerator

/**
 *  Controller object
 */
object Application extends Controller {

  /**
   * The valid Basic HTTP Authorization credentials
   */
  val validUserName = "root"
  val validPassword = "password"

  /**
   *
   * @return Status 404 (Not Found)
   */
  def index = Action {
    Status(404)
  }

  /**
   * shortener method
   * called when POST request sent to /shorten. Cleans the Url, decides if a shortUrl is
   * already associated with requested long url or if a new Url object must be created
   * and added to the db
   * @return Status 200 (Ok) if the request was sucessfully serviced
   *         Status 401 (Unauthorized) if the Basic Auth credentials provided in request header are invalid.
   */
  def shortener = Secured(validUserName,validPassword)  {
    Action(parse.urlFormEncoded) { request =>
      val body = request.body
      val originalUrl = cleanUrl(body.get("originalUrl").get(0))
      if (AppDB.checkLong(originalUrl)) {
        val Url = AppDB.getByFullUrl(originalUrl)
        Ok(Url.shortUrl+"\n")
      } else {
        val Url = new Url(fullUrl = originalUrl)
        AppDB.addUrl(Url)
        Ok(Url.shortUrl+"\n")
      }
    }
  }

  /**
   *
   * @param shortUrl Base36 "short" url. Used to reroute to the long url to which it is assoicated
   * @return Status 302 (Found) if shortUrl is associated with an existing Url in the db
   *         Status 404 (Not Found) if shortUrl is not associated with any existing Urls
   */
  def rerouteTo(shortUrl: String) = Action {
    if (AppDB.checkShort(shortUrl) == false)  {
      Status(404)
    } else  {
      val url = AppDB.getByShortUrl(shortUrl)
      Found(url.fullUrl)
    }
  }

  def Secured[A](username: String, password: String)(action: Action[A]) = Action(action.parser) { request =>
    request.headers.get("Authorization").flatMap { authorization =>
      authorization.split(" ").drop(1).headOption.filter { encoded =>
        new String(org.apache.commons.codec.binary.Base64.decodeBase64(encoded.getBytes)).split(":").toList match {
          case u :: p :: Nil if u == username && password == p => true
          case _ => false
        }
      }.map(_ => action(request))
    }.getOrElse {
      Unauthorized.withHeaders("WWW-Authenticate" -> """Basic realm="Secured"""")
    }
  }

  /**
   *
   * @param url string of the form of a complete url to be "cleaned"
   * @return removes any trailing '/' and returns the same url as a string
   */

  def cleanUrl(url: String): String = {
    val trimmedFullUrlBuff = new StringBuffer(url.trim())
      if (trimmedFullUrlBuff.toString().endsWith("/"))  {
        trimmedFullUrlBuff.deleteCharAt(trimmedFullUrlBuff.length-1)
      }
    trimmedFullUrlBuff.toString
  }

}