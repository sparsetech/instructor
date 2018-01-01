package instructor

import java.io.File
import java.nio.file.{Files, Path, Paths}

import scala.io.Source
import scala.io.BufferedSource
import scala.collection.JavaConverters._

import pine.HtmlParser

private [instructor] object FileUtils {
  def stripExtension(str: String): String = {
    val idx = str.lastIndexOf('.')
    if (idx != -1) str.substring(0, idx)
    else str
  }

  def readFile[T](file: File)(f: BufferedSource => T): T = {
    val source = Source.fromFile(file)
    try f(source) finally source.close()
  }

  def writeFile(file: java.io.File)
               (f: java.io.PrintWriter => Unit): Unit = {
    val p = new java.io.PrintWriter(file)
    try f(p) finally p.close()
  }

  def absolutePath(): String = new File("").getAbsolutePath

  def readTemplate(file: File): pine.Tag[Singleton] =
    FileUtils.readFile(file) { source =>
      HtmlParser.fromString(source.mkString)
    }

  def resolvePath(path: String, base: File): File = {
    val file = new File(path)
    if (file.isAbsolute) file else new File(base, path)
  }

  /** Supports globs (using asterisks as placeholder) */
  def resolvePaths(base: File, paths: List[String]): List[(Path, File)] =
    paths.flatMap { path =>
      val p = Paths.get(path)
      val r = resolvePath(p.toString, base)

      Files.newDirectoryStream(r.toPath.getParent, p.getFileName.toString)
        .asScala
        .toList
        .map(f => (base.toPath.relativize(f), f.toFile))
        .sortBy(_._1.toString)
    }
}