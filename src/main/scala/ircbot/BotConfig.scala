package ircbot

import com.typesafe.config.{Config, ConfigFactory}

import scala.collection.JavaConverters._
import scala.collection.mutable

object BotConfig {
  val conf: Config = ConfigFactory.load()

  def getString(string: String): String = conf.getString(string)
  def getInt(string: String): Int = conf.getInt(string)
  def getStringList(string: String): mutable.Buffer[String] = conf.getStringList(string).asScala
}
