package ircbot.customCommands.privMsgCommands

import akka.actor.{Actor, Props}
import ircbot._
import ircbot.models.extractors.GetChanNickMessage

object respondToHello {def props() = Props(classOf[respondToHello])}

class respondToHello extends Actor {
  override def receive: PartialFunction[Any, Unit] = {
    case ReceiveMessage(mm, _, hm, nick, _, _, None, "hello") =>
      mm.socketActor ! PrivMsg(nick, s"yo $nick. Your hostmask is $hm").message
    case GetChanNickMessage(socket, channel, nick, "hello") =>
      socket ! PrivMsg(channel, s"yo $nick").message
  }
}

