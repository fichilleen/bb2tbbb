package ircbot.customCommands.privMsgCommands

import akka.actor.{Actor, ActorRef, Props}
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension
import ircbot._
import ircbot.customCommands.privMsgCommands.timeGames._
import ircbot.models.GetChanNickTimeMessage

object TimeGameActor {
  def props() =
    Props(classOf[TimeGameActor])
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

  // TODO: This is a terrible hack for the scheduler, because it's not aware of
  // the context of sockets and channels
  var lastKnownSocket: Option[ActorRef] = Some(self)
  val defaultChannel = "#wasteland"

  val scheduler: QuartzSchedulerExtension = QuartzSchedulerExtension(context.system)

  scheduler.schedule("BeforeMidnight", self, FlushLasts)

  override def receive: PartialFunction[Any, Unit] = {

    case FlushLasts =>
      lastKnownSocket.foreach{ s =>
        last.flush().foreach(
          s ! PrivMsg(defaultChannel, _).message
        )
      }

    case GetChanNickTimeMessage(socket, channel, luser, time, message) =>
      val response: Seq[String] = message match {
          // TODO: These could be case insensitive regex

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
          blaze.trigger(luser, time) ++ hatTrick.trigger(luser, time)
        case "420s!" =>
          blaze.getCountAsStringSeq()
        case "hat tricks!" =>
          hatTrick.getCountAsStringSeq(thisYear = false)

        case "last!" =>
          last.trigger(luser, time)
        case "lasts!" =>
          last.getCountAsStringSeq()

        case _ =>
          println("no match")
          Seq.empty[String]
      }
      response.foreach(socket ! PrivMsg(channel, _).message)
  }
}
