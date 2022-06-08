package org.simplement.compiler.frontend.parse.element;

import org.simplement.compiler.backend.Encoder;
import org.simplement.compiler.frontend.lex.Token;
import org.simplement.compiler.frontend.parse.AbstractSyntaxTree;
import org.simplement.compiler.generic.*;

import java.util.List;
import java.util.Objects;

public class Identifier extends Expression {
    protected final String localName;
    private final String fullName;
    private Type type;

    public Identifier(String localName, Type type, List<Pair<Token, Object>> tokens, int line, AbstractSyntaxTree ast) {
        super(tokens, line, ast);
        this.localName = localName;
        this.fullName = ast.getRoot() + "." + localName;
        this.type = type;
    }

    public Identifier(String localName, List<Pair<Token, Object>> tokens, int line, AbstractSyntaxTree ast) {
        this(localName, null, tokens, line, ast);
    }

    public Identifier(String fullName) {
        super(null, 0, null);
        this.localName = fullName;
        this.fullName = fullName;
        this.type = null;
    }

    public String getLocalName() {
        return localName;
    }

    public String getFullName() {
        return fullName;
    }

    public void resolveInferredType(Type type) {
        if(Objects.equals(this.type, type))
            return;
        if(this.type == null)
            this.type = type;
        else if(Primitive.INTEGER.equals(this.type.primitive) && Primitive.DOUBLE.equals(this.type.primitive))
            this.type = type;
    }

    @Override
    public Type getReturnType() {
        return type;
    }

    @Override
    public String check() {
        return null;
    }

    @Override
    public void encode(Encoder encoder) {

    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fullName);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other != null && other instanceof Identifier && fullName.equals(((Identifier)other).getFullName());
    }
}
