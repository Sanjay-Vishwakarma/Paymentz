<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.core.BankConnection" %>
<%@ page import="org.apache.commons.lang.time.StopWatch" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.util.ResourceBundle" %>
<%--
  Created by IntelliJ IDEA.
  User: Nishant
  Date: 7/7/14
  Time: 4:53 PM
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/xml" pageEncoding="UTF-8" %>
<%!
    Logger log = new Logger("systemstatuscheck.jsp");
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.pingdom");
%>
<%
    String rmtIpAddr= Functions.getIpAddress(request);
    String ipList= RB.getString("whitelist_ip");
    String[] ip=ipList.split(",");
    boolean isAllow=false;
    for(int i=0;i<ip.length;i++)
    {
        if(rmtIpAddr.trim().equals(ip[i].trim()))
        {
            isAllow = true;
        }
    }

    StringBuffer message= new StringBuffer();
    if(isAllow)
    {
        //merchant Interface check
        StopWatch watch = new StopWatch();
        PrintWriter pWriter = response.getWriter();
        watch.start();
        boolean isSuccess=true;
        BankConnection bankConnection=new BankConnection();
        try
        {
            String bankConnectionMessage= bankConnection.checkConnection();
            String[] temp=bankConnectionMessage.split("<br>");
            for(int i=1;i<temp.length;i++)
            {
                if(temp[i].contains("Failed"))
                {   isSuccess=false;
                    message.append(""+temp[i]+"<BR>");
                }
            }
        }
        catch (Exception e)
        {
            log.error("Error while checking bank connection",e);
        }
        watch.stop();
        if(!isSuccess)
        {
            out.println(message.toString());
        }
        else
        {
            pWriter.write("<?xml version=\"1.0\" ?>\n" +
                    "<pingdom_http_custom_check>\n" +
                    "\t<status>OK</status>\n" +
                    "\t<response_time>"+watch.getTime()+".000</response_time>\n" +
                    "</pingdom_http_custom_check>");
        }
    }
    else
    {
        out.println("Invalid Access "+rmtIpAddr);
    }
%>
