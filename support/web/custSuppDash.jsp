<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.CustomerSupport" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.Functions"%>
<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ include file="ietest.jsp" %>
<%!static Logger log=new Logger("logger1");%>
<%
    User user =  (User)session.getAttribute("ESAPIUserSessionKey");
    String ctoken= null;
    if(user!=null)
    {
        ctoken = user.getCSRFToken();

    }
    String buttonvalue=request.getParameter("submit");
    if(buttonvalue==null)
    {
        buttonvalue= (String) session.getAttribute("submit");
    }
    String name="";
    String Name="";
    String Email="";
    String Password="";
    Integer csId= (Integer) session.getAttribute("csId");
    String ContactNo="";
    String JoinDate="";
    Hashtable detail;
    if(CustomerSupport.isLoggedIn(session))
    {
        String supportUrl = ApplicationProperties.getProperty("COMPANY_SUPPORT_URL");
        detail= CustomerSupport.getCustomerSupportDetails(csId.intValue());
        name=detail.get("csName").toString();
        Email=detail.get("csEmail").toString();
        csId= (Integer) detail.get("csId");
        ContactNo=detail.get("csContactNumber").toString();
        JoinDate=detail.get("csCreationDate").toString();
//        Cookie cook[] = request.getCookies();
//        log.debug(" cookie length::"+cook.length);
//        String name1 = null;
//        String value = null;
//        if (cook != null)
//        {
//            for (int i = 0; i < cook.length; i++)
//            {
//                name1= cook[i].getName();
//                log.debug(" cookie name::"+name1);
//                value=cook[i].getValue();
//                log.debug("leaving  getCookie " + value);
//            }
//        }


%>
<head>
    <title>CUSTOMER SUPORT</title>

    <meta http-equiv='Content-Type' content='text/html; charset=utf-8' />
    <meta http-equiv='X-UA-Compatible' content='IE=edge,chrome=1' />

    <script src='/support/css/menu.js'></script>


    <script type='text/javascript' src='/support/css/menu_jquery.js'></script>

    <link rel="stylesheet" type="text/css" href="/support/css/styles123.css">

    <script src='/support/css/bootstrap.min.js'></script>

    <link href="/support/new_icons/icons/font-awesome/css/font-awesome.min.css" rel="stylesheet">


</head>
<%--top content--%>
<body class="bodybackground">
<div class="toppanel">
    <table class="toppaneltable"  >
        <tr>
            <td width="10%" style="background-color:#ffffff;" >

                <img src="/support/images/lg.jpg">

            </td>

            <td width="90%" class="agentname">&nbsp;&nbsp;&nbsp;

                <%

                    out.println(" Executive :");
                    out.println(" "+name);
                %>
            </td>
            <td width="13%" style="background-color:#2c3e50; " >




            </td>
            <%--logout panel--%>
            <td width="10%" >
                <div class="btn-group">
                    <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
                        <img src="/support/images/user-icon.png" >

                        <span class="caret"></span>
                    </button>
                    <ul class="dropdown-menu" role="menu">

                        <li><a href="<%=supportUrl%>" target="_new" style="font-size:10px;">Need Support
                            <img border="0"src="/support/images/help.gif"></a></li>
                        <li class="divider"></li>
                        <li><form action="/support/personalDetails.jsp?ctoken=<%=ctoken%>" class="menufont" style="background-color:#ffffff;width:100%; height:30px ">
                            <input type="hidden" value="<%=ctoken%>" name="ctoken">
                            <input type="submit" value="Personal Details" name="submit" class="logout">
                        </form>
                        </li>

                        <li><form action="/support/changePassword.jsp?ctoken=<%=ctoken%>" class="menufont" style="background-color:#ffffff;width:100%; height:30px">
                            <input type="hidden" value="<%=ctoken%>" name="ctoken">
                            <input type="submit" value="Change Password" name="submit" class="logout">
                        </form>
                        </li>

                        <li><form action="/support/logout.jsp?ctoken=<%=ctoken%>" class="menufont" style="background-color:#ffffff;width:100%; height:30px">
                            <input type="hidden" value="<%=ctoken%>" name="ctoken">
                            <input type="submit" value="Logout" name="submit" class="logout">
                        </form>
                        </li>

                    </ul>
                </div>
            </td>
            <%-- End logout panel--%>

        </tr>
    </table>
</div>
<div id='cssmenu'>

    <ul style="margin-top:12px;">
        <li>

            <form action="/support/transactionTrack.jsp?ctoken=<%=ctoken%>">
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <%if(buttonvalue==null)
                {%>

                <button type="submit" value="TRANSACTION DETAILS" name="submit"  class="button1">
                    <i class="fa fa-bar-chart-o" style="float:left;"></i>TransactionDetail</button>
                <% }else {if(buttonvalue.equals("TRANSACTION DETAILS"))
                {%>

                <button type="submit" value="TRANSACTION DETAILS" name="submit"  class="button1" style="background-color: #2c3e50">
                    <i class="fa fa-bar-chart-o" style="float:left;"></i>TransactionDetail</button>
                <%}else{%>

                <button type="submit" value="TRANSACTION DETAILS" name="submit"  class="button1">
                    <i class="fa fa-bar-chart-o" style="float:left;"></i>TransactionDetail</button>
                <%}}%>
            </form>


        </li>


        <li>
            <% if(buttonvalue==null){%>
            <button onclick="window.location.href='#'" id="clicker" >
                <i class="fa fa-rocket" style="float:left;"></i>Shipping Detail<span class="caret" style="float:right;"></span>

            </button>

            <ul style="background: #f5f5f5">
                <li>
                    <form action="/support/servlet/ShipmentDetails?ctoken=<%=ctoken%>">
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                        <button type="submit" value="UPDATE SHIPMENT DETAILS" name="submit" class="button3">
                            <i class="fa fa-angle-double-right" style="float:left;"></i>Update Shippment
                        </button>
                    </form>
                </li>
                <li style="margin-bottom:-12px ">
                    <form action="/support/servlet/ShipmentStatusDetails?ctoken=<%=ctoken%>" >
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                        <button type="submit" value="UPDATE SHIPMENT STATUS" name="submit" class="button3">
                            <i class="fa fa-angle-double-right" style="float:left;"></i>Update Shipment Status
                        </button>
                    </form>
                </li>

            </ul>
            <%}else{if("UPDATE SHIPMENT DETAILS".equals(buttonvalue)){%>
            <button onclick="window.location.href='#'" id="clicker" style="background-color: #2c3e50">
                <i class="fa fa-rocket" style="float:left;"></i>Shipping Detail<span class="caret" style="float:right;"></span>

            </button>

            <ul style="display: block;background:#f5f5f5">
                <li>
                    <form action="/support/servlet/ShipmentDetails?ctoken=<%=ctoken%>">
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                        <button type="submit" value="UPDATE SHIPMENT DETAILS" name="submit" class="button3" style="background-color:#CCE5FF ">
                            <i class="fa fa-angle-double-right" style="float:left;"></i>Update Shippment
                        </button>
                    </form>
                </li>
                <li style="margin-bottom:-12px ">
                    <form action="/support/servlet/ShipmentStatusDetails?ctoken=<%=ctoken%>" >
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                        <button type="submit" value="UPDATE SHIPMENT STATUS" name="submit" class="button3">
                            <i class="fa fa-angle-double-right" style="float:left;"></i>Update Shipment Status
                        </button>
                    </form>
                </li>

            </ul>
            <%}else{if("UPDATE SHIPMENT STATUS".equals(buttonvalue)){%>
            <button onclick="window.location.href='#'" id="clicker" style="background-color: #2c3e50">
                <i class="fa fa-rocket" style="float:left;"></i>Shipping Detail<span class="caret" style="float:right;"></span>

            </button>
            <ul style="display: block;background:#f5f5f5" >
                <li>
                    <form action="/support/servlet/ShipmentDetails?ctoken=<%=ctoken%>">
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                        <button type="submit" value="UPDATE SHIPMENT DETAILS" name="submit" class="button3">
                            <i class="fa fa-angle-double-right" style="float:left;"></i>Update Shippment
                        </button>
                    </form>
                </li>
                <li style="margin-bottom:-12px ">
                    <form action="/support/servlet/ShipmentStatusDetails?ctoken=<%=ctoken%>" >
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                        <button type="submit" value="UPDATE SHIPMENT STATUS" name="submit" class="button3" style="background-color:#CCE5FF ">
                            <i class="fa fa-angle-double-right" style="float:left;"></i>Update Shipment Status
                        </button>
                    </form>
                </li>
            </ul>
            <%}else{%>
            <button onclick="window.location.href='#'" id="clicker" >
                <i class="fa fa-rocket" style="float:left;"></i>Shipping Detail<span class="caret" style="float:right;"></span>

            </button>

            <ul style="background:#f5f5f5">
                <li>
                    <form action="/support/servlet/ShipmentDetails?ctoken=<%=ctoken%>">
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                        <button type="submit" value="UPDATE SHIPMENT DETAILS" name="submit" class="button3" >
                            <i class="fa fa-angle-double-right" style="float:left;"></i>Update Shippment
                        </button>
                    </form>
                </li>
                <li style="margin-bottom:-12px ">
                    <form action="/support/servlet/ShipmentStatusDetails?ctoken=<%=ctoken%>" >
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                        <button type="submit" value="UPDATE SHIPMENT STATUS" name="submit" class="button3">
                            <i class="fa fa-angle-double-right" style="float:left;"></i>Update Shipment Status
                        </button>
                    </form>
                </li>
            </ul>
            <%}}}%>
        </li>
        <li>
            <form action="/support/callerDetails.jsp?ctoken=<%=ctoken%>">
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <%if(buttonvalue==null)
                {%>

                <button type="submit" value="CALLER DETAILS" name="submit"  class="button1">
                    <i class="fa fa-bar-chart-o" style="float:left;"></i>Caller Detail</button>
                <% }else {if(buttonvalue.equals("CALLER DETAILS"))
                {%>

                <button type="submit" value="CALLER DETAILS" name="submit"  class="button1" style="background-color: #2c3e50">
                    <i class="fa fa-bar-chart-o" style="float:left;"></i>Caller Detail</button>
                <%}else{%>

                <button type="submit" value="CALLER DETAILS" name="submit"  class="button1">
                    <i class="fa fa-bar-chart-o" style="float:left;"></i>Caller Detail</button>
                <%}}%>
            </form>
        </li>
    </ul>
</div>
</body>
<%}else {  if (!CustomerSupport.isLoggedIn(session))
{


%>
<html>
<head>
    <title>Merchant Administration Logout</title>
    <link rel="stylesheet" type="text/css" href="/support/style.css"/>
</head>

<body style="margin: 0px" text=#000000 vlink=#000000 alink=#000000 link=#000000 bgcolor=#ffffff leftmargin=0 topmargin=0
      marginwidth="0" marginheight="0">

<form action="/support/login.jsp" method=post>
    <br><br>

    <br>
    <table class=search border="0" cellpadding="0" cellspacing="0" width="50%" align=center valign="center">
        <tr>
            <td align="center">
                <img src="/support/images/logo.jpg" border="0">
            </td>
        </tr>
    </table>
    <br><br>
    <table class=search border="0" bgcolor="#F1EDE0" cellpadding="2" cellspacing="0" width="600"
           bordercolorlight="#000000" bordercolordark="#FFFFFF" align=center valign="center">
        <tr>
            <td bgcolor="#9A1305" class="label" align="left">&nbsp;&nbsp;CUSTOMER SUPPORT Module</td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr align="center">
            <td class="text">&nbsp;&nbsp;Your session has expired </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr align="center">
            <td class="text">&nbsp;&nbsp;Click <a href="/support/login.jsp" class="link">here</a> to go to the CUSTOMER SUPPORT login
                page </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
    </table>
</form>
</body>
</html>
<%
            return;
        }
    }
%>

</body>
<head>
    <meta charset="utf-8">
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <META HTTP-EQUIV="Expires" CONTENT="-1"></head>
</html>
