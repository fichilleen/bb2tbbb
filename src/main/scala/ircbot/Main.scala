package ircbot

import java.net.InetSocketAddress

import akka.actor._
import akka.util.ByteString

// In the future, this will be populated from config and made at init time
// Note that hostname and domainname are normally ignored by the IRC server
// when the USER command comes from a directly connected client
case class RegistrationDetails(nick: String, username: String, hostname: String, domainName: String, realName: String)
case class ChannelsOnConnect(channels: Seq[String])


object BuildLogin {
  // TODO: Once config is done, we can just take config params for this
  def buildRegistration(nick: String, user: String, host: String, domain: String, realName: String): Seq[ByteString] = {
    Seq(
      ByteString(s"USER $user $host $domain $realName\n"),
      ByteString(s"NICK $nick\n")
    )
  }

  def buildJoins(channels: Seq[String]): Seq[ByteString] = {
    channels.map(c => ByteString(s"JOIN $c\n"))
  }
}

object Main extends App {
  val initialMessages =
    BuildLogin.buildRegistration("test_bot", "username", "*", "localhost", "realname") ++
    BuildLogin.buildJoins(Array("#test_channel", "#another_channel"))

  val system = ActorSystem("actors")
  val socketOptions = new InetSocketAddress("localhost", 6669)
  //val responseActor: ActorRef = system.actorOf(MessageBuilder.props())
  val botActor: ActorRef = system.actorOf(Bot.props())
  val _ = system.actorOf(SocketClient.props(socketOptions, initialMessages, botActor), name = "SocketActor")

}
