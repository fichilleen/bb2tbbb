package ircbot

import ircbot.models.MessageTime
import slick.jdbc.SQLiteProfile.api._
import slick.lifted.ProvenShape

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

object DbHandler {
  val db = Database.forURL(
    s"jdbc:sqlite:${BotConfig.getString("botconfig.sqlite_file")}.db",
    driver = "org.sqlite.JDBC")
}
