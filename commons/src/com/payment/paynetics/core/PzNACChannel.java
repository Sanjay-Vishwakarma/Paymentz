package com.payment.paynetics.core;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.channel.NACChannel;

import java.io.IOException;

/**
 * Created by admin on 11/24/2017.
 */
class PzNACChannel extends NACChannel
{
    public PzNACChannel(String host, int port, ISOPackager p, byte[] TPDU) {
        super(host, port, p,TPDU);
    }
    protected void sendMessageLength(int len) throws IOException
    {
        len=len+2;
        this.serverOut.write(len >> 8);
        this.serverOut.write(len);
    }
    protected int getMessageLength() throws IOException, ISOException
    {
        byte[] b = new byte[2];
        serverIn.readFully(b,0,2);
        return (((((int)b[0])&0xFF) << 8) | (((int)b[1])&0xFF))-2;
    }
}

