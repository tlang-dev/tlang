package io.sorne.tlang.lsp

import org.eclipse.lsp4j.services.WorkspaceService
import org.eclipse.lsp4j.{DidChangeConfigurationParams, DidChangeWatchedFilesParams}

class TLangWorkspaceService extends WorkspaceService {

  override def didChangeConfiguration(params: DidChangeConfigurationParams): Unit = {

  }

  override def didChangeWatchedFiles(params: DidChangeWatchedFilesParams): Unit = {

  }
}
