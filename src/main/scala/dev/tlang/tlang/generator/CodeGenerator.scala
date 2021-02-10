package dev.tlang.tlang.generator

import dev.tlang.tlang.ast.tmpl.TmplBlock

trait CodeGenerator {

  def generate(tmpl: TmplBlock): String

}
