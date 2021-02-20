package dev.tlang.tlang.ast.tmpl

class TmplPkg(var parts: List[TmplID]) extends DeepCopy {
  override def deepCopy(): TmplPkg = {
    new TmplPkg(parts.map(_.deepCopy().asInstanceOf[TmplID]))
  }
}
