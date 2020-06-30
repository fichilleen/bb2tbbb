package ircbot

import ircbot.models.MetaMessage

case class Luser(hostMask: String, nick: String, name: String, host: String)

sealed trait IrcMessage {
  def metaMessage: MetaMessage
  def rawMessage: String
}

sealed trait UserMessage extends IrcMessage {
  def metaMessage: MetaMessage
  def luser: Luser
  def content: String
}

sealed trait SystemMessage extends IrcMessage

case class PingFromServer(metaMessage: MetaMessage, rawMessage: String) extends SystemMessage
case class ServerAuthRequest(metaMessage: MetaMessage, rawMessage: String) extends SystemMessage
case class UnknownMessage(metaMessage: MetaMessage, rawMessage: String) extends SystemMessage
case class BotJoinsChannel(
  metaMessage: MetaMessage,
  rawMessage: String,
  channel: String,
  nickList: String
) extends SystemMessage

case class ReceivedMessage(
  metaMessage: MetaMessage,
  rawMessage: String,
  luser: Luser,
  content: String,
  channel: Option[String]
) extends UserMessage {
  def responseDestination: String = channel.getOrElse(luser.nick)
}

object IrcMessageParser {
  private val messageFromChan = """(^:((\w*?)!.*(\w*?)@(.*?)) PRIVMSG (#\w*) :(.*)$)""".r
  private val messageFromUser = """(^:((\w*?)!.*(\w*?)@(.*?)) PRIVMSG \w* :(.*)$)""".r
  private val channelNickList = """(^:.*? 353 .*? = (#\w*) :([@+]?.*)$)""".r
  private val serverAuthRequest = """(^:.*? NOTICE AUTH.*)""".r
  private val pingFromServer = """(^PING.*$)""".r

  def apply(mm: MetaMessage, serverMessage: String): IrcMessage = serverMessage match {
    case messageFromChan(raw, hostmask, nick, realname, userhost, chan, content) =>
      ReceivedMessage(mm, raw, Luser(hostmask, nick, realname, userhost), content, Some(chan))
    case messageFromUser(raw, hostmask, nick, realname, userhost, content) =>
      ReceivedMessage(mm, raw, Luser(hostmask, nick, realname, userhost), content, None)
    case pingFromServer(p) =>
      PingFromServer(mm, p)
    case channelNickList(r, c, nl) =>
      BotJoinsChannel(mm, r, c, nl)
    case serverAuthRequest(x) => ServerAuthRequest(mm, x)
    case x => UnknownMessage(mm, x)
  }
}
