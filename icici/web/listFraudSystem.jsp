<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI,
                org.owasp.esapi.errors.ValidationException"%>
<%@ page import="java.util.*" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Sandip
  Date: 9/9/14
  Time: 1:44 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <title>Fraud Management> Fraud System Master Details> Fraud System Master</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>
<%
    if(request.getAttribute("message")!=null)
    {
        out.println(request.getAttribute("message"));
    }
%>

<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Fraud System Master
                <div style="float: right;">
                    <form action="/icici/addNewFraudSystem.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" value="Add New Fraud System" name="submit" class="addnewmember">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add New Fraud System
                        </button>
                    </form>
                </div>
            </div>

            <form action="/icici/servlet/ListFraudSystem?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <%
                    String str="ctoken=" + ctoken;
                    int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                    int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
                %>

                <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:1.5%;margin-right: 2.5% ">

                    <tr>
                        <td>
                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Fraud System ID</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input  maxlength="15" type="text" name="fsid"  value=""  class="txtbox">
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Fraud System Name</td>
                                    <td width="5%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input  maxlength="50" type="text" name="fsname"  value="" class="txtbox">
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

        String error=(String ) request.getAttribute("errormessage");
        if(error !=null)
        {
            out.println(error);
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
    <table align=center width="70%" class="table table-striped table-bordered table-green dataTable">
        <tr>
            <td valign="middle" align="center" class="th0">Sr No</td>
            <td valign="middle" align="center" class="th0">Fraud System Name</td>
            <td valign="middle" align="center" class="th0">Contact Person</td>
            <td valign="middle" align="center" class="th0">Contact Email</td>
            <td valign="middle" align="center" class="th0">URL</td>
            <td valign="middle" align="center" class="th0">Offline Fraud Check</td>
            <td valign="middle" align="center" class="th0">Online Fraud Check</td>
            <td valign="middle" align="center" class="th0">Is API Call Supported</td>
            <td valign="middle" align="center" class="th0" colspan="">Action</td>
        </tr>
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
                out.println("<td align=\"center\" "+style+">&nbsp;"+srno+ "</td>");
                out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("fsname"))+"</td>");
                out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("contact_person"))+"</td>");
                out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("contact_email"))+"</td>");
                out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("url"))+"</td>");
                out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("offline"))+"</td>");
                out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("online"))+"</td>");
                out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("api"))+"</td>");
                out.println("<td align=\"center\" "+style+">&nbsp;<form action=\"/icici/servlet/ActionFraudSystem?ctoken="+ctoken+"\" method=\"post\"><input type=\"hidden\" name=\"mappingid\" value=\""+temphash.get("fsid")+"\"><input type=\"hidden\" name=\"action\" value=\"modify\"><input type=\"submit\" class=\"gotoauto\" value=\"Modify\"></form></td>");
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
                <jsp:param name="page" value="ListFraudSystem"/>
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