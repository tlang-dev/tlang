package dev.tlang.tlang.parser

case class Rule(name:String, openingToken:String, endRuleToken:String, endBlockToken:String, children:List[Rule])
