package dev.tlang.tlang.generator.langs.dart

import dev.tlang.tlang.ast.tmpl._
import dev.tlang.tlang.ast.tmpl.func.TmplFunc
import dev.tlang.tlang.generator.CodeGenerator
import dev.tlang.tlang.generator.formatter.{FormatRule, Indent}
import dev.tlang.tlang.generator.langs.BlockGenerator

class DartGeneratorGen3 extends CodeGenerator {
  override def generate(tmpl: TmplBlock): String = {
    DartGeneratorGen3.generateBlock(tmpl)
  }
}

object DartGeneratorGen3 {

  private val blocks: Map[String, BlockGenerator] = Map(
    clean(TmplBlock.getClass.getName) -> GenericBlock,
    clean(TmplImpl.getClass.getName) -> GenericImpl,
    clean(TmplFunc.getClass.getName) -> GenericFunc,
    clean(TmplPkg.getClass.getName) -> GenericGenerator.genPackage,
    clean(TmplUse.getClass.getName) -> GenericGenerator.genUse,
    clean(TmplReturn.getClass.getName) -> GenericGenerator.genReturn,
    clean(TmplVar.getClass.getName) -> GenericGenerator.genVar,
    clean(TmplExprBlock.getClass.getName) -> GenericExprBlock,
  )

  private def generateBlock(tmpl: TmplBlock): String = {
    val str = new StringBuilder()
    generate(tmpl, str, Indent(), DartFormatter.rules)
    str.toString()
  }

  def generate(node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule]): Indent = {
    var _ind = indent
    blocks.get(node.getClass.getTypeName).foreach(block => _ind = block.generate(node, str, indent, rules, this.generate))
    _ind
  }

  def clean(clazz: String): String = clazz.replace("$", "")
}
