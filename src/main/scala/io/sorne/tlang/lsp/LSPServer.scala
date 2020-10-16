package io.sorne.tlang.lsp

import java.net.Socket
import java.util.concurrent.CompletableFuture

import org.eclipse.lsp4j._
import org.eclipse.lsp4j.jsonrpc.Launcher
import org.eclipse.lsp4j.launch.LSPLauncher
import org.eclipse.lsp4j.services._

class LSPServer extends LanguageServer with LanguageClientAware {

  val textDocumentService = new TLangTextDocumentService
  val workspaceService = new TLangWorkspaceService

  var client: LanguageClient = _
  var workspaceRoot: String = _

  override def initialize(params: InitializeParams): CompletableFuture[InitializeResult] = {

    workspaceRoot = params.getRootUri

    val capabilities = new ServerCapabilities
    capabilities.setTextDocumentSync(TextDocumentSyncKind.Full)
    capabilities.setCodeActionProvider(false)
    capabilities.setCompletionProvider(new CompletionOptions(true, null))

    CompletableFuture.completedFuture(new InitializeResult(capabilities))
  }

  override def shutdown(): CompletableFuture[AnyRef] = {
    CompletableFuture.completedFuture(null)
  }

  override def exit(): Unit = {

  }

  override def getTextDocumentService: TextDocumentService = {
    textDocumentService
  }

  override def getWorkspaceService: WorkspaceService = {
    workspaceService
  }

  override def connect(client: LanguageClient): Unit = {
    this.client = client
  }

}

object LSPServer {

  def startLSPServer(port: Int): Unit = {
    val socket = new Socket("localhost", port.toInt)

    val in = socket.getInputStream
    val out = socket.getOutputStream

    val server = new LSPServer
    val launcher: Launcher[LanguageClient] = LSPLauncher.createServerLauncher(server, in, out)

    val client = launcher.getRemoteProxy
    server.connect(client)

    launcher.startListening
  }
}
