package ircbot.customCommands.privMsgCommands

import akka.actor.{Actor, Props}
import ircbot._
import ircbot.models.GetChanNickMessage

import scala.util.Random

object respondToHello {def props() = Props(classOf[respondToHello])}

private object DuplicatedResponses {
  val ack: String = "* ack_ imagines a blog post next week where the new guy talks about his trip to the dark web where a group of hackers coded a bot to respond with ascii cocks"

  val aga = "<Agamemnon> which of you drunk fuckers stuck ileostomy bags over the front of my house?"

  def goibhniu(): String = {
    val resA = "goibhniu should order a mac, then return it because it doesn't have a purely functional package manager and declarative configuration system"
    val resB = "<goibhniu> feed me to the pigs"
    if((Random.nextInt(6) % 2) == 0) resA
    else resB
  }
}

class respondToHello extends Actor {
  override def receive: PartialFunction[Any, Unit] = {
    case ReceiveMessage(mm, _, luser, None, "hello") =>
      mm.socketActor ! PrivMsg(luser.nick, s"yo $luser.nick. Your hostmask is ${luser.hostMask}").message
    case GetChanNickMessage(socket, channel, luser, message) =>
      val responseString = message match {
        case "hello" => s"yo ${luser.nick}"
        case "cock!" => "8======D"
        case "COCK!" => "8===============D"

        // Nick specific japes
        case "420thingyit!" => "thingy today was thingy! It's always thingy!"
        case "ack_!" => DuplicatedResponses.ack
        case "ack!" => DuplicatedResponses.ack
        case "aga!" => DuplicatedResponses.aga
        case "Agamemnon!" => DuplicatedResponses.aga
        case "ctrl!" => "<ctrl> i'll bring the cyber lube"
        case "dw!" =>
          socket ! PrivMsg(channel, "smell").message
          socket ! PrivMsg(channel, "yer").message
          socket ! PrivMsg(channel, "WHIPS").message
          ""
        case "f34r!" => "<f34r> flip me - a bit of penis mentioned and this is the most chatty wasteland has been this year"
        case "fado!" => "<fado> i also imagine web designers calling themselves developers"
        case "fich!" => "<fado> fuelled by beer and spite."
        case "goibhniu!" => DuplicatedResponses.goibhniu()
        case "haize!" => "2016-02-05 - Never forget the day haize accidently ran an open proxy"
        case "kry0!" => "<kry0> i find a good ratio is 10 lines of code -> 1 line of coke"
        case "rooboy!" => "<rooboy> just saw a plane with a really dark chemtrail.. that means they're spraying the *really* bad stuff, yeah ?"
        case "thingys!" => s"thingy has ${Random.nextInt(1500)} 420thingyits!"
        case "wave!" => """\o\"""
        case "/o/" => """\o/"""
        case _ => ""
      }
      if(!responseString.isEmpty) socket ! PrivMsg(channel, responseString).message
  }
}

