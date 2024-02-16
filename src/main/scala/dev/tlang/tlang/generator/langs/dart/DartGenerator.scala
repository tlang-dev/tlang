package dev.tlang.tlang.generator.langs.dart

import dev.tlang.tlang.ast.common.operation.Operator
import dev.tlang.tlang.generator.formatter.Formatter
import dev.tlang.tlang.generator.langs.kotlin.KotlinGenerator.{mkSeq, mkSeqFromSeq}
import dev.tlang.tlang.generator.{CodeGenerator, Seq, SeqBuilder}
import dev.tlang.tlang.tmpl._
import dev.tlang.tlang.tmpl.lang.ast.call._
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.ast.func.{LangAnnotationParam, LangAnonFunc, LangFunc}
import dev.tlang.tlang.tmpl.lang.ast.loop.{LangDoWhile, LangFor, LangWhile}
import dev.tlang.tlang.tmpl.lang.ast.primitive._
import dev.tlang.tlang.tmpl.lang.ast._
import tlang.internal.TmplNode

class DartGenerator extends CodeGenerator {
  override def generate(tmpl: LangBlock): String = {
    Formatter.format(DartGenerator.genBlock(tmpl), DartFormatter.formatter())
  }
}

object DartGenerator {

  def genBlock(tmpl: LangBlock): Seq = {
    val root = new SeqBuilder()
    root.setBlockName("block")
    //    root += genPackage(tmpl.pkg)
//    root ++= genIncludes(tmpl.uses)
//    tmpl.content.foreach(genContents(_).foreach(root ++= _))
    root.build()
  }


  def genPackage(pkg: Option[LangPkg]): Seq = Seq()

  def genIncludes(uses: Option[List[LangUse]]): Seq = {
    val str = new SeqBuilder()
    str.setBlockName("includes")
    uses.foreach(_.foreach(use => {
      str += includeKeyword() += " '" += use.parts.mkString("/").replaceFirst("/", ":").replace("/dart", ".dart") += "'"
      if (use.alias.isDefined) str += " as " += use.alias.get.toString
//      str += comma() += DartFormatter.RET
    }))
    str.build()
  }

  def genContents(impls: List[TmplNode[_]], addEndOfStatement: Boolean = false): Iterable[Seq] = {
    val str: Array[Seq] = Array.ofDim[Seq](impls.size)
    impls.zipWithIndex.foreach(impl => str(impl._2) = genContent(impl._1, addEndOfStatement))
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
    val str = new SeqBuilder()
    str.setBlockName("class")
    str.head(genAnnotations(impl.annots, DartFormatter.RET))
    if (impl.props.isDefined) {
      str.head(genProps(impl.props.get))
    } else {
      str.head(Seq("class"))
    }
    str.head(SeqBuilder.build(Seq(" "), Seq(impl.name.toString)))
    if (impl.fors.isDefined) {
      str.head(SeqBuilder.build(Seq(" "), impl.fors.get.props.fold(Seq("extends"))(genProps(_)), Seq(" ")))
      //      cur = cur += " " += impl.withs.get.props.fold(Seq("implements"))(genProps(_)) += " "
//      str.head(mkSeq(impl.fors.get.types.map(implFor => genType(implFor)), ","))
    }
    if (impl.withs.isDefined) {
      val sep = if (impl.fors.isDefined) "" else " "
      str.head(SeqBuilder.build(Seq(" "), impl.withs.get.props.fold(Seq((sep + "implements")))(genProps(_)), Seq(" ")))
//      str.head(mkSeq(impl.withs.get.types.map(implFor => genType(implFor)), ","))
    }
    str.head(Seq("{"))
    if (impl.content.isDefined) genContents(impl.content.get, addEndOfStatement = true).foreach(content => str += content)
    str.bottom(Seq("}"))
    str.build()
  }

  def genFunc(func: LangFunc): Seq = {
    val str = new SeqBuilder()
    str.setBlockName("func")
    str ++= genAnnotations(func.annots)
    //    str += func.props.fold(Seq("public"))(prop => genProps(prop)) += " "
    func.props.foreach(prop => str ++= genProps(prop, addSpace = true))

    val header = new SeqBuilder()
//    func.ret.foreach(ret => header += SeqBuilder.build(genType(ret.head), Seq(" ")))
  //  header += (Seq(func.name.toString))
    //    str += func.ret.fold(Seq("void"))(ret => genType(ret.head)) += " "
//    header += (genCurrying(func.curries))
    header += (func.postPros.fold(Seq())(prop => SeqBuilder.build(genProps(prop), Seq(" "))))
    //    if (func.ret.isDefined) str += ":"
    str ++= header.build()

    if (func.content.isDefined) str ++= genContent(func.content.get, addEndOfStatement = true)
    else str += ";"
    str.build()
  }

  /*def genCurrying(curries: Option[List[TmplFuncCurry]]): Seq = {
    val str = new SeqBuilder()
    if (curries.isDefined) curries.get.foreach(str += genFuncCurry(_))
    else str += "()"
    str.build()
  }

  def genFuncCurry(curry: TmplFuncCurry): Seq = {
    val str = new SeqBuilder()
    str.setBlockName("curry")
    str += "("
    curry.params.foreach(params => params.foreach(str += genParam(_)))
    str += ")"
    str.build()
  }*/

  def genAnnotations(annots: Option[List[LangAnnotation]], sep: String = ""): Seq = {
    if (annots.isDefined) {
      val str = new SeqBuilder()
      str.setBlockName("annot")
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
      str.build()
    }
    else Seq()
  }

  private def genAnnotValue(value: LangAnnotationParam): Seq = {
    val seq = Seq(blockName = "annotValue")
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
    val seq = Seq(blockName = "props")
    seq += mkSeq(props.props, " ")
    if (addSpace) seq += " "
    seq
  }

  def genParam(param: LangParam): Seq = {
    val str = new SeqBuilder()
    str += genAnnotations(param.annots, sep = " ")
//    if (param.`type`.isDefined) str += genType(param.`type`.get) += " "
//    str += param.name.toString
    str.build()
  }

  /*def genType(`type`: TmplType): Seq = {
    val str = new SeqBuilder()
    str += `type`.name.toString
    str += genGeneric(`type`.generic)
    if (`type`.isArray) str += "[]"
    if (`type`.instance.isDefined) str += genTypeCurry(`type`.instance.get)
    str.build()
  }

  def genTypeCurry(curry: TmplCurryParam): Seq = {
    val str = new SeqBuilder()
    str += "("
//    curry.params.foreach(params => str += mkSeq(params.map(param => genContent(param)), ","))
    str += ")"
    str.build()
  }

  def genGeneric(gen: Option[TmplGeneric]): Seq = {
    if (gen.isDefined) {
      val str = new SeqBuilder()
      str += "<" += mkSeqFromSeq(gen.get.types.map(genType), ",") += ">"
      str.build()
    } else Seq()
  }*/

  def genExprContent(content: LangExprContent[_], endOfStatement: Boolean = false, newLine: Boolean = false): Seq = {
    content match {
      case block: LangExprBlock => genExprBlock(block)
      case expression: LangExpression[_] => genExpression(expression, endOfStatement)
    }
  }

  def genExprBlock(block: LangExprBlock, endOfStatement: Boolean = false): Seq = {
    val str = new SeqBuilder()
    str.setBlockName("exprBlock")
    str.head(Seq("{"))
    block.exprs.foreach(b => str ++= genContent(b, endOfStatement))
    str.bottom(Seq("}"))
    str.build()
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
    if (endOfStatement) SeqBuilder.build(statement, Seq(DartFormatter.END_OF_STATEMENT))
    else statement
  }

  def genAffect(affect: LangAffect): Seq = {
    val str = new SeqBuilder()
    str += genTmplCallObj(affect.variable) += "=" += genOperation(affect.value)
    str.build()
  }

  def genReturn(ret: LangReturn): Seq = {
    val str = new SeqBuilder()
    str.setBlockName("return")
    str += "return"
    str += " "
    str += genOperation(ret.operation)
    str.build()
  }

  def genAnonFunc(anonFunc: LangAnonFunc): Seq = {
    val str = new SeqBuilder()
    str.setBlockName("anonFunc")
//    str ++= genFuncCurry(anonFunc.currying)
    str ++= genExprContent(anonFunc.content)
    str.build()
  }

  def genIf(ifStmt: LangIf): Seq = {
    val str = new SeqBuilder()
    str.setBlockName("if")
    str.head(SeqBuilder.build(Seq("if"), Seq("("), genOperation(ifStmt.cond), Seq(")")))
    str += genExprContent(ifStmt.content, ifStmt.content.isInstanceOf[LangExpression[_]])
    if (ifStmt.elseBlock.isDefined) ifStmt.elseBlock.get match {
      case Left(elseBlock) => str += " else " += genExprContent(elseBlock, elseBlock.isInstanceOf[LangExpression[_]])
      case Right(ifBlock) => str += " else " += genIf(ifBlock)
    }
    str.build()
  }

  def genFor(forLoop: LangFor): Seq = {
    val str = new SeqBuilder()
    str.setBlockName("for")
    str.setSeq("for")
    str += "("
//    str += forLoop.variable.toString
//    str += " " += genForType(forLoop.forType) += " "
    str += genOperation(forLoop.cond)
    str += ")"
    str += genExprContent(forLoop.content)
    str.build()
  }

//  def genForType(forType: ForType): Seq = forType match {
//    case dev.tlang.tlang.tmpl.loop.ForType.IN => Seq("in")
//    case dev.tlang.tlang.tmpl.loop.ForType.TO => Seq("to")
//    case dev.tlang.tlang.tmpl.loop.ForType.UNTIL => Seq("until")
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
    val str = new SeqBuilder()
    str.setBlockName("callObject")
    callObj.props.foreach(prop => str += genProps(prop, addSpace = true))
    str += genCallObjType(callObj.firstCall)
    callObj.calls.foreach(link => str += genCallLink(link))
    str.build()
  }

  def genCallLink(objLink: LangCallObjectLink): Seq = {
    val str = new SeqBuilder()
    str += objLink.link
    str += genCallObjType(objLink.call)
    str.build()
  }

  def genCallObjType(objType: LangCallObjType[_]): Seq = {
    objType match {
      case array: LangCallArray => genCallArray(array)
      case func: LangCallFunc => genCallFunc(func)
//      case variable: LangCallVar => genCallVar(variable)
    }
  }

  def genCallArray(array: LangCallArray): Seq = {
    val str = new SeqBuilder()
//    str += array.name.toString += "[" += genOperation(array.elem) += "]"
    str.build()
  }

  def genCallFunc(func: LangCallFunc): Seq = {
    val str = new SeqBuilder()
    str.setBlockName("callFunc")
//    str += func.name.toString
    if (func.currying.isDefined) {
      func.currying.foreach(_.foreach(curry => {
        str += Seq("(")
//        curry.params.foreach(param =>  str += mkSeq(param.map(attr => SeqBuilder.build(attr.asInstanceOf[TmplSetAttribute].name.fold(Seq())( p=>SeqBuilder.build(Seq(p.toString) ,Seq(":"))), genOperation(attr.asInstanceOf[TmplSetAttribute].value))), ","))
        str += Seq(")")
      }))
    } else str += "()"
    str.build()
  }

//  def genCallVar(variable: LangCallVar): Seq = Seq(variable.name.toString)

  def genVar(variable: LangVar): Seq = {
    val str = new SeqBuilder()
    str.setBlockName("var")
    str += genAnnotations(variable.annots, DartFormatter.RET)
    variable.props.foreach(prop => str += genPropsForVar(prop, variable, addSpace = true))
    if (variable.props.isEmpty && variable.`type`.isEmpty) {
      str += "var "
    }
    if (variable.`type`.isDefined) {
//      str += genType(variable.`type`.get)
      str += " "
    }
    str += variable.name.toString
    if (variable.isOptional) str += "?"
    if (variable.value.isDefined) {
      str += "="
      str += genOperation(variable.value.get)
    }
    //    str.bottom(Seq(DartFormatter.END_OF_STATEMENT))
    str.build()
  }

  def genPropsForVar(props: LangProp, variable: LangVar, addSpace: Boolean = false): Seq = {
    val seq = new SeqBuilder()
    if (props.props.nonEmpty && props.props.head.toString.equals("lateinit")) {
      seq += "lateinit var"
    } else {
      seq += mkSeq(props.props, " ")
    }
    if (addSpace) seq += " "
    seq.build()
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
    val str = new SeqBuilder()
    str.setBlockName("operation")
    block.content match {
      case Left(block) => str += "(" += genOperation(block) += ")"
      case Right(cond) => str += genExpression(cond)
    }
    if (block.next.isDefined) {
      str += genOperator(block.next.get._1)
      str += genOperation(block.next.get._2)
    }
    str.build()
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
//    array.`type`.foreach(t => str += genType(t))
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
//    if (attr.name.isDefined) str += genOptTmplID(attr.name) += ":"
    str += genOperation(attr.value)
    str
  }

//  def genOptTmplID(tmplId: Option[TmplID]): Seq = {
//    if (tmplId.isDefined) genTmplID(tmplId.get)
//    else Seq()
//  }

//  def genTmplID(tmplId: TmplID): Seq = {
//    tmplId match {
//      case str: TmplStringID =>
//        val seq = Seq("\"")
//        seq += str.toString
//        seq += "\""
//        seq
//      case _ => Seq(tmplId.toString)
//    }
//  }


  //  override def genArrayValue(array: TmplArrayValue): String = {
  //    val params = array.params.asInstanceOf[Option[List[TmplSetAttribute]]]
  //    if (params.isDefined && params.get.nonEmpty) genArrayValueParams(array.params.asInstanceOf[Option[List[TmplSetAttribute]]])
  //    else "[]"
  //  }
  //
  //  override def genFor(forLoop: TmplFor): String = {
  //    val str = new StringBuilder
  //    str ++= "for("
  //    str ++= "var " ++= forLoop.variable.toString
  //    str ++= " " ++= genForType(forLoop.forType) ++= " "
  //    str ++= genOperation(forLoop.cond)
  //    str ++= ") "
  //    str ++= genExprContent(forLoop.content) ++= "\n"
  //    str.toString()
  //  }

  //   def genAnonFunc(anonFunc: TmplAnonFunc): String = {
  //    val str = new StringBuilder
  //    str ++= genFuncCurry(anonFunc.currying)
  //    if (anonFunc.content.isInstanceOf[TmplExpression[_]]) str ++= " => "
  //    str ++= genExprContent(anonFunc.content)
  //    str.toString()
  //  }
  //
  //   def genAttribute(attr: TmplAttribute): String = {
  //    val str = new StringBuilder
  //    if (attr.attr.isDefined) str ++= attr.attr.get.toString ++= ": "
  //    str ++= genOperation(attr.value)
  //    str.toString()
  //  }
  //
  //   def genEntityValue(entity: TmplEntityValue): String = {
  //    val str = new StringBuilder
  //    str ++= " new " ++= entity.name.getOrElse("").toString
  //    str ++= "("
  //    if (entity.params.isDefined) {
  //      str ++= entity.params.get.map(param => genAttribute(param.asInstanceOf[TmplAttribute])).mkString(",\n")
  //    }
  //    str ++= ")"
  //    if (entity.attrs.isDefined) {
  //      str ++= "{"
  //      entity.attrs.get.map(attr => genAttribute(attr.asInstanceOf[TmplAttribute])).mkString(",\n")
  //      str ++= "}"
  //    }
  //    str.toString()
  //  }

  def commaRequired(): Boolean = true

  def includeKeyword(): String = "import"

  def packageKeyword(): String = ""

  def defaultImplProps(): String = "class"

  def defaultFuncProps(): String = ""

  def genDefaultVarKeyword(): String = "var"
}