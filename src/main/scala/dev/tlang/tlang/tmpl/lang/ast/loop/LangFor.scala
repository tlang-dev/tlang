package dev.tlang.tlang.tmpl.lang.ast.loop

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{EntityValue, NullValue, TLangString}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.ast.{LangExprContent, LangExpression, LangModel}
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Value}
import tlang.internal.{ContextContent, TmplID}

case class LangFor(context: Null[ContextContent], var variable: TmplID, var start: Option[LangOperation], forType: ForType.ForType, var cond: LangOperation, var content: LangExprContent[_]) extends LangExpression[LangFor] {
  override def deepCopy(): LangFor = LangFor(context,
    variable.deepCopy().asInstanceOf[TmplID],
    if (start.isDefined) Some(start.get.deepCopy()) else None,
    forType, cond.deepCopy(), content.deepCopy().asInstanceOf[LangExprContent[_]])

  override def compareTo(value: Value[LangFor]): Int = 0

  override def getElement: LangFor = this

  override def getType: String = getClass.getSimpleName


  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangFor.name)),
    Some(List(
      BuildLang.createAttrEntity(context, "variable", variable.toEntity),
      BuildLang.createAttrNull(context, "start",
        if (start.isDefined) Some(start.get.toEntity) else None,
        None
      ),
      BuildLang.createAttrStr(context, "forType", ForType.value(forType)),
      BuildLang.createAttrEntity(context, "cond", cond.toEntity),
      BuildLang.createAttrEntity(context, "content", content.toEntity),
    ))
  )

  override def toModel: ModelSetEntity = LangFor.model
}

object LangFor {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("variable"), ModelSetType(Null.empty(), TmplID.name)),
    ModelSetAttribute(Null.empty(), Some("start"), ModelSetType(Null.empty(), NullValue.name)),
    ModelSetAttribute(Null.empty(), Some("forType"), ModelSetType(Null.empty(), TLangString.getType)),
    ModelSetAttribute(Null.empty(), Some("cond"), ModelSetType(Null.empty(), LangOperation.name)),
    ModelSetAttribute(Null.empty(), Some("content"), ModelSetType(Null.empty(), LangExprContent.name)),
  )))
}

object ForType extends Enumeration {
  type ForType = Value
  val IN, TO, UNTIL = Value

  def value(forType: ForType): String = {
    forType match {
      case IN => "IN"
      case TO => "TO"
      case UNTIL => "UNTIL"
    }
  }
}
