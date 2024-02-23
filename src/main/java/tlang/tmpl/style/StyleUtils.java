package tlang.tmpl.style;

import tlang.Entity;
import tlang.core.Array;
import tlang.core.Instance;
import tlang.core.String;
import tlang.core.func.FuncRet;
import tlang.mutable.List;

public class StyleUtils {

    public static FuncRet<Array<Entity>> findStyles(Entity block, String name) {
        var styles = new List<>();
        var struct = block.getAttr(new String("contents"));
        if (struct.isNotNull().get()) {
            var contents = (Array<Entity>) struct.get();
            for (var content : contents.getRecords()) {
                //content.getAttr(name).ifNotNull(value -> {
                  //  Instance.isEntity(value).onResult(isEntity -> isEntity.getElement().ifTrue(styles::add));
//                    return FuncRet.VOID;
               // });
            }
        }
        return FuncRet.of(Array.empty());
    }

    public static FuncRet<Array<Entity>> findRule(Entity struct, String name) {
        var rule = struct.getAttr(new String(name.get()));
        if (rule.isNull().get()) {
            return FuncRet.of(Array.empty());
        } else {
            return FuncRet.of(new Array<>((Entity) rule.get()));
        }
    }
}
