package ircbot.Modules.Commands

import akka.actor.{Actor, Props}
import ircbot.{PrivMsg, ReceivedMessage}
import ircbot.Modules.Commands.TimeGames._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object TimeGameActor {
  def props(): Props = Props(new TimeGameActor())
}

case object FlushLasts

class TimeGameActor extends Actor {

  val first = new FirstGame
  val second = new SecondGame(first)
  val haize = new HaizeIt
  val leet = new LeetGame
  val blaze = new BlazeIt
  val hatTrick = new HatTricks(first, leet, blaze)
  val last = new LastGame

  override def receive: PartialFunction[Any, Unit] = {

    case r@ReceivedMessage(mm, _, luser, message, _) =>
      val time = mm.timeStamp
      val response: Future[Seq[String]] = message match {
          // TODO: These should be case insensitive regex

        case "first!" =>
          first.trigger(luser, time)
        case "firsts!" =>
          first.getCountAsStringSeq()

        case "second!" =>
          second.trigger(luser, time)
        case "seconds!" =>
          second.getCountAsStringSeq()

        case "1337!" =>
          leet.trigger(luser, time)
        case "1337s!" =>
          leet.getCountAsStringSeq()

        case "420haizeit!" =>
          haize.trigger(luser, time)
        case "haizes!" =>
          haize.getCountAsStringSeq()

        case "420blazeit!" =>
          Future.reduceLeft(
            Set(blaze.trigger(luser, time), hatTrick.trigger(luser, time))
          )( _ ++ _)
        case "420s!" =>
          blaze.getCountAsStringSeq()
        case "hat tricks!" =>
          hatTrick.getCountAsStringSeq(thisYear = false)

        case "last!" =>
          last.trigger(luser, time)
        case "lasts!" =>
          last.getCountAsStringSeq()
        case "lastlast!" =>
          last.flush()

        case _ => Future.successful(Seq.empty[String])
      }
      response.foreach(
        _.foreach(
          mm.socketActor ! PrivMsg(r.responseDestination, _).message
        )
      )
  }
}
