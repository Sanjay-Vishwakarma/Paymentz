<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="com.manager.enums.TemplatePreference" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="net.partner.PartnerFunctions" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="com.directi.pg.FileUploadBean" %>
<%@ include file="top.jsp"%>

<%--
  Created by IntelliJ IDEA.
  User: Kalyani
  Date: 28/01/2022
  Time: 13:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
    session.setAttribute("submit","Black List");
  %>
  <title><%=company%> | Upload Bank Account Number </title>
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

%>

<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">

      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/blockBankAccountNumber.jsp?ctoken=<%=ctoken%>" method="post" name="form">
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

        <form name = "FIRCForm" id="uploadForm" action="/partner/net/UploadBulkBankAccountNumber?ctoken=<%=ctoken%>" method="post" ENCTYPE="multipart/form-data" >
          <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
          <input type="hidden" value="" name="perfromAction" id="search">
          <%
            PartnerFunctions functions = new PartnerFunctions();
            FileUploadBean fub=new FileUploadBean();
            String errorMsg = (String) request.getAttribute("errorMsg");
            String sMessage = (String) request.getAttribute("error");
            if (functions.isValueNull(errorMsg))
            {
              out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errorMsg + "</h5>");
            }
            if(functions.isValueNull(sMessage))
            {
              out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;"+sMessage+"</h5>");
            }

          %>
        <div class="row reporttable">
          <div class="col-md-12">
            <div class="widget">
              <div class="widget-header">
                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Upload Bank Account Number</strong></h2>
                <div class="additional-btn">
                  <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                  <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                  <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                </div>
              </div>
              <center>
                <div class="widget-content padding" style="overflow-x: auto;">

                 <%-- <%
                    String errormsg1 = (String) request.getAttribute("error");
                    if (partnerFunctions.isValueNull(errormsg1))
                    {
                      //out.println("<center><div class=\"textb\" style=\"text-align: center;color: red;\"><b>"+errormsg1+"<br></b></div></center>");
                      out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg1 + "</h5>");
                    }
                  %>

                  <%

                    String errormsg = (String)request.getAttribute("cbmessage");
                    if (partnerFunctions.isValueNull(errormsg))
                    {
                      //out.println("<center><div class=\"textb\" style=\"text-align: center;color: red;\"><b>"+errormsg1+"<br></b></div></center>");
                      out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;" + errormsg + "</h5>");
                    }


                  %>--%>

                  <input type=hidden name="ctoken" value="<%=ESAPI.encoder().encodeForHTMLAttribute(ctoken)%>">

                  <table align=center  id="smalltable" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">

                    <thead>

                    </thead>

                    <tbody>

                    <tr>
                    </tr>

                    <tr>

                      <td valign="middle" data-label="Logo Upload" align="center">
                        <%--<img src="" class="form-control" alt="Please upload the logo"/>--%>
                        <input class="form-control" style="text-align: center;"  value="Please Upload Bank Account Number" disabled/>
                      </td>

                      <td valign="middle" data-label="Action" align="center">
                        <input type="file" class="btn btn-default" id="filepathnew" name="File" value="choose File">
                      </td>

                    </tr>
                    </tbody>

                  </table>

                  <table align="center" id="smalltable" border="0" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                    <tr>
                      <td align="center" colspan="2">
                        <button type="submit" name="mybutton"  value="Upload" onclick="return check()" class="buttonform btn btn-default">
                          Upload
                        </button>
                      </td>
                    </tr>
                    <tr>
                      <td align="center" colspan="2">
                        <button type="submit" name="download" value="downloadExcel"  id="downloadExcel"  onclick="downLoadExcel()" class="buttonform btn btn-default">
                          Download Excel
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
