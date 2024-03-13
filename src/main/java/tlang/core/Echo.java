package tlang.core;

import tlang.core.func.FuncRet;

public class Echo {

    public static FuncRet echo(Entity entity, String attr) {
        var str = ((String) entity.getAttr(attr).get());
        System.out.println(str.toString());
        return FuncRet.of(str);
    }
}
