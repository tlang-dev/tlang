package io.sorne.tlang.loader

import org.apache.commons.compress.archivers.tar.{TarArchiveEntry, TarArchiveOutputStream}
import org.apache.commons.compress.archivers.{ArchiveEntry, ArchiveInputStream, ArchiveStreamFactory}
import org.apache.commons.compress.compressors.CompressorStreamFactory
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import org.apache.commons.compress.utils.IOUtils

import java.io._
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file._
import scala.annotation.tailrec

trait TBagManager {
  def extract(tarGzFile: Path, dest: Path): Either[LoaderError, Unit]

  def isDirectory(path: Path): Boolean

  def findTBagFile(path: Path): Option[File]

  def compress(dir: Path, dest: Path): Unit
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

  override def compress(dir: Path, dest: Path): Unit = {
    dest.getParent.toFile.mkdirs()
    val out = Files.newOutputStream(dest)
    val buffOut = new BufferedOutputStream(out)
    val gzOut = new GzipCompressorOutputStream(buffOut)
    val tarOut = new TarArchiveOutputStream(gzOut)
    try {
      Files.walkFileTree(dir, new SimpleFileVisitor[Path] {
        override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {

          if (attrs.isSymbolicLink) FileVisitResult.CONTINUE
          else {

            val targetFile = dir.relativize(file)

            try {
//              val tarEntry = new TarArchiveEntry(file.toFile, targetFile.toString)
              val tarEntry = tarOut.createArchiveEntry(file.toFile, targetFile.toString)
              tarOut.putArchiveEntry(tarEntry)
              Files.copy(file, tarOut)
              tarOut.closeArchiveEntry()
            } catch {
              case e: IOException =>
                System.err.printf("Unable to tar.gz : %s%n%s%n", file, e)
            }
            FileVisitResult.CONTINUE
          }
        }

        override def visitFileFailed(file: Path, exc: IOException): FileVisitResult = {
          System.err.printf("Unable to tar.gz : %s%n%s%n", file, exc)
          FileVisitResult.CONTINUE
        }
      })
      tarOut.finish()
    } finally {
      if (gzOut != null) gzOut.close()
      tarOut.close()
      if (buffOut != null) buffOut.close()
      if (out != null) out.close()


      //      if (tarOut != null) tarOut.close()
    }
  }

}
