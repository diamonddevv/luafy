package dev.diamond.luafy.autodocs;

import dev.diamond.luafy.script.abstraction.NamedParam;
import dev.diamond.luafy.script.abstraction.TypedFunctions;
import dev.diamond.luafy.script.abstraction.api.AbstractTypedScriptApi;
import dev.diamond.luafy.script.abstraction.obj.AbstractTypedScriptObject;
import dev.diamond.luafy.script.registry.callback.ScriptCallbackEvent;
import dev.diamond.luafy.script.registry.lang.ScriptLanguage;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiConsumer;

public class MarkdownAutodoc implements Autodoc<MarkdownAutodoc.Markdown, MarkdownAutodoc.MarkdownSection> {

    private final String filename;

    public MarkdownAutodoc(String filename) {
        this.filename = filename;
    }


    @Override
    public Markdown getBlankFormat() {
        return new Markdown(null);
    }

    @Override
    public MarkdownSection getEmptyList() {
        return new MarkdownSection(null);
    }


    @Override
    public void preLangs(MarkdownSection sec) {
        var table = new MarkdownTable<>();
        table.setHeaders("ScriptLanguage Subclass Name", "Language Name", "Implementer", "Language Documentation", "Description", "File Extensions");

        MarkdownTableSubSection sub = new MarkdownTableSubSection("Script Languages", "", table);
        sec.subsections.add(sub);
    }

    @Override
    public void preEvents(MarkdownSection sec) {
        var table = new MarkdownTable<>();
        table.setHeaders("Event Identifier", "Description", "Context Parameters");

        MarkdownTableSubSection sub = new MarkdownTableSubSection("Callback Events", "", table);
        sec.subsections.add(sub);
    }

    @Override
    public void addLang(ScriptLanguage<?> lang, MarkdownSection section) {
        var table = section.subsections.get(0).table;

        table.addObj(lang, (l, a) -> {
            var la = (ScriptLanguage<?>) l;

            a.assign(0, la.getClass().getSimpleName());
            a.assign(1, la.getLangName());
            a.assign(2, la.getImplementerCredits());
            a.assign(3, la.getLanguageDocumentationUrl());
            a.assign(4, la.getDescription());
            a.assign(5, Arrays.stream(la.getFileExtensions()).reduce((s1, s2) -> s1 + ", " + s2).orElse(""));
        });

    }

    @Override
    public void addEvent(ScriptCallbackEvent event, MarkdownSection section) {
        var table = section.subsections.get(0).table;

        table.addObj(event, (e, a) -> {
            var ev = (ScriptCallbackEvent) e;

            a.assign(0, ev.toString());
            a.assign(1, ev.getDescription());
            a.assign(2, ev.getContextParams() == null ? "" : stringifyNamedParams(ev.getContextParams()));
        });
    }

    @Override
    public void addTypedObject(AbstractTypedScriptObject<?> obj, MarkdownSection section) {
        var table = getFunctionTable(obj.getTypedFunctionList().getHash().entrySet());

        MarkdownTableSubSection ss = new MarkdownTableSubSection(obj.getName(), obj.getDescription(), table);
        section.subsections.add(ss);
    }

    @Override
    public void addTypedApi(AbstractTypedScriptApi typedApi, MarkdownSection section) {
        var table = getFunctionTable(typedApi.getTypedFunctionList().getHash().entrySet());

        MarkdownTableSubSection ss = new MarkdownTableSubSection(typedApi.name, typedApi.getDescription(), table);
        section.subsections.add(ss);
    }

    @Override
    public void addList(Markdown head, MarkdownSection section, String snakeCaseKey, String titleCaseKey) {
        section.header = titleCaseKey;
        head.sections.add(section);
    }

    @Override
    public byte[] completedToBytes(Markdown format) {
        format.header = "Luafy Documentation";
        StringBuilder md = new StringBuilder();

        md.append("# ").append(format.header).append("\n_This documentation was autogenerated by Luafy Autodocs._\n\n");

        for (var s : format.sections) {
            md.append("## ").append(s.header).append("\n");

            for (var sec : s.subsections) {
                md.append("### ").append(sec.title).append("\n");
                if (sec.desc != null && !sec.desc.isEmpty()) md.append("_").append(sec.desc).append("_\n");

                md.append("\n");

                String table = sec.table.print();
                md.append(table);
                md.append("\n<br>\n<br>\n\n");
            }
            md.append("\n<br>\n\n");
        }



        return md.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String getFilename() {
        return filename;
    }

    ////////////////////////////////////////////////////

    public static MarkdownTable<Map.Entry<String, TypedFunctions.TypeData>> getFunctionTable(Set<Map.Entry<String, TypedFunctions.TypeData>> f) {
        MarkdownTable<Map.Entry<String, TypedFunctions.TypeData>> table = new MarkdownTable<>();
        table.setHeaders("Function Name", "Description", "Parameters", "Optional Parameters", "Return Type");

        for (var entry : f) {
            table.add(entry, (e, a) -> {
                a.assign(0, e.getKey());
                a.assign(1, e.getValue().description().orElse(" "));
                a.assign(2, stringifyNamedParams(e.getValue().params()));
                a.assign(3, stringifyNamedParams(e.getValue().optionalParams()));
                a.assign(4, stringifyNamedParamClassWrapper(e.getValue().returnType().orElse(null)));
            });
        }

        return table;
    }

    public static String stringifyNamedParam(NamedParam param) {
        return "`" + param.name + ": " + stringifyNamedParamClassWrapper(param.clazz) + "`";
    }
    public static String stringifyNamedParams(NamedParam[] params) {
        StringBuilder b = new StringBuilder();
        for (NamedParam p : params) {
            b.append("- ").append(stringifyNamedParam(p)).append("<br>");
        }
        String s = b.toString();

        if (s.isEmpty()) return null;
        else return s;
    }

    public static String stringifyNamedParamClassWrapper(NamedParam.NamedParamClassWrapper<?> npcw) {

        if (npcw == null) return " ";

        if (npcw.isFunction()) {

        } else {
            return npcw.getName();
        }

        return "";
    }

    /////////////////////////////////////////////////////

    public static class Markdown {

        private String header;
        private ArrayList<MarkdownSection> sections;

        public Markdown(String header) {
            this.header = header;
            this.sections = new ArrayList<>();
        }

        public MarkdownSection getOrCreateSection(String key) {
            var matches = sections.stream().filter(s -> Objects.equals(s.header, key)).toList();
            if (matches.isEmpty()) {
                MarkdownSection section = new MarkdownSection(key);
                sections.add(section);
                return section;
            } else {
                return matches.get(0);
            }
        }
    }

    public static class MarkdownSection {
        private String header;
        private ArrayList<MarkdownTableSubSection> subsections;

        public MarkdownSection(String header) {
            this.header = header;
            this.subsections = new ArrayList<>();
        }
    }

    public static class MarkdownTableSubSection {

        private String title;
        private String desc;
        private MarkdownTable<?> table;

        public MarkdownTableSubSection(String title, String desc, MarkdownTable<?> table) {
            this.title = title;
            this.desc = desc;
            this.table = table;
        }

        public String print() {
            StringBuilder s = new StringBuilder();

            s.append("# ").append(title).append("\n");
            s.append(desc).append("\n\n");
            s.append(table.print());

            return s.toString();
        }
    }
    public static class MarkdownTable<T> {

        private Collection<String> headers;
        private List<LinkedList<String>> rows;

        public MarkdownTable() {
            rows = new ArrayList<>();
        }

        public void setHeaders(String... headers) {
            this.headers = Arrays.stream(headers).toList();
        }

        public void addObj(Object object, BiConsumer<Object, Assigner<Integer, String>> assignmentBiconsumer) {
            add((T) object, assignmentBiconsumer::accept);
        }

        public void add(T t, BiConsumer<T, Assigner<Integer, String>> assignmentBiconsumer) {
            Assigner<Integer, String> assigner = new Assigner<>(" ");
            assignmentBiconsumer.accept(t, assigner);
            var sorted = assigner.assignments.stream().sorted((a, b) -> {
                // a is before b, -1
                // a == b, 0
                // a is after b, 1

                if (a.key() > b.key())
                    return 1;
                else if (Objects.equals(a.key(), b.key()))
                    return 0;
                else
                    return -1;
            });
            rows.add(new LinkedList<>(sorted.map(Assignment::value).toList()));
        }

        public String print() {
            StringBuilder builder = new StringBuilder();

            for (String h : headers) builder.append("|").append(h);
            builder.append("|\n");

            for (String h : headers) builder.append("|--");
            builder.append("|\n");

            for (var row : rows) {
                for (String h : row) builder.append("|").append(h);
                builder.append("|\n");
            }

            return builder.toString();
        }
    }
    public interface Assignment<K, T> {
        K key();

        T value();

        static <K, T> Assignment<K, T> of(K key, T value) {
            return new Assignment<K, T>() {
                @Override
                public K key() {
                    return key;
                }

                @Override
                public T value() {
                    return value;
                }
            };
        }
    }
    public static class Assigner<K, T> {

        private final T def;
        private Collection<Assignment<K, T>> assignments;

        public Assigner(T def) {
            this.def = def;
            this.assignments = new ArrayList<>();
        }

        public void assign(K key, T value) {
            assignments.add(Assignment.of(key, value == null ? def : value));
        }
    }
}
