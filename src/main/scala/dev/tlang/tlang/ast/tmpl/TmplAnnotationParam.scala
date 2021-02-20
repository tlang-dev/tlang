package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.tmpl.primitive.TmplPrimitiveValue

case class TmplAnnotationParam(var name: String, var value: TmplPrimitiveValue) extends DeepCopy {
  override def deepCopy(): TmplAnnotationParam =
    TmplAnnotationParam(new String(name), value.deepCopy().asInstanceOf[TmplPrimitiveValue])

}
