package ircbot

import com.typesafe.config.Config
import ircbot.customCommands.privMsgCommands.timeGames.Timestamps
import ircbot.models.{MessageTime, MessageTimeFactory}
import slick.jdbc.SQLiteProfile.api._
import slick.lifted.ProvenShape

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

sealed trait TimeGame {
  def nick: String
  def timeStamp: MessageTime
}

case class FirstToday(nick: String, timeStamp: MessageTime) extends TimeGame

private class First(tag: Tag) extends Table[(String, Long)](tag, "first") {

  def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def timestamp: Rep[Long] = column[Long]("timestamp")
  def name: Rep[String] = column[String]("name")
  def * : ProvenShape[(String, Long)] = (name, timestamp)
}

class OldDbHandler(config: Config) {
  private val first = TableQuery[First]
  private val db = Database.forURL(
    s"jdbc:sqlite:${config.getString("botconfig.sqlite_file")}.db",
    driver = "org.sqlite.JDBC")

  def tryFirst(user: String, timestamp: MessageTime): FirstToday = {
    Await.result(getFirst, Duration.Inf) match {
      case Some(f: FirstToday) => f
      case None =>
        Future.successful(setFirst(user, timestamp.epochMillis))
        FirstToday(user, timestamp)
    }
  }

  private def setFirst(nick: String, ts: Long): Unit = {
    db.run(first += (nick, ts))
  }

  private def getFirst: Future[Option[FirstToday]] = {
    val first = TableQuery[First]
    val x = first.filter(_.timestamp > Timestamps.midnight())
    db.run(x.result)
      .map(_.headOption.map { res =>
        FirstToday(res._1, MessageTimeFactory(Some(res._2)))
      })
  }
}

object DbHandler {
  val db = Database.forURL(
    s"jdbc:sqlite:${BotConfig.getString("botconfig.sqlite_file")}.db",
    driver = "org.sqlite.JDBC")
}
