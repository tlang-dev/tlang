package dev.tlang.tlang.parser

import scala.collection.mutable.ListBuffer

object LexerRunner {

  def run(src: Array[Char], pos: Pos, seq: Seq, lexer: Lexer): Seq = {
    if (pos.charNum >= src.length) seq
    else run(src, pos, seq, seq, lexer, lexer)
  }

  def run(src: Array[Char], pos: Pos, father: Seq, current: Seq, fatherLexer: Lexer, currentLexer: Lexer): Seq = {
    if (pos.charNum >= src.length) father
    else {
      managerTokenFound(findTokenInLexer(src, pos, currentLexer), src, pos, father, current, fatherLexer, currentLexer, findId)
    }
  }

  def managerTokenFound(token: Option[(Child, Pos)], src: Array[Char], pos: Pos, father: Seq, current: Seq, fatherLexer: Lexer, currentLexer: Lexer, nextSearch: (Array[Char], Pos, Seq, Seq, Lexer, Lexer) => Seq): Seq = {
    token match {
      case Some(newToken) =>
        val newSeq = addTokenToSeq(current, newToken._1.token.token, newToken._2)
        if (newToken._1.lexer.isDefined) run(src, newToken._2, current, newSeq, currentLexer, newToken._1.lexer.get)
        else findId(src, newToken._2, current, newSeq, currentLexer, currentLexer)
      case None => nextSearch(src, pos, father, current, fatherLexer, currentLexer)
    }
  }

  def addTokenToSeq(seq: Seq, token: String, pos: Pos): Seq = {
    val newSeq = Seq(token, pos = pos)
    seq.children.addOne(newSeq)
    newSeq
  }

  def findId(src: Array[Char], pos: Pos, father: Seq, current: Seq, fatherLexer: Lexer, currentLexer: Lexer): Seq = {
    val token = currentLexer.tokens.find(token => token.token.token == "$ID")
    if (token.isDefined) {
      findVariableToken(src, pos, token.get.token) match {
        case Some(tokenStr) =>
          val newSeq = addTokenToSeq(current, tokenStr._1, tokenStr._2)
          if (token.get.lexer.isDefined) run(src, tokenStr._2, current, newSeq, currentLexer, token.get.lexer.get)
          else findId(src, tokenStr._2, current, newSeq, currentLexer, currentLexer)
        case None => findTillTheEnd(src, pos, father, current, fatherLexer, currentLexer)
      }
    } else findTillTheEnd(src, pos, father, current, fatherLexer, currentLexer)

  }


  def findTillTheEnd(src: Array[Char], pos: Pos, father: Seq, current: Seq, fatherLexer: Lexer, currentLexer: Lexer): Seq = {
    if (pos.charNum >= src.length) father
    else if (findEndToken(src, pos, currentLexer.endToken)) {
      val newSeq = addTokenToSeq(father, currentLexer.endToken.get.token, pos)
      val tokLen = currentLexer.endToken.get.token.length
      run(src, Pos(pos.charNum + tokLen, pos.line, pos.col + tokLen), father, father, fatherLexer, fatherLexer)
    } else if(currentLexer.endToken.isDefined){
      val newSeq = addTokenToSeq(current, src(pos.charNum).toString, pos)
      run(src, Pos(pos.charNum + 1, pos.line, pos.col + 1), father, current, fatherLexer, currentLexer)
    } else {
//      addTokenToSeq(father, src(pos.charNum).toString, pos)
      run(src, Pos(pos.charNum, pos.line, pos.col), father, father, fatherLexer, fatherLexer)
    }
  }

  def findEndToken(src: Array[Char], pos: Pos, endToken: Option[Token]): Boolean = {
    if (endToken.isDefined) {
      findToken(src, pos, endToken.get) match {
        case Some(_) => true
        case None => false
      }
    } else false
  }

  //  def manageChildCall(src: Array[Char], pos: Pos, father: Seq, current: Seq, lexer: Lexer):Seq = {
  //    run(src, pos, father, current, lexer)
  //
  //  }

  //  def run(src: Array[Char], pos: Pos, seq: Seq, lexer: Lexer): Pos = {
  //    var i = pos.charNum
  //    var line = pos.line
  //    var col = pos.col
  //    var currentSeq = seq
  //    var end = false
  //    var added = 0
  //    while (!end && i < src.length) {
  //      findTokenInLexer(src, Pos(i, line, col), lexer) match {
  //        case Some(value) =>
  //          val newSeq = Seq(value._1.token.token, pos = Pos(i, line, col))
  //          seq.children.addOne(newSeq)
  //          added = added + 1
  //          i = value._2.charNum
  //          line = value._2.line
  //          col = value._2.col
  //          if(value._1.lexer.isDefined) {
  //            val newPos = run(src, Pos(i, line, col), newSeq, value._1.lexer.get)
  //            i = newPos.charNum
  //            line = newPos.line
  //            col = newPos.col
  //            if (lexer.endToken.isEmpty) end = true
  //          }
  //        case None =>
  //          lexer.tokens.find(token => token.token.token == "$ID").foreach(token => {
  //            findVariableToken(src, Pos(i, line, col), token.token) match {
  //              case Some(value) =>
  //                val newSeq = Seq(value._1, pos = Pos(i, line, col))
  //                seq.children.addOne(newSeq)
  //                added = added + 1
  //                i = value._2.charNum
  //                line = value._2.line
  //                col = value._2.col
  //                if(token.lexer.isDefined) {
  //                  val newPos = run(src, Pos(i, line, col), newSeq, token.lexer.get)
  //                  i = newPos.charNum
  //                  line = newPos.line
  //                  col = newPos.col
  //                  if (lexer.endToken.isEmpty) end = true
  //                }
  //              case None =>
  //            }
  //          })
  //          if (lexer.endToken.isDefined && lexer.endToken.get.token != "$EOF") {
  //            findToken(src, Pos(i, line, col), lexer.endToken.get) match {
  //              case Some(value) =>
  //                val newSeq = Seq(lexer.endToken.get.token, pos = Pos(i, line, col))
  //                currentSeq.children.addOne(newSeq)
  //                currentSeq = newSeq
  //                i = value.charNum
  //                line = value.line
  //                col = value.col
  //                end = true
  //              case None =>
  //            }
  //          }
  //          if (!end && i < src.length) {
  //            val newSeq = Seq(src(i).toString, pos = Pos(i, line, col))
  //            currentSeq.children.addOne(newSeq)
  //            currentSeq = newSeq
  //            if (src(i) == '\n') {
  //              col = 0
  //              line = line + 1
  //            } else col = col + 1
  //            i = i + 1
  //          }
  //      }
  //    }
  //    Pos(i, line, col)
  //  }


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
