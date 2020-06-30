package ircbot.Modules.Commands.TimeGames

import ircbot.Luser
import slick.jdbc.SQLiteProfile.api._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class FirstGame extends BaseTimeGame {
  override val tableQuery =
    TableQuery[TimeGameTable]((tag: Tag) => new TimeGameTable(tag, "first"))

  override def tooEarly: Boolean = System.currentTimeMillis() < Timestamps.first()
  override def precondition(user: Luser): Future[Boolean] = Future.successful(System.currentTimeMillis() >= Timestamps.first())

  override def response(user: Luser, res: TimeGameResponse): Future[Seq[String]] = {
    // TODO: This method is just because if we need to count the users score, that needs a future, and everything
    // needs to be a seq because of hat tricks
    def wrapStringResponse(timeGameResponse: TimeGameResponse): Future[Seq[String]] = {
      Future.successful(
        Seq(
          timeGameResponse match {
            case AlreadySet(u, t, _) => s"First today was $u, at ${t.timeString}! Your attempt was at $nowTimestring, you fucking clown"
            case TooEarly() => s"${user.nick} was firs... only joking, you're too early"
            case _ => "Uh oh, something went fucky wucky"
          }
        )
      )
    }

    res match {
      case UserScores(u, t, _) => countByNick(u).map { n =>
        Seq(s"Congratulations on your first $u, at ${t.timeString}! You now have $n")
      }
      case otherResult => wrapStringResponse(otherResult)
    }
  }
}
