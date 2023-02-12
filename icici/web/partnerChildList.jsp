<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="index.jsp" %>
<%@ include file="functions.jsp" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.payment.MultiplePartnerUtill" %>
<%@ page import="java.util.*" %>
<%--
  Created by IntelliJ IDEA.
  User: Naushad
  Date: 4/9/2019
  Time: 12:41 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <title>Partners Management> Partner's User Management </title>
    <script language="javascript">
        function DoReverse(ctoken)
        {
            if (confirm("Do you really want to Delete this User."))
            {
                return true;
            }
            else
                return false;
        }
    </script>

</head>
<body>

<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/autocomplete.js"></script>

<%
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>

<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                Partner's User Management
                <div style="float: right;">
                    <form action="/icici/partnerChildSignup.jsp?ctoken=<%=ctoken%>" method="POST">

                        <button type="submit" class="addnewmember" value="Add New Child Partner" name="submit">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add New User
                        </button>
                    </form>
                </div>
            </div>

            <form action="/icici/servlet/PartnerUserList?ctoken=<%=ctoken%>" method="post" name="forms">
                <br>
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <table align="center" width="80%" cellpadding="2" cellspacing="2" style="margin-left:60px; ">
                    <tr>
                        <td>

                            <%
                                Functions functions = new Functions();
                                String memberid = (String) request.getAttribute("memberid");
                                //String partnerId = nullToStr(request.getParameter("merchantid"));
                                String partnerId = Functions.checkStringNull(request.getParameter("partnerid"))==null?"":request.getParameter("partnerid");
                                String error = (String) request.getAttribute("error");
                                String success = (String) request.getAttribute("success");

                                if (request.getAttribute("error") != null)
                                {
                                    String message = (String) request.getAttribute("error");
                                    if (message != null)
                                        out.println("<center><font class=\"textb\">" + message + "</font></center><br/><br/>");
                                }
                                if (functions.isValueNull(success))
                                {
                                    out.println("<center><font class=\"textb\">" + success + "</font></center><br/><br/>");
                                }

                            %>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="20%" class="textb">Partner Id</td>
                                    <td width="0%" class="textb"></td>
                                    <td width="22%" class="textb">

                                        <input name="partnerid" id="pid1"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(partnerId)%>"
                                               class="form-control" autocomplete="on">
                                    </td>

                                    <td width="10%" class="textb">&nbsp;</td>
                                    <td width="40%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb">
                                        <button type="submit" class="buttonform" style="margin-left:40px; ">
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
                                        </button>
                                    </td>
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

    <br>
    <%
        int records = 0;
        Hashtable temphash = null;
        Hashtable detailHash = (Hashtable) request.getAttribute("detailHash");
        if (detailHash != null && (detailHash.size() != 0 && detailHash.size() != 1))
        {
            detailHash = (Hashtable) request.getAttribute("detailHash");
            records = Integer.parseInt((String) detailHash.get("records"));
        }
        if (records > 0)
        {
    %>

    <table align=center width="50%" class="table table-striped table-bordered table-hover table-green dataTable">
        <thead>
        <tr>
            <td valign="middle" align="center" class="th0">Sr no</td>
            <%--<td valign="middle" align="center" class="th0">Member Id</td>--%>
            <td valign="middle" align="center" class="th0">User Name</td>
            <td valign="middle" align="center" class="th0">Contact Email</td>
            <td valign="middle" align="center" class="th0" colspan="4">Action</td>
        </tr>
        </thead>
            <%
        String style="class=td1";
        String ext="light";

        for(int i = 1;i<=records;i++)
        {
            String id=Integer.toString(i);

          int srno=i+ records;

            if(i%2==0)
            {
                style="class=tr1";
                ext="dark";
            }
            else
            {
                style="class=tr0";
                ext="light";
            }

        temphash=(Hashtable)detailHash.get(id);


          out.println("<tr>");
          out.println("<td align=\"center\" "+style+">&nbsp;"+i+ "</td>");
          out.println("<td align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("login"))+"<input type=\"hidden\" name=\"login\" value=\""+temphash.get("login")+"\"></td>");
          out.println("<td align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("contact_emails"))+"<input type=\"hidden\" name=\"emailaddress\" value=\""+temphash.get("contact_emails")+"\"></td>");
          out.println("<td align=\"center\""+style+">");
          out.println("<form action=\"/icici/servlet/EditPartnerUserList?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"partnerid\" value=\""+ESAPI.encoder().encodeForHTML((String)request.getAttribute("partnerid"))+"\"><input type=\"hidden\" name=\"login\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("login"))+"\"><input type=\"submit\" name=\"submit\" value=\"View\" class=\"gotoauto\"><input type=\"hidden\" name=\"action\" value=\"view\"></form></td>");
          out.println("<td align=\"center\""+style+"><form action=\"/icici/servlet/EditPartnerUserList?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"partnerid\" value=\""+ESAPI.encoder().encodeForHTML((String)request.getAttribute("partnerid"))+"\"><input type=\"hidden\" name=\"login\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("login"))+"\"><input type=\"submit\" name=\"submit\" value=\"Edit\" class=\"gotoauto\"><input type=\"hidden\" name=\"action\" value=\"modify1\"></form></td>");
         // out.println("<td align=\"center\""+style+"><form action=\"/icici/servlet/EditPartnerUserList?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"partnerid\" value=\""+ESAPI.encoder().encodeForHTML((String)request.getAttribute("partnerid"))+"\"><input type=\"hidden\" name=\"login\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("login"))+"\"><input type=\"submit\" name=\"submit\" value=\"Delete\" class=\"gotoauto\"><input type=\"hidden\" name=\"action\" value=\"delete\"></form></td>");8
          out.println("<td align=\"center\" "+style+"><form action=\"/icici/servlet/EditPartnerUserList?ctoken="+ctoken+"\" method=\"POST\" name=\"formAction\" onSubmit=\"return DoReverse('"+ctoken+"')\"><input type=\"hidden\" name=\"partnerid\" value=\""+ESAPI.encoder().encodeForHTML((String) request.getAttribute("partnerid"))+"\"><input type=\"hidden\" name=\"login\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("login"))+"\"><input type=\"submit\" name=\"submit\" value=\"Delete\" class=\"gotoauto\"><input type=\"hidden\" name=\"action\" value=\"delete\"></form></td>");
          //out.println("<td align=\"center\" "+style+"><form action=\"/icici/partnerUserModuleAllocation.jsp?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"partnerid\" value=\""+ESAPI.encoder().encodeForHTML((String)request.getAttribute("partnerid"))+"\"><input type=\"hidden\" name=\"login\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("login"))+"\"><input type=\"hidden\" name=\"userid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("userid"))+"\"><input type=\"submit\" name=\"partnerUserModuleAllocation\" value=\"Module Allocation\"class=\"gotoauto\" width=\"100\">");
          out.println("<td align=\"center\""+style+"><form action=\"/icici/partnerUserModuleAllocation.jsp?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"partnerid\" value=\""+ESAPI.encoder().encodeForHTML((String)request.getAttribute("partnerid"))+"\"><input type=\"hidden\" name=\"login\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("login"))+"\"><input type=\"hidden\" name=\"userid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("userid"))+"\"><input type=\"submit\" name=\"submit\" value=\"Module Allocation\" class=\"gotoauto\"><input type=\"hidden\" name=\"action\" value=\"partnerUserModuleAllocation\"></form></td>");


          out.println("</tr>");
        }
        %>
            <%
      }
  else if (records==0)
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
<%!
    public static String nullToStr(String str)
    {
        if (str == null)
            return "";
        return str;
    }
%>