package ircbot

import akka.actor.{Actor, Props}
import akka.util.ByteString


sealed trait AuthState
case object Unregistered extends AuthState
case object Unjoined extends AuthState
case object Joined extends AuthState

sealed trait BotResponse {
  val rawString: String
  def message: ByteString = ByteString(rawString ++ "\r\n")
}

case class JoinMessages(rawString: String) extends BotResponse
case class RegisterClient(rawString: String = "PONG") extends BotResponse

case class Pong(original: String) extends {
  /*
    This dirty hack allows us to easily handle different types of ping messages without special handling
    eg, "PING", "PING :ABC123", "PING :irc.mynetwork.net", etc

  */
  val rawString = original.replace("PING", "PONG")
} with BotResponse

case class PrivMsg(recipient: String, sendMessage: String) extends {
  val rawString: String = s"PRIVMSG $recipient :$sendMessage"
} with BotResponse

case class ReplyInChannel(recipient: String, nick: String, sendMessage: String) extends {
  val rawString: String = s"PRIVMSG $recipient :$nick: $sendMessage"
} with BotResponse

case class NickRegistration() extends {
  val rawString: String = s"NICK ${BotConfig.getString("botconfig.bot_nick")}"
} with BotResponse

case class UserRegistration() extends {
  val name = BotConfig.getString("botconfig.bot_name")
  val rawString: String = s"USER $name * localhost $name"
} with BotResponse

object MessageBuilder {
  def props(): Props = Props(classOf[MessageBuilder])
}

object JoinMessages {
  def apply(): JoinMessages = {
    JoinMessages(
      BotConfig.getStringList("botconfig.join_channels").foldLeft("") {
        case (a, b) =>
          a ++ s"JOIN $b\r\n"
      }
    )
  }
}

class MessageBuilder extends Actor {
  override def receive: PartialFunction[Any, Unit] = {
    case a: ServerAuthRequest =>
      a.metaMessage.socketActor ! NickRegistration().message
      a.metaMessage.socketActor ! UserRegistration().message
      context become postConnect
  }

  def postConnect: Receive = {
    case m: PingFromServer =>
      m.metaMessage.socketActor ! Pong(m.rawMessage).message
      m.metaMessage.socketActor ! JoinMessages().message
      context become registered
  }

  def registered: Receive = {
    case m: PingFromServer =>
      m.metaMessage.socketActor ! Pong(m.rawMessage).message
    case bjc: BotJoinsChannel =>
      bjc.metaMessage.socketActor ! PrivMsg(bjc.channel, "morning boyfriends").message
  }
}
