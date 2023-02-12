<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="top.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Supriya
  Date: 9/10/2015
  Time: 3:38 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title> Partner Merchant SignUp</title>
</head>
<body>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","memberlist");
  PartnerFunctions partner1=new PartnerFunctions();
%>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (partner.isLoggedInPartner(session))
  {
%>

<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">




      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">

            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Merchant Overview</strong></h2>
              <div class="additional-btn">
                <form action="/partner/partnermerchantsignup.jsp?ctoken=<%=ctoken%>" method="POST">
                  <button type="submit" class="btn btn-default" value="Add New Member" name="submit" style="/*width: 250px; */font-size:14px;">
                    <i class="fa fa-sign-in"></i>
                    &nbsp;&nbsp;Add New Member
                  </button>
                </form>
                <%--<a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>--%>
              </div>
            </div>

            <div class="widget-content padding">
              <div id="horizontal-form">

                <form action="/partner/net/MemberDetailList?ctoken=<%=ctoken%>" method="post" name="forms">
                  <input type="hidden" value="<%=ctoken%>" name="ctoken">
                  <%
                    String str="ctoken=" + ctoken;
                    if (request.getParameter("memberid") != null) str = str + "&memberid=" + request.getParameter("memberid");
                    int pageno=partner1.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                    int pagerecords=partner1.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);
                  %>
                  <%
                    if(request.getAttribute("error")!=null)
                    {
                      String message = (String) request.getAttribute("error");
                      if(message != null)
                        out.println("<center><font class=\"textb\"><b>"+message+"</b></font></center><br/><br/>");
                    }

                  %>


                  <div class="form-group col-md-4 has-feedback">
                    <label >Member Id</label>
                    <input  type="text" name="memberid" class="form-control">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label>Username</label>
                    <input  type="text" name="username" class="form-control" >
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label>Contact Person Name</label>
                    <input  type="text" name="contact_persons" class="form-control" >
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label >Contact Person Email</label>
                    <input  type="text" name="contact_emails" class="form-control">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label >Company Name</label>
                    <input  type="text" name="company_name" class="form-control" >
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

      <div class="row reporttable">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Merchant Information (New Login)</strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding">
              <div id="horizontal-form">
                <br>
                <%--<div class="table-responsive datatable">
                <div class="form foreground bodypanelfont_color panelbody_color">

                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="/*background-color: #ffffff;*/color:#34495e;padding-top: 5px;font-size: 18px;align: initial;"><i class="fa fa-th-large"></i>&nbsp;&nbsp;Merchant's User Signup</h2>
                    <hr class="hrform">
                </div>--%>
                <%--<%
                    String errormsg=(String)request.getAttribute("error");
                    if(errormsg!=null)
                    {
                            /*out.println("<center><font class=\"text\" face=\"arial\">"+errormsg+"</font></center>");*/
                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;"+errormsg+"</h5>");
                    }
                %>--%>
                <form action="/partner/net/PartnerMerchantSignUp?ctoken=<%=ctoken%>" method="post" name="form1" class="form-horizontal">


                  <div class="form-group">
                    <label class="col-sm-4 control-label">New Login*</label>
                    <div class="col-sm-6">
                      <input type="text" maxlength="100" class="form-control" value="" name="username">
                    </div>
                  </div>


                  <input type="hidden" value="3" name="step">

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