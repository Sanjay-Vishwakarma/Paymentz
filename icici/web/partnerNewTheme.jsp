<%@ page import="com.directi.pg.Functions"%>
<%@ page import="com.directi.pg.Logger" %>
<%@ include file="functions.jsp"%>

<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Sagar
  Date: 3/9/15
  Time: 9:58 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title> Partner Default Theme</title>
</head>
<body>
<%!
  Logger logger=new Logger("partnerNewTheme");
%>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Add New Default Theme
        <div style="float: right;">
          <form action="/icici/partnerTheme.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="partnerTheme" name="submit" class="addnewmember">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Default Theme List
            </button>
          </form>
        </div>
        <%--<div style="float: right;">
            <form action="/icici/addNewAdminModule.jsp?ctoken=<%=ctoken%>" method="POST">
                <button type="submit" value="Module Master" name="submit" class="addnewmember">
                    <i class="fa fa-sign-in"></i>
                    &nbsp;&nbsp;Admin Module Master
                </button>
            </form>
        </div>--%>
      </div>
      <br>
      <form action="/icici/servlet/PartnerNewTheme?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <table align="center" width="65%" cellpadding="2" cellspacing="2">

          <tbody>
          <tr>
            <td>

              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                <tbody>
                <tr><td colspan="4">&nbsp;</td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb">New Default Theme</td>
                  <td width="5%" class="textb">:</td>
                  <td width="50%" class="textb">
                    <input maxlength="100" type="text" name="defaultTheme" class="txtbox" value="" style="width:186px">
                  </td>

                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb"></td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb">
                    <button type="submit" class="buttonform" value="Save">
                      <i class="fa fa-sign-in"></i>
                      &nbsp;&nbsp;Save
                    </button>
                  </td>
                </tr>

                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb"></td>
                </tr>

                </tbody>
              </table>

            </td>
          </tr>
          </tbody>
        </table>
      </form>
    </div>
  </div>
</div>
<%
  String message=(String)request.getAttribute("message");
  Functions functions=new Functions();
  if(functions.isValueNull(message))
  {
%>
<div class="reporttable">
  <%
    out.println(Functions.NewShowConfirmation("Result",message));
  %>
</div>
<%
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