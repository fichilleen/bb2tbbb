package ircbot.customCommands.privMsgCommands.timeGames

import ircbot.Luser
import slick.jdbc.SQLiteProfile.api._

class HaizeIt extends BaseTimeGame {
  override val tableQuery =
    TableQuery[TimeGameTable]((tag: Tag) => new TimeGameTable(tag, "haize"))

  override def precondition(user: Luser): Boolean = {
    System.currentTimeMillis() >= Timestamps.haizeit() &&
    System.currentTimeMillis() <= (Timestamps.haizeit() + 59999)
  }

  override def tooEarly: Boolean = System.currentTimeMillis() < Timestamps.haizeit()
  override def tooLate: Boolean = System.currentTimeMillis() >= (Timestamps.haizeit() + 60000)

  override def response(user: Luser, res: TimeGameResponse): Seq[String] = {
    Seq(
      res match {
        case UserScores(u, t, _) =>
          s"Congratulations on your haize $u, at ${t.timeString}! You're now ${countByNick(u)} times cooler than vanilla ice https://www.youtube.com/watch?v=rog8ou-ZepE"
        case AlreadySet(u, t, _) =>
          s"haiZe today was $u, at ${t.timeString}! https://www.youtube.com/watch?v=rog8ou-ZepE Your attempt was at $nowTimestring"
        case TooEarly() => "Only at 14:20"
        case TooLate() => "Since no one got haize today, I guess that makes haiZe haiZe. How disappointing"
        case _ => "Uh oh, something went fucky wucky"
      }
    )
  }
}
