package dev.tlang.tlang.generator.langs.dart

import dev.tlang.tlang.tmpl._
import dev.tlang.tlang.tmpl.lang.ast.call._
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.ast.func.{LangAnnotationParam, LangFunc}
import dev.tlang.tlang.tmpl.lang.ast.primitive._
import dev.tlang.tlang.generator.CodeGenerator
import dev.tlang.tlang.generator.formatter.{FormatRule, Indent}
import dev.tlang.tlang.generator.langs.BlockGenerator
import dev.tlang.tlang.generator.langs.common.GenParameter
import dev.tlang.tlang.tmpl.lang.ast.{LangAffect, LangAnnotation, LangAttribute, LangBlock, LangExprBlock, LangGeneric, LangIf, LangImpl, LangParam, LangPkg, LangProp, LangReturn, LangSetAttribute, LangSpecialBlock, LangUse, LangVar}

class DartGeneratorGen3 extends CodeGenerator {
  override def generate(tmpl: LangBlock): String = {
//    DartGeneratorGen3.generateBlock(tmpl)
    ""
  }
}

object DartGeneratorGen3 {

  private val blocks: Map[String, BlockGenerator] = Map(
    clean(LangBlock.getClass.getName) -> GenericBlock,
    clean(LangImpl.getClass.getName) -> GenericImpl,
    clean(LangFunc.getClass.getName) -> GenericFunc,
    clean(LangIf.getClass.getName) -> GenericIf,
    clean(LangPkg.getClass.getName) -> GenericGenerator.genPackage,
    clean(LangUse.getClass.getName) -> GenericGenerator.genUse,
    clean(LangReturn.getClass.getName) -> GenericGenerator.genReturn,
    clean(LangVar.getClass.getName) -> GenericGenerator.genVar,
    clean(LangAnnotation.getClass.getName) -> GenericGenerator.genAnnot,
    clean(LangAnnotationParam.getClass.getName) -> GenericGenerator.genAnnotParam,
    clean(LangExprBlock.getClass.getName) -> GenericExprBlock,
    clean(LangSpecialBlock.getClass.getName) -> GenericGenerator.genSpecialBlock,
//    clean(TmplFuncCurry.getClass.getName) -> GenericGenerator.genFuncCurry,
    clean(LangParam.getClass.getName) -> GenericGenerator.genParam,
//    clean(TmplType.getClass.getName) -> GenericGenerator.genType,
//    clean(TmplCurryParam.getClass.getName) -> GenericGenerator.genTypeCurry,
    clean(LangGeneric.getClass.getName) -> GenericGenerator.genGeneric,
    clean(LangProp.getClass.getName) -> GenericGenerator.genProps,
    clean(LangSetAttribute.getClass.getName) -> GenericGenerator.genSetAttribute,
    clean(LangOperation.getClass.getName) -> GenericGenerator.genOperation,
    //    TmplCallObjType. -> GenericGenerator.genCallObjType,
    clean(LangCallVar.getClass.getName) -> GenericGenerator.genCallVar,
    clean(LangCallObj.getClass.getName) -> GenericGenerator.genCallObject,
    clean(LangCallObjectLink.getClass.getName) -> GenericGenerator.genCallLink,
//    clean(TmplCallFunc.getClass.getName) -> GenericGenerator.genCallFunc,
//    clean(TmplAnonFunc.getClass.getName) -> GenericGenerator.genAnonFunc,
    clean(LangCallArray.getClass.getName) -> GenericGenerator.genCallArray,
    clean(LangAffect.getClass.getName) -> GenericGenerator.genAffect,
    clean(LangEntityValue.getClass.getName) -> GenericGenerator.genEntityValue,
    clean(LangAttribute.getClass.getName) -> GenericGenerator.genAttribute,
    clean(LangArrayValue.getClass.getName) -> GenericGenerator.genArrayValue,
    clean(LangStringValue.getClass.getName) -> GenericGenerator.genStringValue,
    clean(LangTextValue.getClass.getName) -> GenericGenerator.genTextValue,
    clean(LangLongValue.getClass.getName) -> GenericGenerator.genLongValue,
    clean(LangDoubleValue.getClass.getName) -> GenericGenerator.genDoubleValue,
    clean(LangBoolValue.getClass.getName) -> GenericGenerator.genBoolValue,
    //    "dev.tlang.tlang.tmpl.lang.ast.TmplID" -> GenericGenerator.genTmplID,
  )

//  private def generateBlock(tmpl: LangBlock): String = {
//    val str = new StringBuilder()
//    generate(tmpl, str, Indent(), DartFormatter.rules, GenParameter.default())
//    str.toString()
//  }
//
//  def generate(node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter): Indent = {
//    var _ind = indent
//    blocks.get(node.getClass.getTypeName).foreach(block => _ind = block.generate(node, str, indent, rules, params, this.generate))
//    _ind
//  }

  def clean(clazz: String): String = clazz.replace("$", "")
}
