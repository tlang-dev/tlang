package io.sorne.tlang.generator.java

import io.sorne.tlang.ast.helper.{ConditionLink, ConditionType}
import io.sorne.tlang.ast.tmpl.call.{TmplCallFunc, TmplCallObj, TmplCallVar}
import io.sorne.tlang.ast.tmpl.func.{TmplFunc, TmplFuncCurry}
import io.sorne.tlang.ast.tmpl.primitive.{TmplLongValue, TmplStringValue}
import io.sorne.tlang.ast.tmpl._
import io.sorne.tlang.ast.tmpl.condition.{TmplCondition, TmplConditionBlock}
import io.sorne.tlang.ast.tmpl.loop.{TmplDoWhile, TmplFor, TmplWhile}
import io.sorne.tlang.astbuilder.BuildTmplBlock
import io.sorne.tlang.{TLangLexer, TLangParser}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

class JavaGeneratorTest extends AnyFunSuite {

  test("Package") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[java] myTmpl {
        |pkg my.package
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildTmplBlock.build(parser.tmplBlock())
    assert(new JavaGenerator().generate(impl).contains("package my.package;"))
  }

  test("Uses") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[java] myTmpl {
        |use my.package1
        |use my.package2
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildTmplBlock.build(parser.tmplBlock())
    assert(new JavaGenerator().generate(impl).contains("import my.package1;"))
    assert(new JavaGenerator().generate(impl).contains("import my.package2;"))
  }

  test("Simple class") {
    val impl = TmplImpl(None, None, "MyClass", None, None)
    val res = JavaGenerator.genImpl(impl)
    assert(res.contains("public class MyClass {"))
  }

  test("Simple interface") {
    val impl = TmplImpl(None, Some(TmplProp(List("public", "interface"))), "MyInterface", None, None)
    val res = JavaGenerator.genContent(impl)
    assert(res.contains("public interface MyInterface {"))
  }

  test("Annotation before impl") {
    val impl = TmplImpl(Some(List(
      TmplAnnotation("MyAnnot1", None),
      TmplAnnotation("MyAnnot2", Some(List(TmplAnnotationParam("param1", TmplStringValue("val1")), TmplAnnotationParam("param2", TmplStringValue("val2"))))))), None, "MyClass", None, None)
    val res = JavaGenerator.genImpl(impl)
    assert(res.contains("@MyAnnot1\n" +
      "@MyAnnot2(param1 = \"val1\", param2 = \"val2\")\n" +
      "public class MyClass {"))
  }

  test("Annotation before func") {
    val impl = TmplFunc(Some(List(
      TmplAnnotation("MyAnnot1", None),
      TmplAnnotation("MyAnnot2", Some(List(TmplAnnotationParam("param1", TmplStringValue("val1")), TmplAnnotationParam("param2", TmplStringValue("val2"))))))), None, "myFunc", None, Some(TmplExprBlock(List())), None)
    val res = JavaGenerator.genExpression(impl)
    assert(res.contains("@MyAnnot1\n" +
      "@MyAnnot2(param1 = \"val1\", param2 = \"val2\")\n" +
      "public void myFunc() {"))
  }

  test("New variable in TmplBlock") {
    val block = TmplBlock("test", "java", None, None, None, Some(List(TmplVar(None, Some(TmplProp(List("private", "final"))), "myVar", TmplType("List", Some(TmplGeneric(List(TmplType("String", None, isArray = true))))), TmplLongValue(5)))))
    val res = new JavaGenerator().generate(block)
    assert(res.contains("private final List<String[]> myVar = 5;"))
  }

  test("Func with parameters and returns") {
    val content = TmplFunc(None, None, "myFunc", Some(List(TmplFuncCurry(Some(List(TmplParam("myDouble", TmplType("Double")), TmplParam("myString", TmplType("String"))))))),
      Some(TmplExprBlock(List(TmplCallObj(List(TmplCallVar("myVar")))))), Some(List(TmplType("boolean"))))
    val res = JavaGenerator.genContent(content)
    assert(res.contains("public boolean myFunc(Double myDouble, String myString) {\n" +
      "myVar;\n" +
      "}"))
  }

  test("If with one expression") {
    val cond = TmplConditionBlock(Right(TmplCondition(TmplLongValue(1), Some(ConditionType.EQUAL), Some(TmplLongValue(1)))))
    val ifStmt = TmplIf(cond, TmplCallObj(List(TmplCallVar("myVar"))), None)
    val res = JavaGenerator.genExpression(ifStmt)
    assert(res.contains("if(1 == 1) myVar;"))
  }

  test("If with expression and else expression") {
    val cond = TmplConditionBlock(Right(TmplCondition(TmplLongValue(1), Some(ConditionType.LESSER), Some(TmplLongValue(1)))))
    val ifStmt = TmplIf(cond, TmplCallObj(List(TmplCallVar("myVar"))), Some(Left(TmplCallObj(List(TmplCallFunc("myFunc", None))))))
    val res = JavaGenerator.genExpression(ifStmt)
    assert(res.contains("if(1 < 1) myVar; else myFunc();"))
  }

  test("If with expression block and else block") {
    val cond = TmplConditionBlock(Right(TmplCondition(TmplLongValue(1), Some(ConditionType.GREATER), Some(TmplLongValue(1)))))
    val ifStmt = TmplIf(cond, TmplExprBlock(List(TmplCallObj(List(TmplCallVar("myVar"))))), Some(Left(TmplExprBlock(List(TmplCallObj(List(TmplCallFunc("myFunc", None))))))))
    val res = JavaGenerator.genExpression(ifStmt)
    assert(res.contains("if(1 > 1) {\nmyVar;\n} else {\nmyFunc();\n}"))
  }

  test("If with expression and else if expression") {
    val cond = TmplConditionBlock(Right(TmplCondition(TmplLongValue(1), Some(ConditionType.GREATER_OR_EQUAL), Some(TmplLongValue(1)))))
    val cond2 = TmplConditionBlock(Right(TmplCondition(TmplLongValue(1), Some(ConditionType.LESSER_OR_EQUAL), Some(TmplLongValue(1)))))
    val ifStmt = TmplIf(cond, TmplCallObj(List(TmplCallVar("myVar"))), Some(Right(TmplIf(cond2, TmplCallObj(List(TmplCallFunc("myFunc", None))), None))))
    val res = JavaGenerator.genExpression(ifStmt)
    assert(res.contains("if(1 >= 1) myVar; else if(1 <= 1) myFunc();"))
  }

  test("While") {
    val cond = TmplConditionBlock(Right(TmplCondition(TmplLongValue(1), Some(ConditionType.NOT_EQUAL), Some(TmplLongValue(1)))))
    val whileLoop = TmplWhile(cond, TmplCallObj(List(TmplCallFunc("myFunc", None))))
    val res = JavaGenerator.genExpression(whileLoop)
    assert("while(1 != 1) myFunc();\n" == res)
  }

  test("Do while") {
    val cond = TmplConditionBlock(Right(TmplCondition(TmplLongValue(1), Some(ConditionType.NOT_EQUAL), Some(TmplLongValue(1)))))
    val whileLoop = TmplDoWhile(TmplCallObj(List(TmplCallFunc("myFunc", None))), cond)
    val res = JavaGenerator.genExpression(whileLoop)
    assert("do myFunc(); while(1 != 1);\n" == res)
  }

  test("For") {
    val forLoop = TmplFor(TmplExprBlock(List(TmplCallObj(List(TmplCallFunc("myFunc", None))))))
    val res = JavaGenerator.genExpression(forLoop)
    assert("for() {\nmyFunc();\n}\n" == res)
  }

  test("Condition block with OR") {
    val cond = TmplConditionBlock(Left(TmplConditionBlock(Right(TmplCondition(TmplLongValue(1), Some(ConditionType.NOT_EQUAL), Some(TmplLongValue(1)))))))
    val cond2 = TmplConditionBlock(Right(TmplCondition(TmplLongValue(1), Some(ConditionType.EQUAL), Some(TmplLongValue(1)))), Some(ConditionLink.OR), Some(cond))
    val res = JavaGenerator.genValueType(cond2)
    assert("1 == 1 || (1 != 1)" == res)
  }

  test("Condition block with AND") {
    val cond = TmplConditionBlock(Left(TmplConditionBlock(Right(TmplCondition(TmplLongValue(1), Some(ConditionType.NOT_EQUAL), Some(TmplLongValue(1)))))))
    val cond2 = TmplConditionBlock(Right(TmplCondition(TmplLongValue(1), Some(ConditionType.EQUAL), Some(TmplLongValue(1)))), Some(ConditionLink.AND), Some(cond))
    val res = JavaGenerator.genValueType(cond2)
    assert("1 == 1 && (1 != 1)" == res)
  }

}
