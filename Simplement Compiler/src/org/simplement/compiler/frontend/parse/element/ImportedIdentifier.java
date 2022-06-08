package org.simplement.compiler.frontend.parse.element;

import org.simplement.compiler.generic.Type;

import java.util.Objects;

public class ImportedIdentifier extends Identifier {
    private Identifier imported;

    public ImportedIdentifier(String localName) {
        super(localName, null, null, 0, null);
        this.imported = null;
    }

    public void setImport(Identifier identifier) {
        imported = identifier;
    }

    @Override
    public String getLocalName() {
        return imported.getLocalName();
    }

    @Override
    public String getFullName() {
        return imported.getFullName();
    }

    @Override
    public Type getReturnType() {
        return imported.getReturnType();
    }

    @Override
    public int hashCode() {
        return imported == null ? Objects.hashCode(localName) : imported.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (imported == null ? other instanceof ImportedIdentifier && localName.equals(((ImportedIdentifier)other).localName) : imported.equals(other));
    }
}
