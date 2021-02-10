package dev.tlang.tlang.lsp

case class Request(jsonrpc: String, id: Int, method: String, params: Map[String, Any])

