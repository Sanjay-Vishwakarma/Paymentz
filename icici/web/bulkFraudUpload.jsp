<%--
  Created by IntelliJ IDEA.
  User: Mahima
  Date: 6/25/18
  Time: 1:48 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ include file="index.jsp" %>
<html>
<head>
  <title>Bulk Fraud Upload</title>
  <script type="text/javascript" language="JavaScript">
    function check()
    {
      var  retpath = document.FIRCForm.File.value;
      var pos = retpath.lastIndexOf(".");
      var filename="";
      if (pos != -1)
        filename = retpath.substring(pos + 1);
      else
        filename = retpath;
      if (filename==('xls')) {
        return true;
      }
      alert('Please select a .xls file instead!');
      return false;
    }
  </script>
</head>
<body>
<%
  ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading">
        Bulk Fraud Upload
        <div style="float: right;">
          <form action="/icici/markFraudTransaction.jsp?ctoken=<%=ctoken%>" method="POST">
            <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
            <button type="submit" class="addnewmember">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Upload Fraud
            </button>
          </form>
        </div>
      </div>
      <form name = "FIRCForm" action="/icici/servlet/UploadFraudFile?ctoken=<%=ctoken%>" method="post" ENCTYPE="multipart/form-data" >
        <br>
          <%
          String errorMsg=(String) request.getAttribute("errorMsg");
          if(errorMsg !=null)
          {
            out.println("<center><font class=\"textb\"><b>"+errorMsg+"</b></font></center>");
          }
        %>
        <table  border="0" cellpadding="5" cellspacing="0" align="center">
          <tr>
            <td>
              <table  border="0" cellpadding="5" cellspacing="0" align="center">
                <tr>
                  <td colspan="6"></td>
                </tr>
                <tr>
                  <td colspan="6"></td>
                </tr>
                <tr>
                  <td colspan="6">&nbsp;</td>
                </tr>
                <tr>
                  <td class="textb" colspan="2" align="left"><b>Bank Fraud File:&nbsp;</b></td>
                  <td colspan="4" align="center"><input name="File" type="file" value="choose File"
                                                        style="width: 280px"></td>
                </tr>
                <tr>
                  <td colspan="6">&nbsp;</td>
                </tr>
                <tr>
                  <td colspan="6" align="center">
                    <button name="mybutton" type="submit" value="Upload" class="buttonform" onclick="return check()">Upload</button>
                  </td>
                </tr>
                <tr>
                  <td colspan="6">&nbsp;</td>
                </tr>
              </table>
        </table>
    </div>
  </div>
</div>
</form>
<%
    String message = (String) request.getAttribute("error");
    if (message != null)
    {
      out.println(Functions.ShowMessage("Message", message));
    }
  }
  else
  {
    response.sendRedirect("/icici/logout.jsp");
    return;

  }
%>
</body>
</html>