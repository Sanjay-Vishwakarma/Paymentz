<%@ page import="com.directi.pg.Database,com.directi.pg.Functions"%>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.manager.PartnerManager" %>
<%@ include file="top.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
  Logger logger=new Logger("manageGatewayAccount.jsp");
  Functions functions= new Functions();
%>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","gatewayAccountInterface");
%>
<html>
<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
<script src="/partner/NewCss/js/jquery-ui.min.js"></script>
<head>
  <title><%=company%> | Partner Gateway Mapping</title>
  <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
  <script src="/partner/NewCss/am_multipleselection/bootstrap-multiselect.js"></script>
  <link href="/partner/NewCss/am_multipleselection/bootstrap-multiselect.css" rel="stylesheet" />
  <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.8.1/css/all.css">
  <style type="text/css">
    #maintitle{
      text-align: center;
      background: #7eccad;
      color: #fff;
      font-size: 14px;
    }

    @media(min-width: 640px){
      #saveid{
        position: absolute;
        background: #F9F9F9!important;
      }

      #savetable{
        padding-bottom: 25px;
      }

      table.table{
        margin-bottom: 6px !important;
      }

      table#savetable td:before{
        font-size: inherit;
      }
    }

    table#savetable td:before{
      font-size: 13px;
      font-family: Open Sans;
    }

    table.table{
      margin-bottom: 0px !important;
    }

    #saveid input{
      font-size: 16px;
      padding-right: 30px;
      padding-left: 30px;
    }


    .multiselect {
      width: 100%;

    }

    .selectBox {
      position: relative;
    }

    .selectBox select {
      width: 100%;
      font-weight: bold;
    }

    .overSelect {
      position: absolute;
      left: 0;
      right: 0;
      top: 0;
      bottom: 0;
    }

    #checkboxes {
      display: none;
      border: 1px #dadada solid;
      position: absolute;
      width: 100%;
      background-color: #ffffff;
      z-index: 1;
      height: 130px;
      overflow-x: auto;
    }

    #checkboxes label {
      display: block;
    }

    #checkboxes label:hover {
      background-color: #1e90ff;
    }


    #checkboxes_1 label {
      display: block;
    }

    #checkboxes_1 label:hover {
      background-color: #1e90ff;
    }


    #checkboxes_2 label {
      display: block;
    }

    #checkboxes_2 label:hover {
      background-color: #1e90ff;
    }

    input[type="checkbox"]{
      width: 18px; /*Desired width*/
      height: 18px; /*Desired height*/
    }

    /********************************************************************************/

    .multiselect-container>li {
      padding: 0;
      margin-left: 31px;
    }
    .open>#multiselect-id.dropdown-menu {
      display: block;
      padding-top: 5px;
      padding-bottom: 5px;
    }
    .multiselect-container>li>a>label {
      margin: 0;
      height: 28px;
      padding-left:1px; !important;
      text-align: left;
    }
    span.multiselect-native-select {
      position: relative;
    }

    @supports (-ms-ime-align:auto) {
      span.multiselect-native-select {
        position: static!important;
      }
    }

    select[multiple], select[size] {
      height: auto;
      border-color: rgb(169, 169, 169);
    }
    .widget .btn-group {
      z-index: 1;
    }
    .btn-group, .btn-group-vertical {
      position: relative;
      vertical-align: middle;
      border-radius: 4px;
      width:100%;
      height: 30px;
      background-color: #fff;
    }
    #mainbtn-id.btn-default {
      color: #333;
      background-color: #fff;
      padding: 6px;
      border: 1px solid #b2b2b2;
      height: 33px;
    }
    .btn-group>.btn:first-child {
      margin-left: 0;
    }

    .btn-group>.btn:first-child {
      margin-left: 0;
    }

    .btn-group>.btn, .btn-group-vertical>.btn {
      position: relative;
      float: left;
    }
    .multiselect-container {
      position: absolute;
      list-style-type: none;
      margin: 0;
      padding: 0;
      height: 188px;
      overflow-y: scroll;
    }
    #multiselect-id.dropdown-menu {
      position: absolute;
      top: 100%;
      left: 0;
      z-index: 1000;
      display: none;
      float: left;
      min-width: 100%;
      font-size: 14px;
      font-family: "Open Sans","Helvetica Neue",Helvetica,Arial,sans-serif;
      list-style: none;
      background-color: #fff;
      border: 1px solid #ccc;
      border: 1px solid rgba(0,0,0,0.15);
      border-radius: 4px;
      -webkit-box-shadow: 0 6px 12px rgba(0,0,0,0.175);
      box-shadow: 0 6px 12px rgba(0,0,0,0.175);
      background-clip: padding-box;
    }
    #mainbtn-id .multiselect-selected-text{
      font-size: 12px;
      font: inherit;
      padding-right: 18px;
    }
    #mainbtn-id.btn-default, #mainbtn-id.btn-default:focus, #mainbtn-id.btn-default.focus, #mainbtn-id.btn-default:active, #mainbtn-id.btn-default.active, .open>.dropdown-toggle#mainbtn-id.btn-default {
      color: #333;
      /*color: #fff;*/
      background-color: white!important;
      text-align: left;
      width: 100%;
    }
    .tr1 .multiselect-native-select .btn-group #mainbtn-id .multiselect-selected-text , .tr0 .multiselect-native-select .btn-group #mainbtn-id .multiselect-selected-text
    {
      font-size: 13px;
      font: inherit;
    }
    .tr1 .multiselect-native-select .btn-group #mainbtn-id , .tr0 .multiselect-native-select .btn-group #mainbtn-id
    {
      border: 1px solid #ddd; !important;
    }
    .btn .caret { /*fa fa-chevron-down*/
      position: absolute;
      display: inline-block;!important;
      width: 0px;!important;
      height: 1px;!important;
      margin-left: 2px;!important;
      vertical-align: middle;!important;
      border-top: 7px solid;!important;
      border-right: 4px solid transparent;!important;
      border-left: 4px solid transparent;!important;
      float: right;!important;
      margin-top: 5px;!important;
      box-sizing: inherit;
      right: 5px;
      top: 15px;
      margin-top: -2px;
    }
    .fa-chevron-down{
      position: absolute;
      right:0px;
      top: 0px;
      margin-top: -2px;
      vertical-align: middle;
      float: right;
      font-size: 9px;
    }
    #mainbtn-id
    {
      overflow: hidden;
      display: block;
    }
  </style>
  <script src="/merchant/javascript/hidde.js"></script>
  <style type="text/css">
    @media(max-width: 991px) {
      .additional-btn {
        float: left;
        margin-left: 30px;
        margin-top: 10px;
        position: inherit!important;
      }
    }

    @media (min-width: 768px){
      .form-horizontal .control-label {
        text-align: left!important;
      }
    }


    /*    .textb{color: red!important;}*/

  </style>
  <script type="text/javascript">
     function check()
    {
      var msg = "";
      var flag = false;
      if (document.getElementById("accountid").value.length == 0)
      {
        msg = msg + "\nPlease Select Account ID.";
        flag = true;
        //document.getElementById("pgtypeid").focus();
      }

      if (flag == true)
      {
        alert(msg);
        return false;
      }
      else
      {
        document.addtype.submit();
        return true;
      }
    }

    $(function ()
    {
      $(document).ready(function ()
      {
        $(".caret").addClass('icon2');
        $('.multiselect-selected-text').addClass("filter-option pull-left");
        firefox = navigator.userAgent.search("Firefox");
        if (firefox > -1)
        {
          $('.icon2').removeClass("caret");
          $('.icon2').addClass("fa fa-chevron-down");
          $('.icon2').css({
            "height": "30px",
            "width": "17px",
            "text-align": "center",
            "background-color": "#E6E2E2",
            "padding-top": "6px",
            "margin-top": "0px",
            "border": "1px solid #C7BFBF"
          });
          $('.dropdown-toggle').css({"padding": "0px", "vertical-align": "middle", "height": "25px"});
          $('.tr0 .dropdown-toggle .icon2').css({"background-color": "white", "border": "0px"});
          $('.tr1 .dropdown-toggle .icon2').css({"background-color": "white", "border": "0px"});
          $('.multiselect-selected-text').css({
            "padding-top": "4px",
            "padding-bottom": "10px",
            "padding-left": "10px",
            "vertical-align": "middle"
          });
        }
      });

    });

  </script>
  <style type="text/css">
    .field-icon
    {
      float:  right;
      margin-top: -25px;
      position: relative;
      z-index: 2;
    }
  </style>
</head>
<body>
<%
  Functions functions = new Functions();
  PartnerFunctions partnerFunctions=new PartnerFunctions();
  PartnerManager partnerManager=new PartnerManager();
  TreeMap<Integer, String> gatewayTypes =null;
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (partner.isLoggedInPartner(session))
  {
    String partnerId = (String) session.getAttribute("merchantid");
    String pgtypeId="";
    if (request.getParameter("pgtypeid") != null && request.getParameter("pgtypeid").length() > 0)
      pgtypeId = request.getParameter("pgtypeid");
    String reqPartnerId = Functions.checkStringNull(request.getParameter("partnerId")) == null ? "" : request.getParameter("partnerId");
    gatewayTypes = partnerManager.loadGatewayAccountForPartner(partnerId);
    TreeMap<String, String> subPartnersDetails = partnerFunctions.getPartnerDetailsForMap(partnerId);
    String str="";
    str= str + "&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    int pageno=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
    int pagerecords=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));
    boolean flag=false;
    String bankid=request.getParameter("bankid");
    String accountId1=request.getParameter("accountid");
    str = str + "&SRecords=" + pagerecords;
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String partnerGatewayAccountMapping_Partner_Gateway = StringUtils.isNotEmpty(rb1.getString("partnerGatewayAccountMapping_Partner_Gateway")) ? rb1.getString("partnerGatewayAccountMapping_Partner_Gateway") : "Partner Gateway Account Allocation";
    String partnerGatewayAccountMapping_account_id = StringUtils.isNotEmpty(rb1.getString("partnerGatewayAccountMapping_account_id")) ? rb1.getString("partnerGatewayAccountMapping_account_id") : "Account Id* :";
    String partnerGatewayAccountMapping_select_bank = StringUtils.isNotEmpty(rb1.getString("partnerGatewayAccountMapping_select_bank")) ? rb1.getString("partnerGatewayAccountMapping_select_bank") : "Select Bank";
    String partnerGatewayAccountMapping_partner_id = StringUtils.isNotEmpty(rb1.getString("partnerGatewayAccountMapping_partner_id")) ? rb1.getString("partnerGatewayAccountMapping_partner_id") : "Partner Id* :";
    String partnerGatewayAccountMapping_select_partner = StringUtils.isNotEmpty(rb1.getString("partnerGatewayAccountMapping_select_partner")) ? rb1.getString("partnerGatewayAccountMapping_select_partner") : "Select Partner Id";
    String partnerGatewayAccountMapping_is_active = StringUtils.isNotEmpty(rb1.getString("partnerGatewayAccountMapping_is_active")) ? rb1.getString("partnerGatewayAccountMapping_is_active") : "Is Active* :";
    String partnerGatewayAccountMapping_Y = StringUtils.isNotEmpty(rb1.getString("partnerGatewayAccountMapping_Y")) ? rb1.getString("partnerGatewayAccountMapping_Y") : "Y";
    String partnerGatewayAccountMapping_N = StringUtils.isNotEmpty(rb1.getString("partnerGatewayAccountMapping_N")) ? rb1.getString("partnerGatewayAccountMapping_N") : "N";
    String partnerGatewayAccountMapping_Button = StringUtils.isNotEmpty(rb1.getString("partnerGatewayAccountMapping_Button")) ? rb1.getString("partnerGatewayAccountMapping_Button") : "Button";
    String partnerGatewayAccountMapping_save = StringUtils.isNotEmpty(rb1.getString("partnerGatewayAccountMapping_save")) ? rb1.getString("partnerGatewayAccountMapping_save") : "Save";
    String partnerGatewayAccountMapping_search = StringUtils.isNotEmpty(rb1.getString("partnerGatewayAccountMapping_search")) ? rb1.getString("partnerGatewayAccountMapping_search") : "Search";
    String partnerGatewayAccountMapping_report_table = StringUtils.isNotEmpty(rb1.getString("partnerGatewayAccountMapping_report_table")) ? rb1.getString("partnerGatewayAccountMapping_report_table") : "Report Table";
    String partnerGatewayAccountMapping_SrNo = StringUtils.isNotEmpty(rb1.getString("partnerGatewayAccountMapping_SrNo")) ? rb1.getString("partnerGatewayAccountMapping_SrNo") : "Sr No";
    String partnerGatewayAccountMapping_account_id1 = StringUtils.isNotEmpty(rb1.getString("partnerGatewayAccountMapping_account_id1")) ? rb1.getString("partnerGatewayAccountMapping_account_id1") : "Account Id";
    String partnerGatewayAccountMapping_PgType = StringUtils.isNotEmpty(rb1.getString("partnerGatewayAccountMapping_PgType")) ? rb1.getString("partnerGatewayAccountMapping_PgType") : "PgType Id";
    String partnerGatewayAccountMapping_Currency = StringUtils.isNotEmpty(rb1.getString("partnerGatewayAccountMapping_Currency")) ? rb1.getString("partnerGatewayAccountMapping_Currency") : "Currency";
    String partnerGatewayAccountMapping_partner = StringUtils.isNotEmpty(rb1.getString("partnerGatewayAccountMapping_partner")) ? rb1.getString("partnerGatewayAccountMapping_partner") : "Partner Id";
    String partnerGatewayAccountMapping_action = StringUtils.isNotEmpty(rb1.getString("partnerGatewayAccountMapping_action")) ? rb1.getString("partnerGatewayAccountMapping_action") : "Action";
    String partnerGatewayAccountMapping_delete = StringUtils.isNotEmpty(rb1.getString("partnerGatewayAccountMapping_delete")) ? rb1.getString("partnerGatewayAccountMapping_delete") : "Delete";
    String partnerGatewayAccountMapping_showing = StringUtils.isNotEmpty(rb1.getString("partnerGatewayAccountMapping_showing")) ? rb1.getString("partnerGatewayAccountMapping_showing") : "Showing Page";
    String partnerGatewayAccountMapping_of = StringUtils.isNotEmpty(rb1.getString("partnerGatewayAccountMapping_of")) ? rb1.getString("partnerGatewayAccountMapping_of") : "of";
    String partnerGatewayAccountMapping_records = StringUtils.isNotEmpty(rb1.getString("partnerGatewayAccountMapping_records")) ? rb1.getString("partnerGatewayAccountMapping_records") : "records";
    String partnerGatewayAccountMapping_Sorry = StringUtils.isNotEmpty(rb1.getString("partnerGatewayAccountMapping_Sorry")) ? rb1.getString("partnerGatewayAccountMapping_Sorry") : "Sorry";
    String partnerGatewayAccountMapping_no_records = StringUtils.isNotEmpty(rb1.getString("partnerGatewayAccountMapping_no_records")) ? rb1.getString("partnerGatewayAccountMapping_no_records") : "No records found.";

%>

<div class="content-page">
  <div class="content">
    <div class="page-heading">
      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/gatewayAccountInterface.jsp?ctoken=<%=ctoken%>" method="post" name="form">
            <button class="btn-xs" type="submit" name="B1" style="background: transparent;border: 0;">
              <img style="height: 35px;" src="/partner/images/goBack.png">
            </button>
          </form>
        </div>
      </div>
      <br><br><br>

      <div class="row">
        <div class="col-md-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerGatewayAccountMapping_Partner_Gateway%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <br>
            <form action="/partner/net/PartnerGatewayAccountMapping?ctoken=<%=ctoken%>" method="post" name="addbankaccount" class="form-horizontal" onsubmit="return check()">
              <input type="hidden" value="<%=ctoken%>" name="ctoken">
              <div class="widget-content padding">
                <%
                  String msg = (String) request.getAttribute("statusMsg");
                  String msg2 = (String) request.getAttribute("statusMsg2");

                  if(functions.isValueNull(msg) && !"Invalid PartnerID<BR>".equalsIgnoreCase(msg))
                  {
                    flag=true;
                    out.println("<h5 class=\"bg-info\" style=\"text-align: center;\">&nbsp;&nbsp;" + msg + "</h5>");
                  }else if("Invalid PartnerID<BR>".equalsIgnoreCase(msg)){
                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\">&nbsp;&nbsp;" + msg + "</h5>");
                  }
                  else if(functions.isValueNull(msg2))
                  {
                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\">&nbsp;&nbsp;" + msg2 + "</h5>");
                  }
                %>
                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=partnerGatewayAccountMapping_account_id%></label>
                  <div class="col-md-4">
                    <select id="accountid" name="accountid" class="form-control" multiple="multiple" size="1">
                      <%--<option value="" default>Display all Banks</option>--%>
                      <%
                        StringBuilder sb = new StringBuilder();
                        for (Integer id : gatewayTypes.keySet())
                        {
                          String st = "";
                          String name = gatewayTypes.get(id);%>
                      <option value="<%=id%>"><%=id%>-<%=gatewayTypes.get(id)%>
                      </option>
                      <%
                        }
                      %>
                    </select>
                    <input type="hidden" id="bankid" name="bankid" value="<%=accountId1%>">

                    <script type="application/javascript">
                      <%
                      if(flag){
                        if(functions.isValueNull(bankid)){%>
                          var bankid = "<%=bankid%>";
                          var temp = new Array();
                          if(bankid != "")
                          {
                            temp = bankid.split(",");
                          }
                          for(var i=0; i <= temp.length ; i++)
                          {
                            console.log(temp[i]);
                            $("#accountid option[value='"+temp[i]+"']").prop('selected', true);
                          }
                          <%
                            }
                                }else{
                                        if(functions.isValueNull(accountId1)){%>
                          $("#accountid option[value='<%=accountId1%>']").prop('selected', true);
                          <%
                          }
                          }
                          %>
                      $('#accountid').multiselect({
                        buttonText: function (options, select)
                        {
                          var labels = [];
                          if (options.length === 0)
                          {
                            labels.pop();
                            document.getElementById('bankid').value = labels;
                            return 'Select Account ID';
                          }
                          else
                          {
                            options.each(function ()
                            {
                              if($(this).val() != "")
                              {
                                labels.push($(this).val());
                              }
                            });
                            document.getElementById('bankid').value = labels;
                            return 'Select Account ID';
                          }
                        },
                        includeSelectAllOption: true
                      });
                    </script>
                    <%--<select id="accountid" name="accountid" class="form-control"> <option value="" default><%=partnerGatewayAccountMapping_select_bank%></option>
                      <%
                        Connection con = null;
                        try
                        {
                          con = Database.getConnection();
                          StringBuffer qry = new StringBuffer("\n" +
                                  "SELECT gt.pgtypeid,gt.gateway,gt.currency,gt.name,ga.accountid,ga.merchantid,gapm.partnerid \n" +
                                  "FROM gateway_type AS gt JOIN gateway_accounts AS ga JOIN gateway_account_partner_mapping AS gapm WHERE \n" +
                                  "gt.pgtypeid=ga.pgtypeid AND ga.accountid=gapm.accountid AND gapm.isActive='Y' AND gapm.partnerid=?");
                          PreparedStatement pstmt = con.prepareStatement(qry.toString());
                          pstmt.setString(1, partnerId);
                          ResultSet rs = pstmt.executeQuery();
                          while (rs.next())
                          {
                            String selected = "";
                            if (rs.getString("pgtypeid").equals(pgtypeId))
                            {
                              selected = "selected"; 
                            }
                            out.println("<option value=" + rs.getString("accountid")+ selected + ">" + rs.getString("accountid") +"- " + rs.getString("currency") +"-"+rs.getString("merchantid")+"-"+ rs.getString("name") +"</option>");
                          }
                        }
                        catch (Exception e)
                        {
                          logger.error("Exception::::" + e);
                        }
                        finally
                        {
                          Database.closeConnection(con);
                        }
                      %>
                    </select>--%>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=partnerGatewayAccountMapping_partner_id%></label>
                  <div class="col-md-4">
                    <select name="partnerId" class="form-control">
                      <option value=""><%=partnerGatewayAccountMapping_select_partner%></option>
                      <%
                        for(String pid : subPartnersDetails.keySet())
                        {
                          String isSelected = "";
                          if (pid.equals(reqPartnerId))
                          {
                            isSelected = "selected";
                          }
                      %>
                      <option value="<%=pid%>" <%=isSelected%>><%=subPartnersDetails.get(pid)%></option>
                      <%
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=partnerGatewayAccountMapping_is_active%></label>
                  <div class="col-md-4">
                    <select name="isactive" id="isactive">
                      <option value='Y'><%=partnerGatewayAccountMapping_Y%></option>
                      <option value='N'><%=partnerGatewayAccountMapping_N%></option>
                    </select></td>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label" style="visibility:hidden;"><%=partnerGatewayAccountMapping_Button%></label>
                  <div class="col-md-1">
                    <button type="submit" class="buttonform btn btn-default" name="action" value="save">
                      <i class="fa fa-save"></i>&nbsp;
                      <%=partnerGatewayAccountMapping_save%>
                    </button>
                  </div>
                  <div class="col-md-5">
                    <button type="submit" class="buttonform btn btn-default">
                      <i class="fa fa-save"></i>&nbsp;
                      <%=partnerGatewayAccountMapping_search%>
                    </button>
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
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerGatewayAccountMapping_report_table%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding" style="overflow-x: auto;">

              <%  StringBuffer requestParameter = new StringBuffer();
                Enumeration<String> parameterNames = request.getParameterNames();
                while(parameterNames.hasMoreElements())
                {
                  String name=parameterNames.nextElement();
                  if("SPageno".equals(name) || "SRecords".equals(name))
                  {

                  }
                  else
                    requestParameter.append("<input type='hidden' name='"+name+"' value=\""+request.getParameter(name)+"\"/>");
                }
                requestParameter.append("<input type='hidden' name='SPageno' value='"+pageno+"'/>");
                requestParameter.append("<input type='hidden' name='SRecords' value='"+pagerecords+"'/>");


                // Hashtable hash = (Hashtable)request.getAttribute("transdetails");
                HashMap hash = (HashMap)request.getAttribute("transdetails");

                HashMap temphash=null;
                int records=0;
                int totalrecords=0;

                String errormsg=(String)request.getAttribute("message");
                String currentblock=request.getParameter("currentblock");
                if(currentblock==null)
                  currentblock="1";
                try
                {
                  records=Integer.parseInt((String)hash.get("records"));
                  totalrecords=Integer.parseInt((String)hash.get("totalrecords"));
                }
                catch(Exception ex)
                {

                }
                if(hash!=null)
                {
                  hash = (HashMap)request.getAttribute("transdetails");
                }
                if(records>0)
                {
                  flag=true;
              %>
              <table class="table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                <thead>
                <tr>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerGatewayAccountMapping_SrNo%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerGatewayAccountMapping_account_id1%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerGatewayAccountMapping_PgType%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerGatewayAccountMapping_PgType%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerGatewayAccountMapping_Currency%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerGatewayAccountMapping_partner%></b></td>
                  <td  colspan="2" valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerGatewayAccountMapping_action%></b></td>
                </tr>
                </thead>

                <%
                  String style="class=td1";
                  String ext="light";
                  for(int pos=1;pos<=records;pos++)
                  {
                    String id = Integer.toString(pos);
                    style = "class=\"tr" + (pos + 1) % 2 + "\"";
                    int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);
                    temphash=(HashMap)hash.get(id);
                    out.println("<tr id=\"maindata\">");
                    out.println("<td "+style+" valign=\"middle\" data-label=\"Sr No\" align=\"center\">&nbsp;"+srno+ "</td>");
                    out.println("<td "+style+" valign=\"middle\" data-label=\"Account Id\" align=\"center\">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("accountid"))+"<input type=\"hidden\" name=\"accountid\" value=\""+temphash.get("accountid")+"\"></td>");
                    out.println("<td "+style+" valign=\"middle\" data-label=\"PgType Id\" align=\"center\">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("pgtypeid"))+"<input type=\"hidden\" name=\"pgtypeid\" value=\""+temphash.get("pgtypeid")+"\"></td>");
                    out.println("<td "+style+" valign=\"middle\" data-label=\"Merchant Id\" align=\"center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("gateway"))+"<input type=\"hidden\" name=\"gateway\" value=\""+temphash.get("gateway")+"\"></td>");
                    out.println("<td "+style+" valign=\"middle\" data-label=\"Currency\" align=\"center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("currency"))+"<input type=\"hidden\" name=\"currency\" value=\""+temphash.get("currency")+"\"></td>");
                    out.println("<td "+style+" valign=\"middle\" data-label=\"Bank Name\" align=\"center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("partnerid"))+"<input type=\"hidden\" name=\"partnerid\" value=\""+temphash.get("partnerid")+"\"></td>");
                    out.println("<td "+style+" valign=\"middle\" data-label=\"Action\" align=\"center\"><form action=\"/partner/net/PartnerGatewayAccountMapping?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"accountid\" value=\"" + ESAPI.encoder().encodeForHTML((String) temphash.get("accountid"))+"\"><input type=\"hidden\" name=\"partnerId\" value=\"" + ESAPI.encoder().encodeForHTML((String) temphash.get("partnerid"))+"\"><input type=\"submit\" name=\"submit\" class=\"gotoauto btn btn-default\" value="+partnerGatewayAccountMapping_delete+"><input type=\"hidden\" name=\"action\" value=\"Delete\">");
                    out.println("</tr>");
                  }
                %>
              </table>
              <div id="showingid"><strong><%=partnerGatewayAccountMapping_showing%> <%=pageno%> <%=partnerGatewayAccountMapping_of%> <%=totalrecords%> <%=partnerGatewayAccountMapping_records%></strong></div>
              <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                <jsp:param name="pageno" value="<%=pageno%>"/>
                <jsp:param name="str" value="<%=str%>"/>
                <jsp:param name="page" value="PartnerGatewayAccountMapping"/>
                <jsp:param name="currentblock" value="<%=currentblock%>"/>
                <jsp:param name="orderby" value=""/>
              </jsp:include>
              <%
                }
                else
                {
                  out.println(Functions.NewShowConfirmation1(partnerGatewayAccountMapping_Sorry, partnerGatewayAccountMapping_no_records));
                }
              %>
              <%
                }
                else
                {
                  response.sendRedirect("/partner/logout.jsp");
                  return;
                }
              %>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>