package ircbot.customCommands.privMsgCommands.timeGames

import slick.jdbc.SQLiteProfile.api._

class LeetGame extends BaseTimeGame {
  override val tableQuery =
    TableQuery[TimeGameTable]((tag: Tag) => new TimeGameTable(tag, "leet"))

  override def precondition(user: String): Boolean = {
    System.currentTimeMillis() >= Timestamps.leet() &&
    System.currentTimeMillis() <= (Timestamps.leet() + 59000)
  }

  override def tooEarly: Boolean = System.currentTimeMillis() < Timestamps.leet()
  override def tooLate: Boolean = System.currentTimeMillis() >= (Timestamps.leet() + 60000)

  override def response(nick: String, res: TimeGameResponse): String = {
    res match {
      case UserScores(u, t) =>
        s"Congratulations on your leet 1337 $u, at ${t.timeString}! You now have ${countByNick(u)}"
      case AlreadySet(u, t) =>
        s"1337 today was $u, at ${t.timeString}! Your attempt was at $nowTimestring"
      case TooEarly() => "Only at 13:37"
      case TooLate() => "No one got 1337 today - lazy idling dicks"
      case _ => "Uh oh, something went fucky wucky"
    }
  }
}