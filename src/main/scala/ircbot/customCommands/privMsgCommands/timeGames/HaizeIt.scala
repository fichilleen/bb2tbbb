package ircbot.customCommands.privMsgCommands.timeGames

import slick.jdbc.SQLiteProfile.api._

class HaizeIt extends BaseTimeGame {
  override val tableQuery =
    TableQuery[TimeGameTable]((tag: Tag) => new TimeGameTable(tag, "haize"))

  override def precondition(user: String): Boolean = {
    System.currentTimeMillis() >= Timestamps.haizeit() &&
    System.currentTimeMillis() <= (Timestamps.haizeit() + 59000)
  }

  override def tooEarly: Boolean = System.currentTimeMillis() < Timestamps.haizeit()
  override def tooLate: Boolean = System.currentTimeMillis() >= (Timestamps.haizeit() + 60000)

  override def response(nick: String, res: TimeGameResponse): String = {
    res match {
      case UserScores(u, t) =>
        s"Congratulations on your haize $u, at ${t.timeString}! You're now ${countByNick(u)} times cooler than vanilla ice https://www.youtube.com/watch?v=rog8ou-ZepE"
      case AlreadySet(u, t) =>
        s"haiZe today was $u, at ${t.timeString}! https://www.youtube.com/watch?v=rog8ou-ZepE Your attempt was at $nowTimestring"
      case TooEarly() => "Only at 14:20"
      case TooLate() => "Since no one got haize today, I guess that makes haiZe haiZe. How dissapointing"
      case _ => "Uh oh, something went fucky wucky"
    }
  }
}