package instructor

import java.io.File

case class Options(watch : Boolean = false,
                   file  : Option[File] = None,
                   assets: Option[File] = None,
                   output: Option[File] = None)

object Main {
  val parser = new scopt.OptionParser[Options]("instructor") {
    head("instructor", BuildInfo.version)

    opt[File]('a', "assets").valueName("<path>")
      .action((v, c) => c.copy(assets = Some(v)))
      .text("assets path (default: $PWD/assets/)")

    opt[File]('o', "out").valueName("<path>")
      .action((v, c) => c.copy(output = Some(v)))
      .text("output path (default: $PWD/output/)")

    arg[File]("<file>.toml")
      .action((v, c) => c.copy(file = Some(v)))
      .text("project file")
  }

  def main(args: Array[String]): Unit = {
    Log.info(Ansi.bold(Constants.Generator))

    parser.parse(args, Options()) match {
      case Some(config) =>
        Builder.build(config.file.get, config.assets, config.output)
        System.exit(0)
      case None => System.exit(1)
    }
  }
}