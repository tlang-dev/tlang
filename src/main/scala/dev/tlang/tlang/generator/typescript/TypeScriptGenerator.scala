package dev.tlang.tlang.generator.typescript

import dev.tlang.tlang.ast.tmpl.TmplBlock
import dev.tlang.tlang.generator.CodeGenerator
import dev.tlang.tlang.generator.style.CStyle

class TypeScriptGenerator extends CodeGenerator {
  override def generate(tmpl: TmplBlock): String = TypeScriptGenerator.genBlock(tmpl)
}

object TypeScriptGenerator extends CStyle {
  override def commaRequired(): Boolean = true

  override def includeKeyword(): String = "import"

  override def packageKeyword(): String = ""

  override def defaultImplProps(): String = "export class"

  override def defaultFuncProps(): String = "function"
}
