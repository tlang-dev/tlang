package tlang.tmpl.style;

import tlang.Entity;
import tlang.core.Array;
import tlang.core.Instance;
import tlang.core.Null;
import tlang.core.String;
import tlang.core.func.FuncRet;
import tlang.mutable.List;

public class StyleUtils {

    public static FuncRet<Array<Entity>> findStyles(Entity block, String name) {
        var styles = new List<>();
        var struct = block.getAttr(new String("contents"));
        if (struct.isNotNull().get()) {
            var contents = (Array<Entity>)struct.get();
            for (var content : contents.getRecords()) {
                content.getAttr(name).ifNotNull(value -> {
                    Instance.isEntity(value).onResult(isEntity -> value)
                });
            }
        }
        return FuncRet.of(Array.empty());
    }

    public static FuncRet<Array<Entity>> findRule(Entity struc, String name) {
        var rule = struc.getAttr(new String(name.get()));
        if(rule.isNull()) {
            return FuncRet.of(Null.empty());
        }else {
            return (FuncRet<Null<StyleAttribute>>) FuncRet.of(rule.get());
        }
    }
}
