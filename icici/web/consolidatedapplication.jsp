<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.enums.BankApplicationStatus" %>
<%@ page import="com.manager.ApplicationManager" %>
<%@ page import="com.vo.applicationManagerVOs.*" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 2/23/15
  Time: 12:31 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="index.jsp"%>
<%!
  private static Logger logger=new Logger("consolidatedapplication.jsp");

%>
<%
  try{
%>

<%!
  ApplicationManager applicationManager = new ApplicationManager();
  Functions functions = new Functions();
%>
<html>
<head>
  <title>Application Manager> Merchant Consolidated Application</title>
  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script src="/icici/css/jquery-ui.min.js"></script>
  <script src="/icici/javascript/autocomplete.js"></script>

  <style type="text/css">
    .testdiv
    {
      display: none;
    }
  </style>

  <script language="javascript">
    function SelectGateway(checkbox,actionname)
    {
      flag = checkbox.checked;
      var checkboxes = document.getElementsByName("bankapplicationId");
      if(checkbox.checked)
        document.getElementById(actionname).style.display="block";
      else
        document.getElementById(actionname).style.display="none";

    }
  </script>
  <script language="javascript">
    function SelectKYC(checkbox,fromaction)
    {
      flag = checkbox.checked;
      var checkboxes = document.getElementsByName(fromaction);
      var total_boxes = checkboxes.length;

      for(i=0; i<total_boxes; i++ )
      {
        checkboxes[i].checked =flag;
      }
    }

    function submitForm()
    {
      document.getElementById("view").form.submit();
    }
  </script>
</head>
<%--using for admin AppManagerStatus--%>
<body>

<div class="row">
  <div class="col-lg-12" style="margin-top: 8%">
    <div class="panel panel-default" style="margin-top: 0%">
      <div class="panel-heading" >
        Merchant Consolidated Application

      </div>

      <form action="/icici/servlet/ConsolidatedApplication" method="post">
        <input type="hidden" value="<%=ctoken%>" id="ctoken" name="ctoken">
        <%
          if(request.getParameter("MES")!=null)
          {
            String mes=request.getParameter("MES");
            if(mes.equals("ERR"))
            {
              ValidationErrorList error= (ValidationErrorList) request.getAttribute("error");
              for(Object errorList : error.errors())
              {
                ValidationException ve = (ValidationException) errorList;
                out.println("<center><font class=\"textb\">" + ve.getMessage() + "</font></center>");
              }
            }

          }
        %>
        <%
          StringBuffer arrName=new StringBuffer();
          String orderBy= "member_id";
          String groupBy= "member_id";

          BankApplicationMasterVO bankApplicationMasterVOParam = new BankApplicationMasterVO();
          bankApplicationMasterVOParam.setStatus(BankApplicationStatus.VERIFIED.toString());
          Map<String, List<BankApplicationMasterVO>> bankApplicationMasterVOList=applicationManager.getBankApplicationMasterVOForMemberId(bankApplicationMasterVOParam, orderBy, groupBy);

        %>
        <table  align="center" width="100%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">

          <tr>
            <td>

              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">


                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Merchant Id</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
<%--
                    <input name="memberid" class="txtbox" id="mid">
--%>
                    <select name="memberid" class="txtbox">

                      <option value="">Select Merchant Id</option>
                      <%
                        /* for(Map.Entry<String,List<BankApplicationMasterVO>> bankApplicationMasterVOKeyPair : bankApplicationMasterVOList.entrySet())
                         {
                           List<BankApplicationMasterVO> bankApplicationMasterVOList1=bankApplicationMasterVOKeyPair.getValue();
                           for(BankApplicationMasterVO bankApplicationMasterVO:bankApplicationMasterVOList1)
                           {
                               out.println("<option value=\"" + bankApplicationMasterVO.getMember_id() + "\" " + (bankApplicationMasterVO.getMember_id().equals(request.getParameter("member_id")) ? "selected" : "") + ">" + bankApplicationMasterVO.getMember_id() + "</option>");
                           }
                         }*/
                        for(String memberId : bankApplicationMasterVOList.keySet())
                        {

                          out.println("<option value=\"" + memberId + "\" " + (memberId.equals(request.getParameter("memberid")) ? "selected" : "") + ">" +memberId + "</option>");

                        }

                      %>
                    </select>
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>
                  <td width="6%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" ></td>
                  <td width="3%" class="textb"><button type="submit" class="buttonform" >
                    <i class="fa fa-clock-o"></i>
                    &nbsp;&nbsp;Search
                  </button></td>
                  <td width="10%" class="textb">

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
    if(request.getAttribute("errorC")!=null)
    {
      ValidationErrorList error = (ValidationErrorList) request.getAttribute("errorC");
      for (ValidationException errorList : error.errors())
      {
        out.println("<center><font class=\"textb\">" + errorList.getMessage() + "</font></center>");
      }
    }
  %>
  <%
    if(request.getAttribute("bankTypeVOList")!=null)
    {
      if(request.getAttribute("bankApplicationMasterVOs")!=null && request.getAttribute("filedetailsVOs")!=null && !((Map<String, List<BankApplicationMasterVO>>) request.getAttribute("bankApplicationMasterVOs")).isEmpty() && !((Map<String, AppFileDetailsVO>) request.getAttribute("filedetailsVOs")).isEmpty() )
      {
        List<BankTypeVO> bankTypeVOList = (List<BankTypeVO>) request.getAttribute("bankTypeVOList");
        //  Map<String,List<AppFileDetailsVO>> fileDetailsVOList= (Map<String, List<AppFileDetailsVO>>) request.getAttribute("filedetailsVOs");
        Map<String,FileDetailsListVO> fileDetailsVOList= (Map<String,FileDetailsListVO>) request.getAttribute("filedetailsVOs");
        Map<String,List<BankApplicationMasterVO>> bankApplicationMasterVOs = (Map<String, List<BankApplicationMasterVO>>) request.getAttribute("bankApplicationMasterVOs");
        Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMap = (Map<String, ConsolidatedApplicationVO>) request.getAttribute("consolidatedApplicationVOMap");
        Map<String,AppFileDetailsVO> fileDetailsVOMap=null;
        if(request.getAttribute("consolidatedFileDetailsVO")!=null)
          fileDetailsVOMap= (Map<String, AppFileDetailsVO>) request.getAttribute("consolidatedFileDetailsVO");
        /*Map<String,FileDetailsVO> consolidatedFileDetailsVO= (Map<String, FileDetailsVO>) request.getAttribute("consolidatedFileDetailsVO");*/

        //BankApplicationMasterVO bankApplicationMasterVO=null;
        StringBuffer kycString = new StringBuffer("");

        for(FileDetailsListVO fileDetailsVO:fileDetailsVOList.values())
        {

        }
  %>
  <form name="form" action="/icici/servlet/GeneratedConsolidatedApplication?ctoken=<%=ctoken%>" method="post" id="myformname" target="_blank">
    <input type="hidden" name="memberid" value="<%=request.getParameter("memberid")%>">
    <table align=center width="45%" class="table table-striped table-bordered table-hover table-green dataTable">
      <thead>
      <tr>
        <td valign="middle" class="th0"><%--<input type="checkbox" onClick="ToggleAll(this);" id="chkStatus" name="alltrans">--%></td>
        <td valign="middle" class="th0">Gateway Name</td>
        <td valign="middle" class="th0">View Application</td>
        <%
          if(!consolidatedApplicationVOMap.isEmpty())
          {
        %>
        <td valign="middle" class="th0">Consolidated Application</td>
        <%
          }
        %>
        <td valign="middle" class="th0">Message</td>

      </tr>
      </thead>
      <%--Start update code--%>
      <%

        for(BankTypeVO bankTypeVO : bankTypeVOList)
        {

          if((bankApplicationMasterVOs!=null && bankApplicationMasterVOs.containsKey(bankTypeVO.getBankId())))
          {
            ConsolidatedApplicationVO consolidatedApplicationVO=null;
            AppFileDetailsVO subfileDetailsVO=null;
            if(consolidatedApplicationVOMap.containsKey(bankTypeVO.getBankId()))
              consolidatedApplicationVO=consolidatedApplicationVOMap.get(bankTypeVO.getBankId());
            if(fileDetailsVOMap!=null && fileDetailsVOMap.containsKey(bankApplicationMasterVOs.get(bankTypeVO.getBankId()).get(0).getBankapplicationid()))
              subfileDetailsVO=fileDetailsVOMap.get(bankApplicationMasterVOs.get(bankTypeVO.getBankId()).get(0).getBankapplicationid());
      %>

      <tr>
        <td valign="middle" align="center" class="tr0"><%if((consolidatedApplicationVO==null) || "INVALIDATED".equals(consolidatedApplicationVO.getStatus())) {%><input type="checkbox" id="chkStatus" onclick="SelectGateway(this,<%=bankApplicationMasterVOs.get(bankTypeVO.getBankId()).get(0).getBankapplicationid()%>)" name="bankapplicationId" value="<%=bankApplicationMasterVOs.get(bankTypeVO.getBankId()).get(0).getBankapplicationid()%>"></td><%}%>
        <td valign="middle" align="center" class="tr0"><%=bankTypeVO.getBankName()%></td>
        <td valign="middle" align="center" class="tr0">
          <%--<%
            for (Map.Entry<String, List<BankApplicationMasterVO>> bankApplicationMasterVOEntry : bankApplicationMasterVOs.entrySet())
            {
              List<BankApplicationMasterVO> bankApplicationMasterVOlist = bankApplicationMasterVOEntry.getValue();
              for (BankApplicationMasterVO bankApplicationMasterVO : bankApplicationMasterVOlist)
              {
          %>--%>
          <%
            for (List<BankApplicationMasterVO> bankApplicationMasterVOlist : bankApplicationMasterVOs.values())
            {
              for (int i = 0; i < bankApplicationMasterVOlist.size(); i++)
              {
                BankApplicationMasterVO bankApplicationMasterVO = bankApplicationMasterVOlist.get(i);
          %>
          <form id="view" action="/icici/servlet/GeneratedConsolidatedApplication?ctoken=<%=ctoken%>" method="post">
            <input type="hidden" name="memberid" value="<%=request.getParameter("memberid")%>">
            <input type="hidden" name="fileName" value="<%=bankApplicationMasterVO.getBankfilename()%>">
          </form>
          <%
              }
            }
          %>
          <%--<%
              }
            }
          %>--%>
          <input type="image" src="/icici/images/pdflogo.jpg" width="10%" name="action" onclick="submitForm()" value="<%=bankApplicationMasterVOs.get(bankTypeVO.getBankId()).get(0).getBankapplicationid()%>|View">
        </td>

        <%
          if(consolidatedApplicationVO!=null)
          {
        %>
        <td valign="middle" align="center"  class="tr0"><%=consolidatedApplicationVO.getTimestamp()%><br><br>
          <form action="/icici/servlet/GeneratedConsolidatedApplication?ctoken=<%=ctoken%>" method="post">
            <input type="hidden" name="fileName" value="<%=consolidatedApplicationVO.getFilename()%>">
            <input type="hidden" name="memberid" value="<%=consolidatedApplicationVO.getMemberid()%>">
            <input type="hidden" name="action" value="<%=consolidatedApplicationVO.getConsolidated_id()%>|Download">
            <input type="image" src="/icici/images/Zip.jpg">
          </form>
          <select name="" class="txtbox" disabled="true" style="border: none;background-color:#eee9e9 ">
            <%=applicationManager.getBankApplicationStatus(functions.isValueNull(consolidatedApplicationVO.getStatus()) ? consolidatedApplicationVO.getStatus() : "", null)%>
          </select>
        </td>
        <input type="hidden" name="consolidatedId" value="<%=consolidatedApplicationVO.getConsolidated_id()%>">

        <%
          }
        %>
        <td valign="middle" align="center" class="tr0">
          <div style="height:37px"><%=subfileDetailsVO!=null?(!subfileDetailsVO.isSuccess()?subfileDetailsVO.getReasonOfFailure():"Success"):""%></div>
        </td>

      </tr>

      <tr>
        <td colspan="5">
          <div id="<%=bankApplicationMasterVOs.get(bankTypeVO.getBankId()).get(0).getBankapplicationid()%>" class="testdiv" style="padding:20px; border:5px solid #fff; width:92%;margin-left:3%; font-weight:bold;background:#ffffff;">
            <table align=center style="width:80%" border="1" class="table table-striped table-bordered table-green dataTable">

              <tr>
                <td width="15%" valign="middle" align="center" class="th0">Sr.No</td>
                <td width="15%" valign="middle" align="center" class="th0">Document Name</td>
                <td valign="middle" class="th0"><input type="checkbox" onClick="SelectKYC(this,'<%=bankApplicationMasterVOs.get(bankTypeVO.getBankId()).get(0).getBankapplicationid()+"_mappingId"%>');" name="allkycdocument"></td>
                <td width="15%" valign="middle" align="center" class="th0">File Name</td>
                <td width="15%" valign="middle" align="center" class="th0">Timestamp</td>
                <td width="25%" valign="middle" align="center" class="th0">view file</td>
              </tr>
              <%
                int srno=1;

                for(FileDetailsListVO fileDetailsVOList1 : fileDetailsVOList.values())
                {
              %>

              <td class="tr0" align="center"><%=srno%></td>

              <%
                for(int i=0;i<fileDetailsVOList1.getFiledetailsvo().size();i++)
                {
                  AppFileDetailsVO fileDetailsVO=fileDetailsVOList1.getFiledetailsvo().get(i);

              %>

              <tr>

                <td>&nbsp;</td>
                <td class="tr0" align="center">
                  <%=fileDetailsVO.getFieldName()%></td>
                <td valign="middle" align="center" class="tr0"><input type="checkbox"  name="<%=bankApplicationMasterVOs.get(bankTypeVO.getBankId()).get(0).getBankapplicationid()%>_mappingId" value="<%=fileDetailsVO.getFieldName()%>|<%=fileDetailsVO.getMappingId()%>"></td>
                <td class="tr0" align="center"><%=fileDetailsVO.getFilename()%></td>
                <td class="tr0" align="center"><%=fileDetailsVO.getTimestamp()%></td>
                <td class="tr0" align="center">
                  <form action="/icici/servlet/GeneratedConsolidatedApplication?ctoken=<%=ctoken%>" method="post">
                    <input type="image" src="<%="PDF".equalsIgnoreCase(fileDetailsVO.getFileType())?"/icici/images/pdflogo.jpg":("XLSX".equalsIgnoreCase(fileDetailsVO.getFileType())?"/icici/images/excel.png":("JPG".equalsIgnoreCase(fileDetailsVO.getFileType())?"/icici/images/JPG1.jpg":"PNG".equalsIgnoreCase(fileDetailsVO.getFileType())?"/icici/images/PNGimg.jpg":"/merchant/images/pdflogo.jpg"))%>" width="25%" name="action" value="<%=fileDetailsVO.getMappingId()+"|ViewKYC|"+fileDetailsVO.getFilename()%>">
                    <input type="hidden" name="memberid" value="<%=fileDetailsVO.getMemberid()%>">
                    <input type="hidden" name="fileName" value="<%=fileDetailsVO.getFilename()%>">
                    <%--<input type="hidden" name="action" value="<%=fileDetailsVO.getMappingId()+"|ViewKYC|"+fileDetailsVO.getFilename()%>">--%>
                  </form>
                </td>
              </tr>

              <%
                  }
                  srno++;
                }
              %>
            </table>
            <%--end update code--%>
          </div>
        </td>
      </tr>

      <%
          }
        }
      %>
    </table>
    <table align=center style="width:80%" border="1" class="table table-striped table-bordered table-green dataTable">
      <tr>
        <td valign="middle" align="center" class="tr0"><input type="submit" class="buttonform"  name="action" value="Generate ZIP" onclick="myOpenBankGeneratedTemplate()"></td>
      </tr>
    </table>
  </form>

  <%
      }

      else
      {
        out.println(Functions.NewShowConfirmation("Sorry","No records found"));
      }
    }

    else
    {
      out.println(Functions.NewShowConfirmation("Filter","Please provide data for generating consolidated application"));
    }
  %>
</div>
<script src='/icici/stylenew/AfterAppManager.js'></script>
</body>
</html>
<%
  }
  catch (Exception e)
  {
    logger.debug("Exception for consolidatedapplication "+e);
  }
%>