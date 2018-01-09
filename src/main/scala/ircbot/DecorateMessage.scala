package ircbot

import akka.util.ByteString

case class MetaMessage(epoch: Long, msg: IrcMessage)

object DecorateMessage {
  def apply(serverResponse: ByteString): Array[MetaMessage] = {
    // IRC always uses CR-LF line breaks, as defined in RFC-1459
    val terminatedStrings = serverResponse.utf8String.toString.split("\r\n")
    for ( s <- terminatedStrings ) yield MetaMessage(System.currentTimeMillis(), MessageTypeParser(s))
  }
}
