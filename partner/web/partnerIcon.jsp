<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.manager.enums.TemplatePreference" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ include file="top.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Ajit.k
  Date: 18/09/2019
  Time: 7:11 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
    session.setAttribute("submit","partnerlist");
    String partnerid= String.valueOf(session.getAttribute("partnerId"));
  %>
  <title><%=company%> | Partner Upload Icon</title>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
  <script>

    $(document).ready(function(){

      var w = $(window).width();

      if(w > 990){
        //alert("It's greater than 990px");
        $("body").removeClass("smallscreen").addClass("widescreen");
        $("#wrapper").removeClass("enlarged");


      }
      else{
        //alert("It's less than 990px");
        $("body").removeClass("widescreen").addClass("smallscreen");
        $("#wrapper").addClass("enlarged");
        $(".left ul").removeAttr("style");
      }
    });

  </script>
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

        if (Extension == "jpeg" || Extension == "jpg" || Extension == "png") {

          // To Display
          if (fuData.files && fuData.files[0]) {
            var reader = new FileReader();

            reader.onload = function(e) {
              $('#blah').attr('src', e.target.result);
            }

            reader.readAsDataURL(fuData.files[0]);
            return true;
          }



        }

        //The file upload is NOT an image
        else {
          alert("Allows only file types of JPG , JPEG and PNG.");
          return false;
        }


      }
    }

  </script>
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

<body class="bodybackground">
<%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (partner.isLoggedInPartner(session))
  {
    LinkedHashMap memberidDetails = partner.getPartnerDetails(partnerid);
    String partnerId=nullToStr(request.getParameter("partid"));
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String partnerIcon_Partner_Upload_Icon = StringUtils.isNotEmpty(rb1.getString("partnerIcon_Partner_Upload_Icon")) ? rb1.getString("partnerIcon_Partner_Upload_Icon") : "Partner Upload Icon";
    String partnerIcon_Icon_Upload = StringUtils.isNotEmpty(rb1.getString("partnerIcon_Icon_Upload")) ? rb1.getString("partnerIcon_Icon_Upload") : "Icon Upload";
    String partnerIcon_PartnerID = StringUtils.isNotEmpty(rb1.getString("partnerIcon_PartnerID")) ? rb1.getString("partnerIcon_PartnerID") : "Partner ID";
    String partnerIcon_select = StringUtils.isNotEmpty(rb1.getString("partnerIcon_select")) ? rb1.getString("partnerIcon_select") : "---select---";
    String partnerIcon_image = StringUtils.isNotEmpty(rb1.getString("partnerIcon_image")) ? rb1.getString("partnerIcon_image") : "Image size should not exceed 500KB";
    String partnerIcon_Upload = StringUtils.isNotEmpty(rb1.getString("partnerIcon_Upload")) ? rb1.getString("partnerIcon_Upload") : "Upload";

%>

<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">

      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/partnerlist.jsp?ctoken=<%=ctoken%>" method="post" name="form">
            <button class="btn-xs" type="submit" name="B1" style="background: transparent;border: 0;">
              <img style="height: 35px;" src="/partner/images/goBack.png">
            </button>
          </form>
        </div>
      </div>
      <br><br><br>

      <form action="/partner/net/UploadPartnerIcon?ctoken=<%=ctoken%>" enctype="multipart/form-data" method="post">
        <div class="row reporttable">
          <div class="col-md-12">
            <div class="widget">
              <div class="widget-header">
                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerIcon_Partner_Upload_Icon%></strong></h2>
                <div class="additional-btn">
                  <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                  <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                  <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                </div>
              </div>
              <center>
                <div class="widget-content padding" style="overflow-x: auto;">

                  <%
                    if (request.getParameter("MES") != null)
                    {
                      String mes = request.getParameter("MES");
                      if (mes.equals("F"))
                      {
                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp; Please Select Partner ID.</h5>");
                      }else if(mes.equals("S")){
                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp; Invalid file or file Name(File Name should not contain white space)</h5>");
                      }
                      else if (mes.equals("FS"))
                      {
                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp; File size should not exceed 500KB </h5>");
                      }
                      else
                      {
                        out.println("<h5 class=\"bg-alert\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i></i>&nbsp;&nbsp; Icon File Uploaded Successfully.</h5>");
                      }
                    }

                  %>

                  <table align=center  id="smalltable" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">

                    <thead>
                    <tr>
                      <td colspan="2" valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerIcon_Icon_Upload%></b></td>
                    </tr>
                    </thead>

                    <tbody>

                    <tr>
                      <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                      <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                      <div class="form-group col-md-3 has-feedback">&nbsp;</div>
                      <div class="form-group col-sm-12 col-md-8">
                        <label class="col-sm-3 control-label"><%=partnerIcon_PartnerID%></label>
                        <div class="col-sm-6">
                          <%--<input name="partnerid" id="mid" value="<%=memberid%>" class="form-control" autocomplete="on">--%>
                          <select name="partid" class="form-control">
                            <option value=""><%=partnerIcon_select%></option>
                            <%
                              String isSelected="";
                              for(Object mid : memberidDetails.keySet())
                              {
                                if(mid.toString().equals(partnerId))
                                {
                                  isSelected="selected";
                                }
                                else
                                {
                                  isSelected="";
                                }
                            %>
                            <option value="<%=mid%>" <%=isSelected%>><%=memberidDetails.get(mid)%></option>
                            <%
                              }
                            %>
                          </select>
                        </div>
                      </div>

                    </tr>

                    <tr>

                      <td valign="middle" data-label="Icon Upload" align="center">
                        <%--<img src="" class="form-control" alt="Please upload the logo"/>--%>
                        <input class="form-control" style="text-align: center;"  value="Please upload the Icon" disabled/>
                        <input class="form-control" style="text-align: center;"  value="( Ex. Icon.jpg / Icon.jpeg / Icon.png )" disabled/>
                      </td>

                      <td valign="middle" data-label="Action" align="center">
                        <input type="file" class="btn btn-default form-control" id="filepathnew" name='<%=TemplatePreference.TRANSACTIONPAGEMERCHANTLOGO.toString()%>' />
                        <label><%=partnerIcon_image%></label>
                      </td>

                    </tr>
                    </tbody>

                  </table>



                  <table align="center" id="smalltable" border="0" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                    <tr>
                      <td align="center" colspan="2">
                        <button type="submit" name="dataFile" id="fileChooser" onclick="return ValidateFileUpload(event)" class="buttonform btn btn-default">
                          <%=partnerIcon_Upload%>
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

