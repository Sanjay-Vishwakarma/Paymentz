<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.manager.ProfileManagementManager" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.dao.MerchantDAO" %>
<%@ page import="com.manager.enums.PZTransactionCurrency" %>
<%@ page import="com.manager.utils.CommonFunctionUtil" %>
<%@ page import="com.manager.vo.ActionVO" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.manager.vo.riskRuleVOs.ProfileVO" %>
<%@ page import="com.manager.vo.userProfileVOs.MemberDetails" %>
<%@ page import="com.manager.vo.userProfileVOs.MerchantVO" %>
<%@ page import="com.manager.vo.userProfileVOs.TemplateVO" %>
<%@ page import="com.manager.vo.userProfileVOs.UserSetting" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ResourceBundle" %>
<%--
  Created by IntelliJ IDEA.
  User: Pradeep
  Date: 25/08/2015
  Time: 21:23
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp"%>
<html>
<head>
    <title></title>
    <script src="/merchant/javascript/hidde.js"></script>

    <style type="text/css">
        .textb {
            color: red;
            font-weight: bold;
        }

        @media (min-width: 768px){
            .form-horizontal .control-label {
                text-align: left!important;
            }
        }
        .field-icon
        {
            float:  right;
            margin-top: -25px;
            position: relative;
            z-index: 2;
        }

    </style>


</head>
<body class="pace-done widescreen fixed-left-void" onload="bodyonload()">



<%
    session.setAttribute("submit","userProfile");
%>
<%! private Functions functions= new Functions();
    private CommonFunctionUtil commonFunctionUtil= new CommonFunctionUtil();
    private Map<String,String> yesNoDropdown= new HashMap<String,String>();
    private Map<String,String> testModeDropDown= new HashMap<String,String>();
    List<PZTransactionCurrency> pzTransactionCurrenciesNotNeeded =new ArrayList<PZTransactionCurrency>();%>
<script>

    //User setting
    var memberId=[];
    var terminalForMemberArray=[];
    var terminalToMemberArray=[];
    var key=[];
    //var currency=[];
    var companyName=[];
    var contactEmail=[];
    var addressVerification=[];
    var autoRedirect=[];

</script>
<%
    yesNoDropdown.put("Y","YES");
    yesNoDropdown.put("N","NO");
    testModeDropDown.put("iridium","IRIDIUM");
    testModeDropDown.put("offline","OFFLINE");

    pzTransactionCurrenciesNotNeeded.add(PZTransactionCurrency.INR);
    pzTransactionCurrenciesNotNeeded.add(PZTransactionCurrency.PEN);

    String hostURL= (String) session.getAttribute("hostURL");
    List<String> urlList= new ArrayList<String>();

    urlList.add(hostURL);
    urlList.add("secure"+hostURL.substring(hostURL.indexOf(".")));
%>
<%
    ProfileManagementManager profileManagementManager = new ProfileManagementManager();
    TerminalManager terminalManager = new TerminalManager();

    int count=1;
    StringBuffer memberId=new StringBuffer();
    StringBuffer key=new StringBuffer();
    //StringBuffer currency=new StringBuffer();
    StringBuffer companyName=new StringBuffer();
    StringBuffer contactEmail=new StringBuffer();
    StringBuffer addressVerification=new StringBuffer();
    StringBuffer autoRedirect=new StringBuffer();

    StringBuffer terminalOptionTag=new StringBuffer();
    StringBuffer terminalForMemberArray=new StringBuffer();
    StringBuffer terminalToMemberArray=new StringBuffer();

    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String addOrUpdateUserProfile_User_Profile = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_User_Profile")) ? rb1.getString("addOrUpdateUserProfile_User_Profile") : "User Profile";
    String addOrUpdateUserProfile_PartnerID = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_PartnerID")) ? rb1.getString("addOrUpdateUserProfile_PartnerID") : "Partner ID";
    String addOrUpdateUserProfile_Search = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Search")) ? rb1.getString("addOrUpdateUserProfile_Search") : "Search";
    String addOrUpdateUserProfile_View_User_Profile = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_View_User_Profile")) ? rb1.getString("addOrUpdateUserProfile_View_User_Profile") : "View User Profile";
    String addOrUpdateUserProfile_Edit_User_Profile = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Edit_User_Profile")) ? rb1.getString("addOrUpdateUserProfile_Edit_User_Profile") : "Edit User Profile";
    String addOrUpdateUserProfile_Add_New_User_Profile = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Add_New_User_Profile")) ? rb1.getString("addOrUpdateUserProfile_Add_New_User_Profile") : "Add New User Profile";
    String addOrUpdateUserProfile_Partner_ID = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Partner_ID")) ? rb1.getString("addOrUpdateUserProfile_Partner_ID") : "Partner ID* :";
    String addOrUpdateUserProfile_Online_Processing_URL = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Online_Processing_URL")) ? rb1.getString("addOrUpdateUserProfile_Online_Processing_URL") : "Online Processing URL* :";
    String addOrUpdateUserProfile_select = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_select")) ? rb1.getString("addOrUpdateUserProfile_select") : "(Select Processing URL for Online transaction)";
    String addOrUpdateUserProfile_https = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_https")) ? rb1.getString("addOrUpdateUserProfile_https") : "https://";
    String addOrUpdateUserProfile_Offline_Processing_URL = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Offline_Processing_URL")) ? rb1.getString("addOrUpdateUserProfile_Offline_Processing_URL") : "Offline Processing URL* :";
    String addOrUpdateUserProfile_select_offline = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_select_offline")) ? rb1.getString("addOrUpdateUserProfile_select_offline") : "(Select Processing URL for Offline transaction)";
    String addOrUpdateUserProfile_Key = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Key")) ? rb1.getString("addOrUpdateUserProfile_Key") : "Key* :";
    String addOrUpdateUserProfile_secret_key = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_secret_key")) ? rb1.getString("addOrUpdateUserProfile_secret_key") : "(Secret Key)";
    String addOrUpdateUserProfile_Company_Name = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Company_Name")) ? rb1.getString("addOrUpdateUserProfile_Company_Name") : "Company Name:";
    String addOrUpdateUserProfile_partner_company = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_partner_company")) ? rb1.getString("addOrUpdateUserProfile_partner_company") : "(Partner Company Name)";
    String addOrUpdateUserProfile_Contact_Email = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Contact_Email")) ? rb1.getString("addOrUpdateUserProfile_Contact_Email") : "Contact Email :";
    String addOrUpdateUserProfile_partner_email = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_partner_email")) ? rb1.getString("addOrUpdateUserProfile_partner_email") : "(Partner Contact Email)";
    String addOrUpdateUserProfile_Currency = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Currency")) ? rb1.getString("addOrUpdateUserProfile_Currency") : "Currency* :";
    String addOrUpdateUserProfile_Autoredirect = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Autoredirect")) ? rb1.getString("addOrUpdateUserProfile_Autoredirect") : "Autoredirect* :";
    String addOrUpdateUserProfile_autoredirect_configuration = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_autoredirect_configuration")) ? rb1.getString("addOrUpdateUserProfile_autoredirect_configuration") : "(AutoRedirect Configuration Of Merchant)";
    String addOrUpdateUserProfile_Online_Risk_Threshold = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Online_Risk_Threshold")) ? rb1.getString("addOrUpdateUserProfile_Online_Risk_Threshold") : "Online Risk Threshold* :";
    String addOrUpdateUserProfile_numbers = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_numbers")) ? rb1.getString("addOrUpdateUserProfile_numbers") : "(Enter Online Risk Threshold in Numbers eg 100)";
    String addOrUpdateUserProfile_Offline_Risk_Threshold = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Offline_Risk_Threshold")) ? rb1.getString("addOrUpdateUserProfile_Offline_Risk_Threshold") : "Offline Risk Threshold* :";
    String addOrUpdateUserProfile_offline_number = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_offline_number")) ? rb1.getString("addOrUpdateUserProfile_offline_number") : "(Enter Offline Risk Threshold in Numbers eg 100)";
    String addOrUpdateUserProfile_Address_Verification = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Address_Verification")) ? rb1.getString("addOrUpdateUserProfile_Address_Verification") : "Address Verification* :";
    String addOrUpdateUserProfile_Merchant_configuration = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Merchant_configuration")) ? rb1.getString("addOrUpdateUserProfile_Merchant_configuration") : "(Merchant Configuration for Address Validation)";
    String addOrUpdateUserProfile_Address_Details_Display = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Address_Details_Display")) ? rb1.getString("addOrUpdateUserProfile_Address_Details_Display") : "Address Details Display* :";
    String addOrUpdateUserProfile_Address_Configuration = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Address_Configuration")) ? rb1.getString("addOrUpdateUserProfile_Address_Configuration") : "(Merchant Configuration for Address Details Display)";
    String addOrUpdateUserProfile_Default_Mode = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Default_Mode")) ? rb1.getString("addOrUpdateUserProfile_Default_Mode") : "Default Mode* :";
    String addOrUpdateUserProfile_select_default = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_select_default")) ? rb1.getString("addOrUpdateUserProfile_select_default") : "(Select Default Mode for Transaction)";
    String addOrUpdateUserProfile_Risk_Profile = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Risk_Profile")) ? rb1.getString("addOrUpdateUserProfile_Risk_Profile") : "Risk Profile* :";
    String addOrUpdateUserProfile_Select_Risk = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Select_Risk")) ? rb1.getString("addOrUpdateUserProfile_Select_Risk") : "(Select Risk Profile)";
    String addOrUpdateUserProfile_Business_Profile = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Business_Profile")) ? rb1.getString("addOrUpdateUserProfile_Business_Profile") : "Business Profile* :";
    String addOrUpdateUserProfile_Select_Business_Profile = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Select_Business_Profile")) ? rb1.getString("addOrUpdateUserProfile_Select_Business_Profile") : "(Select Business Profile)";
    String addOrUpdateUserProfile_Template_Setting = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Template_Setting")) ? rb1.getString("addOrUpdateUserProfile_Template_Setting") : "Template Setting";
    String addOrUpdateUserProfile_Background_Color = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Background_Color")) ? rb1.getString("addOrUpdateUserProfile_Background_Color") : "Background Color :";
    String addOrUpdateUserProfile_Enter_color = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Enter_color")) ? rb1.getString("addOrUpdateUserProfile_Enter_color") : "(Enter Color eg White)";
    String addOrUpdateUserProfile_Foreground_Color = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Foreground_Color")) ? rb1.getString("addOrUpdateUserProfile_Foreground_Color") : "Foreground Color :";
    String addOrUpdateUserProfile_Enter_Black = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Enter_Black")) ? rb1.getString("addOrUpdateUserProfile_Enter_Black") : "(Enter Color eg Black)";
    String addOrUpdateUserProfile_Font_Color = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Font_Color")) ? rb1.getString("addOrUpdateUserProfile_Font_Color") : "Font Color :";
    String addOrUpdateUserProfile_Logo = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Logo")) ? rb1.getString("addOrUpdateUserProfile_Logo") : "Logo :";
    String addOrUpdateUserProfile_Enter_Logo_Name = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Enter_Logo_Name")) ? rb1.getString("addOrUpdateUserProfile_Enter_Logo_Name") : "(Enter Logo Name with extension eg logo.png)";
    String addOrUpdateUserProfile_Add = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Add")) ? rb1.getString("addOrUpdateUserProfile_Add") : "Add";
    String addOrUpdateUserProfile_Update = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Update")) ? rb1.getString("addOrUpdateUserProfile_Update") : "Update";
    String addOrUpdateUserProfile_Cancel = StringUtils.isNotEmpty(rb1.getString("addOrUpdateUserProfile_Cancel")) ? rb1.getString("addOrUpdateUserProfile_Cancel") : "Cancel";

%>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">


            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=addOrUpdateUserProfile_User_Profile%></strong></h2>
                            <div class="additional-btn">
                                <%--<form action="/partner/net/SingleUserProfile?ctoken=<%=ctoken%>" method="POST" name="myForm">
                                    <button type="submit" class="btn btn-default" name="action" value="1_Add" onclick="cancel('<%=ctoken%>')" style="width: 250px; font-size:14px">
                                        <i class="fa fa-download"></i>
                                        &nbsp;&nbsp;Download UserProfile XMl
                                    </button>
                                    <button type="submit" class="btn btn-default" name="action" value="1_Add" onclick="add('<%=ctoken%>')" style="width: 250px; font-size:14px">
                                        <i class="fa fa-sign-in"></i>
                                        &nbsp;&nbsp;Add New User Profile
                                    </button>
                                </form>--%>
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>




                        <form action="/partner/net/UserProfileList?ctoken=<%=ctoken%>" method="post" name="forms" class="form-horizontal">
                            <%
                                MerchantDAO merchantDAO = new MerchantDAO();
                                MerchantDetailsVO merchantDetailsVO=merchantDAO.getALLUserMerchantForPartner((String) session.getAttribute("merchantid"));
                                Map<String, List<TerminalVO>> terminalVOsByMember = terminalManager.getTerminalsByMemberIdForPartner((String) session.getAttribute("merchantid"));

                            %>

                            <div class="widget-content padding">
                                <div id="horizontal-form">

                                    <%
                                        String partnerId = (String) session.getAttribute("merchantid");
                                        if(request.getAttribute("error")!=null)
                                        {
                                            ValidationErrorList error = (ValidationErrorList) request.getAttribute("error");
                                            for (ValidationException errorList : error.errors())
                                            {
                                                //out.println("<center><font class=\"textb\">" + errorList.getMessage() + "</font></center>");
                                                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errorList.getMessage() + "</h5>");
                                            }
                                        }
                                        if(request.getAttribute("catchError")!=null)
                                        {
                                            //out.println("<center><font class=\"textb\">" + request.getAttribute("catchError") + "</font></center>");
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + request.getAttribute("catchError") + "</h5>");
                                        }
                                        if(request.getAttribute("DELETED")!=null)
                                        {
                                            //out.println("<center><font class=\"textb\">Deleted Successfully</font></center>");
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;Deleted Successfully</h5>");
                                        }
                                    %>


                                    <div class="form-group col-md-8">
                                        <label class="col-sm-3 control-label"><%=addOrUpdateUserProfile_PartnerID%></label>
                                        <div class="col-sm-6">
                                            <input type="text" name="partnerid" class="form-control"  value="<%=partnerId%>" disabled>
                                        </div>
                                    </div>


                                    <%--<div class="form-group col-md-3 has-feedback">&nbsp;</div>--%>
                                    <div class="form-group col-md-3">
                                        <div class="col-sm-offset-2 col-sm-3">
                                            <button type="submit" class="btn btn-default"><i class="fa fa-clock-o"></i>
                                                &nbsp;&nbsp;<%=addOrUpdateUserProfile_Search%></button>
                                        </div>
                                    </div>



                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>





            <%
                ActionVO actionVO  = new ActionVO();
                UserSetting userSetting=null;

                String profileId="";
                String memberID = "";
                String onlineProcessingURL="";
                String offlineProcessingUrl="";
                String clkey=merchantDetailsVO.getKey();
                String riskProfile="";
                String businessProfile="";
                String defaultMode="";
                String currency="";
                String onlineThreshold="";
                String offlinethreshold="";

                String company_name=merchantDetailsVO.getCompany_name();
                String contact_emails=merchantDetailsVO.getContact_emails();
                String addressverification=merchantDetailsVO.getAddressValidation();
                String addressdetails=merchantDetailsVO.getAddressDeatails();
                String autoredirect=merchantDetailsVO.getAutoRedirect();

                String background="";
                String foreground="";
                String font="";
                //String logo="";

                if(request.getAttribute("actionVO")!=null)
                {
                    actionVO= (ActionVO) request.getAttribute("actionVO");
                }
                if(request.getAttribute("userSetting")!=null)
                {
                    userSetting= (UserSetting) request.getAttribute("userSetting");
                }
                if(userSetting!=null)
                {
                    for(Map.Entry<String,MemberDetails> memberDetailsEntry:userSetting.getMembers().entrySet())
                    {
                        MerchantVO merchantVO=memberDetailsEntry.getValue().getUserSetting();
                        TemplateVO templateVO=memberDetailsEntry.getValue().getTemplateSetting();

                        profileId=memberDetailsEntry.getKey();
                        memberID= merchantVO.getMemberId();
                        onlineProcessingURL=merchantVO.getOnlineTransactionURL();
                        offlineProcessingUrl=merchantVO.getOfflineTransactionURL();
                        clkey=merchantVO.getKey();
                        company_name=merchantVO.getCompanyName();
                        contact_emails=merchantVO.getContactEmail();
                        currency=merchantVO.getCurrency();
                        autoredirect=merchantVO.getAutoRedirect();
                        onlineThreshold= String.valueOf(merchantVO.getOnlineThreshold());
                        offlinethreshold=String.valueOf(merchantVO.getOfflineThreshold());
                        addressverification=merchantVO.getAddressVerification();
                        addressdetails=merchantVO.getAddressDetailDisplay();
                        riskProfile=merchantVO.getRiskProfile();
                        businessProfile=merchantVO.getBusinessProfile();
                        defaultMode=merchantVO.getDefaultMode();

                        background=templateVO.getBackgroundColor();
                        foreground=templateVO.getForegroundColor();
                        font=templateVO.getFontColor();
                        logo=templateVO.getLogo();
                    }
                }

                List<ProfileVO> riskProfileVOs=profileManagementManager.getListOfRiskProfileVO((String) session.getAttribute("merchantid"), null, "profileid ASC", null);
                List<com.manager.vo.businessRuleVOs.ProfileVO> businessProfileVOs=profileManagementManager.getListOfBusinessProfileVO((String) session.getAttribute("merchantid"), null, "profileid ASC", null);
            %>

            <form action="/partner/net/AddOrUpdateUserProfile?ctoken=<%=ctoken%>" method="post" name="myForm" class="form-horizontal">

                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <%if (actionVO.isView())
                                {%>
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=addOrUpdateUserProfile_View_User_Profile%></strong></h2>
                                <input type="hidden" name="memberid" value="<%=memberID%>"/>
                                <%
                                }
                                else if (actionVO.isEdit())
                                {
                                %>
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=addOrUpdateUserProfile_Edit_User_Profile%></strong></h2>
                                <input type="hidden" name="memberid" value="<%=memberID%>"/>
                                <%
                                }
                                else if(actionVO.isAdd())
                                {
                                %>
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=addOrUpdateUserProfile_Add_New_User_Profile%></strong></h2>
                                <%
                                    }
                                %>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <div class="widget-content" style="overflow-x: auto;">


                                <%--<div align="center" class="textb"><h5><b><u>User Setting</u></b></h5></div>--%>



                                <div class="widget-content padding">

                                    <input type="hidden" value="<%=ctoken%>" name="ctoken"/>
                                    <%
                                        if(request.getAttribute("errorL")!=null)
                                        {
                                            ValidationErrorList error = (ValidationErrorList) request.getAttribute("errorL");
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;");
                                            for (ValidationException errorList : error.errors())
                                            {
                                                //out.println("<tr><td colspan=2><center><font class=\"textb\">" + errorList.getMessage() + "</font></center></td></tr>");
                                                out.println(errorList.getMessage());
                                                out.println("<br>");
                                            }

                                            out.println("</h5>");
                                        }
                                        if(request.getAttribute("catchErrorL")!=null)
                                        {
                                            //out.println("<tr><td colspan=2><center><font class=\"textb\">" + request.getAttribute("catchErrorL") + "</font></center></tr></td>");
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;"+request.getAttribute("catchErrorL")+"</h5>");
                                        }
                                        if(request.getAttribute("update")!=null && "true".equalsIgnoreCase(request.getAttribute("update").toString()))
                                        {
                                            //out.println("<tr><td colspan=2><center><font class=\"textb\">Updated successfully</font></center></tr></td>");
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;Updated successfully</h5>");
                                        }
                                    %>

                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <label class="col-md-3 control-label"><%=addOrUpdateUserProfile_Partner_ID%></label>
                                        <div class="col-md-6">
                                            <input type="text" class="form-control" name="partnerid" value="<%=session.getAttribute("merchantid")%>" disabled>
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <label class="col-md-3 control-label"><%=addOrUpdateUserProfile_Online_Processing_URL%><br>
                                            <%=addOrUpdateUserProfile_select%></label>
                                        <div class="col-md-6">
                                            <%=addOrUpdateUserProfile_https%>
                                            <input type="text" class="form-control" style="display: inline-block; width: 60%;" name="onlineProcessingUrl" <%if(actionVO.isView()){%>readonly style="background-color: #EBEBE4" <%}%> value="<%=functions.isValueNull(onlineProcessingURL)?onlineProcessingURL.replace("/transactionServices","").replace("https://",""):""%>"> /transactionServices
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <label class="col-md-3 control-label"><%=addOrUpdateUserProfile_Offline_Processing_URL%><br>
                                            <%=addOrUpdateUserProfile_select_offline%></label>
                                        <div class="col-md-6">
                                            <%=addOrUpdateUserProfile_https%>
                                            <input type="text" class="form-control" style="display: inline-block; width: 60%;" name="offlineProcessingUrl" <%if(actionVO.isView()){%>readonly style="background-color: #EBEBE4" <%}%> value="<%=functions.isValueNull(offlineProcessingUrl)?offlineProcessingUrl.replace("/transactionServices","").replace("https://",""):""%>" > /transactionServices
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <label class="col-md-3 control-label"><%=addOrUpdateUserProfile_Key%><br>
                                            <%=addOrUpdateUserProfile_secret_key%></label>
                                        <div class="col-md-6">
                                            <input type="text" class="form-control" name="clkey" <%if(actionVO.isView()){%>readonly style="background-color: #EBEBE4" <%}%> value="<%=functions.isValueNull(clkey)?clkey:""%>" disabled>
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <label class="col-md-3 control-label"><%=addOrUpdateUserProfile_Company_Name%><br>
                                            <%=addOrUpdateUserProfile_partner_company%></label>
                                        <div class="col-md-6">
                                            <input type="text" class="form-control" name="company_name" <%if(actionVO.isView()){%>readonly style="background-color: #EBEBE4" <%}%> value="<%=functions.isValueNull(company_name)?company_name:""%>" disabled>
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <label class="col-md-3 control-label"><%=addOrUpdateUserProfile_Contact_Email%><br>
                                            <%=addOrUpdateUserProfile_partner_email%></label>
                                        <div class="col-md-6">
                                            <%--<input type="text" class="form-control" name="contact_emails" <%if(actionVO.isView()){%>readonly style="background-color: #EBEBE4" <%}%> value="<%=functions.isValueNull(contact_emails)?contact_emails:""%>" disabled>--%>
                                            <input type="hidden" name="contact_emails" id="contactemails_hidden"  class="form-control" value="<%=functions.isValueNull(contact_emails)?contact_emails:""%>">
                                            <input type="Text"  id="contactemails"  class="form-control hidedivemail"  <%if(actionVO.isView()){%>readonly style="background-color: #EBEBE4" <%}%> value="<%=functions.isValueNull(contact_emails)?contact_emails:""%>" onchange="setvalue('contactemails')" disabled><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hideemail('contactemails')"></span>
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <label class="col-md-3 control-label"><%=addOrUpdateUserProfile_Currency%></label>
                                        <div class="col-md-6">
                                            <select name="currency" class="form-control" <%=actionVO.isView()?"disabled":""%>>
                                                <%=commonFunctionUtil.getCurrencyUnit(currency, pzTransactionCurrenciesNotNeeded)%>
                                            </select>
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <label class="col-md-3 control-label"><%=addOrUpdateUserProfile_Autoredirect%><br>
                                            <%=addOrUpdateUserProfile_autoredirect_configuration%></label>
                                        <div class="col-md-6">
                                            <input type="text" class="form-control" name="autoRedirect" <%if(actionVO.isView()){%>readonly style="background-color: #EBEBE4" <%}%> value="<%=functions.isValueNull(autoredirect)?autoredirect:""%>" disabled>
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <label class="col-md-3 control-label"><%=addOrUpdateUserProfile_Online_Risk_Threshold%><br>
                                            <%=addOrUpdateUserProfile_numbers%></label>
                                        <div class="col-md-6">
                                            <input type="text" class="form-control" name="onlineThreshold" <%if(actionVO.isView()){%>readonly style="background-color: #EBEBE4" <%}%> value="<%=functions.isValueNull(onlineThreshold)?onlineThreshold:""%>" >
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <label class="col-md-3 control-label"><%=addOrUpdateUserProfile_Offline_Risk_Threshold%><br>
                                            <%=addOrUpdateUserProfile_offline_number%></label>
                                        <div class="col-md-6">
                                            <input type="text" class="form-control" name="offlinethreshold" <%if(actionVO.isView()){%>readonly style="background-color: #EBEBE4" <%}%> value="<%=functions.isValueNull(offlinethreshold)?offlinethreshold:""%>" >
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <label class="col-md-3 control-label"><%=addOrUpdateUserProfile_Address_Verification%><br>
                                            <%=addOrUpdateUserProfile_Merchant_configuration%></label>
                                        <div class="col-md-6">
                                            <input type="text" class="form-control" name="addressverification"  value="<%=functions.isValueNull(addressverification)?addressverification:""%>" readonly style="background-color: #EBEBE4">
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <label class="col-md-3 control-label"><%=addOrUpdateUserProfile_Address_Details_Display%><br>
                                            <%=addOrUpdateUserProfile_Address_Configuration%></label>
                                        <div class="col-md-6">
                                            <input type="text" class="form-control" name="addressdetaildisplay"  value="<%=addressdetails%>" readonly style="background-color: #EBEBE4" disabled>
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <label class="col-md-3 control-label"><%=addOrUpdateUserProfile_Default_Mode%><br>
                                            <%=addOrUpdateUserProfile_select_default%></label>
                                        <div class="col-md-6">
                                            <select name="defaultMode" class="form-control" <%=actionVO.isView()?"disabled":""%>>
                                                <%=commonFunctionUtil.getDefaultModeOptions(defaultMode)%>
                                            </select>
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <label class="col-md-3 control-label"><%=addOrUpdateUserProfile_Risk_Profile%><br>
                                            <%=addOrUpdateUserProfile_Select_Risk%></label>
                                        <div class="col-md-6">
                                            <select name="profileid" class="form-control" <%=actionVO.isView()?"disabled":""%>>
                                                <%=profileManagementManager.getOptionForRiskProfile(riskProfileVOs,riskProfile)%>
                                            </select>
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <label class="col-md-3 control-label"><%=addOrUpdateUserProfile_Business_Profile%><br>
                                            <%=addOrUpdateUserProfile_Select_Business_Profile%></label>
                                        <div class="col-md-6">
                                            <select name="businessProfile" class="form-control" <%=actionVO.isView()?"disabled":""%>>
                                                <%=profileManagementManager.getOptionForBusinessProfile(businessProfileVOs, businessProfile)%>
                                            </select>
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>



                                </div>




                                <%--                                <div id="showingid"><strong>Showing Page <%=pageno%> of <%=totalrecords%> records</strong></div>

                                                                <jsp:include page="page.jsp" flush="true">
                                                                    <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
                                                                    <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
                                                                    <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
                                                                    <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
                                                                    <jsp:param name="page" value="<%=paginationVO.getPage()%>"/>
                                                                    <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
                                                                    <jsp:param name="orderby" value=""/>
                                                                </jsp:include>--%>

                            </div>


                        </div>
                    </div>

                </div>

                <div class="row">

                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=addOrUpdateUserProfile_Template_Setting%></strong></h2>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <div class="widget-content" style="overflow-x: auto;">

                                <div class="widget-content padding">


                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <label class="col-md-3 control-label"><%=addOrUpdateUserProfile_Background_Color%><br>
                                            <%=addOrUpdateUserProfile_Enter_color%></label>
                                        <div class="col-md-6">
                                            <input name="background" type="text" class="form-control" <%if(actionVO.isView()){%>readonly style="background-color: #EBEBE4" <%}%> value="<%=functions.isValueNull(background)?background:""%>">
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <label class="col-md-3 control-label"><%=addOrUpdateUserProfile_Foreground_Color%><br>
                                            <%=addOrUpdateUserProfile_Enter_Black%></label>
                                        <div class="col-md-6">
                                            <input name="foreground" type="text" class="form-control" <%if(actionVO.isView()){%>readonly style="background-color: #EBEBE4" <%}%> value="<%=functions.isValueNull(foreground)?foreground:""%>">
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <label class="col-md-3 control-label"><%=addOrUpdateUserProfile_Font_Color%><br>
                                            <%=addOrUpdateUserProfile_Enter_Black%></label>
                                        <div class="col-md-6">
                                            <input name="font" type="text" class="form-control" <%if(actionVO.isView()){%>readonly style="background-color: #EBEBE4" <%}%> value="<%=functions.isValueNull(font)?font:""%>">
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <label class="col-md-3 control-label"><%=addOrUpdateUserProfile_Logo%><br>
                                            <%=addOrUpdateUserProfile_Enter_Logo_Name%></label>
                                        <div class="col-md-6">
                                            <input name="merchantlogo" type="text" class="form-control"  <%if(actionVO.isView()){%>readonly style="background-color: #EBEBE4" <%}%> value="<%=functions.isValueNull(logo)?logo:""%>">
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>



                                </div>


                            </div>


                        </div>
                    </div>

                </div>


                <div class="form-group col-md-12 has-feedback">
                    <center>
                        <label >&nbsp;</label>
                        <%if(actionVO.isAdd()){%>
                        <button type="submit" class="btn btn-default" name="action" value="1_Add"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=addOrUpdateUserProfile_Add%></button>
                        <button type="submit" class="btn btn-default" name="toid" VALUE="<%=functions.isValueNull(request.getParameter("toid"))?request.getParameter("toid"):""%>" onclick="cancel('<%=ctoken%>')"><i class="fa fa-times"></i>&nbsp;&nbsp;<%=addOrUpdateUserProfile_Cancel%></button>
                        <%
                            }
                        %>
                        <%if(actionVO.isEdit())
                        {
                        %>
                        <button type="submit" class="btn btn-default" name="action" VALUE="<%=profileId%>_Edit"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=addOrUpdateUserProfile_Update%></button>
                        <button type="submit" class="btn btn-default" name="toid" VALUE="<%=functions.isValueNull(request.getParameter("toid"))?request.getParameter("toid"):""%>" onclick="cancel('<%=ctoken%>')"><i class="fa fa-times"></i>&nbsp;&nbsp;<%=addOrUpdateUserProfile_Cancel%></button>
                        <%
                            }
                        %>
                        <%if(actionVO.isView())
                        {
                        %>
                        <button type="submit" class="btn btn-default" name="toid" VALUE="<%=functions.isValueNull(request.getParameter("toid"))?request.getParameter("toid"):""%>" onclick="cancel('<%=ctoken%>')"><i class="fa fa-times"></i>&nbsp;&nbsp;<%=addOrUpdateUserProfile_Cancel%></button>
                        <%
                            }
                        %>

                    </center>
                </div>
                <input type="hidden" name="addressvalidation" value="<%=merchantDetailsVO.getAddressValidation()%>">
                <input type="hidden" name="addressdetaildisplay" value="<%=merchantDetailsVO.getAddressDeatails()%>">
                <input type="hidden" name="autoRedirect" value="<%=merchantDetailsVO.getAutoRedirect()%>">


            </form>

        </div>

    </div>
</div>


<script>
    <%=memberId%>;
    <%=terminalForMemberArray%>;
    <%=terminalToMemberArray%>;
    <%=key%>;
    <%=companyName%>;
    <%=contactEmail%>;
    <%=addressVerification%>;
    <%=autoRedirect%>;


    function selectedDropdown(name)
    {
        var hat1 = this.document.getElementsByName(name)[0].selectedIndex ;
        var hatto1 = this.document.getElementsByName(name)[0].options[hat1].value;
        if(hatto1!="")
        {
            this.document.getElementsByName("clkey")[0].value = key[memberId.indexOf(hatto1)];
            //this.document.getElementsByName("currency")[0].value = currency[memberId.indexOf(hatto1)];
            this.document.getElementsByName("company_name")[0].value = companyName[memberId.indexOf(hatto1)];
            this.document.getElementsByName("contact_emails")[0].value = contactEmail[memberId.indexOf(hatto1)];
            //this.document.getElementsByName("addressverification")[0].value=addressVerification[memberId.indexOf(hatto1)];
            this.document.getElementsByName("autoRedirect")[0].value = autoRedirect[memberId.indexOf(hatto1)];
        }
        else
        {
            this.document.getElementsByName("clkey")[0].value = "";
            //this.document.getElementsByName("currency")[0].value = "";
            this.document.getElementsByName("company_name")[0].value = "";
            this.document.getElementsByName("contact_emails")[0].value = "";
            //this.document.getElementsByName("addressverification")[0].value="";
            this.document.getElementsByName("autoRedirect")[0].value = "";
        }

    }

    function selectedMultipleTerminal()
    {

        var selectedTerminalForMember=[];
        var addressValidation="";
        $('#multipleTerminal option:selected').each(function()
                {
                    var selectedInput = $(this).val();
                    var terminalString=terminalToMemberArray[selectedInput];
                    if(terminalString!=null && terminalString!="")
                    {
                        selectedTerminalForMember[terminalString]="Y";
                        $("#"+terminalString+"").attr("selected","");
                        if(addressValidation!=0)
                        {
                            addressValidation+=",";
                        }
                        addressValidation+=(selectedInput)+"="+(addressVerification[selectedInput]);
                    }
                }
        );
        this.document.getElementsByName("addressverification")[0].value=(addressValidation);

        $('#multipleTerminal option:not(:selected)').each(function()
                {

                    var selectedInput = $(this).val();
                    var terminalString=terminalToMemberArray[selectedInput];
                    if(terminalString!=null && terminalString!="" && selectedTerminalForMember[terminalString]!="Y")
                    {
                        $("#"+terminalString+"").removeAttr("selected");
                    }
                }
        );
    }

    function selectedMultipleMember()
    {
        var addressValidation="";
        $('#multipleMember option:selected').each(function()
                {
                    var selectedInput = $(this).val();
                    var terminalString=terminalForMemberArray[Number(selectedInput)];
                    if(terminalString!=null && terminalString!="")
                        for(var i=0;i<terminalString.split(":").length;i++)
                        {
                            document.getElementById(terminalString.split(":")[i]).setAttribute("selected","");
                            if(addressValidation!=0)
                            {
                                addressValidation+=",";
                            }
                            addressValidation+=(selectedInput)+"="+(addressVerification[terminalString.split(":")[i]]);
                        }
                }
        );
        this.document.getElementsByName("addressverification")[0].value=(addressValidation);

        $('#multipleMember option:not(:selected)').each(function()
                {

                    var selectedInput = $(this).val();
                    var terminalString=terminalForMemberArray[selectedInput];
                    if(terminalString!=null && terminalString!="")
                    {
                        for (var i = 0; i < terminalString.split(":").length; i++)
                        {
                            $("#"+terminalString.split(":")[i]+"").removeAttr("selected");
                        }
                    }
                }
        );
    }

    function cancel(ctoken) {
        document.myForm.action="/partner/net/UserProfileList?ctoken="+ctoken;
    }
</script>
</body>
</html>