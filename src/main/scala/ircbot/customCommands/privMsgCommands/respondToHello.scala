package ircbot.customCommands.privMsgCommands

import akka.actor.{Actor, Props}
import ircbot.{PrivMsg, ReceiveChannelMessage, ReceiveDirectMessage}

object respondToHello {def props() = Props(classOf[respondToHello])}

class respondToHello extends Actor {
  override def receive: PartialFunction[Any, Unit] = {
    case ReceiveChannelMessage(_, _, nick, _, _, chan, "hello") =>
      sender() ! PrivMsg(chan, s"yo $nick")
    case ReceiveDirectMessage(_, hm, nick, _, _, "hello") =>
      sender() ! PrivMsg(nick, s"yo $nick. Your hostmask is $hm")
  }
}

