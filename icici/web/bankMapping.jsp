<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="java.util.List" %>
<%@ page import="com.vo.applicationManagerVOs.AppFileDetailsVO" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.vo.applicationManagerVOs.BankTypeVO" %>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Vishal
  Date: 6/5/2017
  Time: 1:30 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
  Functions functions = new Functions();
  private static Logger logger=new Logger("bankMapping.jsp");
%>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">

  <title>Application Manager> Bank Mapping</title>
</head>
<body>
<%
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {

    String bankID=nullToStr(request.getParameter("bankId"));
    String bankName=nullToStr(request.getParameter("bankName"));
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Bank Template Interface
        <div style="float: right;">
          <form action="/icici/addNewBank.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Add New Charge" name="submit" class="addnewmember">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New Bank Template
            </button>
          </form>
        </div>
        <div style="float: right;">
          <form action="/icici/partnerBankMapping.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Add New Charge" name="submit" class="addnewmember">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Partner Bank Mapping
            </button>
          </form>
        </div>
      </div>

      <form action="/icici/servlet/BankMapping?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <%
          String str="ctoken=" + ctoken;
          str = str+"&isinputrequired="+request.getParameter("isinputrequired");
          List<String> errors = (List<String>) request.getAttribute("sberror");
          if(errors!=null && errors.size()>0 && !errors.isEmpty())
          {
            for (String error : errors)
            {
              out.println("<center><font class=\"textb\"> <b>" + error + "</b></font></center>");
            }
          }
        %>
        <table border="0" align="center" width="100%" cellpadding="2" cellspacing="2" >
          <tr>
            <td>
              <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">

              <table border="0" cellpadding="5" cellspacing="0" width="95%"  align="center">
                <tr><td colspan="11">&nbsp;</td></tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Bank ID</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input  maxlength="15" type="text" name="bankId"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(bankID)%>" class="txtbox">
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Bank Name</td>
                  <td width="5%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input   maxlength="15" type="text" name="bankName" class="txtbox" value="<%=ESAPI.encoder().encodeForHTMLAttribute(bankName)%>">
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" ></td>
                  <td width="10%" class="textb">
                    <button type="submit" class="buttonform" >
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </td>
                </tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
      </form>
    </div>
  </div>
</div>

<div class="reporttable">
  <%
    List<BankTypeVO> bankTypeVOList = (List<BankTypeVO>) request.getAttribute("bankMappingVOList");
    session.setAttribute("bankMappingVOList", bankTypeVOList);

    String error = (String) request.getAttribute("error");
    if(functions.isValueNull(error))
    {
      out.println(error);
    }

    if(bankTypeVOList != null && bankTypeVOList.size() > 0)
    {
      PaginationVO paginationVO = (PaginationVO) request.getAttribute("paginationVO");
      session.setAttribute("paginationVO", paginationVO);
      paginationVO.setInputs(paginationVO.getInputs()+"&ctoken="+ctoken);
      int srno = ((paginationVO.getPageNo()-1)*paginationVO.getRecordsPerPage())+1;
  %>
  <form action="/icici/servlet/UploadNewTemplate?ctoken=<%=ctoken%>" method="post" name="myformname" id="myformname">
    <input type="hidden" name="ctoken" value="<%=ctoken%>">
    <table align=center style="width:95%" class="table table-striped table-bordered table-hover table-green dataTable">
      <thead>
      <tr>
        <td valign="middle" align="center" class="th0">Sr No</td>
        <td valign="middle" align="center" class="th0">Bank ID</td>
        <td valign="middle" align="center" class="th0">Bank Name</td>
        <td valign="middle" align="center" class="th0">View Template</td>
        <td valign="middle" align="center" class="th0">Template</td>
        <td valign="middle" align="center" class="th0">Result</td>
      </tr>
      </thead>
      <%
        String style="class=td1";
        String ext="light";
        String upload="|Upload";
        int pos = 1;

        Map<String,AppFileDetailsVO> fileDetailsVOMap = (Map<String,AppFileDetailsVO>) request.getAttribute("fileDetailsVOMap");
        AppFileDetailsVO fileDetailsVO = null;
        String success = "Success";

        for(BankTypeVO bankTypeVO : bankTypeVOList)
        {
          if(pos%2==0)
          {
            style="class=tr1";
            ext="dark";
          }
          else
          {
            style="class=tr0";
            ext="light";
          }

          out.println("<tr>");
          out.println("<td "+style+" align=\"center\">&nbsp;"+srno+ "</td>");
          out.println("<td "+style+" align=\"center\">&nbsp;" +ESAPI.encoder().encodeForHTML(bankTypeVO.getBankId())+"</td>");
          out.println("<td "+style+" align=\"center\">&nbsp;" +ESAPI.encoder().encodeForHTML(bankTypeVO.getBankName())+"</td>");

          out.println("<td align=\"center\">");
          out.println("<form action=\"/icici/servlet/UploadNewTemplate?fileName="+bankTypeVO.getFileName()+"&ctoken="+ctoken+"\" method=\"post\" enctype=\"multipart/form-data\">");
          out.println("<input type=\"hidden\" name=\"fileName\" value=\"" + bankTypeVO.getFileName() + "\">");
          out.println("<input type=\"image\" src=\"/icici/images/pdflogo.jpg\" width=\"15%\">");
          out.println("</form>");
          out.println("</td>");

          out.println("<td " + style + " align=\"right\"><input type=\"file\" class=\"filestyle\" name=\"" + bankTypeVO.getBankName() + "_file|"+bankTypeVO.getFileName()+"\"></td>");
          out.println("<input type=\"hidden\" name=\"bankname\" value=\"" + bankTypeVO.getBankName()+ "\">");
          out.println("<td><div width=\"10px\" class=\"scrolladmin\">");

          if (fileDetailsVOMap != null)
          {
            for (Map.Entry<String, AppFileDetailsVO> entry : fileDetailsVOMap.entrySet())
            {
              fileDetailsVO = entry.getValue();
              if (fileDetailsVO.getFieldName().equals(bankTypeVO.getBankName()))
              {
                if (fileDetailsVO.isSuccess())
                {
                  out.println("<span class=\"txtboxconfirm\" style=\"margin-left: 20%;padding-bottom: 1%;padding-top: 1%;padding-right: 1%;padding-left: 1%;font-size:11px\">" + success + "</span>");
                }
                else
                {
                  out.println("<span class=\"txtboxconfirm\" style=\"margin-left: 20%;padding-bottom: 1%;padding-top: 1%;padding-right: 1%;padding-left: 1%;font-size:11px\">" + fileDetailsVO.getReasonOfFailure() + "</span>");
                }
              }
            }
          }
          out.println("</div>");
          out.println("</td>");
          out.println("</tr>");
          srno++;
        }
      %>
      <tr>
        <td class="th0">  </td>
        <td class="th0">  </td>
        <td class="th0">  </td>
        <td class="th0">  </td>
        <td class="th0">  </td>
        <td class="th0"><Button type="submit" align="center" value="Upload" name="upload" class="addnewmember" onclick="uploadBankTemplate('<%=ctoken%>')">Upload</Button></td>
      </tr>
    </table>
  </form>
  <table align=center valign=top><tr>
    <td align=center>
      <jsp:include page="page.jsp" flush="true">
        <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
        <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
        <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
        <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
        <jsp:param name="page" value="BankMapping"/>
        <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>
    </td>
  </tr>
  </table>
  <%
    }
    else
    {
      out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
    }
  %>
</div>
<%
  }
  else
  {
    response.sendRedirect("/icici/logout.jsp");
    return;
  }
%>
<script src='/icici/stylenew/AfterAppManager.js'></script>
</body>
<%!
  public static String nullToStr(String str)
  {
    if(str == null)
      return "";
    return str;
  }
  public static String getStatus(String str)
  {
    if(str.equals("Y"))
      return "Active";
    else if(str.equals("N"))
      return "Inactive";
    else if(str.equals("T"))
      return "Test";

    return str;
  }
%>
</html>