package dev.tlang.tlang.ast.tmpl

case class TmplProp(props: List[String]) extends DeepCopy {
  override def deepCopy(): TmplProp =  TmplProp(props.map(new String(_)))
}
