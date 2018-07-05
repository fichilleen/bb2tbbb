package ircbot.customCommands.privMsgCommands.timeGames

import slick.jdbc.SQLiteProfile.api._

class SecondGame(firstGame: FirstGame)
    extends BaseTimeGame {
  override val tableQuery =
    TableQuery[TimeGameTable]((tag: Tag) => new TimeGameTable(tag, "second"))

  override def precondition(user: String): Boolean = {
    firstGame.getResult match {
      case Some(res) =>
        if (res.nick == user) false
        else true
      case None => false
    }
  }

  override def response(nick: String, res: TimeGameResponse): String = {
    res match {
      case UserScores(u, t) =>
        s"Congratulations on your shitty first I guess $u, at ${t.timeString}. You now have ${countByNick(u)}"
      case AlreadySet(u, t) =>
        s"Second today was $u, at ${t.timeString}! Your attempt was at $nowTimestring"
      case Blocked() =>
        s"$nick: https://pauric.eu/captcha/notyet.gif"
      case _ => "Uh oh, something went fucky wucky"
    }
  }
}
