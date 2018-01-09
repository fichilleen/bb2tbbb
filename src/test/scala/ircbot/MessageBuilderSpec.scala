package ircbot

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import org.scalatest._

import scala.concurrent.Await

class MessageBuilderSpec extends FlatSpec with Matchers {
  val system = ActorSystem("spec-actors")
  implicit val timeout: Timeout = Timeout(50 milliseconds) // needed for `?` below
  val responseActor: ActorRef = system.actorOf(MessageBuilder.props())

  "A PingFromServer instance" should "send back a Pong message" in {
    val r = Await.result(responseActor ? PingFromServer("PING xxx"), timeout.duration)
    r shouldBe Pong()
  }

  // TODO: Remove this test later. The bot greets on channel join as a test, it would be obnoxious in a real channel
  "A BotJoinsChannel instance" should "send back a PrivMsg" in {
    val r = Await.result(responseActor ? BotJoinsChannel("_","a","_"), timeout.duration)
    r shouldBe PrivMsg("a", "hello pals")
  }
}
