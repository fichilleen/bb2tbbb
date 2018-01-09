package ircbot

sealed trait IrcMessage {
  def rawMessage: String
}

sealed trait UserMessage extends IrcMessage {
  def hostMaskFrom: String
  def nickFrom: String
  def nameFrom: String
  def hostFrom: String
  def content: String
}

sealed trait SystemMessage extends IrcMessage

case class PingFromServer(rawMessage: String) extends SystemMessage
case class UnknownMessage(rawMessage: String) extends SystemMessage
case class BotJoinsChannel(
                            rawMessage: String,
                            channel: String,
                            nickList: String
                          ) extends SystemMessage


case class ReceiveChannelMessage(
                                   rawMessage: String,
                                   hostMaskFrom: String,
                                   nickFrom: String,
                                   nameFrom: String,
                                   hostFrom: String,
                                   inChannel: String,
                                   content: String
                                 ) extends UserMessage

case class ReceiveDirectMessage(
                                 rawMessage: String,
                                 hostMaskFrom: String,
                                 nickFrom: String,
                                 nameFrom: String,
                                 hostFrom: String,
                                 content: String
                               ) extends UserMessage


object MessageTypeParser {

  /* TODO: Think about how to handle cases where we want the same behaviour for a message from a channel or user */
  private val messageFromChan = """(^:((\w*?)!(\w*?)@(.*?)) PRIVMSG (#\w*) :(.*)$)""".r
  private val messageFromUser = """(^:((\w*?)!(\w*?)@(.*?)) PRIVMSG \w* :(.*)$)""".r
  private val channelNickList = """(^:.*? 353 .*? = (#\w*) :([@+]?.*)$)""".r
  private val pingFromServer = """(^PING.*$)""".r

  def apply(serverMessage: String): IrcMessage = serverMessage match {
    case messageFromChan(r, hm, nf, nmf, hf, ic, c) => ReceiveChannelMessage(r, hm, nf, nmf, hf, ic, c)
    case messageFromUser(r, hm, nf, nmf, hf, c) => ReceiveDirectMessage(r, hm, nf, nmf, hf, c)
    case pingFromServer(p) => PingFromServer(p)
    case channelNickList(r, c, nl) => BotJoinsChannel(r, c, nl)
    case x => UnknownMessage(x)
  }
}
