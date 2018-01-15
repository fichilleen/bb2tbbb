package ircbot

import akka.actor._
import akka.io.{IO, Tcp}
import java.net.InetSocketAddress

import akka.util.ByteString
import com.typesafe.config.Config

object SocketClient {
  def props(remote: InetSocketAddress, config: Config, initMessages: Seq[ByteString], replies: ActorRef) =
    Props(classOf[SocketClient], remote, config, initMessages, replies)
}

// Largely copied from https://doc.akka.io/docs/akka/current/io-tcp.html?language=scala#using-tcp

class SocketClient(
                    remote: InetSocketAddress,
                    config: Config,
                    initMessages: Seq[ByteString],
                    listener: ActorRef)
  extends Actor {

  import Tcp._
  import context.system

  IO(Tcp) ! Connect(remote)
  def receive: PartialFunction[Any, Unit] = {
    case CommandFailed(_: Connect) ⇒
      listener ! "connect failed"
      context stop self

    case c @ Connected(remote, local) ⇒
      listener ! c
      val connection = sender()
      connection ! Register(self)
      initMessages.foreach(connection ! Write(_))
      context become {
        case data: ByteString ⇒
          println(s"Wrote ${data.utf8String}")
          connection ! Write(data)
        case CommandFailed(_: Write) ⇒
          // O/S buffer was full
          listener ! "write failed"
        case Received(data) ⇒
          DecorateMessage(context.self, config, data).foreach(listener ! _)
        case "close" ⇒
          connection ! Close
        case _: ConnectionClosed ⇒
          listener ! "connection closed"
          context stop self
      }
  }
}

