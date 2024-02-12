package tlang.internal;

import tlang.core.Array;
import tlang.core.String;

public interface AnyTmplBlock<T> extends TmplNode<T>, DomainBlock {

    Array<String> getLangs();

    String getName();
}
