package ircbot.customCommands.privMsgCommands.timeGames

import ircbot.Luser
import slick.jdbc.SQLiteProfile.api._

class BlazeIt extends BaseTimeGame {
  override val tableQuery =
    TableQuery[TimeGameTable]((tag: Tag) => new TimeGameTable(tag, "blaze"))

  override def precondition(user: Luser): Boolean = {
    System.currentTimeMillis() >= Timestamps.blazeit() &&
    System.currentTimeMillis() <= (Timestamps.blazeit() + 59999)
  }

  // These could be tied in with the precondition, although this is a bit easier to read than two negative ifs
  override def tooEarly: Boolean = System.currentTimeMillis() < Timestamps.blazeit()
  override def tooLate: Boolean = System.currentTimeMillis() >= (Timestamps.blazeit() + 60000)

  override def response(user: Luser, res: TimeGameResponse): Seq[String] = {
    Seq(
      res match {
        case UserScores(u, t, _) =>
          s"$u smooooookes weed e'ry day! ${t.timeString} You've been blazed ${countByNick(u)} times"
        case AlreadySet(u, t, _) =>
          s"$u was blazed today at ${t.timeString}! Your attempt was at $nowTimestring"
        case TooEarly() => "Only at 16:20"
        case TooLate() => "No one got blazed today ;("
        case x => s"Uh oh, something went fucky wucky - $x"
      }
    )
  }
}