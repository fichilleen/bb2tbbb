package ircbot.customCommands.privMsgCommands.timeGames

import ircbot.Luser
import ircbot.models.MessageTime
import slick.jdbc.SQLiteProfile.api._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class HaizeIt extends BaseTimeGame {
  override val tableQuery =
    TableQuery[TimeGameTable]((tag: Tag) => new TimeGameTable(tag, "haize"))

  override def precondition(user: Luser): Future[Boolean] = {
    Future.successful(
      System.currentTimeMillis() >= Timestamps.haizeit() &&
      System.currentTimeMillis() <= (Timestamps.haizeit() + 59999)
    )
  }

  override def tooEarly: Boolean = System.currentTimeMillis() < Timestamps.haizeit()
  override def tooLate: Boolean = System.currentTimeMillis() >= (Timestamps.haizeit() + 60000)

  private def scoreReponse(nick: String, t: MessageTime): Future[Seq[String]] =
    countByNick(nick).map{ c =>
      Seq(s"Congratulations on your haize $nick, at ${t.timeString}! You're now $c times cooler than vanilla ice https://www.youtube.com/watch?v=rog8ou-ZepE")
    }

  private def alreadySetResponse(nick: String, t: MessageTime): Future[Seq[String]] =
    Future.successful(Seq(
      s"haiZe today was $nick, at ${t.timeString}! https://www.youtube.com/watch?v=rog8ou-ZepE Your attempt was at $nowTimestring"
    ))

  override def response(user: Luser, res: TimeGameResponse): Future[Seq[String]] = {
    res match {
      case UserScores(u, t, _) => scoreReponse(u, t)
      case AlreadySet(u, t, _) => alreadySetResponse(u, t)
      case TooEarly() => Future.successful(Seq("Only at 14:20"))
      case TooLate() => Future.successful(Seq("Since no one got haize today, I guess that makes haiZe haiZe. How disappointing"))
    }
  }
}
