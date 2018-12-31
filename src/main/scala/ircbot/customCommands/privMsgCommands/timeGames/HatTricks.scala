package ircbot.customCommands.privMsgCommands.timeGames

import slick.jdbc.SQLiteProfile.api._

class HatTricks(firstGame: FirstGame, leetGame: LeetGame, blazeIt: BlazeIt) extends BaseTimeGame {
  override val tableQuery =
    TableQuery[TimeGameTable]((tag: Tag) => new TimeGameTable(tag, "hattricks"))

  override def precondition(user: String): Boolean = {
    val results = for {
      f <- firstGame.getResult
      s <- leetGame.getResult
      b <- blazeIt.getResult
    } yield List(f.nick, s.nick, b.nick)

    results match {
      case Some(nicks) => nicks.forall(_ == user)
      case None => false
    }
  }

  override def response(nick: String, res: TimeGameResponse): Seq[String] = {
    res match {
      case UserScores(u, _) =>
        Seq(
          s"gooooooooooooaaaaaaaaaaaaaaallllllllllllllll! $u has ${countByNick(u, thisYear = false)} hat tricks!",
          """\o\"""
        )
      case _ => Seq.empty[String]
    }
  }
}
