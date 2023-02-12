<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="top.jsp"%>

<%@ page
        import="com.directi.pg.Functions,com.directi.pg.TransactionEntry,com.logicboxes.util.ApplicationProperties,java.util.Calendar,java.util.Enumeration,
                 org.owasp.esapi.errors.ValidationException" %>
<%@ page import="com.payment.MultiplePartnerUtill" %>
<%@ page import="java.util.Hashtable" %>
<%--
  Created by IntelliJ IDEA.
  User: Jinesh
  Date: 1/5/2016
  Time: 12:41 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title>Partner Child Signup</title>
</head>
<body>
<%
  MultiplePartnerUtill multiplePartnerUtill = new MultiplePartnerUtill();
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","User Management");
  session.getAttribute("merchantid");
  //System.out.println("partnerid in jsp----"+session.getAttribute("merchantid"));
  if (partner.isLoggedInPartner(session))

  {
%>

<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">


      <%--<form action="/partner/partnerChildSignup.jsp?ctoken=<%=ctoken%>" name="form" method="post">
          <div class="pull-right">
              <div class="btn-group">


                  <%

                      Enumeration<String> aName=request.getParameterNames();
                      while(aName.hasMoreElements())
                      {
                          String name=aName.nextElement();
                          String value = request.getParameter(name);
                          if(value==null || value.equals("null"))
                          {
                              value = "";
                          }
                  %>
                  <input type=hidden name=<%=name%> value=<%=value%>>
                  <%
                      }

                  %>
                  <button class="btn-xs" type="submit" name="B2" style="background: transparent;border: 0;">
                      <img style="height: 35px;" src="/partner/images/goBack.png">
                  </button>


              </div>
          </div>
      </form>
      <br><br>--%>

      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">

            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Partner's Partner Master</strong>

              </h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="pull-right">
              <div class="btn-group">
                <%--<i class="fa fa-sign-in"></i>--%>
                <%--<img style="height: 35px;" src="/merchant/images/goBack.png">
                &lt;%&ndash;&nbsp;Go Back&ndash;%&gt;
              </button>--%>
                <form action="/partner/partnerChildSignup.jsp?ctoken=<%=ctoken%>" method="POST">
                  <button class="btn-xs" type="submit" value="partnerChildList" name="submit" name="B1" style="background: white;border: 0;">
                    <img style="height: 35px;" src="/partner/images/newuser.png">
                  </button>
                </form>
              </div>
            </div>

            <div class="widget-content">
              <div id="horizontal-form">

                <form action="/partner/net/PartnerUserList?ctoken=<%=ctoken%>" method="post" name="forms" class="form-horizontal">
                  <input type="hidden" value="<%=ctoken%>" name="ctoken">

                  <%
                      Hashtable detailHash = new Hashtable();
                      detailHash = (Hashtable) session.getAttribute("childmember");
                  %>

                  <div class="form-group col-md-8">
                    <label class="col-sm-3 control-label">Partner ID</label>
                    <div class="col-sm-6">
                      <input type="text" name="partnerid" class="form-control" value="<%=(String) detailHash.get("partnerid")%>" disabled>
                    </div>
                  </div>


                  <%--<div class="form-group col-md-3 has-feedback">&nbsp;</div>--%>
                  <div class="form-group col-md-3">
                    <div class="col-sm-offset-2 col-sm-3">
                      <button type="submit" class="btn btn-default"><i class="fa fa-clock-o"></i>
                        &nbsp;&nbsp;Search</button>
                    </div>
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
                <%--<div class="table-responsive datatable">
                <div class="form foreground bodypanelfont_color panelbody_color">

                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="/*background-color: #ffffff;*/color:#34495e;padding-top: 5px;font-size: 18px;align: initial;"><i class="fa fa-th-large"></i>&nbsp;&nbsp;Merchant's User Signup</h2>
                    <hr class="hrform">
                </div>--%>
                <%
                  String errormsg=(String)request.getAttribute("error");

                  if(errormsg!=null)
                  {
                                            /*out.println("<center><font class=\"text\" face=\"arial\">"+errormsg+"</font></center>");*/
                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;"+errormsg+"</h5>");
                  }
                %>
                <form action="/partner/net/NewChildPartnerSignUp?ctoken=<%=ctoken%>" method="post" name="form1" class="form-horizontal">

                <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>" >
                <input id="isEncrypted" name="isEncrypted" type="hidden" value="false" >

                <div class="form-group">
                  <label class="col-sm-4 control-label">New Login*</label>
                  <div class="col-sm-6">
                    <input type="text" maxlength="100" class="form-control" value="" name="username">
                  </div>
                </div>


                <input type="hidden" value="3" name="step">

                <div class="form-group col-md-4"></div>
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
    </div>
  </div>
</div>

<%
  }
%>
</body>
</html>
