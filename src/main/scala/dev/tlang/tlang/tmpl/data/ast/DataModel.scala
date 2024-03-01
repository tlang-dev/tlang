package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.{AstModel, BuildAstTmpl}

object DataModel {

  val pkg = "tlang.tmpl.data"

  val dataModel: AstModel = AstModel(None, ManualType(pkg, "DataNode"), None, None, Some(List(
    BuildAstTmpl.createModelAttrEntity(None, Some("context"), LangModel.langContext.getType)
  )))

  val getAll: List[AstModel] = List(
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
