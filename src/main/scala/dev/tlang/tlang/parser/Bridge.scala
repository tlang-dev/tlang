package dev.tlang.tlang.parser

case class Bridge(seq: Seq, lexer: Option[Lexer] = None, father: Option[Bridge] = None)
