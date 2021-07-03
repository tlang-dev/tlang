package dev.tlang.tlang.generator.langs.java

import dev.tlang.tlang.ast.common.operation.Operator
import dev.tlang.tlang.ast.tmpl._
import dev.tlang.tlang.ast.tmpl.call._
import dev.tlang.tlang.ast.tmpl.condition.TmplOperation
import dev.tlang.tlang.ast.tmpl.func.{TmplFunc, TmplFuncCurry}
import dev.tlang.tlang.ast.tmpl.loop.ForType.ForType
import dev.tlang.tlang.ast.tmpl.loop.{TmplDoWhile, TmplFor, TmplWhile}
import dev.tlang.tlang.ast.tmpl.primitive._
import dev.tlang.tlang.generator.{CodeGenerator, Seq}

class NewJavaGenerator extends CodeGenerator {
  override def generate(tmpl: TmplBlock): String = {
    ""
  }
}

object NewJavaGenerator {

  def genBlock(tmpl: TmplBlock): Seq = {
    val root = Seq()
    root += genPackage(tmpl.pkg)
    root ++= genIncludes(tmpl.uses)
    tmpl.content.foreach(root ++= genContents(_))
    root
  }

  def genPackage(pkg: Option[TmplPkg]): Seq = {
    val seq = Seq()
    pkg.foreach(p => seq -> "package" += " " += mkSeq(p.parts, ".") += ";")
    seq
  }

  def genIncludes(uses: Option[List[TmplUse]]): Iterable[Seq] = {
    val str: Array[Seq] = Array.ofDim[Seq](uses.fold(0)(_.size))
    uses.foreach(_.zipWithIndex.foreach(use => {
      val seq = Seq("import")
      seq += " " += mkSeq(use._1.parts, ".") += ";"
      str(use._2) = seq
    }))
    str
  }

  def genContents(impls: List[TmplNode[_]]): Iterable[Seq] = {
    val str: Array[Seq] = Array.ofDim[Seq](impls.size)
    impls.zipWithIndex.foreach(impl => str(impl._2) = genContent(impl._1))
    str
  }

  def genContent(impl: TmplNode[_]): Seq = {
    impl match {
      case func: TmplFunc => genFunc(func)
      case expr: TmplExpression[_] => genExpression(expr)
      case impl: TmplImpl => genImpl(impl)
    }
  }

  def genImpl(impl: TmplImpl): Seq = {
    val str = Seq()
    var cur = str
    cur += genAnnotations(impl.annots)
    cur = impl.props.fold(Seq.add(cur, "public", " ", "class"))(prop => cur += genProps(prop)) += " " += impl.name.toString
    if (impl.fors.isDefined) {
      cur = cur += " " += impl.fors.get.props.fold(Seq("extends"))(genProps(_)) += " "
      cur = cur += mkSeq(impl.fors.get.types.map(implFor => genType(implFor)), ",")
    }
    if (impl.withs.isDefined) {
      cur = cur += " " += impl.withs.get.props.fold(Seq("implements"))(genProps(_)) += " "
      cur = cur += mkSeq(impl.fors.get.types.map(implFor => genType(implFor)), ",")
    }
    cur += "{"
    if (impl.content.isDefined) cur ++= genContents(impl.content.get)
    cur += "}"
    str
  }

  def genFunc(func: TmplFunc): Seq = {
    val str = Seq()
    str += genAnnotations(func.annots)
    str += func.props.fold(Seq("public"))(prop => genProps(prop)) += " "
    str += func.ret.fold(Seq("void"))(ret => genType(ret.head)) += " "
    str += func.name.toString
    str += genCurrying(func.curries)
    str += func.postPros.fold(Seq())(prop => genProps(prop) += " ")
    if (func.content.isDefined) str += genExprBlock(func.content.get)
    else str += ";"
    str
  }

  def genCurrying(curries: Option[List[TmplFuncCurry]]): Seq = {
    if (curries.isDefined) mkSeq(curries.get.map(genFuncCurry), "")
    else Seq("()")
  }

  def genFuncCurry(curry: TmplFuncCurry): Seq = {
    val str = Seq()
    str += "("
    curry.params.foreach(params => str += mkSeq(params.map(genParam), ","))
    str += ")"
    str
  }

  def genAnnotations(annots: Option[List[TmplAnnotation]], sep: String = ""): Seq = {
    if (annots.isDefined) {
      val str = Seq()
      annots.get.foreach(annot => {
        str += "@" += annot.name.toString
        if (annot.values.isDefined) {
          str += "("
          str += mkSeqFromSeq(annot.values.get.map(value => Seq.addTo(Seq(), value.name.toString, "=" ) += genPrimitive(value.value)), ",")
          str += ")"
        }
        str += sep
      })
      str
    }
    else Seq()
  }

  def genOptionalProps(props: Option[TmplProp], addSpace: Boolean = false): Seq = {
    if (props.isDefined) genProps(props.get, addSpace)
    else Seq("")
  }

  def genProps(props: TmplProp, addSpace: Boolean = false): Seq = {
    val seq = Seq()
    seq += mkSeq(props.props, " ")
    if (addSpace) seq += " "
    seq
  }

  def genParam(param: TmplParam): String = {
    val str = Seq()
    str += genAnnotations(param.annots, sep = " ")
    if (param.`type`.isDefined) str += genType(param.`type`.get) += " "
    str += param.name.toString
    str.toString()
  }

  def genType(`type`: TmplType): Seq = {
    val str = Seq()
    str += `type`.name.toString
    str += genGeneric(`type`.generic)
    if (`type`.isArray) str += "[]"
    str
  }

  def genGeneric(gen: Option[TmplGeneric]): Seq = {
    if (gen.isDefined) {
      val str = Seq()
      str += "<" += mkSeqFromSeq(gen.get.types.map(genType), ",") += ">"
      str
    } else Seq()
  }

  def genExprContent(content: TmplExprContent[_], endOfStatement: Boolean = false, newLine: Boolean = false): Seq = {
    content match {
      case block: TmplExprBlock => genExprBlock(block)
      case expression: TmplExpression[_] => genExpression(expression, endOfStatement)
    }
  }

  def genExprBlock(block: TmplExprBlock): Seq = {
    val str = Seq()
    str += "{" += mkSeqFromSeq(block.exprs.map(b => genExpression(b, endOfStatement = true)), "") += "}"
    str
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
    var ret = statement
    if (endOfStatement) ret = ret += ";"
    statement
  }

  def genAffect(affect: TmplAffect): Seq = {
    val str = Seq()
    str += genTmplCallObj(affect.variable) += "=" += genOperation(affect.value)
    str
  }

  def genReturn(ret: TmplReturn): Seq = {
    val str = Seq()
    str += "return " += genOperation(ret.operation)
    str
  }

  def genAnonFunc(anonFunc: TmplAnonFunc): Seq = {
    val str = Seq()
    str += genFuncCurry(anonFunc.currying)
    str += genExprContent(anonFunc.content)
    str
  }

  def genIf(ifStmt: TmplIf): Seq = {
    val str = Seq()
    str += "if(" += genOperation(ifStmt.cond) += ")"
    str += genExprContent(ifStmt.content, ifStmt.content.isInstanceOf[TmplExpression[_]])
    if (ifStmt.elseBlock.isDefined) ifStmt.elseBlock.get match {
      case Left(elseBlock) => str += " else " += genExprContent(elseBlock, elseBlock.isInstanceOf[TmplExpression[_]])
      case Right(ifBlock) => str += " else " += genIf(ifBlock)
    }
    str
  }

  def genFor(forLoop: TmplFor): Seq = {
    val str = Seq()
    str += "for("
    str += forLoop.variable.toString
    str += " " += genForType(forLoop.forType) += " "
    str += genOperation(forLoop.cond)
    str += ")"
    str += genExprContent(forLoop.content)
    str
  }

  def genForType(forType: ForType): Seq = forType match {
    case dev.tlang.tlang.ast.tmpl.loop.ForType.IN => Seq("in")
    case dev.tlang.tlang.ast.tmpl.loop.ForType.TO => Seq("to")
    case dev.tlang.tlang.ast.tmpl.loop.ForType.UNTIL => Seq("until")
  }

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
    val str = Seq()
    callObj.props.foreach(prop => str += genProps(prop, addSpace = true))
    str += mkSeqFromSeq(callObj.calls.map(genCallObjType), ".")
    str
  }

  def genCallObjType(objType: TmplCallObjType[_]): Seq = {
    objType match {
      case array: TmplCallArray => genCallArray(array)
      case func: TmplCallFunc => genCallFunc(func)
      case variable: TmplCallVar => genCallVar(variable)
    }
  }

  def genCallArray(array: TmplCallArray): Seq = {
    val str = Seq()
    str += array.name.toString += "[" += genOperation(array.elem) += "]"
    str
  }

  def genCallFunc(func: TmplCallFunc): Seq = {
    val str = Seq()
    str += func.name.toString
    if (func.currying.isDefined) {
      func.currying.foreach(_.foreach(curry => {
        str += "("
        curry.params.foreach(param => str += mkSeq(param.map(attr => attr.asInstanceOf[TmplSetAttribute].name.fold(Seq())(Seq() -> _.toString += ":") += genOperation(attr.asInstanceOf[TmplSetAttribute].value)), ","))
        str += ")"
      }))
    } else str += "()"
    str
  }

  def genCallVar(variable: TmplCallVar): Seq = Seq(variable.name.toString)

  def genVar(variable: TmplVar): Seq = {
    val str = Seq()
    str += genAnnotations(variable.annots)
    variable.props.foreach(prop => str += genProps(prop, addSpace = true))
    if (variable.`type`.isDefined) str += genType(variable.`type`.get) += " "
    else str += "var" += " "
    str += variable.name.toString
    variable.value.foreach(str += "=" += genOperation(_))
    str += ";"
    str
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

  def genPrimitive(primitive: TmplPrimitiveValue[_]): Seq = {
    primitive match {
      case string: TmplStringValue => genStringValue(string)
      case long: TmplLongValue => genLongValue(long)
      case double: TmplDoubleValue => genDoubleValue(double)
      case bool: TmplBoolValue => genBoolValue(bool)
      case array: TmplArrayValue => genArrayValue(array)
      case entity: TmplEntityValue => genEntityValue(entity)
    }
  }

  def genEntityValue(entity: TmplEntityValue): Seq = {
    Seq()
  }

  def genAttribute(attr: TmplAttribute): Seq = {
    val str = Seq()
    if (attr.`type`.isDefined) str += attr.`type`.get.toString += ":"
    str += genOperation(attr.value)
    str
  }

  def genArrayValue(array: TmplArrayValue): Seq = {
    val str = Seq()
    str += " new "
    array.`type`.foreach(t => str += genType(t))
    str += "[]"
    str += genArrayValueParams(array.params.asInstanceOf[Option[List[TmplSetAttribute]]])
    str
  }

  def genArrayValueParams(params: Option[List[TmplSetAttribute]]): Seq = {
    val str = Seq()
    if (params.isDefined) {
      str += "{"
      str += mkSeq(params.get.map(genSetAttribute), ",")
      str += "}"
    }
    str
  }

  def genStringValue(string: TmplStringValue): Seq = Seq("\"") += string.value.toString += "\""

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
      case str: TmplStringID => Seq("\"") += str.toString += "\""
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
