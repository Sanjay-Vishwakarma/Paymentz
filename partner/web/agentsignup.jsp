<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.*" %>

<%--
  Created by IntelliJ IDEA.
  User: Kanchan
  Date: 19-01-2021
  Time: 15:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp"%>
<%@ include file="functions.jsp"%>
<%
    session.setAttribute("submit","agentInterface");
%>
<%
    String partnerid= String.valueOf(session.getAttribute("partnerId"));
    String Config =" ";
    String partner_id = "";
    String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
    if(Roles.contains("superpartner")){
        partner_id = "";
    }
    else
    {
        Config = "disabled";
        partner_id = String.valueOf(session.getAttribute("merchantid"));
    }
%>
<html>
<head>

  <title></title>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>

  <script type="text/javascript" src="/partner/javascript/jquery.jcryption.js?ver=1">
  </script>
  <script>
    $(document).ready(function(){
      var w = $(window).width();

      if(w > 990)
      {
        //alert("It's greater than 990px");
        $("body").removeClass("smallscreen").addClass("widescreen");
        $("#wrapper").removeClass("enlarged");
      }
      else
      {
        //alert("It's less than 990px");
        $("body").removeClass("widescreen").addClass("smallscreen");
        $("#wrapper").addClass("enlarged");
        $(".left ul").removeAttr("style");
      }
    });

    $(document).ready(function() {
        document.getElementById('submit').disabled =  false;
        $("#submit").click(function() {
            var encryptedString1 =  $.jCryption.encrypt($("#passwd").val(), $("#ctoken").val());
            document.getElementById('passwd').value =  encryptedString1;

            var encryptedString2 =  $.jCryption.encrypt($("#conpasswd").val(), $("#ctoken").val());
            document.getElementById('conpasswd').value =  encryptedString2;

            document.getElementById('isEncrypted').value =  true;
        });
    });

    function hideshowpass(spanid,inputid)
    {
        var x = document.getElementById(inputid);
        if (x.type === "password")
        {
            $("#"+spanid).removeClass('fa-eye-slash').addClass('fa-eye')
            x.type = "text";
        }
        else
        {
            $("#"+spanid).removeClass('fa-eye').addClass('fa-eye-slash')
            x.type = "password";
        }
    }
  </script>
    <style type="text/css">

        #main{background-color: #ffffff}

        :target:before {
            content: "";
            display: block;
            height: 50px;
            margin: -50px 0 0;
        }

        .table > thead > tr > th {font-weight: inherit;}

        :target:before {
            content: "";
            display: block;
            height: 90px;
            margin: -50px 0 0;
        }

        footer{border-top:none;margin-top: 0;padding: 0;}

        /********************Table Responsive Start**************************/

        @media (max-width: 640px){

            table {border: 0;}

            /*table tr {
                padding-top: 20px;
                padding-bottom: 20px;
                display: block;
            }*/

            table thead { display: none;}

            tr:nth-child(odd), tr:nth-child(even) {background: #ffffff;}

            table td {
                display: block;
                border-bottom: none;
                padding-left: 0;
                padding-right: 0;
            }

            table td:before {
                content: attr(data-label);
                float: left;
                width: 100%;
                font-weight: bold;
            }

        }

        table {
            width: 100%;
            max-width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
            display: table;
            border-collapse: separate;
            border-color: grey;
        }

        thead {
            display: table-header-group;
            vertical-align: middle;
            border-color: inherit;

        }

        tr:nth-child(odd) {background: #F9F9F9;}

        tr {
            display: table-row;
            vertical-align: inherit;
            border-color: inherit;
        }

        th {padding-right: 1em;text-align: left;font-weight: bold;}

        td, th {display: table-cell;vertical-align: inherit;}

        tbody {
            display: table-row-group;
            vertical-align: middle;
            border-color: inherit;
        }

        td {
            padding-top: 6px;
            padding-bottom: 6px;
            padding-left: 10px;
            padding-right: 10px;
            vertical-align: top;
            border-bottom: none;
        }

        .table>thead>tr>th, .table>tbody>tr>th, .table>tfoot>tr>th, .table>thead>tr>td, .table>tbody>tr>td, .table>tfoot>tr>td{border-top: 1px solid #ddd;}

        /********************Table Responsive Ends**************************/

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
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">

      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/agentInterface.jsp?ctoken=<%=ctoken%>" method="post" name="form">
            <button class="btn-xs" type="submit" name="B1" style="background: transparent;border: 0;">
              <img style="height: 35px;" src="/partner/images/goBack.png">
            </button>
          </form>
        </div>
      </div>
      <br><br><br>

      <div class="row">
        <div class="col-md-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2 align="center"><u><strong>Agent Information</strong></u></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <br>

            <%
                String username = "";
                String passwd = "";
                String conpasswd = "";
                String company_name = "";
                String sitename = "";
                String supporturl = "";
                String notifyemail = "";

                String contact_emails = "";
                String contact_persons = "";
                String mainContact_cCmailid="";
                String mainContact_phone="";

                String refundContact_name="";
                String refundContact_mailId="";
                String refundContact_cCmail="";
                String refundContact_phone="";

                String cbContact_name="";
                String cbContact_mailId="";
                String cbContact_cCmailId="";
                String cbContact_phone="";

                String salesContact_name="";
                String salesContact_mailid="";
                String salesContact_cCmailId="";
                String salesContact_phone="";

                String billingContact_name="";
                String billingContact_mailid="";
                String billingContact_cCmailId="";
                String billingContact_phone="";

                String fraudContact_name="";
                String fraudContact_mailid="";
                String fraudContact_cCmailId="";
                String fraudContact_phone="";

                String technicalContact_name="";
                String technicalContact_mailId="";
                String technicalContact_cCmailId="";
                String technicalContact_phone="";

                String telno = "";
                String logoName = "";
                String country = "";
                String emailTemplateLang = "";

                 Hashtable details = (Hashtable) request.getAttribute("details");
                 String errormsg = (String) request.getAttribute("error");
                 Map<String,String> erjbMap= new LinkedHashMap<String, String>();
                 erjbMap.put("RO","RO");
                 erjbMap.put("JA","JA");
                 erjbMap.put("BG","BG");
                 erjbMap.put("EN","EN");

                  String isipwhitelisted = request.getParameter("isipwhitelisted") == null ? "" : request.getParameter("isipwhitelisted");
                  if(errormsg!=null)
                  {
                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg + "</h5>");
                  }

                  if (request.getParameter("MES") != null)
                  {
                      String mes = request.getParameter("MES");

                      if (details.get("login") != null) username = (String) details.get("login");
                      if (details.get("company_name") != null) company_name = (String) details.get("company_name");

                      if (details.get("sitename") != null) sitename = (String) details.get("sitename");
                      if (details.get("telno") != null) telno = (String) details.get("telno");
                      if (details.get("supporturl") != null) supporturl = (String) details.get("supporturl");
                      if (details.get("notifyemail") != null) notifyemail = (String) details.get("notifyemail");
                      if (details.get("emailTemplateLang") != null)
                          emailTemplateLang = (String) details.get("emailTemplateLang");
                      if (details.get("country") != null) country = (String) details.get("country");

                      if (details.get("logoName") != null) logoName = (String) details.get("logoName");
                      if (details.get("partnerid") != null) partner_id = (String) details.get("partnerid");

                      if (details.get("contact_emails") != null)
                          contact_emails = (String) details.get("contact_emails");
                      if (details.get("contact_persons") != null)
                          contact_persons = (String) details.get("contact_persons");
                      if (details.get("maincontact_ccmailid") != null)
                          mainContact_cCmailid = (String) details.get("maincontact_ccmailid");
                      if (details.get("maincontact_phone") != null)
                          mainContact_phone = (String) details.get("maincontact_phone");

                      if (details.get("salescontact_name") != null)
                          salesContact_name = (String) details.get("salescontact_name");
                      if (details.get("salescontact_mailid") != null)
                          salesContact_mailid = (String) details.get("salescontact_mailid");
                      if (details.get("salescontact_ccmailid") != null)
                          salesContact_cCmailId = (String) details.get("salescontact_ccmailid");
                      if (details.get("salescontact_phone") != null)
                          salesContact_phone = (String) details.get("salescontact_phone");

                      if (details.get("refundcontact_name") != null)
                          refundContact_name = (String) details.get("refundcontact_name");
                      if (details.get("refundcontact_mailid") != null)
                          refundContact_mailId = (String) details.get("refundcontact_mailid");
                      if (details.get("refundcontact_ccmailid") != null)
                          refundContact_cCmail = (String) details.get("refundcontact_ccmailid");
                      if (details.get("refundcontact_phone") != null)
                          refundContact_phone = (String) details.get("refundcontact_phone");

                      if (details.get("cbcontact_name") != null)
                          cbContact_name = (String) details.get("cbcontact_name");
                      if (details.get("cbcontact_mailid") != null)
                          cbContact_mailId = (String) details.get("cbcontact_mailid");
                      if (details.get("cbcontact_ccmailid") != null)
                          cbContact_cCmailId = (String) details.get("cbcontact_ccmailid");
                      if (details.get("cbcontact_phone") != null)
                          cbContact_phone = (String) details.get("cbcontact_phone");

                      if (details.get("billingcontact_name") != null)
                          billingContact_name = (String) details.get("billingcontact_name");
                      if (details.get("billingcontact_mailid") != null)
                          billingContact_mailid = (String) details.get("billingcontact_mailid");
                      if (details.get("billingcontact_ccmailId") != null)
                          billingContact_cCmailId = (String) details.get("billingcontact_ccmailId");
                      if (details.get("billingcontact_phone") != null)
                          billingContact_phone = (String) details.get("billingcontact_phone");

                      if (details.get("fraudcontact_name") != null)
                          fraudContact_name = (String) details.get("fraudcontact_name");
                      if (details.get("fraudcontact_mailid") != null)
                          fraudContact_mailid = (String) details.get("fraudcontact_mailid");
                      if (details.get("fraudcontact_ccmailid") != null)
                          fraudContact_cCmailId = (String) details.get("fraudcontact_ccmailid");
                      if (details.get("fraudcontact_phone") != null)
                          fraudContact_phone = (String) details.get("fraudcontact_phone");

                      if (details.get("technicalcontact_name") != null)
                          technicalContact_name = (String) details.get("technicalcontact_name");
                      if (details.get("technicalcontact_mailid") != null)
                          technicalContact_mailId = (String) details.get("technicalcontact_mailid");
                      if (details.get("technicalcontact_ccmailid") != null)
                          technicalContact_cCmailId = (String) details.get("technicalcontact_ccmailid");
                      if (details.get("technicalcontact_phone") != null)
                          technicalContact_phone = (String) details.get("technicalcontact_phone");
                  }
              %>
                    <%--  if (mes.equals("F"))
                      {
                          out.println("<table align=\"center\" width=\"60%\" ><tr><td><font class=\"textb\" >You have <b>NOT FILLED</b> some of required details or some of details filled by you are incorrect. Please fill all the details completely before going for next step.</font>");
                          out.println("</td></tr><tr><td algin=\"center\" class=\"bg-infoorange\" style=\"text-align: center; ><font class=\"textb\"  size=\"2\">");
                          errormsg = errormsg.replace("&lt;BR&gt;", "<BR>");
                          out.println(errormsg);
                          out.println("</font>");
                          out.println("</td></tr></table>");

                      }
                  }
            %>
--%>
            <form action="/partner/net/NewAgent?ctoken=<%=ctoken%>" method="post" name="form1" class="form-horizontal">
                <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>" >
                <input id="isEncrypted" name="isEncrypted" type="hidden" value="false" >

              <div class="widget-content padding">

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label" style="text-align: left">Username*<br>
                    (Username Should Not Contain Special Characters like !@#$%)</label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="100"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(username)%>" name="username" size="35">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label" style="text-align: left">Password*<br>
                    (Passwords length should be at least 8 and should contain alphabet, numeric, and special characters like !@#$)</label>
                  <div class="col-md-4">
                    <input id="passwd" class="form-control" type="Password" maxlength="125"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(passwd)%>" name="passwd" size="35" autocomplete="off" readonly onfocus="this.removeAttribute('readonly');">
                      <span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="showHidepass" onclick="hideshowpass('showHidepass','passwd')"></span>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label" style="text-align: left">Confirm Password*<br>
                    (Should be same as PASSWORD)</label>
                  <div class="col-md-4">
                    <input id="conpasswd" class="form-control" type="Password" maxlength="125" value="<%=ESAPI.encoder().encodeForHTMLAttribute(conpasswd)%>" name="conpasswd" size="35" autocomplete="off" readonly onfocus="this.removeAttribute('readonly');">
                      <span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="conshowHidepass" onclick="hideshowpass('conshowHidepass','conpasswd')"></span>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label" style="text-align: left">Agent Organization Name*</label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(company_name)%>" name="company_name" size="35">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label" style="text-align: left">Site URL*<br>
                    (Ex. http://www.abc.com)</label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="100"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(sitename)%>" name="sitename" size="35">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label" style="text-align: left">Support Number*</label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="20"  name="telno" size="35" size="20" value="<%=ESAPI.encoder().encodeForHTMLAttribute(telno)%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label" style="text-align: left">Support Url*</label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(supporturl)%>" name="supporturl" size="35">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label" style="text-align: left">Notify Email Id*</label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="255" value="<%=ESAPI.encoder().encodeForHTMLAttribute(notifyemail)%>" name="notifyemail" size="35" size="20">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label" style="text-align: left">Email Template Language</label>
                  <div class="col-md-4">
                    <select class="form-control" name="emailTemplateLang" >
                      <%
                        if ("RO".equals(emailTemplateLang))
                        {
                      %>
                      <option value="RO" selected>RO-Romanian</option>
                      <option value="JA">JA-Japanese</option>
                      <option value="BG">BG-Bulgarian</option>
                      <option value="EN">EN-English</option>
                      <%
                      }
                      else if ("JA".equals(emailTemplateLang))
                      {
                      %>
                      <option value="RO">RO-Romanian</option>
                      <option value="JA" selected>JA-Japanese</option>
                      <option value="BG">BG-Bulgarian</option>
                      <option value="EN">EN-English</option>
                      <%
                        }
                        else if ("BG".equals(emailTemplateLang))
                        {
                      %>
                      <option value="RO">RO-Romanian</option>
                      <option value="JA">JA-Japanese</option>
                      <option value="BG" selected>BG-Bulgarian</option>
                      <option value="EN">EN-English</option>
                      <%
                        }
                        else
                        {
                      %>
                      <option value="RO">RO-Romanian</option>
                      <option value="JA">JA-Japanese</option>
                      <option value="BG">BG-Bulgarian</option>
                      <option value="EN" selected>EN-English</option>
                      <% } %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label" style="text-align: left">Country*</label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(country)%>" name="country" size="35">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label" style="text-align: left">Logo Name*<br>(Ex. logo.jpg)</label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(logoName)%>" name="logoName" size="35">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label" style="text-align: left">Is IP Whitelisted*</label>
                  <div class="col-md-4">
                    <select class="form-control" name="isipwhitelisted">
                      <%
                        if ("Y".equals(isipwhitelisted))
                        {
                      %>
                      <option value="Y" selected>Y</option>
                      <option value="N">N</option>
                      <%
                         }
                        else
                         {
                      %>
                      <option value="N" selected>N</option>
                      <option value="Y">Y</option>
                      <%
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label" style="text-align: left">Partner Id*</label>
                  <div class="col-md-4">
                      <input id="pid" name="pid" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(partner_id)%>"  autocomplete="on" <%=Config%>>
                      <input  name="pid" type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(partner_id)%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>
                <br>

                  <div class="widget-header transparent">
                      <h2 align="center"><i class="fa fa-th-large"></i>&nbsp;&nbsp;<strong>Contact Details</strong></h2>
                      <div class="additional-btn">
                      </div>
                  </div>

                  <div class="widget-content padding">
                      <div id="horizontal-form">                        <%-- End Radio Button--%>
                          <div class="form-group col-md-12 has-feedback">
                              <div class="table-responsive">

                                  <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                      <tr id="mytabletr">
                                          <td align="left" style="background-color: #7eccad !important;color: white;">Main Contact*:</td>
                                          <td align="center">
                                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_persons)%>" name="contact_persons" placeholder="Name*" >
                                          </td>
                                          <td align="center">
                                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_emails)%>" name="contact_emails" placeholder="Email*" >
                                          </td>
                                          <td align="center">
                                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(mainContact_cCmailid)%>" name="maincontact_ccmailid" placeholder="Email Cc"  >
                                          </td>
                                          <td align="center">
                                              <input class="form-control" type="text" maxlength="20" value="<%=ESAPI.encoder().encodeForHTMLAttribute(mainContact_phone)%>" name="maincontact_phone" placeholder="Phone"  >
                                          </td>
                                      </tr>

                                      <tr id="mytabletr">
                                          <td align="left" style="background-color: #7eccad !important;color: white;">Chargeback Contact:</td>
                                          <td align="center">
                                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_name)%>" name="cbcontact_name" placeholder="Name" >
                                          </td>
                                          <td align="center">
                                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_mailId)%>" name="cbcontact_mailid" placeholder="Email" >
                                          </td>
                                          <td align="center">
                                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_cCmailId)%>" name="cbcontact_ccmailid" placeholder="Email Cc"  >
                                          </td>
                                          <td align="center">
                                              <input class="form-control" type="text" maxlength="20" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_phone)%>" name="cbcontact_phone" placeholder="Phone"  >
                                          </td>
                                      </tr>

                                      <tr id="mytabletr">
                                          <td align="left" style="background-color: #7eccad !important;color: white;">Refund Contact:</td>
                                          <td align="center">
                                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_name)%>" name="refundcontact_name" placeholder="Name" >
                                          </td>
                                          <td align="center">
                                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_mailId)%>" name="refundcontact_mailid" placeholder="Email" >
                                          </td>
                                          <td align="center">
                                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_cCmail)%>" name="refundcontact_ccmailid" placeholder="Email Cc"  >
                                          </td>
                                          <td align="center">
                                              <input class="form-control" type="text" maxlength="20" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_phone)%>" name="refundcontact_phone" placeholder="Phone"  >
                                          </td>
                                      </tr>

                                      <tr id="mytabletr">
                                          <td align="left" style="background-color: #7eccad !important;color: white;">Sales Contact:</td>
                                          <td align="center">
                                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_name)%>" name="salescontact_name" placeholder="Name" >
                                          </td>
                                          <td align="center">
                                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_mailid)%>" name="salescontact_mailid" placeholder="Email" >
                                          </td>
                                          <td align="center">
                                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_cCmailId)%>" name="salescontact_ccmailid" placeholder="Email Cc"  >
                                          </td>
                                          <td align="center">
                                              <input class="form-control" type="text" maxlength="20" value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_phone)%>" name="salescontact_phone" placeholder="Phone"  >
                                          </td>
                                      </tr>

                                      <tr id="mytabletr">
                                          <td align="left" style="background-color: #7eccad !important;color: white;">Billing Contact:</td>
                                          <td align="center">
                                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_name)%>" name="billingcontact_name" placeholder="Name" >
                                          </td>
                                          <td align="center">
                                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_mailid)%>" name="billingcontact_mailid" placeholder="Email" >
                                          </td>
                                          <td align="center">
                                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_cCmailId)%>" name="billingcontact_ccmailid" placeholder="Email Cc"  >
                                          </td>
                                          <td align="center">
                                              <input class="form-control" type="text" maxlength="20" value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_phone)%>" name="billingcontact_phone" placeholder="Phone"  >
                                          </td>
                                      </tr>

                                      <tr id="mytabletr">
                                          <td align="left" style="background-color: #7eccad !important;color: white;">Fraud Contact:</td>
                                          <td align="center">
                                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_name)%>" name="fraudcontact_name" placeholder="Name" >
                                          </td>
                                          <td align="center">
                                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_mailid)%>" name="fraudcontact_mailid" placeholder="Email" >
                                          </td>
                                          <td align="center">
                                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_cCmailId)%>" name="fraudcontact_ccmailid" placeholder="Email Cc"  >
                                          </td>
                                          <td align="center">
                                              <input class="form-control" type="text" maxlength="20" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_phone)%>" name="fraudcontact_phone" placeholder="Phone"  >
                                          </td>
                                      </tr>

                                      <tr id="mytabletr">
                                          <td align="left" style="background-color: #7eccad !important;color: white;">Technical Contact:</td>
                                          <td align="center">
                                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_name)%>" name="technicalcontact_name" placeholder="Name" >
                                          </td>
                                          <td align="center">
                                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_mailId)%>" name="technicalcontact_mailid" placeholder="Email" >
                                          </td>
                                          <td align="center">
                                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_cCmailId)%>" name="technicalcontact_ccmailid" placeholder="Email Cc"  >
                                          </td>
                                          <td align="center">
                                              <input class="form-control" type="text" maxlength="20" value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_phone)%>" name="technicalcontact_phone" placeholder="Phone"  >
                                          </td>
                                      </tr>
                                    </table>
                                  </div>
                              </div>

                          <div class="form-group col-md-12 has-feedback">
                              <center>
                                  <label >&nbsp;</label>
                                  <input type="hidden" value="-" name="bussinessdevexecutive"><input type="hidden"
                                                                                                     value="1"
                                                                                                     name="step">
                                  <button type="submit" class="btn btn-default" id="submit" disabled="disabled" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;Save</button>
                              </center>
                          </div>
                          </div>
                      </div>
                  </div>
                </form>
              </div>
            <h5 class="bg-infoorange" style="text-align: center;"><i class="fa fa-exclamation-triangle"></i>&nbsp;&nbsp;KINDLY KEEP JAVASCRIPT ENABLED IN YOUR BROWSER FOR SECURITY PURPOSES.</h5>
            </div>
          </div>
        </div>
      </div>
    </div>
<script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
<script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
<link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
<link href="/partner/cookies/cookies_popup.css" rel="stylesheet">
</body>
</html>
