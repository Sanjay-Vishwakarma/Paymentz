<%--
  Created by IntelliJ IDEA.
  User: Mahima
  Date: 6/25/18
  Time: 1:48 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.User" %>

<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="java.util.List" %>
<%@ page import="com.directi.pg.fileupload.FileUploadBean" %>
<%@ include file="index.jsp" %>
<html>
<head>
  <title>Block Phone Upload</title>
  <script type="text/javascript" language="JavaScript">
    function downLoadExcel()
    {
      $('#search').val('download');
      $('#uploadForm').removeAttr('enctype');
    }
    function check()
    {
      var  retpath = document.FIRCForm.File.value;
      var pos = retpath.lastIndexOf(".");
      var filename="";
      if (pos != -1)
        filename = retpath.substring(pos + 1);
      else
        filename = retpath;
      if (filename==('xls')) {
        return true;
      }
      alert('Please select a .xls file instead!');
      return false;
    }
  </script>
</head>
<body>
<%
  ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    String error= (String) request.getAttribute("ERROR");
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading">
        Upload Block Phone
        <div style="float: right;">
          <form action="/icici/blockphone.jsp?ctoken=<%=ctoken%>" method="POST">
            <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
            <button type="submit" class="addnewmember">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Upload Block Phone
            </button>
          </form>
        </div>
      </div>
      <form name = "FIRCForm" id="uploadForm" action="/icici/servlet/UploadBlockPhone?ctoken=<%=ctoken%>" method="post" ENCTYPE="multipart/form-data" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <input type="hidden" value="" name="perfromAction" id="search">
        <br>
        <%
          String errorMsg=(String) request.getAttribute("errorMsg");
          FileUploadBean fub=new FileUploadBean();
          String filename=(String) request.getAttribute("File")==null?"":(String)request.getAttribute("File");

          if(errorMsg !=null)
          {
            out.println("<center><font class=\"textb\"><b>"+errorMsg+"</b></font></center>");
          }
        %>
        <table  border="0" cellpadding="5" cellspacing="0" align="center">
          <tr>
            <td>
              <table  border="0" cellpadding="5" cellspacing="0" align="center">
                <tr>
                  <td colspan="6"></td>
                </tr>
                <tr>
                  <td colspan="6"></td>
                </tr>
                <tr>
                  <td colspan="6">&nbsp;</td>
                </tr>

                <tr>
                  <td colspan="6">&nbsp;</td>
                </tr>
                <tr>
                  <td class="textb" colspan="2" align="left"><b>Update Block Phone:&nbsp;</b></td>
                  <td colspan="4" align="center"><input name="File" type="file" value="choose File"
                                                        style="width: 280px"></td>
                </tr>
                <tr>
                  <td colspan="6">&nbsp;</td>
                </tr>
                <tr>
                  <td colspan="6" align="center">
                    <button name="mybutton" type="submit" value="Upload" class="buttonform" onclick="return check()">Upload</button>
                  </td>
                  <td colspan="3" align="left">
                    <button name="download" type="submit" value="downloadExcel"  id="downloadExcel" class="buttonform" onclick="downLoadExcel()" style="width: 150px; height: 30px;">Download Excel Format</button>
                  </td>
                </tr>
                <tr>
                  <td colspan="6">&nbsp;</td>
                </tr>
              </table>
        </table>
      </form>
    </div>
  </div>
  <%
    String message = (String) request.getAttribute("error");
    if (message != null)
    {
  %>
  <div style="padding-left: 0;padding-right: 30px;">
    <div class="col-lg-12" style="background-color: white; width: 100%; border: 1px solid black; border-radius: 4px; padding:15px;">
      <%
        String str="";
        str = str + "<table bgcolor=\"#ecf0f1\" width=\"100%\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\">";
        str = str + "<table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" align=\"center\" class=\"table table-striped table-bordered table-hover table-green dataTable\">";
        str = str + message;
        str = str + "</table></table>";
        out.println(str);
      %>
    </div>
  </div>
  <%
      }
    }
    else
    {
      response.sendRedirect("/icici/logout.jsp");
      return;
    }
  %>
</div>
</body>
</html>