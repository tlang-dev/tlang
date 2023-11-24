package dev.tlang.tlang.generator.langs.dart

import dev.tlang.tlang.tmpl._
import dev.tlang.tlang.tmpl.lang.ast.call._
import dev.tlang.tlang.tmpl.lang.ast.condition.TmplOperation
import dev.tlang.tlang.tmpl.lang.ast.func.{TmplAnnotationParam, TmplFunc}
import dev.tlang.tlang.tmpl.lang.ast.primitive._
import dev.tlang.tlang.generator.CodeGenerator
import dev.tlang.tlang.generator.formatter.{FormatRule, Indent}
import dev.tlang.tlang.generator.langs.BlockGenerator
import dev.tlang.tlang.generator.langs.common.GenParameter
import dev.tlang.tlang.tmpl.lang.ast.{TmplAffect, TmplAnnotation, TmplAttribute, LangBlock, TmplExprBlock, TmplGeneric, TmplIf, TmplImpl, TmplNode, TmplParam, TmplPkg, TmplProp, TmplReturn, TmplSetAttribute, TmplSpecialBlock, TmplUse, TmplVar}

class DartGeneratorGen3 extends CodeGenerator {
  override def generate(tmpl: LangBlock): String = {
    DartGeneratorGen3.generateBlock(tmpl)
  }
}

object DartGeneratorGen3 {

  private val blocks: Map[String, BlockGenerator] = Map(
    clean(LangBlock.getClass.getName) -> GenericBlock,
    clean(TmplImpl.getClass.getName) -> GenericImpl,
    clean(TmplFunc.getClass.getName) -> GenericFunc,
    clean(TmplIf.getClass.getName) -> GenericIf,
    clean(TmplPkg.getClass.getName) -> GenericGenerator.genPackage,
    clean(TmplUse.getClass.getName) -> GenericGenerator.genUse,
    clean(TmplReturn.getClass.getName) -> GenericGenerator.genReturn,
    clean(TmplVar.getClass.getName) -> GenericGenerator.genVar,
    clean(TmplAnnotation.getClass.getName) -> GenericGenerator.genAnnot,
    clean(TmplAnnotationParam.getClass.getName) -> GenericGenerator.genAnnotParam,
    clean(TmplExprBlock.getClass.getName) -> GenericExprBlock,
    clean(TmplSpecialBlock.getClass.getName) -> GenericGenerator.genSpecialBlock,
//    clean(TmplFuncCurry.getClass.getName) -> GenericGenerator.genFuncCurry,
    clean(TmplParam.getClass.getName) -> GenericGenerator.genParam,
//    clean(TmplType.getClass.getName) -> GenericGenerator.genType,
//    clean(TmplCurryParam.getClass.getName) -> GenericGenerator.genTypeCurry,
    clean(TmplGeneric.getClass.getName) -> GenericGenerator.genGeneric,
    clean(TmplProp.getClass.getName) -> GenericGenerator.genProps,
    clean(TmplSetAttribute.getClass.getName) -> GenericGenerator.genSetAttribute,
    clean(TmplOperation.getClass.getName) -> GenericGenerator.genOperation,
    //    TmplCallObjType. -> GenericGenerator.genCallObjType,
    clean(TmplCallVar.getClass.getName) -> GenericGenerator.genCallVar,
    clean(TmplCallObj.getClass.getName) -> GenericGenerator.genCallObject,
    clean(TmplCallObjectLink.getClass.getName) -> GenericGenerator.genCallLink,
//    clean(TmplCallFunc.getClass.getName) -> GenericGenerator.genCallFunc,
//    clean(TmplAnonFunc.getClass.getName) -> GenericGenerator.genAnonFunc,
    clean(TmplCallArray.getClass.getName) -> GenericGenerator.genCallArray,
    clean(TmplAffect.getClass.getName) -> GenericGenerator.genAffect,
    clean(TmplEntityValue.getClass.getName) -> GenericGenerator.genEntityValue,
    clean(TmplAttribute.getClass.getName) -> GenericGenerator.genAttribute,
    clean(TmplArrayValue.getClass.getName) -> GenericGenerator.genArrayValue,
    clean(TmplStringValue.getClass.getName) -> GenericGenerator.genStringValue,
    clean(TmplTextValue.getClass.getName) -> GenericGenerator.genTextValue,
    clean(TmplLongValue.getClass.getName) -> GenericGenerator.genLongValue,
    clean(TmplDoubleValue.getClass.getName) -> GenericGenerator.genDoubleValue,
    clean(TmplBoolValue.getClass.getName) -> GenericGenerator.genBoolValue,
    //    "dev.tlang.tlang.tmpl.lang.ast.TmplID" -> GenericGenerator.genTmplID,
  )

  private def generateBlock(tmpl: LangBlock): String = {
    val str = new StringBuilder()
    generate(tmpl, str, Indent(), DartFormatter.rules, GenParameter.default())
    str.toString()
  }

  def generate(node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter): Indent = {
    var _ind = indent
    blocks.get(node.getClass.getTypeName).foreach(block => _ind = block.generate(node, str, indent, rules, params, this.generate))
    _ind
  }

  def clean(clazz: String): String = clazz.replace("$", "")
}
