package dev.tlang.tlang.generator.langs.xml

import dev.tlang.tlang.ast.tmpl._
import dev.tlang.tlang.ast.tmpl.condition.TmplOperation
import dev.tlang.tlang.ast.tmpl.primitive._
import dev.tlang.tlang.generator.formatter.Formatter
import dev.tlang.tlang.generator.{CodeGenerator, Seq}

class XMLGenerator extends CodeGenerator {
  override def generate(tmpl: TmplBlock): String = {
    Formatter.format(XMLGenerator.genBlock(tmpl), XMLFormatter.formatter())
  }
}

object XMLGenerator {

  def genBlock(tmpl: TmplBlock): Seq = {
    val root = Seq()
    tmpl.content.foreach(root ++= genContents(_))
    root
  }

  def genContents(impls: List[TmplNode[_]]): Iterable[Seq] = {
    val str: Array[Seq] = Array.ofDim[Seq](impls.size)
    impls.zipWithIndex.foreach(impl => str(impl._2) = genContent(impl._1))
    str
  }

  def genContent(impl: TmplNode[_]): Seq = {
    impl match {
      case expr: TmplExpression[_] => genExpression(expr)
      case _ => Seq()
    }
  }

  def genExpression(expr: TmplExpression[_], endOfStatement: Boolean = false): Seq = {
    expr match {
      case primitive: TmplPrimitiveValue[_] => genPrimitive(primitive)
    }
  }

  def genPrimitive(primitive: TmplPrimitiveValue[_]): Seq = {
    primitive match {
      case entity: TmplEntityValue => genEntityValue(entity)
      case TmplStringValue(_, value) => genTmplID(value)
      case TmplBoolValue(_, value) => Seq(if (value) "true" else "false")
      case TmplLongValue(_, value) => Seq(value.toString)
      case TmplDoubleValue(_, value) => Seq(value.toString)
      case _ => Seq()
    }
  }

  def genEntityValue(entity: TmplEntityValue): Seq = {
    val seq = Seq("<")
    seq += genOptTmplID(entity.name)
    entity.params.foreach(_.foreach(attribute => {
      val attr = attribute.asInstanceOf[TmplAttribute]
      seq += " " += genOptTmplID(attr.attr) += "=\"" += genOperation(attr.value) += "\""
    }))
    if (entity.attrs.isEmpty) Seq.addTo(seq, "/>")
    else {
      seq += ">"
      entity.attrs.foreach(_.foreach(parameter => {
        val param = parameter.asInstanceOf[TmplAttribute]
        seq += genOperation(param.value)
      }))
      seq += "</" += genOptTmplID(entity.name) += ">"
      seq
    }
  }

  def genOptTmplID(tmplID: Option[TmplID]): Seq = {
    tmplID match {
      case Some(value) => genTmplID(value)
      case None => Seq()
    }
  }

  def genTmplID(tmplId: TmplID): Seq = {
    Seq(tmplId.toString)
  }

  def genOperation(block: TmplOperation): Seq = {
    block.content match {
      case Left(_) => Seq()
      case Right(cond) => Seq() += genExpression(cond)
    }
  }

}
