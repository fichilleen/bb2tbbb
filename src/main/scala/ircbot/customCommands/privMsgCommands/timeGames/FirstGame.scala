package ircbot.customCommands.privMsgCommands.timeGames


import ircbot.Luser
import slick.jdbc.SQLiteProfile.api._

import scala.concurrent.Future

class FirstGame extends BaseTimeGame {
  override val tableQuery =
    TableQuery[TimeGameTable]((tag: Tag) => new TimeGameTable(tag, "first"))

  override def tooEarly: Boolean = System.currentTimeMillis() < Timestamps.first()
  override def precondition(user: Luser): Future[Boolean] = Future.successful(System.currentTimeMillis() >= Timestamps.first())

  override def response(user: Luser, res: TimeGameResponse): Seq[String] = {
    Seq(
      res match {
        case UserScores(u, t, _) =>
          s"Congratulations on your first $u, at ${t.timeString}! You now have ${countByNick(u)}"
        case AlreadySet(u, t, _) =>
          s"First today was $u, at ${t.timeString}! Your attempt was at $nowTimestring, you fucking clown"
        case TooEarly() => s"${user.nick} was firs... only joking, you're too early"
        case _ => "Uh oh, something went fucky wucky"
      }
    )
  }
}