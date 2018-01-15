package ircbot
import akka.actor._
import ircbot.customCommands.CommandRouters
import ircbot.models.MetaMessage

object Bot {
  def props() = Props(classOf[Bot])
}

class Bot() extends Actor {
  val commandRouter: ActorRef = context.actorOf(CommandRouters.props())
  override def receive: Receive = {
    case mm: MetaMessage =>
      println(s"metamessage - ${mm.timeStamp.timeString} - ${mm.serverMessage}")
      commandRouter ! mm
    case ircm: IrcMessage =>
      println(s"ircmessage - ${ircm.metaMessage.timeStamp.timeString} - ${ircm.rawMessage}")
      commandRouter ! ircm
  }
}
