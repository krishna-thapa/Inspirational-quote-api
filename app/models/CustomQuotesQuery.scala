package models

import java.sql.Date
import com.krishna.model.Genre.Genre
import com.krishna.model.base.{ QuoteResource, QuotesTable }
import play.api.libs.json._

// https://nrinaudo.github.io/scala-best-practices/tricky_behaviours/final_case_classes.html
final case class CustomQuotesQuery(
    id: Int,
    csvId: String,
    quote: String,
    author: String,
    genre: Option[Genre] = None,
    storedDate: Date,
    ownQuote: Boolean
) extends QuotesTable
    with QuoteResource

object CustomQuotesQuery {
  implicit lazy val customerQuotesFormat: OFormat[CustomQuotesQuery] =
    Json.format[CustomQuotesQuery]
}
