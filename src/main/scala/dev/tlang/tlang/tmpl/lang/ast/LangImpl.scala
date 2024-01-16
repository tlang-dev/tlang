package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class LangImpl(context: Option[ContextContent], var annots: Option[List[LangAnnotation]] = None, var props: Option[LangProp] = None, var name: LangID, var fors: Option[LangImplFor], var withs: Option[LangImplWith], var content: Option[List[LangNode[_]]] = None) extends LangContent[LangImpl] with AstContext {
  override def deepCopy(): LangImpl = LangImpl(context,
    if (annots.isDefined) Some(annots.get.map(_.deepCopy())) else None,
    if (props.isDefined) Some(props.get.deepCopy()) else None,
    name.deepCopy().asInstanceOf[LangID],
    if (fors.isDefined) Some(fors.get.deepCopy()) else None,
    if (withs.isDefined) Some(withs.get.deepCopy()) else None,
    if (content.isDefined) Some(content.get.map(_.deepCopy().asInstanceOf[LangContent[_]])) else None
  )

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangImpl]): Int = 0

  override def getElement: LangImpl = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplImplAst.langImpl.name)),
    Some(List(
      BuildLang.createAttrEntity(context, "name", name.toEntity),
      //      BuildLang.createAttrEntity(context, "fors", fors.t),
      BuildLang.createArray(context, "contents", content.map(_.map(_.toEntity)).getOrElse(List())),
    ))
  )

  override def toModel: ModelSetEntity = LangImpl.model
}

object LangImpl {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}