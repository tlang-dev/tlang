package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import tlang.core.Null

object DataModel {

  val pkg = "tlang.tmpl.data"

  val dataModel: ModelSetEntity = ModelSetEntity(Null.empty(), ManualType(pkg, "DataNode"), None, None, Some(List(
    ModelSetAttribute(Null.empty(), Some("context"), LangModel.langContext)
  )))

  val getAll: List[ModelSetEntity] = List(
    DataArray.model,
    DataAttribute.model,
    DataBlock.model,
    DataInclude.model,
    DataNumber.model,
    DataSetAttribute.model,
    DataString.model,
    DataValue.model
  )
}
