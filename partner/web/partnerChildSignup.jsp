<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ include file="top.jsp"%>
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
  session.setAttribute("submit","partnerChildList");
%>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <script type="text/javascript" src="/partner/javascript/jquery.jcryption.js?ver=1"></script>
  <script src="/merchant/javascript/hidde.js"></script>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
  <script language="javascript">
    $(document).ready(function() {

      document.getElementById('submit').disabled =  false;
      $("#submit").click(function() {
        var str =$("#passwd").val();
        if(str.indexOf('<') >= 0 && str.indexOf('/') >= 0)
        {

        }else
        {
          var encryptedString1 = $.jCryption.encrypt($("#passwd").val(), $("#ctoken").val());
          document.getElementById('passwd').value = encryptedString1;

          var encryptedString2 = $.jCryption.encrypt($("#conpasswd").val(), $("#ctoken").val());
          document.getElementById('conpasswd').value = encryptedString2;

          document.getElementById('isEncrypted').value = true;
        }
      });

    });
  </script>
  <title>Member Child Signup</title>

  <style type="text/css">

    @media (min-width: 768px){
      .form-horizontal .control-label {
        text-align: left!important;
      }
    }
  </style>
  <style type="text/css">
    .field-icon
    {
      float:  right;
      margin-top: -25px;
      position: relative;
      z-index: 2;
    }
  </style>


</head>
<body>
<%
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (partner.isLoggedInPartner(session))
  {
    Functions functions = new Functions();
    String role=(String)session.getAttribute("role");
    String username=(String)session.getAttribute("username");
    String actionExecutorId=(String)session.getAttribute("merchantid");
    String actionExecutorName=role+"-"+username;

    String partnerId = (String) session.getAttribute("merchantid");
    String Config = "";
    String pid = null;
    String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
    if(Roles.contains("superpartner")){
      pid=functions.checkStringNull(request.getParameter("pid"))==null?"":request.getParameter("pid");
    }else{
      Config = "disabled";
      pid = String.valueOf(session.getAttribute("merchantid"));
    }
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String partnerChildSignup_Partner_User_Signup = StringUtils.isNotEmpty(rb1.getString("partnerChildSignup_Partner_User_Signup")) ? rb1.getString("partnerChildSignup_Partner_User_Signup") : "Partner's User Signup";
    String partnerChildSignup_PartnerID = StringUtils.isNotEmpty(rb1.getString("partnerChildSignup_PartnerID")) ? rb1.getString("partnerChildSignup_PartnerID") : "Partner ID*:";
    String partnerChildSignup_Username = StringUtils.isNotEmpty(rb1.getString("partnerChildSignup_Username")) ? rb1.getString("partnerChildSignup_Username") : "Username*:";
    String partnerChildSignup_special_character = StringUtils.isNotEmpty(rb1.getString("partnerChildSignup_special_character")) ? rb1.getString("partnerChildSignup_special_character") : "(Username Should Not Contain Special Characters like !@#$%)";
    String partnerChildSignup_Password = StringUtils.isNotEmpty(rb1.getString("partnerChildSignup_Password")) ? rb1.getString("partnerChildSignup_Password") : "Password*:";
    String partnerChildSignup_Partner_special_character = StringUtils.isNotEmpty(rb1.getString("partnerChildSignup_Partner_special_character")) ? rb1.getString("partnerChildSignup_Partner_special_character") : "(Passwords length should be at least 8 and should contain alphabet, numeric, and special characters like !@#$)";
    String partnerChildSignup_Confirm_Password = StringUtils.isNotEmpty(rb1.getString("partnerChildSignup_Confirm_Password")) ? rb1.getString("partnerChildSignup_Confirm_Password") : "Confirm Password*:";
    String partnerChildSignup_should = StringUtils.isNotEmpty(rb1.getString("partnerChildSignup_should")) ? rb1.getString("partnerChildSignup_should") : "(Should be same as PASSWORD)";
    String partnerChildSignup_Email_Address = StringUtils.isNotEmpty(rb1.getString("partnerChildSignup_Email_Address")) ? rb1.getString("partnerChildSignup_Email_Address") : "Email Address*:";
    String partnerChildSignup_Button = StringUtils.isNotEmpty(rb1.getString("partnerChildSignup_Button")) ? rb1.getString("partnerChildSignup_Button") : "Button";
    String partnerChildSignup_Submit = StringUtils.isNotEmpty(rb1.getString("partnerChildSignup_Submit")) ? rb1.getString("partnerChildSignup_Submit") : "Submit";
%>


<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">

      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/partnerChildList.jsp?ctoken=<%=ctoken%>" method="post" name="form">
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
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Partner's Partner Master</strong>

              </h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            &lt;%&ndash;<div class="pull-right">
              <div class="btn-group">
                &lt;%&ndash;<i class="fa fa-sign-in"></i>&ndash;%&gt;
                &lt;%&ndash;<img style="height: 35px;" src="/merchant/images/goBack.png">
                &lt;%&ndash;&nbsp;Go Back&ndash;%&gt;
              </button>&ndash;%&gt;
                <form action="/partner/partnerChildSignup.jsp?ctoken=<%=ctoken%>" method="POST">
                  <button class="btn-xs" type="submit" value="partnerChildList" name="submit" name="B1" style="background: white;border: 0;">
                    <img style="height: 35px;" src="/partner/images/newuser.png">
                  </button>
                </form>
              </div>
            </div>&ndash;%&gt;

            <div class="widget-content">
              <div id="horizontal-form">

                <form action="/partner/net/PartnerUserList?ctoken=<%=ctoken%>" method="post" name="forms" class="form-horizontal">
                  <input type="hidden" value="<%=ctoken%>" name="ctoken">

                  &lt;%&ndash;<%
                    String merchantid= (String) session.getAttribute("merchantid");
                    if(request.getAttribute("error")!=null)
                    {
                      String message = (String) request.getAttribute("error");
                      if(message != null)
                        //out.println("<center><li class=\"alert alert-danger alert-dismissable\"><b>"+message+"</b></li></center><br/><br/>");
                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + message + "</h5>");
                    }
                  %>&ndash;%&gt;


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

      <%--<form action="/partner/partnerChildList.jsp?ctoken=<%=ctoken%>" name="form" method="post">
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
      <br><br>
--%>
      <div class="row reporttable">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerChildSignup_Partner_User_Signup%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <%--<%
              String partnerId = (String) session.getAttribute("merchantid");
              String memberid = (String)request.getAttribute("memberid");
              if(request.getAttribute("error")!=null)
              {
                                            /*out.println("<center><font class=\"textb\">"+(String) request.getAttribute("error")+"</font></center><br/><br/>");*/
                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;"+(String) request.getAttribute("error")+"</h5>");
              }

            %>--%>

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
                  String userName = "";
                  if(errormsg!=null)
                  {
                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg + "</h5>");
                  }
                 username=functions.checkStringNull(request.getParameter("username"))==null?"":request.getParameter("username");
                  String Email=functions.checkStringNull(request.getParameter("email"))==null?"":request.getParameter("email");
                  if ((String) request.getAttribute("username") != null) userName = (String) request.getAttribute("username");
                %>

                <form action="/partner/net/NewChildPartnerSignUp?ctoken=<%=ctoken%>" method="post" name="form1" class="form-horizontal">

                  <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>" >
                  <input id="partnerid" name="partnerid" type="hidden" value="<%=partnerId%>" >
                  <input id="isEncrypted" name="isEncrypted" type="hidden" value="false" >
                  <div class="form-group">
                    <div class="col-md-3"></div>
                    <label class="col-md-2 control-label"><%=partnerChildSignup_PartnerID%></label>
                    <div class="col-md-4">
                      <input name="pid" id="pid" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                      <input type="hidden" name="pid" value="<%=pid%>">
                    </div>
                    <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                    <div class="col-md-3"></div>
                    <label class="col-md-2 control-label"><%=partnerChildSignup_Username%></label>
                    <div class="col-md-4">
                      <input class="form-control" type="Text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(username)%>" name="username" size="35">
                      <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=partnerChildSignup_special_character%></label>
                    </div>
                    <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                    <div class="col-md-3"></div>
                    <label class="col-md-2 control-label"><%=partnerChildSignup_Password%></label>
                    <div class="col-md-4">
                      <input id="passwd" class="form-control" type="Password" maxlength="125"  value="" name="passwd" size="35" autocomplete="off" readonly  onfocus="this.removeAttribute('readonly');">
                      <span toggle="#password-field" class="fa fa-fw fa-eye-slash  field-icon toggle-password" id="showHidepass" onclick="hideshowpass('showHidepass','passwd')"></span>
                      <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=partnerChildSignup_Partner_special_character%></label>
                    </div>
                    <div class="col-md-6"></div>
                  </div>


                  <div class="form-group">
                    <div class="col-md-3"></div>
                    <label class="col-md-2 control-label"><%=partnerChildSignup_Confirm_Password%></label>
                    <div class="col-md-4">
                      <input id="conpasswd" class="form-control" type="Password" maxlength="125" value="" name="conpasswd" size="35" autocomplete="off" readonly  onfocus="this.removeAttribute('readonly');"><span toggle="#password-field" class="fa fa-fw fa-eye-slash  field-icon toggle-password" id="conshowHidepass" onclick="hideshowpass('conshowHidepass','conpasswd')"></span>
                      <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=partnerChildSignup_should%></label>
                    </div>
                    <div class="col-md-6"></div>
                  </div>


                  <div class="form-group">
                    <div class="col-md-3"></div>
                    <label class="col-md-2 control-label"><%=partnerChildSignup_Email_Address%></label>
                    <div class="col-md-4">
                      <input id="email" class="form-control" type="text" value="<%=Email%>" maxlength="125" value="" name="email" size="35">
                    </div>
                    <div class="col-md-6"></div>
                  </div>

                  <input type="hidden" value="1" name="step">
                  <div class="form-group">
                    <div class="col-md-3"></div>
                    <label class="col-md-2 control-label" style="visibility: hidden;"><%=partnerChildSignup_Button%></label>
                    <div class="col-md-4">
                      <button type="submit" name="submit" class="btn btn-default" id="submit" value="partnerChildList">
                        <i class="fa fa-save"></i>
                        <%=partnerChildSignup_Submit%>
                      </button>
                    </div>
                    <div class="col-md-6"></div>
                  </div>
                </form>
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