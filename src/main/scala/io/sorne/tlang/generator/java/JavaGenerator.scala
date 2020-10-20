package io.sorne.tlang.generator.java

import io.sorne.tlang.ast.tmpl.{TmplBlock, TmplImpl}
import io.sorne.tlang.generator.CodeGenerator

class JavaGenerator extends CodeGenerator{
  override def generate(tmpl: TmplBlock): String = {
    val str = new StringBuilder()
    tmpl.pkg.foreach(str ++= "package " ++= _ ++= "\n\n")
    tmpl.uses.foreach(_.foreach((str ++= "import " ++= _.name ++= "\n")))
    tmpl.impls.foreach(str ++= genBlocks(_))
    str.toString
  }

  def genBlocks(impls: List[TmplImpl]): String = {
    ""
  }
}
