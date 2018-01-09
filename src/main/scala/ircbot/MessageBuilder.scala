package ircbot

import akka.actor.{Actor, Props}
import akka.util.ByteString

sealed trait BotResponse {
  val rawString: String
  def message: ByteString = ByteString(rawString ++ "\r\n")
}

case class Pong(rawString: String = "PONG") extends BotResponse
case class PrivMsg(recipient: String, sendMessage: String) extends {
  val rawString: String = s"PRIVMSG $recipient :$sendMessage"
} with BotResponse


object MessageBuilder {
  def props() = Props(classOf[MessageBuilder])
}

class MessageBuilder extends Actor {

  override def receive: PartialFunction[Any, Unit] = {
    case _: PingFromServer => sender() ! Pong()
    case bjc: BotJoinsChannel =>
      sender() ! PrivMsg(bjc.channel, "hello pals")
  }
}
