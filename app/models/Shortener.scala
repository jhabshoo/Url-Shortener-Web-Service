package models

import java.sql.Timestamp
import org.squeryl.{Schema, KeyedEntity}
import java.security._
import scala.util.Random
import org.squeryl.Query
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.annotations.Column


/**
 * Basic Url information for any url in the database
 */
case class Url(
  id: Long = 0,
  fullUrl: String = "",
  shortUrl: String = {Url.convertToBase36}
) extends KeyedEntity[Long] 
{
}

/**
 * Companion object for Url case class
 */
object Url {

  /**
   * Returns a string in Base36 that is "randomly" created
   * Used to initialize the @shortUrl member of Url
   */
  def convertToBase36: String = {
    Integer.toString(new Random().nextInt(100000), 36)
  }

}

/**
 * A representation of the urlTable that contains all associated urls
 * Extends Schema; utilizes the Squeryl persistence to communicate
 * with a PostgreSQL db
 */
object AppDB extends Schema {

  /**
   * Services used by AppDB object
   */

  val urlTable = table[Url]("urlTable")

  /**
   *
   * @param fullUrl the "full" url being used to query the db
   * @return Query[Url] if a Url with fullUrl=@param:fullUrl exists
   *         in the db, otherwise @return None
   */
  def findByFullUrl(fullUrl: String): Query[Url] = {
    transaction {
      from(urlTable)((url) => where(url.fullUrl === fullUrl) select (url))
    }
  }

  /**
   *
   * @param shortUrl the Base36 "short" url being used to query the db
   * @return Query[Url] if a Url with shortUrl=@param:shortUrl exists
   *         in the db, otherwise @return None
   */
  def findByShortUrl(shortUrl: String): Query[Url] = {
    transaction {
      from(urlTable)((url) => where(url.shortUrl === shortUrl) select (url))
    }
  }

  /**
   *
   * @param fullUrl the "full" url being used to query the db
   * @return Url with Url.fullUrl = fullUrl if such a Url exists in the db
   *         Otherwise, @return Url()
   */
  def getByFullUrl(fullUrl: String) = {
    transaction {
      if (checkLong(fullUrl)) {
        from(urlTable)((url) => where (url.fullUrl === fullUrl) select (url)).head
      } else  {
        new Url()
      }
    }
  }

  /**
   *
   * @param shortUrl the Base36 "short" url being used to query the db
   * @return Url with Url.shortUrl = shortUrl if such a Url exists in the db
   *         Otherwise, @return Url()
   */
  def getByShortUrl(shortUrl: String) = {
    transaction {
      if (checkShort(shortUrl)) {
        from(urlTable)((url) => where (url.shortUrl === shortUrl) select (url)).head
      } else  {
          new Url()
      }
    }
  }

  /**
   *
   * @param url the Url object to be inserted into the db
   */
  def addUrl(url: Url)  {
    transaction {
      urlTable.insert(url)
    }
  }

  /**
   *
   * @param shortUrl the Base36 "short" url being used to query the db
   * @return true if such a Url exists in db, otherwise false
   */
  def checkShort(shortUrl: String): Boolean =  {
    inTransaction {
      val v = findByShortUrl(shortUrl).headOption
      if (v == None)  {
        return false
      }
      return true
    }
  }

  /**
   *
   * @param longUrl the "long" url being used to query the db
   * @return true if such a Url exists in db, otherwise false
   */
  def checkLong(longUrl: String): Boolean =  {
    inTransaction {
      val v = findByFullUrl(longUrl).headOption
      if (v == None)  {
        return false
      }
      return true
    }
  }
}

