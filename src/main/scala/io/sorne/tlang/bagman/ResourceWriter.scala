package io.sorne.tlang.bagman

import java.nio.file.Path

trait ResourceWriter {
  def write(path: Path, fileName: String, content: String): Unit
}
