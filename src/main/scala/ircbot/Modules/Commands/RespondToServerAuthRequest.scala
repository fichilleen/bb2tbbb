package ircbot.Modules.Commands

import akka.actor.{Actor, Props}
import ircbot._

object RespondToServerAuthRequest {
  def props(): Props = Props(classOf[respondToHello])
}

class RespondToServerAuthRequest extends Actor {
  override def receive: PartialFunction[Any, Unit] = {
    case ReceivedMessage(mm, _, luser, "VERSION", None) =>
      mm.socketActor ! PrivMsg(luser.nick, s"bb2tbbb").message
  }
}
