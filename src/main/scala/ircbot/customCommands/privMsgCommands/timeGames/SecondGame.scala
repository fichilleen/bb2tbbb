package ircbot.customCommands.privMsgCommands.timeGames

import ircbot.Luser
import slick.jdbc.SQLiteProfile.api._

class SecondGame(firstGame: FirstGame)
    extends BaseTimeGame {
  override val tableQuery =
    TableQuery[TimeGameTable]((tag: Tag) => new TimeGameTable(tag, "second"))

  override def precondition(user: Luser): Boolean = {
    firstGame.getResult match {
      case Some(res) =>
        if ((res.nick == user.nick) || (res.hostMask == user.hostMask)) false
        else true
      case None => true
    }
  }

  override def response(user: Luser, res: TimeGameResponse): Seq[String] = {
    Seq(
      res match {
        case UserScores(u, t, _) =>
          s"Congratulations on your shitty first I guess $u, at ${t.timeString}. You now have ${countByNick(u)}"
        case AlreadySet(u, t, _) =>
          s"Second today was $u, at ${t.timeString}! Your attempt was at $nowTimestring"
        case Blocked() =>
          s"${user.nick}: https://pauric.eu/captcha/notyet.gif"
        case _ => "Uh oh, something went fucky wucky"
      }
    )
  }
}
