package ircbot

import akka.actor.{ActorRef, ActorSystem}
import ircbot.models.{MessageTimeFactory, MetaMessage}
import org.scalatest._

class MessageParserSpec extends FlatSpec with Matchers {
  // TODO: Mock actors and config
  val system = ActorSystem("spec-actors")
  val responseActor: ActorRef = system.actorOf(MessageBuilder.props())
  val genericMetaMessage = MetaMessage(responseActor, MessageTimeFactory(), "x")


  "A ping message" should "return a pong class" in {
    val s = "PING :some.server.name"
    MessageParser(genericMetaMessage, s) shouldEqual PingFromServer(genericMetaMessage, s)
  }

  "A message from a channel" should "return a receive channel message class" in {
    val s = ":u!n@x PRIVMSG #test :hi bot"
    MessageParser(genericMetaMessage, s) shouldEqual
      ReceiveMessage(genericMetaMessage, s, Luser("u!n@x","u", "n", "x"), Some("#test"), "hi bot")
  }

  "A pm" should "return a receive direct message class" in {
    val s = ":u!n@x PRIVMSG bot_name :hi bot"
    MessageParser(genericMetaMessage, s) shouldEqual
      ReceiveMessage(genericMetaMessage, s, Luser("u!n@x", "u", "n", "x"), None, "hi bot")
  }


}
