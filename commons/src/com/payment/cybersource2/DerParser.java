package com.payment.cybersource2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
/**
 * Created by Vivek on 11/24/2020.
 */
public class DerParser
{
    public final static int UNIVERSAL = 0x00;
    public final static int APPLICATION = 0x40;
    public final static int CONTEXT = 0x80;
    public final static int PRIVATE = 0xC0;

    public final static int CONSTRUCTED = 0x20;

    public final static int ANY = 0x00;
    public final static int BOOLEAN = 0x01;
    public final static int INTEGER = 0x02;
    public final static int BIT_STRING = 0x03;
    public final static int OCTET_STRING = 0x04;
    public final static int NULL = 0x05;
    public final static int OBJECT_IDENTIFIER = 0x06;
    public final static int REAL = 0x09;
    public final static int ENUMERATED = 0x0a;
    public final static int RELATIVE_OID = 0x0d;

    public final static int SEQUENCE = 0x10;
    public final static int SET = 0x11;

    public final static int NUMERIC_STRING = 0x12;
    public final static int PRINTABLE_STRING = 0x13;
    public final static int T61_STRING = 0x14;
    public final static int VIDEOTEX_STRING = 0x15;
    public final static int IA5_STRING = 0x16;
    public final static int GRAPHIC_STRING = 0x19;
    public final static int ISO646_STRING = 0x1A;
    public final static int GENERAL_STRING = 0x1B;

    public final static int UTF8_STRING = 0x0C;
    public final static int UNIVERSAL_STRING = 0x1C;
    public final static int BMP_STRING = 0x1E;

    public final static int UTC_TIME = 0x17;
    public final static int GENERALIZED_TIME = 0x18;

    protected InputStream in;

    public DerParser(InputStream in) throws IOException {
        this.in = in;
    }

    public DerParser(byte[] bytes) throws IOException {
        this(new ByteArrayInputStream(bytes));
    }

    public Asn1Object read() throws IOException {
        int tag = in.read();

        if (tag == -1)
            throw new IOException("Invalid DER: stream too short, missing tag");

        int length = getLength();

        byte[] value = new byte[length];
        int n = in.read(value);
        if (n < length)
            throw new IOException("Invalid DER: stream too short, missing value");

        Asn1Object o = new Asn1Object(tag, length, value);

        return o;
    }

    private int getLength() throws IOException {

        int i = in.read();
        if (i == -1)
            throw new IOException("Invalid DER: length missing");

        if ((i & ~0x7F) == 0)
            return i;

        int num = i & 0x7F;

        if ( i >= 0xFF || num > 4)
            throw new IOException("Invalid DER: length field too big ("
                    + i + ")");

        byte[] bytes = new byte[num];
        int n = in.read(bytes);
        if (n < num)
            throw new IOException("Invalid DER: length too short");

        return new BigInteger(1, bytes).intValue();
    }
}
