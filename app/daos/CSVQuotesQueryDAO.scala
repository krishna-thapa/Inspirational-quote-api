package daos

import javax.inject.{ Inject, Singleton }
import models.CSVQuotesQuery
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.{ ExecutionContext, Future }

/**
 * A repository for Quotes stored in quotations table.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 * We want the JdbcProfile for this provider
 */
@Singleton
class CSVQuotesQueryDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)(
  implicit executionContext: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  /**
  * Here we define the table. It will have a quotations
  */
  private class CSVQuoteQueriesTable(tag: Tag) extends Table[CSVQuotesQuery](tag, "quotations") {
    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def quote: Rep[String] = column[String]("quote")
    def author: Rep[String] = column[String]("author")
    def genre: Rep[String] = column[String]("genre")

    /**
     * This is the tables default "projection".
     *
     * It defines how the columns are converted to and from the QuotesQuery object.
     *
     * In this case, we are simply passing the id, quote, author and genre parameters to the QuotesQuery case classes
     * apply and unapply methods.
     */
    def * : ProvenShape[CSVQuotesQuery] = (id, quote, author, genre) <> ((CSVQuotesQuery.apply _).tupled, CSVQuotesQuery.unapply)
  }

  /**
   * The starting point for all queries on the quotations table.
   */
  private val CSVQuoteQueries = TableQuery[CSVQuoteQueriesTable]

  def listAllQuotes(): Future[Seq[CSVQuotesQuery]] = db.run(CSVQuoteQueries.result)

  def getGenreQuote(genre: String): Future[Option[CSVQuotesQuery]] = {
    val randomFunction = SimpleFunction.nullary[Double]("random")
    db.run(CSVQuoteQueries.filter(_.genre === genre).sortBy(x => randomFunction).result.headOption)
  }
}
