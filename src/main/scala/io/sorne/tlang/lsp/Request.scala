package io.sorne.tlang.lsp



case class Request(jsonrpc: String, id: Int, method: String, params: Map[String, Any])

