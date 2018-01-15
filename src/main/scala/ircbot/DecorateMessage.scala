package ircbot

import akka.actor.ActorRef
import akka.util.ByteString
import com.typesafe.config.Config
import ircbot.models.{MessageTimeFactory, MetaMessage}

object DecorateMessage {
  def apply(
             socketActor: ActorRef,
             config: Config,
             dbInstance: String,
             serverResponse: ByteString
           ): Array[IrcMessage] = {
    // IRC always uses CR-LF line breaks, as defined in RFC-1459
    val terminatedStrings = serverResponse.utf8String.toString.split("\r\n")
    for ( s <- terminatedStrings ) yield
      MessageTypeParser(
        MetaMessage(socketActor, config, dbInstance, MessageTimeFactory(), s),
        s
      )
  }
}
