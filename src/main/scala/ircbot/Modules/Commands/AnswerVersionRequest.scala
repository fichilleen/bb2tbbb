package ircbot.Modules.Commands

import akka.actor.{Actor, Props}
import ircbot._

object AnswerVersionRequest {
  def props(): Props = Props(classOf[respondToHello])
}

class AnswerVersionRequest extends Actor {
  override def receive: PartialFunction[Any, Unit] = {
    case ReceivedMessage(mm, _, luser, "VERSION", None) =>
      mm.socketActor ! PrivMsg(luser.nick, s"bb2tbbb").message
  }
}
