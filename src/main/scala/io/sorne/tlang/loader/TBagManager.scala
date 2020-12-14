package io.sorne.tlang.loader

import org.apache.commons.compress.archivers.{ArchiveEntry, ArchiveInputStream, ArchiveStreamFactory}
import org.apache.commons.compress.compressors.CompressorStreamFactory
import org.apache.commons.compress.utils.IOUtils

import java.io.{BufferedInputStream, File, FileInputStream, FilenameFilter, InputStream}
import java.nio.file.{Files, Path, Paths}
import scala.annotation.tailrec

trait TBagManager {
  def extract(tarGzFile: Path, dest: Path): Either[LoaderError, Unit]

  def isDirectory(path: Path): Boolean

  def findTBagFile(path: Path): Option[File]
}

object TBagManager extends TBagManager {

  override def extract(tarGzFile: Path, dest: Path): Either[LoaderError, Unit] = {
    val input = new FileInputStream(tarGzFile.toFile)
    val uncompressed = new CompressorStreamFactory().createCompressorInputStream(getMarkedStream(input))
    val archive = new ArchiveStreamFactory().createArchiveInputStream(getMarkedStream(uncompressed))
    extractArchive(archive, dest)
    Right(())
  }

  @tailrec
  def extractArchive(archive: ArchiveInputStream, dest: Path): Unit = {
    val entry = archive.getNextEntry
    if (entry != null) {
      extractFile(entry, archive, dest)
      extractArchive(archive, dest)
    }
  }

  def extractFile(entry: ArchiveEntry, archive: ArchiveInputStream, dest: Path): Unit = {
    val file = new File(Paths.get(dest.toString, entry.getName).toString)
    if (entry.isDirectory) file.mkdirs()
    else {
      val parent = file.getParentFile
      parent.mkdirs()
      val output = Files.newOutputStream(file.toPath)
      try IOUtils.copy(archive, output)
      finally if (output != null) output.close()
    }
  }

  def getMarkedStream(input: InputStream): InputStream = {
    if (input.markSupported()) input
    else new BufferedInputStream(input)
  }

  override def isDirectory(path: Path): Boolean = Files.exists(path) && Files.isDirectory(path)

  override def findTBagFile(path: Path): Option[File] = {
    path.toFile.listFiles(new FilenameFilter {
      override def accept(file: File, name: String): Boolean = name.endsWith(".tbag")
    }).toList.headOption
  }

}
