<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.fraud.FraudSystemService" %>
<%@ page import="com.manager.ApplicationManager" %>
<%@ page import="com.manager.vo.fraudruleconfVOs.FraudSystemAccountVO" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ include file="functions.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: NAMRATA B. BARI
  Date: 18/01/2020
  Time: 17:04
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp"%>
<html>
<head>
  <title>Fraud System Accounts</title>
  <style type="text/css">
    @media(max-width: 991px) {
      .additional-btn {
        float: left;
        margin-left: 30px;
        margin-top: 10px;
        position: inherit!important;
      }
    }
  </style>
  <script>
    $(document).ready(function(){

      var w = $(window).width();

      //alert(w);

      if(w > 990){
        //alert("It's greater than 990px");
        $("body").removeClass("smallscreen").addClass("widescreen");
        $("#wrapper").removeClass("enlarged");
      }
      else{
        //alert("It's less than 990px");
        $("body").removeClass("widescreen").addClass("smallscreen");
        $("#wrapper").addClass("enlarged");
        $(".left ul").removeAttr("style");
      }
    });

  </script>
</head>
<body>

<%!
  Logger logger =new Logger("test.jsp");
  private ApplicationManager applicationManager = new ApplicationManager();
  private Functions functions = new Functions();
%>
<%
  if (partner.isLoggedInPartner(session))
  {
    logger.debug("Fraud System Account Master");
    session.setAttribute("submit","Fraud System Account Master");
    String fsid = Functions.checkStringNull(request.getParameter("fsid"));
    String accountName = Functions.checkStringNull(request.getParameter("fsaccountid"));
    String str = "";

    if(fsid == null)
    {fsid = "";}

    if(accountName == null)
    {accountName = "";}

    str="ctoken="+ctoken;
    if(fsid != null) {str=str+"&fsid=" + fsid;}
    if(accountName != null) {str=str+"&accountname=" +accountName;}
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String actionFraudSystemAccount_Allocated_Account = StringUtils.isNotEmpty(rb1.getString("actionFraudSystemAccount_Allocated_Account")) ? rb1.getString("actionFraudSystemAccount_Allocated_Account") : "Allocated Account List";
    String actionFraudSystemAccount_Allocate_Account = StringUtils.isNotEmpty(rb1.getString("actionFraudSystemAccount_Allocate_Account")) ? rb1.getString("actionFraudSystemAccount_Allocate_Account") : "Allocate Account";
    String actionFraudSystemAccount_Add_New_Account = StringUtils.isNotEmpty(rb1.getString("actionFraudSystemAccount_Add_New_Account")) ? rb1.getString("actionFraudSystemAccount_Add_New_Account") : "Add New Account";
    String actionFraudSystemAccount_Fraud_System_Account = StringUtils.isNotEmpty(rb1.getString("actionFraudSystemAccount_Fraud_System_Account")) ? rb1.getString("actionFraudSystemAccount_Fraud_System_Account") : "Fraud System Account";
    String actionFraudSystemAccount_Fraud_System = StringUtils.isNotEmpty(rb1.getString("actionFraudSystemAccount_Fraud_System")) ? rb1.getString("actionFraudSystemAccount_Fraud_System") : "Fraud System";
    String actionFraudSystemAccount_ALL = StringUtils.isNotEmpty(rb1.getString("actionFraudSystemAccount_ALL")) ? rb1.getString("actionFraudSystemAccount_ALL") : "ALL";
    String actionFraudSystemAccount_Fraud_System_AccountId = StringUtils.isNotEmpty(rb1.getString("actionFraudSystemAccount_Fraud_System_AccountId")) ? rb1.getString("actionFraudSystemAccount_Fraud_System_AccountId") : "Fraud System Account Id";
    String actionFraudSystemAccount_Search = StringUtils.isNotEmpty(rb1.getString("actionFraudSystemAccount_Search")) ? rb1.getString("actionFraudSystemAccount_Search") : "Search";
    String actionFraudSystemAccount_update = StringUtils.isNotEmpty(rb1.getString("actionFraudSystemAccount_update")) ? rb1.getString("actionFraudSystemAccount_update") : "Update Fraud System Account Configuration";
    String actionFraudSystemAccount_Fraud_SystemID = StringUtils.isNotEmpty(rb1.getString("actionFraudSystemAccount_Fraud_SystemID")) ? rb1.getString("actionFraudSystemAccount_Fraud_SystemID") : "Fraud System ID";
    String actionFraudSystemAccount_System = StringUtils.isNotEmpty(rb1.getString("actionFraudSystemAccount_System")) ? rb1.getString("actionFraudSystemAccount_System") : "Fraud System Account Id";
    String actionFraudSystemAccount_account_name = StringUtils.isNotEmpty(rb1.getString("actionFraudSystemAccount_account_name")) ? rb1.getString("actionFraudSystemAccount_account_name") : "Fraud System Account Name";
    String actionFraudSystemAccount_User_Name = StringUtils.isNotEmpty(rb1.getString("actionFraudSystemAccount_User_Name")) ? rb1.getString("actionFraudSystemAccount_User_Name") : "User Name";
    String actionFraudSystemAccount_Password = StringUtils.isNotEmpty(rb1.getString("actionFraudSystemAccount_Password")) ? rb1.getString("actionFraudSystemAccount_Password") : "Password";
    String actionFraudSystemAccount_Contact_Name = StringUtils.isNotEmpty(rb1.getString("actionFraudSystemAccount_Contact_Name")) ? rb1.getString("actionFraudSystemAccount_Contact_Name") : "Contact Name*";
    String actionFraudSystemAccount_Contact_Email = StringUtils.isNotEmpty(rb1.getString("actionFraudSystemAccount_Contact_Email")) ? rb1.getString("actionFraudSystemAccount_Contact_Email") : "Contact Email*";
    String actionFraudSystemAccount_isTest = StringUtils.isNotEmpty(rb1.getString("actionFraudSystemAccount_isTest")) ? rb1.getString("actionFraudSystemAccount_isTest") : "isTest";
    String actionFraudSystemAccount_Y = StringUtils.isNotEmpty(rb1.getString("actionFraudSystemAccount_Y")) ? rb1.getString("actionFraudSystemAccount_Y") : "Y";
    String actionFraudSystemAccount_N = StringUtils.isNotEmpty(rb1.getString("actionFraudSystemAccount_N")) ? rb1.getString("actionFraudSystemAccount_N") : "N";
    String actionFraudSystemAccount_Update = StringUtils.isNotEmpty(rb1.getString("actionFraudSystemAccount_Update")) ? rb1.getString("actionFraudSystemAccount_Update") : "Update";
    String actionFraudSystemAccount_Result = StringUtils.isNotEmpty(rb1.getString("actionFraudSystemAccount_Result")) ? rb1.getString("actionFraudSystemAccount_Result") : "Result";
    String actionFraudSystemAccount_No = StringUtils.isNotEmpty(rb1.getString("actionFraudSystemAccount_No")) ? rb1.getString("actionFraudSystemAccount_No") : "No Records Found.";
%>

<div class="content-page">
  <div class="content">
    <div class="page-heading">
         <div class="pull-right">
          <div class="btn-group">
            <form action="/partner/managePartnerAllocatedAccountList.jsp?ctoken=<%=ctoken%>" method="POST">
              <button  class="btn-xs" type="submit" name="submit" name="B1" value="Fraud System Account Master"
                       style="background-color: #98A3A3; border-radius: 29px;color: white;padding: 8px 21px;font-size: 10px;cursor: pointer;">
                <%=actionFraudSystemAccount_Allocated_Account%> &nbsp; <i class ="fa fa-sign-in" style="font-size: small; color: white;"></i>
              </button>
            </form>
          </div>
        </div>
        <div class="pull-right">
          <div class="btn-group">

            <form action="/partner/managePartnerFraudSystemAccount.jsp?ctoken=<%=ctoken%>" method="POST">
              <button  class="btn-xs" type="submit" name="submit" name="B1" value="Fraud System Account Master"
                       style="background-color: #98A3A3; border-radius: 29px;color: white;padding: 8px 21px;font-size: 10px;cursor: pointer;">
                <%=actionFraudSystemAccount_Allocate_Account%> &nbsp; <i class ="fa fa-sign-in" style="font-size: small; color: white;"></i>
              </button>
            </form>
          </div>
        </div>

        <div class="pull-right">
          <div class="btn-group">
            <form action="/partner/manageFraudSystemAccount.jsp?ctoken=<%=ctoken%>" method="POST">
              <button  class="btn-xs" type="submit" name="submit" name="B1" value="Fraud System Account Master"
                       style="background-color: #98A3A3; border-radius: 29px;color: white;padding: 8px 21px;font-size: 10px;cursor: pointer;">
                <%=actionFraudSystemAccount_Add_New_Account%> &nbsp; <i class ="fa fa-sign-in" style="font-size: small; color: white;"></i>
              </button>
            </form>
          </div>
        </div>
        <br><br><%--<br><br><br><br><br>--%>
      <br><br>
      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">

            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=actionFraudSystemAccount_Fraud_System_Account%></strong></h2>
            </div>
            <div class="additional-btn">
              <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
              <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
              <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
            </div>
            <form action="/partner/net/FraudSystemAccountList?ctoken=<%=ctoken%>" method="post" name="forms" class="form-horizontal">
              <div class="widget-content padding">
                <div>
                  <input type="hidden" value="<%=ctoken%>" name="ctoken">
                  <div class="form-group col-md-4 has-feedback">
                    <label class="col-sm-4 control-label"><%=actionFraudSystemAccount_Fraud_System%></label>
                    <div class="col-sm-8">
                      <select class="form-control" name="fsid" >
                        <option value="" selected><%=actionFraudSystemAccount_ALL%></option>
                        <%
                          Hashtable<String,String> fraudSystem = FraudSystemService.getFraudSystem();
                          Iterator it1 = fraudSystem.entrySet().iterator();
                          while (it1.hasNext())
                          {
                            Map.Entry pair = (Map.Entry)it1.next();
                            if(pair.getKey().equals(fsid)){
                              out.println("<option value=\""+pair.getKey()+"\" selected>"+pair.getKey()+" - "+pair.getValue()+"</option>");
                            }else
                            {
                              out.println("<option value=\"" + pair.getKey() + "\">" + pair.getKey() + " - " + pair.getValue() + "</option>");
                            }
                          }
                        %>
                      </select>
                    </div>
                  </div>

                  <div class="form-group col-md-6">
                    <label class="col-sm-4 control-label"><%=actionFraudSystemAccount_Fraud_System_AccountId%></label>
                    <div class="col-sm-6">
                      <input name="fsaccountid" id="member" value="<%=accountName%>" class="form-control">
                    </div>
                  </div>

                  <div class="form-group col-md-2">
                    <div class="col-sm-offset-2 col-sm-3">
                      <button type="submit" class="btn btn-default" name="action" value="1_Edit"><i class="fa fa-clock-o"></i>
                        &nbsp;&nbsp;<%=actionFraudSystemAccount_Search%></button>
                    </div>
                  </div>

                </div>
              </div>
            </form>
          </div>
        </div>
      </div>

      <div class="row reporttable">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=actionFraudSystemAccount_update%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding" style="overflow-x: auto;">
              <div id="horizontal-form">
                <%
                  FraudSystemAccountVO accountVO = (FraudSystemAccountVO) request.getAttribute("accountDetails");
                  StringBuffer updateMsg = (StringBuffer) request.getAttribute("updateMsg");
                  StringBuffer msg = (StringBuffer)request.getAttribute("msg");

                  if(accountVO != null)
                  {
                %>
                <form action="/partner/net/ActionFraudSystemAccount?ctoken=<%=ctoken%>" method="post" name="form1" id="form1" class="form-horizontal">
                  <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                  <input type="hidden" name="mappingid" value="<%=accountVO.getFraudSystemAccountId() %>">
                  <div class="widget-content padding">
                    <div class="form-group">
                      <label  class="col-md-4 control-label"><%=actionFraudSystemAccount_Fraud_SystemID%><br>
                      </label>
                      <div class="col-md-4 ui-widget">
                        <input name="fsid"  style="border: 1px solid #b2b2b2;" value="<%=accountVO.getFraudSystemId()%>" class="form-control" disabled/>
                      </div>
                      <div class="col-md-6"></div>
                    </div>
                    <div class="form-group">
                      <label for="member" class="col-md-4 control-label"><%=actionFraudSystemAccount_System%><br>
                      </label>
                      <div class="col-md-4 ui-widget">
                        <input name="fsaccountid"  style="border: 1px solid #b2b2b2;" value="<%=accountVO.getFraudSystemAccountId()%>" class="form-control" disabled/>
                      </div>
                      <div class="col-md-6"></div>
                    </div>
                    <div class="form-group">
                      <label  class="col-md-4 control-label"><%=actionFraudSystemAccount_account_name%><br>
                      </label>
                      <div class="col-md-4 ui-widget">
                        <input name="accountname" style="border: 1px solid #b2b2b2;" value="<%=accountVO.getAccountName()%>" class="form-control" disabled/>
                      </div>
                      <div class="col-md-6"></div>
                    </div>

                    <div class="form-group">
                      <label  class="col-md-4 control-label"><%=actionFraudSystemAccount_User_Name%><br>
                      </label>
                      <div class="col-md-4 ui-widget">
                        <input name="username"  style="border: 1px solid #b2b2b2;" maxlength="255" value="<%=accountVO.getUserName()%>" class="form-control" />
                      </div>
                      <div class="col-md-6"></div>
                    </div>

                    <div class="form-group">
                      <label class="col-md-4 control-label"><%=actionFraudSystemAccount_Password%><br>
                      </label>
                      <div class="col-md-4 ui-widget">
                        <input name="pwd" style="border: 1px solid #b2b2b2;" maxlength="255" value="<%=accountVO.getPassword()%>" class="form-control" autocomplete="off" readonly onfocus="this.removeAttribute('readonly');"/>
                      </div>
                      <div class="col-md-6"></div>
                    </div>

                    <div class="form-group">
                      <label  class="col-md-4 control-label"><%=actionFraudSystemAccount_Contact_Name%><br>
                      </label>
                      <div class="col-md-4 ui-widget">
                        <input name="contactname"  style="border: 1px solid #b2b2b2;" value="<%=accountVO.getContactName()%>" class="form-control" />
                      </div>
                      <div class="col-md-6"></div>
                    </div>

                    <div class="form-group">
                      <label  class="col-md-4 control-label"><%=actionFraudSystemAccount_Contact_Email%><br>
                      </label>
                      <div class="col-md-4 ui-widget">
                        <input name="contactemail"  style="border: 1px solid #b2b2b2;" value="<%=accountVO.getContactEmail()%>" class="form-control" />
                      </div>
                      <div class="col-md-6"></div>
                    </div>

                    <div class="form-group">
                      <label  class="col-md-4 control-label"><%=actionFraudSystemAccount_isTest%><br>
                      </label>
                      <div class="col-md-4 ui-widget">
                        <select name="isTest" class="form-control">
                          <% if(accountVO.getIsTest().equals("Y")){
                          %>
                          <option value="Y" selected> <%=actionFraudSystemAccount_Y%></option>
                          <option value="N"> <%=actionFraudSystemAccount_N%></option>
                          <%}else{%>
                          <option value="Y" > <%=actionFraudSystemAccount_Y%></option>
                          <option value="N" selected> <%=actionFraudSystemAccount_N%></option>
                          <%}%>
                        </select>
                      </div>
                      <div class="col-md-6"></div>
                    </div>

                    <div class="form-group col-md-12 has-feedback">
                      <center>
                        <label >&nbsp;</label>
                        <input type="hidden" value="1" name="step">
                        <button type="submit" class="btn btn-default" id="submit" name="action" value="update" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=actionFraudSystemAccount_Update%></button>
                      </center>
                    </div>
                  </div>
                </form>
                <div>
          </div>
        </div>
      </div>

      <%

      %>
    </div>
  </div>
</div>
        <%

    }
     if(msg!=null && functions.isValueNull(msg.toString()))
     {
      out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + msg.toString() + "</h5>");
        }
    else if(updateMsg!=null && functions.isValueNull(updateMsg.toString()))
    {
      out.println(Functions.NewShowConfirmation1("Result", updateMsg.toString()));
    }
    else
    {
      out.println(Functions.NewShowConfirmation1(actionFraudSystemAccount_Result,actionFraudSystemAccount_No));
    }
  }
  else
  {
  response.sendRedirect("/partner/logout.jsp");
  return;
  }
%>
</body>
</html>