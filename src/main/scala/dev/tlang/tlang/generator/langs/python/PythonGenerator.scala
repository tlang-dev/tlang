package dev.tlang.tlang.generator.langs.python

import dev.tlang.tlang.ast.tmpl.func.{TmplFunc, TmplFuncCurry}
import dev.tlang.tlang.ast.tmpl._
import dev.tlang.tlang.generator.langs.java.NewJavaGenerator.{genExpression, mkSeq}
import dev.tlang.tlang.generator.{CodeGenerator, Seq}

class PythonGenerator extends CodeGenerator {
  override def generate(tmpl: TmplBlock): String = {
    ""
  }
}

object PythonGenerator {

  def genBlock(tmplBlock: TmplBlock): Seq = {
    val root = Seq()
    root
  }

  def genIncludes(uses: Option[List[TmplUse]]): Iterable[Seq] = {
    val str: Array[Seq] = Array.ofDim[Seq](uses.fold(0)(_.size))
    uses.foreach(_.zipWithIndex.foreach(use => {
      val seq = Seq("import")
      seq += " " += mkSeq(use._1.parts, ".")
      str(use._2) = seq
    }))
    str
  }

  def genContents(impls: List[TmplNode[_]]): Iterable[Seq] = {
    val str: Array[Seq] = Array.ofDim[Seq](impls.size)
    impls.zipWithIndex.foreach(impl => str(impl._2) = genContent(impl._1))
    str
  }

  def genContent(impl: TmplNode[_]): Seq = {
    impl match {
      case func: TmplFunc => genFunc(func)
      case expr: TmplExpression[_] => genExpression(expr)
      case impl: TmplImpl => genImpl(impl)
    }
  }

  def genImpl(impl: TmplImpl): Seq = {
    val str = Seq()
    var cur = str
    //    cur += genAnnotations(impl.annots)
    cur = impl.props.fold(Seq.add(cur, "class"))(prop => cur += genProps(prop)) += " " += impl.name.toString
    if (impl.fors.isDefined) {
      cur = cur += " " += impl.fors.get.props.fold(Seq("("))(genProps(_)) += " "
      //cur = cur += mkSeq(impl.fors.get.types.map(implFor => genType(implFor)), ",")
      cur = cur += ")"
    }
    if (impl.withs.isDefined) {
      cur = cur += " " += impl.withs.get.props.fold(Seq("implements"))(genProps(_)) += " "
      //cur = cur += mkSeq(impl.fors.get.types.map(implFor => genType(implFor)), ",")
    }
    if (impl.content.isDefined) cur -> genContents(impl.content.get)
    str
  }

  def genFunc(func: TmplFunc): Seq = {
    val str = Seq()
    str += func.props.fold(Seq("def"))(prop => genProps(prop)) += " "
    str += func.name.toString
    str += genCurrying(func.curries)
    str += func.postPros.fold(Seq())(prop => genProps(prop) += " ")
    str += ":"
    //if (func.content.isDefined) str += genExprBlock(func.content.get)
    str
  }

  def genCurrying(curries: Option[List[TmplFuncCurry]]): Seq = {
    if (curries.isDefined) mkSeq(curries.get.map(genFuncCurry), "")
    else Seq("()")
  }

  def genFuncCurry(curry: TmplFuncCurry): Seq = {
    val str = Seq()
    str += "("
    curry.params.foreach(params => str += mkSeq(params.map(genParam), ","))
    str += ")"
    str
  }

  def genProps(props: TmplProp, addSpace: Boolean = false): Seq = {
    val seq = Seq()
    seq += mkSeq(props.props, " ")
    if (addSpace) seq += " "
    seq
  }

  def genParam(param: TmplParam): String = {
    val str = Seq()
/*    if (param.`type`.isDefined) str += genType(param.`type`.get) += " "*/
    //str += param.name.toString
    str.toString()
  }
}
