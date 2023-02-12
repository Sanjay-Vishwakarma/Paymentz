<%@ page import="org.owasp.esapi.ESAPI,
                 java.util.Hashtable,
                 com.directi.pg.Merchants"%>
<%@ page import="java.util.*"%>
<%@ include file="functions.jsp"%>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ include file="qwipitab.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: Jun 8, 2012
  Time: 8:27:44 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

</head>
<body>
<%
    session.setAttribute("submit","Details");
    if(buttonvalue==null)
    {
        buttonvalue=(String)session.getAttribute("submit");
    }
%>
<form action="/icici/servlet/QwipiDetailList?ctoken=<%=ctoken%>" method="post" name="F1" >
    <div class="row" style="margin-top: 0px">
        <div class="col-lg-12">
            <div class="panel panel-default"style="margin-top: 0px">
                <div class="panel-heading" >
                Qwipi Details
                </div>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>

<%

    String str="";

    int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
    int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

    String searchType = request.getParameter("searchType");
    String searchId = request.getParameter("SearchId");

    str = str + "ctoken=" + ctoken;
    //str = str + "&SRecords=" + pagerecords;



%>


    <input type="hidden" value="<%=ctoken%>" name="ctoken">
    <table  align="center" width="90%" cellpadding="2" cellspacing="2" style="margin-left: 12%;">
        <tr>
            <td>
                <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                    <tr>
                        <td colspan="4">&nbsp;</td>
                    </tr>

                    <tr>
                        <td class="textb">&nbsp;</td>
                        <td  class="textb" >Tracking Id</td>
                        <td  class="textb"></td>
                        <td  class="textb">
                            <input maxlength="15" type="text" name="trackingid"  value="" class="txtbox">


                        </td>

                        <td  class="textb">&nbsp;</td>
                        <td  class="textb"></td>
                        <td  class="textb"></td>
                        <td  class="textb">
                            <button type="submit" class="buttonform" style="margin-left:40px; ">
                                <i class="fa fa-clock-o"></i>
                                &nbsp;&nbsp;Search
                            </button>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            &nbsp;
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</div></div> </div>

</form>
<div class="reporttable">
<%


    String errormsg = (String) request.getAttribute("error");
    if(errormsg!=null)
    {
        out.println("<table align=\"center\"  >");
        out.println("<tr><td algin=\"center\" ><font face=\"arial\" color=\"red\"  size=\"2\">");
        errormsg = errormsg.replace("&lt;BR&gt;","<BR>");
        out.println(errormsg);
        out.println("</font>");
        out.println("</td></tr></table>");
    }
    else
    {
        errormsg = "";
    }


    if(request.getAttribute("transactionHistory")!=null)
    {
        Hashtable hash=(Hashtable)request.getAttribute("transactionHistory");

        Hashtable temphash=null;

        int hashsize=hash.size();

        int records=0;
        int totalrecords=0;

        String error=(String ) request.getAttribute("errormessage");
        if(error !=null)
        {
            out.println("<b>");
            out.println(error);
            out.println("</b>");
        }

        String currentblock=request.getParameter("currentblock");

        if(currentblock==null)
            currentblock="1";

        try
        {
            records=Integer.parseInt((String)hash.get("records"));
            totalrecords=Integer.parseInt((String)hash.get("totalrecords"));
        }
        catch(Exception ex)
        {

        }

        if(records>0)
        {
%>

<table align=center width="50%" class="table table-striped table-bordered table-hover table-green dataTable" >
    <thead>
        <tr>
            <td valign="middle" align="center" class="th0"><font color="#FFFFFF" >Sr No</font></td>
            <td valign="middle" align="center" class="th0"><font color="#FFFFFF" >Trackingid</font></td>
            <td valign="middle" align="center" class="th0"><font color="#FFFFFF" >Description</font></td>
            <td valign="middle" align="center" class="th0"><font color="#FFFFFF" >Action</font></td>
            <td valign="middle" align="center" class="th0"><font color="#FFFFFF" >status</font></td>
            <td valign="middle" align="center" class="th0"><font color="#FFFFFF" >Timestamp</font></td>
            <td valign="middle" align="center" class="th0"><font color="#FFFFFF" >Amount</font></td>
            <td valign="middle" align="center" class="th0"><font color="#FFFFFF" >Response date/time</font></td>
            <td valign="middle" align="center" class="th0"><font color="#FFFFFF" >Customer IpAddress</font></td>
            <td valign="middle" align="center" class="th0"><font color="#FFFFFF">Payment Order No</font></td>
            <td valign="middle" align="center" class="th0"><font color="#FFFFFF" >Remark</font></td>
            <td valign="middle" align="center" class="th0"><font color="#FFFFFF">Actionex Id</font></td>
            <td valign="middle" align="center" class="th0"><font color="#FFFFFF">Actionex Name</font></td>


        </tr>
    </thead>
    <%
        String style="class=td11";
        String ext="light";
        String previousicicitransid ="";
        String previousStyle="class=tr0";
        String previousExt="dark";
        String currentStyle="class=tr1";
        String currentExt="light";

        for(int pos=1;pos<=records;pos++)
        {
            String id=Integer.toString(pos);

            int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);



            temphash=(Hashtable)hash.get(id);
            out.println("<tr>");


            if(!previousicicitransid.equals((String) temphash.get("parentid")))
            {
                String tempStyle="";
                String tempExt="";

                previousicicitransid = (String) temphash.get("parentid");

                tempStyle = previousStyle;
                tempExt =  previousExt;

                previousStyle=currentStyle;
                previousExt=currentExt;

                currentStyle=tempStyle;
                currentExt=tempExt;

            }


            style=currentStyle;
            ext=currentExt;


            out.println("<td "+style+">&nbsp;"+srno+ "</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("parentid"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("description"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("action"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("status"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("timestamp"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("amount"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("responseDateTime"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("ipaddress"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("qwipiPaymentOrderNumber"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("responseRemark"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("actionexecutorid"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("actionexecutorname"))+"</td>");


        }

    %>
    <thead>
    <tr>
        <td valign="middle" align="center"class="th0" colspan="13" ><font color="#FFFFFF" >Total records <%=totalrecords%></font></td>
    </tr>
    </thead>
</table>

<table align=center valign=top><tr>
    <td align=center>


        <jsp:include page="page.jsp" flush="true">
            <jsp:param name="numrecords" value="<%=totalrecords%>"/>
            <jsp:param name="numrows" value="<%=pagerecords%>"/>
            <jsp:param name="pageno" value="<%=pageno%>"/>
            <jsp:param name="str" value="<%=str%>"/>
            <jsp:param name="page" value="QwipiDetailList"/>
            <jsp:param name="currentblock" value="<%=currentblock%>"/>
            <jsp:param name="orderby" value=""/>
        </jsp:include>

    </td>
</tr>
</table>
<%
        }
        else
        {
            out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
        }
    }
    else
    {
        out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
    }

%>
</div>
<%
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>

</body>
</html>