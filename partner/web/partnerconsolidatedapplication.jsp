<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.enums.BankApplicationStatus" %>
<%@ page import="com.manager.ApplicationManager" %>
<%@ page import="com.vo.applicationManagerVOs.*" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ResourceBundle" %>

<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 2/23/15
  Time: 12:31 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp"%>
<%
  session.setAttribute("submit","partnerconsolidatedapplication");

  String memberid="";
  LinkedHashMap memberidDetails=null;
  Functions function = new Functions();
  String partnerid =nullToStr(request.getParameter("partnerid"));
  String partner_id = session.getAttribute("merchantid").toString();
  if(function.isValueNull(partnerid)){
    memberidDetails=partner.getPartnerMembersDetail(partnerid);
  }
  else{
    memberidDetails = partner.getSuperPartnerMembersDetail(partner_id);
  }
  ResourceBundle rb1 = null;
  String language_property1 = (String)session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);

  String partnerconsolidatedapplication_Merchant_Consolidated_Application = rb1.getString("partnerconsolidatedapplication_Merchant_Consolidated_Application");
  String partnerconsolidatedapplication_PartnerId = rb1.getString("partnerconsolidatedapplication_PartnerId");
  String partnerconsolidatedapplication_Select_PartnerID = rb1.getString("partnerconsolidatedapplication_Select_PartnerID");
  String partnerconsolidatedapplication_PartnerID = rb1.getString("partnerconsolidatedapplication_PartnerID");
  String partnerconsolidatedapplication_MerchantId = rb1.getString("partnerconsolidatedapplication_MerchantId");
  String partnerconsolidatedapplication_Select_MerchantId = rb1.getString("partnerconsolidatedapplication_Select_MerchantId");
  String partnerconsolidatedapplication_Search = rb1.getString("partnerconsolidatedapplication_Search");
  String partnerconsolidatedapplication_ReportTable = rb1.getString("partnerconsolidatedapplication_ReportTable");
  String partnerconsolidatedapplication_Check = rb1.getString("partnerconsolidatedapplication_Check");
  String partnerconsolidatedapplication_GatewayName = rb1.getString("partnerconsolidatedapplication_GatewayName");
  String partnerconsolidatedapplication_View_Application = rb1.getString("partnerconsolidatedapplication_View_Application");
  String partnerconsolidatedapplication_Consolidated_Application = rb1.getString("partnerconsolidatedapplication_Consolidated_Application");
  String partnerconsolidatedapplication_Message = rb1.getString("partnerconsolidatedapplication_Message");
  String partnerconsolidatedapplication_SrNo = rb1.getString("partnerconsolidatedapplication_SrNo");
  String partnerconsolidatedapplication_DocumentName = rb1.getString("partnerconsolidatedapplication_DocumentName");
  String partnerconsolidatedapplication_FileName = rb1.getString("partnerconsolidatedapplication_FileName");
  String partnerconsolidatedapplication_Timestamp = rb1.getString("partnerconsolidatedapplication_Timestamp");
  String partnerconsolidatedapplication_view_file = rb1.getString("partnerconsolidatedapplication_view_file");
  String partnerconsolidatedapplication_Sorry = rb1.getString("partnerconsolidatedapplication_Sorry");
  String partnerconsolidatedapplication_No_records_found = rb1.getString("partnerconsolidatedapplication_No_records_found");
  String partnerconsolidatedapplication_Filter = rb1.getString("partnerconsolidatedapplication_Filter");
  String partnerconsolidatedapplication_Please = rb1.getString("partnerconsolidatedapplication_Please");

%>
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
<%
  String company= (String)session.getAttribute("partnername");
%>
<html>
<head>
  <%--<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
  <title><%=company%> Application Manager> Merchant Consolidated Application</title>

  <style type="text/css">
    .testdiv
    {
      display: none;
    }

    @media (max-width: 640px){
      .icheckbox_square-aero{border: 1px solid #749096;}
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
  </script>

  <script type="text/javascript">

    $(function ()
    {
      $('#partnerid').on('change', function (request, response)
      {
        $.ajax({
          url: "/partner/net/GetDetails",
          dataType: "json",
          data: {
            partnerid: $('#partnerid').val(),
            ctoken: $('#ctoken').val(),
            method: "AppManagConsol",
            term: request.term
          },
          success: function (data)
          {
            $('#memberid').find('option').not(':first').remove();
            $.each(data.aaData, function (i, data)
            {
              var div_data = "<option value=" + data.value + ">" + data.text + "</option>";
              console.log(div_data);
              $(div_data).appendTo('#memberid');
            });
          }
        });
        minLength: 0
      });
    });
  </script>

</head>
<%--using for admin AppManagerStatus--%>
<body>
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">

      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">

            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=partnerconsolidatedapplication_Merchant_Consolidated_Application%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <form action="/partner/net/PartnerConsolidatedApplication" method="post">
              <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
              <input type="hidden" value="<%=partner_id%>"  id="partnerid">
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
                      //out.println("<center><font class=\"textb\">" + ve.getMessage() + "</font></center>");
                      out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + ve.getMessage() + "</h5>");
                    }
                  }

                }
                java.util.TreeMap<String,String> partneriddetails =null;
                partneriddetails=partner.getPartnerDetailsForUI(String.valueOf(session.getAttribute("merchantid")));
                String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
                String Style1= "";
                String Style2= "";
                String dISABLED_Id="";
                String Config="";
                if(Roles.contains("superpartner")){
                  Style1= "style=\"display: none\"";
                  Config = "style=\"display: block\"";
                }else{
                  dISABLED_Id=String.valueOf(session.getAttribute("merchantid"));
                  Style2= "style=\"display: none\"";
                }

                StringBuffer arrName=new StringBuffer();
                String orderBy= "member_id";
                String groupBy= "member_id";
                String partnerId = (String)session.getAttribute("merchantid");
                BankApplicationMasterVO bankApplicationMasterVOParam = new BankApplicationMasterVO();
                bankApplicationMasterVOParam.setStatus(BankApplicationStatus.VERIFIED.toString());
                Map<String, List<BankApplicationMasterVO>> bankApplicationMasterVOList=applicationManager.getSuperPartnerBankApplicationMasterVOForMemberId(bankApplicationMasterVOParam, orderBy, groupBy, partnerId);

              %>
              <div class="widget-content padding">
                <div id="horizontal-form">

                  <div class="form-group col-md-4 has-feedback" <%=Style2%>>
                    <label class="col-sm-4 control-label"><%=partnerconsolidatedapplication_PartnerId%></label>
                    <div class="col-sm-8">
                      <select class="form-control" name="partnerid" id="pid">
                        <option value="" default><%=partnerconsolidatedapplication_Select_PartnerID%></option>
                        <%
                          String Selected = "";
                          for(String pid : partneriddetails.keySet())
                          {
                            if(pid.toString().equals(partnerid))
                            {
                              Selected="selected";
                            }
                            else
                            {
                              Selected="";
                            }
                        %>
                        <option value="<%=pid%>" <%=Selected%>><%=partneriddetails.get(pid)%></option>
                        <%
                          }
                        %>
                      </select>
                    </div>
                  </div>


                  <div class="form-group col-md-4 has-feedback" <%=Style1%>>
                    <label class="col-sm-4 control-label"><%=partnerconsolidatedapplication_PartnerID%></label>
                    <div class="col-sm-8">
                      <input  class="form-control" value="<%=dISABLED_Id%>" disabled>
                      <input type="hidden" class="form-control" value="<%=dISABLED_Id%>" disabled>
                    </div>
                  </div>

                  <div class="form-group col-md-4 has-feedback">

                    <label class="col-sm-4 control-label"><%=partnerconsolidatedapplication_MerchantId%></label>
                    <div class="col-sm-8">
                      <select name="memberid" class="form-control" id="memberid">
                        <option value=""><%=partnerconsolidatedapplication_Select_MerchantId%></option>

                        <%
                          for(String memberId : bankApplicationMasterVOList.keySet())
                          {

                            out.println("<option value=\"" + memberId + "\" " + (memberId.equals(request.getParameter("memberid")) ? "selected" : "") + ">" +memberId + "</option>");

                          }
                        %>
                      </select>
                    </div>
                  </div>
                <%--  <div class="form-group col-md-8">
                    <label class="col-sm-3 control-label">&nbsp;<%=partnerconsolidatedapplication_MerchantId%></label>
                    <div class="col-sm-6">
                      <input name="mid" id="member" value="<%=memberid%>" class="form-control" autocomplete="on">
                    </div>
                  </div>--%>

                  <%--<div class="form-group col-md-9 has-feedback">&nbsp;</div>--%>

                  <div class="form-group col-md-3">
                    <button type="submit" name="B1" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-search"></i>&nbsp;&nbsp;<%=partnerconsolidatedapplication_Search%></button>
                  </div>
                </div>
              </div>
            </form>

          </div>
        </div>
      </div>

      <%--<div class="reporttable">--%>
      <div class="row reporttable">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerconsolidatedapplication_ReportTable%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding">
              <%
                if(request.getAttribute("errorC")!=null)
                {
                  ValidationErrorList error = (ValidationErrorList) request.getAttribute("errorC");
                  for (ValidationException errorList : error.errors())
                  {
                    //out.println("<center><font class=\"textb\">" + errorList.getMessage() + "</font></center>");
                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errorList.getMessage() + "</h5>");

                  }
                }
              %>
              <%
                if(request.getAttribute("gatewayTypeVOList")!=null)
                {
                  if(request.getAttribute("bankApplicationMasterVOs")!=null && request.getAttribute("filedetailsVOs")!=null && !((Map<String, List<BankApplicationMasterVO>>) request.getAttribute("bankApplicationMasterVOs")).isEmpty() && !((Map<String, AppFileDetailsVO>) request.getAttribute("filedetailsVOs")).isEmpty() )
                  {
                    List<BankTypeVO> bankTypeVOList = (List<BankTypeVO>) request.getAttribute("gatewayTypeVOList");
                    //  Map<String,List<AppFileDetailsVO>> fileDetailsVOList= (Map<String, List<AppFileDetailsVO>>) request.getAttribute("filedetailsVOs");
                    Map<String,FileDetailsListVO> fileDetailsVOList= (Map<String,FileDetailsListVO>) request.getAttribute("filedetailsVOs");
                    Map<String,List<BankApplicationMasterVO>> bankApplicationMasterVOs = (Map<String, List<BankApplicationMasterVO>>) request.getAttribute("bankApplicationMasterVOs");
                    Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMap = (Map<String, ConsolidatedApplicationVO>) request.getAttribute("consolidatedApplicationVOMap");
                    Map<String,AppFileDetailsVO> fileDetailsVOMap=null;
                    if(request.getAttribute("consolidatedFileDetailsVO")!=null)
                      fileDetailsVOMap= (Map<String, AppFileDetailsVO>) request.getAttribute("consolidatedFileDetailsVO");
              %>
              <form name="form" action="/partner/net/GeneratedPartnerConsolidatedApplication?ctoken=<%=ctoken%>" method="post" id="myformname" target="_blank">
                <input type="hidden" name="memberid" value="<%=request.getParameter("memberid")%>">
                <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                  <thead>
                  <tr style="background-color: #7eccad !important;color: white;">
                    <td style="text-align: center"><%=partnerconsolidatedapplication_Check%></td>
                    <td style="text-align: center"><%=partnerconsolidatedapplication_GatewayName%></td>
                    <td style="text-align: center"><%=partnerconsolidatedapplication_View_Application%></td>
                    <%
                      if(!consolidatedApplicationVOMap.isEmpty())
                      {
                    %>
                    <td style="text-align: center"><%=partnerconsolidatedapplication_Consolidated_Application%></td>
                    <%
                      }
                    %>
                    <td style="text-align: center"><%=partnerconsolidatedapplication_Message%></td>

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
                        if(fileDetailsVOMap!=null && fileDetailsVOMap.containsKey(bankApplicationMasterVOs.get(bankTypeVO.getBankId()).get(0).getBankapplicationid())){
                          subfileDetailsVO=fileDetailsVOMap.get(bankApplicationMasterVOs.get(bankTypeVO.getBankId()).get(0).getBankapplicationid());
                        }
                  %>

                  <script type="application/javascript">
                    $(document).ready(function()
                    {

                      $("input[name='bankapplicationId']").next('.iCheck-helper').on("click", function ()
                      {
                        if ($('input:checkbox[name=bankapplicationId]:checked').val() == "<%=bankApplicationMasterVOs.get(bankTypeVO.getBankId()).get(0).getBankapplicationid()%>")
                        {
                          document.getElementById(<%=bankApplicationMasterVOs.get(bankTypeVO.getBankId()).get(0).getBankapplicationid()%>).style.display="block";
                        }
                        else
                        {
                          document.getElementById(<%=bankApplicationMasterVOs.get(bankTypeVO.getBankId()).get(0).getBankapplicationid()%>).style.display="none";
                        }
                      });

                    });
                  </script>

                  <tr>
                    <td data-label="Check" style="text-align: center"><%if((consolidatedApplicationVO==null) || "INVALIDATED".equals(consolidatedApplicationVO.getStatus())) {%><input type="checkbox" class="CheckBoxClass" id="chkStatus" onclick="SelectGateway(this,<%=bankApplicationMasterVOs.get(bankTypeVO.getBankId()).get(0).getBankapplicationid()%>)" name="bankapplicationId" value="<%=bankApplicationMasterVOs.get(bankTypeVO.getBankId()).get(0).getBankapplicationid()%>"></td><%}%>
                    <td data-label="Gateway Name" style="text-align: center"><%=bankTypeVO.getBankName()%></td>
                    <td data-label="View Application" style="text-align: center">
                      <%--<%
                        for (Map.Entry<String, List<BankApplicationMasterVO>> bankApplicationMasterVOEntry : bankApplicationMasterVOs.entrySet())
                        {
                          List<BankApplicationMasterVO> bankApplicationMasterVOlist = bankApplicationMasterVOEntry.getValue();
                          for (BankApplicationMasterVO bankApplicationMasterVO : bankApplicationMasterVOlist)
                          {
                            bankTypeVO.setFileName(bankApplicationMasterVO.getBankfilename());
                      %>--%>
                      <%
                        for (List<BankApplicationMasterVO> bankApplicationMasterVOlist : bankApplicationMasterVOs.values())
                        {
                          for (int i = 0; i < bankApplicationMasterVOlist.size(); i++)
                          {
                            BankApplicationMasterVO bankApplicationMasterVO = bankApplicationMasterVOlist.get(i);
                      %>
                      <form name="form" action="/partner/net/GeneratedPartnerConsolidatedApplication?ctoken=<%=ctoken%>" method="post">
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
                      <input type="image" src="/partner/images/pdflogo.jpg" width="10%" name="action" value="<%=bankApplicationMasterVOs.get(bankTypeVO.getBankId()).get(0).getBankapplicationid()%>|View"></td>

                    <%
                      if(consolidatedApplicationVO!=null)
                      {
                    %>
                    <td valign="middle" align="center"  class="tr0"><%=consolidatedApplicationVO.getTimestamp()%><br><br>
                      <form action="/partner/net/GeneratedPartnerConsolidatedApplication?action2=|Download&ctoken=<%=ctoken%>" method="post">
                        <input type="hidden" name="memberid" value="<%=request.getParameter("memberid")%>">
                        <input type="hidden" name="fileName" value="<%=consolidatedApplicationVO.getFilename()%>">
                        <input type="image" src="/icici/images/Zip.jpg"  name="action" value="<%=consolidatedApplicationVO.getConsolidated_id()%>|Download">
                      </form>
                      <select name="" class="txtbox" disabled="true" style="border: none;background-color:#eee9e9 ">
                        <%=applicationManager.getBankApplicationStatus(functions.isValueNull(consolidatedApplicationVO.getStatus()) ? consolidatedApplicationVO.getStatus() : "", null)%>
                      </select>
                    </td>
                    <%
                      }
                    %>
                    <td data-label="Message" style="text-align: center">
                      <div style="height:37px"><%=subfileDetailsVO!=null?(!subfileDetailsVO.isSuccess()?subfileDetailsVO.getReasonOfFailure():"Success"):""%></div>
                    </td>

                  </tr>

                  <tr>
                    <td colspan="5">
                      <div id="<%=bankApplicationMasterVOs.get(bankTypeVO.getBankId()).get(0).getBankapplicationid()%>" class="testdiv" style="padding:20px; border:5px solid #fff; width:92%;margin-left:3%; font-weight:bold;background:#ffffff;">
                        <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">

                          <tr>

                            <td width="10%" valign="middle" align="center" class="th0"><%=partnerconsolidatedapplication_SrNo%></td>
                            <td width="15%" valign="middle" align="center" class="th0"><%=partnerconsolidatedapplication_DocumentName%></td>
                            <td valign="middle" align="center" class="th0"><input type="checkbox" onClick="SelectKYC(this,'<%=bankApplicationMasterVOs.get(bankTypeVO.getBankId()).get(0).getBankapplicationid()+"_mappingId"%>');" name="<%=bankApplicationMasterVOs.get(bankTypeVO.getBankId()).get(0).getBankapplicationid()%>"></td>
                            <td width="15%" valign="middle" align="center" class="th0"><%=partnerconsolidatedapplication_FileName%></td>
                            <td width="15%" valign="middle" align="center" class="th0"><%=partnerconsolidatedapplication_Timestamp%></td>
                            <td width="25%" valign="middle" align="center" class="th0"><%=partnerconsolidatedapplication_view_file%></td>
                          </tr>
                          <tr>
                            <%
                              int srno=1;

                              for(FileDetailsListVO fileDetailsVOList1 : fileDetailsVOList.values())
                              {
                            %>
                            <td class="tr0" align="center"><%=srno%></td>
                          </tr>
                          <%
                            for(int i=0;i<fileDetailsVOList1.getFiledetailsvo().size();i++)
                            {
                              AppFileDetailsVO fileDetailsVO=fileDetailsVOList1.getFiledetailsvo().get(i);

                          %>

                          <tr>
                            <td class="tr0" align="center">&nbsp;</td>
                            <td class="tr0" align="center"><%=fileDetailsVO.getFieldName()%></td>
                            <td valign="middle" align="center" class="tr0"><input type="checkbox"  name="<%=bankApplicationMasterVOs.get(bankTypeVO.getBankId()).get(0).getBankapplicationid()%>_mappingId" value="<%=fileDetailsVO.getFieldName()%>|<%=fileDetailsVO.getMappingId()%>"></td>
                            <td class="tr0" align="center"><%=fileDetailsVO.getFilename()%></td>
                            <td class="tr0" align="center"><%=fileDetailsVO.getTimestamp()%></td>
                            <td class="tr0" align="center">
                              <form action="/partner/net/GeneratedPartnerConsolidatedApplication?action3=|ViewKYC&ctoken=<%=ctoken%>" method="post">
                                <input type="hidden" name="memberid" value="<%=fileDetailsVO.getMemberid()%>">
                                <input type="hidden" name="fileName" value="<%=fileDetailsVO.getFilename()%>">
                                <input type="image" src="<%="PDF".equalsIgnoreCase(fileDetailsVO.getFileType())?"/partner/images/pdflogo.jpg":("XLSX".equalsIgnoreCase(fileDetailsVO.getFileType())?"/partner/images/excel.png":("JPG".equalsIgnoreCase(fileDetailsVO.getFileType())?"/partner/images/JPG1.jpg":"PNG".equalsIgnoreCase(fileDetailsVO.getFileType())?"/partner/images/PNGimg.jpg":"/partner/images/pdflogo.jpg"))%>" width="25%" name="action" value="<%=fileDetailsVO.getMappingId()%>|ViewKYC"/>
                              </form>
                            </td>
                          </tr>

                          <%
                              }
                              srno++;
                            }
                          %>
                        </table>
                      </div>
                    </td>
                  </tr>
                  <%
                      }
                    }
                  %>
                </table><br>
                <div class="col-md-5"></div>
                <div class="col-md-2"><input type="submit" class="btn btn-default" style="display: -webkit-box;"  name="action" value="Generate ZIP" onclick="myOpenBankGeneratedTemplate()"></div>
                <div class="col-md-5"></div>
                <br>
                <br>
              </form>

              <%
                  }

                  else
                  {
                    out.println(Functions.NewShowConfirmation1(partnerconsolidatedapplication_Sorry,partnerconsolidatedapplication_No_records_found));
                  }
                }

                else
                {
                  out.println(Functions.NewShowConfirmation1(partnerconsolidatedapplication_Filter,partnerconsolidatedapplication_Please));
                }
              %>
              <%!
                public static String nullToStr(String str)
                {
                  if(str == null)
                    return "";
                  return str;
                }
              %>
            </div>
          </div>
        </div>
      </div>
      <script src='/partner/stylenew/AfterAppManager.js'></script>
    </div>
  </div>
</div>
</body>
</html>
<%
  }
  catch (Exception e)
  {

    logger.debug("Exception for consolidatedapplication "+e);
  }
%>