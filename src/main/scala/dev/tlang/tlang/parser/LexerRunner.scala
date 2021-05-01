package dev.tlang.tlang.parser

import scala.collection.mutable.ListBuffer

object LexerRunner {

  def run(src: Array[Char], pos: Pos, seq: Seq, lexer: Lexer): Pos = {
    var i = pos.charNum
    var line = pos.line
    var col = pos.col
    var currentSeq = seq
    var end = false
    var added = 0
    while (!end && i < src.length) {
      findTokenInLexer(src, Pos(i, line, col), lexer) match {
        case Some(value) =>
          val newSeq = Seq(value._1.token, pos = Pos(i, line, col))
          seq.children.addOne(newSeq)
          added = added + 1
          i = value._3.charNum
          line = value._3.line
          col = value._3.col
          val newPos = run(src, Pos(i, line, col), newSeq, value._2)
          i = newPos.charNum
          line = newPos.line
          col = newPos.col
        case None =>
          lexer.tokens.find(token => token._1.token == "$ID").foreach(token => {
            findVariableToken(src, Pos(i, line, col), token._1) match {
              case Some(value) =>
                val newSeq = Seq(value._1, pos = Pos(i, line, col))
                seq.children.addOne(newSeq)
                added = added + 1
                i = value._2.charNum
                line = value._2.line
                col = value._2.col
                val newPos = run(src, Pos(i, line, col), newSeq, token._2)
                i = newPos.charNum
                line = newPos.line
                col = newPos.col
              case None =>
            }
          })
          if (lexer.endToken.isDefined) {
            if (lexer.endToken.nonEmpty && lexer.endToken.get.token == "$AFTER_ONE") {
              end = true
            } else findToken(src, Pos(i, line, col), lexer.endToken.get) match {
              case Some(value) =>
                val newSeq = Seq(lexer.endToken.get.token, pos = Pos(i, line, col))
                currentSeq.children.addOne(newSeq)
                currentSeq = newSeq
                i = value.charNum
                line = value.line
                col = value.col
                end = true
              case None =>
            }
          }
          if (!end && i < src.length) {
            val newSeq = Seq(src(i).toString, pos = Pos(i, line, col))
            currentSeq.children.addOne(newSeq)
            currentSeq = newSeq
            if (src(i) == '\n') {
              col = 0
              line = line + 1
            } else col = col + 1
            i = i + 1
          }
      }
    }
    Pos(i, line, col)
  }

  def findTokenInLexer(src: Array[Char], pos: Pos, lexer: Lexer): Option[(Token, Lexer, Pos)] = {
    var i = pos.charNum
    val max = i + Math.min(findLongestSeq(lexer), src.length - pos.charNum)
    var j = 0
    var token: Option[(Token, Lexer, Pos)] = None
    val chars = ListBuffer.empty[Char]
    while (token.isEmpty && i < max) {
      chars.addOne(src(i))
      j = 0
      while (token.isEmpty && j < lexer.tokens.length) {
        if (!lexer.tokens(j)._1.token.toSeq.startsWith("$") && chars.toSeq == lexer.tokens(j)._1.token.toSeq && isFollowedBy(src, lexer.tokens(j)._1.followedBy, i)) {
          token = Some(lexer.tokens(j)._1, lexer.tokens(j)._2, Pos(i + 1, pos.line, pos.col + lexer.tokens(j)._1.token.length))
        }
        j = j + 1
      }
      i = i + 1
    }
    token
  }

  def findToken(src: Array[Char], pos: Pos, token: Token): Option[Pos] = {
    var i = pos.charNum
    val max = i + Math.min(token.token.length, src.length - pos.charNum)
    var ret: Option[Pos] = None
    val chars = ListBuffer.empty[Char]
    while (ret.isEmpty && i < max) {
      chars.addOne(src(i))
      if (chars.toSeq == token.token.toSeq && isFollowedBy(src, token.followedBy, i)) {
        ret = Some(Pos(i + 1, pos.line, pos.col + token.token.length))
      }
      i = i + 1
    }
    ret
  }

  def findVariableToken(src: Array[Char], pos: Pos, token: Token): Option[(String, Pos)] = {
    if (token.token == "$ID") {
      var i = pos.charNum
      var over = false
      while (!over && i < src.length) {
        if (!src(i).isLetterOrDigit && src(i) != '_') {
          over = true
        } else {
          i = i + 1
        }
      }
      if (pos.charNum < i) Some((src.slice(pos.charNum, i).mkString(""), Pos(i, pos.line, pos.col + (i - pos.charNum))))
      else None
    } else None
  }

  def isFollowedBy(src: Array[Char], followedBy: Option[String], pos: Int): Boolean = {
    if (followedBy.isEmpty) true
    else if (src.length - pos >= followedBy.get.length) src.slice(pos, pos + followedBy.get.length) sameElements followedBy.get.toCharArray
    else false
  }

  def findLongestSeq(lexer: Lexer): Int = {
    var max = 0
    lexer.tokens.foreach(token => {
      val length = seqLength(token._1)
      if (length > max) {
        max = length
      }
    })
    max
  }

  def seqLength(token: Token): Int = token.token.length + (if (token.followedBy.isDefined) token.followedBy.get.length else 0)
}
