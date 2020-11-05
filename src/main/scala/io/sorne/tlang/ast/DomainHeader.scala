package io.sorne.tlang.ast

case class DomainHeader(exposes: Option[List[DomainExpose]], uses: Option[List[DomainUse]])
