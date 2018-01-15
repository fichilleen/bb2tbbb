package ircbot

import java.net.InetSocketAddress

import akka.actor._
import akka.util.ByteString
import com.typesafe.config.ConfigFactory

import scala.collection.JavaConverters._

// when the USER command comes from a directly connected client
case class RegistrationDetails(nick: String, name: String)
case class ChannelsOnConnect(channels: Seq[String])


object BuildLogin {
  // TODO: Once config is done, we can just take config params for this
  def buildRegistration(nick: String, name: String): Seq[ByteString] = {
    Seq(
      ByteString(s"USER $name * localhost $name\n"),
      ByteString(s"NICK $nick\n")
    )
  }

  def buildJoins(channels: Seq[String]): Seq[ByteString] = {
    channels.map(c => ByteString(s"JOIN $c\n"))
  }
}

object Main extends App {

  val conf = ConfigFactory.load()

  val initialMessages =
    BuildLogin.buildRegistration(
      conf.getString("botconfig.bot_nick"),
      conf.getString("botconfig.bot_name")
    ) ++
    BuildLogin.buildJoins(conf.getStringList("botconfig.join_channels").asScala)

  val system = ActorSystem("actors")
  val socketOptions = new InetSocketAddress(
    conf.getString("botconfig.server_host"),
    conf.getInt("botconfig.server_port")
  )
  //val responseActor: ActorRef = system.actorOf(MessageBuilder.props())
  val botActor: ActorRef = system.actorOf(Bot.props())
  val _ = system.actorOf(SocketClient.props(socketOptions, initialMessages, botActor), name = "SocketActor")

}
