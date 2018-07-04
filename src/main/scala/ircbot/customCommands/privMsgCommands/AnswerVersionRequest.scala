package ircbot.customCommands.privMsgCommands

import akka.actor.{Actor, Props}
import ircbot._

object AnswerVersionRequest {def props() = Props(classOf[respondToHello])}

class AnswerVersionRequest extends Actor {
  override def receive: PartialFunction[Any, Unit] = {
    case ReceiveMessage(mm, _, _, nick, _, _, None, "VERSION") =>
      mm.socketActor ! PrivMsg(nick, s"bb2tbbb").message
  }
}
