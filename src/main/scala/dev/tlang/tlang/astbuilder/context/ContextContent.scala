package dev.tlang.tlang.astbuilder.context

import dev.tlang.tlang.tmpl.DeepCopy

case class ContextContent(resource: ContextResource, line: Int, charPos: Int) extends DeepCopy {
  override def deepCopy(): ContextContent = ContextContent(
    ContextResource(new String(resource.rootDir),
      new String(resource.fromRoot),
      new String(resource.pkg),
      new String(resource.name)),
    line,
    charPos)
}
