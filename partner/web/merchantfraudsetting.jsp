<%@ page import="com.directi.pg.Functions,com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ include file="top.jsp" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ResourceBundle" %>
<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
    session.setAttribute("submit","merchantfraudsetting");
    Functions functions = new Functions();
%>
<%!
    private static Logger log=new Logger("merchantfraudsetting.jsp");
%>
<html>
<head>
    <style type="text/css">
        @media (max-width: 640px){
            #smalltable{
                width: 100%!important;
            }
        }

        #ui-id-2
        {
            overflow: auto;
            max-height: 350px;
        }
    </style>
  <%--  <script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
    <script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
    <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
</head>
<title><%=company%> Merchant Settings> Merchant Fraud Settings </title>
<script>
    var lablevalues = new Array();
    function ChangeFunction(Value , lable){
        console.log("Value" + Value + "lable" + lable);
        var finalvalue=lable+"="+Value;
        console.log("finalvalue" + finalvalue );
        lablevalues.push(finalvalue);
        console.log(lablevalues);
        document.getElementById("onchangedvalue").value = lablevalues;
    }
</script>
<body class="bodybackground">
<%
    PartnerFunctions partnerFunctions=new PartnerFunctions();

    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);


    String merchantfraudsetting_Merchant_Fraud_Settings = rb1.getString("merchantfraudsetting_Merchant_Fraud_Settings");
    String merchantfraudsetting_Partner_ID = rb1.getString("merchantfraudsetting_Partner_ID");
    String merchantfraudsetting_Merchant_ID = rb1.getString("merchantfraudsetting_Merchant_ID");
    String merchantfraudsetting_Search = rb1.getString("merchantfraudsetting_Search");
    String merchantfraudsetting_Report_Table = rb1.getString("merchantfraudsetting_Report_Table");
    String merchantfraudsetting_Max_Score_Allowed = rb1.getString("merchantfraudsetting_Max_Score_Allowed");
    String merchantfraudsetting_Max_Score_Reversal = rb1.getString("merchantfraudsetting_Max_Score_Reversal");
    String merchantfraudsetting_Online_Fraud_Check = rb1.getString("merchantfraudsetting_Online_Fraud_Check");
    String merchantfraudsetting_Internal_Fraud_Check = rb1.getString("merchantfraudsetting_Internal_Fraud_Check");
    String merchantfraudsetting_Update_Selected = rb1.getString("merchantfraudsetting_Update_Selected");
    String merchantfraudsetting_Sorry = rb1.getString("merchantfraudsetting_Sorry");
    String merchantfraudsetting_No = rb1.getString("merchantfraudsetting_No");

%>
<div class="content-page">
    <div class="content">
        <div class="page-heading">
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=merchantfraudsetting_Merchant_Fraud_Settings%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                            if (partner.isLoggedInPartner(session))
                            {
                                //LinkedHashMap memberidDetails=partner.getPartnerMembersDetail((String) session.getAttribute("merchantid"));
                                String memberid=nullToStr(request.getParameter("memberid"));
                                String pid=nullToStr(request.getParameter("pid"));
                                String partnerid = session.getAttribute("partnerId").toString();
                                String Config =null;
                                String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
                                if(Roles.contains("superpartner")){

                                }else{
                                    pid = String.valueOf(session.getAttribute("merchantid"));
                                    Config = "disabled";
                                }
                        %>
                        <form action="/partner/net/MerchantFraudSetting?ctoken=<%=ctoken%>" method="get" name="F1" onsubmit="" class="form-horizontal">
                            <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                            <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                            <%
                                String errormsg1 = (String) request.getAttribute("error");
                                if (functions.isValueNull(errormsg1))
                                {
                                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg1 + "</h5>");
                                }
                            %>
                            <%
                                String cbmessage = (String) request.getAttribute("cbmessage");
                                if (functions.isValueNull(cbmessage))
                                {
                                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + cbmessage + "</h5>");
                                }
                                StringBuilder success = (StringBuilder) request.getAttribute("success");
                                StringBuilder failed = (StringBuilder) request.getAttribute("failed");

                                if(functions.isValueNull(String.valueOf(failed)))
                                {
                                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + failed + "</h5>");
                                }
                            %>
                            <div class="widget-content padding">
                                <div id="horizontal-form">
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                    <div class="form-group col-md-4">
                                        <label class="col-sm-4 control-label"><%=merchantfraudsetting_Partner_ID%></label>
                                        <div class="col-sm-8">
                                            <input name="pid" id="pid" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                                            <input type ="hidden" name="pid" value="<%=pid%>">
                                        </div>
                                    </div>

                                    <div class="form-group col-md-4">
                                        <label class="col-sm-4 control-label"><%=merchantfraudsetting_Merchant_ID%></label>
                                        <div class="col-sm-8">
                                            <input name="memberid" id="member" value="<%=memberid%>" class="form-control" autocomplete="on" >
                                              </div>
                                        </div>
                                    </div>
                                    <div class="form-group col-md-4">
                                        <div class="col-sm-offset-2 col-sm-3">
                                            <button type="submit" class="btn btn-default"><i class="fa fa-clock-o"></i>
                                                &nbsp;&nbsp;<%=merchantfraudsetting_Search%></button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
            <form action="/partner/net/SetReservesFraud?ctoken=<%=ctoken%>" method=post>
                <input type="hidden" value="" name="onchangedvalue" id="onchangedvalue">   <%--***do not remove the field*****--%>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=merchantfraudsetting_Report_Table%></strong></h2>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <div class="widget-content padding" style="overflow-x: auto;">
                                <%
                                    HashMap hash = (HashMap) request.getAttribute("memberdetails");
                                    HashMap temphash = null;
                                    int pageno =partnerFunctions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
                                    int pagerecords =partnerFunctions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);
                                    int records = 0;
                                    if((hash!=null && hash.size()>0))
                                    {
                                        try{
                                            records = Integer.parseInt((String) hash.get("records"));
                                        }
                                        catch (Exception ex)
                                        {
                                            log.error("Records & TotalRecords is found null",ex);
                                        }
                                    }
                                    if (records > 0)
                                    {      %>
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <center>
                                <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 60%;">
                                    <% String style = "td1";
                                        for (int pos = 1; pos <= records; pos++)
                                        {
                                            String id = Integer.toString(pos);
                                            int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);

                                            if (pos % 2 == 0)
                                                style = "tr0";
                                            else
                                                style = "tr0";
                                            temphash = null;
                                            temphash = (HashMap) hash.get(id);

                                            String memberId = (String) temphash.get("memberid");
                                            //String companyName = (String) temphash.get("company_name");

                                           // String accountId = Functions.checkStringNull((String) temphash.get("accountid"));
                                            //System.out.println("accountid---"+(String) temphash.get("accountid"));
                                    %>
                                   <input type=hidden name="memberid" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberId)%>">
                                    <thead>
                                    <tr>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=merchantfraudsetting_Max_Score_Allowed%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=merchantfraudsetting_Max_Score_Reversal%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=merchantfraudsetting_Online_Fraud_Check%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=merchantfraudsetting_Internal_Fraud_Check%></b></td>
                                    </tr>
                                    </thead>

                                    <tbody>
                                    <tr>
                                        <td valign="middle" data-label="Max Score Allowed" align="center" class="<%=style%>"><input type=text maxlength="7" class="form-control" size=10 name='maxscoreallowed' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("maxScoreAllowed"))%>" onchange="ChangeFunction(this.value,'Max Score Allowed')"></td>
                                        <td valign="middle" data-label="Max Score Reversal" align="center" class="<%=style%>"><input type=text maxlength="7" class="form-control" size=10 name='maxscoreautoreversal' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("maxScoreAutoReversal"))%>" onchange="ChangeFunction(this.value,'Max Score Reversal')"></td>
                                        <td valign="middle" data-label="Online Fraud Check" align="center" class="<%=style%>">
                                            <select name='onlineFraudCheck' class="form-control" onchange="ChangeFunction(this.value,'Online Fraud Check')">
                                                <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("onlineFraudCheck"))));  %>
                                            </select>
                                        </td>
                                        <td valign="middle" data-label="internal Fraud Check" align="center" class="<%=style%>">
                                            <select name='internalFraudCheck' class="form-control" onchange="ChangeFunction(this.value,'Internal Fraud Check')">
                                                <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("internalFraudCheck"))));  %>
                                            </select>
                                        </td>
                                    </tr>
                                    </tbody>
                                </table>
                                </center>
                                <center>
                                    <button type="submit" value="Save" class="btn btn-default">
                                       <%=merchantfraudsetting_Update_Selected%>
                                    </button>
                                </center>
                            </div>
                        </div>
                    </div>
                </div>
                <%
                    } //end for
                %>

            </form>
            <br>
            <%
                int currentblock = 1;
                try
                {
                    currentblock = Integer.parseInt(request.getParameter("currentblock"));
                }
                catch (Exception ex)
                {
                    currentblock = 1;
                }

            %>
            <%
                }
                else if (success != null)
                {
                    out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;" + success.toString()+ "</h5>");
                }
                else
                {
                    out.println(Functions.NewShowConfirmation1(merchantfraudsetting_Sorry, merchantfraudsetting_No));
                    out.println("</div>");
                }
            %>
        </div>
    </div>
</div>
<%
    }
    else
    {
        response.sendRedirect("/logout.jsp");
        return;
    }
%>
<script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
<script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
<link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
<link href="/partner/cookies/cookies_popup.css" rel="stylesheet">
</body>
</html>
<%!
    public static String nullToStr(String str)
    {
        if(str == null)
        return "";
        return str;
    }
%>