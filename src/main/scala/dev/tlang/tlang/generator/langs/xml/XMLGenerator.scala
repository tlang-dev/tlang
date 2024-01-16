package dev.tlang.tlang.generator.langs.xml

import dev.tlang.tlang.tmpl._
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.ast.primitive._
import dev.tlang.tlang.generator.formatter.Formatter
import dev.tlang.tlang.generator.{CodeGenerator, Seq}
import dev.tlang.tlang.tmpl.lang.ast.{LangAttribute, LangBlock, LangExpression, LangID, LangNode}

class XMLGenerator extends CodeGenerator {
  override def generate(tmpl: LangBlock): String = {
    Formatter.format(XMLGenerator.genBlock(tmpl), XMLFormatter.formatter())
  }
}

object XMLGenerator {

  def genBlock(tmpl: LangBlock): Seq = {
    val root = Seq()
    //tmpl.content.foreach(root -> genContents(_))
    root
  }

  def genContents(impls: List[LangNode[_]]): Iterable[Seq] = {
    val str: Array[Seq] = Array.ofDim[Seq](impls.size)
    impls.zipWithIndex.foreach(impl => str(impl._2) = genContent(impl._1))
    str
  }

  def genContent(impl: LangNode[_]): Seq = {
    impl match {
      case expr: LangExpression[_] => genExpression(expr)
      case _ => Seq()
    }
  }

  def genExpression(expr: LangExpression[_], endOfStatement: Boolean = false): Seq = {
    expr match {
      case primitive: LangPrimitiveValue[_] => genPrimitive(primitive)
    }
  }

  def genPrimitive(primitive: LangPrimitiveValue[_]): Seq = {
    primitive match {
      case entity: LangEntityValue => genEntityValue(entity)
      case LangStringValue(_, value) => genTmplID(value)
      case LangBoolValue(_, value) => Seq(if (value) "true" else "false")
      case LangLongValue(_, value) => Seq(value.toString)
      case LangDoubleValue(_, value) => Seq(value.toString)
      case _ => Seq()
    }
  }

  def genEntityValue(entity: LangEntityValue): Seq = {
    if (entity.name.isDefined) entity.name.get.toString match {
      case "?xml" => genXMLTag(entity)
      case "!DOCTYPE" => genDOCTYPETag()
      case _ => genNormalEntity(entity)
    }
    else Seq()
  }

  def genXMLTag(entity: LangEntityValue): Seq = {
    val seq = Seq("<?xml")
    seq += genParams(entity.params)
    Seq.addTo(seq, " ", "?>")
  }

  def genDOCTYPETag(): Seq = {
    val seq = Seq("<!DOCTYPE>")
    seq
  }

  def genNormalEntity(entity: LangEntityValue): Seq = {
    val seq = Seq("<")
    seq += genOptTmplID(entity.name)
    seq += genParams(entity.params)
    if (entity.attrs.isEmpty) Seq.addTo(seq, "/>")
    else {
      seq += ">"
      entity.attrs.foreach(_.foreach(parameter => {
        val param = parameter.asInstanceOf[LangAttribute]
        seq += genOperation(param.value)
      }))
      seq += "</" += genOptTmplID(entity.name) += ">"
      seq
    }
  }

  def genParams(params: Option[List[LangNode[_]]]): Seq = {
    val seq = Seq()
    params.foreach(_.foreach(attribute => {
      val attr = attribute.asInstanceOf[LangAttribute]
      seq += " " += genOptTmplID(attr.attr) += "=\"" += genOperation(attr.value) += "\""
    }))
    seq
  }

  def genOptTmplID(tmplID: Option[LangID]): Seq = {
    tmplID match {
      case Some(value) => genTmplID(value)
      case None => Seq()
    }
  }

  def genTmplID(tmplId: LangID): Seq = {
    Seq(tmplId.toString)
  }

  def genOperation(block: LangOperation): Seq = {
    block.content match {
      case Left(_) => Seq()
      case Right(cond) => Seq() += genExpression(cond)
    }
  }

}
