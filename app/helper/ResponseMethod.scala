package helper

import controllers.Assets.Ok
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{ Json, OFormat, Writes }
import play.api.mvc.Result
import play.api.mvc.Results._
import utils.Logging

import scala.util.{ Failure, Success, Try }

trait ResponseMethod extends Logging {

  def badRequest(msg: String): Result = {
    log.warn(s"Bad request error: $msg")
    BadRequest(Json.toJson(Response(msg)))
  }

  def notFound(msg: String): Result = {
    log.warn(s"Not Found error: $msg")
    NotFound(Json.toJson(Response(msg)))
  }

  def internalServerError(msg: String): Result = {
    log.warn(s"Internal server error: $msg")
    InternalServerError(Json.toJson(Response(msg)))
  }

  case class Response(userMsg: String)

  object Response {
    implicit val responseError: Writes[Response] =
      Json.writes[Response]
  }

  def responseSeqResult[T](records: Seq[T])(implicit conv: OFormat[T]): Result = {
    if (records.nonEmpty) Ok(Json.toJson(records))
    else notFound("Database is empty!")
  }

  def responseOptionResult[T](record: Option[T])(implicit conv: OFormat[T]): Result = {
    record match {
      case Some(quote) => Ok(Json.toJson(quote))
      case None =>
        notFound("Database is empty!")
    }
  }

  def responseTryResult[T](record: Try[T])(implicit conv: OFormat[T]): Result = {
    record match {
      case Success(quote)     => Ok(Json.toJson(quote))
      case Failure(exception) => internalServerError(exception.getMessage)
    }
  }

}