package org.simplement.compiler.frontend.parse;

import java.util.*;

import org.simplement.compiler.frontend.parse.element.Identifier;
import org.simplement.compiler.frontend.parse.element.ImportedIdentifier;

public class AbstractSyntaxTree {
    private final Identifier root;
    private final SplObject module;
    private final List<String> imports;
    private final Set<ImportedIdentifier> importedIdentifiers;
    private final Scope globalScope;
    private Scope currentScope;

    public AbstractSyntaxTree(String root) {
        this.root = new Identifier(root);
        this.module = new SplObject(this.root);
        this.imports = new ArrayList<>();
        this.importedIdentifiers = new HashSet<>();
        this.globalScope = new Scope(null);
        this.currentScope = this.globalScope;
    }

    public String getRoot() {
        return root.getFullName();
    }

    public Identifier getRootAsIdentifier() {
        return root;
    }

    public SplObject getModule() {
        return module;
    }

    public void putImportedIdentifier(ImportedIdentifier identifier) {
        importedIdentifiers.add(identifier);
    }

    public void scopeIn() {
        currentScope = currentScope.scopeIn();
    }

    public void scopeOut() {
        Scope scope = currentScope.scopeOut();
        currentScope = scope == null ? globalScope : scope;
    }

    public void putIdentifier(Identifier identifier) {
        currentScope.putIdentifier(identifier);
    }

    public Identifier getIdentifier(String name) {
        return currentScope.getIdentifier(name);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other != null && other instanceof AbstractSyntaxTree && root.equals(((AbstractSyntaxTree)other).root);
    }

    private static final class Scope {
        final Map<String, Identifier> identifiers;
        final Scope parent;
        final List<Scope> innerScopes;

        Scope(Scope parent) {
            this.identifiers = new HashMap<>();
            this.parent = parent;
            this.innerScopes = new ArrayList<>();
        }

        void putIdentifier(Identifier identifier) {
            identifiers.putIfAbsent(identifier.getLocalName(), identifier);
        }

        Identifier getIdentifier(String name) {
            Identifier identifier = identifiers.get(name);
            return identifier == null ? (parent == null ? null : parent.getIdentifier(name)) : identifier;
        }

        Identifier getIdentifierLocal(String name) {
            return identifiers.get(name);
        }

        Scope scopeIn() {
            Scope newScope = new Scope(this);
            innerScopes.add(newScope);
            return newScope;
        }

        Scope scopeOut() {
            return parent;
        }
    }
}
