package ircbot.customCommands.privMsgCommands.timeGames

import ircbot.DbHandler
import ircbot.models.{MessageTime, MessageTimeFactory}
import slick.jdbc.SQLiteProfile.api._
import slick.lifted.ProvenShape

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

trait TimeGameResponse
trait TimeGameResult extends TimeGameResponse {
  def nick: String
  def timeStamp: MessageTime
}

case class Unavailable() extends TimeGameResponse
case class UserScores(nick: String, timeStamp: MessageTime) extends TimeGameResult
case class AlreadySet(nick: String, timeStamp: MessageTime) extends TimeGameResult

class TimeGameTable(tag: Tag, tableName: String)
    extends Table[(String, Long)](tag, tableName) {
  def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def timestamp: Rep[Long] = column[Long]("timestamp")
  def name: Rep[String] = column[String]("name")
  def * : ProvenShape[(String, Long)] = (name, timestamp)
}

abstract class BaseTimeGame {
  protected val tableQuery: TableQuery[TimeGameTable]
  protected def response(nick: String, res: TimeGameResponse): String
  protected def precondition(user: String): Boolean

  // This is super high, but sqlite can be slow for the first query
  // TODO: It should probably be a global variable
  private val TIMEOUT = 500.milliseconds

  protected def queryFilter: Query[TimeGameTable, (String, Long), Seq] = {
    tableQuery.filter(_.timestamp > Timestamps.midnight())
  }

  def countByNick(nick: String): Int = {
    val q = tableQuery.filter(_.name === nick).length.result
    q.statements.foreach(println)
    Await.result(DbHandler.db.run(q), TIMEOUT)
  }

  def countEveryone(): Query[(Rep[String], Rep[Int]), (String, Int), Seq] = {
    tableQuery.groupBy(_.name).map{
      case (s, res) =>  s -> res.length
    }.sortBy(_._2.desc)
  }

  def trigger(user: String, timestamp: MessageTime): String = {
    response(user,
      getResult match {
        case Some(f: TimeGameResult) => f
        case None =>
          if (precondition(user)) {
            Future.successful(setResult(user, timestamp.epochMillis))
            UserScores(user, timestamp)
          }
          else Unavailable()
      }
    )
  }

  def getCount: Seq[(String, Int)] = {
    val q = countEveryone().result
    q.statements.foreach(println)
    Await.result(DbHandler.db.run(q), TIMEOUT)
  }

  def getCountAsStringSeq: Seq[String] = {
    getCount.map {
      case (u, c) =>
        s"$u has $c"
    }
  }

  def getResult: Option[TimeGameResult] = {
    val res = DbHandler.db
      .run(queryFilter.result)
      .map(_.headOption.map { res =>
        AlreadySet(res._1, MessageTimeFactory(Some(res._2)))
      })
    Await.result(res, TIMEOUT)
  }

  protected def setResult(nick: String, ts: Long): Unit = {
    Await.result(DbHandler.db.run(tableQuery += (nick, ts)), TIMEOUT)
  }
}

