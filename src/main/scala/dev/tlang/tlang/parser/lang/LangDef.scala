package dev.tlang.tlang.parser.lang

import dev.tlang.tlang.parser.{Child, Lexer, Token}

object LangDef {

  def domainModel(): Lexer = {
    Lexer(List(
      uses(),
      exposes(),
      helper(),
      model(),
      tmpl()
    ), Some(Token("$EOF", None)))
  }

  def uses(): Child = {
    Child(Token("use", None), Some(Lexer(List(
      Child(Token("$ID", None), Some(Lexer(List(Child(Token(".", None), Some(Lexer(List(Child(Token("$ID", None), None)))))), Some(Token("\\n", None))))),
    ), Some(Token("\n", None)))))
  }

  def exposes(): Child = {
    Child(Token("expose", None), Some(Lexer(List(
      Child(Token("$ID", None), Some(Lexer(List(Child(Token(".", None), Some(Lexer(List(Child(Token("$ID", None), None)))))), Some(Token("\\n", None))))),
    ), Some(Token("\n", None)))))
  }

  def helper(): Child = {
    Child(Token("helper"), Some(Lexer(List(
      Child(Token("{"),
        Some(Lexer(List(
          LangDefHelper.func()
        ))))
    ), Some(Token("}")))))
  }

  def model(): Child = {
    Child(Token("model"), Some(Lexer(List(
      Child(Token("{"),
        Some(Lexer(List())))
    ), Some(Token("}")))))
  }

  def tmpl(): Child = {
    Child(Token("tmpl"), Some(Lexer(List(
      Child(Token("{"),
        Some(Lexer(List())))
    ), Some(Token("}")))))
  }
}
