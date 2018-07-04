package ircbot

import java.net.InetSocketAddress

import akka.actor._
import akka.io.{IO, Tcp}
import akka.util.ByteString
import ircbot.models.MessageTimeFactory

object SocketClient {
  def props(remote: InetSocketAddress,
            replies: ActorRef) =
    Props(classOf[SocketClient], remote, replies)
}

// Largely copied from https://doc.akka.io/docs/akka/current/io-tcp.html?language=scala#using-tcp

class SocketClient(remote: InetSocketAddress,
                   listener: ActorRef)
    extends Actor {

  import Tcp._
  import context.system

  IO(Tcp) ! Connect(remote)
  def receive: PartialFunction[Any, Unit] = {
    case CommandFailed(_: Connect) ⇒
      listener ! "connect failed"
      context stop self

    // TODO: SSL
    case c @ Connected(remote_host, _) ⇒

      listener ! c
      println(s"Connected to ${remote_host.getHostString}")
      val connection = sender()
      connection ! Register(self)

      context become {
        case data: ByteString ⇒
          println(s"Wrote ${MessageTimeFactory.apply().timeString} ${data.utf8String}")
          connection ! Write(data)

        case CommandFailed(_: Write) ⇒
          // O/S buffer was full
          listener ! "write failed"

        case Received(data) ⇒
          DecorateMessage(context.self, data).foreach(listener ! _)

        case "close" ⇒
          println("Hit case 'close'")
          connection ! Close

        case _: ConnectionClosed ⇒
          // TODO: Incremental backoff for reconnect, and eventually kill the process
          println("Hit case _")
          listener ! "connection closed"
          context stop self
      }
  }
}
