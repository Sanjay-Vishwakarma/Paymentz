<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI,
                 org.owasp.esapi.errors.ValidationException"%>
<%@ page import="java.util.*" %>
<%@ include file="functions.jsp"%>

<%@ include file="index.jsp"%>

<%--
  Created by IntelliJ IDEA.
  User: saurabh
  Date: 2/11/14
  Time: 3:34 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <title>Agent Management> Agent Master</title>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        String agentName=Functions.checkStringNull(request.getParameter("agentname"))==null?"":request.getParameter("agentname");
        String agentId=Functions.checkStringNull(request.getParameter("agentid"))==null?"":request.getParameter("agentid");
        String userName= Functions.checkStringNull(request.getParameter("login"))==null?"":request.getParameter("login");

        String str="";
        if(agentName!=null)str = str + "&agentname=" + agentName;
        else
            agentName="";
        if(agentId!=null)str = str + "&agentid=" + agentId;
        else
            agentId="";
        if (userName!=null)str = str + "&login=" + userName;
        else
            userName="";
%>

<!-- TODO: Add .java to fatch record of agent -->

<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Agent Master
                <div style="float: right;">
                    <form action="/icici/agentsignup.jsp?ctoken=<%=ctoken%>" method="POST">

                        <button type="submit" value="Add New Agent" name="submit" class="addnewmember">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add New Agent
                        </button>
                    </form>
                </div>
            </div>

            <form action="/icici/servlet/ListAgentDetails?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <%

                    str="ctoken=" + ctoken;

                    int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                    int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);



                %>
                <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:1.5%;margin-right: 2.5% ">

                    <tr>
                        <td>
                            <input type="hidden" id="ctoken1" value="<%=ctoken%>" name="ctoken">

                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">


                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Agent Id</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input name="agentid" id="agnt" value="<%=agentId%>" class="txtbox" autocomplete="on">
                                      <%--  <input  maxlength="15" type="text" name="agentid"  value=""  class="txtbox">--%>
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Agent Name</td>
                                    <td width="5%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input  type="text" name="agentname"  value="<%=agentName%>" class="txtbox">
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Username</td>
                                    <td width="5%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input  type="text" name="login"  value="<%=userName%>" class="txtbox">
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" ></td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <button type="submit" class="buttonform" >
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
                                        </button>
                                    </td>
                                    </td>

                                </tr>


                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>

                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>


            </form>

        </div>
    </div>
</div>
<div class="reporttable">
<%  Hashtable hash = (Hashtable)request.getAttribute("transdetails");

    Hashtable temphash=null;
    int records=0;
    int totalrecords=0;

    str = str + "&agentid=" + agentId;
    str = str + "&agentname=" + agentName;
    str= str + "&login=" + userName;

    String errormsg=(String)request.getAttribute("message");
    if(errormsg!=null)
    {
        out.println("<center><font class=\"textb\"><b>"+errormsg+"</b></font></center>");
    }
    String error= (String)request.getAttribute("errormessage");
    if (error!= null)
    {
        out.println("<center><font class=\"textb\"><b>"+error+"</b></font></center>");
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
    if(hash!=null)
    {
        hash = (Hashtable)request.getAttribute("transdetails");
    }
    if(records>0)
    {
%>
<table align=center width="50%" class="table table-striped table-bordered table-hover table-green dataTable">
     <thead>
        <tr>
            <td valign="middle" align="center" class="th0">Sr no</td>
            <td valign="middle" align="center" class="th0">Agent Id</td>
            <td valign="middle" align="center" class="th0">Agent Name</td>
            <td valign="middle" align="center" class="th0">Username</td>
            <td valign="middle" align="center" class="th0">Partner Name</td>
            <td valign="middle" align="center" class="th0" colspan = "2">Action</td>
            <td valign="middle" align="center" class="th0">Merchant Details</td>
            <td valign="middle" align="center" class="th0">Merchant Transaction Summary</td>
            </td>
        </tr>
     </thead>
    <%
        String style="class=td1";
        String ext="light";

        for(int pos=1;pos<=records;pos++)
        {
            String id=Integer.toString(pos);

            int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

            if(pos%2==0)
            {
                style="class=tr0";
                ext="dark";
            }
            else
            {
                style="class=tr1";
                ext="light";
            }

            temphash=(Hashtable)hash.get(id);
            out.println("<tr>");
            out.println("<td align=center "+style+">&nbsp;"+srno+ "</td>");
            out.println("<td align=center "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("agentId"))+"<input type=\"hidden\" name=\"agentId\" value=\""+temphash.get("agentId")+"\"></td>");
            out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("agentName"))+"<input type=\"hidden\" name=\"agentName\" value=\""+temphash.get("agentName")+"\"></td>");
            out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("login"))+"<input type=\"hidden\" name=\"login\" value=\""+temphash.get("login")+"\"></td>");
            out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("partnerName"))+"<input type=\"hidden\" name=\"partnerName\" value=\""+temphash.get("partnerName")+"\"></td>");
            out.println("<td align=center "+style+"><form action=\"/icici/servlet/ViewAgentDetails?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"agentid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("agentId"))+"\"><input type=\"submit\" name=\"submit\" class=\"gotoauto\" value=\"View\"><input type=\"hidden\" name=\"action\" value=\"View\"></form></td>");
            out.println("<td align=center "+style+"><form action=\"/icici/servlet/ViewAgentDetails?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"agentid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("agentId"))+"\"><input type=\"submit\" name=\"submit\" class=\"gotoauto\" value=\"Edit\"><input type=\"hidden\" name=\"action\" value=\"modify\"></form></td>");
            out.println("<td align=center "+style+"><form action=\"/icici/servlet/AgentMerchantDetails?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"agentid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("agentId"))+"\"><input type=\"submit\" name=\"submit\" class=\"gotoauto\" value=\"View Merchant\"></form></td>");
            out.println("<td align=center "+style+"><form action=\"/icici/servlet/AgentMerchantTransactionSummary?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"agentid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("agentId"))+"\"><input type=\"submit\" name=\"submit\" class=\"gotoauto\" value=\"View Transactions\"></form></td>");
            //out.println("</form>");
            out.println("</tr>");
        }
    %>
</table>
    <table align=center valign=top><tr>
        <td align=center>
            <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                <jsp:param name="pageno" value="<%=pageno%>"/>
                <jsp:param name="str" value="<%=str%>"/>
                <jsp:param name="page" value="ListAgentDetails"/>
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
        out.println(Functions.NewShowConfirmation("Sorry","No records found."));
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