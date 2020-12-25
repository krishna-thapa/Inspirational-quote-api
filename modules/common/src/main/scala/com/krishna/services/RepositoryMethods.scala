package com.krishna.services

import com.krishna.model.base.IdResource
import com.krishna.table.TableId
import slick.dbio.{ DBIOAction, Effect, NoStream, Streaming }
import slick.lifted.TableQuery
import slick.jdbc.PostgresProfile.api._

trait RepositoryMethods[T <: IdResource, QuoteTable <: Table[T] with TableId[T]] {

  //type T  //https://stackoverflow.com/questions/1154571/scala-abstract-types-vs-generics

  def tables: TableQuery[QuoteTable]

  def getAllQuotes: DBIOAction[Seq[T], Streaming[T], Effect.Read] = {
    tables.sortBy(_.id).result
  }

  def getSelectedQuote(id: Int): DBIOAction[Option[T], NoStream, Effect.Read] = {
    tables.filter(_.id === id).result.headOption
  }

  def deleteCustomQuote(id: Int): DBIOAction[Int, NoStream, Effect.Write] = {
    tables.filter(_.id === id).delete
  }

}
