<%@ page import="com.directi.pg.Functions" %>
<%@ include file="top.jsp"%>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.payment.MultipleMemberUtill" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Enumeration" %>
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
  session.setAttribute("submit","memberChildSignup");
%>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <script type="text/javascript" src="/partner/javascript/jquery.jcryption.js?ver=1"></script>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
  <script src="/merchant/javascript/hidde.js"></script>
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
  <title>Merchant Child Signup</title>


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

    #ui-id-2
    {
      overflow: auto;
      max-height: 350px;
    }
  </style>

</head>
<body>
<%
  MultipleMemberUtill multipleMemberUtill = new MultipleMemberUtill();
  Functions functions = new Functions();
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (partner.isLoggedInPartner(session))
  {
    Functions function = new Functions();
    String partnerid= String.valueOf(session.getAttribute("partnerId"));
    String Config = "";
    String pid = null;
    String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
    if(Roles.contains("superpartner")){
      pid=function.checkStringNull(request.getParameter("pid"))==null?"":request.getParameter("pid");
    }else{
      Config = "disabled";
      pid = String.valueOf(session.getAttribute("merchantid"));
    }
    String memberid=function.checkStringNull(request.getParameter("merchantid"))==null?"":request.getParameter("merchantid");
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String memberChildSignup_Merchant_User_Signup = StringUtils.isNotEmpty(rb1.getString("memberChildSignup_Merchant_User_Signup")) ? rb1.getString("memberChildSignup_Merchant_User_Signup") : "Merchant's User Signup";
    String memberChildSignup_PartnerID = StringUtils.isNotEmpty(rb1.getString("memberChildSignup_PartnerID")) ? rb1.getString("memberChildSignup_PartnerID") : "Partner ID";
    String memberChildSignup_MemberID = StringUtils.isNotEmpty(rb1.getString("memberChildSignup_MemberID")) ? rb1.getString("memberChildSignup_MemberID") : "Member ID*:";
    String memberChildSignup_Username = StringUtils.isNotEmpty(rb1.getString("memberChildSignup_Username")) ? rb1.getString("memberChildSignup_Username") : "Username*:";
    String memberChildSignup_username = StringUtils.isNotEmpty(rb1.getString("memberChildSignup_username")) ? rb1.getString("memberChildSignup_username") : "(Username Should Not Contain Special Characters like !@#$%)";
    String memberChildSignup_Password = StringUtils.isNotEmpty(rb1.getString("memberChildSignup_Password")) ? rb1.getString("memberChildSignup_Password") : "Password*:";
    String memberChildSignup_Password_special = StringUtils.isNotEmpty(rb1.getString("memberChildSignup_Password_special")) ? rb1.getString("memberChildSignup_Password_special") : "(Passwords length should be at least 8 and should contain alphabet, numeric, and special characters like !@#$)";
    String memberChildSignup_Confirm_Password = StringUtils.isNotEmpty(rb1.getString("memberChildSignup_Confirm_Password")) ? rb1.getString("memberChildSignup_Confirm_Password") : "Confirm Password*:";
    String memberChildSignup_password1 = StringUtils.isNotEmpty(rb1.getString("memberChildSignup_password1")) ? rb1.getString("memberChildSignup_password1") : "(Should be same as PASSWORD)";
    String memberChildSignup_Email_Address = StringUtils.isNotEmpty(rb1.getString("memberChildSignup_Email_Address")) ? rb1.getString("memberChildSignup_Email_Address") : "Email Address*:";
  //  String memberChildSignup_Telno = StringUtils.isNotEmpty(rb1.getString("memberChildSignup_Telno")) ? rb1.getString("memberChildSignup_Telno") : "Phone N0*:";
   // String memberChildSignup_Telcc = StringUtils.isNotEmpty(rb1.getString("memberChildSignup_Telcc")) ? rb1.getString("memberChildSignup_Telcc") : "Phone CC Code*:";
    String memberChildSignup_Button = StringUtils.isNotEmpty(rb1.getString("memberChildSignup_Button")) ? rb1.getString("memberChildSignup_Button") : "Button";
    String memberChildSignup_Submit = StringUtils.isNotEmpty(rb1.getString("memberChildSignup_Submit")) ? rb1.getString("memberChildSignup_Submit") : "Submit";

%>

<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">


      <form action="/partner/memberChildList.jsp?ctoken=<%=ctoken%>" name="form" method="post">
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
                }else if(value.contains("<")){
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

      <div class="row reporttable">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=memberChildSignup_Merchant_User_Signup%></strong></h2>
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
                  String userName = "";
                  if(errormsg!=null)
                  {
                                        /*out.println("<center><font class=\"text\" face=\"arial\">"+errormsg+"</font></center>");*/
                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg + "</h5>");
                  }
                  if (request.getParameter("error") != null)
                  {
                    String mes = (String) request.getParameter("MES");

                    //Hashtable details = (Hashtable) request.getAttribute("details");
                    if ((String) request.getAttribute("username") != null) userName = (String) request.getAttribute("username");
                  }
                %>
                <form action="/partner/net/NewChildMemberSignUp?ctoken=<%=ctoken%>" method="post" name="form1" class="form-horizontal">

                  <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>" >
                  <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                  <input id="isEncrypted" name="isEncrypted" type="hidden" value="false" >

                  <div class="form-group">
                    <div class="col-md-3"></div>
                    <label class="col-md-2 control-label"><%=memberChildSignup_PartnerID%></label>
                    <div class="col-md-4">
                      <input name="pid" id="pid" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                      <input type="hidden" name="pid" value="<%=pid%>">
                    </div>
                    <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                    <div class="col-md-3"></div>
                    <label class="col-md-2 control-label"><%=memberChildSignup_MemberID%></label>
                    <div class="col-md-4">
                      <input name="merchantid" id="member" value="<%=memberid%>" class="form-control" autocomplete="on">
                    </div>
                    <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                    <div class="col-md-3"></div>
                    <label class="col-md-2 control-label"><%=memberChildSignup_Username%></label>
                    <div class="col-md-4">
                      <input class="form-control" type="Text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(userName)%>" name="username" size="35">
                      <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=memberChildSignup_username%></label>
                    </div>
                    <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                    <div class="col-md-3"></div>
                    <label class="col-md-2 control-label"><%=memberChildSignup_Password%></label>
                    <div class="col-md-4">
                      <input id="passwd" class="form-control" type="Password" maxlength="125"  value="" name="passwd" size="35" autocomplete="off" readonly onfocus="this.removeAttribute('readonly');">
                      <span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="showHidepass" onclick="hideshowpass('showHidepass','passwd')"></span>
                      <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=memberChildSignup_Password_special%></label>
                    </div>
                    <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                    <div class="col-md-3"></div>
                    <label class="col-md-2 control-label"><%=memberChildSignup_Confirm_Password%></label>
                    <div class="col-md-4">
                      <input id="conpasswd" class="form-control" type="Password" maxlength="125" value="" name="conpasswd" size="35" autocomplete="off" readonly onfocus="this.removeAttribute('readonly');">
                      <span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="conshowHidepass" onclick="hideshowpass('conshowHidepass','conpasswd')"></span>
                      <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=memberChildSignup_password1%></label>
                    </div>
                    <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                    <div class="col-md-3"></div>
                    <label class="col-md-2 control-label"><%=memberChildSignup_Email_Address%></label>
                    <div class="col-md-4">
                      <input id="email" class="form-control" type="text" maxlength="125" value="" name="email" size="35">
                    </div>
                    <div class="col-md-6"></div>
                  </div>
                  <div class="form-group">
                    <div class="col-md-3"></div>
                    <label class="col-md-2 control-label">Phone CC*</label>
                    <div class="col-md-4">
                      <input id="telnocc" class="form-control" type="text" maxlength="125" value="" name="telnocc" size="35">
                    </div>
                    <div class="col-md-6"></div>
                  </div>
                  <div class="form-group">
                    <div class="col-md-3"></div>
                    <label class="col-md-2 control-label">Phone No*</label>
                    <div class="col-md-4">
                      <input id="telno" class="form-control" type="text" maxlength="125" value="" name="telno" size="35">
                    </div>
                    <div class="col-md-6"></div>
                  </div>
                  <input type="hidden" value="1" name="step">

                  <div class="form-group">
                    <div class="col-md-3"></div>
                    <label class="col-md-2 control-label" style="visibility: hidden;"><%=memberChildSignup_Button%></label>
                    <div class="col-md-4">
                      <button type="submit" name="submit" value="memberChildList" class="btn btn-default" id="submit" <%--style="background: rgb(126, 204, 173);"--%>>
                        <i class="fa fa-save"></i>
                        <%=memberChildSignup_Submit%>
                      </button>
                    </div>
                    <div class="col-md-6"></div>
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
<script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
<script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
<link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
<link href="/partner/cookies/cookies_popup.css" rel="stylesheet">
</body>
</html>