package instructor

object Log {
  import Ansi._
  import ColourScheme._

  def error(message: String): Unit =
    println(foreground(red2)(bold("[error]") + " " + message))

  def warn(message: String): Unit =
    println(foreground(yellow2)(bold("[warn]") + " " + message))

  def debug(message: String): Unit =
    println(foreground(green2)(bold("[debug]") + " " + message))

  def info(message: String): Unit =
    println(foreground(blue2)(bold("[info]") + " " + message))
}
