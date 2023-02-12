<%@ page import="com.logicboxes.util.ApplicationProperties,
                 org.owasp.esapi.User" %>
<%@ include file="ietest.jsp" %>
<%@ include file="Top.jsp" %>

<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
  session.setAttribute("submit","View Key");

%>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="com.directi.pg.SystemError" %>
<%@ page import="com.manager.dao.MerchantDAO" %>

<html lang="en">
<head>

  <title><%=company%> Merchant Settings > Generate Key</title>
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

</head>
<body class="pace-done widescreen fixed-left-void">



<%
  merchantDAO = new MerchantDAO();

  String memberKey = merchantDAO.getMerchantKey((String) session.getAttribute("merchantid"));

%>


<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">

      <div class="row">

        <div class="col-sm-12 portlets ui-sortable">
          <%--<div class="widget green-1" style="background-color:  #68c39f;color: #fff;font-family: Helvetica Neue, Helvetica, Arial, sans-serif;font-size: 14px;line-height: 1.42857143;">--%>
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;View Key</strong></h2>
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

                  Inorder for your website to connect to the <%=company%> and perform successful transactions you would need to make adjustments to the scripts on your website,using this current key.<br><br>

                  <%--Without these changes your web site will not connect with the <%=company%> Payment Gateway Service.<br><br>--%>



                  Your current key : <%=memberKey%><br><br><br>

                </label>

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
