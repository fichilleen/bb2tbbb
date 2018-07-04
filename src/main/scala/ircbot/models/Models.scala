package ircbot.models

import java.text.SimpleDateFormat
import java.util.Date

import akka.actor.ActorRef

sealed case class MetaMessage(
    socketActor: ActorRef,
    timeStamp: MessageTime,
    serverMessage: String
)

sealed case class MessageTime(dateClass: Date,
                              epochMillis: Long,
                              timeString: String)

object MessageTimeFactory {
  def apply(epoch: Option[Long] = None): MessageTime = {
    val d = epoch match {
      case Some(e) => new java.util.Date(e)
      case None    => new java.util.Date()
    }
    val sdf = new SimpleDateFormat("HH:mm:ss:SSS").format(d)
    MessageTime(d, System.currentTimeMillis(), sdf)
  }
}
