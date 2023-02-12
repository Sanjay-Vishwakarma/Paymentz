<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI,
                 org.owasp.esapi.errors.ValidationException"%>
<%@ page import="java.util.*" %>
<%@ include file="functions.jsp"%>

<%@ include file="index.jsp"%>

<%--
  Created by IntelliJ IDEA.
  User: WAHEED
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

    <title></title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>
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
            <br><br>
            <form action="/icici/servlet/ListAgentDetails?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <%
                    String agentid=(String)request.getParameter("agentid");
                    String str="ctoken=" + ctoken;
                    if (agentid != null) str = str + "&agentid=" + agentid;
                    int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                    int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);



                %>
                <table  align="center" width="80%" cellpadding="2" cellspacing="2" style="margin-left:60px; ">
                    <tr>
                        <td>
                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="30%" class="textb" >Agent Id</td>
                                    <td width="0%" class="textb"></td>
                                    <td width="25%" class="textb">
                                        <input  maxlength="15" type="text" name="agentid"  value=""  class="txtbox">
                                    </td>

                                    <td width="2%" class="textb"></td>
                                    <td width="45%" class="textb">Agent Name</td>
                                    <td width="10%" class="textb"></td>
                                    <td width="48%" class="textb">
                                        <input  maxlength="15" type="text" name="agentname"  value="" class="txtbox">

                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="20%" class="textb" ></td>
                                    <td width="0%" class="textb"></td>
                                    <td width="22%" class="textb"></td>

                                    <td width="2%" class="textb"></td>
                                    <td width="45%" class="textb" ></td>
                                    <td width="8%" class="textb"></td>
                                    <td width="40%" class="textb"></td>

                                </tr>

                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="20%" class="textb" ></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="22%" class="textb">

                                    </td>

                                    <td width="10%" class="textb">&nbsp;</td>
                                    <td width="40%" class="textb" ></td>
                                    <td width="10%" class="textb"></td>
                                    <td width="50%"  align="center">
                                        <button type="submit" class="buttonform" >
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
                                        </button>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="20%" class="textb" ></td>
                                    <td width="0%" class="textb"></td>
                                    <td width="22%" class="textb"></td>

                                    <td width="2%" class="textb"></td>
                                    <td width="45%" class="textb" ></td>
                                    <td width="8%" class="textb"></td>
                                    <td width="40%" class="textb"></td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </form>

        </div>
    </div>
</div>

<!-- TODO: Add .java to fatch record of agent -->

<div class="reporttable">
<%  Hashtable hash = (Hashtable)request.getAttribute("transdetails");

    Hashtable temphash=null;
    int records=0;
    int totalrecords=0;


    String errormsg=(String)request.getAttribute("message");
    if(errormsg!=null)
    {
        out.println("<center><font class=\"textb\">"+errormsg+"</font></center>");
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
            <td valign="middle" align="center" class="th0">Merchant Id</td>
            <td valign="middle" align="center" class="th0">Merchant Name</td>
            <td valign="middle" align="center" class="th0">Company Name</td>
            <td valign="middle" align="center" class="th0">Company Email</td>

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
                out.println("<td align=center "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("memberid"))+"<input type=\"hidden\" name=\"memberid\" value=\""+temphash.get("memberid")+"\"></td>");
                out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("login"))+"<input type=\"hidden\" name=\"login\" value=\""+temphash.get("login")+"\"></td>");
                out.println("<td align=center "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("company_name"))+"<input type=\"hidden\" name=\"company_name\" value=\""+temphash.get("company_name")+"\"></td>");
                out.println("<td align=center "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("contact_emails"))+"<input type=\"hidden\" name=\"contact_emails\" value=\""+temphash.get("contact_emails")+"\"></td>");
                out.println("</tr>");
        }
    %>
    <thead>
    <tr>
        <td  class="th0" align="left" class=textb>Total Records: <%=totalrecords%></td>
        <td  class="th0" align="right" class=textb>Page No <%=pageno%></td>
        <td class="th0"></td>
        <td class="th0"></td>
        <td class="th0"></td>
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
                <jsp:param name="page" value="AgentMerchantDetails"/>
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


%>

<%
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>
</div>
</body>
</html>