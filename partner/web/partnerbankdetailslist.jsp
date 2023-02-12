<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.enums.ApplicationStatus" %>
<%@ page import="com.manager.ApplicationManager" %>
<%@ page import="com.vo.applicationManagerVOs.AppFileDetailsVO" %>
<%@ page import="com.vo.applicationManagerVOs.ApplicationManagerVO" %>
<%@ page import="com.vo.applicationManagerVOs.BankApplicationMasterVO" %>
<%@ page import="com.vo.applicationManagerVOs.BankTypeVO" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
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
  session.setAttribute("submit","partnerbankdetailslist");

  ResourceBundle rb1 = null;
  String language_property1 = (String)session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);

  String partnerbankdetailslist_Merchant_Application_PDF = rb1.getString("partnerbankdetailslist_Merchant_Application_PDF");
  String partnerbankdetailslist_PartnerId = rb1.getString("partnerbankdetailslist_PartnerId");
  String partnerbankdetailslist_Select_Partner_ID = rb1.getString("partnerbankdetailslist_Select_Partner_ID");
  String partnerbankdetailslist_PartnerID = rb1.getString("partnerbankdetailslist_PartnerID");
  String partnerbankdetailslist_MerchantId = rb1.getString("partnerbankdetailslist_MerchantId");
  String partnerbankdetailslist_Select_Merchant_Id = rb1.getString("partnerbankdetailslist_Select_Merchant_Id");
  String partnerbankdetailslist_Search = rb1.getString("partnerbankdetailslist_Search");
  String partnerbankdetailslist_ReportTable = rb1.getString("partnerbankdetailslist_ReportTable");
  String partnerbankdetailslist_Check = rb1.getString("partnerbankdetailslist_Check");
  String partnerbankdetailslist_Bank_Name = rb1.getString("partnerbankdetailslist_Bank_Name");
  String partnerbankdetailslist_View_Application = rb1.getString("partnerbankdetailslist_View_Application");
  String partnerbankdetailslist_Sorry = rb1.getString("partnerbankdetailslist_Sorry");
  String partnerbankdetailslist_No_records_found = rb1.getString("partnerbankdetailslist_No_records_found");
  String partnerbankdetailslist_Filter = rb1.getString("partnerbankdetailslist_Filter");
  String partnerbankdetailslist_Please = rb1.getString("partnerbankdetailslist_Please");

%>
<%!
  ApplicationManager applicationManager = new ApplicationManager();
  Functions functions = new Functions();
  PartnerFunctions partner = new PartnerFunctions();
%>
<%
  String company=(String) session.getAttribute("partnername");
%>
<html>
<head>
  <title><%=company%> Application Manager> Merchant Application PDF</title>
  <script language="javascript">
    function ToggleAll(checkbox)
    {
      flag = checkbox.checked;
      var checkboxes = document.getElementsByName("pgtypeid");
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
            method: "ApplicationManagerPDF",
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

  <style type="text/css">

    @media (max-width: 640px){
      .icheckbox_square-aero{border: 1px solid #749096;}
    }
  </style>
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
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=partnerbankdetailslist_Merchant_Application_PDF%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <form action="/partner/net/PartnerBankDetailList" method="post" name="forms" >
              <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
              <%

                //Error Disply
                java.util.TreeMap<String,String> partneriddetails =null;
                partneriddetails=partner.getPartnerDetailsForUI(String.valueOf(session.getAttribute("merchantid")));
                String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
                String Style1= "";
                String Style2= "";
                String dISABLED_Id="";
                if(Roles.contains("superpartner")){
                  Style1= "style=\"display: none\"";
                }else{
                  dISABLED_Id=String.valueOf(session.getAttribute("merchantid"));
                  Style2= "style=\"display: none\"";
                }

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
                ApplicationManagerVO applicationManagerVOParam = new ApplicationManagerVO();
                //applicationManagerVOParam.setStatus(ApplicationStatus.VERIFIED.name());
                String partnerId = (String)session.getAttribute("merchantid");

                List<ApplicationManagerVO> applicationManagerVOList = applicationManager.getSuperPartnerApplicationManagerVO(applicationManagerVOParam, partnerId);%>

              <div class="widget-content padding">
                <div id="horizontal-form">

                  <div class="form-group col-md-4 has-feedback" <%=Style2%>>

                    <label class="col-sm-4 control-label"><%=partnerbankdetailslist_PartnerId%></label>
                    <div class="col-sm-8">
                      <select  class="form-control" name="partnerid" id="partnerid">
                        <option value=""><%=partnerbankdetailslist_Select_Partner_ID%></option>
                        <%
                          String partneid = request.getParameter("pid");
                          for(String pid : partneriddetails.keySet())
                            if(functions.isValueNull(partneid) && partneid.equals(pid)){
                        %>
                        <option value="<%=pid%>" selected><%=partneriddetails.get(pid)%></option>
                        <%
                        }
                        else{
                        %>
                        <option value="<%=pid%>" ><%=partneriddetails.get(pid)%></option>
                        <%
                            }
                        %>
                      </select>
                    </div>
                  </div>

                  <div class="form-group col-md-4 has-feedback" <%=Style1%>>
                    <label class="col-sm-4 control-label"><%=partnerbankdetailslist_PartnerID%></label>
                    <div class="col-sm-8">
                      <input  class="form-control" value="<%=dISABLED_Id%>" disabled>
                      <input type="hidden" class="form-control" value="<%=dISABLED_Id%>" disabled>
                    </div>
                  </div>

                  <div class="form-group col-md-4 has-feedback">

                    <label class="col-sm-4 control-label"><%=partnerbankdetailslist_MerchantId%></label>
                    <div class="col-sm-8">
                      <select name="memberid" class="form-control" id="memberid">
                        <option value=""><%=partnerbankdetailslist_Select_Merchant_Id%></option>
                        <%
                          for(ApplicationManagerVO applicationManagerVO : applicationManagerVOList)
                          {
                            if(ApplicationStatus.MODIFIED.name().equals(applicationManagerVO.getStatus()) || ApplicationStatus.VERIFIED.name().equals(applicationManagerVO.getStatus()))
                              out.println("<option value=\"" + applicationManagerVO.getMemberId()+"\" "+((applicationManagerVO.getMemberId().equals(request.getParameter("memberid")))?"selected":"")+">"+applicationManagerVO.getMemberId()+"</option>");
                          }
                        %>
                      </select>
                    </div>
                  </div>

                  <%--<div class="form-group col-md-9 has-feedback">&nbsp;</div>--%>

                  <div class="form-group col-md-3">
                    <%--<button type="submit" name="B1" class="btnblue" style="width:100px;margin-left: 20%; margin-top: 25px;background: rgb(126, 204, 173);">
                        <i class="fa fa-save"></i>
                        Search
                    </button>--%>
                    <button type="submit" name="B1" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=partnerbankdetailslist_Search%></button>
                  </div>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>

      <%--<div class="reporttable">--%>
      <div class="row">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerbankdetailslist_ReportTable%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <%

              if(request.getAttribute("bankTypeVOList")!=null)
              {
                List<BankTypeVO> bankTypeVOList = (List<BankTypeVO>) request.getAttribute("bankTypeVOList");

                Map<String,AppFileDetailsVO> fileDetailsVOMap =null;
                Map<String,BankTypeVO> merchantBankMappingMap=null;

                if(request.getAttribute("fileDetailsVOMap")!=null)
                  fileDetailsVOMap=(Map<String, AppFileDetailsVO>) request.getAttribute("fileDetailsVOMap");

                if(request.getAttribute("merchantBankMappingMap")!=null)
                  merchantBankMappingMap= (Map<String, BankTypeVO>) request.getAttribute("merchantBankMappingMap");
                Map<String,List<BankApplicationMasterVO>> bankApplicationMasterVOs = (Map<String, List<BankApplicationMasterVO>>) request.getAttribute("bankApplicationMasterVOs");
                if(bankTypeVOList.size()>0)
                {
            %>
            <div class="widget-content padding" style="overflow-y: auto;">
              <br>
              <form name="form" action="/partner/net/CreatePartnerBankApplication?ctoken=<%=ctoken%>" method="post" id="myformname" target="_blank">
                <input type="hidden" name="memberid" value="<%=request.getParameter("memberid")%>">
                <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                  <thead>
                  <tr style="background-color: #7eccad !important;color: white;">
                    <%--<th style="text-align: center"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></th>--%>
                    <th style="text-align: center"><%=partnerbankdetailslist_Check%></th>
                    <th style="text-align: center"><%=partnerbankdetailslist_Bank_Name%></th>

                    <%
                      if(bankApplicationMasterVOs!=null)
                      {
                    %>
                    <th style="text-align: center" colspan="3"><%=partnerbankdetailslist_View_Application%></th>
                    <%
                      }
                    %>
                  </tr>
                  </thead>
                  <%

                    for(BankTypeVO bankTypeVO : bankTypeVOList)
                    {
                      if(functions.isValueNull(bankTypeVO.getFileName()) && (((merchantBankMappingMap!=null && merchantBankMappingMap.containsKey(bankTypeVO.getBankId())) || (merchantBankMappingMap!=null && !(merchantBankMappingMap.size()>0))) ||(bankApplicationMasterVOs!=null && bankApplicationMasterVOs.containsKey(bankTypeVO.getBankId()))))
                      {
                  %>
                  <tr>
                    <td data-label="Gateway Name" style="text-align: center" <%=(fileDetailsVOMap!=null && fileDetailsVOMap.containsKey(bankTypeVO.getBankId()) )?"rowspan=\"2\"":""%>>
                      <input type="checkbox" class="CheckBoxClass"  name="pgtypeid" value="<%=bankTypeVO.getBankId()%>">
                    </td>
                    <td data-label="Gateway Name" style="text-align: center" <%=(fileDetailsVOMap!=null && fileDetailsVOMap.containsKey(bankTypeVO.getBankId()) )? "rowspan=\"2\"":""%>><%=bankTypeVO.getBankName()%></td>
                    <%
                      if((bankApplicationMasterVOs!=null && bankApplicationMasterVOs.containsKey(bankTypeVO.getBankId())))
                      {
                        boolean firstElement=true;
                        List<BankApplicationMasterVO> bankApplicationMasterVOList=bankApplicationMasterVOs.get(bankTypeVO.getBankId());
                        for(BankApplicationMasterVO bankApplicationMasterVO:bankApplicationMasterVOList)
                        {
                    %>
                    <td data-label="View Application" style="text-align: center" >
                      <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                        <tr>
                          <td style="text-align: center">
                            <form action="/partner/net/CreatePartnerBankApplication?ctoken=<%=ctoken%>" method="post">
                              <input type="hidden" name="memberid" value="<%=bankApplicationMasterVO.getMember_id()%>">
                              <input type="hidden" name="fileName" value="<%=bankApplicationMasterVO.getBankfilename()%>">
                              <%--<input type="hidden" name="action" value="<%=bankApplicationMasterVO.getBankapplicationid()+"|View"%>">--%>
                              <input type="image" name="action" src="/icici/images/pdflogo.jpg" width="50%" value="<%=bankApplicationMasterVO.getBankapplicationid()+"|View"%>">
                            </form>
                          </td>
                          <td style="text-align: center;width: 70%"><%=bankApplicationMasterVO.getTimestamp()%><br><br>
                            <select name="" class="form-control" disabled="true" style="border: none;background-color:#eee9e9 " >
                              <%=applicationManager.getBankApplicationStatus(functions.isValueNull(bankApplicationMasterVO.getStatus())?bankApplicationMasterVO.getStatus():"",null)%>
                            </select>
                          </td>
                        </tr>
                      </table>
                    </td>
                    <%
                        }
                      }
                    %>
                  </tr>
                  <%
                    if(fileDetailsVOMap!=null && fileDetailsVOMap.containsKey(bankTypeVO.getBankId()))
                    {
                  %>
                  <tr>
                    <td colspan="3">
                      <span><%=fileDetailsVOMap.get(bankTypeVO.getBankId()).isSuccess()?"Success":fileDetailsVOMap.get(bankTypeVO.getBankId()).getReasonOfFailure()%></span>
                    </td>
                  </tr>
                  <%
                    }
                  %>
                  <%
                      }
                    }
                  %>

                </table><br>
                <div class="col-md-4"></div>
                <div class="col-md-2">
                  <input type="submit"  class="btn btn-default" style="display: -webkit-box;"  name="action" value="Generate PDF" onclick="myOpenBankGeneratedTemplate('<%=ctoken%>')">
                </div>

                <div class="col-md-2">
                  <input type="submit"  class="btn btn-default" style="display: -webkit-box;" name="action" value="Verified PDF"  onclick="myOpenBankGeneratedTemplate('<%=ctoken%>')">
                </div>
                <div class="col-md-4"></div>
              </form>

              <%
                  }
                  else
                  {
                    out.println(Functions.NewShowConfirmation1(partnerbankdetailslist_Sorry,partnerbankdetailslist_No_records_found));
                  }
                }
                else
                {
                  out.println(Functions.NewShowConfirmation1(partnerbankdetailslist_Filter,partnerbankdetailslist_Please));
                }
              %>
            </div>
          </div>
        </div>
        <script src='/icici/stylenew/AfterAppManager.js'></script>
      </div>
    </div>
  </div>
</div>
</body>
</html>