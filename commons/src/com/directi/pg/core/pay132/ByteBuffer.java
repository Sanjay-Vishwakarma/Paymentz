package com.directi.pg.core.pay132;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Sep 13, 2012
 * Time: 8:42:08 AM
 * To change this template use File | Settings | File Templates.
 */


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ByteBuffer implements Serializable {
	private static final long serialVersionUID = 3644423043783409142L;

	public ByteBuffer(InputStream _source) {
		this(_source, 16384);
	}

	public ByteBuffer(InputStream _source, int _size) {
		mBuffer = getAllChunks(_source, _size);
	}

	public ByteBuffer(byte _buffer[]) {
		mBuffer = _buffer;
	}

	public ByteBuffer(String _buffer) {
		this(_buffer.getBytes());
	}

	private byte[] getChunk(InputStream _is, int _size) throws IOException {
		byte readbytes[] = new byte[_size];
		int size = _is.read(readbytes);
		byte result[] = new byte[size];
		System.arraycopy(readbytes, 0, result, 0, size);
		return result;
	}

	private byte[] getAllChunks(InputStream _is, int _size) {
		byte result[] = new byte[0];
		do
			try {
				byte chunk[] = getChunk(_is, _size);
				byte temp[] = new byte[result.length + chunk.length];
				System.arraycopy(result, 0, temp, 0, result.length);
				System.arraycopy(chunk, 0, temp, result.length, chunk.length);
				result = temp;
			} catch (Exception ex) {
				return result;
			}
		while (true);
	}

	public byte[] toByteArray() {
		return mBuffer;
	}

	public String toString() {
		return new String(toByteArray());
	}

	public InputStream toInputStream() {
		return new ByteArrayInputStream(toByteArray());
	}

	public List toList() {
		return toList(false);
	}

	public List toList(boolean _trim) {
		List result = new ArrayList();
		String as[] = toString().split("\n");
		int i = 0;
		for (int j = as.length; i < j; i++) {
			String s = as[i];
			if (_trim) {
				if (s != null && !s.equals(""))
					result.add(s);
			} else {
				result.add(s);
			}
		}

		return result;
	}

	public int getSize() {
		return mBuffer == null ? 0 : mBuffer.length;
	}

	private static final int SIZE = 16384;

	private byte mBuffer[];
}

