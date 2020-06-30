package ircbot.Modules.Commands.TimeGames

import ircbot.Luser
import ircbot.models.MessageTime
import slick.jdbc.SQLiteProfile.api._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class LeetGame extends BaseTimeGame {
  override val tableQuery =
    TableQuery[TimeGameTable]((tag: Tag) => new TimeGameTable(tag, "leet"))

  override def precondition(user: Luser): Future[Boolean] = {
    Future.successful(
      System.currentTimeMillis() >= Timestamps.leet() &&
      System.currentTimeMillis() <= (Timestamps.leet() + 59999)
    )
  }

  override def tooEarly: Boolean = System.currentTimeMillis() < Timestamps.leet()
  override def tooLate: Boolean = System.currentTimeMillis() >= (Timestamps.leet() + 60000)

  private def scoreReponse(nick: String, t: MessageTime): Future[Seq[String]] =
    countByNick(nick).map{ c =>
      Seq(s"Congratulations on your leet 1337 $nick, at ${t.timeString}! You now have $c")
    }

  private def alreadySetResponse(nick: String, t: MessageTime): Future[Seq[String]] =
    Future.successful(Seq(
      s"1337 today was $nick, at ${t.timeString}! Your attempt was at $nowTimestring"
    ))

  override def response(user: Luser, res: TimeGameResponse): Future[Seq[String]] = {
   res match {
      case UserScores(u, t, _) => scoreReponse(u, t)
      case AlreadySet(u, t, _) => alreadySetResponse(u, t)
      case TooEarly() => Future.successful(Seq("Only at 13:37"))
      case TooLate() => Future.successful(Seq("No one got 1337 today - lazy idling dicks"))
    }
  }
}