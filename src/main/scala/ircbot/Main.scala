package ircbot

import java.net.InetSocketAddress

import akka.actor._

object Main extends App {


  val system = ActorSystem("actors")
  val socketOptions = new InetSocketAddress(
    BotConfig.getString("botconfig.server_host"),
    BotConfig.getInt("botconfig.server_port")
  )
  //val responseActor: ActorRef = system.actorOf(MessageBuilder.props())
  val botActor: ActorRef = system.actorOf(Bot.props())
  val _ = system.actorOf(
    SocketClient.props(socketOptions, botActor),
    name = "SocketActor")

}
