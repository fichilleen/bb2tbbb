package ircbot.customCommands.privMsgCommands.timeGames

import ircbot.Luser
import slick.jdbc.SQLiteProfile.api._
import cats.data.OptionT
import cats.implicits._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class HatTricks(firstGame: FirstGame, leetGame: LeetGame, blazeIt: BlazeIt) extends BaseTimeGame {
  override val tableQuery =
    TableQuery[TimeGameTable]((tag: Tag) => new TimeGameTable(tag, "hattricks"))

  override def precondition(user: Luser): Boolean = {
    val results = for {
      f <- OptionT[Future, TimeGameResult](firstGame.getResult)
      s <- OptionT[Future, TimeGameResult](leetGame.getResult)
      b <- OptionT[Future, TimeGameResult](blazeIt.getResult)
    } yield List(f.nick, s.nick, b.nick)

    // It's not nice to await here, but otherwise it means refactoring the underlying precondition
    // and anything that implements it to be a future
    Await.result(
      results.value.map {
        case Some(nicks) => nicks.forall(_ == user.nick)
        case None => false
      },
      2.seconds
    )
  }

  override def response(user: Luser, res: TimeGameResponse): Seq[String] = {
    res match {
      case UserScores(u, _, _) =>
        Seq(
          s"gooooooooooooaaaaaaaaaaaaaaallllllllllllllll! $u has ${countByNick(u, thisYear = false)} hat tricks!",
          """\o\"""
        )
      case _ => Seq.empty[String]
    }
  }
}
