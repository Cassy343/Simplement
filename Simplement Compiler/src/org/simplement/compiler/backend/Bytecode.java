package org.simplement.compiler.backend;

import org.simplement.compiler.generic.Primitive;

import static org.simplement.compiler.backend.BytecodeConstant.*;

public enum Bytecode {
    // These comments allows the tools program to know where to insert code
    // !BCT_MARKER_START 4
    HALT,
    CALL(ID, ID),
    GOTO(FILE_ADDRESS),
    DUP,
    STORE(INTERNAL_ADDRESS),
    LOAD(INTERNAL_ADDRESS),
    RETURN,
    RETURNV,
    PRINT,
    PRINTLN,
    NEG64,
    NEG32,
    NEG16,
    NEG8,
    ICONST_N1,
    ICONST_0,
    ICONST_1,
    ICONST_2,
    ICONST_3,
    ICONST_4,
    ICONST_5,
    FCONST_N1,
    FCONST_0,
    FCONST_1,
    DCONST_N1,
    DCONST_0,
    DCONST_1,
    LCONST(LONG),
    ICONST(INTEGER),
    JCONST(SHORT),
    BCONST(BYTE),
    DCONST(DOUBLE),
    FCONST(FLOAT),
    SCONST(STRING),
    ZTRUE,
    ZFALSE,
    ADD8,
    ADD16,
    ADD32,
    ADD64,
    FADD32,
    FADD64,
    SUB8,
    SUB16,
    SUB32,
    SUB64,
    FSUB32,
    FSUB64,
    MUL8,
    UMUL8,
    MUL16,
    UMUL16,
    MUL32,
    UMUL32,
    MUL64,
    UMUL64,
    FMUL32,
    FMUL64,
    DIV8,
    UDIV8,
    DIV16,
    UDIV16,
    DIV32,
    UDIV32,
    DIV64,
    UDIV64,
    FDIV32,
    FDIV64,
    MOD8,
    UMOD8,
    MOD16,
    UMOD16,
    MOD32,
    UMOD32,
    MOD64,
    UMOD64,
    FMOD32,
    FMOD64,
    INC8(INTERNAL_ADDRESS, LONG),
    INC16(INTERNAL_ADDRESS, INTEGER),
    INC32(INTERNAL_ADDRESS, SHORT),
    INC64(INTERNAL_ADDRESS, BYTE),
    FINC32(INTERNAL_ADDRESS, DOUBLE),
    FINC64(INTERNAL_ADDRESS, FLOAT),
    RSHIFT8,
    RSHIFT16,
    RSHIFT32,
    RSHIFT64,
    LSHIFT8,
    LSHIFT16,
    LSHIFT32,
    LSHIFT64,
    BFLIP8,
    BFLIP16,
    BFLIP32,
    BFLIP64,
    URSHIFT8,
    URSHIFT16,
    URSHIFT32,
    URSHIFT64,
    OR8,
    OR16,
    OR32,
    OR64,
    AND8,
    AND16,
    AND32,
    AND64,
    XOR8,
    XOR16,
    XOR32,
    XOR64,
    CMP8_EQU(FILE_ADDRESS),
    CMP16_EQU(FILE_ADDRESS),
    CMP32_EQU(FILE_ADDRESS),
    CMP64_EQU(FILE_ADDRESS),
    FCMP32_EQU(FILE_ADDRESS),
    FCMP64_EQU(FILE_ADDRESS),
    CMP8_NEQ(FILE_ADDRESS),
    CMP16_NEQ(FILE_ADDRESS),
    CMP32_NEQ(FILE_ADDRESS),
    CMP64_NEQ(FILE_ADDRESS),
    FCMP32_NEQ(FILE_ADDRESS),
    FCMP64_NEQ(FILE_ADDRESS),
    CMP8_GT(FILE_ADDRESS),
    UCMP8_GT(FILE_ADDRESS),
    CMP16_GT(FILE_ADDRESS),
    UCMP16_GT(FILE_ADDRESS),
    CMP32_GT(FILE_ADDRESS),
    UCMP32_GT(FILE_ADDRESS),
    CMP64_GT(FILE_ADDRESS),
    UCMP64_GT(FILE_ADDRESS),
    FCMP32_GT(FILE_ADDRESS),
    FCMP64_GT(FILE_ADDRESS),
    CMP8_GTE(FILE_ADDRESS),
    UCMP8_GTE(FILE_ADDRESS),
    CMP16_GTE(FILE_ADDRESS),
    UCMP16_GTE(FILE_ADDRESS),
    CMP32_GTE(FILE_ADDRESS),
    UCMP32_GTE(FILE_ADDRESS),
    CMP64_GTE(FILE_ADDRESS),
    UCMP64_GTE(FILE_ADDRESS),
    FCMP32_GTE(FILE_ADDRESS),
    FCMP64_GTE(FILE_ADDRESS),
    CMP8_LT(FILE_ADDRESS),
    UCMP8_LT(FILE_ADDRESS),
    CMP16_LT(FILE_ADDRESS),
    UCMP16_LT(FILE_ADDRESS),
    CMP32_LT(FILE_ADDRESS),
    UCMP32_LT(FILE_ADDRESS),
    CMP64_LT(FILE_ADDRESS),
    UCMP64_LT(FILE_ADDRESS),
    FCMP32_LT(FILE_ADDRESS),
    FCMP64_LT(FILE_ADDRESS),
    CMP8_LTE(FILE_ADDRESS),
    UCMP8_LTE(FILE_ADDRESS),
    CMP16_LTE(FILE_ADDRESS),
    UCMP16_LTE(FILE_ADDRESS),
    CMP32_LTE(FILE_ADDRESS),
    UCMP32_LTE(FILE_ADDRESS),
    CMP64_LTE(FILE_ADDRESS),
    UCMP64_LTE(FILE_ADDRESS),
    FCMP32_LTE(FILE_ADDRESS),
    FCMP64_LTE(FILE_ADDRESS);
    // !BCT_MARKER_END

    public static final Bytecode[] VALUES = values();

    private final BytecodeConstant[] parameters;
    public final byte code;

    Bytecode(BytecodeConstant... parameters) {
        this.parameters = parameters;
        this.code = (byte)ordinal();
    }

    public BytecodeConstant[] getParameters() {
        return parameters;
    }

    public static Bytecode forPrimitive(Bytecode baseCode, Primitive primitive) {
        int i = baseCode.ordinal();
        switch(primitive) {
            case LONG:
            case UNSIGNED_LONG:
                return VALUES[i + 3];
            case INTEGER:
            case UNSIGNED_INTEGER:
                return VALUES[i + 2];
            case SHORT:
            case UNSIGNED_SHORT:
                return VALUES[i + 1];
            case BYTE:
            case UNSIGNED_BYTE:
                return baseCode;
            case DOUBLE:
                return VALUES[i + 5];
            case FLOAT:
                return VALUES[i + 4];
            default:
                return baseCode;
        }
    }
}
