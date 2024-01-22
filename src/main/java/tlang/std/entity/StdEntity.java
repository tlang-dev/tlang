package tlang.std.entity;

import dev.tlang.tlang.ast.common.value.EntityValue;
import dev.tlang.tlang.ast.common.value.TLangBool;
import dev.tlang.tlang.ast.common.value.TLangString;
import scala.Option;
import tlang.core.func.FuncRet;

public class StdEntity {

    public static FuncRet exists(EntityValue entity, TLangString attr) {
        if (entity.attrs().isDefined()) {
            if (entity.attrs().get().exists(v1 -> v1.attr().isDefined() && v1.attr().get().equals(attr.toString()))) {
                return new FuncRet(new TLangBool(Option.empty(), true));
            }
        }
        return new FuncRet(new TLangBool(Option.empty(), false));
    }
}
