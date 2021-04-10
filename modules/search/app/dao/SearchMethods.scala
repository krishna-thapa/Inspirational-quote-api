package dao

import com.krishna.util.Logging
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.Response
import com.sksamuel.elastic4s.requests.bulk.BulkResponse
import com.sksamuel.elastic4s.requests.indexes.admin.DeleteIndexResponse
import com.sksamuel.elastic4s.requests.searches.{ SearchRequest, SearchResponse }
import config.InitEs

import scala.concurrent.Future

trait SearchMethods extends InitEs with Logging {

  def getAndStoreQuotes(records: Int): Future[Response[BulkResponse]]

  def deleteQuotesIndex(indexName: String): Future[Response[DeleteIndexResponse]]

  def searchQuote(
      text: String,
      offset: Int,
      limit: Int
  ): Future[Response[SearchResponse]]

  /*
  Count the total docs inside the index, used for testing
   */
  def countDocsInIndex: Long = {
    client
      .execute {
        count(indexName)
      }
      .await
      .result
      .count
  }

  /*
  Check if the index is present in the ElasticSearch
  Returns a boolean
   */
  def doesIndexExists: Boolean = {
    log.info(s"Checking if the index: $indexName exists already")
    client
      .execute {
        indexExists(indexName)
      }
      .await
      .result
      .isExists
  }

  /*
  Use search API query to match phrase prefix
  https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-prefix-query.html
   */
  def searchRequest(text: String): SearchRequest = {
    search(indexName).query(matchPhrasePrefixQuery("quote", s"$text"))
  }
}
