package instructor

import java.io.File

import leaf.pipeline
import leaf.{Node, NodeType, Structure}

object Builder {
  def nodesToText(nodes: List[Node[_]]): String =
    nodes.asInstanceOf[List[Node[NodeType]]].collect {
      case Node(NodeType.Text(text), _) => text
      case Node(_, c) => nodesToText(c)
    }.mkString("")

  def printTodos(nodes: List[Node[_]]): Unit = {
    val todos = nodes.flatMap(_.filter(_.tpe == NodeType.Todo))
    if (todos.nonEmpty) {
      Log.info(Ansi.bold("Todos:"))
      todos.foreach { todo =>
        println(Ansi.bold("[todo]") + " " + nodesToText(todo.children))
      }
    }
  }

  def loadChapter(project     : Project,
                  pathRelative: String,
                  pathResolved: File,
                  constants   : Map[String, String]
                 ): Option[List[Node[_]]] = {
    Log.info(s"Parsing ${Ansi.italic(pathResolved.toString)}...")
    val result = Markdown.loadFile(project, pathRelative, pathResolved, constants)
    result match {
      case Left(e) =>
        Log.error(s"Parsing failed")
        e.printStackTrace()
        None

      case Right(r) =>
        printTodos(r)
        Some(r)
    }
  }

  def loadTemplate(templatePath: String,
                   templateName: String): Template[_] = {
    val base = new File(templatePath, templateName)
    val file = new File(base, Constants.LayoutFileName)
    val layout = FileUtils.readTemplate(file)

    // Filter all relative hrefs
    val hrefs: List[String] = layout
      .filterTags(_.tagName == "link")
      .map(_.asInstanceOf[pine.Tag[pine.tag.Link]])
      .flatMap(_.href)
      .filter(!_.startsWith("/"))

    Template(layout, base, hrefs)
  }

  def build(projectFile: File,
            assetsPath : Option[File],
            outputFile : Option[File]): Unit = {
    Log.info("Loading project configuration from " +
      Ansi.italic(projectFile.toString) + "...")

    // Call getAbsoluteFile(), otherwise the object may be null
    val projectDir = projectFile.getAbsoluteFile.getParentFile

    val assetsPathResolved = assetsPath.getOrElse(
      new File(FileUtils.absolutePath(), Constants.AssetsFolder))

    if (!assetsPathResolved.exists()) {
      Log.error(s"Assets path ${Ansi.italic(assetsPathResolved.toString)} does not exist")
      return
    }

    val project = Configuration.parse(projectFile, assetsPathResolved, projectDir)

    val constants = project.constantsInherit.map { path =>
      SbtParser.parseConstants(FileUtils.resolvePath(path, projectDir))
    }.getOrElse(Map.empty[String, String])

    val template = loadTemplate(project.templatePath, project.templateName)

    val files = FileUtils.resolvePaths(projectDir, project.inputPaths)
    Log.info(s"Found ${Ansi.bold(files.length.toString)} file(s) for compilation")

    val chapters =
      files.flatMap { case (source, resolved) =>
        loadChapter(project, source.toString, resolved, constants)
          .map(_
            .asInstanceOf[List[Node[NodeType]]]
            .map(pipeline.SetIds.convert))
      }.flatten

    Log.info(s"Found ${Ansi.bold(chapters.length.toString)} top-level nodes")

    val blocks = project.listingsPath.map(x =>
      pipeline.Listings.read(FileUtils.resolvePath(x, projectDir).getPath))

    val chaptersWithCode = blocks match {
      case None    => chapters
      case Some(b) =>
        val listingsPath = project.listingsPath.get
        Log.debug(s"Loaded " + Ansi.bold(b.size.toString) + " listings from " +
          Ansi.italic(listingsPath))
        chapters.map(ch => pipeline.Listings.embed(ch, b))
    }

    val structure = Structure.tree(NodeType.Chapter, chaptersWithCode)
    structure.foreach { st =>
      Log.debug(s"Chapter title: ${Ansi.italic(nodesToText(st.caption))}")
    }

    val outputPath = outputFile.getOrElse(new File("output"))
    outputPath.mkdirs()

    Log.info(s"Writing output to ${Ansi.italic(outputPath.toString)}...")
    HTML.write(project, projectFile, template, chaptersWithCode, structure, outputPath)
  }
}