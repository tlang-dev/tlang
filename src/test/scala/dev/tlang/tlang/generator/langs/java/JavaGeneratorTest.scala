package dev.tlang.tlang.generator.langs.java

import dev.tlang.tlang.ast.common.operation.Operator
import dev.tlang.tlang.ast.tmpl._
import dev.tlang.tlang.tmpl.lang.ast.call.{TmplCallFunc, TmplCallObj, TmplCallVar}
import dev.tlang.tlang.tmpl.lang.ast.condition.TmplOperation
import dev.tlang.tlang.tmpl.lang.ast.func.TmplFunc
import dev.tlang.tlang.tmpl.lang.ast.loop.{TmplDoWhile, TmplFor, TmplWhile}
import dev.tlang.tlang.tmpl.lang.ast.primitive.{TmplLongValue, TmplStringValue}
import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildTmplBlock
import dev.tlang.tlang.{TLangLexer, TLang}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

class JavaGeneratorTest extends AnyFunSuite {

  val fakeContext: ContextResource = ContextResource("", "", "", "")

 /* test("Package") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[java] myTmpl {
        |pkg my.package
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildTmplBlock.build(fakeContext, parser.tmplBlock())
    assert(new JavaGenerator().generate(impl).contains("package my.package;"))
  }

  test("Uses") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[java] myTmpl {
        |use my.package1
        |use my.package2
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildTmplBlock.build(fakeContext, parser.tmplBlock())
    assert(new JavaGenerator().generate(impl).contains("import my.package1;"))
    assert(new JavaGenerator().generate(impl).contains("import my.package2;"))
  }

  test("Simple class") {
    val impl = TmplImpl(None, None, None, TmplStringID(None, "MyClass"), None, None)
    val res = JavaGenerator.genImpl(impl)
    assert(res.contains("public class MyClass {"))
  }

  test("Simple interface") {
    val impl = TmplImpl(None, None, Some(TmplProp(None, List(TmplStringID(None, "public"), TmplStringID(None, "interface")))), TmplStringID(None, "MyInterface"), None, None)
    val res = JavaGenerator.genContent(impl)
    assert(res.contains("public interface MyInterface {"))
  }

  test("Annotation before impl") {
    val impl = TmplImpl(None, Some(List(
      TmplAnnotation(None, TmplStringID(None, "MyAnnot1"), None),
      TmplAnnotation(None, TmplStringID(None, "MyAnnot2"), Some(List(TmplAnnotationParam(None, TmplStringID(None, "param1"), TmplStringValue(None, TmplStringID(None, "val1"))), TmplAnnotationParam(None, TmplStringID(None, "param2"), TmplStringValue(None, TmplStringID(None, "val2")))))))), None, TmplStringID(None, "MyClass"), None, None)
    val res = JavaGenerator.genImpl(impl)
    assert(res.contains("@MyAnnot1\n" +
      "@MyAnnot2(param1 = \"val1\", param2 = \"val2\")\n" +
      "public class MyClass {"))
  }

  test("Annotation before func") {
    val impl = TmplFunc(None, Some(List(
      TmplAnnotation(None, TmplStringID(None, "MyAnnot1"), None),
      TmplAnnotation(None, TmplStringID(None, "MyAnnot2"), Some(List(TmplAnnotationParam(None, TmplStringID(None, "param1"), TmplStringValue(None, TmplStringID(None, "val1"))), TmplAnnotationParam(None, TmplStringID(None, "param2"), TmplStringValue(None, TmplStringID(None, "val2")))))))), None, TmplStringID(None, "myFunc"), None, Some(TmplExprBlock(None, List())), None)
    val res = JavaGenerator.genExpression(impl)
    assert(res.contains("@MyAnnot1\n" +
      "@MyAnnot2(param1 = \"val1\", param2 = \"val2\")\n" +
      "public void myFunc() {"))
  }

  test("New variable in TmplBlock") {
    val block = TmplBlock(None, "test", "java", None, None, None, specialised = false, Some(List(TmplVar(None, None, Some(TmplProp(None, List(TmplStringID(None, "private"), TmplStringID(None, "final")))), TmplStringID(None, "myVar"), Some(TmplType(None, TmplStringID(None, "List"), Some(TmplGeneric(None, List(TmplType(None, TmplStringID(None, "String"), None, isArray = true)))))), Some(TmplOperation(None, Right(TmplLongValue(None, 5))))))))
    val res = new JavaGenerator().generate(block)
    assert(res.contains("private final List<String[]> myVar = 5;"))
  }

  test("Func with parameters and returns") {
    val content = TmplFunc(None, None, None, TmplStringID(None, "myFunc"), Some(List(TmplFuncCurry(None, Some(List(TmplParam(None, None, TmplStringID(None, "myDouble"), Some(TmplType(None, TmplStringID(None, "Double")))), TmplParam(None, None, TmplStringID(None, "myString"), Some(TmplType(None, TmplStringID(None, "String"))))))))),
      Some(TmplExprBlock(None, List(TmplCallObj(None, None, List(TmplCallVar(None, TmplStringID(None, "myVar"))))))), Some(List(TmplType(None, TmplStringID(None, "boolean")))))
    val res = JavaGenerator.genContent(content)
    assert(res.contains("public boolean myFunc(Double myDouble, String myString) {\n" +
      "myVar;\n" +
      "}"))
  }

  test("If with one expression") {
    val cond = TmplOperation(None,  Right(TmplLongValue(None, 1)), Some((Operator.EQUAL, TmplOperation(None, Right(TmplLongValue(None, 1))))))
    val ifStmt = TmplIf(None, cond, TmplCallObj(None, None, List(TmplCallVar(None, TmplStringID(None, "myVar")))), None)
    val res = JavaGenerator.genExpression(ifStmt, endOfStatement = true)
    assert(res.contains("if(1 == 1) myVar;"))
  }

  test("If with expression and else expression") {
    val cond = TmplOperation(None,  Right(TmplLongValue(None, 1)), Some((Operator.LESSER, TmplOperation(None, Right(TmplLongValue(None, 1))))))
    val ifStmt = TmplIf(None, cond, TmplCallObj(None, None, List(TmplCallVar(None, TmplStringID(None, "myVar")))), Some(Left(TmplCallObj(None, None, List(TmplCallFunc(None, TmplStringID(None, "myFunc"), None))))))
    val res = JavaGenerator.genExpression(ifStmt)
    assert(res.contains("if(1 < 1) myVar; else myFunc();"))
  }

  test("If with expression block and else block") {
    val cond = TmplOperation(None, Right( TmplLongValue(None, 1)), Some((Operator.GREATER, TmplOperation(None, Right(TmplLongValue(None, 1))))))
    val ifStmt = TmplIf(None, cond, TmplExprBlock(None, List(TmplCallObj(None, None, List(TmplCallVar(None, TmplStringID(None, "myVar")))))), Some(Left(TmplExprBlock(None, List(TmplCallObj(None, None, List(TmplCallFunc(None, TmplStringID(None, "myFunc"), None))))))))
    val res = JavaGenerator.genExpression(ifStmt)
    assert(res.contains("if(1 > 1) {\nmyVar;\n} else {\nmyFunc();\n}"))
  }

  test("If with expression and else if expression") {
    val cond = TmplOperation(None, Right(TmplLongValue(None, 1)), Some((Operator.GREATER_OR_EQUAL, TmplOperation(None, Right(TmplLongValue(None, 1))))))
    val cond2 = TmplOperation(None, Right(TmplLongValue(None, 1)), Some((Operator.LESSER_OR_EQUAL, TmplOperation(None, Right(TmplLongValue(None, 1))))))
    val ifStmt = TmplIf(None, cond, TmplCallObj(None, None, List(TmplCallVar(None, TmplStringID(None, "myVar")))), Some(Right(TmplIf(None, cond2, TmplCallObj(None, None, List(TmplCallFunc(None, TmplStringID(None, "myFunc"), None))), None))))
    val res = JavaGenerator.genExpression(ifStmt)
    assert(res.contains("if(1 >= 1) myVar; else if(1 <= 1) myFunc();"))
  }

  test("While") {
    val cond = TmplOperation(None, Right(TmplLongValue(None, 1)), Some((Operator.NOT_EQUAL, TmplOperation(None, Right(TmplLongValue(None, 1))))))
    val whileLoop = TmplWhile(None, cond, TmplCallObj(None, None, List(TmplCallFunc(None, TmplStringID(None, "myFunc"), None))))
    val res = JavaGenerator.genExpression(whileLoop)
    assert("while(1 != 1) myFunc();\n" == res)
  }

  test("Do while") {
    val cond = TmplOperation(None, Right(TmplLongValue(None, 1)), Some((Operator.NOT_EQUAL, TmplOperation(None, Right(TmplLongValue(None, 1))))))
    val whileLoop = TmplDoWhile(None, TmplCallObj(None, None,  List(TmplCallFunc(None, TmplStringID(None, "myFunc"), None))), cond)
    val res = JavaGenerator.genExpression(whileLoop)
    assert("do myFunc(); while(1 != 1);\n" == res)
  }

  /*test("For") {
    val forLoop = TmplFor(None, TmplExprBlock(None, List(TmplCallObj(None, None, List(TmplCallFunc(None, TmplStringID(None, "myFunc"), None))))))
    val res = JavaGenerator.genExpression(forLoop)
    assert("for() {\nmyFunc();\n}\n" == res)
  }*/

  test("Condition block with OR") {
    val cond = TmplOperation(None, Right(TmplLongValue(None, 1)), Some(Operator.EQUAL, TmplOperation(None, Right(TmplLongValue(None, 1)), Some((Operator.OR, TmplOperation(None, Left(TmplOperation(None, Right(TmplLongValue(None, 1)), Some((Operator.NOT_EQUAL, TmplOperation(None, Right(TmplLongValue(None, 1)))))))))))))
    val res = JavaGenerator.genOperation(cond)
    assert("1 == 1 || (1 != 1)" == res)
  }

  test("Condition block with AND") {
    val cond = TmplOperation(None, Right(TmplLongValue(None, 1)), Some(Operator.EQUAL, TmplOperation(None, Right(TmplLongValue(None, 1)), Some((Operator.AND, TmplOperation(None, Left(TmplOperation(None, Right(TmplLongValue(None, 1)), Some((Operator.NOT_EQUAL, TmplOperation(None, Right(TmplLongValue(None, 1)))))))))))))
    val res = JavaGenerator.genOperation(cond)
    assert("1 == 1 && (1 != 1)" == res)
  }*/

}
