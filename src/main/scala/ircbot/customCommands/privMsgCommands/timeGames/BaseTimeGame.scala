package ircbot.customCommands.privMsgCommands.timeGames

import akka.Done
import ircbot.{DbHandler, Luser}
import ircbot.models.{MessageTime, MessageTimeFactory}
import slick.jdbc.SQLiteProfile.api._
import slick.lifted.ProvenShape

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
trait TimeGameResponse
trait Unavailable extends TimeGameResponse
trait TimeGameResult extends TimeGameResponse {
  def nick: String
  def timeStamp: MessageTime
  def hostMask: String
}

case class Blocked() extends Unavailable
case class TooEarly() extends Unavailable
case class TooLate() extends Unavailable
case class UserScores(nick: String, timeStamp: MessageTime, hostMask: String) extends TimeGameResult
case class AlreadySet(nick: String, timeStamp: MessageTime, hostMask: String) extends TimeGameResult

class TimeGameTable(tag: Tag, tableName: String)
    extends Table[(String, Long, String)](tag, tableName) {
  def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name: Rep[String] = column[String]("name")
  def timestamp: Rep[Long] = column[Long]("timestamp")
  def hostmask: Rep[String] = column[String]("hostmask")
  def * : ProvenShape[(String, Long, String)] = (name, timestamp, hostmask)
}

abstract class BaseTimeGame {
  protected val tableQuery: TableQuery[TimeGameTable]
  protected def response(user: Luser, res: TimeGameResponse): Seq[String]
  protected def precondition(user: Luser): Boolean
  protected def tooEarly: Boolean = false
  protected def tooLate: Boolean = false

  protected val TIMEOUT: FiniteDuration = 500.milliseconds

  protected def nowTimestring: String = MessageTimeFactory.apply().timeString

  protected def queryFilter: Query[TimeGameTable, (String, Long, String), Seq] = {
    tableQuery.filter(_.timestamp > Timestamps.midnight())
  }

  def countByNick(nick: String, thisYear: Boolean = true): Int = {
    val q =
        if (thisYear) tableQuery.filter(_.timestamp > Timestamps.currentYear())
        else tableQuery
    val e = q.filter(_.name === nick).length.result
    e.statements.foreach(println)
    Await.result(DbHandler.db.run(e), TIMEOUT)
  }

  def countEveryone(thisYear: Boolean = true): Query[(Rep[String], Rep[Int]), (String, Int), Seq] = {
    val q =
      if (thisYear) tableQuery.filter(_.timestamp > Timestamps.currentYear())
      else tableQuery
    q.groupBy(_.name).map{
      case (s, res) =>  s -> res.length
    }.sortBy(_._2.desc)
  }

  def trigger(user: Luser, timestamp: MessageTime): Seq[String] = {
    response(user,
      getResult match {
        case Some(f: TimeGameResult) => f
        case None =>
          if (tooEarly) TooEarly()
          else if (tooLate) TooLate()
          else if (precondition(user)){
            setResult(user, timestamp.epochMillis)
            UserScores(user.nick, timestamp, user.hostMask)
          }
          else Blocked()
      }
    )
  }

  def getCount(thisYear: Boolean = true): Seq[(String, Int)] = {
    val q = countEveryone(thisYear).result
    q.statements.foreach(println)
    Await.result(DbHandler.db.run(q), TIMEOUT)
  }

  def getCountAsStringSeq(thisYear: Boolean = true): Seq[String] = {
    getCount(thisYear).map {
      case (u, c) =>
        s"$u has $c"
    }
  }

  def getResult: Option[TimeGameResult] = {
    val res = DbHandler.db
      .run(queryFilter.result)
      .map(_.headOption.map { res =>
        AlreadySet(res._1, MessageTimeFactory(Some(res._2)), res._3)
      })
    Await.result(res, TIMEOUT)
  }

  protected def setResult(user: Luser, ts: Long): Future[Done] = {
    DbHandler.db.run(tableQuery += (user.nick, ts, user.host)).map(_ => Done)
  }
}

