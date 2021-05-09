package dev.tlang.tlang.parser

import scala.collection.mutable.ListBuffer

object LexerRunner {

  def run(src: Array[Char], pos: Pos, seq: Seq, lexer: Lexer): Seq = {
    if (pos.charNum >= src.length) seq
    else run(src, pos, Bridge(seq, Some(lexer)))
  }

  def run(src: Array[Char], pos: Pos, bridge: Bridge): Seq = {
    if (pos.charNum >= src.length) bridge.seq
    else managerTokenFound(findTokenInLexer(src, pos, bridge.lexer.get), src, pos, bridge, findId)
  }

  def managerTokenFound(token: Option[(Child, Pos)], src: Array[Char], pos: Pos, bridge: Bridge, nextSearch: (Array[Char], Pos, Bridge) => Seq): Seq = {
    token match {
      case Some(newToken) =>
        val newSeq = addTokenToSeq(bridge.seq, newToken._1.token.token, newToken._2)
        if (newToken._1.lexer.isDefined) run(src, newToken._2, Bridge(newSeq, Some(newToken._1.lexer.get), Some(bridge)))
        else findId(src, newToken._2, Bridge(newSeq, Some(bridge.lexer.get), Some(bridge)))
      case None => nextSearch(src, pos, bridge)
    }
  }

  def addTokenToSeq(seq: Seq, token: String, pos: Pos): Seq = {
    val newSeq = Seq(token, pos = pos)
    seq.children.addOne(newSeq)
    newSeq
  }

  def findId(src: Array[Char], pos: Pos, bridge: Bridge): Seq = {
    val token = bridge.lexer.get.tokens.find(token => token.token.token == "$ID")
    if (token.isDefined) {
      findVariableToken(src, pos, token.get.token) match {
        case Some(tokenStr) =>
          val newSeq = addTokenToSeq(bridge.seq, tokenStr._1, tokenStr._2)
          if (token.get.lexer.isDefined) run(src, tokenStr._2, Bridge(newSeq, token.get.lexer, Some(bridge)))
          else findId(src, tokenStr._2, Bridge(newSeq, Some(bridge.lexer.get), Some(bridge)))
        case None => findTillTheEnd(src, pos, Bridge(bridge.seq, bridge.lexer, bridge.father))
      }
    } else findTillTheEnd(src, pos, bridge)

  }


  def findTillTheEnd(src: Array[Char], pos: Pos, bridge: Bridge): Seq = {
    if (pos.charNum >= src.length) bridge.father.get.seq
    else if (findEndToken(src, pos, bridge.lexer)) {
      addTokenToSeq(bridge.father.get.seq, bridge.lexer.get.endToken.get.token, pos)
      val tokLen = bridge.lexer.get.endToken.get.token.length
      run(src, Pos(pos.charNum + tokLen, pos.line, pos.col + tokLen), bridge.father.get)
    } else if (bridge.father.isDefined && findEndToken(src, pos, bridge.father.get.lexer)) {
//      if(bridge.father.isDefined)
      addTokenToSeq(bridge.father.get.seq, bridge.father.get.lexer.get.endToken.get.token, pos)
      val tokLen = bridge.father.get.lexer.get.endToken.get.token.length
      run(src, Pos(pos.charNum + tokLen, pos.line, pos.col + tokLen), bridge.father.get.father.get)
    } else if (bridge.lexer.get.endToken.isDefined) {
      addTokenToSeq(bridge.seq, src(pos.charNum).toString, pos)
      run(src, Pos(pos.charNum + 1, pos.line, pos.col + 1), bridge)
    } else {
      run(src, Pos(pos.charNum, pos.line, pos.col), bridge.father.get)
    }
  }

  def findEndToken(src: Array[Char], pos: Pos, lexer: Option[Lexer]): Boolean = {
    if (lexer.isDefined && lexer.get.endToken.isDefined) {
      findToken(src, pos, lexer.get.endToken.get) match {
        case Some(_) => true
        case None => false
      }
    } else false
  }

  def findTokenInLexer(src: Array[Char], pos: Pos, lexer: Lexer): Option[(Child, Pos)] = {
    var i = pos.charNum
    val max = i + Math.min(findLongestSeq(lexer), src.length - pos.charNum)
    var j = 0
    var token: Option[(Child, Pos)] = None
    val chars = ListBuffer.empty[Char]
    while (token.isEmpty && i < max) {
      chars.addOne(src(i))
      j = 0
      while (token.isEmpty && j < lexer.tokens.length) {
        if (!lexer.tokens(j).token.token.startsWith("$") && chars.toSeq == lexer.tokens(j).token.token.toSeq && isFollowedBy(src, lexer.tokens(j).token.followedBy, i)) {
          token = Some(Child(lexer.tokens(j).token, lexer.tokens(j).lexer), Pos(i + 1, pos.line, pos.col + lexer.tokens(j).token.token.length))
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
      val length = seqLength(token.token)
      if (length > max) {
        max = length
      }
    })
    max
  }

  def seqLength(token: Token): Int = token.token.length + (if (token.followedBy.isDefined) token.followedBy.get.length else 0)
}
