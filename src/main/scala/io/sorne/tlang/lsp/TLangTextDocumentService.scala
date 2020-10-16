package io.sorne.tlang.lsp

import java.util
import java.util.concurrent.CompletableFuture

import org.eclipse.lsp4j.jsonrpc.messages
import org.eclipse.lsp4j.services.TextDocumentService
import org.eclipse.lsp4j.{CompletionItem, CompletionList, CompletionParams, DidChangeTextDocumentParams, DidCloseTextDocumentParams, DidOpenTextDocumentParams, DidSaveTextDocumentParams}

class TLangTextDocumentService extends TextDocumentService {
  override def didOpen(params: DidOpenTextDocumentParams): Unit = {

  }

  override def didChange(params: DidChangeTextDocumentParams): Unit = {

  }

  override def didClose(params: DidCloseTextDocumentParams): Unit = {

  }

  override def didSave(params: DidSaveTextDocumentParams): Unit = {

  }

  override def completion(position: CompletionParams): CompletableFuture[messages.Either[util.List[CompletionItem], CompletionList]] = {
    import java.util
    import java.util.concurrent.CompletableFuture

    import org.eclipse.lsp4j.{CompletionItem, CompletionItemKind, CompletionList}
    val typescriptCompletionItem = new CompletionItem
    typescriptCompletionItem.setLabel("TypeScript")
    typescriptCompletionItem.setKind(CompletionItemKind.Text)
    typescriptCompletionItem.setData(1.0)

    val javascriptCompletionItem = new CompletionItem
    javascriptCompletionItem.setLabel("JavaScript")
    javascriptCompletionItem.setKind(CompletionItemKind.Text)
    javascriptCompletionItem.setData(2.0)

    val completions = new util.ArrayList[CompletionItem]
    completions.add(typescriptCompletionItem)
    completions.add(javascriptCompletionItem)

    CompletableFuture.completedFuture(new CompletionList(false, completions))
  }
}
