package dev.tlang.tlang.parser

case class Token(token: String, followedBy: Option[String] = None)
