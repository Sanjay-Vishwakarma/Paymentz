<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="top.jsp"%>
<%@ include file="functions.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Kanchan
  Date: 21-01-2021
  Time: 18:02
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  session.setAttribute("submit","agentInterface");
%>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
    <title>New Agent Signup</title>
</head>
<body>
<%
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (partner.isLoggedInPartner(session))
  {
%>

<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">

      <div class="row reporttable">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>New Agent SingUp form</strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <div class="form-group col-md-4 has-feedback">
              <label style="text-align: center">
                The username(<%=ESAPI.encoder().encodeForHTML((String) request.getAttribute("username"))%>),this agent name  specified by you already exists for an agent.
              </label>
            </div>
            <br><br>

            <div class="form-group col-md-4 has-feedback">
              <label style="text-align: center">
                Please enter New Username.
              </label>
            </div>
            <br>
<%
              String errormsg = ESAPI.encoder().encodeForHTML((String) request.getAttribute("error"));
              if (errormsg != null)
              {
               out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;"+errormsg+"</h5>");
              }
%>
            <div class="widget-content padding">
              <div id="horizontal-form">
                <br>
            <form action="/partner/net/NewAgent?ctoken=<%=ctoken%>" method="post" name="form1" class="form-horizontal">


              <div class="form-group">
                <label class="col-sm-4 control-label">New Agent Login*</label>
                <div class="col-sm-6">
                  <input type="text" maxlength="100" class="form-control" value="" name="username">
                </div>
              </div>


              <input type="hidden" value="3" name="step">

              <div class="form-group col-md-6"></div>
              <div class="form-group col-md-4">
                <button type="submit" name="submit" class="btn btn-default" id="submit">
                  <i class="fa fa-save"></i>
                  Submit
                </button>
              </div>
            </form>
          </div>
         </div>
     </div>
    </div>
  </div>


      <%
        }
        else
        {
          response.sendRedirect("/partner/logout.jsp");
          return;
        }
      %>

    </div>
  </div>
</div>

</body>
</html>
