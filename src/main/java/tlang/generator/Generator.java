package tlang.generator;

import dev.tlang.tlang.tmpl.lang.ast.LangBlock;
import tlang.core.func.FuncRet;

public class Generator {

    public static FuncRet generate(LangBlock block) {
        var generated = "Generated code";
        return FuncRet.of(generated);
    }
}
