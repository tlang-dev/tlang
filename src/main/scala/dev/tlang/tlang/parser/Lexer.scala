package dev.tlang.tlang.parser

case class Lexer(tokens: List[(Token, Lexer)], endToken: Option[Token]=None)
