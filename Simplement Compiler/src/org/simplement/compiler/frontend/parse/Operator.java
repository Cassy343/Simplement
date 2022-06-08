package org.simplement.compiler.frontend.parse;

public enum Operator {
    ARRAY_ACCESS(16, 2, true),
    OBJECT_ACCESS(16, 2, true),
    CALL(16, 2, false),
    INCREMENT(15, 1, true),
    DECREMENT(15, 1, true),
    NEGATE(14, 1, false),
    LOGIC_NOT(14, 1, false),
    BIT_COMPLEMENT(14, 1, false),
    MULTIPLY(12, 2, true),
    DIVIDE(12, 2, true),
    MODULUS(12, 2, true),
    ADDITION(11, 2, true),
    SUBTRACTION(11, 2, true),
    BIT_LSHIFT(10, 2, true),
    BIT_RSHIFT(10, 2, true),
    BIT_URSHIFT(10, 2, true),
    LESS_THAN(9, 2, true),
    LESS_THAN_OR_EQU(9, 2, true),
    GREATER_THAN(9, 2, true),
    GREATER_THAN_OR_EQU(9, 2, true),
    EQUAL(8, 2, true),
    NOT_EQUAL(8, 2, true),
    BIT_AND(7, 2, true),
    BIT_XOR(6, 2, true),
    BIT_OR(5, 2, true),
    LOGIC_AND(4, 2, true),
    LOGIC_OR(3, 2, true),
    TERNARY(2, 3, false),
    ASSIGN(1, 2, false),
    IMPORT(0, 1, false),
    CLASS_DEF(0, 1, false),
    FOR(0, 1, false),
    WHILE(0, 1, false),
    FUNC_DEF(0, 1, false),
    IF(0, 1, false),
    PRINT(0, 1, false),
    PRINTLN(0, 1, false),
    RETURN(0, 1, false);

    private final int precedence;
    private final int operands;
    private final boolean leftToRight;

    public static final Operator[] VALUES = values();

    Operator(int precedence, int operands, boolean leftToRight){
        this.precedence = precedence;
        this.operands = operands;
        this.leftToRight = leftToRight;
    }

    public int getPrecedence() {
        return precedence;
    }

    public int getOperands() {
        return operands;
    }

    public boolean isLeftToRight() {
        return leftToRight;
    }
}
