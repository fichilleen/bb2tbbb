package ircbot.customCommands.privMsgCommands

import akka.actor.{Actor, Props}
import ircbot._

object RespondToServerAuthRequest {def props() = Props(classOf[respondToHello])}

class RespondToServerAuthRequest extends Actor {
  override def receive: PartialFunction[Any, Unit] = {
    case ReceiveMessage(mm, _, luser, None, "VERSION") =>
      mm.socketActor ! PrivMsg(luser.nick, s"bb2tbbb").message
  }
}
