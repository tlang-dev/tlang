package io.sorne.tlang.ast.tmpl

import io.sorne.tlang.ast.DomainBlock

case class TmplBlock(name: String, lang: String,
                     params: Option[List[String]],
                     pkg: Option[TmplPkg] = None,
                     uses: Option[List[TmplUse]] = Some(List()),
                     content: Option[List[TmplContent]] = None) extends DomainBlock
