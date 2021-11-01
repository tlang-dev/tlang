package dev.tlang.tlang.resolver.checker

import dev.tlang.tlang.ast.common.ValueType
import dev.tlang.tlang.ast.common.call.{CallObject, ComplexValueStatement}
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ComplexValueType, EntityValue, LazyValue, PrimitiveValue, TLangBool, TLangDouble, TLangLong, TLangString}
import dev.tlang.tlang.resolver.ResolverError

object FindOperationType {

  def findOperationType(operation: Operation): Either[ResolverError, ValueType] = {
    operation.content match {
      case Left(op) => findOperationType(op)
      case Right(value) => findComplexValueStatementType(value)
    }
  }

  def findComplexValueStatementType(value: ComplexValueStatement[_]): Either[ResolverError, ValueType] = {
    value match {
      case callObject: CallObject => FollowCallToTheEnd.followCallToTheEnd(callObject, value.getContext)
      case primitiveValue: PrimitiveValue[_] =>findPrimitiveType(primitiveValue)
      case lazyValue: LazyValue =>
    }
  }

  def findPrimitiveType(primitiveValue: PrimitiveValue[_]): Either[ResolverError, ValueType] = {
    primitiveValue match {
      case _: TLangBool => Right(TLangBool.getValueType)
      case _: TLangString => Right(TLangString.getValueType)
      case _: TLangDouble => Right(TLangDouble.getValueType)
      case _: TLangLong => Right(TLangLong.getValueType)
      case _: EntityValue => Right(EntityValue.getT)
    }
  }

}
