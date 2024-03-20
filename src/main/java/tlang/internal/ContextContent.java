package tlang.internal;

import tlang.core.Int;
import tlang.core.String;
import tlang.core.Type;
import tlang.core.Value;

public class ContextContent implements Value {

    private final ContextResource resource;

    private final Int line;

    private final Int charPos;

    private final Type type;

    public ContextContent(ContextResource resource, Int line, Int charPos) {
        this.resource = resource;
        this.line = line;
        this.charPos = charPos;
        this.type = null;
    }

    public ContextContent(ContextResource resource, Int line, Int charPos, Type type) {
        this.resource = resource;
        this.line = line;
        this.charPos = charPos;
        this.type = type;
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
    public ContextContent getValue() {
        return this;
    }

    @Override
    public Type getType() {
        if (type != null) {
            return type;
        }
        return new Type() {
            @Override
            public String getType() {
                return new String(getPkg() + "/" + getSimpleType());
            }

            @Override
            public String getSimpleType() {
                return getResource().getName();
            }

            @Override
            public String getPkg() {
                return getResource().getPkg();
            }
        };
    }

}
