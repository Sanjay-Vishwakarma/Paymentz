//package com.payment.gold24.core.message;
//
///**
// * Created with IntelliJ IDEA.
// * User: Jignesh
// * Date: 11/1/12
// * Time: 11:22 AM
// * To change this template use File | Settings | File Templates.
// */
//
//import org.apache.axis.handlers.LogHandler;
//
//import java.io.ByteArrayOutputStream;
//
//import javax.xml.namespace.QName;
//import javax.xml.rpc.handler.GenericHandler;
//import javax.xml.rpc.handler.MessageContext;
//import javax.xml.rpc.handler.soap.SOAPMessageContext;
//
//public class TestHandler extends LogHandler
//{
//
//    private static final long serialVersionUID = -1088299597212448917L;
//    private String timeStamp;
//
//    public TestHandler()
//    {
//        timeStamp = Long.toString(System.currentTimeMillis());
//    }
//
//    @Override
//    public QName[] getHeaders()
//    {
//        return null;
//    }
//
//    @Override
//    public boolean handleFault(MessageContext context)
//    {
//        System.out.println(timeStamp + "_fault: " + getStringMessage(context));
//
//        return super.handleFault(context);
//    }
//
//    private String getStringMessage(MessageContext context)
//    {
//        String res = null;
//
//        try
//        {
//            SOAPMessageContext ctx = (SOAPMessageContext) context;
//
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            ctx.getMessage().writeTo(stream);
//            byte[] items = stream.toByteArray();
//
//            res = new String(items);
//        }
//        catch (Exception e)
//        {
//            // nothing - just ensuring the method will not throw an exception in case something is wrong.
//        }
//
//        return res;
//    }
//
//    @Override
//    public boolean handleRequest(MessageContext context)
//    {
//        System.out.println(timeStamp + "_request: " + getStringMessage(context));
//
//        return super.handleRequest(context);
//    }
//
//    @Override
//    public boolean handleResponse(MessageContext context)
//    {
//        System.out.println(timeStamp + "_response: " + getStringMessage(context));
//
//        return super.handleResponse(context);
//    }
//
//}
