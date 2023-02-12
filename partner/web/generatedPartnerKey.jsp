<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.ResourceBundle" %>
<%--
  Created by IntelliJ IDEA.
  User: Sneha
  Time: 8:11 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp"%>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","generatedPartnerKey");
  ResourceBundle rb1 = null;
  String language_property1 = (String) session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String generatedPartnerKey_Key_Generated = StringUtils.isNotEmpty(rb1.getString("generatedPartnerKey_Key_Generated")) ? rb1.getString("generatedPartnerKey_Key_Generated") : "Key Generated";
  String generatedPartnerKey_Note = StringUtils.isNotEmpty(rb1.getString("generatedPartnerKey_Note")) ? rb1.getString("generatedPartnerKey_Note") : "Note";
  String generatedPartnerKey_confidential = StringUtils.isNotEmpty(rb1.getString("generatedPartnerKey_confidential")) ? rb1.getString("generatedPartnerKey_confidential") : "The generated key is highly confidential information and must not be shared to anyone including our system and support team. In case by any chance it has been shared to anyone, we suggest you to report to us and change it immediately. ";
  String generatedPartnerKey_secret_key = StringUtils.isNotEmpty(rb1.getString("generatedPartnerKey_secret_key")) ? rb1.getString("generatedPartnerKey_secret_key") : "You are the owner of this secret key and to maintain its confidentiality is your responsibility and only you will be held responsible if it has been leaked and misused. We can't see this key or never ask to share this information to us or to anyone.";
  String generatedPartnerKey_New_Key_Generated = StringUtils.isNotEmpty(rb1.getString("generatedPartnerKey_New_Key_Generated")) ? rb1.getString("generatedPartnerKey_New_Key_Generated") : "New Key Generated";

%>
<html>
<head>
  <title><%=company%> | Partner Secret Key</title>

  <style type="text/css">

    /*        #textareaid{
                width: 31%;
            }
            */
    @media (min-width: 441px) {
      #textareaid{
        width: inherit;
      }
    }

    @media (max-width: 440px) {
      #textareaid{
        padding: 0;
      }
    }


    #generatekeyid{
      border: 1px solid #b2b2b2;
      font-weight: bold;
      word-break: break-all;
      color: #1e8b92;
      background-color: rgb(255, 255, 255);
      border-color: transparent;
      font-size: 15px;
      cursor: text;
      width: 50%;
      height: inherit;
    }

    @media (max-width: 640px) {
      #generatekeyid{
        width: inherit;
      }
    }

  </style>


</head>
<body>


<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">

      <div class="row">

        <div class="col-sm-12 portlets ui-sortable">
          <%--<div class="widget green-1" style="background-color:  #68c39f;color: #fff;font-family: Helvetica Neue, Helvetica, Arial, sans-serif;font-size: 14px;line-height: 1.42857143;">--%>
          <div class="widget">

            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=generatedPartnerKey_Key_Generated%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <div class="widget-content padding">

              <%
                String key = (String) request.getAttribute("key");
                String errMsg = (String) request.getAttribute("errorMsg");
                Functions functions = new Functions();
              %>

              <div class="form-group col-md-12 has-feedback">
                <label class="bg-info" style="font-family:Open Sans;font-size: 13px;font-weight: 600; margin-left: 1%;">

                  <%
                    if(functions.isValueNull(errMsg))
                    {
                  %>

                  <p style="background: #34495e;color:#ffffff;height:20px;border-radius:4px;width:400px;">&nbsp;&nbsp;<%=errMsg%></p>

                  <%
                  }
                  else {
                  %>

                  <i class="fa fa-info-circle"></i>&nbsp;<%=generatedPartnerKey_Note%><br><br>

                 <%=generatedPartnerKey_confidential%>
                  <br><br>
                  <%=generatedPartnerKey_secret_key%>
                  <br><br><br><br>

                  <div class="form-group col-md-12 has-feedback">
                    <center>
                      <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><h4><b><%=generatedPartnerKey_New_Key_Generated%></b></h4></label>
                      <%--<input type="text" size="32" class="form-control" value=<%=key%> disabled style="border: 1px solid #b2b2b2;font-weight:bold; width: 32%">--%>
                      <label class="form-control" id="generatekeyid" style="border: 1px solid #b2b2b2;font-weight:bold;word-break: break-all;"><%=key%></label>
                    </center>
                  </div>
                </label>
              </div>
              <%
                }
              %>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>


</body>
</html>
