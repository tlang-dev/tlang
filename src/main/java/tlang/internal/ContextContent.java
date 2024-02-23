package tlang.internal;

import tlang.core.Int;
import tlang.core.Null;
import tlang.core.Type;
import tlang.core.Value;

public class ContextContent implements Value<ContextContent>, DeepCopy{

    private final ContextResource resource;

    private final Int line;

    private final Int charPos;


    public ContextContent(ContextResource resource, Int line, Int charPos) {
        this.resource = resource;
        this.line = line;
        this.charPos = charPos;
    }

    public ContextResource getResource() {
        return resource;
    }

    public Int getLine() {
        return line;
    }

    public Int getCharPos() {
        return charPos;
    }

    @Override
    public Value<?> deepCopy() {
        return null;
    }

    @Override
    public ContextContent getElement() {
        return this;
    }

    @Override
    public Type getType() {
        return null;
    }

}
