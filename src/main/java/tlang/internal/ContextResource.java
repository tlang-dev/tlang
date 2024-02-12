package tlang.internal;

import tlang.core.String;

public class ContextResource {

    private final String rootDir;

    private final String fromRoot;

    private final String pkg;

    private final String name;

    public ContextResource(String rootDir, String fromRoot, String pkg, String name) {
        this.rootDir = rootDir;
        this.fromRoot = fromRoot;
        this.pkg = pkg;
        this.name = name;
    }

    public String getRootDir() {
        return rootDir;
    }

    public String getFromRoot() {
        return fromRoot;
    }

    public String getPkg() {
        return pkg;
    }

    public String getName() {
        return name;
    }
}
