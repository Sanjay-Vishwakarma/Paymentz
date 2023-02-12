package com.payment.cybersource2;

import java.io.IOException;
import java.math.BigInteger;
/**
 * Created by Vivek on 11/24/2020.
 */
public class Asn1Object
{
    protected final int type;
    protected final int length;
    protected final byte[] value;
    protected final int tag;

    public Asn1Object(int tag, int length, byte[] value) {
        this.tag = tag;
        this.type = tag & 0x1F;
        this.length = length;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

    public byte[] getValue() {
        return value;
    }

    public boolean isConstructed() {
        return  (tag & DerParser.CONSTRUCTED) == DerParser.CONSTRUCTED;
    }

    public DerParser getParser() throws IOException {
        if (!isConstructed())
            throw new IOException("Invalid DER: can't parse primitive entity"); //$NON-NLS-1$

        return new DerParser(value);
    }

    public BigInteger getInteger() throws IOException {
        if (type != DerParser.INTEGER)
            throw new IOException("Invalid DER: object is not integer");

        return new BigInteger(value);
    }

    public String getString() throws IOException {

        String encoding;

        switch (type) {

            case DerParser.NUMERIC_STRING:
            case DerParser.PRINTABLE_STRING:
            case DerParser.VIDEOTEX_STRING:
            case DerParser.IA5_STRING:
            case DerParser.GRAPHIC_STRING:
            case DerParser.ISO646_STRING:
            case DerParser.GENERAL_STRING:
                encoding = "ISO-8859-1";
                break;

            case DerParser.BMP_STRING:
                encoding = "UTF-16BE";
                break;

            case DerParser.UTF8_STRING:
                encoding = "UTF-8";
                break;

            case DerParser.UNIVERSAL_STRING:
                throw new IOException("Invalid DER: can't handle UCS-4 string");

            default:
                throw new IOException("Invalid DER: object is not a string");
        }

        return new String(value, encoding);
    }
}
