<%@ page import="com.directi.pg.Functions" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 12/13/13
  Time: 3:38 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.SystemError" %>
<%@ page import="com.directi.pg.fileupload.FileUploadBean" %>
<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ page import="com.directi.pg.CommonChargeback" %>
<%@ page import="com.directi.pg.AuditTrailVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="index.jsp" %>
<html>
<head>
  <title>Bulk ChargeBack Upload</title>
  <style>
  .report td{
    text-align: center;
    border: 1px;
  }
  </style>
</head>
<br><br><br><br><br><br><br><br>

<body>
<div class="qwipitab reporttable" style="margin-top: 0px">
  <%
    user = (User) session.getAttribute("ESAPIUserSessionKey");
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
      String val= (String) request.getAttribute("result");
      if (val != null)
      {
        String str="";
       /* str = str + "<table align=\"center\" width=\"90%\" cellpadding=\"2\" cellspacing=\"2\" ><tr><td>";*/

        str = str + "<table bgcolor=\"#ecf0f1\" width=\"100%\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\">";
        str = str + "<table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" align=\"center\" class=\"table table-striped table-bordered table-hover table-green dataTable\">";

        /*str = str + "<tr height=30>";
        str = str + "<td colspan=\"3\" bgcolor=\"#34495e\"  class=\"texthead\" align=\"center\"><font color=\"#FFFFFF\" size=\"2\" face=\"Open Sans,Helvetica Neue,Helvetica,Arial,Palatino Linotype', 'Book Antiqua, Palatino, serif\">  Result</font></td>";
        str = str + "</tr>";

        str = str + "<tr><td>&nbsp;</td></tr>";*/

        str = str + /*"<tr><td align=\"center\" class=\"textb\">" +*/ val /*+ "</td></tr>"*/;
        str = str + "</table></table>";// </tr></td> </table>
        out.println(str);
      }
      else
      {
        out.println(Functions.NewShowConfirmation("ERROR", "No records found."));
      }
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
