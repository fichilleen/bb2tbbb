package ircbot.customCommands.privMsgCommands.timeGames

import akka.Done
import ircbot.models.{MessageTime, MessageTimeFactory}
import ircbot.{DbHandler, Luser}
import slick.jdbc.SQLiteProfile.api._
import slick.lifted.ProvenShape

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
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

  def countByNick(nick: String, thisYear: Boolean = true): Future[Int] = {
    val q =
        if (thisYear) tableQuery.filter(_.timestamp > Timestamps.currentYear())
        else tableQuery
    DbHandler.db.run(q.filter(_.name === nick).length.result)
  }

  def countEveryone(thisYear: Boolean = true): Query[(Rep[String], Rep[Int]), (String, Int), Seq] = {
    val q =
      if (thisYear) tableQuery.filter(_.timestamp > Timestamps.currentYear())
      else tableQuery

    q.groupBy(_.name).map{
      case (s, res) =>  s -> res.length
    }.sortBy(_._2.desc)
  }

  def trigger(user: Luser, timestamp: MessageTime): Future[Seq[String]] = {
    getResult.map{ r =>
      // TODO: This is pretty ugly - may be worth refactoring it
      response(user,
        r match {
          case Some(f: TimeGameResult) => f
          case None =>
            if (tooEarly) TooEarly()
            else if (tooLate) TooLate()
            else if (precondition(user)) {
              setResult(user, timestamp.epochMillis)
              UserScores(user.nick, timestamp, user.hostMask)
            }
            else Blocked()
        }
      )
    }
  }

  def getCount(thisYear: Boolean = true): Future[Seq[(String, Int)]] =
    DbHandler.db.run(countEveryone(thisYear).result)

  def getCountAsStringSeq(thisYear: Boolean = true): Future[Seq[String]] =
    getCount(thisYear).map{ r => r.map {case (u, c) => s"$u has $c" } }

  def getResult: Future[Option[TimeGameResult]] =
    DbHandler.db
      .run(queryFilter.result)
      .map(_.headOption.map { res =>
        AlreadySet(res._1, MessageTimeFactory(Some(res._2)), res._3)
      })

  protected def setResult(user: Luser, ts: Long): Future[Done] =
    DbHandler.db.run(tableQuery += (user.nick, ts, user.host)).map(_ => Done)
}

