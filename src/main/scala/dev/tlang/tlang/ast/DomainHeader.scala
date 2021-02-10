package dev.tlang.tlang.ast

case class DomainHeader(exposes: Option[List[DomainExpose]], uses: Option[List[DomainUse]])
