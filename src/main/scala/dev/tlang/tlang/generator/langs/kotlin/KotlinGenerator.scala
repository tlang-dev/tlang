package dev.tlang.tlang.generator.langs.kotlin

import dev.tlang.tlang.ast.common.operation.Operator
import dev.tlang.tlang.tmpl._
import dev.tlang.tlang.tmpl.lang.ast.call._
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.ast.func.{LangAnnotationParam, LangAnonFunc, LangFunc}
import dev.tlang.tlang.tmpl.lang.ast.loop.ForType.ForType
import dev.tlang.tlang.tmpl.lang.ast.loop.{LangDoWhile, LangFor, LangWhile}
import dev.tlang.tlang.tmpl.lang.ast.primitive._
import dev.tlang.tlang.generator.formatter.Formatter
import dev.tlang.tlang.generator.{CodeGenerator, Seq}
import dev.tlang.tlang.tmpl.common.ast.{TmplID, TmplStringID}
import dev.tlang.tlang.tmpl.lang.ast.{LangAffect, LangAnnotation, LangAttribute, LangBlock, LangExprBlock, LangExprContent, LangExpression, LangGeneric, LangIf, LangImpl, LangInclude, LangParam, LangPkg, LangProp, LangReturn, LangSetAttribute, LangSimpleValueType, LangType, LangUse, LangValueType, LangVar}

import scala.language.postfixOps

class KotlinGenerator extends CodeGenerator {
  override def generate(tmpl: LangBlock): String = {
    Formatter.format(KotlinGenerator.genBlock(tmpl), KotlinFormatter.formatter())
  }
}

object KotlinGenerator {

  def genBlock(tmpl: LangBlock): Seq = {
    val root = Seq()
//    root += genPackage(tmpl.pkg)
//    root -> genIncludes(tmpl.uses)
//    tmpl.content.foreach(root -> genContents(_))
    root
  }

  def genPackage(pkg: Option[LangPkg]): Seq = {
    val seq = Seq()
    pkg.foreach(p => seq -> "package" += " " += mkSeq(p.parts, ".") += KotlinFormatter.RET)
    seq
  }

  def genIncludes(uses: Option[List[LangUse]]): Iterable[Seq] = {
    val str: Array[Seq] = Array.ofDim[Seq](uses.fold(0)(_.size))
    uses.foreach(_.zipWithIndex.foreach(use => {
      val seq = Seq("import")
      seq += " " += mkSeq(use._1.parts, ".") += KotlinFormatter.RET
      str(use._2) = seq
    }))
    str
  }

  def genContents(impls: List[TmplNode[_]]): Iterable[Seq] = {
    val str: Array[Seq] = Array.ofDim[Seq](impls.size)
    impls.zipWithIndex.foreach(impl => str(impl._2) = genContent(impl._1))
    str
  }

  def genContent(impl: TmplNode[_], addEndOfStatement: Boolean = false): Seq = {
    impl match {
      case func: LangFunc => genFunc(func)
      case expr: LangExpression[_] => genExpression(expr, addEndOfStatement)
      case impl: LangImpl => genImpl(impl)
      case exprBlock: LangExprBlock => genExprBlock(exprBlock, addEndOfStatement)
      case tmplBlock: LangBlock => genBlock(tmplBlock)
    }
  }

  def genImpl(impl: LangImpl): Seq = {
    val str = Seq()
    var cur = str
    cur += genAnnotations(impl.annots, KotlinFormatter.RET)
    cur = impl.props.fold(Seq.add(cur, "class"))(prop => cur += genProps(prop)) += " " += impl.name.toString
    if (impl.fors.isDefined) {
      cur = cur += " " += impl.fors.get.props.fold(Seq(":"))(genProps(_)) += " "
      cur = cur += mkSeq(impl.fors.get.types.map(implFor => genType(implFor)), ",")
    }
    if (impl.withs.isDefined) {
      val sep = if (impl.fors.isDefined) "" else ":"
      cur = cur += " " += impl.withs.get.props.fold(Seq(sep))(genProps(_)) += " "
      cur = cur += mkSeq(impl.fors.get.types.map(implFor => genType(implFor)), ",")
    }
    cur += "{"
    if (impl.content.isDefined) cur -> genContents(impl.content.get)
    cur += "}"
    str
  }

  def genFunc(func: LangFunc): Seq = {
    val str = Seq()
    str += genAnnotations(func.annots)
    //    str += func.props.fold(Seq("public"))(prop => genProps(prop)) += " "
    func.props.foreach(prop => str += genProps(prop, addSpace = true))
    str += "fun "
    str += func.name.toString
    //    str += func.ret.fold(Seq("void"))(ret => genType(ret.head)) += " "
    //str += genCurrying(func.curries)
    str += func.postPros.fold(Seq())(prop => genProps(prop) += " ")
    if (func.ret.isDefined) str += ":"
    func.ret.foreach(ret => str += genType(ret.head))
    if (func.content.isDefined) str += genContent(func.content.get, addEndOfStatement = true)
    else str += ";"
    str
  }

//  def genCurrying(curries: Option[List[TmplFuncCurry]]): Seq = {
//    if (curries.isDefined) mkSeq(curries.get.map(genFuncCurry), "")
//    else Seq("()")
//  }
//
//  def genFuncCurry(curry: TmplFuncCurry): Seq = {
//    val str = Seq()
//    str += "("
//    curry.params.foreach(params => str += mkSeq(params.map(genParam), ","))
//    str += ")"
//    str
//  }

  def genAnnotations(annots: Option[List[LangAnnotation]], sep: String = ""): Seq = {
    if (annots.isDefined) {
      val str = Seq()
      annots.get.foreach(annot => {
        str += "@" += annot.name.toString
        if (annot.values.isDefined) {
          str += "("
          str += mkSeqFromSeq(annot.values.get.map(value => genAnnotValue(value)), ",")
          //          str += mkSeqFromSeq(annot.values.get.map(value => Seq(value.name.toString) += Seq("=") += genPrimitive(value.value)), ",")
          str += ")"
        }
        str += sep
      })
      str
    }
    else Seq()
  }

  private def genAnnotValue(value: LangAnnotationParam): Seq = {
    val seq = Seq()
    if (value.name.isDefined) {
      seq += value.name.get.toString
      seq += "="
    }
    seq += genValueType(value.value)
    seq
  }

  def genOptionalProps(props: Option[LangProp], addSpace: Boolean = false): Seq = {
    if (props.isDefined) genProps(props.get, addSpace)
    else Seq("")
  }

  def genProps(props: LangProp, addSpace: Boolean = false): Seq = {
    val seq = Seq()
    seq += mkSeq(props.props, " ")
    if (addSpace) seq += " "
    seq
  }

  def genParam(param: LangParam): String = {
    val str = Seq()
    str += genAnnotations(param.annots, sep = " ")
    str += param.name.toString
    str += ":"
    if (param.`type`.isDefined) str += genType(param.`type`.get)
    str.toString()
  }

  def genType(`type`: LangType): Seq = {
    val str = Seq()
    str += `type`.name.toString
    str += genGeneric(`type`.generic)
    if (`type`.isArray) str += "[]"
   // if (`type`.instance.isDefined) str += genTypeCurry(`type`.instance.get)
    str
  }

//  def genTypeCurry(curry: TmplCurryParam): Seq = {
//    val str = Seq()
//    str += "("
////    curry.params.foreach(params => str += mkSeq(params.map(param => genContent(param)), ","))
//    str += ")"
//    str
//  }

  def genGeneric(gen: Option[LangGeneric]): Seq = {
    if (gen.isDefined) {
      val str = Seq()
      str += "<" += mkSeqFromSeq(gen.get.types.map(genType), ",") += ">"
      str
    } else Seq()
  }

  def genExprContent(content: LangExprContent[_], endOfStatement: Boolean = false, newLine: Boolean = false): Seq = {
    content match {
      case block: LangExprBlock => genExprBlock(block)
      case expression: LangExpression[_] => genExpression(expression, endOfStatement)
    }
  }

  def genExprBlock(block: LangExprBlock, endOfStatement: Boolean = false): Seq = {
    val str = Seq()
    str += "{" += mkSeqFromSeq(block.exprs.map(b => genContent(b, endOfStatement)), "") += "}"
    str
  }

  def genExpression(expr: LangExpression[_], endOfStatement: Boolean = false): Seq = {
    expr match {
      case call: LangCallObj => genEndOfStatement(genTmplCallObj(call), endOfStatement)
      case func: LangFunc => genFunc(func)
      case valueType: LangValueType[_] => genValueType(valueType)
      case variable: LangVar => genEndOfStatement(genVar(variable), endOfStatement)
      case ifStmt: LangIf => genIf(ifStmt)
      case forLoop: LangFor => genFor(forLoop)
      case whileLoop: LangWhile => genWhile(whileLoop)
      case doWhile: LangDoWhile => genDoWhile(doWhile)
      //      case incl: TmplInclude => genInclude(incl)
      case ret: LangReturn => genEndOfStatement(genReturn(ret), endOfStatement)
      case affect: LangAffect => genEndOfStatement(genAffect(affect), endOfStatement)
      case anonFunc: LangAnonFunc => genAnonFunc(anonFunc)
    }
  }

  def genEndOfStatement(statement: String, endOfStatement: Boolean): Seq = {
    genEndOfStatement(Seq(statement), endOfStatement)
  }

  def genEndOfStatement(statement: Seq, endOfStatement: Boolean): Seq = {
    var ret = statement
    if (endOfStatement) ret = ret += KotlinFormatter.RET
    statement
  }

  def genAffect(affect: LangAffect): Seq = {
    val str = Seq()
    str += genTmplCallObj(affect.variable) += "=" += genOperation(affect.value)
    str
  }

  def genReturn(ret: LangReturn): Seq = {
    val str = Seq()
    str += "return " += genOperation(ret.operation)
    str
  }

  def genAnonFunc(anonFunc: LangAnonFunc): Seq = {
    val str = Seq()
   // str += genFuncCurry(anonFunc.currying)
    str += genExprContent(anonFunc.content)
    str
  }

  def genIf(ifStmt: LangIf): Seq = {
    val str = Seq()
    str += "if(" += genOperation(ifStmt.cond) += ")"
    str += genExprContent(ifStmt.content, ifStmt.content.isInstanceOf[LangExpression[_]])
    if (ifStmt.elseBlock.isDefined) ifStmt.elseBlock.get match {
      case Left(elseBlock) => str += " else " += genExprContent(elseBlock, elseBlock.isInstanceOf[LangExpression[_]])
      case Right(ifBlock) => str += " else " += genIf(ifBlock)
    }
    str
  }

  def genFor(forLoop: LangFor): Seq = {
    val str = Seq()
    str += "for("
    str += forLoop.variable.toString
//    str += " " += genForType(forLoop.forType) += " "
    str += genOperation(forLoop.cond)
    str += ")"
    str += genExprContent(forLoop.content)
    str
  }

//  def genForType(forType: ForType): Seq = forType match {
//    case dev.tlang.tlang.ast.tmpl.loop.ForType.IN => Seq("in")
//    case dev.tlang.tlang.ast.tmpl.loop.ForType.TO => Seq("to")
//    case dev.tlang.tlang.ast.tmpl.loop.ForType.UNTIL => Seq("until")
//  }

  def genWhile(whileLoop: LangWhile): Seq = {
    val str = Seq()
    str += "while(" += genOperation(whileLoop.cond) += ") "
    str += genExprContent(whileLoop.content, whileLoop.content.isInstanceOf[LangExpression[_]])
    str
  }

  def genDoWhile(doWhile: LangDoWhile): Seq = {
    val str = Seq()
    str += "do " += genExprContent(doWhile.content, doWhile.content.isInstanceOf[LangExpression[_]])
    str += " while(" += genOperation(doWhile.cond) += ");"
    str
  }

  def genTmplCallObj(callObj: LangCallObj): Seq = {
    val str = Seq()
    callObj.props.foreach(prop => str += genProps(prop, addSpace = true))
    str += genCallObjType(callObj.firstCall)
    callObj.calls.foreach(link => str += genCallLink(link))
    str
  }

  def genCallLink(objLink: LangCallObjectLink): Seq = {
    val str = Seq()
    str += objLink.link
    str += genCallObjType(objLink.call)
    str
  }

  def genCallObjType(objType: LangCallObjType[_]): Seq = {
    objType match {
      case array: LangCallArray => genCallArray(array)
      case func: LangCallFunc => genCallFunc(func)
      case variable: LangCallVar => genCallVar(variable)
    }
  }

  def genCallArray(array: LangCallArray): Seq = {
    val str = Seq()
    str += array.name.toString += "[" += genOperation(array.elem) += "]"
    str
  }

  def genCallFunc(func: LangCallFunc): Seq = {
    val str = Seq()
    str += func.name.toString
    if (func.currying.isDefined) {
      func.currying.foreach(_.foreach(curry => {
        str += "("
//        curry.params.foreach(param => str += mkSeq(param.map(attr => attr.asInstanceOf[TmplSetAttribute].name.fold(Seq())(Seq() -> _.toString += ":") += genOperation(attr.asInstanceOf[TmplSetAttribute].value)), ","))
        str += ")"
      }))
    } else str += "()"
    str
  }

  def genCallVar(variable: LangCallVar): Seq = Seq(variable.name.toString)

  def genVar(variable: LangVar): Seq = {
    val str = Seq()
    str += genAnnotations(variable.annots, KotlinFormatter.RET)
    variable.props.foreach(prop => str += genPropsForVar(prop, variable, addSpace = true))
    if (variable.props.isEmpty) {
      if (variable.isOptional) str += "var " else str += "val "
    }
    str += variable.name.toString
    if (variable.`type`.isDefined) {
      str += ":"
      str += genType(variable.`type`.get)
    }
    if (variable.isOptional) str += "?"
    if (variable.value.isDefined) {
      str += "="
      str += genOperation(variable.value.get)
    }
    str += KotlinFormatter.RET
    str
  }

  def genPropsForVar(props: LangProp, variable: LangVar, addSpace: Boolean = false): Seq = {
    val seq = Seq()
    if (props.props.nonEmpty && props.props.head.toString.equals("lateinit")) {
      seq += "lateinit var"
    } else {
      seq += mkSeq(props.props, " ")
    }
    if (addSpace) seq += " "
    seq
  }

  def genValueType(valueType: LangValueType[_]): Seq = {
    valueType match {
      case call: LangCallObj => genTmplCallObj(call)
      case primitive: LangPrimitiveValue[_] => genPrimitive(primitive)
    }
  }

  def genSimpleValueType(valueType: LangSimpleValueType[_]): Seq = {
    valueType match {
      case call: LangCallObj => genTmplCallObj(call)
      case value: LangPrimitiveValue[_] => genPrimitive(value)
    }
  }

  def genOperation(block: LangOperation): Seq = {
    val str = Seq()
    block.content match {
      case Left(block) => str += "(" += genOperation(block) += ")"
      case Right(cond) => str += genExpression(cond)
    }
    if (block.next.isDefined) {
      str += genOperator(block.next.get._1)
      str += genOperation(block.next.get._2)
    }
    str
  }

  def genOperator(op: Operator.operator): Seq = {
    op match {
      case Operator.OR => Seq("||")
      case Operator.AND => Seq("&&")
      case Operator.ADD => Seq("+")
      case Operator.SUBTRACT => Seq("-")
      case Operator.MULTIPLY => Seq("*")
      case Operator.DIVIDE => Seq("/")
      case Operator.MODULO => Seq("%")
      case Operator.EQUAL => Seq("==")
      case Operator.GREATER => Seq(">")
      case Operator.LESSER => Seq("<")
      case Operator.GREATER_OR_EQUAL => Seq(">=")
      case Operator.LESSER_OR_EQUAL => Seq("<=")
      case Operator.NOT_EQUAL => Seq("!=")
    }
  }

  def genPrimitive(primitive: LangPrimitiveValue[_]): Seq = {
    primitive match {
      case string: LangStringValue => genStringValue(string)
      case string: LangTextValue => genTextValue(string)
      case long: LangLongValue => genLongValue(long)
      case double: LangDoubleValue => genDoubleValue(double)
      case bool: LangBoolValue => genBoolValue(bool)
      case array: LangArrayValue => genArrayValue(array)
      case entity: LangEntityValue => genEntityValue(entity)
    }
  }

  def genEntityValue(entity: LangEntityValue): Seq = {
    val str = Seq()
    if (entity.name.isDefined) str += entity.name.get.toString
    str += "("
    str += genEntityValueAttribute(entity.attrs)
    str += ")"
    str
  }

  def genEntityValueAttribute(attrs: Option[List[TmplNode[_]]]): Seq = {
    val str = Seq()
    attrs.foreach(_.foreach { attr =>
      attr match {
        case operation: LangOperation => str += genOperation(operation)
        case attribute: LangAttribute => str += genAttribute(attribute)
        case include: LangInclude =>
      }
    })
    str
  }

  def genAttribute(attr: LangAttribute): Seq = {
    val str = Seq()
    if (attr.`type`.isDefined) str += attr.`type`.get.toString += ":"
    str += genOperation(attr.value)
    str
  }

  def genArrayValue(array: LangArrayValue): Seq = {
    val str = Seq()
    array.`type`.foreach(t => str += genType(t))
    str += "["
    str += genArrayValueParams(array.params.asInstanceOf[Option[List[LangSetAttribute]]])
    str += "]"
    str
  }

  def genArrayValueParams(params: Option[List[LangSetAttribute]]): Seq = {
    val str = Seq()
    if (params.isDefined) {
      //      str += "{"
      str += mkSeq(params.get.map(genSetAttribute), ",")
      //      str += "}"
    }
    str
  }

  def genStringValue(string: LangStringValue): Seq = Seq.build("\"", string.value.toString, "\"")

  def genTextValue(string: LangTextValue): Seq = Seq.build("\"", string.value.toString, "\"")

  def genLongValue(long: LangLongValue): Seq = Seq(long.value.toString)

  def genDoubleValue(double: LangDoubleValue): Seq = Seq(double.value.toString)

  def genBoolValue(bool: LangBoolValue): Seq = Seq(if (bool.value) "true" else "false")

  def genSetAttribute(attr: LangSetAttribute): Seq = {
    val str = Seq()
    if (attr.name.isDefined) str += genOptTmplID(attr.name) += ":"
    str += genOperation(attr.value)
    str
  }

  def genOptTmplID(tmplId: Option[TmplID]): Seq = {
    if (tmplId.isDefined) genTmplID(tmplId.get)
    else Seq()
  }

  def genTmplID(tmplId: TmplID): Seq = {
    tmplId match {
      case str: TmplStringID =>
        val seq = Seq("\"")
        seq += str.toString
        seq += "\""
        seq
      case _ => Seq(tmplId.toString)
    }
  }

  def mkSeq[T](list: List[T], sep: String): Seq = {
    if (list.isEmpty) Seq()
    else if (list.size == 1) Seq(list.head.toString)
    else {
      val seq = Seq()
      var currentSeq = seq
      for (i <- 0 until list.size - 1) {
        currentSeq = currentSeq += list(i).toString
        if (sep.nonEmpty) currentSeq = currentSeq += sep
      }
      currentSeq += list.last.toString
      seq
    }
  }

  def mkSeqFromSeq(list: List[Seq], sep: String): Seq = {
    if (list.isEmpty) Seq()
    else if (list.size == 1) list.head
    else {
      val seq = Seq()
      var currentSeq = seq
      for (i <- 0 until list.size - 1) {
        currentSeq = currentSeq += list(i)
        if (sep.nonEmpty) currentSeq = currentSeq += sep
      }
      currentSeq += list.last.toString
      seq
    }
  }
}
