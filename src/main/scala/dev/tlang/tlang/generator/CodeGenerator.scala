package dev.tlang.tlang.generator

import io.sorne.tlang.ast.tmpl.TmplBlock

trait CodeGenerator {

  def generate(tmpl: TmplBlock): String

}
