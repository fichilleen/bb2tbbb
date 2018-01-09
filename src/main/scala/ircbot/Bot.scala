package ircbot
import akka.actor._
import scala.collection.mutable.HashMap

import ircbot.customCommands.CommandRouters

object Bot {
  def props() = Props(classOf[Bot])
}

class Bot() extends Actor {
  /* Using a path to reference the socket actor here. In theory that's not ideal,
  because we don't get the UID for it and so it's not bound to the lifecycle.
  We could get the uid though by calling it with Identity with an ask and checking
  the sender.
  In practice I think it's ok though, because Bot just does routing - so if we
  don't have a living socket, we don't get messages. We don't need to die just
  because it does - and the path will be the same when it comes back
   */
  val connectionActor: ActorSelection  = context.actorSelection("../SocketActor")
  // TODO: Maybe always pass the socket actor down the chain
  val commandRouter: ActorRef = context.actorOf(CommandRouters.props(context.self))

  override def receive: Receive = {
    case bt: BotResponse =>
      connectionActor ! bt.message
    case mm: MetaMessage =>
      println(s"metamessage - ${mm.epoch} - ${mm.msg.rawMessage}")
      commandRouter ! mm.msg
  }
}
