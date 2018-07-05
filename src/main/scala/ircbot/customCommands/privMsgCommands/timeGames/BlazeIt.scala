package ircbot.customCommands.privMsgCommands.timeGames

import slick.jdbc.SQLiteProfile.api._

class BlazeIt extends BaseTimeGame {
  override val tableQuery =
    TableQuery[TimeGameTable]((tag: Tag) => new TimeGameTable(tag, "blaze"))

  override def precondition(user: String): Boolean = {
    System.currentTimeMillis() >= Timestamps.blazeit() &&
    System.currentTimeMillis() <= (Timestamps.blazeit() + 59000)
  }

  override def response(nick: String, res: TimeGameResponse): String = {
    res match {
      case UserScores(u, t) =>
        s"$u smooooookes weed e'ry day! ${t.timeString} You've been blazed ${countByNick(u)} times"
      case AlreadySet(u, t) =>
        s"$u was blazed today at ${t.timeString}! Your attempt was at ${t.timeString}"
      case Unavailable() => "Only at 16:20"
      case _ => "Uh oh, something went fucky wucky"
    }
  }
}