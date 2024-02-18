package tlang.internal;

import tlang.core.Array;
import tlang.core.String;

public interface AnyTmplBlock<T> extends TmplNode, DomainBlock {

    Array<String> getLangs();

    String getName();
}
