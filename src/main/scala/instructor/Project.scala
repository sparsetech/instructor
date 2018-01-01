package instructor

case class Project(
  meta: Meta,

  templatePath: String,
  templateName: String,

  highlightJsStyle: String,
  highlightJsPath: String,

  inputPaths: List[String],

  /* E.g. https://github.com/$user/$project/edit/master/ */
  editSourceUrl: Option[String],

  constantsInherit: Option[String],

  listingsPath: Option[String]
)