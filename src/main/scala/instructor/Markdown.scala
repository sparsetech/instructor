package instructor

import java.io.File

import scala.util.Try

object Markdown {
  def replaceConstants(raw: String, constants: Map[String, String]): String =
    constants.foldLeft(raw) { case (acc, (k, v)) =>
      acc.replaceAllLiterally(s"%$k%", v)
    }

  def loadFile(project: Project,
               pathRelative: String,
               pathResolved: File,
               constants: Map[String, String]
              ): Either[Throwable, List[leaf.Node[_]]] =
    FileUtils.readFile(pathResolved) { reader =>
      val replaced = replaceConstants(reader.mkString, constants)

      Try(
        leaf.markdown.Reader.parse(replaced) :+
        leaf.Node(CustomNodeType.SourceFile(pathRelative))
      ).toEither
    }
}