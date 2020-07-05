package io.sorne.tlang.generator.scala

import io.sorne.tlang.ast.tmpl.func.TmplFunc
import io.sorne.tlang.ast.tmpl.{TmplBlock, TmplImpl, TmplImplContent}

class ScalaGenerator {

  def gen(tmpl: TmplBlock): String = {
    val str = new StringBuilder()
    tmpl.pkg.foreach(str ++= "package " ++= _ ++= "\n\n")
    tmpl.uses.foreach(_.foreach((str ++= "import " ++= _.name ++= "\n")))
    tmpl.impls.foreach(str ++= gen(_))
    str.toString
  }

  def gen(impls: List[TmplImpl]): String = {
    val str = new StringBuilder
    impls.foreach(str ++= gen(_) ++ "\n\n")
    str.toString
  }

  def gen(impl: TmplImpl): String = {
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
