package dev.tlang.tlang.generator.langs.dart

import dev.tlang.tlang.ast.common.operation.Operator
import dev.tlang.tlang.tmpl._
import dev.tlang.tlang.tmpl.lang.ast.call._
import dev.tlang.tlang.tmpl.lang.ast.condition.TmplOperation
import dev.tlang.tlang.tmpl.lang.ast.func.{TmplAnnotationParam, TmplAnonFunc, TmplFunc}
import dev.tlang.tlang.tmpl.lang.ast.loop.ForType.ForType
import dev.tlang.tlang.tmpl.lang.ast.loop.{TmplDoWhile, TmplFor, TmplWhile}
import dev.tlang.tlang.tmpl.lang.ast.primitive._
import dev.tlang.tlang.generator.formatter.Formatter
import dev.tlang.tlang.generator.langs.kotlin.KotlinGenerator.{mkSeq, mkSeqFromSeq}
import dev.tlang.tlang.generator.{CodeGenerator, Seq, SeqBuilder}
import dev.tlang.tlang.tmpl.lang.ast.{TmplAffect, TmplAnnotation, TmplAttribute, LangBlock, TmplExprBlock, TmplExprContent, TmplExpression, TmplID, TmplIf, TmplImpl, TmplInclude, TmplNode, TmplParam, TmplPkg, TmplProp, TmplReturn, TmplSetAttribute, TmplSimpleValueType, TmplStringID, TmplUse, TmplValueType, TmplVar}

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


  def genPackage(pkg: Option[TmplPkg]): Seq = Seq()

  def genIncludes(uses: Option[List[TmplUse]]): Seq = {
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
      case func: TmplFunc => genFunc(func)
      case expr: TmplExpression[_] => genExpression(expr, addEndOfStatement)
      case impl: TmplImpl => genImpl(impl)
      case exprBlock: TmplExprBlock => genExprBlock(exprBlock, addEndOfStatement)
      case tmplBlock: LangBlock => genBlock(tmplBlock)
    }
  }

  def genImpl(impl: TmplImpl): Seq = {
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

  def genFunc(func: TmplFunc): Seq = {
    val str = new SeqBuilder()
    str.setBlockName("func")
    str ++= genAnnotations(func.annots)
    //    str += func.props.fold(Seq("public"))(prop => genProps(prop)) += " "
    func.props.foreach(prop => str ++= genProps(prop, addSpace = true))

    val header = new SeqBuilder()
//    func.ret.foreach(ret => header += SeqBuilder.build(genType(ret.head), Seq(" ")))
    header += (Seq(func.name.toString))
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

  def genAnnotations(annots: Option[List[TmplAnnotation]], sep: String = ""): Seq = {
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

  private def genAnnotValue(value: TmplAnnotationParam): Seq = {
    val seq = Seq(blockName = "annotValue")
    if (value.name.isDefined) {
      seq += value.name.get.toString
      seq += "="
    }
    seq += genValueType(value.value)
    seq
  }

  def genOptionalProps(props: Option[TmplProp], addSpace: Boolean = false): Seq = {
    if (props.isDefined) genProps(props.get, addSpace)
    else Seq("")
  }

  def genProps(props: TmplProp, addSpace: Boolean = false): Seq = {
    val seq = Seq(blockName = "props")
    seq += mkSeq(props.props, " ")
    if (addSpace) seq += " "
    seq
  }

  def genParam(param: TmplParam): Seq = {
    val str = new SeqBuilder()
    str += genAnnotations(param.annots, sep = " ")
//    if (param.`type`.isDefined) str += genType(param.`type`.get) += " "
    str += param.name.toString
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

  def genExprContent(content: TmplExprContent[_], endOfStatement: Boolean = false, newLine: Boolean = false): Seq = {
    content match {
      case block: TmplExprBlock => genExprBlock(block)
      case expression: TmplExpression[_] => genExpression(expression, endOfStatement)
    }
  }

  def genExprBlock(block: TmplExprBlock, endOfStatement: Boolean = false): Seq = {
    val str = new SeqBuilder()
    str.setBlockName("exprBlock")
    str.head(Seq("{"))
    block.exprs.foreach(b => str ++= genContent(b, endOfStatement))
    str.bottom(Seq("}"))
    str.build()
  }

  def genExpression(expr: TmplExpression[_], endOfStatement: Boolean = false): Seq = {
    expr match {
      case call: TmplCallObj => genEndOfStatement(genTmplCallObj(call), endOfStatement)
      case func: TmplFunc => genFunc(func)
      case valueType: TmplValueType[_] => genValueType(valueType)
      case variable: TmplVar => genEndOfStatement(genVar(variable), endOfStatement)
      case ifStmt: TmplIf => genIf(ifStmt)
      case forLoop: TmplFor => genFor(forLoop)
      case whileLoop: TmplWhile => genWhile(whileLoop)
      case doWhile: TmplDoWhile => genDoWhile(doWhile)
      //      case incl: TmplInclude => genInclude(incl)
      case ret: TmplReturn => genEndOfStatement(genReturn(ret), endOfStatement)
      case affect: TmplAffect => genEndOfStatement(genAffect(affect), endOfStatement)
      case anonFunc: TmplAnonFunc => genAnonFunc(anonFunc)
    }
  }

  def genEndOfStatement(statement: String, endOfStatement: Boolean): Seq = {
    genEndOfStatement(Seq(statement), endOfStatement)
  }

  def genEndOfStatement(statement: Seq, endOfStatement: Boolean): Seq = {
    if (endOfStatement) SeqBuilder.build(statement, Seq(DartFormatter.END_OF_STATEMENT))
    else statement
  }

  def genAffect(affect: TmplAffect): Seq = {
    val str = new SeqBuilder()
    str += genTmplCallObj(affect.variable) += "=" += genOperation(affect.value)
    str.build()
  }

  def genReturn(ret: TmplReturn): Seq = {
    val str = new SeqBuilder()
    str.setBlockName("return")
    str += "return"
    str += " "
    str += genOperation(ret.operation)
    str.build()
  }

  def genAnonFunc(anonFunc: TmplAnonFunc): Seq = {
    val str = new SeqBuilder()
    str.setBlockName("anonFunc")
//    str ++= genFuncCurry(anonFunc.currying)
    str ++= genExprContent(anonFunc.content)
    str.build()
  }

  def genIf(ifStmt: TmplIf): Seq = {
    val str = new SeqBuilder()
    str.setBlockName("if")
    str.head(SeqBuilder.build(Seq("if"), Seq("("), genOperation(ifStmt.cond), Seq(")")))
    str += genExprContent(ifStmt.content, ifStmt.content.isInstanceOf[TmplExpression[_]])
    if (ifStmt.elseBlock.isDefined) ifStmt.elseBlock.get match {
      case Left(elseBlock) => str += " else " += genExprContent(elseBlock, elseBlock.isInstanceOf[TmplExpression[_]])
      case Right(ifBlock) => str += " else " += genIf(ifBlock)
    }
    str.build()
  }

  def genFor(forLoop: TmplFor): Seq = {
    val str = new SeqBuilder()
    str.setBlockName("for")
    str.setSeq("for")
    str += "("
    str += forLoop.variable.toString
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

  def genWhile(whileLoop: TmplWhile): Seq = {
    val str = Seq()
    str += "while(" += genOperation(whileLoop.cond) += ") "
    str += genExprContent(whileLoop.content, whileLoop.content.isInstanceOf[TmplExpression[_]])
    str
  }

  def genDoWhile(doWhile: TmplDoWhile): Seq = {
    val str = Seq()
    str += "do " += genExprContent(doWhile.content, doWhile.content.isInstanceOf[TmplExpression[_]])
    str += " while(" += genOperation(doWhile.cond) += ");"
    str
  }

  def genTmplCallObj(callObj: TmplCallObj): Seq = {
    val str = new SeqBuilder()
    str.setBlockName("callObject")
    callObj.props.foreach(prop => str += genProps(prop, addSpace = true))
    str += genCallObjType(callObj.firstCall)
    callObj.calls.foreach(link => str += genCallLink(link))
    str.build()
  }

  def genCallLink(objLink: TmplCallObjectLink): Seq = {
    val str = new SeqBuilder()
    str += objLink.link
    str += genCallObjType(objLink.call)
    str.build()
  }

  def genCallObjType(objType: TmplCallObjType[_]): Seq = {
    objType match {
      case array: TmplCallArray => genCallArray(array)
      case func: TmplCallFunc => genCallFunc(func)
      case variable: TmplCallVar => genCallVar(variable)
    }
  }

  def genCallArray(array: TmplCallArray): Seq = {
    val str = new SeqBuilder()
    str += array.name.toString += "[" += genOperation(array.elem) += "]"
    str.build()
  }

  def genCallFunc(func: TmplCallFunc): Seq = {
    val str = new SeqBuilder()
    str.setBlockName("callFunc")
    str += func.name.toString
    if (func.currying.isDefined) {
      func.currying.foreach(_.foreach(curry => {
        str += Seq("(")
//        curry.params.foreach(param =>  str += mkSeq(param.map(attr => SeqBuilder.build(attr.asInstanceOf[TmplSetAttribute].name.fold(Seq())( p=>SeqBuilder.build(Seq(p.toString) ,Seq(":"))), genOperation(attr.asInstanceOf[TmplSetAttribute].value))), ","))
        str += Seq(")")
      }))
    } else str += "()"
    str.build()
  }

  def genCallVar(variable: TmplCallVar): Seq = Seq(variable.name.toString)

  def genVar(variable: TmplVar): Seq = {
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

  def genPropsForVar(props: TmplProp, variable: TmplVar, addSpace: Boolean = false): Seq = {
    val seq = new SeqBuilder()
    if (props.props.nonEmpty && props.props.head.toString.equals("lateinit")) {
      seq += "lateinit var"
    } else {
      seq += mkSeq(props.props, " ")
    }
    if (addSpace) seq += " "
    seq.build()
  }

  def genValueType(valueType: TmplValueType[_]): Seq = {
    valueType match {
      case call: TmplCallObj => genTmplCallObj(call)
      case primitive: TmplPrimitiveValue[_] => genPrimitive(primitive)
    }
  }

  def genSimpleValueType(valueType: TmplSimpleValueType[_]): Seq = {
    valueType match {
      case call: TmplCallObj => genTmplCallObj(call)
      case value: TmplPrimitiveValue[_] => genPrimitive(value)
    }
  }

  def genOperation(block: TmplOperation): Seq = {
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

  def genPrimitive(primitive: TmplPrimitiveValue[_]): Seq = {
    primitive match {
      case string: TmplStringValue => genStringValue(string)
      case string: TmplTextValue => genTextValue(string)
      case long: TmplLongValue => genLongValue(long)
      case double: TmplDoubleValue => genDoubleValue(double)
      case bool: TmplBoolValue => genBoolValue(bool)
      case array: TmplArrayValue => genArrayValue(array)
      case entity: TmplEntityValue => genEntityValue(entity)
    }
  }

  def genEntityValue(entity: TmplEntityValue): Seq = {
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
        case operation: TmplOperation => str += genOperation(operation)
        case attribute: TmplAttribute => str += genAttribute(attribute)
        case include: TmplInclude =>
      }
    })
    str
  }

  def genAttribute(attr: TmplAttribute): Seq = {
    val str = Seq()
    if (attr.`type`.isDefined) str += attr.`type`.get.toString += ":"
    str += genOperation(attr.value)
    str
  }

  def genArrayValue(array: TmplArrayValue): Seq = {
    val str = Seq()
//    array.`type`.foreach(t => str += genType(t))
    str += "["
    str += genArrayValueParams(array.params.asInstanceOf[Option[List[TmplSetAttribute]]])
    str += "]"
    str
  }

  def genArrayValueParams(params: Option[List[TmplSetAttribute]]): Seq = {
    val str = Seq()
    if (params.isDefined) {
      //      str += "{"
      str += mkSeq(params.get.map(genSetAttribute), ",")
      //      str += "}"
    }
    str
  }

  def genStringValue(string: TmplStringValue): Seq = Seq.build("\"", string.value.toString, "\"")

  def genTextValue(string: TmplTextValue): Seq = Seq.build("\"", string.value.toString, "\"")

  def genLongValue(long: TmplLongValue): Seq = Seq(long.value.toString)

  def genDoubleValue(double: TmplDoubleValue): Seq = Seq(double.value.toString)

  def genBoolValue(bool: TmplBoolValue): Seq = Seq(if (bool.value) "true" else "false")

  def genSetAttribute(attr: TmplSetAttribute): Seq = {
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