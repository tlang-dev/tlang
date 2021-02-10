package dev.tlang.tlang.loader.remote

import dev.tlang.tlang.loader.manifest.ManifestLoader
import io.sorne.tlang.loader.manifest.ManifestLoader

object RepoManifestLoader {

  def parseManifest(content: String): RepoManifest = {
    val map = ManifestLoader.loadFromString(content)
    mapToRepoManifest(map)
  }

  def mapToRepoManifest(elems: Map[String, _]): RepoManifest = {
    RepoManifest(
      elems.getOrElse("name", "").asInstanceOf[String],
      elems.getOrElse("fullName", "").asInstanceOf[String],
      elems.getOrElse("hash", "").asInstanceOf[String],
      elems.getOrElse("file", "").asInstanceOf[String],
    )
  }

}
