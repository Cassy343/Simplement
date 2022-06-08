package org.simplement.compiler.frontend.parse.element;

public abstract class LabeledCodeBlock extends CodeBlock {
    protected final Identifier identifier;

    protected LabeledCodeBlock(Identifier identifier) {
        super();
        this.identifier = identifier;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other != null && LabeledCodeBlock.class.isAssignableFrom(other.getClass()) && identifier.equals(((LabeledCodeBlock)other).identifier);
    }
}
