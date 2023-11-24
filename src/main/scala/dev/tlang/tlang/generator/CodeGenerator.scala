package dev.tlang.tlang.generator

import dev.tlang.tlang.tmpl.lang.ast.LangBlock

trait CodeGenerator {

  def generate(tmpl: LangBlock): String

}
