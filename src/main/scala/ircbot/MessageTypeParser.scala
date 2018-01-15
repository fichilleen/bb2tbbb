package ircbot

import ircbot.models.MetaMessage

sealed trait IrcMessage {
  def metaMessage: MetaMessage
  def rawMessage: String
}

sealed trait UserMessage extends IrcMessage {
  def metaMessage: MetaMessage
  def hostMaskFrom: String
  def nickFrom: String
  def nameFrom: String
  def hostFrom: String
  def content: String
}

sealed trait SystemMessage extends IrcMessage

case class PingFromServer(metaMessage: MetaMessage, rawMessage: String) extends SystemMessage
case class UnknownMessage(metaMessage: MetaMessage, rawMessage: String) extends SystemMessage
case class BotJoinsChannel(
                            metaMessage: MetaMessage,
                            rawMessage: String,
                            channel: String,
                            nickList: String
                          ) extends SystemMessage


case class ReceiveMessage(
                                   metaMessage: MetaMessage,
                                   rawMessage: String,
                                   hostMaskFrom: String,
                                   nickFrom: String,
                                   nameFrom: String,
                                   hostFrom: String,
                                   inChannel: Option[String],
                                   content: String
                                 ) extends UserMessage

object MessageTypeParser {

  /* TODO: Think about how to handle cases where we want the same behaviour for a message from a channel or user */
  private val messageFromChan = """(^:((\w*?)!(\w*?)@(.*?)) PRIVMSG (#\w*) :(.*)$)""".r
  private val messageFromUser = """(^:((\w*?)!(\w*?)@(.*?)) PRIVMSG \w* :(.*)$)""".r
  private val channelNickList = """(^:.*? 353 .*? = (#\w*) :([@+]?.*)$)""".r
  private val pingFromServer = """(^PING.*$)""".r

  def apply(mm: MetaMessage, serverMessage: String): IrcMessage = serverMessage match {
    case messageFromChan(raw, hostmask, nick, realname, userhost, chan, content) =>
      ReceiveMessage(mm, raw, hostmask, nick, realname, userhost, Some(chan), content)
    case messageFromUser(raw, hostmask, nick, realname, userhost, content) =>
      ReceiveMessage(mm, raw, hostmask, nick, realname, userhost, None, content)
    case pingFromServer(p) => PingFromServer(mm, p)
    case channelNickList(r, c, nl) => BotJoinsChannel(mm, r, c, nl)
    case x => UnknownMessage(mm, x)
  }
}
