package ircbot.customCommands.privMsgCommands.timeGames

import cats.data.OptionT
import cats.implicits._
import ircbot.Luser
import slick.jdbc.SQLiteProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class HatTricks(firstGame: FirstGame, leetGame: LeetGame, blazeIt: BlazeIt) extends BaseTimeGame {
  override val tableQuery =
    TableQuery[TimeGameTable]((tag: Tag) => new TimeGameTable(tag, "hattricks"))

  override def precondition(user: Luser): Future[Boolean] = {
    val results = for {
      f <- OptionT[Future, TimeGameResult](firstGame.getResult)
      s <- OptionT[Future, TimeGameResult](leetGame.getResult)
      b <- OptionT[Future, TimeGameResult](blazeIt.getResult)
    } yield List(f.nick, s.nick, b.nick)

    results.value.map {
      case Some(nicks) => nicks.forall(_ == user.nick)
      case None => false
    }
  }

  override def response(user: Luser, res: TimeGameResponse): Future[Seq[String]] = {
    res match {
      case UserScores(u, _, _) =>
        countByNick(u, thisYear = false).map { c =>
          Seq(
            s"gooooooooooooaaaaaaaaaaaaaaallllllllllllllll! $u has $c hat tricks!",
            """\o\"""
          )
        }
      case _ => Future.successful(Seq.empty[String])
    }
  }
}
