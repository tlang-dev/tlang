package io.sorne.tlang
import org.antlr.v4.runtime.misc.Interval
import org.antlr.v4.runtime.tree.{ErrorNode, ParseTree, RuleNode, TerminalNode}

class TLangInterpreterVisitor extends TLangVisitor[String] {
  /**
   * Visit a parse tree produced by {@link TLangParser#domainmodel}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  override def visitDomainmodel(ctx: TLangParser.DomainmodelContext): String = {
    println("ICICIC")
    "toto"
  }

  override def visit(tree: ParseTree): String = {
    println("LALALALA")
    //tree.
    ""
  }

  override def visitChildren(node: RuleNode): String = {
    ""
  }

  override def visitTerminal(node: TerminalNode): String = {
    ""
  }

  override def visitErrorNode(node: ErrorNode): String = {
    ""
  }

  /**
   * Visit a parse tree produced by {@link TLangParser#lang}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  override def visitLang(ctx: TLangParser.LangContext): String = {
//    val a: Int = ctx.start.getStartIndex
//    val b: Int = ctx.stop.getStopIndex
//    val interval = new Interval(a, b)
//    ctx.start.getInputStream.getText(interval)
//    ctx.name.getText
    println("ICICICICICIC")
    ctx.children.forEach(x => {println(x.getText)})
    ctx.name.getText
  }

  /**
   * Visit a parse tree produced by {@link TLangParser#file}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  override def visitFile(ctx: TLangParser.FileContext): String = {
    ctx.name.getText
  }
}
