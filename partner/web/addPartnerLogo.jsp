<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.manager.enums.TemplatePreference" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ include file="top.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 2/3/2017
  Time: 8:20 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
    session.setAttribute("submit","checkout_configuration");
  %>
  <title><%=company%> | Merchant Transaction Processing </title>
  <style type="text/css">
    @media (max-width: 640px){
      #smalltable{
        width: 100%!important;
      }

    }


    @media (min-width: 641px) {
      #flightid {
        width: inherit;
      }
    }

    #smalltable{width: 50%;}
    @media (max-width: 767px){
      #smalltable{width: inherit;}
    }

    @media (min-width: 768px) and (max-width: 991px){
      #smalltable{width: inherit;}
    }

  </style>
</head>
<%--<script type="text/javascript">
  function myUpload()
  {
    document.getElementById('myformname').submit();
  }
</script>--%>
<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
<script src="/partner/NewCss/js/jquery-ui.min.js"></script>
<script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
<script type="text/javascript">

          function ValidateFileUpload(event) {
            //alert("Inside function");
            var fuData = document.getElementById('filepathnew');
            var FileUploadPath = fuData.value;

            //To check if user upload any file
            if (FileUploadPath == '')
            {
              alert("Please upload an image");
              event.preventDefault();
            }

            else

            {

              var Extension = FileUploadPath.substring(
                      FileUploadPath.lastIndexOf('.') + 1).toLowerCase();

            //The file uploaded is an image

              if (Extension == "gif" || Extension == "png" || Extension == "bmp"
                      || Extension == "jpeg" || Extension == "jpg") {

              // To Display
                if (fuData.files && fuData.files[0]) {
                  var reader = new FileReader();

                  reader.onload = function(e) {
                    $('#blah').attr('src', e.target.result);
                  }

                  reader.readAsDataURL(fuData.files[0]);
                }



              }

              //The file upload is NOT an image
              else {
                alert("Allows only file types of GIF, PNG, JPG, JPEG and BMP. ");

              }

              return true;
            }
          }

</script>
<body class="bodybackground">
<%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (partner.isLoggedInPartner(session))
  {
    LinkedHashMap memberidDetails=partner.getPartnerMembersDetail((String)session.getAttribute("merchantid"));
    String memberid=nullToStr(request.getParameter("memberid"));
    String pid=nullToStr(request.getParameter("pid"));
    String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
    String Config =null;
    if(Roles.contains("superpartner")){

    }else{
      pid = String.valueOf(session.getAttribute("merchantid"));
      Config = "disabled";
    }
    String partnerid = session.getAttribute("partnerId").toString();
    PartnerFunctions partnerFunctions=new PartnerFunctions();
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String addPartnerLogo_Upload_Logo = StringUtils.isNotEmpty(rb1.getString("addPartnerLogo_Upload_Logo")) ? rb1.getString("addPartnerLogo_Upload_Logo") : "Upload Logo";
    String addPartnerLogo_PartnerID = StringUtils.isNotEmpty(rb1.getString("addPartnerLogo_PartnerID")) ? rb1.getString("addPartnerLogo_PartnerID") : "Partner ID";
    String addPartnerLogo_MerchantID = StringUtils.isNotEmpty(rb1.getString("addPartnerLogo_MerchantID")) ? rb1.getString("addPartnerLogo_MerchantID") : "Merchant ID";
    String addPartnerLogo_Upload = StringUtils.isNotEmpty(rb1.getString("addPartnerLogo_Upload")) ? rb1.getString("addPartnerLogo_Upload") : " Upload";
    String addPartnerLogo_logo = StringUtils.isNotEmpty(rb1.getString("addPartnerLogo_logo")) ? rb1.getString("addPartnerLogo_logo") : " Logo Upload";

%>

<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">

      <div class="pull-right">
        <div class="btn-group">
          <%--<form action="/partner/transactionsprocess.jsp?ctoken=<%=ctoken%>" method="post" name="form">--%>
            <form action="/partner/checkoutConfiguration.jsp?ctoken=<%=ctoken%>" method="post" name="form">

            <%
              Enumeration<String> stringEnumeration=request.getParameterNames();
              while(stringEnumeration.hasMoreElements())
              {
                String name=stringEnumeration.nextElement();

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

      <form action="/partner/net/UploadMerchantLogo?ctoken=<%=ctoken%>" enctype="multipart/form-data" method="post">
         <div class="row reporttable">
          <div class="col-md-12">
            <div class="widget">
              <div class="widget-header">
                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=addPartnerLogo_Upload_Logo%></strong></h2>
                <div class="additional-btn">
                  <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                  <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                  <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                </div>
              </div>
              <center>
                <div class="widget-content padding" style="overflow-x: auto;">

                  <%
                    String errormsg1 = (String) request.getAttribute("error");
                    if (partnerFunctions.isValueNull(errormsg1))
                    {
                      out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg1 + "</h5>");
                    }
                  %>

                  <%

                    String errormsg = (String)request.getAttribute("cbmessage");
                    if (partnerFunctions.isValueNull(errormsg))
                    {
                      out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;" + errormsg + "</h5>");
                    }


                  %>

                  <input type=hidden name="memberid" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberid)%>">
                  <input type=hidden name="pid" value="<%=ESAPI.encoder().encodeForHTMLAttribute(pid)%>">
                  <input type=hidden name="ctoken" value="<%=ESAPI.encoder().encodeForHTMLAttribute(ctoken)%>">

                  <table align=center  id="smalltable" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">

                    <thead>
                    <tr>
                      <td colspan="2" valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=addPartnerLogo_logo%></b></td>
                    </tr>
                    </thead>

                    <tbody>

                    <tr>
                      <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                      <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">

                      <div class="form-group col-md-3 has-feedback">&nbsp;</div>
                      <div class="form-group col-sm-12 col-md-8">
                        <label class="col-sm-3 control-label"><%=addPartnerLogo_PartnerID%></label>
                        <div class="col-sm-6">
                          <input name="pid" id="pid" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                          <input name="pid" type="hidden" value="<%=pid%>">
                        </div>
                      </div>

                    </tr>

                    <tr>
                      <div class="form-group col-md-3 has-feedback">&nbsp;</div>
                      <div class="form-group col-sm-12 col-md-8">
                        <label class="col-sm-3 control-label"><%=addPartnerLogo_MerchantID%></label>
                        <div class="col-sm-6">
                          <input name="memberid" id="member" value="<%=memberid%>" class="form-control" autocomplete="on">
                         <%-- <select name="memberid" class="form-control">
                            <%
                              String isSelected="";
                              for(Object mid : memberidDetails.keySet())
                              {
                                if(mid.toString().equals(memberid))
                                {
                                  isSelected="selected";
                                }
                                else
                                {
                                  isSelected="";
                                }
                            %>
                            <option value="<%=mid%>" <%=isSelected%>><%=mid+"-"+memberidDetails.get(mid)%></option>
                            <%
                              }
                            %>
                          </select>--%>
                        </div>
                      </div>

                    </tr>

                    <tr>

                      <td valign="middle" data-label="Logo Upload" align="center">
                        <%--<img src="" class="form-control" alt="Please upload the logo"/>--%>
                        <input class="form-control" style="text-align: center;"  value="Please upload the logo" disabled/>
                      </td>

                      <td valign="middle" data-label="Action" align="center">
                        <input type="file" class="btn btn-default" id="filepathnew" name='<%=TemplatePreference.TRANSACTIONPAGEMERCHANTLOGO.toString()%>' />
                      </td>

                    </tr>
                    </tbody>

                  </table>



                  <table align="center" id="smalltable" border="0" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                    <tr>
                      <td align="center" colspan="2">
                        <button type="submit" name="dataFile" id="fileChooser" onclick="return ValidateFileUpload(event)" class="buttonform btn btn-default">
                         <%=addPartnerLogo_Upload%>
                        </button>
                      </td>
                    </tr>
                  </table>

                </div>
              </center>
            </div>
          </div>
        </div>



      </form>

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
<script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
<script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
<link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
<link href="/partner/cookies/cookies_popup.css" rel="stylesheet">
</body>
</html>
<%!
  public static String nullToStr(String str)
  {
    if(str == null)
      return "";
    return str;
  }
%>