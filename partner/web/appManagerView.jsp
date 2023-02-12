<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.enums.ApplicationStatus" %>
<%@ page import="com.manager.ApplicationManager" %>
<%@ page import="com.manager.vo.ActionVO" %>
<%@ page import="com.utils.AppFunctionUtil" %>
<%@ page import="com.vo.applicationManagerVOs.*" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ResourceBundle" %>

<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 3/16/15
  Time: 3:16 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="ietest.jsp" %>
<%@include file="index.jsp"%>

<%!
  private Functions functions = new Functions();
  private AppFunctionUtil commonFunctionUtil= new AppFunctionUtil();
  List<ApplicationStatus> applicationStatusNotNeeded= new ArrayList<ApplicationStatus>();

  private static Logger logger=new Logger("appManagerView.jsp");

%>
<%
  ResourceBundle rb1 = null;
  String language_property1 = (String) session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String appManagerView_View_Merchant = StringUtils.isNotEmpty(rb1.getString("appManagerView_View_Merchant")) ? rb1.getString("appManagerView_View_Merchant") : "View Of Merchant Application";
  String appManagerView_Sr_No = StringUtils.isNotEmpty(rb1.getString("appManagerView_Sr_No")) ? rb1.getString("appManagerView_Sr_No") : "Sr No";
  String appManagerView_Document_Name = StringUtils.isNotEmpty(rb1.getString("appManagerView_Document_Name")) ? rb1.getString("appManagerView_Document_Name") : "Document Name";
  String appManagerView_File_Name = StringUtils.isNotEmpty(rb1.getString("appManagerView_File_Name")) ? rb1.getString("appManagerView_File_Name") : "File Name";
  String appManagerView_Timestamp = StringUtils.isNotEmpty(rb1.getString("appManagerView_Timestamp")) ? rb1.getString("appManagerView_Timestamp") : "Timestamp";
  String appManagerView_View_File = StringUtils.isNotEmpty(rb1.getString("appManagerView_View_File")) ? rb1.getString("appManagerView_View_File") : "View File";
  String appManagerView_Modified_Status = StringUtils.isNotEmpty(rb1.getString("appManagerView_Modified_Status")) ? rb1.getString("appManagerView_Modified_Status") : "Modified Status";
  String appManagerView_Status = StringUtils.isNotEmpty(rb1.getString("appManagerView_Status")) ? rb1.getString("appManagerView_Status") : "Status";
  String appManagerView_Save = StringUtils.isNotEmpty(rb1.getString("appManagerView_Save")) ? rb1.getString("appManagerView_Save") : "Save";
  ActionVO actionVO=null;
  if(session.getAttribute("actionVO")!=null)
  {
    actionVO= (ActionVO) session.getAttribute("actionVO");

  }
%>

<script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>

<style type="text/css">
  /*  #ownershipid .input-group{
      display: flex;
    }

    #ownershipid .input-group-addon{
      line-height: 1.5;
    }*/

  #forviewid{
    display: flex!important;
  }

  .input-group-addon{
    line-height: 1.5!important;
  }

  .input-group-addon {
    background-color: #eee!important;
    color: #555!important;
  }

  .input-group-addon, .input-group-btn {
    width: inherit;
  }

  #income_sources_other {
    width: 10%!important;
  }

  input[type=checkbox] {
    margin-left: -20px!important;
    width: inherit!important;
  }


  #view_css{
    padding-left: 15px!important;
  }

  #mainbtn-id{
    border: 1px solid #b2b2b2!important;
    font-weight: bold!important;
    background-color: #eee!important;
    color: #555!important;
    opacity: 1!important;
  }

</style>

<%--company profile--%>
<%  String selectedY="";
  String selectedN="";
  String disableEU="";
  String bgcolor="";
  ApplicationManagerVO applicationManagerVO=null;
  CompanyProfileVO companyProfileVO=null;
  ValidationErrorList validationErrorList=null;

  if(session.getAttribute("applicationManagerVO")!=null)
  {
    applicationManagerVO= (ApplicationManagerVO) session.getAttribute("applicationManagerVO");
  }

  if(applicationManagerVO.getCompanyProfileVO()!=null)
  {
    companyProfileVO=applicationManagerVO.getCompanyProfileVO();
  }

  if(companyProfileVO==null)
  {
    companyProfileVO=new CompanyProfileVO();
  }

  if(session.getAttribute("validationErrorList")!=null)
  {
    validationErrorList = (ValidationErrorList) session.getAttribute("validationErrorList");
  }
  else if(request.getAttribute("validationErrorList")!=null)
  {
    validationErrorList = (ValidationErrorList) request.getAttribute("validationErrorList");
  }
  else
  {
    validationErrorList= new ValidationErrorList();
  }
  if(functions.isValueNull(companyProfileVO.getCompanyRegisteredEU()) && "Y".equals(companyProfileVO.getCompanyRegisteredEU()))
  {
    selectedY="checked";
    selectedN="disabled";
  }
  else
  {
    selectedN="checked";
    disableEU="disabled";
    selectedY="disabled";
    bgcolor="background-color:#EBEBE4";
  }

%>
<%--ownership profile--%>
<%
  String disableNamePrinciple2="";
  String disableShareHolder2="";
  String disablecolor="";

  OwnershipProfileVO ownershipProfileVO=null;

  if(applicationManagerVO.getOwnershipProfileVO()!=null)
  {
    ownershipProfileVO=applicationManagerVO.getOwnershipProfileVO();
  }
  if(ownershipProfileVO==null)
  {
    ownershipProfileVO=new OwnershipProfileVO();
  }

  /*if(!functions.isValueNull(ownershipProfileVO.getNameprincipal1_owned()) || "100".equals(ownershipProfileVO.getNameprincipal1_owned()))
  {
    disableNamePrinciple2="disabled";
    disablecolor="background-color:#EBEBE4";
  }
  if(!functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getOwned()) || "100".equals(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getOwned()))
  {
    disableShareHolder2="disabled";
    disablecolor="background-color:#EBEBE4";
  }*/

%>
<%--business profile--%>
<%

  BusinessProfileVO businessProfileVO=null;

  if(applicationManagerVO.getBusinessProfileVO()!=null)
  {
    businessProfileVO=applicationManagerVO.getBusinessProfileVO();
  }
  if(businessProfileVO==null)
  {
    businessProfileVO=new BusinessProfileVO();
  }


  String disableMoto="";
  String disableInternet="";
  String disableSwipe="";
  String disabled="";
  if(Integer.parseInt(functions.isValueNull(businessProfileVO.getMethodofacceptance_moto())?businessProfileVO.getMethodofacceptance_moto():"0")+Integer.parseInt(functions.isValueNull(businessProfileVO.getMethodofacceptance_internet())?businessProfileVO.getMethodofacceptance_internet():"0")+Integer.parseInt(functions.isValueNull(businessProfileVO.getMethodofacceptance_swipe())?businessProfileVO.getMethodofacceptance_swipe():"0")>=100)
  {
    if(!functions.isValueNull(businessProfileVO.getMethodofacceptance_moto()))
      disableMoto="disabled";
    if(!functions.isValueNull(businessProfileVO.getMethodofacceptance_internet()))
      disableInternet="disabled";
    if(!functions.isValueNull(businessProfileVO.getMethodofacceptance_swipe()))
      disableSwipe="disabled";
  }
  if("Y".equals(businessProfileVO.getCardtypesaccepted_americanexpress()) || "Y".equals(businessProfileVO.getCardtypesaccepted_diners()) || "Y".equals(businessProfileVO.getCardtypesaccepted_discover()) || "Y".equals(businessProfileVO.getCardtypesaccepted_mastercard()) || "Y".equals(businessProfileVO.getCardtypesaccepted_jcb()) || "Y".equals(businessProfileVO.getCardtypesaccepted_visa()))
  {
    businessProfileVO.setCardtypesaccepted_other("N");
  }
  else
  {
    businessProfileVO.setCardtypesaccepted_other("Y");
  }
%>
<%--bank Profile--%>
<%
  BankProfileVO bankProfileVO=null;

  if(applicationManagerVO.getBankProfileVO()!=null)
  {
    bankProfileVO=applicationManagerVO.getBankProfileVO();
  }
  if(bankProfileVO==null)
  {
    bankProfileVO=new BankProfileVO();
  }
%>



<%--cardholder profile--%>
<%
  CardholderProfileVO cardholderProfileVO=null;

  if(applicationManagerVO.getCardholderProfileVO()!=null)
  {
    cardholderProfileVO=applicationManagerVO.getCardholderProfileVO();
  }
  if(cardholderProfileVO==null)
  {
    cardholderProfileVO=new CardholderProfileVO();
  }

  String checkedN="";
  String checkedY="";

  String electroDisabled="";

  if(!functions.isValueNull(cardholderProfileVO.getCompliance_swapp()) || "N".equals(cardholderProfileVO.getCompliance_swapp()))
  {
    disabled="disabled";
    checkedN="checked";
  }
  else
  {
    checkedY="checked";
  }
  if(!functions.isValueNull(cardholderProfileVO.getCompliance_electronically()) || "N".equals(cardholderProfileVO.getCompliance_electronically()))
  {
    electroDisabled="disabled";
  }
%>
<%--Extra Details Profile--%>
<%

  ExtraDetailsProfileVO extraDetailsProfileVO=null;

  if(applicationManagerVO.getExtradetailsprofileVO()!=null)
  {
    extraDetailsProfileVO=applicationManagerVO.getExtradetailsprofileVO();
  }

  if(extraDetailsProfileVO==null)
  {
    extraDetailsProfileVO=new ExtraDetailsProfileVO();

  }
%>

<%

  Map<String,FileDetailsListVO> fileDetailsVOHashMap=null;
  List<AppUploadLabelVO> uploadLabelVOList=null;


  if(applicationManagerVO.getFileDetailsVOs()!=null)
  {
    fileDetailsVOHashMap=  applicationManagerVO.getFileDetailsVOs();
  }
  else
  {
    fileDetailsVOHashMap=new HashMap<String, FileDetailsListVO>();
  }


%>
<html>
<head>
  <title></title>

  <style type="text/css">
    #companycontent{
      margin-top: 0;
    }

    #companyid_container{
      padding-right: 0!important;
      padding-left: 0!important;
    }

    #ownershipid, #businessid, #bankappid, #cardid, #extraid, #uploadid, #viewmerchantid, #statusid{
      /*margin-left: auto!important;*/
    }

    @supports (-ms-ime-align:auto) {
      span.multiselect-native-select{
        position: static!important;
      }
    }

    #ownershipid > .content, #businessid > .content, #bankappid > .content, #cardid > .content, #extraid > .content, #uploadid > .content, #viewmerchantid > .content, #statusid > .content{
      margin-top: 0!important;
    }

    @media (max-width: 480px){
      /*      #wrapper .content-page {
              padding-left: 50px;
            }*/

      #ownershipid, #businessid, #bankappid, #cardid, #extraid, #uploadid, #viewmerchantid, #statusid{
        padding-left: 0!important;
      }
    }
  </style>

</head>
<body>

<%
  if(request.getParameter("MES")!=null)
  {
    String mes=request.getParameter("MES");
    if(mes.equals("ERR"))
    {
      ValidationErrorList error= (ValidationErrorList) request.getAttribute("error");
      for(ValidationException errorList : error.errors())
      {
        out.println("<center><font class=\"textb\">" + errorList.getMessage() + "</font></center>");
      }
    }

  }
  ApplicationManager applicationManager = new ApplicationManager();
  List<ApplicationManagerVO> applicationManagerVOList=applicationManager.getApplicationManagerVO(null);
%>
<%=actionVO.getActionCriteria()+"_____--_____--"%>
<%
  if(actionVO.getActionCriteria().contains("View") || actionVO.getActionCriteria().contains("Edit"))
  {   //edit or view jsp include
%>
<jsp:include page="appManagerAction.jsp"></jsp:include>
<%
}
else
{
  //for create application manager jsp include
%>
<jsp:include page="createmerchantapplication.jsp"></jsp:include>
<%

  }
%>


<%--<jsp:include page="appManagerAction.jsp">
  <jsp:param name="view" value="true" />
</jsp:include>--%>

<jsp:include page="companyprofile.jsp">
  <jsp:param name="view" value="true" />
</jsp:include>


<jsp:include page="ownershipprofile.jsp">
  <jsp:param name="view" value="true" />
</jsp:include>

<jsp:include page="appbusinessprofile.jsp">
  <jsp:param name="view" value="true" />
</jsp:include>


<jsp:include page="bankapplication.jsp">
  <jsp:param name="view" value="true" />
</jsp:include>


<jsp:include page="cardholderprofile.jsp">
  <jsp:param name="view" value="true" />
</jsp:include>


<jsp:include page="extradetailsprofile.jsp">
  <jsp:param name="view" value="true" />
</jsp:include>


<div class="content-page" id="viewmerchantid">
<div class="content" style="margin-bottom: 0;">
  <!-- Page Heading Start -->
  <div class="page-heading" style="margin-bottom: 0;">

    <div class="row">



      <div class="col-sm-12 portlets ui-sortable">

        <div class="widget" style="margin-bottom: 0;">

          <div class="widget-header transparent">
            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=appManagerView_View_Merchant%></strong></h2>
            <div class="additional-btn">
              <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
              <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
              <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
            </div>
          </div>

          <div class="widget-content padding">
            <div >
              <form name="form" action="/partner/net/ViewKycDocument?ctoken=<%=ctoken%>" method="post" id="myformname" target="_blank">
                <table align=center style="width:100%" border="1" class="table table-striped table-bordered table-green dataTable">
                  <thead>
                  <tr>

                    <td valign="middle" align="center" class="th0" style="background-color: #7eccad !important;color: white;vertical-align: middle;text-align: center;font-family: Open Sans;font-size: 13px;font-weight: 600;"><%=appManagerView_Sr_No%></td>
                    <td valign="middle" align="center" class="th0" style="background-color: #7eccad !important;color: white;vertical-align: middle;text-align: center;font-family: Open Sans;font-size: 13px;font-weight: 600;"><%=appManagerView_Document_Name%></td>
                    <td valign="middle" align="center" class="th0" style="background-color: #7eccad !important;color: white;vertical-align: middle;text-align: center;font-family: Open Sans;font-size: 13px;font-weight: 600;"><%=appManagerView_File_Name%></td>
                    <td valign="middle" align="center" class="th0" style="background-color: #7eccad !important;color: white;vertical-align: middle;text-align: center;font-family: Open Sans;font-size: 13px;font-weight: 600;"><%=appManagerView_Timestamp%></td>
                    <td valign="middle" align="center" class="th0" style="background-color: #7eccad !important;color: white;vertical-align: middle;text-align: center;font-family: Open Sans;font-size: 13px;font-weight: 600;"><%=appManagerView_View_File%></td>
                  </tr>
                  </thead>
                  <tbody>
                  <tr>
                    <%
                      int srno=1;
                            /*String fieldName = "";*/
                      for(FileDetailsListVO fileDetailsVOList : fileDetailsVOHashMap.values())
                      {

                    %>
                    <td class="tr0" data-label="Sr No" align="center" style="font-family: Open Sans;font-size: 13px;font-weight: 600;"><%=srno%></td>
                  </tr><tr>
                    <%
                      for(AppFileDetailsVO fileDetailsVO : fileDetailsVOList.getFiledetailsvo())
                      {
                            /*String name = fileDetailsVO.getFieldName();
                            if(fieldName.equals(fileDetailsVO.getFieldName())) {fileDetailsVO.setFieldName(" ");}*/
                    %>

                    <td>&nbsp;</td>
                    <td class="tr0" data-label="Document Name" align="center" style="font-family: Open Sans;font-size: 13px;font-weight: 600;"> <%=fileDetailsVO.getFieldName()%> </td>

                    <td class="tr0" data-label="File Name" align="center" style="font-family: Open Sans;font-size: 13px;font-weight: 600;"><%=fileDetailsVO.getFilename()%></td>
                    <%--<td class="tr0" data-label="File Creation Date" align="center" style="font-family: Open Sans;font-size: 13px;font-weight: 600;"><%=Functions.convertStringtoDate(fileDetailsVO.getDtstamp())%></td>--%>
                    <td class="tr0" data-label="Timestamp" align="center" style="font-family: Open Sans;font-size: 13px;font-weight: 600;"><%=fileDetailsVO.getTimestamp()%></td>
                    <td class="tr0" data-label="View File" align="center" style="font-family: Open Sans;font-size: 13px;font-weight: 600;">
                      <form name="form" action="/partner/net/ViewKycDocument?ctoken=<%=ctoken%>" method="post">
                        <input type="hidden" name="memberid" value="<%=applicationManagerVO.getMemberId()%>">
                        <input type="hidden" name="action" value="<%=fileDetailsVO.getFieldName()+"|View|"+fileDetailsVO.getMappingId()+"|"+fileDetailsVO.getFilename()%>">
                        <input type="image" src="<%="PDF".equalsIgnoreCase(fileDetailsVO.getFileType())?"/partner/images/pdflogo.jpg":("XLSX".equalsIgnoreCase(fileDetailsVO.getFileType())?"/partner/images/excel.png":("JPG".equalsIgnoreCase(fileDetailsVO.getFileType())?"/partner/images/JPG1.jpg":"PNG".equalsIgnoreCase(fileDetailsVO.getFileType())?"/partner/images/PNGimg.jpg":"/partner/images/pdflogo.jpg"))%>" width="15%" value="<%=fileDetailsVO.getFieldName()+"|View|"+fileDetailsVO.getMappingId()+"|"+fileDetailsVO.getFilename()%>">
                      </form>
                  </tr>
                  </tbody>
                  <%
                        /*fieldName = name;*/
                      }

                      srno++;
                    }
                  %>
                </table>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</div>

<div class="content-page" id="statusid">
<div class="content" style="margin-bottom: 0;">
  <!-- Page Heading Start -->
  <div class="page-heading" style="margin-bottom: 0;">
    <div class="row">



      <div class="col-sm-12 portlets ui-sortable">

        <div class="widget" style="margin-bottom: 0;">

          <div class="widget-header transparent">
            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=appManagerView_Modified_Status%></strong></h2>
            <div class="additional-btn">
              <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
              <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
              <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
            </div>
          </div>

          <div class="widget-content padding">
            <div id="horizontal-form">


              <form action="/partner/net/AppManagerStatus?ctoken=<%=ctoken%>" method="post">
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <input type="hidden" value="<%=applicationManagerVO.getMemberId()%>" name="apptoid">
                <%
                  //applicationStatusNotNeeded.add(ApplicationStatus.SAVED);
                  //applicationStatusNotNeeded.add(ApplicationStatus.SUBMIT);
                  applicationStatusNotNeeded.add(ApplicationStatus.STEP1_SUBMIT);
                  applicationStatusNotNeeded.add(ApplicationStatus.STEP1_SAVED);
                        /*applicationStatusNotNeeded.add(ApplicationStatus.MODIFIED);
                        applicationStatusNotNeeded.add(ApplicationStatus.VERIFIED);*/

                %>
                <div class="form-group col-md-12 has-feedback">
                  <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=appManagerView_Status%></label>
                  <select  name="status"  class="form-control" style="width: 50%;">
                    <%=applicationManager.getApplicationStatus(functions.isValueNull(applicationManagerVO.getStatus()) == true ? applicationManagerVO.getStatus() : "",applicationStatusNotNeeded)%>

                  </select>
                </div>
                <div class="form-group col-md-4 has-feedback">
                  <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"></label>
                  <button type="submit" class="buttonform btn btn-default" >
                    <i class="fa fa-clock-o"></i>
                    &nbsp;&nbsp;<%=appManagerView_Save%>
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