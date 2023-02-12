<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.ProfileManagementManager" %>
<%@ page import="com.manager.enums.DataType" %>
<%@ page import="com.manager.enums.PZOperatorEnums" %>
<%@ page import="com.manager.enums.RuleTypeEnum" %>
<%@ page import="com.manager.vo.ActionVO" %>
<%@ page import="com.manager.vo.PayIfeTableInfo" %>
<%@ page import="com.manager.vo.businessRuleVOs.BusinessProfile" %>
<%@ page import="com.manager.vo.businessRuleVOs.ProfileVO" %>
<%@ page import="com.manager.vo.businessRuleVOs.RuleOperation" %>
<%@ page import="com.manager.vo.businessRuleVOs.RuleVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.HashSet" %>
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
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","businessProfile");
  ResourceBundle rb1 = null;
  String language_property1 = (String) session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String addBusinessProfile_Business_Profile = StringUtils.isNotEmpty(rb1.getString("addBusinessProfile_Business_Profile")) ? rb1.getString("addBusinessProfile_Business_Profile") : "Business Profile";
  String addBusinessProfile_ProfileID = StringUtils.isNotEmpty(rb1.getString("addBusinessProfile_ProfileID")) ? rb1.getString("addBusinessProfile_ProfileID") : "Profile ID";
  String addBusinessProfile_All = StringUtils.isNotEmpty(rb1.getString("addBusinessProfile_All")) ? rb1.getString("addBusinessProfile_All") : "All";
  String addBusinessProfile_Search = StringUtils.isNotEmpty(rb1.getString("addBusinessProfile_Search")) ? rb1.getString("addBusinessProfile_Search") : "Search";
  String addBusinessProfile_Profile_Details = StringUtils.isNotEmpty(rb1.getString("addBusinessProfile_Profile_Details")) ? rb1.getString("addBusinessProfile_Profile_Details") : "Profile Details";
  String addBusinessProfile_Profile_Name = StringUtils.isNotEmpty(rb1.getString("addBusinessProfile_Profile_Name")) ? rb1.getString("addBusinessProfile_Profile_Name") : "Profile Name:";
  String addBusinessProfile_Details = StringUtils.isNotEmpty(rb1.getString("addBusinessProfile_Details")) ? rb1.getString("addBusinessProfile_Details") : "Details";
  String addBusinessProfile_Rule_Name = StringUtils.isNotEmpty(rb1.getString("addBusinessProfile_Rule_Name")) ? rb1.getString("addBusinessProfile_Rule_Name") : "Rule Name";
  String addBusinessProfile_Description = StringUtils.isNotEmpty(rb1.getString("addBusinessProfile_Description")) ? rb1.getString("addBusinessProfile_Description") : "Description";
  String addBusinessProfile_IsApplicable = StringUtils.isNotEmpty(rb1.getString("addBusinessProfile_IsApplicable")) ? rb1.getString("addBusinessProfile_IsApplicable") : "IsApplicable";
  String addBusinessProfile_Rule_Operation = StringUtils.isNotEmpty(rb1.getString("addBusinessProfile_Rule_Operation")) ? rb1.getString("addBusinessProfile_Rule_Operation") : "Rule Operation";
  String addBusinessProfile_Action = StringUtils.isNotEmpty(rb1.getString("addBusinessProfile_Action")) ? rb1.getString("addBusinessProfile_Action") : "Action";
  String addBusinessProfile_Operation_Type = StringUtils.isNotEmpty(rb1.getString("addBusinessProfile_Operation_Type")) ? rb1.getString("addBusinessProfile_Operation_Type") : "Operation Type";
  String addBusinessProfile_Input_Name = StringUtils.isNotEmpty(rb1.getString("addBusinessProfile_Input_Name")) ? rb1.getString("addBusinessProfile_Input_Name") : "Input Name";
  String addBusinessProfile_Operator = StringUtils.isNotEmpty(rb1.getString("addBusinessProfile_Operator")) ? rb1.getString("addBusinessProfile_Operator") : "Operator";
  String addBusinessProfile_File_Path = StringUtils.isNotEmpty(rb1.getString("addBusinessProfile_File_Path")) ? rb1.getString("addBusinessProfile_File_Path") : "File Path";
  String addBusinessProfile_Value1 = StringUtils.isNotEmpty(rb1.getString("addBusinessProfile_Value1")) ? rb1.getString("addBusinessProfile_Value1") : "Value1";
  String addBusinessProfile_Value2 = StringUtils.isNotEmpty(rb1.getString("addBusinessProfile_Value2")) ? rb1.getString("addBusinessProfile_Value2") : "Value2";
  String addBusinessProfile_Delete = StringUtils.isNotEmpty(rb1.getString("addBusinessProfile_Delete")) ? rb1.getString("addBusinessProfile_Delete") : "Delete";
  String addBusinessProfile_Edit = StringUtils.isNotEmpty(rb1.getString("addBusinessProfile_Edit")) ? rb1.getString("addBusinessProfile_Edit") : "Edit";
  String addBusinessProfile_Output_Type = StringUtils.isNotEmpty(rb1.getString("addBusinessProfile_Output_Type")) ? rb1.getString("addBusinessProfile_Output_Type") : "Output Type";
  String addBusinessProfile_Add = StringUtils.isNotEmpty(rb1.getString("addBusinessProfile_Add")) ? rb1.getString("addBusinessProfile_Add") : "Add";
  String addBusinessProfile_Update = StringUtils.isNotEmpty(rb1.getString("addBusinessProfile_Update")) ? rb1.getString("addBusinessProfile_Update") : "Update";
  String addBusinessProfile_Cancel = StringUtils.isNotEmpty(rb1.getString("addBusinessProfile_Cancel")) ? rb1.getString("addBusinessProfile_Cancel") : "Cancel";

%>
<%!
  private static Logger logger=new Logger("businessProfile.jsp");
%>
<%
  try{
%>
<html>
<head>
  <title><%=company%> | Business Profile</title>
  <script>
    var addedRow=[];
    var currentRowStatus=[];
    var option=[];
    var optionValue=[];
    var optionStatus=[];
    var selectedOption=[];
    var currentRowFlag=1;
  </script>


  <style type="text/css">

    .textb {
      color: red;
      font-weight: bold;
    }

  </style>

</head>
<body>


<%
  session.setAttribute("submit","businessProfile");
%>
<%!private Functions functions = new Functions();

%>
<%
  Map<String,String> businessIdAndIdMap=new HashMap<String, String>();
%>
<style type="text/css">
  .testdiv
  {
    display: none;
  }
/*  .tableScroll
  {
    width: 400px;
    overflow-x: auto;
  }*/

  #ruleOperationTR_1{
    /*padding: 4px 13px 4px 13px;
    margin: 0px;*/
    font-family: 'Open Sans';
    font-weight: 700;
    font-size: 14px;
    /*display: block;*/
    color: #5b5b5b;
  }

  #ruleOperation_1 tr:nth-of-type(2){
    background-color: #7eccad !important;
    color: white;
    padding-top: 13px;
    padding-bottom: 13px;
    font-family: Open Sans;
    font-size: 13px;
    font-weight: 600;
  }

  #ruleOperation_1 textarea{
    width: inherit;
  }


  #ruleOperation_1 input{
    width: inherit;
  }



</style>

<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">


      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">

            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=addBusinessProfile_Business_Profile%></strong></h2>
              <div class="additional-btn">
                 <%-- <form action="/partner/net/SingleRiskRuleDetails?ctoken=<%=ctoken%>" method="POST" name="myForm">
                      <button type="submit" class="btn btn-default" style="width:250px; font-size:14px" name="action" value="1_Add" onclick="cancel('<%=ctoken%>')">
                          <i class="fa fa-download"></i>
                          &nbsp;&nbsp;Download Risk Profile XMl
                      </button>
                      <button type="submit" class="btn btn-default" style="width:250px; font-size:14px" name="action" value="1_Add" onclick="add('<%=ctoken%>')">
                          <i class="fa fa-sign-in"></i>
                          &nbsp;&nbsp;Add New Risk Profile
                      </button>
                  </form>--%>
                  <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                  <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                  <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>



            <form action="/partner/net/BusinessProfileList?ctoken=<%=ctoken%>" method="post" name="forms" class="form-horizontal">
              <%
                ProfileManagementManager profileManagementManager=new ProfileManagementManager();
                List<ProfileVO> businessProfileVOList= profileManagementManager.getListOfBusinessProfileVO(session.getAttribute("merchantid").toString(), null, " profileid ASC", null);
              %>

              <div class="widget-content padding">
                <div id="horizontal-form">

                  <%
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

                  %>
                  <br>

                  <div class="form-group col-md-8">
                    <label class="col-sm-3 control-label"><%=addBusinessProfile_ProfileID%></label>
                    <div class="col-sm-6">
                      <select name="profileid" class="form-control">
                        <option value=""><%=addBusinessProfile_All%></option>
                        <%
                          for(ProfileVO profileVO : businessProfileVOList)
                          {
                            out.println("<option value=\""+profileVO.getId()+"\""+(profileVO.getId().equals(request.getParameter("profileid"))?"selected":"")+">"+profileVO.getId()+"</option>");
                          }
                        %>
                      </select>
                    </div>
                  </div>


                  <%--<div class="form-group col-md-3 has-feedback">&nbsp;</div>--%>
                  <div class="form-group col-md-3">
                    <div class="col-sm-offset-2 col-sm-3">
                      <button type="submit" class="btn btn-default"><i class="fa fa-clock-o"></i>
                        &nbsp;&nbsp;<%=addBusinessProfile_Search%></button>
                    </div>
                  </div>



                </div>
              </div>
            </form>
          </div>
        </div>
      </div>



      <div class="reporttable">

        <form action="/partner/net/AddOrUpdateBusinessProfile" name="myForm" onsubmit="setCountOfRows()" method="post" class="form-horizontal">
          <input type="hidden" name="ctoken" value="<%=ctoken%>">
          <div class="row">
            <div class="col-md-12">
              <div class="widget">
                <div class="widget-header">
                  <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=addBusinessProfile_Profile_Details%></strong></h2>
                  <div class="additional-btn">
                      <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                      <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                      <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                  </div>
                </div>

                <br><br>

                <%
                  boolean validationFailed=false;
                  if(request.getAttribute("errorL")!=null)
                  {
                    ValidationErrorList error = (ValidationErrorList) request.getAttribute("errorL");
                    for (ValidationException errorList : error.errors())
                    {
                      //out.println("<center><font class=\"textb\">" + errorList.getMessage() + "</font></center>");
                      out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;"+errorList.getMessage()+"</h5>");
                    }
                    validationFailed=true;
                  }
                  if(request.getAttribute("catchErrorL")!=null)
                  {
                    //out.println("<center><font class=\"textb\">" + request.getAttribute("catchErrorL") + "</font></center>");
                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;"+request.getAttribute("catchErrorL")+"</h5>");
                    validationFailed=true;
                  }

                %>
                <%
                  ActionVO actionVO= (ActionVO) request.getAttribute("actionVO");



                  String profileName =null;
                  String profileId=null;

                  Map<String,RuleVO> businessRuleList=null;
                  List<RuleVO> presentBusinessRuleList=null;
                  Map<String,PayIfeTableInfo> payIfeTableInfoMap =new HashMap<String, PayIfeTableInfo>();
                  Set<String> tableAliasName=new HashSet<String>();

                  if(request.getAttribute("payIfeTableInfo")!=null)
                    payIfeTableInfoMap= (Map<String, PayIfeTableInfo>) request.getAttribute("payIfeTableInfo");
                  if(request.getAttribute("tableAliasName")!=null)
                    tableAliasName= (Set<String>) request.getAttribute("tableAliasName");
                  if(request.getAttribute("businessRuleList")!=null)
                    businessRuleList= (Map<String, RuleVO>) request.getAttribute("businessRuleList");
                  if(request.getAttribute("businessProfile")!=null)
                  {
                    BusinessProfile businessProfile= (BusinessProfile) request.getAttribute("businessProfile");
                    if(businessProfile.getProfiles()!=null)
                    {
                      for(Map.Entry<String,ProfileVO> profileVO:businessProfile.getProfiles().entrySet())
                      {
                        profileName=profileVO.getValue().getName();
                        profileId=profileVO.getValue().getId();
                        presentBusinessRuleList=profileVO.getValue().getRules();
                      }
                    }
                  }

                  //this is for EDIT or view
                  int presentCount=0;
                  StringBuffer addedrow=new StringBuffer();
                  StringBuffer currentRowStatus=new StringBuffer();
                  StringBuffer selectedOption=new StringBuffer();

                  //initail
                  int count=0;
                  StringBuffer businessRule=new StringBuffer();
                  StringBuffer option=new StringBuffer();
                  StringBuffer optionValue=new StringBuffer();
                  StringBuffer optionStatus=new StringBuffer();
                  if(businessRuleList!=null && businessRuleList.size()>0)
                  {
                    for (Map.Entry<String,RuleVO> businessRulePair : businessRuleList.entrySet())
                    {
                      RuleVO business =businessRulePair.getValue();

                      businessRule.append("<option value=\\\"" + business.getId() + "\\\"  id=\\\"opt_" + (count + 1) + "\\\">" + business.getName() + "</option>");
                      option.append("option[" + (count + 1) + "]=\"opt_" + (count + 1) + "\";");
                      optionValue.append("optionValue[" + (count + 1) + "]=\"" + business.getId() + "|" + business.getDescription() + "|" + business.getRuleType() +"|");
                      optionStatus.append("optionStatus[" + (count + 1) + "]=\"N\";");
                      if(business.getRuleOperation()!=null)
                      {
                        int innerCount=1;
                        for(RuleOperation ruleOperation:business.getRuleOperation())
                        {
                          if(innerCount>1)
                          {
                            optionValue.append(",");
                          }

                          optionValue.append(ruleOperation.getInputName()+":"+ruleOperation.getOperator()+":");
                          if(RuleTypeEnum.DATABASE.name().equals(business.getRuleType()))
                          {
                            String inputName=null;
                            String aggregateFunction="";
                            if(ruleOperation.getInputName().contains("(") || ruleOperation.getInputName().contains(")"))
                            {
                              inputName=ruleOperation.getInputName().substring(ruleOperation.getInputName().indexOf("(")+1,ruleOperation.getInputName().indexOf(")"));
                              aggregateFunction=ruleOperation.getInputName().substring(0,ruleOperation.getInputName().indexOf("("))+" Of ";
                            }
                            else
                            {
                              inputName=ruleOperation.getInputName();
                            }
                            if(payIfeTableInfoMap!=null && payIfeTableInfoMap.containsKey(inputName))
                            {
                              optionValue.append(aggregateFunction+payIfeTableInfoMap.get(inputName).getDescription()+":");
                            }
                            else
                            {
                              optionValue.append(inputName+":");
                            }
                          }
                          else
                          {
                            optionValue.append(ruleOperation.getInputName()+":");
                          }
                          optionValue.append(functions.isValueNull(ruleOperation.getComparator())?ruleOperation.getComparator():"");
                          optionValue.append(":");
                          optionValue.append(functions.isValueNull(ruleOperation.getDataType())?ruleOperation.getDataType():"");
                          optionValue.append(":");
                          optionValue.append(functions.isValueNull(ruleOperation.getEnumValue())?ruleOperation.getEnumValue().replaceAll(",","!"):"");
                          optionValue.append(":");
                          optionValue.append(functions.isValueNull(ruleOperation.getOperationType())?ruleOperation.getOperationType():"");

                          innerCount++;
                        }
                        optionValue.append("\";");
                      }
                      businessIdAndIdMap.put(business.getId(), "opt_" + (count + 1));
                      count++;
                    }
                  }
                %>

                <div class="widget-content padding">


                  <div class="form-group">
                    <div class="col-md-2"></div>
                    <label class="col-md-2 control-label"><%=addBusinessProfile_Profile_Name%></label>
                    <div class="col-md-4">
                      <input type="text" name="businessProfileName" value="<%=functions.isValueNull(profileName)?profileName:(functions.isValueNull(request.getParameter("businessProfileName"))?request.getParameter("businessProfileName"):"")%>" class="form-control" <%=actionVO.isView()?"disabled":""%>>
                    </div>
                    <div class="col-md-6"></div>
                  </div>


                </div>

              </div>
            </div>
          </div>

          <div class="row">
            <div class="col-md-12">
              <div class="widget">
                <div class="widget-header">
                  <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=addBusinessProfile_Details%></strong></h2>
                  <div class="additional-btn">
                    <%if(!actionVO.isView()){%>
                    <input type="Button" class="btn btn-default" onClick="addRow('myTable')" value="Add Row">
                    <%
                      }
                    %>


                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                  </div>
                </div>
                <div class="widget-content padding" style="overflow-x: auto;">


                  <table id="myTable" align=center class="table table-striped table-bordered table-green dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                    <%--<TR>
                      <td colspan="<%= actionVO.isView()?"4":"6"%>" valign="middle" align="center" class="th0">
                        <input type="text" name="riskProfileName" value="<%=functions.isValueNull(profileName)?profileName:""%>" class="txtbox" <%=actionVO.isView()?"disabled":""%>>
                      </td>
                    </TR>--%>

                    <tr>
                      <thead>
                      <%--<TH class="th0">Check(please check below check box for profile to be added)</TH>--%>
                      <td valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=addBusinessProfile_Rule_Name%></b></td>
                      <td valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=addBusinessProfile_Description%></b></td>
                      <td valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=addBusinessProfile_IsApplicable%></b></td>
                      <td valign="middle" align="center" colspan="3" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=addBusinessProfile_Rule_Operation%></b></td>
                      <%
                        if(!actionVO.isView())
                        {
                      %>
                      <td valign="middle" align="center" colspan="2" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><%=addBusinessProfile_Action%></td>
                      <%
                        }
                      %>
                      </thead>
                    </tr>

                    <%
                      if(presentBusinessRuleList!=null && !validationFailed)
                      {
                        for(RuleVO ruleVO:presentBusinessRuleList)
                        {
                    %>

                    <tr>
                      <%--<TH class="th0">Check(please check below check box for profile to be added)</TH>--%>
                      <td valign="middle" align="center" data-label="Rule Name"><select name="businessProfileId_<%=(presentCount+1)%>" style="width:inherit;" class="form-control" onchange="selectedDropdown('businessProfileId_<%=(presentCount+1)%>')" disabled><%=profileManagementManager.getOptionTagForBusinessRule(businessRuleList, businessIdAndIdMap, ruleVO.getId())%></select><input type="hidden" name="Text|businessProfileId_<%=(presentCount+1)%>" value="<%=ruleVO.getId()%>"></td>
                      <td valign="middle" align="center" data-label="Description"><textarea type="text" name="businessProfileDesc_<%=(presentCount+1)%>" style="width:inherit;" class="form-control"   readonly style="background-color:#EBEBE4;font-size: 12px;/*min-height:50px;max-height: 150px;max-width: 100px*/"><%=ruleVO.getDescription()%></textarea></td>
                      <td valign="middle" align="center" data-label="IsApplicable"><input type="checkbox" name="businessProfileIsApplicable_<%=(presentCount+1)%>" id="businessProfileIsApplicable_<%=(presentCount+1)%>" <%=ruleVO.isApplicable()?"checked":""%> value="Y" onclick="return false"  readonly/></td>
                      <%--<td width="15%" valign="middle" align="center" class="tr0"><input type="text" name="businessProfileRuleType_<%=(presentCount+1)%>" class="txtbox" value="<%=ruleVO.getRuleType()%>"  readonly style="background-color:#EBEBE4"/></td>--%>
                      <td valign="middle" align="center" colspan="3">
                        <div class="tableScroll">
                          <table id="ruleOperation_<%=(presentCount+1)%>" width="100%" class="table table-striped table-bordered table-green dataTable" style="margin-bottom: 0px">

                            <tr id="ruleOperationTR_<%=(presentCount+1)%>"> <td colspan="5" align="center"><%=ruleVO.getRuleType()%></tr>

                            <thead>

                            <tr style="background-color: #7eccad !important; color: white; padding-top: 13px; padding-bottom: 13px; font-family: Open Sans; font-size: 13px; font-weight: 600;">


                              <td valign="middle" align="center"><%=addBusinessProfile_Operation_Type%></td>
                              <td valign="middle" align="center"><%=addBusinessProfile_Input_Name%></td>
                              <td valign="middle" align="center" <%=RuleTypeEnum.REGULAR_EXPRESSION.name().equals(ruleVO.getRuleType())?"colspan=\"3\"":""%>><%=addBusinessProfile_Operator%></td>
                              <%
                                if(RuleTypeEnum.REGULAR_EXPRESSION.name().equals(ruleVO.getRuleType()))
                                {
                              %>

                              <%
                              }
                              else if(RuleTypeEnum.FLAT_FILE .name().equals(ruleVO.getRuleType()))
                              {
                              %>
                              <td valign="middle" align="center" colspan="2"><%=addBusinessProfile_File_Path%></td>
                              <%
                              }
                              else
                              {
                              %>
                              <td valign="middle" align="center" ><%=addBusinessProfile_Value1%></td>
                              <td valign="middle" align="center" ><%=addBusinessProfile_Value2%></td>
                              <%
                                }
                              %>
                            </tr>
                            </thead>
                            <%
                              if(ruleVO.getRuleOperation()!=null)
                              {
                                int innerCount=1;
                                for(RuleOperation ruleOperation:ruleVO.getRuleOperation())
                                {
                            %>
                            <tr>
                              <td align="center" valign="middle" data-label="Operation Type"><textarea type="text" class="form-control" name="operationType_<%=(presentCount+1)%>_<%=innerCount%>" style='/*max-width:50px;max-height:100px*/' readonly><%=ruleOperation.getOperationType()%></textarea></td>
                              <td align="center" valign="middle" data-label="Input Name"><input type="text" class="form-control" name="ruleInputName_<%=(presentCount+1)%>_<%=innerCount%>" value="<%=ruleOperation.getInputName()%>" readonly></td>
                              <td align="center" valign="middle" data-label="Operator"><input type="text" style="width:200px;" class="form-control" name="ruleOperation_<%=(presentCount+1)%>_<%=innerCount%>" value="<%=ruleOperation.getOperator()%>" readonly></td>
                              <%
                                if(RuleTypeEnum.REGULAR_EXPRESSION.name().equals(ruleVO.getRuleType()))
                                {
                              %>

                              <%
                              }
                              else if(RuleTypeEnum.FLAT_FILE .name().equals(ruleVO.getRuleType()))
                              {
                              %>
                              <td align="center" valign="middle" data-label="File Path"><input type="text" class="form-control" name="businessProfileValue1_<%=(presentCount+1)%>_<%=innerCount%>" value="<%=functions.isValueNull(ruleOperation.getValue1())?ruleOperation.getValue1():""%>"  readonly style="background-color:#EBEBE4"/></td>
                              <%
                              }
                              else
                              {
                                String inputType="text";
                                StringBuffer prefix1=new StringBuffer();
                                StringBuffer prefix2=new StringBuffer();
                                if(DataType.ENUM.name().equals(ruleOperation.getDataType()))
                                {
                                  prefix1.append("<select class=\"form-control\" name=\"selectBusinessProfileValue1_"+(presentCount+1)+ "_"+innerCount+"\" onchange=\"selectEnumValue1('"+(presentCount+1)+"','"+innerCount+"')\" disabled style=\"background-color:#EBEBE4\">");
                                  prefix2.append("<select class=\"form-control\" name=\"selectBusinessProfileValue2_"+(presentCount+1)+ "_"+innerCount+"\" onchange=\"selectEnumValue2('"+(presentCount+1)+"','"+innerCount+"')\" disabled style=\"background-color:#EBEBE4\">");

                                  prefix1.append("<option value=\"\">Select Value</option>");
                                  prefix2.append("<option value=\"\">Select Value</option>");

                                  if(functions.isValueNull(ruleOperation.getEnumValue()))
                                  {
                                    String enumValues[]=ruleOperation.getEnumValue().split(",");
                                    for (int enumvalue=0;enumvalue<enumValues.length;enumvalue++)
                                    {
                                      prefix1.append("<option value=\""+enumValues[enumvalue]+"\" "+(enumValues[enumvalue].equals(ruleOperation.getValue1())?"selected":"")+">"+enumValues[enumvalue]+"</option>");
                                      prefix2.append("<option value=\""+enumValues[enumvalue]+"\" "+(enumValues[enumvalue].equals(ruleOperation.getValue2())?"selected":"")+">"+enumValues[enumvalue]+"</option>");
                                    }
                                  }
                                  prefix1.append("</select>");
                                  prefix2.append("</select>");
                                  inputType="hidden";
                                }
                              %>

                              <td align="center" valign="middle" data-label="Value1"><%=prefix1.toString()%><input type="<%=inputType%>" class="form-control" name="businessProfileValue1_<%=(presentCount+1)%>_<%=innerCount%>" value="<%=functions.isValueNull(ruleOperation.getValue1())?ruleOperation.getValue1():""%>"  readonly style="background-color:#EBEBE4"/></td>
                              <td align="center" valign="middle" data-label="Value2"><%=prefix2.toString()%><input type="<%=inputType%>" class="form-control" name="businessProfileValue2_<%=(presentCount+1)%>_<%=innerCount%>" value="<%=functions.isValueNull(ruleOperation.getValue2())?ruleOperation.getValue2():""%>" readonly style="background-color:#EBEBE4"/></td>
                              <%
                                }
                              %>
                            </tr>
                            <%if(functions.isValueNull(ruleOperation.getComparator())){%>
                            <tr><td colspan="5" align="center"><%=ruleOperation.getComparator()%></td></tr>
                            <%}%>
                            <%
                                  innerCount++;
                                }
                              }
                            %>
                          </table>
                        </div>
                      </td>
                      <%
                        if(!actionVO.isView())
                        {
                      %>
                      <td valign="middle" align="center" data-label="Action"><button type="button" class="btn btn-default"  <%--style="width:60px"--%> onclick="delRow('<%=(presentCount+1)%>',event)"  name="delete_<%=(presentCount+1)%>" value="<%=(presentCount+1)%>" ><%=addBusinessProfile_Delete%></button></td>
                      <td valign="middle" align="center" data-label="Action"><button type="button" class="btn btn-default" <%--style="width:60px"--%> name="edit_<%=(presentCount+1)%>" onclick="editRow('<%=(presentCount+1)%>')" value="<%=(presentCount+1)%>" ><%=addBusinessProfile_Edit%></button></td>

                      <%
                        }
                      %>
                    </tr>


                    <%
                        addedrow.append("addedRow["+(presentCount+1)+"]=\"P\";");
                        currentRowStatus.append("currentRowStatus["+(presentCount+1)+"]=\"P\";");
                        selectedOption.append("selectedOption["+(presentCount+1)+"]=\""+businessIdAndIdMap.get(ruleVO.getId())+"\";");
                        presentCount++;
                      }
                    }
                    else if(validationFailed)
                    {
                      int i=0;
                      int countFinal=functions.isValueNull(request.getParameter("countOfRow"))?Integer.parseInt(request.getParameter("countOfRow")):0;

                      //System.out.println("Inside failed condition");
                      String no="";
                      if(request.getAttribute("no")!=null)
                      {
                        no=request.getAttribute("no").toString();
                      }

                      for(i=1;i<=countFinal;i++)
                      {
                        if(functions.isValueNull(request.getParameter("Text|businessProfileId_"+i)))
                        {
                          RuleVO ruleVO = new RuleVO();
                          if( businessRuleList!=null && businessRuleList.containsKey(request.getParameter("Text|businessProfileId_"+i)))
                          {
                            ruleVO=businessRuleList.get(request.getParameter("Text|businessProfileId_"+i));
                          }
                    %>


                    <tr >
                      <%--<TH class="th0">Check(please check below check box for profile to be added)</TH>--%>
                      <td valign="middle" align="center" data-label="Rule Name"><select name="businessProfileId_<%=(presentCount+1)%>" style="width:inherit;" class="form-control" onchange="selectedDropdown('businessProfileId_<%=(presentCount+1)%>')" <%=no.equals(String.valueOf(i))?"":"disabled"%>><%=profileManagementManager.getOptionTagForBusinessRule(businessRuleList, businessIdAndIdMap, request.getParameter("Text|businessProfileId_" + i))%></select><input type="hidden" name="Text|businessProfileId_<%=(presentCount+1)%>" value="<%=request.getParameter("Text|businessProfileId_"+i)%>"></td>
                      <td valign="middle" align="center" data-label="Description"><textarea type="text" name="businessProfileDesc_<%=(presentCount+1)%>" style="width:inherit;" class="form-control" value="<%=request.getParameter("businessProfileDesc_" + i)%>" readonly style="background-color:#EBEBE4;font-size: 12px;/*min-height:50px;max-height: 150px;max-width: 100px*/"><%=request.getParameter("businessProfileDesc_" + i)%></textarea></td>
                      <td valign="middle" align="center" data-label="IsApplicable"><input type="checkbox" name="businessProfileIsApplicable_<%=(presentCount+1)%>" id="businessProfileIsApplicable_<%=(presentCount+1)%>" <%=no.equals(String.valueOf(i))?"onclick=\\\"enable_text(this.checked,'"+i+"')\\\"":"onclick=\"return false\""%> <%="Y".equals(request.getParameter("businessProfileIsApplicable_" + i))?"checked":""%> value="Y" readonly/></td>


                      <td valign="middle" align="center" colspan="3">
                        <div class="tableScroll">
                          <table id="ruleOperation_<%=(presentCount+1)%>" width="100%" class="table table-striped table-bordered table-green dataTable" style="margin-bottom: 0px">

                            <tr id="ruleOperationTR_<%=(presentCount+1)%>"> <td colspan="5" align="center"><%=ruleVO.getRuleType()%></tr>
                            <thead>
                            <tr style="background-color: #7eccad !important; color: white; padding-top: 13px; padding-bottom: 13px; font-family: Open Sans; font-size: 13px; font-weight: 600;">

                              <td valign="middle" align="center"><%=addBusinessProfile_Output_Type%></td>
                              <td valign="middle" align="center"><%=addBusinessProfile_Input_Name%></td>
                              <td valign="middle" align="center" <%=RuleTypeEnum.REGULAR_EXPRESSION.name().equals(ruleVO.getRuleType())?"colspan=\"3\"":""%>><%=addBusinessProfile_Operator%></td>
                              <%
                                if(RuleTypeEnum.REGULAR_EXPRESSION.name().equals(ruleVO.getRuleType()))
                                {
                              %>

                              <%
                              }
                              else if(RuleTypeEnum.FLAT_FILE .name().equals(ruleVO.getRuleType()))
                              {
                              %>
                              <td valign="middle" align="center" colspan="2"><%=addBusinessProfile_File_Path%></td>
                              <%
                              }
                              else
                              {
                              %>
                              <td valign="middle" align="center" ><%=addBusinessProfile_Value1%></td>
                              <td valign="middle" align="center" ><%=addBusinessProfile_Value2%></td>
                              <%
                                }
                              %>

                            </tr>
                            </thead>
                            <%
                              if(ruleVO.getRuleOperation()!=null)
                              {
                                int innerCount=1;
                                for(RuleOperation ruleOperation:ruleVO.getRuleOperation())
                                {
                            %>
                            <tr>
                              <td align="center" valign="middle" data-label="Output Type"><textarea type="text" class="form-control" name="operationType_<%=(presentCount+1)%>_<%=innerCount%>" style='/*max-width:50px;max-height:100px*/' readonly><%=ruleOperation.getOperationType()%></textarea></td>
                              <td align="center" valign="middle" data-label="Input Name"><input type="text" class="form-control" name="ruleInputName_<%=(presentCount+1)%>_<%=innerCount%>" value="<%=ruleOperation.getInputName()%>" readonly></td>
                              <td align="center" valign="middle" data-label="Operator"><input type="text" style="width:200px;" class="form-control" name="ruleOperation_<%=(presentCount+1)%>_<%=innerCount%>" value="<%=ruleOperation.getOperator()%>" readonly></td>
                              <%
                                if(RuleTypeEnum.REGULAR_EXPRESSION.name().equals(ruleVO.getRuleType()))
                                {
                              %>

                              <%
                              }
                              else if(RuleTypeEnum.FLAT_FILE .name().equals(ruleVO.getRuleType()))
                              {
                              %>
                              <td align="center" valign="middle" data-label="File Path"><input type="text" class="form-control" name="businessProfileValue1_<%=(presentCount+1)%>_<%=innerCount%>" value="<%=functions.isValueNull(request.getParameter("businessProfileValue1_"+(presentCount+1)+"_"+innerCount))?request.getParameter("businessProfileValue1_"+(presentCount+1)+"_"+innerCount):""%>"  <%="Y".equals(request.getParameter("businessProfileIsApplicable_" + i)) && no.equals(String.valueOf(i))?"":"readonly style=\"background-color:#EBEBE4\""%>/></td>
                              <%
                              }
                              else
                              {
                                String inputType="text";
                                StringBuffer prefix1=new StringBuffer();
                                StringBuffer prefix2=new StringBuffer();
                                if(DataType.ENUM.name().equals(ruleOperation.getDataType()))
                                {
                                  prefix1.append("<select class=\"form-control\" name=\"selectBusinessProfileValue1_"+(presentCount+1)+ "_"+innerCount+"\" onchange=\"selectEnumValue1('"+(presentCount+1)+"','"+innerCount+"')\" "+("Y".equals(request.getParameter("businessProfileIsApplicable_" + i)) && no.equals(String.valueOf(i))?"":"disabled  style=\"background-color:#EBEBE4\"")+">");
                                  prefix2.append("<select class=\"form-control\" name=\"selectBusinessProfileValue2_"+(presentCount+1)+ "_"+innerCount+"\" onchange=\"selectEnumValue2('"+(presentCount+1)+"','"+innerCount+"')\" "+("Y".equals(request.getParameter("businessProfileIsApplicable_" + i)) && no.equals(String.valueOf(i)) && (PZOperatorEnums.BETWEEN.name().equals(ruleOperation.getOperator()))?"":"disabled  style=\"background-color:#EBEBE4\"")+">");

                                  prefix1.append("<option value=\"\">Select Value</option>");
                                  prefix2.append("<option value=\"\">Select Value</option>");

                                  if(functions.isValueNull(ruleOperation.getEnumValue()))
                                  {
                                    String enumValues[]=ruleOperation.getEnumValue().split(",");
                                    for (int enumvalue=0;enumvalue<enumValues.length;enumvalue++)
                                    {
                                      prefix1.append("<option value=\""+enumValues[enumvalue]+"\" "+(enumValues[enumvalue].equals(request.getParameter("businessProfileValue1_" + (presentCount + 1) + "_" + innerCount))?"selected":"")+">"+enumValues[enumvalue]+"</option>");
                                      prefix2.append("<option value=\""+enumValues[enumvalue]+"\" "+(enumValues[enumvalue].equals(request.getParameter("businessProfileValue2_" + (presentCount + 1) + "_" + innerCount))?"selected":"")+">"+enumValues[enumvalue]+"</option>");
                                    }
                                  }
                                  prefix1.append("</select>");
                                  prefix2.append("</select>");
                                  inputType="hidden";
                                }
                              %>
                              <td align="center" valign="middle" data-label="Value1"><%=prefix1%><input type="<%=inputType%>" class="form-control" name="businessProfileValue1_<%=(presentCount+1)%>_<%=innerCount%>" value="<%=functions.isValueNull(request.getParameter("businessProfileValue1_"+(presentCount+1)+"_"+innerCount))?request.getParameter("businessProfileValue1_"+(presentCount+1)+"_"+innerCount):""%>"  <%="Y".equals(request.getParameter("businessProfileIsApplicable_" + i)) && no.equals(String.valueOf(i))?"":"readonly style=\"background-color:#EBEBE4\""%>/></td>
                              <td align="center" valign="middle" data-label="Value2"><%=prefix2%><input type="<%=inputType%>" class="form-control" name="businessProfileValue2_<%=(presentCount+1)%>_<%=innerCount%>" value="<%=functions.isValueNull(request.getParameter("businessProfileValue2_"+(presentCount+1)+"_"+innerCount))?request.getParameter("businessProfileValue2_"+(presentCount+1)+"_"+innerCount):""%>" <%="Y".equals(request.getParameter("businessProfileIsApplicable_" + i)) && no.equals(String.valueOf(i)) && (PZOperatorEnums.BETWEEN.name().equals(ruleOperation.getOperator()))?"":"readonly style=\"background-color:#EBEBE4\""%>/></td>
                              <%
                                }
                              %>
                            </tr>
                            <%if(functions.isValueNull(ruleOperation.getComparator())){%>
                            <tr><td colspan="5" align="center"><%=ruleOperation.getComparator()%></td></tr>
                            <%}%>
                            <%
                                  innerCount++;
                                }
                              }
                            %>
                          </table>
                        </div>
                      </td>


                      <%
                        if(!actionVO.isView())
                        {
                      %>
                      <td align="center" valign="middle" data-label="Action" class="tr0"><button type="button" class="btn btn-default"<%-- style="width: 60px"--%> onclick="delRow('<%=(presentCount+1)%>',event)"  name="delete_<%=(presentCount+1)%>" value="<%=(presentCount+1)%>" ><%=addBusinessProfile_Delete%></button></td>
                      <td align="center" valign="middle" data-label="Action" class="tr0"><button type="button" class="btn btn-default"<%-- style="width: 60px"--%> name="edit_<%=(presentCount+1)%>" onclick="editRow('<%=(presentCount+1)%>')" value="<%=(presentCount+1)%>" ><%=addBusinessProfile_Edit%></button></td>

                      <%
                        }
                      %>
                    </tr>


                    <%
                            addedrow.append("addedRow["+(presentCount+1)+"]=\"P\";");
                            currentRowStatus.append("currentRowStatus["+(presentCount+1)+"]=\"P\";");
                            selectedOption.append("selectedOption["+(presentCount+1)+"]=\""+businessIdAndIdMap.get(request.getParameter("Text|businessProfileId_"+i))+"\";");
                            presentCount++;
                          }
                        }
                        profileId=request.getParameter("action").split("_")[0];
                      }
                    %>
                    <input type="hidden" name="countOfRow" value="<%=presentCount%>"/>
                  </table>




                </div>

                <div class="form-group col-md-12 has-feedback" style="margin-top: 20px;">
                  <center>
                    <label >&nbsp;</label>
                    <%if(actionVO.isAdd()){%>
                    <button type="submit" class="btn btn-default" name="action" value="1_Add"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=addBusinessProfile_Add%></button>
                    <button type="submit" class="btn btn-default" name="profileid" value="<%=functions.isValueNull(request.getParameter("profileid"))?request.getParameter("profileid"):""%>" onclick="cancel('<%=ctoken%>')"><i class="fa fa-times"></i>&nbsp;&nbsp;<%=addBusinessProfile_Cancel%></button>
                    <%
                      }
                    %>
                    <%if(actionVO.isEdit())
                    {
                    %>
                    <button type="submit" class="btn btn-default" name="action" value="<%=profileId%>_Edit"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=addBusinessProfile_Update%></button>
                    <button type="submit" class="btn btn-default" name="profileid" value="<%=functions.isValueNull(request.getParameter("profileid"))?request.getParameter("profileid"):""%>" onclick="cancel('<%=ctoken%>')"><i class="fa fa-times"></i>&nbsp;&nbsp;<%=addBusinessProfile_Cancel%></button>
                    <%
                      }
                    %>
                    <%if(actionVO.isView())
                    {
                    %>
                    <button type="submit" class="btn btn-default" name="profileid" value="<%=functions.isValueNull(request.getParameter("profileid"))?request.getParameter("profileid"):""%>" onclick="cancel('<%=ctoken%>')"><i class="fa fa-times"></i>&nbsp;&nbsp;<%=addBusinessProfile_Cancel%></button>
                    <%
                      }
                    %>

                  </center>
                </div>

              </div>



            </div>
          </div>

        </form>

      </div>

    </div>

  </div>
</div>

<script type="text/javascript">

  <%=option%>;
  <%=optionStatus%>;
  <%=optionValue%>;
  <%=selectedOption%>
  <%=addedrow%>
  <%=currentRowStatus%>

  var count = "1";

  <%=(presentCount==0)?"":"count="+presentCount%>
  function selectEnumValue1(name,count)
  {
    var hat1 = this.document.getElementsByName("selectBusinessProfileValue1_"+name+"_"+count)[0].selectedIndex;
    this.document.getElementsByName("businessProfileValue1_"+name+"_"+count)[0].value = this.document.getElementsByName("selectBusinessProfileValue1_"+name+"_"+count)[0].options[hat1].value;
  }
  function selectEnumValue2(name,count)
  {
    var hat1 = this.document.getElementsByName("selectBusinessProfileValue2_"+name+"_"+count)[0].selectedIndex;
    this.document.getElementsByName("businessProfileValue2_"+name+"_"+count)[0].value = this.document.getElementsByName("selectBusinessProfileValue2_"+name+"_"+count)[0].options[hat1].value;
  }
  function selectedDropdown(name)
  {
    var hat1 = this.document.getElementsByName(name)[0].selectedIndex ;
    var hatto1 = this.document.getElementsByName(name)[0].options[hat1].getAttribute("id");

    selectedOption[name.split("_")[1]]=hatto1;
    this.document.getElementsByName('businessProfileDesc_'+name.split("_")[1])[0].value=optionValue[option.indexOf(hatto1)].split("|")[1];
    // this.document.getElementsByName('businessProfileRuleType_'+name.split("_")[1])[0].value=optionValue[option.indexOf(hatto1)].split("|")[2];


    this.document.getElementsByName('Text|'+name)[0].value=optionValue[option.indexOf(hatto1)].split("|")[0];

    //This is for value 2
    var isapplicable = "businessProfileIsApplicable_" +name.split("_")[1];
    var value1 = "businessProfileValue1_" + name.split("_")[1];
    var value2 = "businessProfileValue2_" + name.split("_")[1];

    var subBody = document.getElementById("ruleOperation_"+name.split("_")[1]).getElementsByTagName("TBODY")[0];
    var row = document.createElement("tr");
    var rowH = document.createElement("tr");
    var lastRow = subBody.rows.length;
    for(var i=lastRow-1;i>0;i--)
    {
      subBody.deleteRow(i);
    }
    lastRow = subBody.rows.length;
    var iteration=1;

    document.getElementById("ruleOperationTR_"+name.split("_")[1]).innerHTML="<td colspan=\"5\" align=\"center\">"+optionValue[option.indexOf(hatto1)].split("|")[2]+"</td>"
    var row = subBody.insertRow(lastRow);
    var cellLeft0 = row.insertCell(0);
    var textNode0 = "Operation&nbsp;Type";
    cellLeft0.innerHTML=textNode0.replace(/!count!/g, iteration);
    cellLeft0.setAttribute("class","th0");
    cellLeft0.setAttribute("valign","middle");
    cellLeft0.setAttribute("align","center");

    var cellLeft = row.insertCell(1);
    var textNode = "Input&nbsp;Name";
    cellLeft.innerHTML=textNode.replace(/!count!/g, iteration);
    cellLeft.setAttribute("class","th0");
    cellLeft.setAttribute("valign","middle");
    cellLeft.setAttribute("align","center");

    var cellLeft2 = row.insertCell(2);
    var textNode2 = "Operator";
    cellLeft2.innerHTML=textNode2.replace(/!count!/g, iteration);
    cellLeft2.setAttribute("class","th0");
    cellLeft2.setAttribute("valign","middle");
    cellLeft2.setAttribute("align","center");

    if(optionValue[option.indexOf(hatto1)].split("|")[2]=="<%=RuleTypeEnum.FLAT_FILE.name()%>")
    {
      var cellLeft3 = row.insertCell(3);
      var textNode3 = "File Path";
      cellLeft3.innerHTML=textNode3.replace(/!count!/g, iteration);
      cellLeft3.setAttribute("class","th0");
      cellLeft3.setAttribute("valign","middle");
      cellLeft3.setAttribute("align","center");
      cellLeft3.setAttribute("colspan","2");
    }
    else if(optionValue[option.indexOf(hatto1)].split("|")[2]=="<%=RuleTypeEnum.REGULAR_EXPRESSION.name()%>")
    {
      cellLeft2.setAttribute("colspan","3");
    }
    else
    {
      var cellLeft3 = row.insertCell(3);
      var textNode3 = "Value1";
      cellLeft3.innerHTML = textNode3.replace(/!count!/g, iteration);
      cellLeft3.setAttribute("class", "th0");
      cellLeft3.setAttribute("valign", "middle");
      cellLeft3.setAttribute("align", "center");

      var cellLeft4 = row.insertCell(4);
      var textNode4 = "Value2";
      cellLeft4.innerHTML = textNode4.replace(/!count!/g, iteration);
      cellLeft4.setAttribute("class", "th0");
      cellLeft4.setAttribute("valign", "middle");
      cellLeft4.setAttribute("align", "center");
    }
    lastRow++;

    var value2Disable=""
    var value1Disable=""
    var value2Disabled=""
    var value1Disabled=""
    if(document.getElementById(isapplicable).checked != true)
    {
      value1Disable="readonly style=\"background-color:#EBEBE4\"";
      value2Disable="readonly style=\"background-color:#EBEBE4\"";
      value1Disabled="disabled style=\"background-color:#EBEBE4\"";
      value2Disabled="disabled style=\"background-color:#EBEBE4\"";
    }
    for(var i=0; i<optionValue[option.indexOf(hatto1)].split("|")[3].split(",").length;i++)
    {
      var singleOperation=optionValue[option.indexOf(hatto1)].split("|")[3].split(",")[i];
      var innerRow = subBody.insertRow(lastRow);

      var cellLeftInner10 = innerRow.insertCell(0);
      var textNodeInner10 = "<textarea type=\"text\" class=\"form-control\" name=\"operationType_"+name.split("_")[1]+"_!count!\" value=\""+singleOperation.split(":")[6]+"\" readonly <%--style='max-width:50px;max-height:100px'--%>>"+singleOperation.split(":")[6]+"</textarea>";
      cellLeftInner10.innerHTML=textNodeInner10.replace(/!count!/g, iteration);
      cellLeftInner10.setAttribute("class","tr0") ;
      cellLeftInner10.setAttribute("align","center");
      cellLeftInner10.setAttribute("valign","middle");

      var cellLeftInner1 = innerRow.insertCell(1);
      var textNodeInner1 = "<input type=\"text\" class=\"form-control\" name=\"ruleInputName_"+name.split("_")[1]+"_!count!\" value=\""+singleOperation.split(":")[2]+"\" readonly>";
      cellLeftInner1.innerHTML=textNodeInner1.replace(/!count!/g, iteration);
      cellLeftInner1.setAttribute("class","tr0") ;
      cellLeftInner1.setAttribute("align","center");
      cellLeftInner1.setAttribute("valign","middle");

      var cellLeftInner2 = innerRow.insertCell(2);
      var textNodeInner2 = "<input type=\"text\" class=\"form-control\" style=\"width:200px;\" name=\"ruleOperation_"+name.split("_")[1]+"_!count!\" value=\""+singleOperation.split(":")[1]+"\" readonly>";
      cellLeftInner2.innerHTML=textNodeInner2.replace(/!count!/g, iteration);
      cellLeftInner2.setAttribute("class","tr0") ;
      cellLeftInner2.setAttribute("align","center");
      cellLeftInner2.setAttribute("valign","middle");

      if(singleOperation.split(":")[1]!="<%=PZOperatorEnums.BETWEEN.name()%>")
      {
        value2Disable="readonly style=\"background-color:#EBEBE4\"";
        value2Disabled="disabled style=\"background-color:#EBEBE4\"";
      }

      if(optionValue[option.indexOf(hatto1)].split("|")[2]=="<%=RuleTypeEnum.FLAT_FILE.name()%>")
      {
        var cellLeftInner3 = innerRow.insertCell(3);
        var textNodeInner3 = "<input type=\"text\" class=\"form-control\" name=\"businessProfileValue1_" + name.split("_")[1] + "_!count!\" value=\"\" "+value1Disable+">";
        cellLeftInner3.innerHTML = textNodeInner3.replace(/!count!/g, iteration);
        cellLeftInner3.setAttribute("class", "tr0");
        cellLeftInner3.setAttribute("align", "center");
        cellLeftInner3.setAttribute("valign", "middle");
        cellLeftInner3.setAttribute("colspan", "2");
      }
      else if(optionValue[option.indexOf(hatto1)].split("|")[2]=="<%=RuleTypeEnum.REGULAR_EXPRESSION.name()%>")
      {
        cellLeftInner2.setAttribute("colspan","3");
      }
      else
      {
        var cellLeftInner3 = innerRow.insertCell(3);
        var inputType="text";
        var prefix1="";
        var prefix2="";
        if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
        {
          prefix1+="<select class=\"form-control\" name=\"selectBusinessProfileValue1_"+ name.split("_")[1] + "_!count!\" onchange=\"selectEnumValue1('"+name.split("_")[1]+"','!count!')\" "+value1Disabled+">";
          prefix1+="<option value=\"\" >Select Value</option>";
          var enumValues=singleOperation.split(":")[5];
          for(var enumValue=0;enumValue<enumValues.split("!").length;enumValue ++)
          {
            prefix1+="<option value=\""+enumValues.split("!")[enumValue]+"\" >"+enumValues.split("!")[enumValue]+"</option>";
          }
          prefix1+="</select>";
          prefix2+="<select class=\"form-control\" name=\"selectBusinessProfileValue2_"+ name.split("_")[1] + "_!count!\" onchange=\"selectEnumValue2('"+name.split("_")[1]+"','!count!')\" "+value2Disabled+">";
          prefix2+="<option value=\"\" >Select Value</option>";
          var enumValues=singleOperation.split(":")[5];
          for(var enumValue=0;enumValue<enumValues.split("!").length;enumValue ++)
          {
            prefix2+="<option value=\""+enumValues.split("!")[enumValue]+"\" >"+enumValues.split("!")[enumValue]+"</option>";
          }
          prefix2+="</select>"
          inputType="hidden";
        }
        var textNodeInner3 = ""+prefix1+"<input type=\""+inputType+"\" class=\"form-control\" name=\"businessProfileValue1_" + name.split("_")[1] + "_!count!\" value=\"\" "+value1Disable+">";
        cellLeftInner3.innerHTML = textNodeInner3.replace(/!count!/g, iteration);
        cellLeftInner3.setAttribute("class", "tr0");
        cellLeftInner3.setAttribute("align", "center");
        cellLeftInner3.setAttribute("valign", "middle");

        var cellLeftInner4 = innerRow.insertCell(4);
        var textNodeInner4 = ""+prefix2+"<input type=\""+inputType+"\" class=\"form-control\" name=\"businessProfileValue2_" + name.split("_")[1] + "_!count!\" value=\"\" "+value2Disable+">";
        cellLeftInner4.innerHTML = textNodeInner4.replace(/!count!/g, iteration);
        cellLeftInner4.setAttribute("class", "tr0");
        cellLeftInner4.setAttribute("align", "center");
        cellLeftInner4.setAttribute("valign", "middle");
      }
      if(singleOperation.split(":")[3]!="")
      {
        var innerRow2 = subBody.insertRow(++lastRow);
        var cellLeftInner21 = innerRow2.insertCell(0);
        var textNodeInner21 = "" + singleOperation.split(":")[3] + "";
        cellLeftInner21.innerHTML = textNodeInner21.replace(/!count!/g, iteration);
        cellLeftInner21.setAttribute("class", "tr0");
        cellLeftInner21.setAttribute("align", "center");
        cellLeftInner21.setAttribute("valign", "middle");
        cellLeftInner21.setAttribute("colspan", "5");
      }
      iteration++;
      lastRow++;
    }
  }
  function addRow(in_tbl_name)
  {
    var i=0;
    var validationPass=[];
    var validationFail=false;
    var minimalCount=false;
    //alert("Inside Add Row");
    for(i=1;i<addedRow.length;i++)
    {
      //alert("Main Iteration:::"+i);
      if(currentRowStatus[i]=="P")
      {
        //alert("Inside P");
        var previous = i;
        var profileId = "businessProfileId_" + previous;
        var isApplicable = "businessProfileIsApplicable_" + previous;

        if($('select[name='+profileId+'] option:selected').val()!="")
        {
          for (var inner = 0; inner <optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",").length; inner++)
          {
            var singleOperation = optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",")[inner];
            var value1 = "businessProfileValue1_" + previous + "_" + (Number(inner) + 1);
            var value2 = "businessProfileValue2_" + previous + "_" + (Number(inner) + 1);

            if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.FLAT_FILE.name()%>")
            {
              if ((document.getElementsByName(value1) != null ) && ( (document.getElementById(isApplicable).checked == true && document.getElementsByName(value1)[0].value == "" )))
              {
                validationPass[i] = false;
                break;
              }
              else
              {
                validationPass[i]=true;
              }
            }
            else if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.REGULAR_EXPRESSION.name()%>")
            {
              validationPass[i]=true;
            }
            else
            {
              if ((document.getElementsByName(value1) != null || document.getElementsByName(value2) != null) && ( (document.getElementById(isApplicable).checked == true && document.getElementsByName(value1)[0].value == "" || ( $('select[name=' + profileId + '] option:selected').val() != "" && singleOperation.split(":")[1] == "BETWEEN" && document.getElementsByName(value2)[0].value == "" && document.getElementById(isApplicable).checked == true )) || $('select[name=' + profileId + '] option:selected').val() == ""))
              {
                validationPass[i] = false;
                break;
              }
              else
              {
                validationPass[i] = true;
              }
            }
          }
        }
        else
        {
          validationPass[i]=false;
        }

        if(option.indexOf(selectedOption[i])>=0)
        {
          var index=option.indexOf(selectedOption[i]);
          optionStatus[index]="Y";
        }
      }
      else if(minimalCount==false || currentRowStatus[i]=="D")
      {
        if (minimalCount == false)
        {
          count = i;
          minimalCount = true;
        }
        if(selectedOption.indexOf(selectedOption[i])==selectedOption.lastIndexOf(selectedOption[i]))
        {
          var index = option.indexOf(selectedOption[i]);
          optionStatus[index] = "N";
          selectedOption[i] = "";
        }
      }
    }
    //For count if present or not if present then count ofd or last+1
    if(currentRowStatus[count]=="P")
    {
      if(currentRowStatus.indexOf("D")>=0)
      {
        count=currentRowStatus.indexOf("D");
      }
      else if(currentRowStatus.indexOf("D")==-1)
      {
        count=parseInt(currentRowStatus.lastIndexOf("P"))+1;
      }
    }
    var tbody = document.getElementById(in_tbl_name).getElementsByTagName("TBODY")[0];
    // create row
    var row = document.createElement("tr");
    row.setAttribute("id",count);
    // create table cell 1
    var td1 = document.createElement("td");
    var strHtml1 = "<center><select name=\"businessProfileId_!count!\" style=\"width:inherit;\" class=\"form-control\" onchange=\"selectedDropdown('businessProfileId_!count!')\"><option value=\"\">Select One Rule</option>" +
            "<%=businessRule%>" +
            "</select><input type=\"hidden\" name=\"Text|businessProfileId_!count!\" value=\"\"></center>";
    td1.innerHTML = strHtml1.replace(/!count!/g, count);

    var td2 = document.createElement("td");
    var strHtml2 = "<textarea type=\"text\" name=\"businessProfileDesc_!count!\" style=\"width:inherit;\" class=\"form-control\"  style=\"background-color:#EBEBE4;font-size: 12px;<%--min-height:50px;max-height: 150px;max-width: 100px--%>\" readonly/>";
    td2.innerHTML = strHtml2.replace(/!count!/g, count);

    var td3 = document.createElement("td");
    var strHtml3 = "<input type=\"checkbox\" name=\"businessProfileIsApplicable_!count!\" align=\"center\" id=\"businessProfileIsApplicable_!count!\" onclick=\"enable_text(this.checked,'!count!')\"  value=\"Y\"/>";
    td3.innerHTML = strHtml3.replace(/!count!/g, count);

    var td9 = document.createElement("td");
    /*var strHtml9 = "<input type=\"text\" name=\"businessProfileOperator_!count!\" class=\"txtbox\"  style=\"background-color:#EBEBE4\" readonly/>";*/
    var strHtml9 = "<div class=\"tableScroll\"><table id=\"ruleOperation_!count!\" width=\"100%\" class=\"table table-striped table-bordered table-green dataTable tableScroll\" style=\"margin-bottom: 0px\"><tr id=\"ruleOperationTR_!count!\" colspan=\"3\"></tr></table></div>";

    td9.innerHTML = strHtml9.replace(/!count!/g, count);

    var td7 = document.createElement("td");
    var strHtml7 = "<button type=\"button\" class=\"btn btn-default\" width=\"\" onclick=\"delRow('!count!',event)\"  name=\"delete_!count!\" value=\"!count!\" ><!--style=\"width:60px\"-->Delete</button>";
    td7.innerHTML = strHtml7.replace(/!count!/g, count);

    var td8 = document.createElement("td");
    var strHtml8 = "<button type=\"button\" class=\"btn btn-default\"  name=\"edit_!count!\" onclick=\"editRow('!count!')\" value=\"!count!\" ><!--style=\"width:60px\"-->Edit</button>";
    td8.innerHTML = strHtml8.replace(/!count!/g, count);

    td1.setAttribute("valign","middle");
    td1.setAttribute("align","center");
    td2.setAttribute("valign","middle");
    td2.setAttribute("align","center");
    td3.setAttribute("valign","middle");
    td3.setAttribute("align","center");

    td7.setAttribute("valign","middle");
    td7.setAttribute("align","center");
    td8.setAttribute("valign","middle");
    td8.setAttribute("align","center");
    td9.setAttribute("valign","middle");
    td9.setAttribute("align","center");
    td9.setAttribute("colspan","3");

    // append data to row
    row.appendChild(td1);
    row.appendChild(td2);
    row.appendChild(td3);
    row.appendChild(td9);
    row.appendChild(td7);
    row.appendChild(td8);

    //This is for removal of dropdown to make distinct select
    var j=0;
    for(j=1;j<validationPass.length;j++)
    {
      //alert("Inside After Check Main Iteration:::"+j);
      if(validationPass[j]==false)
      {
        /*//alert("Inside After Check Failed:::"+j);*/
        var previous = j;
        var profileId = "businessProfileId_" + previous;
        var isapplicable = "businessProfileIsApplicable_" + previous;

        document.getElementsByName(profileId)[0].disabled = false
        $(document.getElementsByName(profileId)[0]).css('background-color','#ffffff');
        document.getElementById(isapplicable).setAttribute("onclick","enable_text(this.checked,"+j+")");

        if($('select[name='+profileId+'] option:selected').val()!="")
        {
          for (var inner = 0; inner < optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",").length; inner++)
          {
            //alert("Inside After Check Sub Disable:::"+inner);
            var singleOperation = optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",")[inner];
            var value1 = "businessProfileValue1_" + previous + "_" + (Number(inner) + 1);
            var value2 = "businessProfileValue2_" + previous + "_" + (Number(inner) + 1);
            var selectvalue1 = "selectBusinessProfileValue1_" + previous + "_" + (Number(inner) + 1);
            var selectvalue2 = "selectBusinessProfileValue2_" + previous + "_" + (Number(inner) + 1);

            if (document.getElementById(isapplicable).checked == true)
            {
              if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.FLAT_FILE.name()%>")
              {
                document.getElementsByName(value1)[0].readOnly = false
                $(document.getElementsByName(value1)[0]).css('background-color', '#ffffff');
              }
              else if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.REGULAR_EXPRESSION.name()%>")
              {

              }
              else
              {
                if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
                {
                  document.getElementsByName(selectvalue1)[0].disabled = false;
                  $(document.getElementsByName(selectvalue1)[0]).css('background-color', '#ffffff');
                }
                document.getElementsByName(value1)[0].readOnly = false;
                $(document.getElementsByName(value1)[0]).css('background-color', '#ffffff');
                if (singleOperation.split(":")[1] == "BETWEEN")
                {
                  if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
                  {
                    document.getElementsByName(selectvalue2)[0].disabled = false;
                    $(document.getElementsByName(selectvalue2)[0]).css('background-color', '#ffffff');
                  }
                  document.getElementsByName(value2)[0].readOnly = false;
                  $(document.getElementsByName(value2)[0]).css('background-color', '#ffffff');
                }
                else
                {
                  if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
                  {
                    document.getElementsByName(selectvalue2)[0].disabled = true;
                    $(document.getElementsByName(selectvalue2)[0]).css('background-color', '#EBEBE4');
                  }
                  document.getElementsByName(value2)[0].readOnly = true;
                  $(document.getElementsByName(value2)[0]).css('background-color', '#EBEBE4');
                }
              }
            }
            else
            {
              if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.FLAT_FILE.name()%>")
              {
                document.getElementsByName(value1)[0].readOnly = true
                $(document.getElementsByName(value1)[0]).css('background-color', '#EBEBE4');
              }
              else if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.REGULAR_EXPRESSION.name()%>")
              {

              }
              else
              {
                if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
                {
                  document.getElementsByName(selectvalue1)[0].disabled = true;
                  $(document.getElementsByName(selectvalue1)[0]).css('background-color', '#EBEBE4');
                  document.getElementsByName(selectvalue2)[0].disabled = true;
                  $(document.getElementsByName(selectvalue2)[0]).css('background-color', '#EBEBE4');
                }
                document.getElementsByName(value1)[0].readOnly = true;
                $(document.getElementsByName(value1)[0]).css('background-color', '#EBEBE4');

                document.getElementsByName(value2)[0].readOnly = true;
                $(document.getElementsByName(value2)[0]).css('background-color', '#EBEBE4');
              }
            }
          }
        }
        validationFail=true;
        break;
      }
      else if(validationPass[j]=="" || validationPass[j]==null)
      {

      }
      else
      {
        var previous = j;
        var profileId = "businessProfileId_" + previous;
        var isapplicable = "businessProfileIsApplicable_" +previous;

        document.getElementsByName(profileId)[0].disabled = true;
        $(document.getElementsByName(profileId)[0]).css('background-color','#EBEBE4');
        document.getElementById(isapplicable).setAttribute("onclick","return false");

        for (var inner = 0; inner < optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",").length; inner++)
        {
          //alert("Inside After Check Main Disable:::"+inner);
          var singleOperation = optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",")[inner];
          var value1 = "businessProfileValue1_" + previous + "_" + (Number(inner) + 1);
          var value2 = "businessProfileValue2_" + previous + "_" + (Number(inner) + 1);
          var selectvalue1 = "selectBusinessProfileValue1_" + previous + "_" + (Number(inner) + 1);
          var selectvalue2 = "selectBusinessProfileValue2_" + previous + "_" + (Number(inner) + 1);

          if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.FLAT_FILE.name()%>")
          {
            document.getElementsByName(value1)[0].readOnly = true
            $(document.getElementsByName(value1)[0]).css('background-color', '#EBEBE4');
          }
          else if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.REGULAR_EXPRESSION.name()%>")
          {

          }
          else
          {
            if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
            {
              document.getElementsByName(selectvalue1)[0].disabled = true;
              $(document.getElementsByName(selectvalue1)[0]).css('background-color', '#EBEBE4');
              document.getElementsByName(selectvalue2)[0].disabled = true;
              $(document.getElementsByName(selectvalue2)[0]).css('background-color', '#EBEBE4');
            }
            document.getElementsByName(value1)[0].readOnly = true;
            $(document.getElementsByName(value1)[0]).css('background-color', '#EBEBE4');

            document.getElementsByName(value2)[0].readOnly = true;
            $(document.getElementsByName(value2)[0]).css('background-color', '#EBEBE4');
          }

            document.getElementsByName(value2)[0].readOnly = true;
            $(document.getElementsByName(value2)[0]).css('background-color', '#EBEBE4');
          /*}*/
        }
      }
    }

    var k=0;
    for(k=1;k<optionStatus.length;k++)
    {
      if(optionStatus[k]=="Y")
      {
        row.getElementsByTagName("option")[k].style.display="none";//TODO temporary
      }
    }
    currentRowFlag=count;
    // append row to table
    if(validationFail==true )
    {
      alert("please fill the previous data");
    }
    else if(optionStatus.indexOf("N")==-1)
    {
      alert("All the rule has been configured");
    }
    else
    {
      tbody.appendChild(row);
      addedRow[count]="Y";
      currentRowStatus[count]="P"
      count = parseInt(count) + 1;
    }
  }
  function delRow(element,e)
  {
    var current =  e.target;
    //here we will delete the line
    while ((current = current.parentElement) && current.tagName != "TR");
    current.parentElement.removeChild(current);

    currentRowStatus[element]="D";

    var profileId = "businessProfileId_" + currentRowFlag;
    var l=0;
    for(l=1;l<optionStatus.length;l++)
    {
      if(optionStatus[l]=="Y" )
      {
        if (option.indexOf(selectedOption[element]) == l)
        {
          (this.document.getElementsByName(profileId)[0].options[l]).style.display = "block";
        }
      }
    }
  }
  function setCountOfRows(element)
  {
    this.document.getElementsByName("countOfRow")[0].value=count;
  }
  function editRow(element)   //updated
  {
    var validationFail=false;
    var i=0;
    for (i = 1; i < addedRow.length; i++)
    {
      if (currentRowStatus[i] == "P")
      {
        var previous = i;
        var profileId = "businessProfileId_" + previous;
        var fileType = "fileType_" + previous;
        var isApplicable = "businessProfileIsApplicable_" + previous;

        if($('select[name='+profileId+'] option:selected').val()!="")
        {
          for (var inner = 0; inner <optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",").length; inner++)
          {
            //alert("Validation Check Iteration::::"+inner);
            var singleOperation = optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",")[inner];
            var value1 = "businessProfileValue1_" + previous + "_" + (Number(inner) + 1);
            var value2 = "businessProfileValue2_" + previous + "_" + (Number(inner) + 1);
            var selectvalue1 = "selectBusinessProfileValue1_" + previous + "_" + (Number(inner) + 1);
            var selectvalue2 = "selectBusinessProfileValue2_" + previous + "_" + (Number(inner) + 1);

            if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.FLAT_FILE.name()%>")
            {
              //alert("Validation Check Is File Type");
              if ((document.getElementsByName(value1) != null ) && ( (document.getElementById(isApplicable).checked == true && document.getElementsByName(value1)[0].value == "" )))
              {
                currentRowFlag = i;
                validationFail = true;
                break;
              }
              else
              {
                validationFail = false;
              }
            }
            else if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.REGULAR_EXPRESSION.name()%>")
            {
              validationFail = false;
            }
            else
            {
              //alert("Validation Check Is Data Or Comparator");
              if ((document.getElementsByName(value1) != null || document.getElementsByName(value2) != null) && ( (document.getElementById(isApplicable).checked == true && document.getElementsByName(value1)[0].value == "" || ( $('select[name=' + profileId + '] option:selected').val() != "" && singleOperation.split(":")[1] == "BETWEEN" && document.getElementsByName(value2)[0].value == "" && document.getElementById(isApplicable).checked == true )) || $('select[name=' + profileId + '] option:selected').val() == ""))
              {
                currentRowFlag = i;
                validationFail = true;
                break;
              }
              else
              {
                validationFail = false;
              }
            }
          }
        }
        else
        {
          currentRowFlag = i;
          validationFail = true;
          break;
        }

        if(validationFail==false)
        {
          document.getElementsByName(profileId)[0].disabled = true;
          $(document.getElementsByName(profileId)[0]).css('background-color', '#EBEBE4');
          document.getElementById(isApplicable).setAttribute("onclick", "return false");
          if ($('select[name='+profileId+'] option:selected').val()!="")
          {
            for (var inner = 0; inner < optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",").length; inner++)
            {
              //alert("Inside After Check Sub Disable:::"+inner);
              var singleOperation = optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",")[inner];
              var value1 = "businessProfileValue1_" + previous + "_" + (Number(inner) + 1);
              var value2 = "businessProfileValue2_" + previous + "_" + (Number(inner) + 1);
              var selectvalue1 = "selectBusinessProfileValue1_" + previous + "_" + (Number(inner) + 1);
              var selectvalue2 = "selectBusinessProfileValue2_" + previous + "_" + (Number(inner) + 1);

              if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.FLAT_FILE.name()%>")
              {
                document.getElementsByName(value1)[0].readOnly = true
                $(document.getElementsByName(value1)[0]).css('background-color', '#EBEBE4');
              }
              else if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.REGULAR_EXPRESSION.name()%>")
              {

              }
              else
              {
                if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
                {
                  document.getElementsByName(selectvalue1)[0].disabled = true;
                  $(document.getElementsByName(selectvalue1)[0]).css('background-color', '#EBEBE4');
                }
                document.getElementsByName(value1)[0].readOnly = true;
                $(document.getElementsByName(value1)[0]).css('background-color', '#EBEBE4');
                if (singleOperation.split(":")[1] == "BETWEEN")
                {
                  document.getElementsByName(value2)[0].readOnly = true;
                  $(document.getElementsByName(value2)[0]).css('background-color', '#EBEBE4');
                  if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
                  {
                    document.getElementsByName(selectvalue2)[0].disabled = true;
                    $(document.getElementsByName(selectvalue2)[0]).css('background-color', '#EBEBE4');
                  }
                }
                else
                {
                  if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
                  {
                    document.getElementsByName(selectvalue2)[0].disabled = true;
                    $(document.getElementsByName(selectvalue2)[0]).css('background-color', '#EBEBE4');
                  }
                  document.getElementsByName(value2)[0].readOnly = true;
                  $(document.getElementsByName(value2)[0]).css('background-color', '#EBEBE4');
                }
              }

            }
          }
        }
        else
        {
          currentRowFlag = i;
          validationFail = true;

          document.getElementsByName(profileId)[0].disabled = false;
          $(document.getElementsByName(profileId)[0]).css('background-color', '#ffffff');
          document.getElementById(isApplicable).setAttribute("onclick", "enable_text(this.checked,"+previous+")");
          if ($('select[name='+profileId+'] option:selected').val()!="")
          {
            for (var inner = 0; inner < optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",").length; inner++)
            {
              //alert("Inside After Check Sub Disable:::"+inner);
              var singleOperation = optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",")[inner];
              var value1 = "businessProfileValue1_" + previous + "_" + (Number(inner) + 1);
              var value2 = "businessProfileValue2_" + previous + "_" + (Number(inner) + 1);
              var selectvalue1 = "selectBusinessProfileValue1_" + previous + "_" + (Number(inner) + 1);
              var selectvalue2 = "selectBusinessProfileValue2_" + previous + "_" + (Number(inner) + 1);

              if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.FLAT_FILE.name()%>")
              {
                document.getElementsByName(value1)[0].readOnly = false
                $(document.getElementsByName(value1)[0]).css('background-color', '#ffffff');
              }
              else if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.REGULAR_EXPRESSION.name()%>")
              {

              }
              else
              {
                if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
                {
                  document.getElementsByName(selectvalue1)[0].disabled = false;
                  $(document.getElementsByName(selectvalue1)[0]).css('background-color', '#ffffff');
                }
                document.getElementsByName(value1)[0].readOnly = false;
                $(document.getElementsByName(value1)[0]).css('background-color', '#ffffff');
                if (singleOperation.split(":")[1] == "BETWEEN")
                {
                  document.getElementsByName(value2)[0].readOnly = false;
                  $(document.getElementsByName(value2)[0]).css('background-color', '#ffffff');
                  if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
                  {
                    document.getElementsByName(selectvalue2)[0].disabled = false;
                    $(document.getElementsByName(selectvalue2)[0]).css('background-color', '#ffffff');
                  }
                }
                else
                {
                  if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
                  {
                    document.getElementsByName(selectvalue2)[0].disabled = true;
                    $(document.getElementsByName(selectvalue2)[0]).css('background-color', '#EBEBE4');
                  }
                  document.getElementsByName(value2)[0].readOnly = true;
                  $(document.getElementsByName(value2)[0]).css('background-color', '#EBEBE4');
                }
              }
            }
          }

          break;
        }

        if(option.indexOf(selectedOption[i])>=0)
        {

          var index=option.indexOf(selectedOption[i]);

          optionStatus[index]="Y";
        }

      }
      else if(currentRowStatus[i]=="D")
      {

        var index=option.indexOf(selectedOption[i]);

        optionStatus[index]="N";

        selectedOption[i]="";

      }
    }
    if(validationFail==true)
    {
      alert("please fill in the previous data");
    }
    else
    {
      currentRowFlag=element;
      var profileId = "businessProfileId_" + element;
      var fileType = "fileType_" + element;
      var isapplicable = "businessProfileIsApplicable_" +element;
      document.getElementsByName(profileId)[0].disabled = false;
      $(document.getElementsByName(profileId)[0]).css('background-color', '#ffffff');
      document.getElementById(isapplicable).setAttribute("onclick","enable_text(this.checked,"+element+")");

      var l=0;
      for(l=1;l<optionStatus.length;l++)
      {
        if(optionStatus[l]=="Y" )
        {
          if(option.indexOf(selectedOption[element])==l)
          {
            (this.document.getElementsByName(profileId)[0].options[l]).style.display="block";//TODO temporary
            optionStatus[(option.indexOf(selectedOption[element]))]="N";
          }
          else
          {
            (this.document.getElementsByName(profileId)[0].options[l]).style.display = "none";//TODO temporary
          }
        }
        else if(optionStatus[l]=="N")
        {
          (this.document.getElementsByName(profileId)[0].options[l]).style.display="block";//TODO temporary
        }
      }
      for (var inner = 0; inner < optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",").length; inner++)
      {
        //alert("Inside After Check Sub Disable:::"+inner);
        var singleOperation = optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",")[inner];
        var value1 = "businessProfileValue1_" + element + "_" + (Number(inner) + 1);
        var value2 = "businessProfileValue2_" + element + "_" + (Number(inner) + 1);
        var selectvalue1 = "selectBusinessProfileValue1_" + element + "_" + (Number(inner) + 1);
        var selectvalue2 = "selectBusinessProfileValue2_" + element + "_" + (Number(inner) + 1);
        if(document.getElementById(isapplicable).checked == true)
        {
          if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.FLAT_FILE.name()%>")
          {
            document.getElementsByName(value1)[0].readOnly = false
            $(document.getElementsByName(value1)[0]).css('background-color', '#ffffff');
          }
          else if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.REGULAR_EXPRESSION.name()%>")
          {

          }
          else
          {
            if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
            {
              document.getElementsByName(selectvalue1)[0].disabled = false;
              $(document.getElementsByName(selectvalue1)[0]).css('background-color', '#ffffff');
              document.getElementsByName(selectvalue2)[0].disabled = false;
              $(document.getElementsByName(selectvalue2)[0]).css('background-color', '#ffffff');
            }
            document.getElementsByName(value1)[0].readOnly = false;
            $(document.getElementsByName(value1)[0]).css('background-color', '#ffffff');
            if (singleOperation.split(":")[1] == "BETWEEN")
            {
              if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
              {
                document.getElementsByName(selectvalue2)[0].disabled = false;
                $(document.getElementsByName(selectvalue2)[0]).css('background-color', '#ffffff');
              }
              document.getElementsByName(value2)[0].readOnly = false;
              $(document.getElementsByName(value2)[0]).css('background-color', '#ffffff');
            }
            else
            {
              if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
              {
                document.getElementsByName(selectvalue2)[0].disabled = true;
                $(document.getElementsByName(selectvalue2)[0]).css('background-color', '#EBEBE4');
              }
              document.getElementsByName(value2)[0].readOnly = true;
              $(document.getElementsByName(value2)[0]).css('background-color', '#EBEBE4');
            }
          }
        }
        else
        {
          if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.FLAT_FILE.name()%>")
          {
            document.getElementsByName(value1)[0].readOnly = true
            $(document.getElementsByName(value1)[0]).css('background-color', '#EBEBE4');
          }
          else if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.REGULAR_EXPRESSION.name()%>")
          {

          }
          else
          {
            if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
            {
              document.getElementsByName(selectvalue1)[0].disabled = true;
              $(document.getElementsByName(selectvalue1)[0]).css('background-color', '#EBEBE4');
              document.getElementsByName(selectvalue2)[0].disabled = true;
              $(document.getElementsByName(selectvalue2)[0]).css('background-color', '#EBEBE4');
            }
            document.getElementsByName(value1)[0].readOnly = true;
            $(document.getElementsByName(value1)[0]).css('background-color', '#EBEBE4');

            document.getElementsByName(value2)[0].readOnly = true;
            $(document.getElementsByName(value2)[0]).css('background-color', '#EBEBE4');
          }
        }
      }

    }
  }
  function isNumberKey(evt)  //updated
  {
    var charCode = (evt.which) ? evt.which : event.keyCode
    if (charCode > 31 && (charCode < 48 || charCode > 57))
      return false;

    return true;
  }
  function enable_text(status,name)
  {
    var profileId = "businessProfileId_" + name;

    if ($('select[name=' + profileId + '] option:selected').val() != "")
    {
      for (var inner = 0; inner < optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",").length; inner++)
      {
        //alert("Inside After Check Sub Disable:::"+inner);
        var singleOperation = optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",")[inner];
        var value1 = "businessProfileValue1_" + name + "_" + (Number(inner) + 1);
        var value2 = "businessProfileValue2_" + name + "_" + (Number(inner) + 1);
        var selectvalue1 = "selectBusinessProfileValue1_" + name+ "_" + (Number(inner) + 1);
        var selectvalue2 = "selectBusinessProfileValue2_" + name + "_" + (Number(inner) + 1);

        if(status==true)
        {
          if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.FLAT_FILE.name()%>")
          {
            document.getElementsByName(value1)[0].readOnly = false
            $(document.getElementsByName(value1)[0]).css('background-color', '#ffffff');
          }
          else if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.REGULAR_EXPRESSION.name()%>")
          {

          }
          else
          {
            if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
            {
              document.getElementsByName(selectvalue1)[0].disabled = false;
              $(document.getElementsByName(selectvalue1)[0]).css('background-color', '#ffffff');
            }

            document.getElementsByName(value1)[0].readOnly = false;
            $(document.getElementsByName(value1)[0]).css('background-color', '#ffffff');
            if (singleOperation.split(":")[1] == "BETWEEN")
            {
              if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
              {
                document.getElementsByName(selectvalue2)[0].disabled = false;
                $(document.getElementsByName(selectvalue2)[0]).css('background-color', '#ffffff');
              }
              document.getElementsByName(value2)[0].readOnly = false;
              $(document.getElementsByName(value2)[0]).css('background-color', '#ffffff');
            }
            else
            {
              document.getElementsByName(value2)[0].readOnly = true;
              $(document.getElementsByName(value2)[0]).css('background-color', '#EBEBE4');
            }
          }
        }
        else
        {
          if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.FLAT_FILE.name()%>")
          {
            document.getElementsByName(value1)[0].readOnly = true
            $(document.getElementsByName(value1)[0]).css('background-color', '#EBEBE4');
          }
          else if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.REGULAR_EXPRESSION.name()%>")
          {

          }
          else
          {
            if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
            {
              document.getElementsByName(selectvalue1)[0].disabled = true;
              $(document.getElementsByName(selectvalue1)[0]).css('background-color', '#EBEBE4');
              document.getElementsByName(selectvalue2)[0].disabled = true;
              $(document.getElementsByName(selectvalue2)[0]).css('background-color', '#EBEBE4');
            }
            document.getElementsByName(value1)[0].readOnly = true;
            $(document.getElementsByName(value1)[0]).css('background-color', '#EBEBE4');

            document.getElementsByName(value2)[0].readOnly = true;
            $(document.getElementsByName(value2)[0]).css('background-color', '#EBEBE4');
          }
        }
      }
    }
  }
  function cancel(ctoken) {
    document.myForm.action="/partner/net/BusinessProfileList?ctoken="+ctoken;
  }
</script>


</body>
</html>
<%
  }
  catch (Exception e)
  {
    logger.debug("Exception"+e);
  }
%>