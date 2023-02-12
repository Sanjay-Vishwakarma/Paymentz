<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2//EN">
<%@ page language="java" import="java.sql.Timestamp,com.opus.epg.sfa.java.*,java.net.*,javax.net.*,
                                 java.util.Enumeration,
                                 java.io.StringWriter,
                                 java.io.PrintWriter" session="false" isErrorPage="false" %>
<HTML>

<HEAD>
	<META HTTP-EQUIV="Content-Type" CONTENT="text/html;CHARSET=iso-8859-1">
<TITLE>TRANSACTION STATUS</TITLE>
</HEAD>

<BODY bgcolor='#83a1C6'>
<%
    Enumeration enum1=request.getParameterNames();
    while(enum1.hasMoreElements())
    {
        String  name=(String )enum1.nextElement();
        out.println(name+"="+request.getParameter(name)+"<BR>");

    }

    out.println("<br><br>");
	com.opus.epg.sfa.java.Merchant oMerchant 	= new com.opus.epg.sfa.java.Merchant();
    String EpgTxnId=request.getParameter("EpgTxnId");
    String rrn=request.getParameter("rrn");
    String txnId=request.getParameter("txnId");
    String authcode=request.getParameter("authcode");
    String mid=request.getParameter("mid");
    String currency=request.getParameter("currency");

	PostLib oPostLib	= new PostLib();

	oMerchant.setMerchantRelatedTxnDetails(
			mid
			,null
			,null
			,txnId
			,EpgTxnId
			,rrn
			,authcode
			,null
			, "POST"
			,currency
			, "req.Authorization"      //message type(req.Authorization,req.Refund)
			, request.getParameter("amount")
			,"GMT+05:30"
			, "Ext1"
			, "Ext2"
			, "Ext3"
			, "Ext4"
			, "Ext5a"
			);



    PGResponse	oPgResp	=	null;
    try
    {
        oPgResp = oPostLib.postRelatedTxn(oMerchant);
    }
    catch ( Exception e )
    {
        out.println("Exception");
        StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
        String mess = sw.toString();
        out.println(mess);

    }

    String AuthIdCode= oPgResp.getAuthIdCode();
    EpgTxnId= oPgResp.getEpgTxnId();
    String RespCode= oPgResp.getRespCode();
	String RespMessage= oPgResp.getRespMessage();
	String RRN= oPgResp.getRRN();
    String TxnId= oPgResp.getTxnId();
	String TxnType= oPgResp.getTxnType();

    out.println("TxnId="+TxnId+ "<br>");
    out.println("EpgTxnId="+EpgTxnId+ "<br>");
    out.println("RespCode="+RespCode+ "<br>");
	out.println("RespMessage="+RespMessage+ "<br>");
	out.println("AuthIdCode="+AuthIdCode+ "<br>");
    out.println("RRN="+RRN+ "<br>");

	out.println("TxnType="+TxnType+ "<br>");

    out.println("<form action=\"/testmoto/refund.jsp\" method=\"post\" >");
    out.println("<input type=\"text\" name=\"txnId\" value=\""+TxnId+"\">");
    out.println("<input type=\"text\" name=\"EpgTxnId\" value=\""+EpgTxnId+"\">");
    out.println("<input type=\"text\" name=\"rrn\" value=\""+RRN+"\">");
    out.println("<input type=\"text\" name=\"amount\" value=\""+request.getParameter("amount")+"\">");
    out.println("<input type=\"text\" name=\"authcode\" value=\""+AuthIdCode+"\">");
    out.println("<input type=\"hidden\" name=\"mid\" value=\""+mid+"\">");
    out.println("<input type=\"hidden\" name=\"currency\" value=\""+currency+"\">");
    out.println("<input type=\"Submit\" value=\"Refund\" >");
    out.println("</form>");



%>


</BODY>

</HTML>