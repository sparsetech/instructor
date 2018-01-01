package instructor

import java.io.File

object SbtParser {
  def parseConstants(file: File): Map[String, String] =
    FileUtils.readFile(file) { source =>
      val regex = """(\w+) in ThisBuild := "(.*)"""".r
      regex.findAllMatchIn(source.mkString).toList.map { x =>
        x.group(1) -> x.group(2)
      }.toMap
    }
}