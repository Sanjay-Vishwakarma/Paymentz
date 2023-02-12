<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.enums.ApplicationManagerTypes" %>
<%@ page import="com.enums.ApplicationStatus" %>
<%@ page import="com.manager.ApplicationManager" %>
<%@ page import="com.manager.utils.CommonFunctionUtil" %>
<%@ page import="com.manager.vo.ActionVO" %>
<%@ page import="com.manager.vo.fileRelatedVOs.UploadLabelVO" %>
<%@ page import="com.vo.applicationManagerVOs.*" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

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
    private CommonFunctionUtil commonFunctionUtil= new CommonFunctionUtil();
    List<ApplicationStatus> applicationStatusNotNeeded= new ArrayList<ApplicationStatus>();

    private static Logger logger=new Logger("appManagerView.jsp");

%>
<%
    ActionVO actionVO=null;
    if(session.getAttribute("actionVO")!=null)
    {
        actionVO= (ActionVO) session.getAttribute("actionVO");
    }
%>

<style type="text/css">
    @-moz-document url-prefix() {

        select[multiple], select[size]{
            height: 29px!important;
        }

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
    }*/
    if(!functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getOwned()) || "100".equals(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getOwned()))
    {
        disableShareHolder2="disabled";
        disablecolor="background-color:#EBEBE4";
    }

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
        fileDetailsVOHashMap=applicationManagerVO.getFileDetailsVOs();
    }
    else
    {
        fileDetailsVOHashMap=new HashMap<String, FileDetailsListVO>();
    }


%>
<html>
<head>
    <title></title>
</head>
<body>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading" >
                View of Merchant Application

            </div>
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
            %>
            <form action="/icici/servlet/ListofAppMember?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken">

                <%!
                    ApplicationManager applicationManager = new ApplicationManager();
                %>
                <%List<ApplicationManagerVO> applicationManagerVOList=applicationManager.getApplicationManagerVO(null);%>
                <table  align="center" width="100%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">

                    <tr>
                        <td>

                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Member Id</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <select name="apptoid" class="txtbox">
                                            <option value="">Select Application</option>
                                            <%
                                                for(ApplicationManagerVO applicationManagerVO1 : applicationManagerVOList)
                                                {
                                                    out.println("<option value=\""+applicationManagerVO1.getMemberId()+"\" "+((applicationManagerVO1.getMemberId().equals(request.getParameter("apptoid")))?"selected":((actionVO!=null && functions.isValueNull(actionVO.getActionCriteria()) && applicationManagerVO1.getMemberId().equals(actionVO.getActionCriteria().split("_")[0]))?"selected":""))+">"+applicationManagerVO1.getMemberId()+"</option>");
                                                }
                                            %>
                                        </select>

                                    </td>

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

<jsp:include page="companyprofile.jsp">
    <jsp:param name="view" value="true" />
</jsp:include>


<jsp:include page="ownershipprofile.jsp">
    <jsp:param name="view" value="true" />
</jsp:include>




<jsp:include page="businessprofile.jsp">
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

<%--Disply Upload Details--%>
<div class="container-fluid ">
    <div class="row" style="margin-top: 100px;margin-left: 226px;margin-bottom: 12px;background-color: #ffffff">
        <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%">

            <div class="form foreground bodypanelfont_color panelbody_color">
                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #9fabb7">DOCUMENT UPLOAD DETAILS</h2>
                <br>
                <form name="form" action="/icici/servlet/ViewKycDocument?ctoken=<%=ctoken%>" method="post" id="myformname" target="_blank">
                    <table align=center style="width:80%" border="1" class="table table-striped table-bordered table-green dataTable">
                        <tr>

                            <td width="15%" valign="middle" align="center" class="th0">Sr.No</td>
                            <td width="15%" valign="middle" align="center" class="th0">Label Name</td>
                            <td width="15%" valign="middle" align="center" class="th0">document_name</td>
                            <td width="15%" valign="middle" align="center" class="th0">Timestamp</td>
                            <td width="25%" valign="middle" align="center" class="th0">view file</td>
                        </tr>
                        <tr>
                            <%
                                int srno=1;

                                for(FileDetailsListVO fileDetailsVOlist : fileDetailsVOHashMap.values())
                                {
                            %>
                            <td class="tr0" data-label="Sr No" align="center"><%=srno%></td>
                        </tr>
                        <%
                            for(AppFileDetailsVO fileDetailsVO : fileDetailsVOlist.getFiledetailsvo())
                            {

                        %>
                        <tr>

                            <td>&nbsp;</td>
                            <td class="tr0" align="center"><%=fileDetailsVO.getFieldName()%></td>
                            <td class="tr0" align="center"><%=fileDetailsVO.getFilename()%></td>
                            <td class="tr0" align="center"><%=fileDetailsVO.getTimestamp()%></td>
                            <td class="tr0" align="center">
                                <form id="pdfForm" action="/icici/servlet/ViewKycDocument?ctoken=<%=ctoken%>" method="post">
                                    <input type="hidden" name="memberid" value="<%=fileDetailsVO.getMemberid()%>">
                                    <input type="hidden" name="action" value="<%=fileDetailsVO.getFieldName()+"|View|"+fileDetailsVO.getMappingId()+"|"+fileDetailsVO.getFilename()%>">
                                    <input type="image" src="<%="PDF".equalsIgnoreCase(fileDetailsVO.getFileType())?"/icici/images/pdflogo.jpg":("XLSX".equalsIgnoreCase(fileDetailsVO.getFileType())?"/icici/images/excel.png":("JPG".equalsIgnoreCase(fileDetailsVO.getFileType())?"/icici/images/JPG1.jpg":"PNG".equalsIgnoreCase(fileDetailsVO.getFileType())?"/icici/images/PNGimg.jpg":"/merchant/images/pdflogo.jpg"))%>" width="25%" value="<%=fileDetailsVO.getFieldName()+"|View|"+fileDetailsVO.getMappingId()+"|"+fileDetailsVO.getFilename()%>">
                                </form>
                            </td>
                        </tr>
                        <%

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

<%--Disply Upload Details--%>

<div class="container-fluid ">
    <div class="row" style="margin-top: 100px;margin-left: 226px;margin-bottom: 12px;background-color: #ffffff">
        <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%">

            <div class="form foreground bodypanelfont_color panelbody_color">

                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #9fabb7">Modified Status</h2>

                <form action="/icici/servlet/AppManagerStatus?ctoken=<%=ctoken%>" method="post">
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
                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Status</label>
                        <select  name="status"  class="form-control" style="width: 50%;">
                            <%=applicationManager.getApplicationStatus(functions.isValueNull(applicationManagerVO.getStatus()) == true ? applicationManagerVO.getStatus() : "",applicationStatusNotNeeded)%>

                        </select>
                    </div>
                    <div class="form-group col-md-4 has-feedback">
                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"></label>
                        <button type="submit" class="buttonform" >
                            <i class="fa fa-clock-o"></i>
                            &nbsp;&nbsp;Save
                        </button>
                    </div>
                </form>

            </div>
        </div>
    </div>
</div>
</body>
</html>