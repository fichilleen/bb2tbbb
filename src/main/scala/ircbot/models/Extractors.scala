package ircbot.models

import akka.actor.ActorRef
import ircbot.{Luser, ReceiveMessage}

import scala.util.matching.Regex
import scala.util.matching.Regex.MatchIterator

object FindResponseDestination {
  def apply(from: ReceiveMessage): String = {
    from.inChannel match {
      case Some(channel) => channel
      case _ => from.luser.nick
    }
  }
}

trait BaseRegexMatcher {
  def regExpression: Regex
}

abstract class BaseRegexMatcherClass extends BaseRegexMatcher {
  val regExpression: Regex = """(placeholder)""".r
  def unapply (arg: ReceiveMessage): Option[(MetaMessage, String, Luser, MatchIterator)] = {
    val matchResult = regExpression.findAllIn(arg.content)
    matchResult.hasNext match {
      case true =>
        Some(arg.metaMessage, FindResponseDestination(arg), arg.luser, matchResult)
      case false => None
    }
  }
}

object HttpUrl {
  private val urlPattern = """(http[s]?:\/\/[-A-Za-z0-9+&@#\/%?=~_|!:,.;]+[-A-Za-z0-9+&@#\/%=~_|])""".r
  private val youtubePattern = """((?:https?:\/\/)?(?:www\.)?youtu\.?be(?:\.com)?\/?.*(?:watch|embed)?(?:.*v=|v\/|\/)([\w\-_]+)\&?)""".r
  def isUrl(text: String): Option[String] = text match {
    case urlPattern(u) => Some(u)
    case _ => None
  }
  def isYouTube(text: String): Option[String] = text match {
    case youtubePattern(full, _) => Some(full)
    case _ => None
  }
}

object GetChanNickMessage {
  def unapply(arg: ReceiveMessage): Option[(ActorRef, String, Luser, String)] =
    Some(arg.metaMessage.socketActor, FindResponseDestination(arg), arg.luser, arg.content)
}

object GetChanNickTimeMessage {
  def unapply(arg: ReceiveMessage): Option[(ActorRef, String, Luser, MessageTime, String)] =
    Some(arg.metaMessage.socketActor, FindResponseDestination(arg), arg.luser, arg.metaMessage.timeStamp, arg.content)
}

object GetChanNickMessageContainingLink {
  def unapply(arg: ReceiveMessage): Option[(ActorRef, String, Luser, String)] = {
    val parsedLink = HttpUrl.isUrl(arg.content)
    parsedLink match {
      case Some(u) => Some(arg.metaMessage.socketActor, FindResponseDestination(arg), arg.luser, u)
      case _ => None
    }
  }
}
