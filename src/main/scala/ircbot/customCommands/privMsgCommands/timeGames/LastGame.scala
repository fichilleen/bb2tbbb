package ircbot.customCommands.privMsgCommands.timeGames

import akka.Done
import ircbot.models.MessageTime
import ircbot.{DbHandler, Luser}
import slick.jdbc.SQLiteProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LastGame extends BaseTimeGame {
  override val tableQuery =
    TableQuery[TimeGameTable]((tag: Tag) => new TimeGameTable(tag, "last"))

  override def precondition(user: Luser): Future[Boolean] = Future.successful(true)
  override def response(user: Luser, res: TimeGameResponse): Future[Seq[String]] = Future.successful(Seq("Unused"))

  override def trigger(user: Luser, timestamp: MessageTime): Future[Seq[String]] = {
      getResult.flatMap {
        case Some(existingResult: TimeGameResult) =>
          if ((existingResult.nick == user.nick) || (existingResult.hostMask == user.hostMask))
            Future.successful(Seq(s"${existingResult.nick}: You already have last! @ ${existingResult.timeStamp.timeString}"))
          else
            updateLock(existingResult.timeStamp.epochMillis, timestamp.epochMillis, user).map{ _ =>
              Seq(s"${user.nick}: you stole last! from ${existingResult.nick} @ ${existingResult.timeStamp.timeString}, now you're holding it at ${timestamp.timeString}")
            }

        case None =>
          setResult(user, timestamp.epochMillis).map{ _ =>
            Seq(s"${user.nick}: You're holding last! @ ${timestamp.timeString}")
          }
      }
  }

  private def updateLock(oldTimestamp: Long, newTimestamp: Long, user: Luser): Future[Done] = {
    val query = tableQuery.filter(_.timestamp === oldTimestamp)
      .map(r => (r.name, r.timestamp, r.hostmask))
      .update((user.nick, newTimestamp, user.hostMask))
    query.statements.foreach(println)
    DbHandler.db.run(
      query
    ).map(_ => Done)
  }

  def flush(): Future[Seq[String]] = {
    getResult.map {
      case Some(result: TimeGameResult) =>
        Seq(s"last today was ${result.nick} at ${result.timeStamp.timeString}!")
      case None => Seq.empty[String]
    }
  }
}
