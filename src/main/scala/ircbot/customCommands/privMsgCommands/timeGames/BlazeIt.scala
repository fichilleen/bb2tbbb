package ircbot.customCommands.privMsgCommands.timeGames

import ircbot.Luser
import ircbot.models.MessageTime
import slick.jdbc.SQLiteProfile.api._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class BlazeIt extends BaseTimeGame {
  override val tableQuery =
    TableQuery[TimeGameTable]((tag: Tag) => new TimeGameTable(tag, "blaze"))

  override def precondition(user: Luser): Future[Boolean] = {
    Future.successful(
      System.currentTimeMillis() >= Timestamps.blazeit() &&
      System.currentTimeMillis() <= (Timestamps.blazeit() + 59999)
    )
  }

  // These could be tied in with the precondition, although this is a bit easier to read than two negative ifs
  override def tooEarly: Boolean = System.currentTimeMillis() < Timestamps.blazeit()
  override def tooLate: Boolean = System.currentTimeMillis() >= (Timestamps.blazeit() + 60000)

  private def scoreReponse(nick: String, t: MessageTime): Future[Seq[String]] =
    countByNick(nick).map{ c =>
      Seq(s"$nick smooooookes weed e'ry day! ${t.timeString} You've been blazed $c times")
    }

  private def alreadySetResponse(nick: String, t: MessageTime): Future[Seq[String]] =
    Future.successful(Seq(
      s"$nick was blazed today at ${t.timeString}! Your attempt was at $nowTimestring"
    ))

  override def response(user: Luser, res: TimeGameResponse): Future[Seq[String]] = {
    res match {
      case UserScores(u, t, _) => scoreReponse(u, t)
      case AlreadySet(u, t, _) => alreadySetResponse(u, t)
      case TooEarly() => Future.successful(Seq("Only at 16:20"))
      case TooLate() => Future.successful(Seq("No one got blazed today ;("))
    }
  }
}