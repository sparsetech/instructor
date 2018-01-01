package instructor

import pine._

import leaf.{Node => LNode}
import leaf.NodeType
import leaf.html.Writer

object CustomNodeType {
  case class SourceFile(path: String) extends NodeType
}

class CustomWriter(editSourceUrl: Option[String]) extends Writer {
  import CustomNodeType._

  val sourceFile = { sourceFile: LNode[SourceFile] =>
    editSourceUrl match {
      case None => List.empty
      case Some(editUrl) =>
        val url = editUrl + sourceFile.tpe.path
        List(html"""<a class="edit" href=$url>Edit chapter ⤴</a>""")
    }
  }

  override def node(node: LNode[_]): List[Node] = node.tpe match {
    case _: SourceFile => sourceFile(node.asInstanceOf[LNode[SourceFile]])
    case _ => super.node(node)
  }
}
