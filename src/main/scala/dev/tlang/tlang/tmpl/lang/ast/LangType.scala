package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast.call.LangCallFuncParam
import dev.tlang.tlang.tmpl._
import tlang.core.Type
import tlang.internal.ContextContent

case class LangType(context: Option[ContextContent], var name: TmplID, var generic: Option[LangGeneric] = None, isArray: Boolean = false, var currying: Option[List[LangCallFuncParam]] = None) extends AstTmplNode {
  //  override def deepCopy(): LangType = LangType(context, name.deepCopy().asInstanceOf[TmplID],
  //    if (generic.isDefined) Some(generic.get.deepCopy()) else None,
  //    if (isArray) true else false,
  //    if (currying.isDefined) Some(currying.get.map(_.deepCopy())) else None,
  //  )

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangType.model),
    Some(List(
      //      BuildAstTmpl.createAttrEntity(context, "name", name.toEntity),
      //      BuildLang.createAttrNull(context, "generic",
      //        generic,
      //        None
      //      ),
    ))
  )

  override def getContext: Option[ContextContent] = context

  override def getElement: LangType = this

  override def getType: Type = LangType.modelName

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangType.model
}

object LangType {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(LangModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrTmplID(None, Some("name")),
    BuildAstTmpl.createModelAttrNull(None, Some("generic")),
  )))
}
