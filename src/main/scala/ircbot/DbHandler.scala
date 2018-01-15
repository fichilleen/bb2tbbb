package ircbot

import akka.actor.{Actor, Props}
import com.typesafe.config.Config
import slick.jdbc.SQLiteProfile.api._

object DbHandler {
  def props(config: Config) = Props(classOf[DbHandler], config)
}

class DbHandler(config: Config){
  private val db = Database.forName(
    config.getString("botconfig.join_channels"),
    Option(1)
  )


}
