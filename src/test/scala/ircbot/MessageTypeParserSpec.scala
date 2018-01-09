package ircbot

import org.scalatest._

class MessageTypeParserSpec extends FlatSpec with Matchers {
  "A ping message" should "return a pong class" in {
    val s = "PING :some.server.name"
    MessageTypeParser(s) shouldEqual PingFromServer(s)
  }

  "A message from a channel" should "return a receive channel message class" in {
    val s = ":u!n@x PRIVMSG #test :hi bot"
    MessageTypeParser(s) shouldEqual ReceiveChannelMessage(s, "u!n@x","u", "n", "x", "#test", "hi bot")
  }

  "A pm" should "return a receive direct message class" in {
    val s = ":u!n@x PRIVMSG bot_name :hi bot"
    MessageTypeParser(s) shouldEqual ReceiveDirectMessage(s, "u!n@x", "u", "n", "x", "hi bot")
  }


}
