package search.controller

import com.sksamuel.elastic4s.Response
import com.sksamuel.elastic4s.playjson.playJsonHitReader
import com.sksamuel.elastic4s.requests.indexes.IndexResponse
import com.sksamuel.elastic4s.requests.indexes.admin.DeleteIndexResponse
import javax.inject.{ Inject, Singleton }
import models.QuotesQuery
import play.api.mvc._
import response.ResponseResult
import search.dao.MethodsInEsDAO
import search.form.SearchForm
import search.util.FutureConv
import utils.Logging

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class SearchController @Inject()(methodsInEsDAO: MethodsInEsDAO, cc: ControllerComponents)(
    implicit executionContext: ExecutionContext
) extends AbstractController(cc)
    with FutureConv[Response[IndexResponse]]
    with ResponseResult
    with Logging {

  /**
    * Write records in index name under "quotes", need to pass number of records that will be generated randomly from posgres table
    * Can only be done by Admin role
    * @param records will be fetched from database and store under ES
    * @return success body or exception message
    */
  def writeInEs(records: Int): Action[AnyContent] = Action.async { implicit request =>
    log.info("Executing writeInEs Controller")

    val listOfFutureResults: Seq[Future[Response[IndexResponse]]] =
      methodsInEsDAO.getAndStoreQuotes(records)

    // Sequence all the futures into a single future of list
    val futureListResults: Future[Seq[Response[IndexResponse]]] =
      Future.sequence(listOfFutureResults)

    futureListResults
      .map(responseEsSeqResult)
      //add recover to handle the case where the future fails.
      .recover {
        case exception =>
          log.warn(s"Error while writing records on index: ${exception.getMessage}")
          badGateway(s"${exception.getMessage}")
      }
  }

  /**
    * Delete an entire index from ES
    * Can only be done by Admin role
    * @param indexName that will be deleted from ES
    * @return success body or exception message
    */
  def deleteIndex(indexName: String): Action[AnyContent] = Action.async { implicit request =>
    log.warn(s"Executing deleteIndex controller for: $indexName")
    log.warn("Hope you know what you are doing!")

    methodsInEsDAO
      .deleteQuotesIndex(indexName)
      .map(responseEsResult)
      .recover {
        case exception =>
          log.error(
            s"Error while deleting an index: $indexName error: ${exception.getMessage}"
          )
          badGateway(s"${exception.getMessage}")
      }
  }

  /**
    * List of the quotes that match the search text
    * @return Returns seq of matched quote
    */
  def searchQuote: Action[AnyContent] = Action.async { implicit request =>
    log.warn(s"Executing searchQuote controller")
    // Add request validation
    val searchResults = SearchForm.searchRequestForm.bindFromRequest.fold(
      formWithErrors => {
        Future(badRequest(s"The form was not in the expected format: $formWithErrors"))
      },
      searchRequest => {
        methodsInEsDAO
          .searchQuote(searchRequest.text, searchRequest.offset, searchRequest.limit)
          .map { response =>
            log.info(
              s"Total hits for the search: ${searchRequest.text} = ${response.result.totalHits}"
            )
            responseSeqResult(response.result.to[QuotesQuery].toList)
          }
          .recover {
            case exception =>
              log.error(
                s"Error while searching the text: ${exception.getMessage}"
              )
              badRequest(s"${exception.getMessage}")
          }
      }
    )
    searchResults
  }

}
