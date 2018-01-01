package instructor

import java.io.File
import java.nio.file.{Files, StandardCopyOption}
import java.time.format.DateTimeFormatter
import java.util.Locale

import pine._

import leaf.Structure

object HTML {
  def locale(meta: Meta): Locale = Locale.forLanguageTag(meta.language)

  def monthAndYear(meta: Meta): String = {
    val pattern = DateTimeFormatter.ofPattern("MMMM YYYY", locale(meta))
    meta.created.format(pattern)
  }

  def pageSkeleton(template: Template[_],
                   title: String,
                   language: String,
                   highlightJsStyle: String,
                   body: List[pine.Node]): Tag[_] =
    template.tag.update { implicit ctx =>
      TagRef[tag.Html].lang := language
      TagRef[tag.Head] += tag.Title.set(title)
      TagRef[tag.Head] += html"""<meta name="generator" content=${Constants.Generator} />"""

      val hjsCss = s"css/$highlightJsStyle.css"
      val hjsJs  = s"js/${Constants.HighlightJsFile}"
      TagRef[tag.Head] += html"""<link rel="stylesheet" href=$hjsCss>"""
      TagRef[tag.Head] += html"""<script src=$hjsJs></script>"""
      TagRef[tag.Head] += html"""<script>hljs.initHighlightingOnLoad()</script>"""

      TagRef("content").replace(body)
    }

  def generatedWith(): List[pine.Node] = {
    val link =
      html"<a href=${Constants.GeneratorUrl}>${Constants.Generator}</a>"
    List(Text("Generated with "), link)
  }

  def writeSinglePage(meta: Meta,
                      editSourceUrl: Option[String],
                      template: Template[_],
                      targetPath: File,
                      highlightJsStyle: String,
                      nodes: List[leaf.Node[_]],
                      structure: List[Structure]): Unit = {
    val writer  = new CustomWriter(editSourceUrl)
    val body    = nodes.flatMap(writer.node)
    val updated = pageSkeleton(template, meta.title, meta.language,
      highlightJsStyle, body)

    val refDate        = TagRef[""]("date")
    val refTitle       = TagRef[""]("title")
    val refAuthor      = TagRef[""]("author")
    val refAbstract    = TagRef[""]("abstract")
    val refAffiliation = TagRef[""]("affiliation")
    val refToc         = TagRef[""]("toc")
    val refFootnotes   = TagRef[""]("footnotes")
    val refGenerator   = TagRef[""]("generator")

    val result = updated.update { implicit ctx =>
      refDate        := monthAndYear(meta)
      refTitle       := meta.title
      refAuthor      := meta.author
      refAffiliation := meta.affiliation
      refAbstract    := meta.`abstract`
      refGenerator   := generatedWith()

      // Do not include subsections
      leaf.html.Writer.tableOfContents(structure, maxDepth = 2) match {
        case None    => refToc.remove()
        case Some(t) => refToc := t
      }
    }

    FileUtils.writeFile(targetPath)(_.write(result.toHtml))
  }

  def write(project: Project,
            projectFile: File,
            template: Template[_],
            nodes: List[leaf.Node[_]],
            structure: List[Structure],
            outputPath: File): Unit = {
    val id = FileUtils.stripExtension(projectFile.getName)
    val htmlPath = new File(outputPath, s"$id.html")

    writeSinglePage(
      project.meta,
      project.editSourceUrl,
      template, htmlPath, project.highlightJsStyle, nodes, structure)

    template.assets.foreach { p =>
      Log.info(s"Copying asset ${Ansi.italic(p)}...")

      val source = new File(template.absolutePath, p)
      val target = new File(outputPath, p)

      // TODO Only copy if changed
      target.mkdirs()

      Files.copy(
        source.toPath, target.toPath,
        StandardCopyOption.REPLACE_EXISTING)
    }

    val js  = new File(outputPath, "js")
    val css = new File(outputPath, "css")

    js.mkdirs()
    css.mkdirs()

    val cssFn = project.highlightJsStyle + ".css"

    val jsTarget  = new File(js,  Constants.HighlightJsFile)
    val cssTarget = new File(css, cssFn)

    Log.info(s"Copying ${Ansi.italic("highlight.js")} assets...")

    Files.copy(
      new File(project.highlightJsPath, Constants.HighlightJsFile).toPath,
      jsTarget.toPath,
      StandardCopyOption.REPLACE_EXISTING)

    Files.copy(
      new File(new File(project.highlightJsPath, "styles"), cssFn).toPath,
      cssTarget.toPath,
      StandardCopyOption.REPLACE_EXISTING)
  }
}
