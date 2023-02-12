<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.manager.PartnerManager" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.ResourceBundle" %>
<%--
  Created by IntelliJ IDEA.
  User: Sneha
  Date: 1/8/2016
  Time: 7:34 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp"%>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","generatePartnerKey");

  PartnerManager partnerManager = new PartnerManager();
  String partnerKey = partnerManager.getPartnerKey((String) session.getAttribute("merchantid"));
  ResourceBundle rb1 = null;
  String language_property1 = (String) session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String generatePartnerKey_Secret_Key_Generation = StringUtils.isNotEmpty(rb1.getString("generatePartnerKey_Secret_Key_Generation")) ? rb1.getString("generatePartnerKey_Secret_Key_Generation") : "Secret Key Generation";
  String generatePartnerKey_Note = StringUtils.isNotEmpty(rb1.getString("generatePartnerKey_Note")) ? rb1.getString("generatePartnerKey_Note") : "Note";
  String generatePartnerKey_while_hitting = StringUtils.isNotEmpty(rb1.getString("generatePartnerKey_while_hitting")) ? rb1.getString("generatePartnerKey_while_hitting") : "It will be used while hitting";
  String generatePartnerKey_APIs = StringUtils.isNotEmpty(rb1.getString("generatePartnerKey_APIs")) ? rb1.getString("generatePartnerKey_APIs") : "APIs.";
  String generatePartnerKey_case = StringUtils.isNotEmpty(rb1.getString("generatePartnerKey_case")) ? rb1.getString("generatePartnerKey_case") : "In case by any chance it has been shared to anyone, we suggest you to report to us  and change it immediately.";
  String generatePartnerKey_current_key = StringUtils.isNotEmpty(rb1.getString("generatePartnerKey_current_key")) ? rb1.getString("generatePartnerKey_current_key") : "Your current key :";
  String generatePartnerKey_Generate_Key = StringUtils.isNotEmpty(rb1.getString("generatePartnerKey_Generate_Key")) ? rb1.getString("generatePartnerKey_Generate_Key") : "Generate Key";

%>
<html>
<head>
  <title><%=company%> Partner> Generate Secret Key</title>

  <script language="javascript">
    function isint(form)
    {
      if (isNaN(form.numrows.value))
        return false;
      else
        return true;
    }

    function DoConfirm()
    {
      if (confirm("Do you really want to generate NEW key ?"))
      {
        document.form.submit();
      }
      else
      {
        return false;
      }
    }
  </script>

  <style type="text/css">
    .bg-info{
      width: inherit;
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
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=company%> <%=generatePartnerKey_Secret_Key_Generation%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <div class="widget-content padding">

              <div class="form-group bg-info col-md-12 has-feedback">
                <label style="font-size: 14px;">
                  <%--The following task that you wish to perform will require you to make adjustments to your scripts in your
                  web pages.<br><br>--%>

                  <i class="fa fa-info-circle"></i>&nbsp;<%=generatePartnerKey_Note%><br><br>

                  <%=generatePartnerKey_while_hitting%> <%=company%> <%=generatePartnerKey_APIs%><br><br>

                  <%--Without these changes your web site will not connect with the <%=company%> Payment Gateway Service.<br><br>--%>

                  <%=generatePartnerKey_case%><br><br><br>

                  <%=generatePartnerKey_current_key%> <%=partnerKey%>
                </label>

                <form action="/partner/net/GeneratePartnerKey?ctoken<%=ctoken%>" method="post" name="form1">
                  <div class="form-group col-md-5">
                  </div>
                  <div class="form-group col-md-3">
                    <input type="hidden" value="<%=ctoken%>" name="ctoken">
                    <button type="Submit"  value="Generate Key" name="key" class="btn btn-default" onclick="return DoConfirm();" <%--style="width:135px;margin-left: 39%; margin-top: 22px;"--%>><%=generatePartnerKey_Generate_Key%>
                      <%--<p><a href = "javascript:void(0)" style="colo" onclick = "document.getElementById('light').style.display='block';document.getElementById('fade').style.display='block'">

                      </a></p>--%>
                    </button>
                  </div>
                </form>

              </div>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</div>

</body>
</html>