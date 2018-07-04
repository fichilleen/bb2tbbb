package ircbot

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import ircbot.models.{MessageTimeFactory, MetaMessage}
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration._

class MessageBuilderSpec extends FlatSpec with Matchers {
  val system = ActorSystem("spec-actors")
  implicit val timeout: Timeout = Timeout(50 milliseconds) // needed for `?` below
  val responseActor: ActorRef = system.actorOf(MessageBuilder.props())
  val genericMetaMessage = MetaMessage(responseActor, MessageTimeFactory(), "x")

  "A PingFromServer instance" should "send back a Pong message" in {
    val message = PingFromServer(genericMetaMessage, "PING ABC")
    val r = Await.result(responseActor ? message, timeout.duration)
    r shouldBe Pong("PING ABC")
  }

  // TODO: Remove this test later. The bot greets on channel join as a test, it would be obnoxious in a real channel
  "A BotJoinsChannel instance" should "send back a PrivMsg" in {
    val r = Await.result(responseActor ? BotJoinsChannel(genericMetaMessage, "_","a","_"), timeout.duration)
    r shouldBe PrivMsg("a", "hello pals")
  }
}
