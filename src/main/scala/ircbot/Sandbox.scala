package ircbot

object Sandbox extends {
  val pings = Seq("PING", "PING :ABCDEF123")

  private val pingFromServer = """(^PING (:\S*)?$)""".r

  def test1: Option[String] = Some("yo")
  def test2: Option[String] = Some("yyoo")
  def test3: Option[String] = None

  def mn(args: Array[String]): Unit = {
    val f = for {
      a <- test1
      b <- test2
      c <- test2
    } yield (a, b, c)
    println(f)

  }

  def apply() = {
    pings.foreach {
      _ match {
        case pingFromServer(a, _, c) =>
          println(s"matched $a, $c")
        case p =>
          println(s"didn't match $p")
      }
    }
  }
}

//Sandbox()


import java.util.{Calendar, GregorianCalendar, TimeZone}

object Ts extends {
  private val tz = TimeZone.getTimeZone("Eire")

  def now()= {
    val date = new GregorianCalendar(tz)
    date.set(Calendar.HOUR_OF_DAY, 22)
    date.set(Calendar.MINUTE, 26)
    date.set(Calendar.SECOND, 0)
    date.set(Calendar.MILLISECOND, 0)
    date.getTimeInMillis
  }

  def testComparison() = {
    System.currentTimeMillis() >= Ts.now() &&
    System.currentTimeMillis() <= (Ts.now() + 59)
  }

  def man(args: Array[String]): Unit = {
    println(testComparison())

  }

}