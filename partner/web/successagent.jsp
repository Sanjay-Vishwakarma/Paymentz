<%@ page import="org.owasp.esapi.ESAPI" %>
<%--
  Created by IntelliJ IDEA.
  User: Kanchan
  Date: 21-01-2021
  Time: 19:47
  To change this template use File | Settings | File Templates.
--%>
<%@ include file="functions.jsp"%>
<%@ include file="top.jsp"%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<%
    ctoken= ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (partner.isLoggedInPartner(session))
    {
%>
<body>
<br>
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
            <br><br>

            <div class="form-group col-md-4 has-feedback">
              <label style="text-align: center">
                Message
              </label>
            </div><br><br>

            <div class="form-group col-md-4 has-feedback">
              <label style="text-align: center">
                Thank you, <b><%=ESAPI.encoder().encodeForHTML((String) request.getAttribute("username"))%></b> for registering with
                Online Payment Gateway Service.
              </label>
            </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

</body>
<%
  }
  else
  {
    response.sendRedirect("/partner/logout.jsp");
    return;
  }
%>
</html>
