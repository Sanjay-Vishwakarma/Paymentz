<%@ page import="com.directi.pg.Functions" %>
<%@ include file="top.jsp"%>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.ResourceBundle" %>
<%--
  Created by IntelliJ IDEA.
  User: Jinesh
  Date: 1/5/2016
  Time: 12:41 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","viewpartnerUserList");
%>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title><%=company%>|Partner User Management</title>

  <style type="text/css">
    .field-icon
    {
      float:  right;
      margin-top: -25px;
      position: relative;
      z-index: 2;
    }
  </style>
  <script src="/merchant/javascript/hidde.js"></script>
</head>
<body class="pace-done widescreen fixed-left-void" onload="bodyonload()">


<%
  ResourceBundle rb1 = null;
  String language_property1 = (String) session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String viewPartnerUserList_Partner_User_Details = StringUtils.isNotEmpty(rb1.getString("viewPartnerUserList_Partner_User_Details")) ? rb1.getString("viewPartnerUserList_Partner_User_Details") : "Partner User Details";
  String viewPartnerUserList_Parent_PartnerID = StringUtils.isNotEmpty(rb1.getString("viewPartnerUserList_Parent_PartnerID")) ? rb1.getString("viewPartnerUserList_Parent_PartnerID") : "Parent Partner ID";
  String viewPartnerUserList_Login = StringUtils.isNotEmpty(rb1.getString("viewPartnerUserList_Login")) ? rb1.getString("viewPartnerUserList_Login") : "Login";
  String viewPartnerUserList_Email_Address = StringUtils.isNotEmpty(rb1.getString("viewPartnerUserList_Email_Address")) ? rb1.getString("viewPartnerUserList_Email_Address") : "Email Address";
  String viewPartnerUserList_Action_ExecutorId = StringUtils.isNotEmpty(rb1.getString("viewPartnerUserList_Action_ExecutorId")) ? rb1.getString("viewPartnerUserList_Action_ExecutorId") : "Action Executor Id";
  String viewPartnerUserList_Action_Executor_Name = StringUtils.isNotEmpty(rb1.getString("viewPartnerUserList_Action_Executor_Name")) ? rb1.getString("viewPartnerUserList_Action_Executor_Name") : "Action Executor Name";
  String viewPartnerUserList_Sorry = StringUtils.isNotEmpty(rb1.getString("viewPartnerUserList_Sorry")) ? rb1.getString("viewPartnerUserList_Sorry") : "Sorry";
  String viewPartnerUserList_no = StringUtils.isNotEmpty(rb1.getString("viewPartnerUserList_no")) ? rb1.getString("viewPartnerUserList_no") : "No records found.";
  String viewPartnerUserList_Save = StringUtils.isNotEmpty(rb1.getString("viewPartnerUserList_Save")) ? rb1.getString("viewPartnerUserList_Save") : "Save";

  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  Functions functions=new Functions();
  //String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
  session.setAttribute("submit","partnerChildList");
  if (partner.isLoggedInPartner(session))
  {
%>
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">


      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/net/PartnerUserList?ctoken=<%=ctoken%>" method="post" name="form">
            <%
              Enumeration<String> stringEnumeration=request.getParameterNames();
              while(stringEnumeration.hasMoreElements())
              {
                String name=stringEnumeration.nextElement();
                if("accountid".equals(name))
                {
                  out.println("<input type='hidden' name='"+name+"' value='"+request.getParameterValues(name)[1]+"'/>");
                }
                else
                  out.println("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
              }
            %>

            <button class="btn-xs" type="submit" name="B1" style="background: transparent;border: 0;">
              <img style="height: 35px;" src="/partner/images/goBack.png">
            </button>
          </form>
        </div>
      </div>
      <br><br><br>
      <%--<div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">

            <div class="widget-header transparent">
              <h2><i class="fa fa-th-large"></i>&nbsp;&nbsp;<strong>Merchant's Merchant Master</strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="pull-right">
              <div class="btn-group">
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
                    String partnerId = (String) session.getAttribute("merchantid");
                   /* if(request.getAttribute("error")!=null)
                    {
                      String message = (String) request.getAttribute("error");
                      if(message != null)
                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;"+message+"</h5>");
                    }*/

                  %>
                  &lt;%&ndash;<div class="form-group">

                    <label class="col-sm-2 control-label">Partner Id</label>

                    <div class="col-sm-4 has-feedback">
                      <input type="text" name="partnerid" class="form-control"  value="<%=partnerId%>" disabled>
                    </div>


                    <div class="form-group col-sm-2"></div>
                    <div class="form-group col-sm-4">
                      <button type="submit" class="btn btn-default" &lt;%&ndash;style="margin-left:300px;padding-right: 50px;"&ndash;%&gt;>
                        <i class="fa fa-clock-o"></i>
                        &nbsp;&nbsp;Search
                      </button>
                    </div>
                  </div>&ndash;%&gt;

                  <div class="form-group col-md-8">
                    <label class="col-sm-3 control-label">Partner ID</label>
                    <div class="col-sm-6">
                      <input type="text" name="partnerid" class="form-control"  value="<%=partnerId%>" disabled>
                    </div>
                  </div>


                  &lt;%&ndash;<div class="form-group col-md-3 has-feedback">&nbsp;</div>&ndash;%&gt;
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
      </div>--%>





      <div class="row reporttable">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=viewPartnerUserList_Partner_User_Details%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding" style="overflow-x: auto;">
              <%
                Hashtable detailHash = (Hashtable) request.getAttribute("detailHash");
                if(detailHash!=null)
                {
                  detailHash = (Hashtable)request.getAttribute("detailHash");
                }
                String action = (String) request.getAttribute("action");
                String isreadonly =(String) request.getAttribute("action");
                String role=(String) request.getAttribute("role");
                String username=(String)session.getAttribute("username");
                String actionExecutorId=(String)session.getAttribute("merchantid");
                String actionExecutorName=role+"-"+username;

                String conf = "";

                if(!functions.isValueNull((String)detailHash.get("actionExecutorId")))
                {
                  actionExecutorId="-";
                }
                else
                {
                  actionExecutorId = (String) detailHash.get("actionExecutorId");

                }
                if(!functions.isValueNull((String)detailHash.get("actionExecutorName")))
                {
                  actionExecutorName="-";
                }
                else
                {
                  actionExecutorName= (String) detailHash.get("actionExecutorName");

                }
                if(isreadonly.equalsIgnoreCase("view"))
                {
                  conf = "disabled";
                }
                else if (isreadonly.equalsIgnoreCase("modify"))
                {
                  conf = "";
                }
                if (detailHash != null && detailHash.size() > 0)
                {
                  String style="class=tr0";




              %>
              <form action="/partner/net/EditPartnerUserList?ctoken=<%=ctoken%>" method="post" name="forms" class="form-horizontal">
                <input type="hidden" name="ctoken" value="<%=ctoken%>">

                <div class="widget-content padding">

                  <div class="form-group">
                    <%--<div class="col-md-2"></div>--%>
                    <label class="col-md-4 control-label"><%=viewPartnerUserList_Parent_PartnerID%></label>
                    <div class="col-md-4">
                      <input type="text" class="form-control" value="<%=ESAPI.encoder().encodeForHTML((String)detailHash.get("partnerid"))%>"disabled>
                    </div>
                    <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                    <%--<div class="col-md-2"></div>--%>
                    <label class="col-md-4 control-label"><%=viewPartnerUserList_Login%></label>
                    <div class="col-md-4">
                      <input type="text" class="form-control" value="<%=ESAPI.encoder().encodeForHTML((String)detailHash.get("login"))%>"disabled>
                    </div>
                    <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                    <%--<div class="col-md-2"></div>--%>
                    <label class="col-md-4 control-label"><%=viewPartnerUserList_Email_Address%></label>
                    <div class="col-md-4">
                     <%-- <input type="text" class="form-control" name="contact_emails" value="<%=ESAPI.encoder().encodeForHTML((String)detailHash.get("emailaddress"))%>" <%=conf%> >--%>
                      <input class="form-control" type="hidden" id="emailaddress_hidden"  value="<%=ESAPI.encoder().encodeForHTML((String)detailHash.get("emailaddress"))%>" name="contact_emails" >
                      <input class="form-control hidedivemail" type="text" id="emailaddress" value="<%=ESAPI.encoder().encodeForHTML((String)detailHash.get("emailaddress"))%>" onchange="setvalue('emailaddress')"<%=conf%>><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hideemail('emailaddress')"></span>

                    </div>
                    <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                    <%--<div class="col-md-2"></div>--%>
                    <label class="col-md-4 control-label"><%=viewPartnerUserList_Action_ExecutorId%></label>
                    <div class="col-md-4">
                      <input type="text" class="form-control" value="<%=ESAPI.encoder().encodeForHTML(actionExecutorId)%>"disabled>
                    </div>
                    <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                    <%--<div class="col-md-2"></div>--%>
                    <label class="col-md-4 control-label"><%=viewPartnerUserList_Action_Executor_Name%></label>
                    <div class="col-md-4">
                      <input type="text" class="form-control" value="<%=ESAPI.encoder().encodeForHTML(actionExecutorName)%>"disabled>
                    </div>
                    <div class="col-md-6"></div>
                  </div>


                <%--<div class="form-group col-md-12 has-feedback">
                      <center>
                          <label >&nbsp;</label>
                          <input type="hidden" value="1" name="step">
                          <button type="submit" class="btn btn-default" id="submit" name="action" value="update" style="display: -webkit-box;"><i class="fa fa-sign-in"></i>&nbsp;&nbsp;Update</button>
                      </center>
                  </div>--%>

                  <%
                    out.println("<div class=\"form-group col-md-12 has-feedback\">\n" +
                            "                                        <center>\n" +
                            "                                            <label ></label>");
                    if(!conf.equalsIgnoreCase("disabled"))
                      out.println("<input type=\"submit\" class=\"gotoauto btn btn-default\" name=\"modify\" value="+viewPartnerUserList_Save+" "+conf+"><input type=\"hidden\" name=\"action\" value=\"modify\"><input type=\"hidden\" name=\"memberid\" value=\""+(String)detailHash.get("memberid")+"\"><input type=\"hidden\" name=\"login\" value=\""+request.getAttribute("login")+"\">");
                    out.println("</center>\n" +
                            "                                    </div>");
                  %>


                </div>

              </form>

            </div>
          </div>
        </div>
      </div>



      <%

        }
        else
        {
          out.println(Functions.NewShowConfirmation1(viewPartnerUserList_Sorry,viewPartnerUserList_no ));
        }
      %>
      <%
        }
      %>


    </div>
  </div>
</div>

</body>
</html>