package dev.diamond.luafy.script.abstraction;

public class NamedParam {
    public final String name;
    public final Class<?> clazz;

    public NamedParam(String name, Class<?> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    public NamedParam(Class<?> clazz) {
        this.name = "";
        this.clazz = clazz;
    }

    public NamedParam(String name) {
        this.name = name;
        this.clazz = Object.class;
    }
}
