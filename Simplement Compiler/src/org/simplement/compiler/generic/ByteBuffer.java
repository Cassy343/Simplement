package org.simplement.compiler.generic;

import java.util.Arrays;

/**
 * A container which holds an array of bytes. A <code>Buffer</code> should not
 * be seen as an implementation of <code>Set</code> or <code>List</code>, but it
 * should be looked at as a way to store any form of data as a byte blob (while
 * preserving order).
 */
public final class ByteBuffer {
    /**
     * The internal buffer.
     */
    protected byte[] data;
    /**
     * The buffer's size. This value may be static or dynamic.
     */
    protected int size;
    /**
     * The default size for the internal byte array of a buffer.
     */
    public static final int DEFAULT_BUFFER_SIZE = 64;
    /**
     * An alias to an empty internal buffer.
     */
    public static final byte[] EMPTY_SET = {};

    /**
     * Creates a new instance of <code>ByteBuffer</code> with a specified initial
     * capacity.
     *
     * @param capacity the initial capacity.
     */
    public ByteBuffer(int capacity) {
        data = new byte[capacity];
    }

    /**
     * Creates a new instance of <code>ByteBuffer</code> with a specified internal
     * value.
     *
     * @param data the internal value.
     */
    public ByteBuffer(byte[] data) {
        this.data = data;
        this.size = data.length;
    }

    /**
     * Creates a new instance of <code>ByteBuffer</code> with a specified internal
     * value. The initial value is the sub-array from index off, and including len
     * bytes.
     *
     * @param data the internal value.
     * @param off the starting index.
     * @param len the amount of bytes to copy.
     */
    public ByteBuffer(byte[] data, int off, int len) {
        this(Arrays.copyOfRange(data, off, off + len));
    }

    /**
     * Creates a new instance of <code>ByteBuffer</code> with the default capacity
     * (64 bytes).
     */
    public ByteBuffer() {
        this(DEFAULT_BUFFER_SIZE);
    }

    // check to make sure an index is valid
    private void checkIndex(int index) {
        if(index >= size)
            throw new IndexOutOfBoundsException("Index: " + index + ", Maximum index: " + (size - 1));
        else if(index < 0)
            throw new IndexOutOfBoundsException("Index less than 0: " + index);
    }

    /**
     * Ensures that the size of the internal buffer is at least as large as the
     * minimum capacity specified.
     *
     * @param minCapacity the minimum capacity that the buffer should be able to
     * hold.
     */
    protected void ensureCapacity(int minCapacity) {
        if(minCapacity - data.length > 0)
            grow(minCapacity);
    }

    /**
     * Expands the internal buffer to the size provided. This method does not affect
     * the size field.
     *
     * @param minCapacity the new size of the internal buffer.
     */
    protected void grow(int minCapacity) {
        if(minCapacity < 0)
            throw new OutOfMemoryError();
        data = Arrays.copyOf(data, minCapacity);
    }

    /**
     * Returns whether or not this byte buffer is empty. If the buffer is empty,
     * then the buffer's size is equal to <code>0</code>.
     *
     * @return whether or not this buffer is empty.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Ensures that this byte buffer can hold the specified amount of bytes plus
     * its current size. If the capacity specified is less than or equal to zero,
     * then this method does not modify the buffer.
     *
     * @param numBytes the number of bytes to allocate.
     */
    public void allocate(int numBytes) {
        if(numBytes <= 0)
            return;
        ensureCapacity(size + numBytes);
    }

    /**
     * Appends a byte to the end of the buffer. The last eight bits of the specified
     * integer are the only bits that are casted to a byte when appending.
     *
     * @param by the byte to append.
     */
    public void append(int by) {
        ensureCapacity(size + 1);
        data[size++] = (byte)(by & 0xFF);
    }

    /**
     * Appends an array of bytes to the end of the buffer.
     *
     * @param bytes the bytes to append.
     */
    public void appendAll(byte[] bytes) {
        ensureCapacity(size + bytes.length);
        System.arraycopy(bytes, 0, data, size, bytes.length);
        size += bytes.length;
    }

    /**
     * Get a byte from the buffer. If the specified index is out of bounds, then
     * an index out of bounds exception is thrown.
     *
     * @param index the index of the byte.
     * @return a byte from the buffer.
     */
    public byte get(int index) {
        checkIndex(index);
        return data[index];
    }

    /**
     * Gets a specified amount of bytes starting at the specified index. If the
     * specified index is out of bounds, then an index out of bounds exception is
     * thrown.
     *
     * @param index the starting index.
     * @param length the amount of bytes to get.
     * @return a specified amount of bytes starting at the specified index.
     */
    public byte[] getRange(int index, int length) {
        checkIndex(index + length - 1);
        byte[] range = new byte[length];
        System.arraycopy(data, index, range, 0, length);
        return range;
    }

    /**
     * Copies a range of bytes from this buffer, and returns that range as a buffer.
     *
     * @param index the starting index of the range.
     * @param length the amount of bytes to copy.
     *
     * @return the copied range of bytes as a ByteBuffer.
     */
    public ByteBuffer subBuffer(int index, int length) {
        checkIndex(index + length - 1);
        ByteBuffer buffer = new ByteBuffer(length);
        System.arraycopy(data, index, buffer.data, 0, length);
        return buffer;
    }

    /**
     * Reads bytes from the buffer until the byte <code>\u005Cu0000</code> (or the null
     * terminator) is reached. The null terminator is <em>not</em> included in the returned
     * byte array. This method is used for reading null-terminated strings.
     *
     * @param index the index to start reading at.
     *
     * @return a byte array from the specified index to the nearest null terminator.
     */
    public byte[] getNullTerminatedValue(int index) {
        ByteBuffer tempBuff = new ByteBuffer();
        for(int i = index;i < size;++ i) {
            if(data[i] == 0)
                break;
            tempBuff.append(data[i]);
        }
        return tempBuff.toArray();
    }

    /**
     * Returns the size of this buffer.
     *
     * @return the size of this buffer.
     */
    public int size() {
        return size;
    }

    /**
     * Deletes all the bytes stored in the buffer and sets the size to <code>0</code>.
     */
    public void clear() {
        data = EMPTY_SET;
        size = 0;
    }

    /**
     * Returns a copy of the internal value of this buffer.
     *
     * @return a copy of the internal value of this buffer.
     */
    public byte[] toArray() {
        return Arrays.copyOf(data, size);
    }

    /**
     * Returns a string representation of this byte buffer. The returned string has
     * the same format as <code>Arrays.toString(byte[])</code>, and is equivalent
     * to calling <code>Arrays.toString(buffer.toArray())</code>.
     *
     * @return a string representation of this byte buffer.
     */
    @Override
    public String toString() {
        if(data.length == 0 || size == 0)
            return "[]";
        StringBuilder sb = new StringBuilder(6 * size); // overestimated approximation
        sb.append('[');
        for(int i = 0;i < size;++ i) {
            sb.append(data[i]);
            if(i != size - 1)
                sb.append(", ");
        }
        return sb.append(']').toString();
    }
}