<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="top.jsp"%>
<%@ page import="org.owasp.esapi.reference.DefaultEncoder" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Hashtable" %>
<%--
  Created by IntelliJ IDEA.
  User: supriya
  Date: 9/9/2015
  Time: 4:50 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String company      = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","partnermerchantsignup");
  String partnerid    = String.valueOf(session.getAttribute("partnerId"));
  ctoken              = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  String Config       = "";
  String supportNo    = "";
  String phonecc      = "";
  String telno        = "";
  String partner_id   = "";
  String Roles        = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
  if(Roles.contains("superpartner")){
    partner_id = "";
  }else{
    Config      = "disabled";
    partner_id  = String.valueOf(session.getAttribute("merchantid"));
  }

%>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <script src="/merchant/javascript/hidde.js"></script>
 <%-- <script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>--%>
  <script type="text/javascript" src="/partner/javascript/jquery.jcryption.js?ver=1"></script>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
  <script language="javascript">
      $(document).ready(function() {
          document.getElementById('btnSubmit').disabled =  false;
          $("#btnSubmit").click(function() {
            var encryptedString1 =  $.jCryption.encrypt($("#passwd").val(), $("#ctoken").val());
            document.getElementById('passwd').value =  encryptedString1;

            var encryptedString2 =  $.jCryption.encrypt($("#conpasswd").val(), $("#ctoken").val());
            document.getElementById('conpasswd').value =  encryptedString2;

            document.getElementById('isEncrypted').value =  true;
          });

          $('#yesSubmit').click(function(){
            document.getElementById('sendEmailNotification').value =  "Y";
            document.getElementById('form1').submit();
          });
          $('#noSubmit').click(function(){
            document.getElementById('sendEmailNotification').value =  "N";
            document.getElementById('form1').submit();
          });
        });
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

  <title><%=company%> | Merchant SignUp</title>
</head>
<body>

<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">

      <div class="pull-right">
          <div class="btn-group">
              <form action="/partner/memberlist.jsp?ctoken=<%=ctoken%>" method="post" name="form">
                  <%
                    Enumeration<String> stringEnumeration = request.getParameterNames();
                    while(stringEnumeration.hasMoreElements())
                    {
                      String name = stringEnumeration.nextElement();
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

      <div class="row">
        <div class="col-md-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=company%> Merchant Master</strong></h2>
              <div class="additional-btn">
                <%--<form action="/partner/memberlist.jsp?ctoken=<%=ctoken%>" method="POST">
                  <button type="submit" class="btn btn-default" value="Add New Member" name="submit" style="/*width: 250px;*/ font-size:14px;">
                    <i class="fa fa-sign-in"></i>
                    &nbsp;&nbsp;Merchant Master
                  </button>
                </form>--%>
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <br><%--<br><br>

            <div align="center" class="textb"><h5><b><u>Merchant Information</u></b></h5></div>--%>
            <%
              String username         = "";
              String passwd           = "";
              String conpasswd        = "";
              String company_name     = "";
              String sitename         = "";
              String domain           = "";
              String contact_emails   = "";
              String contact_persons  = "";
              //String telno = "";
              String country          = "";
              String phone            = "";
              String mainContact_cCmailid       = "";
              String mainContact_bcCmailid      = "";
              String mainContact_phone          = "";
              String support_persons            = "";
              String support_emails             = "";
              String support_cCmailid           = "";
              String support_bcCmailid          = "";
              String support_phone              = "";
              String refundContact_name         = "";
              String refundContact_mailId       = "";
              String refundContact_cCmail       = "";
              String refundContact_bcCmail      = "";
              String refundContact_phone        = "";
              String cbContact_name             = "";
              String cbContact_mailId           = "";
              String cbContact_cCmailId         = "";
              String cbContact_bcCmailId        = "";
              String cbContact_phone            = "";
              String salesContact_name          = "";
              String salesContact_mailid        = "";
              String salesContact_cCmailId      = "";
              String salesContact_bcCmailId     = "";
              String salesContact_phone         = "";
              String billingContact_name        = "";
              String billingContact_mailid      = "";
              String billingContact_cCmailId    = "";
              String billingContact_bcCmailId   = "";
              String billingContact_phone       = "";

              String fraudContact_name          = "";
              String fraudContact_mailid        = "";
              String fraudContact_cCmailId      = "";
              String fraudContact_bcCmailId     = "";
              String fraudContact_phone         = "";

              String technicalContact_name      = "";
              String technicalContact_mailId    = "";
              String technicalContact_cCmailId  = "";
              String technicalContact_bcCmailId = "";
              String technicalContact_phone     = "";

              String etoken     = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
              String errormsg   = (String)request.getAttribute("error");
              Hashtable details = (Hashtable) request.getAttribute("details");
              if(errormsg != null)
              {
                //out.println("<center><font class=\"text\" face=\"arial\">"+errormsg+"</font></center>");
                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg + "</h5>");
              }
              if (details != null)
              {

                String mes = (String) request.getParameter("MES");
                if ((String) details.get("login") != null) username = (String) details.get("login");
                if ((String) details.get("company_name") != null) company_name = (String) details.get("company_name");
                if ((String) details.get("partnerid") != null) partner_id = (String) details.get("partnerid");
                if ((String) details.get("sitename") != null) sitename = (String) details.get("sitename");
                if ((String) details.get("domain") != null) domain = (String) details.get("domain");
                if ((String) details.get("telno") != null) telno = (String) details.get("telno");
                if ((String) details.get("phone") != null) phone = (String) details.get("phone");
                if ((String) details.get("country") != null) country = (String) details.get("country");

                if ((String) details.get("contact_emails") != null) contact_emails = (String) details.get("contact_emails");
                if ((String) details.get("contact_persons") != null) contact_persons = (String) details.get("contact_persons");
                if ((String) details.get("maincontact_ccmailid") != null) mainContact_cCmailid = (String) details.get("maincontact_ccmailid");
                if((String) details.get("maincontact_bccmailid") != null) mainContact_bcCmailid= (String) details.get("maincontact_bccmailid");
                if ((String) details.get("maincontact_phone") != null) mainContact_phone = (String) details.get("maincontact_phone");

                if (details.get("support_persons") != null) support_persons = (String) details.get("support_persons");
                if (details.get("support_emails") != null) support_emails = (String) details.get("support_emails");
                if (details.get("support_ccmailid") != null) support_cCmailid = (String) details.get("support_ccmailid");
                if (details.get("support_bccmailid") != null) support_bcCmailid=(String) details.get("support_bccmailid");
                if (details.get("support_phone") != null) support_phone = (String) details.get("support_phone");

                if ((String) details.get("salescontact_name") != null) salesContact_name = (String) details.get("salescontact_name");
                if ((String) details.get("salescontact_mailid") != null) salesContact_mailid = (String) details.get("salescontact_mailid");
                if ((String) details.get("salescontact_ccmailid") != null) salesContact_cCmailId = (String) details.get("salescontact_ccmailid");
                if ((String) details.get("salescontact_bccmailid") != null) salesContact_bcCmailId=(String) details.get("salescontact_bccmailid");
                if ((String) details.get("salescontact_phone") != null) salesContact_phone = (String) details.get("salescontact_phone");

                if ((String) details.get("refundcontact_name") != null) refundContact_name = (String) details.get("refundcontact_name");
                if ((String) details.get("refundcontact_mailid") != null) refundContact_mailId = (String) details.get("refundcontact_mailid");
                if ((String) details.get("refundcontact_ccmailid") != null) refundContact_cCmail = (String) details.get("refundcontact_ccmailid");
                if((String) details.get("refundcontact_bccmailid") != null) refundContact_bcCmail=(String) details.get("refundcontact_bccmailid");
                if ((String) details.get("refundcontact_phone") != null) refundContact_phone = (String) details.get("refundcontact_phone");

                if ((String) details.get("cbcontact_name") != null) cbContact_name = (String) details.get("cbcontact_name");
                if ((String) details.get("cbcontact_mailid") != null) cbContact_mailId = (String) details.get("cbcontact_mailid");
                if ((String) details.get("cbcontact_ccmailid") != null) cbContact_cCmailId = (String) details.get("cbcontact_ccmailid");
                if((String) details.get("cbcontact_bccmailid")!= null) cbContact_bcCmailId=(String) details.get("cbcontact_bccmailid");
                if ((String) details.get("cbcontact_phone") != null) cbContact_phone = (String) details.get("cbcontact_phone");

                if ((String) details.get("billingcontact_name") != null) billingContact_name = (String) details.get("billingcontact_name");
                if ((String) details.get("billingcontact_mailid") != null) billingContact_mailid = (String) details.get("billingcontact_mailid");
                if ((String) details.get("billingContact_cCmailId") != null) billingContact_cCmailId = (String) details.get("billingContact_cCmailId");
                if ((String) details.get("billingcontact_bccmailid") != null) billingContact_bcCmailId = (String) details.get("billingcontact_bccmailid");
                if ((String) details.get("bllingContact_phone") != null) billingContact_phone = (String) details.get("bllingContact_phone");

                if ((String) details.get("fraudcontact_name") != null) fraudContact_name = (String) details.get("fraudcontact_name");
                if ((String) details.get("fraudcontact_mailid") != null) fraudContact_mailid = (String) details.get("fraudcontact_mailid");
                if ((String) details.get("fraudcontact_ccmailid") != null) fraudContact_cCmailId = (String) details.get("fraudcontact_ccmailid");
                if((String) details.get("fraudcontact_bccmailid") != null) fraudContact_bcCmailId=(String) details.get("fraudcontact_bccmailid");
                if ((String) details.get("fraudcontact_phone") != null) fraudContact_phone = (String) details.get("fraudcontact_phone");

                if ((String) details.get("technicalcontact_name") != null) technicalContact_name = (String) details.get("technicalcontact_name");
                if ((String) details.get("technicalcontact_mailid") != null) technicalContact_mailId = (String) details.get("technicalcontact_mailid");
                if ((String) details.get("technicalcontact_ccmailid") != null) technicalContact_cCmailId = (String) details.get("technicalcontact_ccmailid");
                if ((String) details.get("technicalcontact_bccmailid") != null) technicalContact_bcCmailId=(String) details.get("technicalcontact_bccmailid");
                if ((String) details.get("technicalcontact_phone") != null) technicalContact_phone = (String) details.get("technicalcontact_phone");
                if (details.get("telno") != null) supportNo = (String) details.get("telno");
                if (!supportNo.equals("-"))
                {
                  if(supportNo.contains("-"))
                  {
                    String splitvalue[] = supportNo.split("-");
                    if (splitvalue.length == 2)
                    {
                      phonecc = splitvalue[0];
                      telno   = splitvalue[1];
                    }
                    else
                    {
                      phonecc = splitvalue[0];
                      telno   = "";
                    }
                  }
                }
              }
            %>


            <form action="/partner/net/PartnerMerchantSignUp?ctoken=<%=ctoken%>" method="post" name="form1" id="form1" class="form-horizontal">
                <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>">
                <input id="isEncrypted" name="isEncrypted" type="hidden" value="false" >
                <input type="hidden" id="sendEmailNotification" name="sendEmailNotification" value="">
                <%--<script src="/partner/NewCss/js/jquery-1.12.4.min.js"></script>
                <script src="/partner/NewCss/js/jquery-ui.min.js"></script>
                <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>

                <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
                <script type="text/javascript" src="/merchant/javascript/jquery-1.7.1.js?ver=1"></script>
                <script type="text/javascript" src="/merchant/javascript/jquery.jcryption.js?ver=1"></script>--%>
                <script language="javascript">


                    function myjunk()
                    {
                        var hat       = this.document.form1.country.selectedIndex;
                        var hatto     = this.document.form1.country.options[hat].value;
                        var countrycd = this.document.form1.phonecc.value = hatto.split("|")[1];
                        var telnumb   = this.document.form1.telno.value;
                        // var cctel = countrycd.concat(telnumb);
                        if (hatto != 'Select one') {

                            this.document.form1.countrycode.value   = hatto.split("|")[0];
                            this.document.form1.phonecc.value       = hatto.split("|")[1];
                            this.document.form1.country.options[0].selected=false;

                        }
                    }

                </script>

              <div class="widget-content padding">
                  <div class="form-group">
                      <div class="modal" id="myModal" role="dialog">
                          <div id="target" class="modal-dialog" >
                              <div class="modal-content">
                                  <div class="header">
                                      <div class="logo">
                                        <%--<button type="button" id="closebtnConfirmation" class="close hide" data-dismiss="modal" aria-label="Close" onclick="closefunc()">
                                            <span aria-hidden="true">&times;</span>
                                        </button>--%>
                                          <h4 class="modal-title">Send Notification</h4>
                                      </div>
                                  </div>

                                  <div class="modal-body">
                                      <p>Send email notification to Merchant’s Main contact?</p>
                                  </div>
                                  <div class="modal-footer" >
                                      <button type="button" id="yesSubmit" <%--onclick="submitForm('Y')"--%> class="btn btn-default" data-dismiss="modal">Yes</button>
                                      <button type="button" id="noSubmit" <%--onclick="submitForm('N')"--%> class="btn btn-default" data-dismiss="modal">No</button>
                                  </div>
                              </div>
                          </div>
                      </div>
                      <div class="col-md-2"></div>
                      <label class="col-md-4 control-label">Username or Email*<br>
                        (Username Should Not Contain Special Characters like !#$%)</label>
                      <div class="col-md-4">
                        <input class="form-control" type="Text" maxlength="100"  maxlength = 100 value="<%=ESAPI.encoder().encodeForHTMLAttribute(username)%>" name="username" size="35">
                      </div>
                      <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                      <div class="col-md-2"></div>
                      <label class="col-md-4 control-label">Password*<br>
                        (Passwords length should be at least 8 and should contain alphabet, numeric, and special characters like !@#$)
                      </label>
                      <div class="col-md-4">
                        <input id="passwd" class="form-control" type="Password" autocomplete="off" readonly  onfocus="this.removeAttribute('readonly');"
                               maxlength="125"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(passwd)%>" name="passwd" size="35"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="showHidepass" onclick="hideshowpass('showHidepass','passwd')"></span>
                      </div>
                      <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                      <div class="col-md-2"></div>
                      <label class="col-md-4 control-label">Confirm Password*<br>
                        (Should be same as PASSWORD)
                      </label>
                      <div class="col-md-4">
                        <input id="conpasswd" class="form-control" type="Password" autocomplete="off" readonly  onfocus="this.removeAttribute('readonly');"
                               maxlength="125" value="<%=ESAPI.encoder().encodeForHTMLAttribute(conpasswd)%>" name="conpasswd" size="35"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="conshowHidepass" onclick="hideshowpass('conshowHidepass','conpasswd')"></span>
                      </div>
                      <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                      <div class="col-md-2"></div>
                      <label class="col-md-4 control-label">Partner Id*</label>
                      <div class="col-md-4">
                        <input id="pid" name="pid" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(partner_id)%>"  autocomplete="on" <%=Config%>>
                        <input  name="pid" type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(partner_id)%>">
                      </div>
                      <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                      <div class="col-md-2"></div>
                      <label class="col-md-4 control-label">Organization Name*</label>
                      <div class="col-md-4">
                        <input class="form-control" type="Text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(company_name)%>" name="company_name" size="35">
                      </div>
                      <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                      <div class="col-md-2"></div>
                      <label class="col-md-4 control-label">Site URL*<br>
                        (Ex. http://www.abc.com)</label>
                      <div class="col-md-4">
                        <input class="form-control" type="Text" maxlength="100"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(sitename)%>" name="sitename" size="35">
                      </div>
                      <div class="col-md-6"></div>
                  </div>
                  <div class="form-group">
                      <div class="col-md-2"></div>
                      <label class="col-md-4 control-label">Domain<br>
                        (Ex. http://www.abc.com)</label>
                      <div class="col-md-4">
                        <input class="form-control" type="Text" maxlength="1000"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(domain)%>" name="domain" size="35">
                      </div>
                      <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                      <div class="col-md-2"></div>
                      <label class="col-md-4 control-label">Country*</label>
                      <div class="col-md-4">
                          <select name="country"placeholder="Country*" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(country)%>"  onchange="myjunk();"  >
                              <option value="-|phonecc">Select a Country*</option>
                              <option value="AF|093">Afghanistan</option>
                              <option value="AX|358">Aland Islands</option>
                              <option value="AL|355">Albania</option>
                              <option value="DZ|231">Algeria</option>
                              <option value="AS|684">American Samoa</option>
                              <option value="AD|376">Andorra</option>
                              <option value="AO|244">Angola</option>
                              <option value="AI|001">Anguilla</option>
                              <option value="AQ|000">Antarctica</option>
                              <option value="AG|001">Antigua and Barbuda</option>
                              <option value="AR|054">Argentina</option>
                              <option value="AM|374">Armenia</option>
                              <option value="AW|297">Aruba</option>
                              <option value="AU|061">Australia</option>
                              <option value="AT|043">Austria</option>
                              <option value="AZ|994">Azerbaijan</option>
                              <option value="BS|001">Bahamas</option>
                              <option value="BH|973">Bahrain</option>
                              <option value="BD|880">Bangladesh</option>
                              <option value="BB|001">Barbados</option>
                              <option value="BY|375">Belarus</option>
                              <option value="BE|032">Belgium</option>
                              <option value="BZ|501">Belize</option>
                              <option value="BJ|229">Benin</option>
                              <option value="BM|001">Bermuda</option>
                              <option value="BT|975">Bhutan</option>
                              <option value="BO|591">Bolivia</option>
                              <option value="BA|387">Bosnia and Herzegovina</option>
                              <option value="BW|267">Botswana</option>
                              <option value="BV|000">Bouvet Island</option>
                              <option value="BR|055">Brazil</option>
                              <option value="IO|246">British Indian Ocean Territory</option>
                              <option value="VG|001">British Virgin Islands</option>
                              <option value="BN|673">Brunei</option>
                              <option value="BG|359">Bulgaria</option>
                              <option value="BF|226">Burkina Faso</option>
                              <option value="BI|257">Burundi</option>
                              <option value="KH|855">Cambodia</option>
                              <option value="CM|237">Cameroon</option>
                              <option value="CA|001">Canada</option>
                              <option value="CV|238">Cape Verde</option>
                              <option value="KY|001">Cayman Islands</option>
                              <option value="CF|236">Central African Republic</option>
                              <option value="TD|235">Chad</option>
                              <option value="CL|056">Chile</option>
                              <option value="CN|086">China</option>
                              <option value="CX|061">Christmas Island</option>
                              <option value="CC|061">Cocos (Keeling) Islands</option>
                              <option value="CO|057">Colombia</option>
                              <option value="KM|269">Comoros</option>
                              <option value="CK|682">Cook Islands</option>
                              <option value="CR|506">Costa Rica</option>
                              <option value="CI|225">Cote d'Ivoire</option>
                              <option value="HR|385">Croatia</option>
                              <option value="CU|053">Cuba</option>
                              <option value="CW|599">Curacao</option>
                              <option value="CY|357">Cyprus</option>
                              <option value="CZ|420">Czech Republic</option>
                              <option value="CD|243">Democratic Republic of the Congo</option>
                              <option value="DK|045">Denmark</option>
                              <option value="DJ|253">Djibouti</option>
                              <option value="DM|001">Dominica</option>
                              <option value="DO|001">Dominican Republic</option>
                              <option value="EC|593">Ecuador</option>
                              <option value="EG|020">Egypt</option>
                              <option value="SV|503">El Salvador</option>
                              <option value="GQ|240">Equatorial Guinea</option>
                              <option value="ER|291">Eritrea</option>
                              <option value="EE|372">Estonia</option>
                              <option value="ET|251">Ethiopia</option>
                              <option value="FK|500">Falkland Islands</option>
                              <option value="FO|298">Faroe Islands</option>
                              <option value="FJ|679">Fiji</option>
                              <option value="FI|358">Finland</option>
                              <option value="FR|033">France</option>
                              <option value="GF|594">French Guiana</option>
                              <option value="PF|689">French Polynesia</option>
                              <option value="TF|000">French Southern and Antarctic Lands</option>
                              <option value="GA|241">Gabon</option>
                              <option value="GM|220">Gambia</option>
                              <option value="GE|995">Georgia</option>
                              <option value="DE|049">Germany</option>
                              <option value="GH|233">Ghana</option>
                              <option value="GI|350">Gibraltar</option>
                              <option value="GR|030">Greece</option>
                              <option value="GL|299">Greenland</option>
                              <option value="GD|001">Grenada</option>
                              <option value="GP|590">Guadeloupe</option>
                              <option value="GU|001">Guam</option>
                              <option value="GT|502">Guatemala</option>
                              <option value="GG|000">Guernsey</option>
                              <option value="GN|224">Guinea</option>
                              <option value="GW|245">Guinea-Bissau</option>
                              <option value="GY|592">Guyana</option>
                              <option value="HT|509">Haiti</option>
                              <option value="HM|672">Heard Island & McDonald Islands</option>
                              <option value="HN|504">Honduras</option>
                              <option value="HK|852">Hong Kong</option>
                              <option value="HU|036">Hungary</option>
                              <option value="IS|354">Iceland</option>
                              <option value="IN|091">India</option>
                              <option value="ID|062">Indonesia</option>
                              <option value="IR|098">Iran</option>
                              <option value="IQ|964">Iraq</option>
                              <option value="IE|353">Ireland</option>
                              <option value="IL|972">Israel</option>
                              <option value="IT|039">Italy</option>
                              <option value="JM|001">Jamaica</option>
                              <option value="JP|081">Japan</option>
                              <option value="JE|044">Jersey</option>
                              <option value="JO|962">Jordan</option>
                              <option value="KZ|007">Kazakhstan</option>
                              <option value="KE|254">Kenya</option>
                              <option value="KI|686">Kiribati</option>
                              <option value="KW|965">Kuwait</option>
                              <option value="KG|996">Kyrgyzstan</option>
                              <option value="LA|856">Laos</option>
                              <option value="LV|371">Latvia</option>
                              <option value="LB|961">Lebanon</option>
                              <option value="LS|266">Lesotho</option>
                              <option value="LR|231">Liberia</option>
                              <option value="LY|218">Libya</option>
                              <option value="LI|423">Liechtenstein</option>
                              <option value="LT|370">Lithuania</option>
                              <option value="LU|352">Luxembourg</option>
                              <option value="MO|853">Macau, China</option>
                              <option value="MK|389">Macedonia</option>
                              <option value="MG|261">Madagascar</option>
                              <option value="MW|265">Malawi</option>
                              <option value="MY|060">Malaysia</option>
                              <option value="MV|960">Maldives</option>
                              <option value="ML|223">Mali</option>
                              <option value="MT|356">Malta</option>
                              <option value="MH|692">Marshall Islands</option>
                              <option value="MQ|596">Martinique</option>
                              <option value="MR|222">Mauritania</option>
                              <option value="MU|230">Mauritius</option>
                              <option value="YT|269">Mayotte</option>
                              <option value="MX|052">Mexico</option>
                              <option value="FM|691">Micronesia, Federated States of</option>
                              <option value="MD|373">Moldova</option>
                              <option value="MC|377">Monaco</option>
                              <option value="MN|976">Mongolia</option>
                              <option value="ME|382">Montenegro</option>
                              <option value="MS|001">Montserrat</option>
                              <option value="MA|212">Morocco</option>
                              <option value="MZ|258">Mozambique</option>
                              <option value="MM|095">Myanmar</option>
                              <option value="NA|264">Namibia</option>
                              <option value="NR|674">Nauru</option>
                              <option value="NP|977">Nepal</option>
                              <option value="AN|599">Netherlands Antilles</option>
                              <option value="NL|031">Netherlands</option>
                              <option value="NC|687">New Caledonia</option>
                              <option value="NZ|064">New Zealand</option>
                              <option value="NI|505">Nicaragua</option>
                              <option value="NE|227">Niger</option>
                              <option value="NG|234">Nigeria</option>
                              <option value="NU|683">Niue</option>
                              <option value="NF|672">Norfolk Island</option>
                              <option value="KP|850">North Korea</option>
                              <option value="MP|001">Northern Mariana Islands</option>
                              <option value="NO|047">Norway</option>
                              <option value="OM|968">Oman</option>
                              <option value="PK|092">Pakistan</option>
                              <option value="PW|680">Palau</option>
                              <option value="PS|970">Palestinian Authority</option>
                              <option value="PA|507">Panama</option>
                              <option value="PG|675">Papua New Guinea</option>
                              <option value="PY|595">Paraguay</option>
                              <option value="PE|051">Peru</option>
                              <option value="PH|063">Philippines</option>
                              <option value="PN|064">Pitcairn Islands</option>
                              <option value="PL|048">Poland</option>
                              <option value="PT|351">Portugal</option>
                              <option value="PR|001">Puerto Rico</option>
                              <option value="QA|974">Qatar</option>
                              <option value="CG|242">Republic of the Congo</option>
                              <option value="RE|262">Reunion</option>
                              <option value="RO|040">Romania</option>
                              <option value="RU|007">Russia</option>
                              <option value="RW|250">Rwanda</option>
                              <option value="BL|590">Saint Barthelemy</option>
                              <option value="SH|290">Saint Helena, Ascension & Tristan daCunha</option>
                              <option value="KN|001">Saint Kitts and Nevis</option>
                              <option value="LC|001">Saint Lucia</option>
                              <option value="MF|590">Saint Martin</option>
                              <option value="PM|508">Saint Pierre and Miquelon</option>
                              <option value="VC|001">Saint Vincent and Grenadines</option>
                              <option value="WS|685">Samoa</option>
                              <option value="SM|378">San Marino</option>
                              <option value="ST|239">Sao Tome and Principe</option>
                              <option value="SA|966">Saudi Arabia</option>
                              <option value="SN|221">Senegal</option>
                              <option value="RS|381">Serbia</option>
                              <option value="SC|248">Seychelles</option>
                              <option value="SL|232">Sierra Leone</option>
                              <option value="SG|065">Singapore</option>
                              <option value="SK|421">Slovakia</option>
                              <option value="SI|386">Slovenia</option>
                              <option value="SB|677">Solomon Islands</option>
                              <option value="SO|252">Somalia</option>
                              <option value="ZA|027">South Africa</option>
                              <option value="GS|000">South Georgia & South Sandwich Islands</option>
                              <option value="KR|082">South Korea</option>
                              <option value="ES|034">Spain</option>
                              <option value="LK|094">Sri Lanka</option>
                              <option value="SD|249">Sudan</option>
                              <option value="SR|597">Suriname</option>
                              <option value="SJ|047">Svalbard and Jan Mayen</option>
                              <option value="SZ|268">Swaziland</option>
                              <option value="SE|046">Sweden</option>
                              <option value="CH|041">Switzerland</option>
                              <option value="SY|963">Syria</option>
                              <option value="TW|886">Taiwan</option>
                              <option value="TJ|992">Tajikistan</option>
                              <option value="TZ|255">Tanzania</option>
                              <option value="TH|066">Thailand</option>
                              <option value="TL|670">Timor-Leste</option>
                              <option value="TG|228">Togo</option>
                              <option value="TK|690">Tokelau</option>
                              <option value="TO|676">Tonga</option>
                              <option value="TT|001">Trinidad and Tobago</option>
                              <option value="TN|216">Tunisia</option>
                              <option value="TR|090">Turkey</option>
                              <option value="TM|993">Turkmenistan</option>
                              <option value="TC|001">Turks and Caicos Islands</option>
                              <option value="TV|688">Tuvalu</option>
                              <option value="UG|256">Uganda</option>
                              <option value="UA|380">Ukraine</option>
                              <option value="AE|971">United Arab Emirates</option>
                              <option value="GB|044">United Kingdom</option>
                              <option value="US|001">United States</option>
                              <option value="VI|001">United States Virgin Islands</option>
                              <option value="UY|598">Uruguay</option>
                              <option value="UZ|998">Uzbekistan</option>
                              <option value="VU|678">Vanuatu</option>
                              <option value="VA|379">Vatican City</option>
                              <option value="VE|058">Venezuela</option>
                              <option value="VN|084">Vietnam</option>
                              <option value="WF|681">Wallis and Futuna</option>
                              <option value="EH|212">Western Sahara</option>
                              <option value="YE|967">Yemen</option>
                              <option value="ZM|260">Zambia</option>
                              <option value="ZW|263">Zimbabwe</option>
                          </select>

                          <input value='<%=ESAPI.encoder().encodeForHTMLAttribute(country)%>' id="getcountval" type="hidden">

                          <script>

                            var countryval = document.getElementById('getcountval').value;

                                $('[name=country] option').filter(function() {
                                    if (countryval == 'GB') {
                                      return ($(this).text() == 'United Kingdom');
                                    } else if (countryval == 'US') {
                                      return ($(this).text() == 'United States');
                                    } else if (countryval == 'AF') {
                                      return ($(this).text() == 'Afghanistan');
                                    } else if (countryval == 'AX') {
                                      return ($(this).text() == 'Aland Islands');
                                    } else if (countryval == 'AL') {
                                      return ($(this).text() == 'Albania');
                                    } else if (countryval == 'DZ') {
                                      return ($(this).text() == 'Algeria');
                                    } else if (countryval == 'AS') {
                                      return ($(this).text() == 'American Samoa');
                                    } else if (countryval == 'AD') {
                                      return ($(this).text() == 'Andorra');
                                    } else if (countryval == 'AO') {
                                      return ($(this).text() == 'Angola');
                                    } else if (countryval == 'AI') {
                                      return ($(this).text() == 'Anguilla');
                                    } else if (countryval == 'AQ') {
                                      return ($(this).text() == 'Antarctica');
                                    } else if (countryval == 'AG') {
                                      return ($(this).text() == 'Antigua and Barbuda');
                                    } else if (countryval == 'AR') {
                                      return ($(this).text() == 'Argentina');
                                    } else if (countryval == 'AM') {
                                      return ($(this).text() == 'Armenia');
                                    } else if (countryval == 'AW') {
                                      return ($(this).text() == 'Aruba');
                                    } else if (countryval == 'AU') {
                                      return ($(this).text() == 'Australia');
                                    } else if (countryval == 'AT') {
                                      return ($(this).text() == 'Austria');
                                    } else if (countryval == 'AZ') {
                                      return ($(this).text() == 'Azerbaijan');
                                    } else if (countryval == 'BS') {
                                      return ($(this).text() == 'Bahamas');
                                    } else if (countryval == 'BH') {
                                      return ($(this).text() == 'Bahrain');
                                    } else if (countryval == 'BD') {
                                      return ($(this).text() == 'Bangladesh');
                                    } else if (countryval == 'BB') {
                                      return ($(this).text() == 'Barbados');
                                    } else if (countryval == 'BY') {
                                      return ($(this).text() == 'Belarus');
                                    } else if (countryval == 'BE') {
                                      return ($(this).text() == 'Belgium');
                                    } else if (countryval == 'BZ') {
                                      return ($(this).text() == 'Belize');
                                    } else if (countryval == 'BJ') {
                                      return ($(this).text() == 'Benin');
                                    } else if (countryval == 'BM') {
                                      return ($(this).text() == 'Bermuda');
                                    } else if (countryval == 'BT') {
                                      return ($(this).text() == 'Bhutan');
                                    } else if (countryval == 'BO') {
                                      return ($(this).text() == 'Bolivia');
                                    } else if (countryval == 'BA') {
                                      return ($(this).text() == 'Bosnia and Herzegovina');
                                    } else if (countryval == 'BW') {
                                      return ($(this).text() == 'Botswana');
                                    } else if (countryval == 'BV') {
                                      return ($(this).text() == 'Bouvet Island');
                                    } else if (countryval == 'BR') {
                                      return ($(this).text() == 'Brazil');
                                    } else if (countryval == 'IO') {
                                      return ($(this).text() == 'British Indian Ocean Territory');
                                    } else if (countryval == 'VG') {
                                      return ($(this).text() == 'British Virgin Islands');
                                    } else if (countryval == 'BN') {
                                      return ($(this).text() == 'Brunei');
                                    } else if (countryval == 'BG') {
                                      return ($(this).text() == 'Bulgaria');
                                    } else if (countryval == 'BF') {
                                      return ($(this).text() == 'Burkina Faso');
                                    } else if (countryval == 'BI') {
                                      return ($(this).text() == 'Burundi');
                                    } else if (countryval == 'KH') {
                                      return ($(this).text() == 'Cambodia');
                                    } else if (countryval == 'CM') {
                                      return ($(this).text() == 'Cameroon');
                                    } else if (countryval == 'CA') {
                                      return ($(this).text() == 'Canada');
                                    } else if (countryval == 'CV') {
                                      return ($(this).text() == 'Cape Verde');
                                    } else if (countryval == 'KY') {
                                      return ($(this).text() == 'Cayman Islands');
                                    } else if (countryval == 'CF') {
                                      return ($(this).text() == 'Central African Republic');
                                    } else if (countryval == 'TD') {
                                      return ($(this).text() == 'Chad');
                                    } else if (countryval == 'CL') {
                                      return ($(this).text() == 'Chile');
                                    } else if (countryval == 'CN') {
                                      return ($(this).text() == 'China');
                                    } else if (countryval == 'CX') {
                                      return ($(this).text() == 'Christmas Island');
                                    } else if (countryval == 'CC') {
                                      return ($(this).text() == 'Cocos (Keeling) Islands');
                                    } else if (countryval == 'CO') {
                                      return ($(this).text() == 'Colombia');
                                    } else if (countryval == 'KM') {
                                      return ($(this).text() == 'Comoros');
                                    } else if (countryval == 'CK') {
                                      return ($(this).text() == 'Cook Islands');
                                    } else if (countryval == 'CR') {
                                      return ($(this).text() == 'Costa Rica');
                                    } else if (countryval == 'CI') {
                                      return ($(this).text() == 'Cote d` Ivoire ');
                                    } else if (countryval == 'HR') {
                                      return ($(this).text() == 'Croatia');
                                    }
                                    else if (countryval == 'CU')
                                    {
                                      return ($(this).text() == 'Cuba');

                                    }
                                    else if (countryval == 'CW')
                                    {
                                      return ($(this).text() == 'Curacao');

                                    }
                                    else if (countryval == 'CY') {
                                      return ($(this).text() == 'Cyprus');
                                    } else if (countryval == 'CZ') {
                                      return ($(this).text() == 'Czech Republic');
                                    } else if (countryval == 'CD') {
                                      return ($(this).text() == 'Democratic Republic of the Congo');
                                    } else if (countryval == 'DK') {
                                      return ($(this).text() == 'Denmark');
                                    } else if (countryval == 'DJ') {
                                      return ($(this).text() == 'Djibouti');
                                    } else if (countryval == 'DM') {
                                      return ($(this).text() == 'Dominica');
                                    } else if (countryval == 'DO') {
                                      return ($(this).text() == 'Dominican Republic');
                                    } else if (countryval == 'EC') {
                                      return ($(this).text() == 'Ecuador');
                                    } else if (countryval == 'EG') {
                                      return ($(this).text() == 'Egypt');
                                    } else if (countryval == 'SV') {
                                      return ($(this).text() == 'El Salvador');
                                    } else if (countryval == 'GQ') {
                                      return ($(this).text() == 'Equatorial Guinea');
                                    } else if (countryval == 'ER') {
                                      return ($(this).text() == 'Eritrea');
                                    } else if (countryval == 'EE') {
                                      return ($(this).text() == 'Estonia');
                                    } else if (countryval == 'ET') {
                                      return ($(this).text() == 'Ethiopia');
                                    } else if (countryval == 'FK') {
                                      return ($(this).text() == 'Falkland Islands');
                                    } else if (countryval == 'FO') {
                                      return ($(this).text() == 'Faroe Islands');
                                    } else if (countryval == 'FJ') {
                                      return ($(this).text() == 'Fiji');
                                    } else if (countryval == 'FI') {
                                      return ($(this).text() == 'Finland');
                                    } else if (countryval == 'FR') {
                                      return ($(this).text() == 'France');
                                    } else if (countryval == 'GF') {
                                      return ($(this).text() == 'French Guiana');
                                    } else if (countryval == 'PF') {
                                      return ($(this).text() == 'French Polynesia');
                                    } else if (countryval == 'TF') {
                                      return ($(this).text() == 'French Southern and Antarctic Lands');
                                    } else if (countryval == 'GA') {
                                      return ($(this).text() == 'Gabon');
                                    } else if (countryval == 'GM') {
                                      return ($(this).text() == 'Gambia');
                                    } else if (countryval == 'GE') {
                                      return ($(this).text() == 'Georgia');
                                    } else if (countryval == 'DE') {
                                      return ($(this).text() == 'Germany');
                                    } else if (countryval == 'GH') {
                                      return ($(this).text() == 'Ghana');
                                    } else if (countryval == 'GI') {
                                      return ($(this).text() == 'Gibraltar');
                                    } else if (countryval == 'GR') {
                                      return ($(this).text() == 'Greece');
                                    } else if (countryval == 'GL') {
                                      return ($(this).text() == 'Greenland');
                                    } else if (countryval == 'GD') {
                                      return ($(this).text() == 'Grenada');
                                    } else if (countryval == 'GP') {
                                      return ($(this).text() == 'Guadeloupe');
                                    } else if (countryval == 'GU') {
                                      return ($(this).text() == 'Guam');
                                    } else if (countryval == 'GT') {
                                      return ($(this).text() == 'Guatemala');
                                    } else if (countryval == 'GG') {
                                      return ($(this).text() == 'Guernsey');
                                    } else if (countryval == 'GN') {
                                      return ($(this).text() == 'Guinea');
                                    } else if (countryval == 'GW') {
                                      return ($(this).text() == 'Guinea-Bissau');
                                    } else if (countryval == 'GY') {
                                      return ($(this).text() == 'Guyana');
                                    } else if (countryval == 'HT') {
                                      return ($(this).text() == 'Haiti');
                                    } else if (countryval == 'HM') {
                                      return ($(this).text() == 'Heard Island & McDonald Islands');
                                    } else if (countryval == 'HN') {
                                      return ($(this).text() == 'Honduras');
                                    } else if (countryval == 'HK') {
                                      return ($(this).text() == 'Hong Kong');
                                    } else if (countryval == 'HU') {
                                      return ($(this).text() == 'Hungary');
                                    } else if (countryval == 'IS') {
                                      return ($(this).text() == 'Iceland');
                                    } else if (countryval == 'IN') {
                                      return ($(this).text() == 'India');
                                    } else if (countryval == 'ID') {
                                      return ($(this).text() == 'Indonesia');
                                    } else if (countryval == 'IR') {
                                      return ($(this).text() == 'Iran');
                                    } else if (countryval == 'IQ') {
                                      return ($(this).text() == 'Iraq');
                                    } else if (countryval == 'IE') {
                                      return ($(this).text() == 'Ireland');
                                    } else if (countryval == 'IL') {
                                      return ($(this).text() == 'Israel');
                                    } else if (countryval == 'IT') {
                                      return ($(this).text() == 'Italy');
                                    } else if (countryval == 'JM') {
                                      return ($(this).text() == 'Jamaica');
                                    } else if (countryval == 'JP') {
                                      return ($(this).text() == 'Japan');
                                    } else if (countryval == 'JE') {
                                      return ($(this).text() == 'Jersey');
                                    } else if (countryval == 'JO') {
                                      return ($(this).text() == 'Jordan');
                                    } else if (countryval == 'KZ') {
                                      return ($(this).text() == 'Kazakhstan');
                                    } else if (countryval == 'KE') {
                                      return ($(this).text() == 'Kenya');
                                    } else if (countryval == 'KI') {
                                      return ($(this).text() == 'Kiribati');
                                    } else if (countryval == 'KW') {
                                      return ($(this).text() == 'Kuwait');
                                    } else if (countryval == 'KG') {
                                      return ($(this).text() == 'Kyrgyzstan');
                                    } else if (countryval == 'LA') {
                                      return ($(this).text() == 'Laos');
                                    } else if (countryval == 'LV') {
                                      return ($(this).text() == 'Latvia');
                                    } else if (countryval == 'LB') {
                                      return ($(this).text() == 'Lebanon');
                                    } else if (countryval == 'LS') {
                                      return ($(this).text() == 'Lesotho');
                                    } else if (countryval == 'LR') {
                                      return ($(this).text() == 'Liberia');
                                    } else if (countryval == 'LY') {
                                      return ($(this).text() == 'Libya');
                                    } else if (countryval == 'LI') {
                                      return ($(this).text() == 'Liechtenstein');
                                    } else if (countryval == 'LT') {
                                      return ($(this).text() == 'Lithuania');
                                    } else if (countryval == 'LU') {
                                      return ($(this).text() == 'Luxembourg');
                                    } else if (countryval == 'MO') {
                                      return ($(this).text() == 'Macau, China');
                                    } else if (countryval == 'MK') {
                                      return ($(this).text() == 'Macedonia');
                                    } else if (countryval == 'MG') {
                                      return ($(this).text() == 'Madagascar');
                                    } else if (countryval == 'MW') {
                                      return ($(this).text() == 'Malawi');
                                    } else if (countryval == 'MY') {
                                      return ($(this).text() == 'Malaysia');
                                    } else if (countryval == 'MV') {
                                      return ($(this).text() == 'Maldives');
                                    } else if (countryval == 'ML') {
                                      return ($(this).text() == 'Mali');
                                    } else if (countryval == 'MT') {
                                      return ($(this).text() == 'Malta');
                                    } else if (countryval == 'MH') {
                                      return ($(this).text() == 'Marshall Islands');
                                    } else if (countryval == 'MQ') {
                                      return ($(this).text() == 'Martinique');
                                    } else if (countryval == 'MR') {
                                      return ($(this).text() == 'Mauritania');
                                    } else if (countryval == 'MU') {
                                      return ($(this).text() == 'Mauritius');
                                    } else if (countryval == 'YT') {
                                      return ($(this).text() == 'Mayotte');
                                    } else if (countryval == 'MX') {
                                      return ($(this).text() == 'Mexico');
                                    } else if (countryval == 'FM') {
                                      return ($(this).text() == 'Micronesia, Federated States of');
                                    } else if (countryval == 'MD') {
                                      return ($(this).text() == 'Moldova');
                                    } else if (countryval == 'MC') {
                                      return ($(this).text() == 'Monaco');
                                    } else if (countryval == 'MN') {
                                      return ($(this).text() == 'Mongolia');
                                    } else if (countryval == 'ME') {
                                      return ($(this).text() == 'Montenegro');
                                    } else if (countryval == 'MS') {
                                      return ($(this).text() == 'Montserrat');
                                    } else if (countryval == 'MA') {
                                      return ($(this).text() == 'Morocco');
                                    } else if (countryval == 'MZ') {
                                      return ($(this).text() == 'Mozambique');
                                    } else if (countryval == 'MM') {
                                      return ($(this).text() == 'Myanmar');
                                    } else if (countryval == 'NA') {
                                      return ($(this).text() == 'Namibia');
                                    } else if (countryval == 'NR') {
                                      return ($(this).text() == 'Nauru');
                                    } else if (countryval == 'NP') {
                                      return ($(this).text() == 'Nepal');
                                    } else if (countryval == 'AN') {
                                      return ($(this).text() == 'Netherlands Antilles');
                                    } else if (countryval == 'NL') {
                                      return ($(this).text() == 'Netherlands');
                                    } else if (countryval == 'NC') {
                                      return ($(this).text() == 'New Caledonia');
                                    } else if (countryval == 'NZ') {
                                      return ($(this).text() == 'New Zealand');
                                    } else if (countryval == 'NI') {
                                      return ($(this).text() == 'Nicaragua');
                                    } else if (countryval == 'NE') {
                                      return ($(this).text() == 'Niger');
                                    } else if (countryval == 'NG') {
                                      return ($(this).text() == 'Nigeria');
                                    } else if (countryval == 'NU') {
                                      return ($(this).text() == 'Niue');
                                    } else if (countryval == 'NF') {
                                      return ($(this).text() == 'Norfolk Island');
                                    } else if (countryval == 'KP') {
                                      return ($(this).text() == 'North Korea');
                                    } else if (countryval == 'MP') {
                                      return ($(this).text() == 'Northern Mariana Islands');
                                    } else if (countryval == 'NO') {
                                      return ($(this).text() == 'Norway');
                                    } else if (countryval == 'OM') {
                                      return ($(this).text() == 'Oman');
                                    } else if (countryval == 'PK') {
                                      return ($(this).text() == 'Pakistan');
                                    } else if (countryval == 'PW') {
                                      return ($(this).text() == 'Palau');
                                    } else if (countryval == 'PS') {
                                      return ($(this).text() == 'Palestinian Authority');
                                    } else if (countryval == 'PA') {
                                      return ($(this).text() == 'Panama');
                                    } else if (countryval == 'PG') {
                                      return ($(this).text() == 'Papua New Guinea');
                                    } else if (countryval == 'PY') {
                                      return ($(this).text() == 'Paraguay');
                                    } else if (countryval == 'PE') {
                                      return ($(this).text() == 'Peru');
                                    } else if (countryval == 'PH') {
                                      return ($(this).text() == 'Philippines');
                                    } else if (countryval == 'PN') {
                                      return ($(this).text() == 'Pitcairn Islands');
                                    } else if (countryval == 'PL') {
                                      return ($(this).text() == 'Poland');
                                    } else if (countryval == 'PT') {
                                      return ($(this).text() == 'Portugal');
                                    } else if (countryval == 'PR') {
                                      return ($(this).text() == 'Puerto Rico');
                                    } else if (countryval == 'QA') {
                                      return ($(this).text() == 'Qatar');
                                    } else if (countryval == 'CG') {
                                      return ($(this).text() == 'Republic of the Congo');
                                    } else if (countryval == 'RE') {
                                      return ($(this).text() == 'Reunion');
                                    } else if (countryval == 'RO') {
                                      return ($(this).text() == 'Romania');
                                    } else if (countryval == 'RU') {
                                      return ($(this).text() == 'Russia');
                                    } else if (countryval == 'RW') {
                                      return ($(this).text() == 'Rwanda');
                                    } else if (countryval == 'BL') {
                                      return ($(this).text() == 'Saint Barthelemy');
                                    } else if (countryval == 'SH') {
                                      return ($(this).text() == 'Saint Helena, Ascension & Tristan daCunha');
                                    } else if (countryval == 'KN') {
                                      return ($(this).text() == 'Saint Kitts and Nevis');
                                    } else if (countryval == 'LC') {
                                      return ($(this).text() == 'Saint Lucia');
                                    } else if (countryval == 'MF') {
                                      return ($(this).text() == 'Saint Martin');
                                    } else if (countryval == 'PM') {
                                      return ($(this).text() == 'Saint Pierre and Miquelon');
                                    } else if (countryval == 'VC') {
                                      return ($(this).text() == 'Saint Vincent and Grenadines');
                                    } else if (countryval == 'WS') {
                                      return ($(this).text() == 'Samoa');
                                    } else if (countryval == 'SM') {
                                      return ($(this).text() == 'San Marino');
                                    } else if (countryval == 'ST') {
                                      return ($(this).text() == 'Sao Tome and Principe');
                                    } else if (countryval == 'SA') {
                                      return ($(this).text() == 'Saudi Arabia');
                                    } else if (countryval == 'SN') {
                                      return ($(this).text() == 'Senegal');
                                    } else if (countryval == 'RS') {
                                      return ($(this).text() == 'Serbia');
                                    } else if (countryval == 'SC') {
                                      return ($(this).text() == 'Seychelles');
                                    } else if (countryval == 'SL') {
                                      return ($(this).text() == 'Sierra Leone');
                                    } else if (countryval == 'SG') {
                                      return ($(this).text() == 'Singapore');
                                    } else if (countryval == 'SK') {
                                      return ($(this).text() == 'Slovakia');
                                    } else if (countryval == 'SI') {
                                      return ($(this).text() == 'Slovenia');
                                    } else if (countryval == 'SB') {
                                      return ($(this).text() == 'Solomon Islands');
                                    } else if (countryval == 'SO') {
                                      return ($(this).text() == 'Somalia');
                                    } else if (countryval == 'ZA') {
                                      return ($(this).text() == 'South Africa');
                                    } else if (countryval == 'GS') {
                                      return ($(this).text() == 'South Georgia & South Sandwich Islands');
                                    } else if (countryval == 'KR') {
                                      return ($(this).text() == 'South Korea');
                                    } else if (countryval == 'ES') {
                                      return ($(this).text() == 'Spain');
                                    } else if (countryval == 'LK') {
                                      return ($(this).text() == 'Sri Lanka');
                                    } else if (countryval == 'SD') {
                                      return ($(this).text() == 'Sudan');
                                    } else if (countryval == 'SR') {
                                      return ($(this).text() == 'Suriname');
                                    } else if (countryval == 'SJ') {
                                      return ($(this).text() == 'Svalbard and Jan Mayen');
                                    } else if (countryval == 'SZ') {
                                      return ($(this).text() == 'Swaziland');
                                    } else if (countryval == 'SE') {
                                      return ($(this).text() == 'Sweden');
                                    } else if (countryval == 'CH') {
                                      return ($(this).text() == 'Switzerland');
                                    } else if (countryval == 'SY') {
                                      return ($(this).text() == 'Syria');
                                    } else if (countryval == 'TW') {
                                      return ($(this).text() == 'Taiwan');
                                    } else if (countryval == 'TJ') {
                                      return ($(this).text() == 'Tajikistan');
                                    } else if (countryval == 'TZ') {
                                      return ($(this).text() == 'Tanzania');
                                    } else if (countryval == 'TH') {
                                      return ($(this).text() == 'Thailand');
                                    } else if (countryval == 'TL') {
                                      return ($(this).text() == 'Timor-Leste');
                                    } else if (countryval == 'TG') {
                                      return ($(this).text() == 'Togo');
                                    } else if (countryval == 'TK') {
                                      return ($(this).text() == 'Tokelau');
                                    } else if (countryval == 'TO') {
                                      return ($(this).text() == 'Tonga');
                                    } else if (countryval == 'TT') {
                                      return ($(this).text() == 'Trinidad and Tobago');
                                    } else if (countryval == 'TN') {
                                      return ($(this).text() == 'Tunisia');
                                    } else if (countryval == 'TR') {
                                      return ($(this).text() == 'Turkey');
                                    } else if (countryval == 'TM') {
                                      return ($(this).text() == 'Turkmenistan');
                                    } else if (countryval == 'TC') {
                                      return ($(this).text() == 'Turks and Caicos Islands');
                                    } else if (countryval == 'TV') {
                                      return ($(this).text() == 'Tuvalu');
                                    } else if (countryval == 'UG') {
                                      return ($(this).text() == 'Uganda');
                                    } else if (countryval == 'UA') {
                                      return ($(this).text() == 'Ukraine');
                                    } else if (countryval == 'AE') {
                                      return ($(this).text() == 'United Arab Emirates');
                                    } else if (countryval == 'VI') {
                                      return ($(this).text() == 'United States Virgin Islands');
                                    } else if (countryval == 'UY') {
                                      return ($(this).text() == 'Uruguay');
                                    } else if (countryval == 'UZ') {
                                      return ($(this).text() == 'Uzbekistan');
                                    } else if (countryval == 'VU') {
                                      return ($(this).text() == 'Vanuatu');
                                    } else if (countryval == 'VA') {
                                      return ($(this).text() == 'Vatican City');
                                    } else if (countryval == 'VE') {
                                      return ($(this).text() == 'Venezuela');
                                    } else if (countryval == 'VN') {
                                      return ($(this).text() == 'Vietnam');
                                    } else if (countryval == 'WF') {
                                      return ($(this).text() == 'Wallis and Futuna');
                                    } else if (countryval == 'EH') {
                                      return ($(this).text() == 'Western Sahara');
                                    } else if (countryval == 'YE') {
                                      return ($(this).text() == 'Yemen');
                                    } else if (countryval == 'ZM') {
                                      return ($(this).text() == 'Zambia');
                                    } else if (countryval == 'ZW') {
                                      return ($(this).text() == 'Zimbabwe');
                                    } else {
                                      return ($(this).text() == 'Select Country');
                                    }
                                }).prop('selected', true);

                            </script>

                      </tr>

                      </div>
                      <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                      <div class="col-md-2"></div>
                      <label class="col-md-4 control-label">Support Number*</label>
                      <div class="col-md-1">
                        <input class="form-control" type="text" maxlength="05"  name="phonecc" size="35" size="20"placeholder="phonecc" value="<%=phonecc%>">
                      </div>
                      <div class="col-md-3">
                        <input class="form-control" type="text" maxlength="15"  name="telno" size="35" value="<%=phone%>" size="20">
                      </div>
                      <div class="col-md-6"></div>
                  </div>

                  <input type="hidden" name="emailtoken" value="<%=etoken%>">




                  <div class="widget-header transparent">
                      <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Contact Details</strong></h2>
                      <div class="additional-btn">
                        <%--<a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                        <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                        <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>--%>
                      </div>
                  </div>
                  <div class="widget-content padding">
                    <div id="horizontal-form">                        <%-- End Radio Button--%>
                      <div class="form-group col-md-12 has-feedback">
                        <div class="table-responsive">

                          <%-- <table align="center" width="90%" border="1">--%>
                          <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                            <tr id="mytabletr">
                              <td align="left" style="background-color: #7eccad !important;color: white;">Main&nbsp;Contact*:</td>
                              <td align="center">
                                <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_persons)%>" name="contact_persons" placeholder="Name*" >
                              </td>
                              <td align="center">
                                <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_emails)%>" name="contact_emails" placeholder="Email*" >
                              </td>
                              <td align="center">
                                <input class="form-control" type="text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(mainContact_cCmailid)%>" name="maincontact_ccmailid" placeholder="Email Cc"  >
                              </td>
                              <td align="center">
                                <input class="form-control" type="text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(mainContact_bcCmailid)%>" name="maincontact_bccmailid" placeholder="Email Bcc">
                              </td>

                              <td align="center">
                                <input class="form-control" type="Text" maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute(mainContact_phone)%>" name="maincontact_phone" size="35" placeholder="Phone">
                              </td>
                            </tr>

                            <tr id="mytabletr">
                              <td align="left" style="background-color: #7eccad !important;color: white;">Customer&nbsp;Support*:</td>
                              <td align="center">
                                <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_persons)%>" name="support_persons" placeholder="Name*">
                              </td>
                              <td align="center">
                                <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_emails)%>" name="support_emails" placeholder="Email*">
                              </td>
                              <td align="center">
                                <input class="form-control" type="text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_cCmailid)%>" name="support_ccmailid" placeholder="Email Cc" >
                              </td>

                              <td align="center">
                                <input class="form-control" type="text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_bcCmailid)%>" name="support_bccmailid" placeholder="Email Bcc">

                              </td>
                              <td align="center">
                                <input class="form-control" type="Text" maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_phone)%>" name="support_phone"  placeholder="Phone">
                              </td>

                            </tr>
                            <tr id="mytabletr">
                              <td align="left" style="background-color: #7eccad !important;color: white;">Chargeback&nbsp;Contact:</td>
                              <td align="center">
                                <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_name)%>" name="cbcontact_name" placeholder="Name" >
                              </td>
                              <td align="center">
                                <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_mailId)%>"name="cbcontact_mailid" placeholder="Email" >
                              </td>
                              <td align="center">
                                <input class="form-control" type="text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_cCmailId)%>" name="cbcontact_ccmailid" placeholder="Email Cc"  >
                              </td>
                              <td align="center">
                                <input class="form-control" type="text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_bcCmailId)%>" name="cbcontact_bccmailid" placeholder="Email Bcc">
                              </td>
                              <td align="center">
                                <input class="form-control" type="Text" maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_phone)%>" name="cbcontact_phone" size="35" placeholder="Phone">
                              </td>
                            </tr>
                            <tr id="mytabletr">
                              <td align="left" style="background-color: #7eccad !important;color: white;">Refund&nbsp;Contact:</td>
                              <td align="center">
                                <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_name)%>" name="refundcontact_name" placeholder="Name" >
                              </td>
                              <td align="center"class="text">
                                <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_mailId)%>"name="refundcontact_mailid" placeholder="Email" >
                              </td>
                              <td align="center" >
                                <input class="form-control" type="text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_cCmail)%>" name="refundcontact_ccmailid" placeholder="Email Cc"  >
                              </td>

                              <td align="center">
                                <input class="form-control" type="text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_bcCmail)%>" name="refundcontact_bccmailid" placeholder="Email Bcc">
                              </td>
                              <td align="center"class="text">
                                <input class="form-control" type="Text" maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_phone)%>" name="refundcontact_phone" size="35" placeholder="Phone">
                              </td>
                            </tr>
                            <tr id="mytabletr">
                              <td align="left" style="background-color: #7eccad !important;color: white;">Sales&nbsp;Contact:</td>
                              <td align="center">
                                <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_name)%>" name="salescontact_name" placeholder="Name" >
                              </td>
                              <td align="center" class="text">
                                <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_mailid)%>"name="salescontact_mailid" placeholder="Email" >
                              </td>
                              <td align="center">
                                <input class="form-control" type="text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_cCmailId)%>" name="salescontact_ccmailid" placeholder="Email Cc"  >
                              </td>

                              <td align="center">
                                <input class="form-control" type="text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_bcCmailId)%>" name="salescontact_bccmailid" placeholder="Email Bcc">
                              </td>
                              <td align="center"class="text">
                                <input class="form-control" type="Text" maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_phone)%>" name="salescontact_phone" size="35" placeholder="Phone">
                              </td>
                            </tr>
                            <tr id="mytabletr">
                              <td align="left" style="background-color: #7eccad !important;color: white;">Billing&nbsp;Contact:</td>
                              <td align="center">
                                <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_name)%>" name="billingcontact_name" placeholder="Name" >
                              </td>
                              <td align="center"class="text">
                                <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_mailid)%>"name="billingcontact_mailid" placeholder="Email" >
                              </td>
                              <td align="center">
                                <input class="form-control" type="text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_cCmailId)%>" name="billingcontact_ccmailid" placeholder="Email CC"  >
                              </td>

                              <td align="center">
                                <input class="form-control" type="text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_bcCmailId)%>" name="billingcontact_bccmailid" placeholder="Email Bcc">
                              </td>
                              <td align="center">
                                <input class="form-control" type="Text" maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_phone)%>" name="billingcontact_phone" size="35" placeholder="Phone">
                              </td>
                            </tr>
                            <tr id="mytabletr">
                              <td align="left" style="background-color: #7eccad !important;color: white;">Fraud&nbsp;Contact:</td>
                              <td align="center">
                                <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_name)%>" name="fraudcontact_name" placeholder="Name" >
                              </td>
                              <td align="center"class="text">
                                <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_mailid)%>" name="fraudcontact_mailid" placeholder="Email" >
                              </td>
                              <td align="center">
                                <input class="form-control" type="text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_cCmailId)%>" name="fraudcontact_ccmailid" placeholder="Email Cc"  >
                              </td>

                              <td align="center">
                                <input class="form-control" type="text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_bcCmailId)%>" name="fraudcontact_bccmailid" placeholder="Email Bcc">
                              </td>
                              <td align="center"class="text">
                                <input class="form-control" type="Text" maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_phone)%>" name="fraudcontact_phone" size="35" placeholder="Phone">
                              </td>
                            </tr>
                            <tr id="mytabletr">
                              <td align="left" style="background-color: #7eccad !important;color: white;">Technical&nbsp;Contact:</td>
                              <td align="center">
                                <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_name)%>" name="technicalcontact_name" placeholder="Name" >
                              </td>
                              <td align="center"class="text">
                                <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_mailId)%>" name="technicalcontact_mailid" placeholder="Email" >
                              </td>
                              <td align="center">
                                <input class="form-control" type="text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_cCmailId)%>" name="technicalcontact_ccmailid" placeholder="Email Cc"  >
                              </td>
                              <td>
                                <input class="form-control" type="text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_bcCmailId)%>" name="technicalcontact_bccmailid" placeholder="Email Bcc">
                              </td>
                              <td align="center"class="text">
                                <input class="form-control" type="Text" maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_phone)%>" name="technicalcontact_phone" size="35" placeholder="Phone">
                              </td>
                            </tr>
                          </table>
                        </div>
                      </div>
                      <div class="form-group col-md-12 has-feedback">
                        <center>
                          <label >&nbsp;</label>
                          <input type="hidden" value="1" name="step">
                          <%--<button id="submit" type="submit" class="btn btn-default" style="display: -webkit-box;" data-toggle="modal" data-target="#myModal"><i class="fa fa-save"></i>&nbsp;&nbsp;Save</button>--%>
                          <button id="btnSubmit" type="button" class="btn btn-default" style="display: -webkit-box;" data-toggle="modal" data-target="#myModal"><i class="fa fa-save"></i>&nbsp;&nbsp;Save</button>
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