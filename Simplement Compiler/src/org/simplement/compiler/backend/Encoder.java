package org.simplement.compiler.backend;

import org.simplement.compiler.generic.ByteBuffer;
import org.simplement.compiler.generic.ByteHelper;

public class Encoder {
    private final ByteBuffer buffer;
    private final int currentVarId;

    public Encoder() {
        this.buffer = new ByteBuffer();
        this.currentVarId = 0;
    }

    public void write(byte b) {
        buffer.append(b);
    }

    public void write(Bytecode code) {
        buffer.append(code.code);
    }

    public void write(Bytecode code, Object... params) {
        buffer.append(code.code);
        BytecodeConstant[] p = code.getParameters();
        int len = p.length;
        for(int i = 0;i < len;++ i) {
            switch(p[i]) {
                case FILE_ADDRESS:
                case ID:
                case INTERNAL_ADDRESS:
                case INTEGER:
                case UNSIGNED_INTEGER:
                    buffer.appendAll(ByteHelper.convertInt((int)params[i]));
                    break;
                case BYTE:
                case UNSIGNED_BYTE:
                    buffer.append((byte)params[i]);
                    break;
                case LONG:
                case UNSIGNED_LONG:
                    buffer.appendAll(ByteHelper.convertLong((long)params[i]));
                    break;
                case SHORT:
                case UNSIGNED_SHORT:
                    buffer.appendAll(ByteHelper.convertShort((short)params[i]));
                    break;
                case DOUBLE:
                    buffer.appendAll(ByteHelper.convertDouble((double)params[i]));
                    break;
                case FLOAT:
                    buffer.appendAll(ByteHelper.convertFloat((float)params[i]));
                    break;
                case STRING:
                    buffer.appendAll(ByteHelper.convertString((String)params[i]));
                    break;
            }
        }
    }

    public void write(int i) {
        buffer.appendAll(ByteHelper.convertInt(i));
    }

    public void write(long l) {
        buffer.appendAll(ByteHelper.convertLong(l));
    }

    public void write(short s) {
        buffer.appendAll(ByteHelper.convertShort(s));
    }

    public void write(double d) {
        buffer.appendAll(ByteHelper.convertDouble(d));
    }

    public void write(float f) {
        buffer.appendAll(ByteHelper.convertFloat(f));
    }

    public void write(String s) {
        buffer.appendAll(ByteHelper.convertString(s));
    }
}
