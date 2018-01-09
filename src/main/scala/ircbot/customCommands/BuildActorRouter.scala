package ircbot.customCommands

import akka.actor.{ActorContext, ActorRef, Props}
import akka.routing.{ActorRefRoutee, BroadcastRoutingLogic, Router}

object BuildActorRouter extends {

  private def buildBroadcastRouter(propsList: Seq[Props], ac: ActorContext): Router = {
    {
      val routees = propsList
      val rr = routees.map(buildRoutees(_, ac)).toIndexedSeq
      Router(BroadcastRoutingLogic(), rr)
    }
  }

  private def buildRoutees(p: Props, ac: ActorContext): ActorRefRoutee = {
    val a = ac.actorOf(p)
    ac.watch(a)
    ActorRefRoutee(a)
  }

  def apply(propsList: Seq[Props], ac: ActorContext): Router = {
    buildBroadcastRouter(propsList, ac)
  }
}
