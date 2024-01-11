package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.tmpl.lang.ast.call.TmplCallObj
import dev.tlang.tlang.tmpl.lang.ast.condition.TmplOperation
import dev.tlang.tlang.tmpl.lang.ast.func.TmplFunc
import dev.tlang.tlang.tmpl.lang.ast.primitive.TmplEntityValue

object LangModel {

  val getAll: List[ModelSetEntity] = List(
    LangBlock.model,
    LangFullBlock.model,
    TmplPkg.model,
    TmplUse.model,
    TmplContent.model,
    TmplFunc.model,
    TmplReturn.model,
    TmplEntityValue.model,
    TmplExprBlock.model,
    TmplImpl.model,
    TmplOperation.model,
    TmplCallObj.model
  )

}
