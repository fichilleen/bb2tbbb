package ircbot.customCommands.privMsgCommands.timeGames

import ircbot.Luser
import slick.jdbc.SQLiteProfile.api._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class SecondGame(firstGame: FirstGame) extends BaseTimeGame {
  override val tableQuery =
    TableQuery[TimeGameTable]((tag: Tag) => new TimeGameTable(tag, "second"))

  override def precondition(user: Luser): Future[Boolean] = {
    firstGame.getResult.map {
      case Some(res) =>
        if ((res.nick == user.nick) || (res.hostMask == user.hostMask)) false
        else true
      case None => false
    }
  }

  override def response(user: Luser, res: TimeGameResponse): Future[Seq[String]] = {
    def wrapStringResponse(timeGameResponse: TimeGameResponse): Future[Seq[String]] = {
      Future.successful(
        Seq(
          timeGameResponse match {
            case AlreadySet(u, t, _) => s"Second today was $u, at ${t.timeString}! Your attempt was at $nowTimestring"
            case Blocked() => s"${user.nick}: https://pauric.eu/captcha/notyet.gif"
            case _ => "Uh oh, something went fucky wucky"
          }
        )
      )
    }

    res match {
      case UserScores(u, t, _) => countByNick(u).map{ n =>
        Seq(s"Congratulations on your shitty first I guess $u, at ${t.timeString}. You now have $n")
      }
      case otherResponse => wrapStringResponse(otherResponse)
    }
  }
}
