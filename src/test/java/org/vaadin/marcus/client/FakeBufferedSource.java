package org.vaadin.marcus.client;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Scanner;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Options;
import okio.Sink;
import okio.Timeout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FakeBufferedSource implements BufferedSource {

    private final Scanner scanner;

    public FakeBufferedSource(String input) {
        scanner = new Scanner(input);
    }

    @NotNull @Override public Buffer getBuffer() {
        return null;
    }

    @NotNull @Override public Buffer buffer() {
        return null;
    }

    @Override public boolean exhausted() throws IOException {
        return !scanner.hasNext();
    }

    @Override public void require(long l) throws IOException {

    }

    @Override public boolean request(long l) throws IOException {
        return false;
    }

    @Override public byte readByte() throws IOException {
        return 0;
    }

    @Override public short readShort() throws IOException {
        return 0;
    }

    @Override public short readShortLe() throws IOException {
        return 0;
    }

    @Override public int readInt() throws IOException {
        return 0;
    }

    @Override public int readIntLe() throws IOException {
        return 0;
    }

    @Override public long readLong() throws IOException {
        return 0;
    }

    @Override public long readLongLe() throws IOException {
        return 0;
    }

    @Override public long readDecimalLong() throws IOException {
        return 0;
    }

    @Override public long readHexadecimalUnsignedLong() throws IOException {
        return 0;
    }

    @Override public void skip(long l) throws IOException {

    }

    @NotNull @Override public ByteString readByteString() throws IOException {
        return null;
    }

    @NotNull @Override public ByteString readByteString(long l) throws IOException {
        return null;
    }

    @Override public int select(@NotNull Options options) throws IOException {
        return 0;
    }

    @NotNull @Override public byte[] readByteArray() throws IOException {
        return new byte[0];
    }

    @NotNull @Override public byte[] readByteArray(long l) throws IOException {
        return new byte[0];
    }

    @Override public int read(@NotNull byte[] bytes) throws IOException {
        return 0;
    }

    @Override public void readFully(@NotNull byte[] bytes) throws IOException {

    }

    @Override public int read(@NotNull byte[] bytes, int i, int i1) throws IOException {
        return 0;
    }

    @Override public void readFully(@NotNull Buffer buffer, long l) throws IOException {

    }

    @Override public long readAll(@NotNull Sink sink) throws IOException {
        return 0;
    }

    @NotNull @Override public String readUtf8() throws IOException {
        return "";
    }

    @NotNull @Override public String readUtf8(long l) throws IOException {
        return "";
    }

    @Nullable @Override public String readUtf8Line() throws IOException {
        return scanner.nextLine();
    }

    @NotNull @Override public String readUtf8LineStrict() throws IOException {
        return "";
    }

    @NotNull @Override public String readUtf8LineStrict(long l) throws IOException {
        return "";
    }

    @Override public int readUtf8CodePoint() throws IOException {
        return 0;
    }

    @NotNull @Override public String readString(@NotNull Charset charset) throws IOException {
        return "";
    }

    @NotNull @Override public String readString(long l, @NotNull Charset charset) throws IOException {
        return "";
    }

    @Override public long indexOf(byte b) throws IOException {
        return 0;
    }

    @Override public long indexOf(byte b, long l) throws IOException {
        return 0;
    }

    @Override public long indexOf(byte b, long l, long l1) throws IOException {
        return 0;
    }

    @Override public long indexOf(@NotNull ByteString byteString) throws IOException {
        return 0;
    }

    @Override public long indexOf(@NotNull ByteString byteString, long l) throws IOException {
        return 0;
    }

    @Override public long indexOfElement(@NotNull ByteString byteString) throws IOException {
        return 0;
    }

    @Override public long indexOfElement(@NotNull ByteString byteString, long l) throws IOException {
        return 0;
    }

    @Override public boolean rangeEquals(long l, @NotNull ByteString byteString) throws IOException {
        return false;
    }

    @Override public boolean rangeEquals(long l, @NotNull ByteString byteString, int i, int i1) throws IOException {
        return false;
    }

    @NotNull @Override public BufferedSource peek() {
        return null;
    }

    @NotNull @Override public InputStream inputStream() {
        return null;
    }

    @Override public int read(ByteBuffer dst) throws IOException {
        return 0;
    }

    @Override public boolean isOpen() {
        return false;
    }

    @Override public long read(@NotNull Buffer buffer, long l) throws IOException {
        return 0;
    }

    @NotNull @Override public Timeout timeout() {
        return null;
    }

    @Override public void close() throws IOException {

    }
}
