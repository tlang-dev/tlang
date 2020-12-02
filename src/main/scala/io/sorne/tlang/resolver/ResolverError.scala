package io.sorne.tlang.resolver

class ResolverError(code: String, message: String = "")

case class ResourceNotFound(error: String) extends ResolverError("ResourceNotFound", error)
