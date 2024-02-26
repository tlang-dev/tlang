package tlang.internal;

import tlang.core.String;
import tlang.core.Type;
import tlang.core.Value;

public abstract class TmplID implements TmplNode<TmplID>, Value {

    public static final Type TYPE = ClassType.of(TmplID.class);

    public String toStr() {
        if (this instanceof TmplStringID) {
            return ((TmplStringID) this).getId();
        } else if (this instanceof TmplInterpretedId interpretedId) {
            java.lang.String pre = ((String) interpretedId.getPre().orElse(new String("")).get().get().getValue()).get();
            java.lang.String post = ((String) interpretedId.getPost().orElse(new String("")).get().get().getValue()).get();
            return new String(pre + "${uninterpreted}" + post);
        } else if (this instanceof TmplReplacedId replacedId) {
            java.lang.String pre = ((String) replacedId.getPre().orElse(new String("")).get().get().getValue()).get();
            java.lang.String post = ((String) replacedId.getPost().orElse(new String("")).get().get().getValue()).get();
            return new String(pre + replacedId.getNode().toString() + post);
        } else if (this instanceof AnyTmplBlock<?>) {
            return new String("${uninterpretedBlock}");
        }
        return new String("TmplId type not found");
    }

    public static final String name = new String(TmplID.class.getSimpleName());
}
