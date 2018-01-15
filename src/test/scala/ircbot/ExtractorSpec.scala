package ircbot
import akka.actor.{ActorRef, ActorSystem}
import ircbot.models.extractors.{BaseRegexMatcherClass, HttpUrl}
import ircbot.models.{MessageTimeFactory, MetaMessage}
import org.scalatest._

import scala.util.matching.Regex.MatchIterator

class ExtractorSpec extends FlatSpec with Matchers {
  val system = ActorSystem("spec-actors")
  val responseActor: ActorRef = system.actorOf(MessageBuilder.props())

  val s = ":u!n@x PRIVMSG bot_name :hi bot"
  val genericMetaMessage = MessageTypeParser(MetaMessage(responseActor, MessageTimeFactory(), s), s)

  "A html link" should "return an Option[String] of the original link" in {
    val link = "https://www.ietf.org/rfc/rfc1459.txt"
    HttpUrl.isUrl(link) shouldBe Option(link)
  }

  "A youtube link" should "return an Option[String] of the original link" in {
    val ytLink = "https://www.youtube.com/watch?v=UCch_W2Ro38"
    HttpUrl.isYouTube(ytLink) shouldBe Option(ytLink)
  }

  "Regex extractor" should "return a regex object with N matches" in {
    object TestRegexExtractor extends BaseRegexMatcherClass {
      override val regExpression = """(hi) (bot)""".r
    }
    val regexResponse: MatchIterator = genericMetaMessage match {
      case TestRegexExtractor(_, _, _, r) => r
    }
    regexResponse.group(1) shouldBe "hi"
    regexResponse.group(2) shouldBe "bot"

  }


}
