package tlang.core;

import tlang.core.func.FuncRet;

public class Echo {

    public static FuncRet echo(Entity entity, String attr) {
        System.out.println(entity.getAttr(attr).get());
        return FuncRet.of(entity.getAttr(attr).get());
    }
}
