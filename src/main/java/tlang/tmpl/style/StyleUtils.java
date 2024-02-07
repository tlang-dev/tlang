package tlang.tmpl.style;

import tlang.core.Null;
import tlang.core.String;
import tlang.core.func.FuncRet;
import tlang.mutable.List;

public class StyleUtils {

    public static FuncRet findStyles(StyleBlock block, String name) {
        var styles = new List<>();
        for (var struct : block.getAttr(new String("contents")).get()) {
            if (struct.getName().isNotNull() && struct.getName().get().equals(name.get())) {
                styles.add(struct);
            }
        }
        return styles.toArray();
    }

    public static FuncRet<Null<StyleAttribute>> findRule(StyleStruct struc, String name) {
        var rule = struc.getAttr(new String(name.get()));
        if(rule.isNull()) {
            return FuncRet.of(Null.empty());
        }else {
            return (FuncRet<Null<StyleAttribute>>) FuncRet.of(rule.get());
        }
    }
}
