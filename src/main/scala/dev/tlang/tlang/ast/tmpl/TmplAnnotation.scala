package dev.tlang.tlang.ast.tmpl

case class TmplAnnotation(var name: String, values: Option[List[TmplAnnotationParam]]) extends DeepCopy {
  override def deepCopy(): TmplAnnotation = TmplAnnotation(new String(name),
    if(values.isDefined) Some(values.get.map(_.deepCopy())) else None)
}
