package dev.tlang.tlang.parser.lang

import dev.tlang.tlang.parser.{Child, Lexer, Token}

object LangDefHelper {

  def func(): Child = {
    Child(Token("func"), Some(Lexer(List(
      Child(Token("$ID"), Some(Lexer(List(
        Child(Token("("), Some(Lexer(List(
          param()
        ), Some(Token(")"))))),
      ), Some(Token("{"))))),
      Child(Token("{"), Some(Lexer(List(

      ), Some(Token("}")))))
    ), Some(Token("\n")))))
  }

  def param(): Child = {
    Child(Token("$ID"), Some(Lexer(List(
      Child(Token(":"), Some(Lexer(List(
        Child(Token("$ID"), Some(Lexer(List(
          Child(Token(","), Some(Lexer(List(

            Child(Token("$ID"), Some(Lexer(List(
              Child(Token(":"), Some(Lexer(List(
                Child(Token("$ID"))))))))))


          )))
          ))))
        ))))
      )))))
  }

}
