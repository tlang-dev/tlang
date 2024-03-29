package dev.tlang.tlang.loader

trait ResourceLoader {
  def load(root: String, fromRoot: String, pkg: String, name: String): Either[LoaderError, String]
}


