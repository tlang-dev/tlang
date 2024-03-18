package tlang.core;

import tlang.core.func.FuncRet;

public class Instance {

    public static FuncRet isOf(Entity entity, String type) {
        if (entity.getType().getType().isEqual(type).get()) {
            return FuncRet.of(Bool.TRUE);
        } else if (entity.getModel().isNotNull().get()) {
            return Instance.isModelOf((Model) entity.getModel().get().getValue(), type);
        }
        return FuncRet.of(Bool.FALSE);
    }

    public static FuncRet isModelOf(Model model, String type) {
        if (model.getType().getType().isEqual(type).get()) {
            return FuncRet.of(Bool.TRUE);
        } else {
            return FuncRet.of(new Bool(model.getModel().isNotNull().get() && ((Bool) Instance.isModelOf((Model) model.getModel().get().getValue(), type).get().getValue().getValue()).get()));
        }
    }

    public static FuncRet isArray(Value value) {
        return FuncRet.of(new Bool(value instanceof Array));
    }

    public static FuncRet isLong(Value value) {
        return FuncRet.of(new Bool(value instanceof Long));
    }

    public static FuncRet isDouble(Value value) {
        return FuncRet.of(new Bool(value instanceof Double));
    }

    public static FuncRet isString(Value value) {
        return FuncRet.of(new Bool(value instanceof String));
    }

    public static FuncRet isBool(Value value) {
        return FuncRet.of(new Bool(value instanceof Bool));
    }

    public static FuncRet isANull(Value value) {
        return FuncRet.of(new Bool(value instanceof Null));
    }

    public static FuncRet isMultiValue(Value value) {
        return FuncRet.of(new Bool(value instanceof MultiValue));
    }

    public static FuncRet isEntity(Value value) {
        return FuncRet.of(new Bool(value instanceof Entity));
    }
}
