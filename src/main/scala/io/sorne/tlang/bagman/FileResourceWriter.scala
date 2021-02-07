package io.sorne.tlang.bagman

import java.io.{BufferedWriter, File, FileWriter}
import java.nio.file.{Path, Paths}

object FileResourceWriter extends ResourceWriter {
  override def write(path: Path, fileName: String, content: String): Unit = {
    val file = new File(Paths.get(path.toString, fileName).toString)
    file.getParentFile.mkdirs()
    val writer = new BufferedWriter(new FileWriter(file))
    writer.write(content)
    writer.close()
  }
}
