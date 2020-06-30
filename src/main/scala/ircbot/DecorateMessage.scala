package ircbot

import akka.actor.ActorRef
import akka.util.ByteString
import ircbot.models.{MessageTimeFactory, MetaMessage}

object DecorateMessage {
  def apply(socketActor: ActorRef, serverResponse: ByteString): Array[IrcMessage] = {
    // IRC always uses CR-LF line breaks, as defined in RFC-1459
    val terminatedStrings = serverResponse.utf8String.toString.split("\r\n")
    for (s <- terminatedStrings)
      yield IrcMessageParser(MetaMessage(socketActor, MessageTimeFactory(), s), s)
  }
}
