<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="top.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Ajit.k
  Date: 11/09/2019
  Time: 12:50 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","partnersignup");
%>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title><%=company%> | Partner SignUp</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (partner.isLoggedInPartner(session))
  {
    String sendEmailNotification = "N";
    if(request.getAttribute("sendEmailNotification") != null){
      sendEmailNotification = (String) request.getAttribute("sendEmailNotification");
    }
%>

<div class="content-page">
  <div class="content">
    <div class="page-heading">

      <div class="row reporttable">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Partner Information (New Login)</strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding">
              <div id="horizontal-form">
                <br>
                <form action="/partner/net/NewPartner?ctoken=<%=ctoken%>" method="post" name="form1" class="form-horizontal">


                  <div class="form-group">
                    <label class="col-sm-4 control-label">New Login*</label>
                    <div class="col-sm-6">
                      <input type="text" maxlength="100" class="form-control" value="" name="username">
                    </div>
                  </div>


                  <input type="hidden" value="3" name="step">
                  <input type="hidden" value="<%=sendEmailNotification%>" name="sendEmailNotification">

                  <div class="form-group col-md-6"></div>
                  <div class="form-group col-md-4">
                    <button type="submit" name="submit" class="btn btn-default" id="submit" <%--style="background: rgb(126, 204, 173);"--%>>
                      <i class="fa fa-save"></i>
                      Submit
                    </button>
                  </div>
                </form>
                <%--</div>
                <div id="fade" class="black_overlay"></div>
                <div id="wrapper" class="forced">--%>
                <%

                %>
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