package dev.tlang.tlang.ast

case class DomainModel(header: Option[DomainHeader], body: List[DomainBlock])
