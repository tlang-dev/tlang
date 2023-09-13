package dev.tlang.tlang.generator.formatter

case class BlockSelector(
                          name: String,
                          before: List[Rule] = List.empty,
                          opening: List[Selector] = List.empty,
                          content: List[Selector] = List.empty,
                          closing: List[Selector] = List.empty,
                          after: List[Rule] = List.empty
                        )
