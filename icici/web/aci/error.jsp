<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page language="java" session="true"%>

<HTML>
<HEAD>
<TITLE>Colors of Success</TITLE>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META name="GENERATOR" content="IBM WebSphere Studio">
</HEAD>

<BODY>

<%
String ErrorTx = request.getParameter("ErrorText");
String payID = request.getParameter("paymentid");
out.println("Error Text ---:"+ ErrorTx);
out.println("payID ---:"+ payID);
out.println("session ---:"+ (String)session.getAttribute("paymentid"));

%>
</BODY>
</HTML>