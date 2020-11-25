// Should be added controllers for the play routes
package controllers.quotes

import cache.{ CacheController, CacheService }
import com.krishna.model.Genre.Genre
import com.krishna.model.{ AllQuotesOfDay, QuotesQuery }
import com.krishna.response.ResponseMsg.{ EmptyDbMsg, InvalidCsvId }
import com.krishna.response.ResponseResult
import com.krishna.util.DateConversion.{ convertToDate, getCurrentDate }
import com.krishna.util.Logging
import daos.{ FavQuoteQueryDAO, QuoteQueryDAO }
import depInject.{ SecuredController, SecuredControllerComponents }
import javax.inject._
import model.UserDetail
import play.api.cache.redis.{ CacheApi, SynchronousResult }
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json.Json
import play.api.mvc._
import util.DecodeHeader

import scala.concurrent.ExecutionContext
import scala.util.matching.Regex

/**
  * This controller creates an 'Action' to handle HTTP requests to the
  * application's quotes from 'quotes' table.
  */
@Singleton
class QuoteController @Inject()(
    cache: CacheApi,
    cacheController: CacheController,
    cacheService: CacheService,
    scc: SecuredControllerComponents,
    quotesDAO: QuoteQueryDAO,
    favQuotesDAO: FavQuoteQueryDAO
)(implicit executionContext: ExecutionContext)
    extends SecuredController(scc)
    with ResponseResult
    with Logging {

  // CsvId should start with "CSV" prefix
  protected lazy val csvIdPattern: Regex = "CSV[0-9]+$".r

  /**
    * A REST endpoint that gets a random quote as a JSON from quotes table.
    * Should be unique to last 500 retrieved records from this end point
    * Used Redis cache database to store last 500 csv id to get unique record
    * Anyone can do perform this action
    */
  def getRandomQuote: Action[AnyContent] = Action { implicit request =>
    log.info("Executing getRandomQuote")
    responseEitherResult(cacheController.cacheRandomQuote())
  }

  /**
    * A REST endpoint that gets a quote of the day as JSON from quotes table
    *  @param date: Can take milliseconds date format as a path parameter to gets the
    *  previous 5 days quote of the day
    *  It stores the past 5 quote of the day in the Redis cache storage
    *  Anyone can do perform this action
    */
  def getQuoteOfTheDay(date: Option[String]): Action[AnyContent] = Action { implicit request =>
    log.info("Executing getQuoteOfTheDay")

    val contentDate: String =
      date.fold[String](getCurrentDate)((strDate: String) => convertToDate(strDate))
    log.info("Content Date from the API call: " + contentDate)

    // Get the quote from the content date key from global cache storage in Redis
    responseEitherResult(cacheService.cacheQuoteOfTheDay(contentDate))

  }

  /**
    * Get all the cached quotes from last 5 days
    * All of the quotes csv id are stored in the Redis storage
    * @return last 5 quote of the day
    */
  def getCachedQuotes: Action[AnyContent] = Action { implicit request =>
    log.info("Executing getLastFiveQuotes")

    // Get all the cached keys(max 5) and will be in date format
    val allCachedKeys: SynchronousResult[Seq[String]] = cache.matching("20*")
    if (allCachedKeys.nonEmpty) {
      val result: Seq[AllQuotesOfDay] = for {
        key <- allCachedKeys
        quote <- cache
          .get[String](key) // Get the stored value for that key from Redis cached storage
      } yield AllQuotesOfDay(key, quote)

      responseSeqResult(result)
    } else {
      notFound(EmptyDbMsg.msg)
    }
  }

  /**
    * A REST endpoint that gets all the quotes as JSON from quotes table
    * Only Admin can perform this action
    */
  def getAllQuotes: Action[AnyContent] = AdminAction { implicit request =>
    log.info("Executing getAllQuotes")
    responseSeqResult(quotesDAO.listAllQuotes())
  }

  /**
    * A REST endpoint that gets random 10 quotes as JSON from quotes table
    * Anyone can perform this action
    */
  def getFirst10Quotes: Action[AnyContent] = Action { implicit request =>
    log.info("Executing getFirst10Quotes")
    responseSeqResult(quotesDAO.listRandomQuote(10))
  }

  /**
    * A REST endpoint that creates or altered the fav tag in the fav_quotes table.
    * Only the logged user can perform this action and should be stored to user's id only
    */
  def favQuote(csvId: String): Action[AnyContent] = UserAction { implicit request =>
    val user: UserDetail = DecodeHeader(request.headers)
    log.info(s"Executing favQuote by user: ${user.email}")

    if (csvIdPattern.matches(csvId)) {
      responseTryResult(favQuotesDAO.modifyFavQuote(user.id, csvId))
    } else {
      badRequest(InvalidCsvId(csvId).msg)
    }
  }

  /**
    * A REST endpoint that gets all favorite quotes as JSON from fav_quotes table.
    * Only the logged user can perform this action and should retrieve own fav quotes only
    */
  def getFavQuotes: Action[AnyContent] = UserAction { implicit request =>
    val user: UserDetail = DecodeHeader(request.headers)
    log.info(s"Executing getFavQuotes by user: ${user.email}")
    responseSeqResult(favQuotesDAO.listAllQuotes(user.id))
  }

  /**
    * A REST endpoint that gets a random quote as per selected genre from the table from quotes table.
    * Returns 400 response code when the invalid genre is used
    * Anyone can do perform this action
    */
  def getGenreQuote(genre: Genre): Action[AnyContent] = Action { implicit request =>
    log.info("Executing getGenreQuote")
    responseOptionResult(quotesDAO.getGenreQuote(genre))
  }
}
