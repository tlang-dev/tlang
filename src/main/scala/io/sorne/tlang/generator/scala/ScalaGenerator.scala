package io.sorne.tlang.generator.scala

import io.sorne.tlang.ast.tmpl.func.TmplFunc
import io.sorne.tlang.ast.tmpl.{TmplBlock, TmplImpl, TmplImplContent}
import io.sorne.tlang.generator.CodeGenerator

class ScalaGenerator extends CodeGenerator {

  override def generate(tmpl: TmplBlock): String = {
    val str = new StringBuilder()
    tmpl.pkg.foreach(str ++= "package " ++= _ ++= "\n\n")
    tmpl.uses.foreach(_.foreach((str ++= "import " ++= _.name ++= "\n")))
    tmpl.impls.foreach(str ++= genBlocks(_))
    str.toString
  }

  def genBlocks(impls: List[TmplImpl]): String = {
    val str = new StringBuilder
    impls.foreach(str ++= genClasses(_) ++ "\n\n")
    str.toString
  }

  def genClasses(impl: TmplImpl): String = {
    val str = new StringBuilder
    str ++= "class " ++= impl.name
    impl.fors.foreach(_.zipWithIndex.foreach {
      case (for1, 0) => str ++= " extends " ++= for1.name
      case (for1, 1) => str ++= " with " ++= for1.name
      case (for1, _) => str ++= ", " ++= for1.name
    })
    str ++= " {\n" ++= genImplContent(impl.content) ++= "}\n\n"
    str.toString
  }

  def genImplContent(content: Option[List[TmplImplContent]]): String = {
    val str = new StringBuilder
    content.foreach(_.foreach {
      case func: TmplFunc => str ++= ScalaImplFuncGenerator.gen(func)
    })
    str.toString
  }


}
