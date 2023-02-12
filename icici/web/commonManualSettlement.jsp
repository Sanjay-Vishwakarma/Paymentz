<%@ include file="index.jsp"%>
<%--
 Created by IntelliJ IDEA.
  User: Naushad
  Date: 11/14/15
  Time: 2:56 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<META content="text/html; charset=windows-1252" http-equiv=Content-Type>
<meta http-equiv="Expires" content="0">
<meta http-equiv="Pragma" content="no-cache">
<%
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
%>
<html>
<head>Common Manual Settlement</head>
<title>Common Integration> Common Manual Settlement</title>
<body>

<%
  session.setAttribute("submit","Manual Settlement");
%>
<form action="/icici/commonManualCheckSettlement.jsp?ctoken=<%=ctoken%>" method="post">
  <div class="row" style="margin-top: 0px">
    <div class="col-lg-12">
      <div class="panel panel-default" style="margin-top: 100px">
        <div class="panel-heading" >
          Common Manual Settlement
        </div>
        <% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
          if (com.directi.pg.Admin.isLoggedIn(session))
          {
        %>
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <table  align="center" width="80%" cellpadding="2" cellspacing="2" >
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
                    <input maxlength="15" type="text" name="trackingid" class="txtbox" value="">
                  </td>
                  <td  class="textb">&nbsp;</td>
                  <td  class="textb"></td>
                  <td  class="textb"></td>
                  <td  class="textb">
                    <button type="submit" class="buttonform" style="margin-left:40px; ">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Process
                    </button>
                  </td>
                </tr>
                <tr>
                  <td colspan="4">&nbsp;</td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
      </div>
    </div>
  </div>
</form>
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