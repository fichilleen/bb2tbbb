package ircbot.customCommands.privMsgCommands.timeGames


import slick.jdbc.SQLiteProfile.api._

class FirstGame extends BaseTimeGame {
  override val tableQuery =
    TableQuery[TimeGameTable]((tag: Tag) => new TimeGameTable(tag, "first"))

  override def precondition(user: String): Boolean = System.currentTimeMillis() >= Timestamps.first()

  override def response(nick: String, res: TimeGameResponse): String = {
    res match {
      case UserScores(u, t) =>
        s"Congratulations on your first $u, at ${t.timeString}! You now have ${countByNick(u)}"
      case AlreadySet(u, t) =>
        s"First today was $u, at ${t.timeString}! Your attempt was at $nowTimestring"
      case Unavailable() => "Not until 04:00"
      case _ => "Uh oh, something went fucky wucky"
    }
  }
}