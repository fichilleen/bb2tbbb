package ircbot.customCommands.privMsgCommands

import akka.actor.{Actor, Props}
import ircbot._
import ircbot.customCommands.privMsgCommands.timeGames._
import ircbot.models.extractors.GetChanNickTimeMessage

object TimeGameActor {
  def props() =
    Props(classOf[TimeGameActor])
}

class TimeGameActor extends Actor {

  val first = new FirstGame
  val second = new SecondGame(first)
  val haize = new HaizeIt
  val leet = new LeetGame
  val blaze = new BlazeIt
  val hatTrick = new HatTricks(first, leet, blaze)

  override def receive: PartialFunction[Any, Unit] = {

    case GetChanNickTimeMessage(socket, channel, nick, time, message) =>
      val response: Seq[String] = message match {
          // TODO: These could be case insensitive regex

        case "first!" =>
          first.trigger(nick, time)
        case "firsts!" =>
          first.getCountAsStringSeq

        case "second!" =>
          second.trigger(nick, time)
        case "seconds!" =>
          second.getCountAsStringSeq

        case "1337!" =>
          leet.trigger(nick, time)
        case "1337s!" =>
          leet.getCountAsStringSeq

        case "420haizeit!" =>
          haize.trigger(nick, time)
        case "haizes!" =>
          haize.getCountAsStringSeq

        case "420blazeit!" =>
          blaze.trigger(nick, time) ++ hatTrick.trigger(nick, time)
        case "420s!" =>
          blaze.getCountAsStringSeq
        case "hat tricks!" =>
          hatTrick.getCountAsStringSeq

        case _ =>
          println("no match")
          Seq.empty[String]
      }
      response.foreach(socket ! PrivMsg(channel, _).message)
  }
}
