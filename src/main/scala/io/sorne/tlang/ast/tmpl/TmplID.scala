package io.sorne.tlang.ast.tmpl

import io.sorne.tlang.ast.common.call.CallObject

sealed trait TmplID {

  override def toString: String = {
    this match {
      case iD: TmplStringID => iD.id
      case interId: TmplInterpretedID => interId.pre.getOrElse("") + "${uninterpreted}" + interId.post.getOrElse("")
    }
  }
}

case class TmplInterpretedID(pre: Option[String]=None, call: CallObject, post: Option[String]=None) extends TmplID

case class TmplStringID(id: String) extends TmplID
