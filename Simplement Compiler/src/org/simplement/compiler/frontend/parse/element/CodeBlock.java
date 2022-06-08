package org.simplement.compiler.frontend.parse.element;

import org.simplement.compiler.frontend.parse.SplFunction;
import org.simplement.compiler.frontend.parse.SplObject;
import org.simplement.compiler.generic.CompilationException;

import java.util.ArrayList;
import java.util.List;

public abstract class CodeBlock {
    protected final List<Expression> expressions;

    protected CodeBlock() {
        this.expressions = new ArrayList<>();
    }

    public void appendExpression(Expression expression) {
        expressions.add(expression);
    }

    public void check() throws CompilationException {
        expressions.forEach(Expression::check);
    }

    public abstract void appendFunction(SplFunction function);

    public abstract void appendClass(SplObject clazz);

    public abstract void appendField(Identifier field);
}
