/*
package ircbot.customCommands.privMsgCommands

import akka.actor.{Actor, Props}
import ircbot._
import ircbot.models.extractors.BaseRegexMatcherClass

object ExtractFirst extends BaseRegexMatcherClass {
  override val regExpression = """(first!.*)""".r
}

object FirstMessage {def props() = Props(classOf[FirstMessage])}

class FirstMessage extends Actor {
  override def receive: PartialFunction[Any, Unit] = {
    case ExtractFirst(mm, respondTo, nick, _) =>
      mm.db.tryFirst(nick, mm.timeStamp) match {
        case FirstToday(nick, timeStamp) =>
          mm.socketActor ! PrivMsg(respondTo, s"Congratulations on your first $nick, at ${timeStamp.timeString}!").message
        case FirstToday(otherNick, ts) =>
          mm.socketActor ! PrivMsg(respondTo, s"First today was $otherNick, at ${ts.timeString}!").message
      }
  }
}
*/
