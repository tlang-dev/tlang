package tlang.core;

import tlang.Entity;
import tlang.core.func.FuncRet;

public class Instance {

    public static FuncRet<Bool> isOf(Entity entity, String type) {
        if (entity.getType().getType().isEqual(type).get()) {
            return FuncRet.of(Bool.TRUE);
        } else if (entity.getModel().isNotNull().get()) {
            return Instance.isModelOf(entity.getModel().get(), type);
        }
        return FuncRet.of(Bool.FALSE);
    }

    public static FuncRet<Bool> isModelOf(Model model, String type) {
        if (model.getType().getType().isEqual(type).get()) {
            return FuncRet.of(Bool.TRUE);
        } else {
            return FuncRet.of(new Bool(model.getModel().isNotNull().get() && Instance.isModelOf(model.getModel().get(), type).get().get().value().get()));
        }
    }

    public static FuncRet<Bool> isArray(Value<?> value) {
        return FuncRet.of(new Bool(value.value() instanceof Array<?>));
    }

    public static FuncRet<Bool> isLong(Value<?> value) {
        return FuncRet.of(new Bool(value.value() instanceof Long));
    }

    public static FuncRet<Bool> isDouble(Value<?> value) {
        return FuncRet.of(new Bool(value.value() instanceof Double));
    }

    public static FuncRet<Bool> isString(Value<?> value) {
        return FuncRet.of(new Bool(value.value() instanceof String));
    }

    public static FuncRet<Bool> isBool(Value<?> value) {
        return FuncRet.of(new Bool(value.value() instanceof Bool));
    }

    public static FuncRet<Bool> isANull(Value<?> value) {
        return FuncRet.of(new Bool(value.value() instanceof Null));
    }

    public static FuncRet<Bool> isMultiValue(Value<?> value) {
        return FuncRet.of(new Bool(value.value() instanceof MultiValue));
    }

    public static FuncRet<Bool> isEntity(Value<?> value) {
        return FuncRet.of(new Bool(value.value() instanceof Entity));
    }
}
