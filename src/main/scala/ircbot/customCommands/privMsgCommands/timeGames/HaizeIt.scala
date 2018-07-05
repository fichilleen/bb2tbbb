package ircbot.customCommands.privMsgCommands.timeGames

import slick.jdbc.SQLiteProfile.api._

class HaizeIt extends BaseTimeGame {
  override val tableQuery =
    TableQuery[TimeGameTable]((tag: Tag) => new TimeGameTable(tag, "haize"))

  override def precondition(user: String): Boolean = {
    System.currentTimeMillis() >= Timestamps.haizeit() &&
    System.currentTimeMillis() <= (Timestamps.haizeit() + 59000)
  }

  override def response(nick: String, res: TimeGameResponse): String = {
    res match {
      case UserScores(u, t) =>
        s"Congratulations on your haize $u, at ${t.timeString}! You're now ${countByNick(u)} times cooler than vanilla ice https://www.youtube.com/watch?v=rog8ou-ZepE"
      case AlreadySet(u, t) =>
        s"Haize today was $u, at ${t.timeString}! https://www.youtube.com/watch?v=rog8ou-ZepE Your attempt was at ${t.timeString}"
      case Unavailable() => "Only at 14:20"
      case _ => "Uh oh, something went fucky wucky"
    }
  }
}