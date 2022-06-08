package org.simplement.compiler.frontend.parse;

import org.simplement.compiler.frontend.parse.element.Identifier;
import org.simplement.compiler.frontend.parse.element.LabeledCodeBlock;

import java.util.ArrayList;
import java.util.List;

public class SplObject extends LabeledCodeBlock {
    private final List<SplFunction> functions;
    private final List<SplObject> classes;
    private final List<Identifier> fields;

    public SplObject(Identifier identifier) {
        super(identifier);
        this.functions = new ArrayList<>();
        this.classes = new ArrayList<>();
        this.fields = new ArrayList<>();
    }

    public void appendFunction(SplFunction function) {
        functions.add(function);
    }

    public void appendClass(SplObject clazz) {
        classes.add(clazz);
    }

    public void appendField(Identifier field) {
        fields.add(field);
    }
}
