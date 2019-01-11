package ircbot.customCommands.privMsgCommands.timeGames

import ircbot.{DbHandler, Luser}
import ircbot.models.MessageTime
import slick.jdbc.SQLiteProfile.api._

import scala.concurrent.{Await, Future}

class LastGame extends BaseTimeGame {
  override val tableQuery =
    TableQuery[TimeGameTable]((tag: Tag) => new TimeGameTable(tag, "last"))

  override def precondition(user: Luser): Boolean = true
  override def response(user: Luser, res: TimeGameResponse) = Seq("Unused")

  override def trigger(user: Luser, timestamp: MessageTime): Seq[String] = {
      getResult match {
        case Some(res: TimeGameResult) =>
          if ((res.nick == user.nick) || (res.hostMask == user.hostMask))
            Seq(s"${res.nick}: You already have last! @ ${res.timeStamp.timeString}")
          else {
            Future.successful(updateLock(res.timeStamp.epochMillis, timestamp.epochMillis, user))
            Seq(s"${user.nick}: you stole last! from ${res.nick} @ ${res.timeStamp.timeString}, now you're holding it at ${timestamp.timeString}")
          }

        case None =>
          Future.successful(setResult(user, timestamp.epochMillis))
          Seq(s"${user.nick}: You're holding last! @ ${timestamp.timeString}")
      }
  }

  private def updateLock(oldTimestamp: Long, newTimestamp: Long, user: Luser): Unit = {
    val q = tableQuery.filter(_.timestamp === oldTimestamp)
      .map(r => (r.name, r.timestamp, r.hostmask))
      .update((user.nick, newTimestamp, user.hostMask))
    Await.result(DbHandler.db.run(q), TIMEOUT)
  }
}
