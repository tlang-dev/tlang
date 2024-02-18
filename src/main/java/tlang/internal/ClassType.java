package tlang.internal;

import tlang.core.String;
import tlang.core.Type;

public class ClassType implements Type {

    private final Class<?> clazz;

    public ClassType(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String getType() {
        return new String((clazz.getPackageName() + "/" + clazz.getSimpleName()));
    }

    @Override
    public String getSimpleType() {
        return new String(clazz.getSimpleName());
    }

    @Override
    public String getPkg() {
        return new String(clazz.getPackageName());
    }

    public static Type of(Class<?> clazz) {
        return new ClassType(clazz);
    }
}
