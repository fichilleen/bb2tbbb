package ircbot.customCommands.privMsgCommands.timeGames

import java.util.{Calendar, GregorianCalendar, TimeZone}

object Timestamps {
  private val tz = TimeZone.getTimeZone("Europe/Belfast")

  def midnight(): Long = {
    val date = new GregorianCalendar(tz)
    date.set(Calendar.HOUR_OF_DAY, 0)
    date.set(Calendar.MINUTE, 0)
    date.set(Calendar.SECOND, 0)
    date.set(Calendar.MILLISECOND, 0)
    date.getTimeInMillis
  }

  def first(): Long = {
    val date = new GregorianCalendar(tz)
    date.set(Calendar.HOUR_OF_DAY, 4)
    date.set(Calendar.MINUTE, 0)
    date.set(Calendar.SECOND, 0)
    date.set(Calendar.MILLISECOND, 0)
    date.getTimeInMillis
  }

  def leet(): Long = {
    val date = new GregorianCalendar(tz)
    date.set(Calendar.HOUR_OF_DAY, 13)
    date.set(Calendar.MINUTE, 37)
    date.set(Calendar.SECOND, 0)
    date.set(Calendar.MILLISECOND, 0)
    date.getTimeInMillis
  }

  def haizeit(): Long = {
    val date = new GregorianCalendar(tz)
    date.set(Calendar.HOUR_OF_DAY, 14)
    date.set(Calendar.MINUTE, 20)
    date.set(Calendar.SECOND, 0)
    date.set(Calendar.MILLISECOND, 0)
    date.getTimeInMillis
  }

  def blazeit(): Long = {
    val date = new GregorianCalendar(tz)
    date.set(Calendar.HOUR_OF_DAY, 16)
    date.set(Calendar.MINUTE, 20)
    date.set(Calendar.SECOND, 0)
    date.set(Calendar.MILLISECOND, 0)
    date.getTimeInMillis
  }
}
