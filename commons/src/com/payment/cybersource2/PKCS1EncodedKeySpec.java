package com.payment.cybersource2;

import java.io.IOException;
import java.math.BigInteger;
import java.security.spec.RSAPrivateCrtKeySpec;
/**
 * Created by Vivek on 11/24/2020.
 */
public class PKCS1EncodedKeySpec
{
    private RSAPrivateCrtKeySpec keySpec;

    public PKCS1EncodedKeySpec(byte[] keyBytes) throws IOException {
        decode(keyBytes);
    }

    public RSAPrivateCrtKeySpec getKeySpec() {
        return keySpec;
    }

    private void decode(byte[] keyBytes) throws IOException {

        DerParser parser = new DerParser(keyBytes);

        Asn1Object sequence = parser.read();
        if (sequence.getType() != DerParser.SEQUENCE) {
            throw new IOException("Invalid DER: not a sequence"); //$NON-NLS-1$
        }
        parser = sequence.getParser();

        parser.read();
        BigInteger modulus = parser.read().getInteger();
        BigInteger publicExp = parser.read().getInteger();
        BigInteger privateExp = parser.read().getInteger();
        BigInteger prime1 = parser.read().getInteger();
        BigInteger prime2 = parser.read().getInteger();
        BigInteger exp1 = parser.read().getInteger();
        BigInteger exp2 = parser.read().getInteger();
        BigInteger crtCoef = parser.read().getInteger();

        keySpec = new RSAPrivateCrtKeySpec(
                modulus, publicExp, privateExp, prime1, prime2,
                exp1, exp2, crtCoef);
    }
}
