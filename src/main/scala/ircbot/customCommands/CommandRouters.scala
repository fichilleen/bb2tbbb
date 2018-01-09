package ircbot.customCommands

import akka.actor.{Actor, ActorRef, Props}
import akka.routing.Router
import ircbot.customCommands.privMsgCommands.respondToHello
import ircbot.{BotResponse, MessageBuilder, SystemMessage, UserMessage}

object CommandRouters{
  def props(parentActor: ActorRef) = Props(classOf[CommandRouters], parentActor)
}

class CommandRouters(parentActor: ActorRef) extends Actor {

  val privMsgActors: Seq[Props] = Seq (
      Props[respondToHello]
    // More
    // To
    // Come
  )
  val privMsgRouter: Router = BuildActorRouter(privMsgActors, context)
  val sysMsgActor: ActorRef = context.actorOf(Props[MessageBuilder])

  override def receive: PartialFunction[Any, Unit] = {
    case um: UserMessage => privMsgRouter.route(um, sender())
    case sm: SystemMessage => sysMsgActor ! sm
    case br: BotResponse => parentActor ! br
  }

}
