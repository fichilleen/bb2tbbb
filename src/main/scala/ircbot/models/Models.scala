package ircbot.models

import java.text.SimpleDateFormat
import java.util.Date

import akka.actor.ActorRef
import com.typesafe.config.Config

sealed case class MetaMessage(
                               socketActor: ActorRef,
                               config: Config,
                               timeStamp: MessageTime,
                               serverMessage: String
                             )
sealed case class MessageTime(dateClass: Date, epochMillis: Long, timeString: String)

object MessageTimeFactory {
  def apply(): MessageTime = {
    val d: Date = new java.util.Date()
    val sdf = new SimpleDateFormat("HH:mm:ss:SSS").format(d)
    MessageTime(d, System.currentTimeMillis(), sdf)
  }
}


