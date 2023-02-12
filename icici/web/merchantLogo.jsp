<%@ page import="java.util.*,com.directi.pg.Functions,com.directi.pg.Merchants" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.manager.enums.TemplatePreference" %>
<%@ include file="index.jsp"%>
<%@ include file="functions.jsp"%>

<%--
  Created by IntelliJ IDEA.
  User: Kanchan
  Date: 08-03-2021
  Time: 15:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>

  <script type="text/javascript">

    function check(event)
    {
      var fdata= document.getElementById('filepathnew');
      var FileUploadPath= fdata.value;

      if(FileUploadPath == '')
      {
        alert("Please upload an image");
        event.preventDefault();
      }

      else
      {
        var extension= FileUploadPath.substring(FileUploadPath.lastIndexOf('.')+1).toLowerCase();
        if(extension=='gif' || extension=='jpg' || extension=='jpeg' || extension=='bmp' || extension=='png')
        {
          if(fdata.files && fdata.files[0]){
             var reader= new FileReader();

            reader.onload = function(e) {
              $('#blah').attr('src', e.target.result);
            }
            reader.readAsDataURL(fdata.files[0]);
          }
        }

        else{
          alert("Allows only file types of GIF, PNG, JPG, JPEG and BMP. ");
        }
        return true;
      }
    }
  </script>
</head>

<%
  ctoken= ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    String memberid= nullToStr(request.getParameter("memberid"));

%>
<body class="bodybackground">
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Merchant Logo
        <div style="float: right;">
          <form action="/icici/servlet/MerchantDetails?ctoken=<%=ctoken%>" method="POST">

            <button type="submit" name="submit" class="addnewmember">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Merchant Details
            </button>
          </form>
        </div>
      </div>
      <br>

      <table align="center" width="70%" cellpadding="2" cellspacing="2">
        <form action="/icici/servlet/UploadMerchantLogo?ctoken=<%=ctoken%>" method="post" name="form1" ENCTYPE="multipart/form-data">
          <input type="hidden" name="ctoken" value="<%=ctoken%>" id="ctoken">

          <%
            String errormsg= (String)request.getAttribute("error");
            if (errormsg != null)
            {
              out.println("<center><font class=\"textb\"><b>"+errormsg+"<br></b></font></center>");
            }
          %>

          <%
            String cbmessage= (String)request.getAttribute("cbmessage");
            if (cbmessage != null)
            {
              out.println("<center><font class=\"textb\"><b>"+cbmessage+"<br></b></font></center>");
            }
          %>
          <input type=hidden name="ctoken" value="<%=ESAPI.encoder().encodeForHTMLAttribute(ctoken)%>">
          <input type=hidden name="memberid" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberid)%>">
          <tr>
            <td>
              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center" >

                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Merchant ID</td>
                  <td class="textb">:</td>
                  <td class="textb">
                    <input name="memberid" id="mid" value="<%=memberid%>" class="txtbox" autocomplete="on">
                  </td>
                </tr>

                <tr><td colspan="4">&nbsp;</td> </tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb"><span class="textb">Logo Name*</span><br>
                    (Ex. logo.jpg / logo.jpeg / logo.png)</td>
                  </td>
                  <td class="textb">:</td>
                  <td class="textb"><input type="file" id="filepathnew" size="35" maxlength="30" name='<%=TemplatePreference.TRANSACTIONPAGEMERCHANTLOGO.toString()%>'></td>
                </tr>

                <tr><td colspan="4">&nbsp;</td></tr>

                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb"></td>
                  <td class="textb"></td>
                  <td>
                    <input type="submit" name="submit" value="Upload" id="fileChooser" class="buttonform" onclick="return check(event)">
                  </td>
                </tr>

                <tr><td colspan="4">&nbsp;</td></tr>
              </table>
            </td>
          </tr>

        </form>
      </table>
    </div>
  </div>
</div>
<%
  }
  else
  {
    response.sendRedirect("/icici/logout.jsp");
    return;
  }
%>
</body>
<%!
   public static String nullToStr(String str)
   {
     if (str==null)
       return "";
     return str;
   }
%>
</html>
