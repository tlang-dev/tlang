package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity}
import .langContext

object DataModel {

  val dataModel: ModelSetEntity = ModelSetEntity(None, "DataNode", None, None, Some(List(
    ModelSetAttribute(None, Some("context"), langContext)
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
