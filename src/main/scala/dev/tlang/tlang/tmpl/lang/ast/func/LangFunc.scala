package dev.tlang.tlang.tmpl.lang.ast.func

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast._
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class LangFunc(context: Option[ContextContent], var annots: Option[List[LangAnnotation]] = None, var props: Option[LangProp] = None, var preNames: Option[List[LangID]] = None, var name: LangID, var curries: Option[List[LangFuncParam]], var content: Option[LangExprContent[_]],
                    var ret: Option[List[LangType]] = None, postPros: Option[LangProp] = None) extends LangExpression[LangFunc] with LangContent[LangFunc] with AstContext {
  override def deepCopy(): LangFunc = LangFunc(context,
    if (annots.isDefined) Some(annots.get.map(_.deepCopy())) else None,
    if (props.isDefined) Some(props.get.deepCopy()) else None,
    if (preNames.isDefined) Some(preNames.get.map(_.deepCopy().asInstanceOf[LangID])) else None,
    name.deepCopy().asInstanceOf[LangID],
    if (curries.isDefined) Some(curries.get.map(_.deepCopy())) else None,
    if (content.isDefined) Some(content.get.deepCopy().asInstanceOf[LangExprContent[_]]) else None,
    if (ret.isDefined) Some(ret.get.map(_.deepCopy())) else None,
    if (postPros.isDefined) Some(postPros.get.deepCopy()) else None)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangFunc]): Int = 0

  override def getElement: LangFunc = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangFunc.name)),
    Some(List(
      BuildLang.createAttrEntity(context, "name", name.toEntity),
      BuildLang.createAttrEntity(context, "content", content.map(_.toEntity).getOrElse(EntityValue(context, None, None))),
    ))
  )

  override def toModel: ModelSetEntity = LangFunc.model
}

object LangFunc {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}