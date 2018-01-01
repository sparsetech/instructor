package instructor

import java.io.File
import java.time.LocalDate

object TomlConfig {
  case class Meta(title: String,
                  author: String,
                  date: LocalDate = LocalDate.now(),
                  affiliation: String,
                  `abstract`: String,
                  language: String,
                  editSourceUrl: Option[String])
  case class Input(paths: Option[List[String]])
  case class Output(templatePath: Option[String] = None,
                    highlightJsStyle: Option[String] = None,
                    highlightJsPath: Option[String] = None,
                    templateName: String = instructor.Constants.TemplateDefault)
  case class Constants(inherit: Option[String])
  case class Listings(path: Option[String])
  case class Configuration(meta: Meta,
                           constants: Constants = Constants(None),
                           listings: Listings = Listings(None),
                           input: Input,
                           output: Output = Output())

  def loadFile(file: File): Configuration = {
    val configToml = FileUtils.readFile(file)(_.mkString)

    import toml._
    import toml.Codecs._

    Toml.parseAs[Configuration](configToml) match {
      case Left(error) =>
        Log.error(error.toString)
        System.exit(1)
        null

      case Right(v) => v
    }
  }
}

object Configuration {
  def parse(tomlFile: File, assetsPath: File, projectDir: File): Project = {
    val config = TomlConfig.loadFile(tomlFile)

    Project(
      meta = Meta(
        created = config.meta.date,
        title = config.meta.title,
        author = config.meta.author,
        affiliation = config.meta.affiliation,
        `abstract` = config.meta.`abstract`,
        language = config.meta.language
      ),
      templatePath = config.output.templatePath.getOrElse(
        new File(assetsPath, Constants.TemplatesFolder).getPath
      ),
      highlightJsStyle = config.output.highlightJsStyle.getOrElse("default"),
      highlightJsPath = config.output.highlightJsPath.getOrElse(
        new File(assetsPath, Constants.HighlightJsFolder).getPath
      ),
      templateName = config.output.templateName,
      inputPaths = config.input.paths.getOrElse(List.empty),
      editSourceUrl = config.meta.editSourceUrl,
      constantsInherit = config.constants.inherit,
      listingsPath = config.listings.path
    )
  }
}