<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.directi.pg.Functions" %>
<%--
  Created by IntelliJ IDEA.
  User: Ajit.k
  Date: 11/09/2019
  Time: 12:50 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="functions.jsp" %>
<%@ include file="top.jsp" %>
<%
    String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("partnername"));
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
%>

<html>
<head>
    <title><%=company%> | Partner SignUp</title>
</head>

<div class="content-page">
    <div class="content">
        <div class="page-heading">

            <div class="pull-right">
                <div class="btn-group">
                    <form action="/partner/partnerIcon.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button class="btn-lg"
                                style="background-color: #98A3A3; border-radius: 25px;color: white;padding: 12px 16px;font-size: 16px;cursor: pointer;">
                            Add New Partner Icon
                        </button>
                    </form>
                </div>
            </div>

            <div class="pull-right">
                <div class="btn-group">
                    <form action="/partner/partnerLogo.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button class="btn-lg"
                                style="background-color: #98A3A3; border-radius: 25px;color: white;padding: 12px 16px;font-size: 16px;cursor: pointer;">
                            Add New Partner Logo
                        </button>
                    </form>
                </div>
            </div>

            <div class="pull-right">
                <div class="btn-group">
                    <form action="/partner/partnersignup.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button class="btn-lg"
                                style="background-color: #98A3A3; border-radius: 25px;color: white;padding: 12px 16px;font-size: 16px;cursor: pointer;">
                            Add New Partner
                        </button>
                    </form>
                </div>
            </div>

            <br><br><br>


            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Partner Master</strong></h2>

                            <div class="" style="margin-top: 0px">
                                <div class="bg-info" style="text-align:center;">&nbsp;&nbsp;
                                    <%
                                        String partnerId = Functions.checkStringNull(request.getParameter("partnerId")) == null ? "" : request.getParameter("partnerId");
                                        String partnerName = Functions.checkStringNull(request.getParameter("partnerName")) == null ? "" : request.getParameter("partnerName");
                                        String partner_Name = (String) request.getAttribute("partner_Name");
                                        String partnerid = String.valueOf(session.getAttribute("partnerId"));
                                    %>
                                    New Partner
                                    <b><%=ESAPI.encoder().encodeForHTML((String) request.getAttribute("username"))%>
                                    </b> Added
                                </div>
                            </div>

                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                            <form action="/partner/net/PartnerDetailList?ctoken=<%=ctoken%>" method="post" name="forms">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                                <input type="hidden" value="<%=partnerid%>" name="superAdminId">

                                <%-- </div>--%>
                                <%--<div class="widget-content padding">
                                    <div id="horizontal-form">
                                        <form action="/partner/net/PartnerDetailList?ctoken=<%=ctoken%>" method="post"
                                              name="forms">
                                            <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                                            <input type="hidden" value="<%=partnerid%>" name="superAdminId">
                                            <%
                                                if (request.getAttribute("error") != null)
                                                {
                                                    String message = (String) request.getAttribute("error");
                                                    if (message != null)
                                                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + message + "</h5>");
                                                }
                                                if (request.getAttribute("success") != null)
                                                {
                                                    String success = (String) request.getAttribute("success");
                                                    if (success != null)
                                                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + success + "</h5>");
                                                }
                                            %>--%>

                                <%--<input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">--%>

                                <div class="ui-widget form-group col-md-4 has-feedback">
                                    <label style="color: #5b5b5b;">Partner ID</label>
                                    <input name="partnerId" id="mid" value="<%=partnerId%>"
                                           class="form-control" autocomplete="on">
                                </div>

                                <div class="form-group col-md-4 has-feedback">
                                    <label style="color: #5b5b5b;">Partner ID</label>
                                    <input type="text" name="partnerName" class="form-control"
                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute(partnerName)%>">
                                </div>

                                <div class="form-group col-md-4">
                                    <label style="color: transparent;">Path</label>
                                    <button type="submit" class="btn btn-default" style="display:block;">
                                        <i class="fa fa-clock-o"></i>
                                        &nbsp;&nbsp;Search
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

</body>

</html>
