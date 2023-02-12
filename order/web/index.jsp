<%@ page language="java" import="com.directi.pg.Functions,com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.core.BankConnection"%>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ include file="ietest.jsp" %>

<%String company = (String)session.getAttribute("company");%>

<%

    User user =  (User)session.getAttribute("Anonymous");

    String ctoken= null;

    if(user!=null)
    {
        ctoken = user.getCSRFToken();

    }

%>

<html>
<head>
    <title><%=company%> Customer Order Tracking System</title>

    <script src='/order/css/menu.js'></script>
    <script type='text/javascript' src='/order/css/menu_jquery.js'></script>
    <link rel="stylesheet"href="/order/css/styles123.css" type="text/css">
    <script src='/order/css/bootstrap.min.js'></script>
    <link href="/order/css/icons/font-awesome/css/font-awesome.min.css" rel="stylesheet">

    <SCRIPT LANGUAGE="JavaScript">
        function popUp(url)
        {
            sealWin = window.open(url, "win", 'toolbar=no,location=no,directories=no,status=yes,menubar=yes,scrollbars=yes,resizable=yes,width=500,height=550');
            self.name = "mainWin";
        }
        function check()
        {
            var firstfour = document.form1.firstfour.value;
            var secondfour= document.form1.secondfour.value;
            var thirdfour= document.form1.thirdfour.value;
            var fourthfour= document.form1.fourthfour.value;
            if(firstfour.length==0 || secondfour.length==0 || thirdfour.length==0 || fourthfour.length==0 )
            {
                alert("Enter valid ccnum");
                return false;
            }

            if(firstfour.length<4)
            {

                alert("Enter first four of ccnum");
                return false;
            }
            if(secondfour.length<4)
            {

                alert("Enter second four of ccnum");
                return false;
            }
            if(thirdfour.length<4)
            {

                alert("Enter third four of ccnum");
                return false;
            }
            if(fourthfour.length<4)
            {

                alert("Enter fourth four of ccnum");
                return false;
            }
        }
    </SCRIPT>
</head>

<body class="bodybackground">

<br>
<table class=search border="0" cellpadding="2" cellspacing="0" width="50%" align=center valign="center">
    <tr>
        <td align="center">
            <img src="/images/order/<%=session.getAttribute("logo")%>" border="0">
        </td>
    </tr>
</table>
<br>

<%--
<p class="textb" align="center"><%=company%> Customer Order Tracking System</p>
--%>
<%!
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.pingdom");
%>
<%
    String key=null;
    String compareKey="8XoVeGwxOlnkp9dgnvmg==";
    StringBuffer message= new StringBuffer();
    boolean isSuccess=true;
    if(request.getParameter("md5hash")!=null)
    {
        key=request.getParameter("md5hash");
        if(compareKey.equals(key))
        {
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

            if(isAllow)
            {
                BankConnection bankConnection=new BankConnection();
                String bankConnectionMessage= bankConnection.checkConnection().toString();
                String[] temp=bankConnectionMessage.split("<br>");
                for(int i=1;i<temp.length;i++)
                {
                    if(temp[i].contains("Failed"))
                    {   isSuccess=false;
                        message.append(""+temp[i]+"<BR>");
                    }
                }
                if(isSuccess)
                {
                    out.println("^BANK_CONNECTION_SUCCESSFUL~");
                }
                else
                {
                    out.println("^BANK_CONNECTION_FAILED:"+message.toString()+"~");
                }
            }

        }
    }
%>
<p>
<table border="0" width="100%" align="center">
    <tr>
        <td>&nbsp;&nbsp;</td>
        <td class="textb" style="font-weight: bold;font-size:13px; "><center>Welcome to the <%=company%> Order Tracking System.</center></td>
    </tr>
    <tr>
        <td>&nbsp;</td>
        <td></td>
    </tr>
    <tr>
        <td>&nbsp;&nbsp;</td>
        <td class="textb"><center><%=company%> serves as the Payment Gateway Service for a large number of Merchants
            worldwide.
            To find out details about the amount charged to your card and what you purchased with that amount,
            please follow the steps below</center></td>
    </tr>
</table>
</p>
<hr class="hrnew"><br>
<%
    String trackingId="";
    String amount="";
    Functions functions=new Functions();
    if(functions.isValueNull(request.getParameter("trackingid")))
    {
        trackingId=request.getParameter("trackingid");
    }
    if(functions.isValueNull(request.getParameter("amount")))
    {
        amount=request.getParameter("amount");
    }
%>
<form action="/order/servlet/OrderDetail?ctoken=<%=ctoken%>" method=post name="form"  >
    <input id="ctoken1" name="ctoken" type="hidden" value="<%=ctoken%>" >

    <table  align="center" width="40%" cellpadding="1" cellspacing="1" border="1" bgcolor="#ffffff">
        <tr>
            <td>
                <table border="0" cellpadding="2" cellspacing="0" width="100%"
                       bordercolorlight="#E4D6C9" bordercolordark="#E4D6C9" align=center valign="center">
                    <tr>
                        <td bgcolor="#007ACC" colspan="2" class="texthead">Customer Order Details</td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                        <td class="textb">
                            <%
                                String action=request.getParameter("action");

                                if ("E".equals(action))
                                {
                                    out.println("*Invalid TrackingID.");
                                }
                                if ("F".equals(action))
                                {
                                    out.println("*Invalid Amount.");
                                }

                            %></td>
                    </tr>

                    <tr>
                        <td class="textb" align="right">Tracking Id</td>
                        <td align="center"><input type=text name="trackingid"  class="txtbox" maxlength="20" size="15"> </td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                        <td></td>
                    </tr>
                    <tr>
                        <td class="textb" align="right">Amount </td>
                        <td align="center"><input type=text name="amount"  class="txtbox" maxlength="20" size="15"> </td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                        <td></td>
                    </tr>
                    <tr>
                        <td colspan="2" align="center" >
                            <button type="submit" name="B1" class="buttonform">
                                <i class="fa fa-angle-double-right" class="iconbuttonform"></i>
                                &nbsp;&nbsp;Continue
                            </button>
                        </td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                        <td></td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</form>

<table  align="center">
    <tr><td class="textb">OR</td> </tr>
    <tr><td>&nbsp;</td> </tr>
</table>
<form action="/order/servlet/Order?ctoken=<%=ctoken%>" method=post name="form1"  >
    <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>" >

    <table  align="center" width="40%" cellpadding="1" cellspacing="1" border="1"  bgcolor="#ffffff">
        <tr>
            <td>
                <table border="0"  cellpadding="2" cellspacing="0" width="100%"
                       bordercolorlight="#E4D6C9" bordercolordark="#E4D6C9" align=center valign="center">
                    <tr>
                    <tr>
                        <td bgcolor="#007ACC" colspan="2" class="texthead">Customer Credit Card Details</td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                        <td></td>
                    </tr>
                    <tr>
                        <td class="textb" align="center">Credit Card Number</td>

                        <td align="center"><input type="hidden" name="ccnum" maxlength="16"  >
                            <input type="text" maxlength="4" name="firstfour" size="4" class="txtbox" style="width:70px">
                            <input type="Password" maxlength="4" name="secondfour" size="4" class="txtbox" style="width:70px" >
                            <input type="Password" maxlength="4" name="thirdfour" size="4" class="txtbox" style="width:70px">
                            <input type="text" maxlength="4" name="fourthfour" size="4" class="txtbox" style="width:70px">
                        </td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                        <td></td>
                    </tr>
                    <tr>
                        <td class="textb" align="center">Expiry Date</td>
                        <td align="left">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            <select name="month" class="txtbox" style="width:40px; ">

                                <option value="01">01</option>
                                <option value="02">02</option>
                                <option value="03">03</option>
                                <option value="04">04</option>
                                <option value="05">05</option>
                                <option value="06">06</option>
                                <option value="07">07</option>
                                <option value="08">08</option>
                                <option value="09">09</option>
                                <option value="10">10</option>
                                <option value="11">11</option>
                                <option value="12">12</option>

                            </select>
                            <span class="textb">/</span>
                            <select name="year" class="txtbox" style="width:80px; ">
                                <%
                                    Calendar cal = Calendar.getInstance();
                                    int currentYear = cal.get(Calendar.YEAR);
                                    for (int i = currentYear - 1; i <= currentYear + 15; i++)
                                    {
                                        if (i == currentYear)
                                        {
                                %>

                                <option value="<%=i%>" selected><%=i%></option>
                                <%
                                }
                                else
                                {
                                %>
                                <option value="<%=i%>"><%=i%></option>
                                <%
                                        }
                                    }
                                %>
                            </select>

                        </td>
                    <tr>
                        <td>&nbsp;</td>
                        <td></td>
                    </tr>
                    </tr>
                    <tr>
                        <td colspan="2" align="center" >
                            <button type="submit" name="B1" class="buttonform" onclick="return check()">
                                <i class="fa fa-angle-double-right" class="iconbuttonform"></i>
                                &nbsp;&nbsp;Continue
                            </button>
                        </td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                        <td></td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</form>

<table align=center valign="center">
    <%--<tr>
        <td align="center">
        <a target="_blank" href="http://abc.com/site/certificate/13835412899812182686"><IMG border=0 height=120 src="/merchant/images/Certificatelogo.png" width=210></a>
        </td>
    </tr>--%>
    <tr><td class="textb"> KINDLY KEEP JAVASCRIPT ENABLED IN YOUR BROWSER FOR SECURITY PURPOSES </td></tr>
</table>
</body>
</html>

