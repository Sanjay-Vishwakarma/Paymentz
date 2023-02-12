<%@ page import="com.directi.pg.Functions"%>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.dao.PartnerDAO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.manager.enums.*" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<link rel="stylesheet" type="text/css" href="/icici/styyle.css">
<%--
  Created by IntelliJ IDEA.
  User: Jinesh
  Date: 2/12/14
  Time: 11:43 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <title> PSP Setup</title>
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
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>

    <script language="javascript">



        $(document).ready(function(){
            $("#relaywithauth").val();
            var relaywithauth = document.getElementById("relaywithauth").value;
            if (relaywithauth.value == 'Y') {


                document.getElementById("smtp_password").disabled=false;

            }

            else
            {
                document.getElementById("smtp_password").disabled = true;
            }
        });

        function updatetextbox(relaywithauth)
        {

            if(relaywithauth.value=='Y')
                document.getElementById("smtp_password").disabled = false;

            else{

                document.getElementById("smtp_password").disabled=true;
            }
        }




    </script>


</head>
<body>
<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/autocomplete.js"></script>
<%
    Logger logger = new Logger("viewPartnerDetails.jsp");
    Functions functions=new Functions();
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    PartnerDAO partnerDAO = new PartnerDAO();
    LinkedHashMap<String,Integer> thememap = partnerDAO.listDefaulttheme();



    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        Map<String,String> ynMap=new HashMap<String, String>();
        ynMap.put("N","No");
        ynMap.put("Y","Yes");

        Map<String,String> erjbMap= new HashMap<String, String>();
        erjbMap.put("RO","RO");
        erjbMap.put("JA","JA");
        erjbMap.put("BG","BG");
        erjbMap.put("EN","EN");
%>

<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading">
                Partner Configuration Details

                <div style="float: right;">
                    <form action="/icici/partnerInterface.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" name="submit" class="addnewmember">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Partner Details
                        </button>
                    </form>
                </div>
            </div>

            <form action="/icici/servlet/EditPartnerDetails?ctoken=<%=ctoken%>" method="POST" >
                <input type="hidden" name="ctoken" id="ctoken" value="<%=ctoken%>">
                <input type="hidden" value="" name="onchangedvalue" id="onchangedvalue">   <%--***do not remove the field*****--%>


                <table border="1" align="center" style="width: 50%" class="table table-striped table-bordered table-hover table-green dataTable">
                    <%
                        String message=(String)request.getAttribute("message");
                        String partnerid = (String) request.getAttribute("partnerid");
                        String exportTransactionCron = "";
                        HashMap<String, String> timezoneHash = Functions.getTimeZone();
                        String role="Admin";
                        String username=(String)session.getAttribute("username");
                        String actionExecutorId=(String)session.getAttribute("merchantid");
                        String actionExecutorName=role+"-"+username;


                        String action = (String) request.getAttribute("action");
                        if(message!=null)
                        {
                            out.println("<center><font class=\"textb\" face=\"arial\">"+message+"</font></center>");
                        }

                        Hashtable hash = (Hashtable) request.getAttribute("partnerdetails");
                        String isreadonly =(String) request.getAttribute("isreadonly");
                        String roles =(String) request.getAttribute("roles");
                        String conf = " ";
                        Hashtable innerhash = new Hashtable();
                        Hashtable temphash=null;
                        int records=0;
                        if(isreadonly.equalsIgnoreCase("view"))
                        {
                            conf = "disabled";
                        }
                        if (hash != null && hash.size() > 0)
                        {
                            String style="class=tr0";
                            innerhash = (Hashtable) hash.get(1 + "");

                            String domain = "";
                            if (functions.isValueNull(String.valueOf(innerhash.get("domain"))))
                            {
                                domain = String.valueOf(innerhash.get("domain"));
                            }
                            else
                            {
                                domain = "";
                            }
                            String partnerOrgnizationForWL_Invoice = "";
                            if (functions.isValueNull((String)innerhash.get("partnerOrgnizationForWL_Invoice")))
                            {
                                partnerOrgnizationForWL_Invoice = (String)innerhash.get("partnerOrgnizationForWL_Invoice");
                            }
                            else
                            {
                                partnerOrgnizationForWL_Invoice = "";
                            }

                            String processor_partnerid = "";
                            if (functions.isValueNull((String)innerhash.get("processor_partnerid")))
                            {
                                processor_partnerid = (String)innerhash.get("processor_partnerid");
                            }
                            else
                            {
                                processor_partnerid = "";
                            }
                            exportTransactionCron=(String)innerhash.get("exportTransactionCron");
                            String smtp_password = "";
                            if(functions.isValueNull(ESAPI.encoder().encodeForHTML((String) innerhash.get("smtp_password"))))smtp_password =ESAPI.encoder().encodeForHTML((String) innerhash.get("smtp_password"));

                            if(functions.isValueNull((String) innerhash.get("actionExecutorId")))
                            {
                                actionExecutorId= (String) innerhash.get("actionExecutorId");
                            }
                            else
                            {
                                actionExecutorId="-";
                            }
                            if(functions.isValueNull((String) innerhash.get("actionExecutorName")))
                            {
                                actionExecutorName=(String)innerhash.get("actionExecutorName");

                            }
                            else
                            {
                                actionExecutorName="-";
                            }

                    %>
                    <tr <%=style%>>
                        <td class="th0" colspan="4"><b>Partner Details</b></td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Partner Name: </td>
                        <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" size="30" name="company_name1" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("partnerName"))%>" disabled> <input type="hidden" class="txtbox1" size="30" name="company_name" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("partnerName"))%>"></td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">User Name: </td>
                        <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" size="30" name="user_name1" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("login"))%>" disabled> <input type="hidden" class="txtbox1" size="30" name="user_name" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("login"))%>" ></td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Organisation Name: <br>
                            (Use only for WL invoice)</td>
                        <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" class="txtbox1" size="30" name="partnerNameForWLInvoice" value="<%=ESAPI.encoder().encodeForHTML(partnerOrgnizationForWL_Invoice)%>" <%=conf%> onchange="ChangeFunction(this.value,'Organisation Name')"> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Processor Partner ID: <br>
                            (Use only for Merchant Payout Report)</td>
                        <td class="tr1" colspan="4" align="left"><input type="text"  class="txtbox1" size="30" id="allpid" value="<%=ESAPI.encoder().encodeForHTML(processor_partnerid)%>" name="processor_partnerid" autocomplete="on" <%=conf%> onchange="ChangeFunction(this.value,'Processor Partner ID')"> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Site URL*: <br>
                            (Ex. http://www.abc.com)</td>
                        <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" class="txtbox1" size="30" name="sitename" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("siteurl"))%>" <%=conf%>  onchange="ChangeFunction(this.value,'Site URL')"> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Domain: <br>
                            (Ex. http://www.abc.com)</td>
                        <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" class="txtbox1" size="30" name="domain" value="<%=ESAPI.encoder().encodeForHTML((String) domain)%>" <%=conf%>  onchange="ChangeFunction(this.value,'Domain')"> </td>
                    </tr>
                    <%-- <tr <%=style%>>
                         <td class="tr1">Contact Name*: </td>
                         <td class="tr1"colspan="4" align="left"><input type="text" class="txtbox1" size="30" name="contact_persons" value="<%=ESAPI.encoder().encodeForHTML((String)innerhash.get("contact_persons"))%>" <%=conf%>> </td>
                     </tr>--%>
                    <%--<tr <%=style%>>colspan="4" align="center"
                        <td class="tr1">Logo*: <br>
                            (Ex. logo.jpg / logo.jpeg)</td>
                        <td class="tr1"><input type="file" size="30" name="logoName" value="<%=ESAPI.encoder().encodeForHTML((String)innerhash.get("logoName"))%>" <%=conf%>> </td>
                    </tr>--%>
                    <%-- <tr <%=style%>>
                         <td class="tr1">Partner Email*: </td>
                         <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" size="30" name="contact_emails" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("contact_emails"))%>" <%=conf%>> </td>
                     </tr>--%>
                    <tr <%=style%>>
                        <td class="tr1">Support Mail ID*: </td>
                        <td class="tr1"colspan="4" align="left"><input type="text" class="txtbox1" size="30" name="supportmailid" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("companysupportmailid"))%>" <%=conf%>  onchange="ChangeFunction(this.value,'Support Mail ID')"> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Admin Mail ID*: </td>
                        <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" size="30" name="adminmailid" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("companyadminid"))%>" <%=conf%>  onchange="ChangeFunction(this.value,'Admin Mail ID')"> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Support URL*: </td>
                        <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" size="30" name="supporturl" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("supporturl"))%>" <%=conf%>  onchange="ChangeFunction(this.value,'Support URL')"> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Documentation URL*: </td>
                        <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" size="30" name="documentationurl" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("documentationurl"))%>" <%=conf%>  onchange="ChangeFunction(this.value,'Documentation URL')"> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Host URL*: </td>
                        <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" size="30" name="hosturl" value="<%=innerhash.get("hosturl")==null?"":ESAPI.encoder().encodeForHTML((String) innerhash.get("hosturl"))%>" <%=conf%>  onchange="ChangeFunction(this.value,'Host URL')"> </td>
                    </tr>
                    <%--<tr <%=style%>>--%>
                    <%--<td class="tr1">Sales Email ID*: </td>--%>
                    <%--<td class="tr1"><input type="text" class="txtbox1" size="30" name="salesemail" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("salesemail"))%>" <%=conf%>> </td>--%>
                    <%--</tr>--%>
                    <%--<tr <%=style%>>--%>
                    <%--<td class="tr1">Billing Email ID*: </td>--%>
                    <%--<td class="tr1"><input type="text" class="txtbox1" size="30" name="billingemail" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("billingemail"))%>" <%=conf%>> </td>--%>
                    <%--</tr>--%>
                    <%--<tr <%=style%>>--%>
                    <%--<td class="tr1">Notify Email ID*: </td>--%>
                    <%--<td class="tr1"><input type="text" class="txtbox1" size="30" name="notifyemail" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("notifyemail"))%>" <%=conf%>> </td>--%>
                    <%--</tr>--%>
                    <%--<tr <%=style%>>--%>
                    <%--<td class="tr1">Fraud Email ID*: </td>--%>
                    <%--<td class="tr1"><input type="text" class="txtbox1" size="30" name="fraudemail" value="<%=(String) innerhash.get("fraudemailid")==null?"":ESAPI.encoder().encodeForHTML((String) innerhash.get("fraudemailid"))%>" <%=conf%>> </td>--%>
                    <%--</tr>--%>
                    <tr <%=style%>>
                        <td class="tr1">Company From Address*: </td>
                        <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" size="30" name="companyfromaddress" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("companyfromemail"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Company From Address')"> </td>
                    </tr>
                    <%-- <tr <%=style%>>
                          <td class="tr1">Support From Address*: </td>
                          <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" size="30" name="supportfromaddress" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("supportfromemail"))%>" <%=conf%>> </td>
                      </tr>--%>
                    <tr <%=style%>>
                        <td class="tr1">SMTP Host*: </td>
                        <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" size="30" name="smtp_host" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("smtp_host"))%>" <%=conf%>  onchange="ChangeFunction(this.value,'SMTP Host')"> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">SMTP Port*: </td>
                        <td class="tr1"colspan="4" align="left" ><input type="text" class="txtbox1" size="30" name="smtp_port" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("smtp_port"))%>" <%=conf%>  onchange="ChangeFunction(this.value,'SMTP Port')"> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Relay With Authrization:</td>
                        <td class="tr1" colspan="4" align="left"><select name='relaywithauth'  id="relaywithauth"<%=conf%> onchange="updatetextbox(relaywithauth);ChangeFunction(this.value,'Relay With Authrization')">
                            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)innerhash.get("relayWithAuth"))));  %></select></td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Protocol:</td>
                        <td class="tr1" colspan="4" align="left">
                            <select name='protocol' <%=conf%>  onchange="ChangeFunction(this.value,'Protocol')">
                                <%
                                    System.out.println("Protocol::::"+innerhash.get("protocol"));
                                    if("TLS".equals(innerhash.get("protocol")))
                                    {%>
                                <option value="TLS" selected>TLS</option>
                                <option value="SSL">SSL</option>
                                <%}
                                else
                                {%>
                                <option value="SSL" selected>SSL</option>
                                <option value="TLS">TLS</option>
                                <%}%>
                            </select>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">SMS Service</td>
                        <td class="tr1" colspan="4" align="left">
                            <select name="sms_service" class="txtbox" <%=conf%> onchange="ChangeFunction(this.value,'SMS Service')"></option>
                                <%
                                    for (SmsService smsService:SmsService.values())
                                    {
                                        String selected="";
                                        if(smsService.toString().equals(innerhash.get("sms_service")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value=\""+smsService.toString()+"\" "+selected+">"+smsService.toString()+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">SMTP User*: </td>
                        <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" size="30" name="smtp_user" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("smtp_user"))%>" <%=conf%>  onchange="ChangeFunction(this.value,'SMTP User')"> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">SMTP Password*: </td>
                        <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" size="30" name="smtp_password" id="smtp_password"  onchange="updatetextbox(relaywithauth)" value="<%=smtp_password%>" <%=conf%>  onchange="ChangeFunction(this.value,'SMS Password')">
                            <input type="hidden" class="txtbox1" size="30" name="smtp_password" value="<%=smtp_password%>"></td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">SMS User: </td>
                        <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" size="30" name="sms_user" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("sms_user"))%>" <%=conf%> onchange="ChangeFunction(this.value,'SMS User')"> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">SMS Password: </td>
                        <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" size="30" name="sms_password" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("sms_password"))%>" <%=conf%>  onchange="ChangeFunction(this.value,'SMS Password')"> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">From SMS: </td>
                        <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" size="30" name="from_sms" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("from_sms"))%>" <%=conf%>  onchange="ChangeFunction(this.value,'From SMS')"> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Contact No*:<br>
                            (Allow only numeric value and [.+-#])</td>
                        <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" size="30" name="telno" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("telno"))%>" <%=conf%>  onchange="ChangeFunction(this.value,'Contact No')"> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Is IP Whitelisted: </td>
                        <td class="tr1" colspan="4" align="left"><select name='isipwhitelisted' <%=conf%>  onchange="ChangeFunction(this.value,'Is IP Whitelisted')">
                            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)innerhash.get("isIpWhitelisted"))));  %></select></td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Is CardEncryption Enable*: </td>
                        <td class="tr1" colspan="4" align="left"><select name='iscardencryptionenable' <%=conf%>  onchange="ChangeFunction(this.value,'Is CardEncryption Enable')">
                            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)innerhash.get("isCardEncryptionEnable"))));  %></select></td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Is IP whitelisted for APIs*: </td>
                        <td class="tr1" colspan="4" align="left"><select name='isipwhitelistedforAPIs' <%=conf%>  onchange="ChangeFunction(this.value,'Is IP whitelisted for APIs')">
                            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)innerhash.get("is_rest_whitelisted"))));  %></select></td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Split Payment*: </td>
                        <td class="tr1" colspan="4" align="left"><select name='splitpayment' <%=conf%>  onchange="ChangeFunction(this.value,'Split Payment')">
                            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) innerhash.get("splitpayment"))));  %></select></td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Split Payment Type*: </td>
                        <td class="tr1" colspan="4" align="left"><select name='splitpaymenttype' <%=conf%>  onchange="ChangeFunction(this.value,'Split Payment Type')">
                            <%out.println(Functions.combovalForSplitPayment(ESAPI.encoder().encodeForHTMLAttribute((String) innerhash.get("splitpaymenttype"))));  %>
                        </select></td>  </tr>
                    <tr <%=style%>>
                        <td class="tr1">Address Validation*: </td>
                        <td class="tr1" colspan="4" align="left"><select name='addressvalidation' <%=conf%>  onchange="ChangeFunction(this.value,'Address Validation')">
                            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) innerhash.get("addressvalidation"))));  %></select></td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Address Detail Display*: </td>
                        <td class="tr1" colspan="4" align="left"><select name='addressdetaildisplay' <%=conf%>  onchange="ChangeFunction(this.value,'Address Detail Display')">
                            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)innerhash.get("addressdetaildisplay"))));  %></select></td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">AutoRedirect*: </td>
                        <td class="tr1" colspan="4" align="left"><select name='autoRedirect' <%=conf%> onchange="ChangeFunction(this.value,'AutoRedirect')">
                            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)innerhash.get("autoRedirect"))));  %></select></td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Flight Mode*: </td>
                        <td class="tr1" colspan="4" align="left"><select name='flightMode' <%=conf%> onchange="ChangeFunction(this.value,'Flight Mode')">
                            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)innerhash.get("flightMode"))));  %></select></td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Template: </td>
                        <td class="tr1" colspan="4" align="left"><select name='template' <%=conf%>  onchange="ChangeFunction(this.value,'Template')">
                            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)innerhash.get("template"))));  %></select></td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Checkout Invoice: </td>
                        <td class="tr1" colspan="4" align="left"><select name='checkoutInvoice' <%=conf%>  onchange="ChangeFunction(this.value,'Checkout Invoice')">
                            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)innerhash.get("checkoutInvoice"))));  %></select></td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1"> Bank Card Limit: </td>
                        <td class="tr1" colspan="4" align="left"><select name='bankCardLimit' <%=conf%>  onchange="ChangeFunction(this.value,'Bank Card Limit')">
                            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)innerhash.get("bankCardLimit"))));  %></select></td>
                        <input type="hidden" value="<%=(String)innerhash.get("emi_configuration")%>" name="emiConfiguration">
                    </tr>
                    <%--<tr <%=style%>>
                        <td class="tr1"> Emi Configuration: </td>
                        <td class="tr1" colspan="4" align="left"><select name='emiConfiguration' <%=conf%>>
                            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)innerhash.get("emi_configuration"))));  %></select></td>
                    </tr>--%>
                    <tr <%=style%>>
                        <td class="tr1"> Export Transaction Cron: </td>
                        <td class="tr1" colspan="4" align="left"><select name="exportTransactionCron" style="width: 186px;" <%=conf%>  onchange="ChangeFunction(this.value,'Export Transaction Cron')">
                            <%if("N".equalsIgnoreCase(exportTransactionCron)){%>
                            <option value="N" selected>N</option>
                            <%}else{%>
                            <option value="N">N</option>
                            <%
                                }
                                Set timezoneSet = timezoneHash.keySet();
                                Iterator itr = timezoneSet.iterator();
                                String selected4 = "";
                                String timezonekey = "";
                                String timezonevalue = "";
                                while (itr.hasNext())
                                {
                                    timezonekey = (String) itr.next();
                                    timezonevalue = timezoneHash.get(timezonekey);
                                    if (timezonekey.equals(exportTransactionCron))
                                        selected4 = "selected";
                                    else
                                        selected4 = "";

                            %>
                            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(timezonekey)%>" <%=selected4%>><%=ESAPI.encoder().encodeForHTML(timezonevalue)%>
                            </option>
                            <%
                                }
                            %>
                        </select>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Monthly Min Commission Module:</td>
                        <td class="tr1" colspan="4" align="left"><select name='monthly_min_commission_module' <%=conf%>  onchange="ChangeFunction(this.value,'Monthly Min Commission Module')">
                            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) innerhash.get("monthly_min_commission_module")))); %></select>
                        </td>
                    </tr>

                    <tr <%=style%>>
                        <td class="tr1">Profit Share Commission Module:</td>
                        <td class="tr1" colspan="4" align="left"><select name='profitShareCommissionModule' <%=conf%>  onchange="ChangeFunction(this.value,'Profit Share Commission Module')">
                            <%
                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) innerhash.get("profit_share_commission_module")))); %></select>
                        </td>
                    </tr


                    <tr <%=style%>>
                        <td class="tr1">Country*: </td>
                        <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" size="30" name="country" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("country"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Country')"> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Address:</td>
                        <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" size="255"
                                                                        name="address"
                                                                        value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("address"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Address')">
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">ResponseType*: </td>
                        <td class="tr1" colspan="4" align="left">
                            <select name="responsetype" class="txtbox" <%=conf%> onchange="ChangeFunction(this.value,'ResponseType')"></option>
                                <%
                                    for (ResponseType responseType:ResponseType.values())
                                    {
                                        String selected="";
                                        if(responseType.toString().equals(innerhash.get("responseType")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value=\""+responseType.toString()+"\" "+selected+">"+responseType.toString()+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">ResponseLength*: </td>
                        <td class="tr1" colspan="4" align="left">
                            <select name="responselength" class="txtbox" <%=conf%> onchange="ChangeFunction(this.value,'ResponseLength')"></option>
                                <%
                                    for (ResponseLength responseLength:ResponseLength.values())
                                    {
                                        String selected="";
                                        if(responseLength.toString().equals(innerhash.get("responseLength")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value=\""+responseLength.toString()+"\" "+selected+">"+responseLength.toString()+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Flight&nbsp;Partner*: </td>
                        <td class="tr1" colspan="4" align="left">
                            <select name="isflightPartner" class="txtbox" <%=conf%> onchange="ChangeFunction(this.value,'Flight Partner')"></option>
                                <%
                                    for (Map.Entry<String,String> YNpair:ynMap.entrySet())
                                    {
                                        String selected="";
                                        if((YNpair.getKey()).equals(innerhash.get("isFlightPartner")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value=\""+(YNpair.getKey())+" \" "+selected+">"+(YNpair.getValue())+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Reporting Currency:</td>
                        <td class="tr1" colspan="4" align="left">
                            <select name="currency" class="txtbox" <%=conf%> onchange="ChangeFunction(this.value,'Reporting Currency')"></option>
                                <%
                                    for (Currency currency:Currency.values())
                                    {
                                        String selected="";
                                        if(currency.toString().equals(innerhash.get("reporting_currency")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value=\""+currency.toString()+"\" "+selected+">"+currency.toString()+"</option>");
                                    }
                                %>
                            </select>
                            <input type="hidden" name="isRefund" value="<%=innerhash.get("isRefund")%>">
                        </td>
                    </tr>
                    <%--<tr <%=style%>>
                        <td class="tr1">Is&nbsp;Refund: </td>
                        <td class="tr1" colspan="4" align="left">
                            <select name="isRefund" class="txtbox" <%=conf%> ></option>
                                <%
                                    for (Map.Entry<String,String> YNpair:ynMap.entrySet())
                                    {
                                        String selected="";
                                        if((YNpair.getKey()).equals(innerhash.get("isRefund")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value=\""+(YNpair.getKey())+" \" "+selected+">"+(YNpair.getValue())+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>--%>
                    <tr <%=style%>>
                        <td class="tr1">Tokenization Allowed: </td>
                        <td class="tr1" colspan="4" align="left">
                            <select name="isTokenizationAllowed" class="txtbox" <%=conf%> onchange="ChangeFunction(this.value,'Tokenization Allowed')"></option>
                                <%
                                    for (Map.Entry<String,String> YNpair:ynMap.entrySet())
                                    {
                                        String selected="";
                                        if((YNpair.getKey()).equals(innerhash.get("isTokenizationAllowed")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value=\""+(YNpair.getKey())+" \" "+selected+">"+(YNpair.getValue())+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Merchant Required For CardRegistration: </td>
                        <td class="tr1" colspan="4" align="left">
                            <select name="isMerchantRequiredForCardRegistration" class="txtbox" <%=conf%> onchange="ChangeFunction(this.value,'Merchant Required For CardRegistration')"></option>
                                <%
                                    for (Map.Entry<String,String> YNpair:ynMap.entrySet())
                                    {
                                        String selected="";
                                        if((YNpair.getKey()).equals(innerhash.get("isMerchantRequiredForCardRegistration")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value=\""+(YNpair.getKey())+" \" "+selected+">"+(YNpair.getValue())+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Address Required For TokenTransaction: </td>
                        <td class="tr1" colspan="4" align="left">
                            <select name="isAddressRequiredForTokenTransaction" class="txtbox" <%=conf%> onchange="ChangeFunction(this.value,'Address Required For TokenTransaction')"></option>
                                <%
                                    for (Map.Entry<String,String> YNpair:ynMap.entrySet())
                                    {
                                        String selected="";
                                        if((YNpair.getKey()).equals(innerhash.get("isAddressRequiredForTokenTransaction")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value=\""+(YNpair.getKey())+" \" "+selected+">"+(YNpair.getValue())+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Merchant Required For CardholderRegistration: </td>
                        <td class="tr1" colspan="4" align="left">
                            <select name="isMerchantRequiredForCardholderRegistration" class="txtbox" <%=conf%> onchange="ChangeFunction(this.value,'Merchant Required For CardholderRegistration')"></option>
                                <%
                                    for (Map.Entry<String,String> YNpair:ynMap.entrySet())
                                    {
                                        String selected="";
                                        if((YNpair.getKey()).equals(innerhash.get("isMerchantRequiredForCardholderRegistration")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value=\""+(YNpair.getKey())+" \" "+selected+">"+(YNpair.getValue())+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>

                    <tr <%=style%>>
                        <td class="tr1">Is IP Whitelisted For Invoice: </td>
                        <td class="tr1" colspan="4" align="left">
                            <select name="ip_whitelist_invoice" class="txtbox" <%=conf%> onchange="ChangeFunction(this.value,'Is IP Whitelisted For Invoice')"></option>
                                <%
                                    for (Map.Entry<String,String> YNpair:ynMap.entrySet())
                                    {
                                        String selected="";
                                        if((YNpair.getKey()).equals(innerhash.get("ip_whitelist_invoice")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value=\""+(YNpair.getKey())+" \" "+selected+">"+(YNpair.getValue())+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Address Validation For Invoice: </td>
                        <td class="tr1" colspan="4" align="left">
                            <select name="address_validation_invoice" class="txtbox" <%=conf%> onchange="ChangeFunction(this.value,'Address Validation For Invoice')"></option>
                                <%
                                    for (Map.Entry<String,String> YNpair:ynMap.entrySet())
                                    {
                                        String selected="";
                                        if((YNpair.getKey()).equals(innerhash.get("address_validation_invoice")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value=\""+(YNpair.getKey())+" \" "+selected+">"+(YNpair.getValue())+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>

                    <tr <%=style%>>
                        <td class="tr1">PCI Logo: </td>
                        <td class="tr1" colspan="4" align="left">
                            <select name="ispcilogo" class="txtbox" <%=conf%> onchange="ChangeFunction(this.value,'PCI Logo')">
                                <%
                                    for (Map.Entry<String,String> YNpair:ynMap.entrySet())
                                    {
                                        String selected="";
                                        if((YNpair.getKey()).equals(innerhash.get("ispcilogo")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value=\""+(YNpair.getKey())+" \" "+selected+">"+(YNpair.getValue())+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Bin Service:</td>
                        <td class="tr1" colspan="4" align="left">
                            <select name="binService" class="txtbox" <%=conf%> onchange="ChangeFunction(this.value,'Bin Service')">
                                <%
                                    for (Map.Entry<String,String> YNpair:ynMap.entrySet())
                                    {
                                        String selected="";
                                        if((YNpair.getKey()).equals(innerhash.get("binService")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value=\""+(YNpair.getKey())+" \" "+selected+">"+(YNpair.getValue())+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Signup Prevent:</td>
                        <td class="tr1" colspan="4" align="left">
                            <select name="signupPrevent" class="txtbox" <%=conf%> onchange="ChangeFunction(this.value,'Signup prevent')">
                                <%
                                    for (Map.Entry<String,String> YNpair:ynMap.entrySet())
                                    {
                                        String selected="";
                                        if((YNpair.getKey()).equals(innerhash.get("signupPrevent")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value=\""+(YNpair.getKey())+" \" "+selected+">"+(YNpair.getValue())+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Is Old Checkout Template:</td>
                        <td class="tr1" colspan="4" align="left">
                            <select name="oldcheckout" class="txtbox" <%=conf%> onchange="ChangeFunction(this.value,'Is Old Checkout Template')">
                                <%
                                    for (Map.Entry<String,String> YNpair:ynMap.entrySet())
                                    {
                                        String selected="";
                                        if((YNpair.getKey()).equals(innerhash.get("oldcheckout")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value=\""+(YNpair.getKey())+" \" "+selected+">"+(YNpair.getValue())+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>

                    <%--<tr <%=style%>>
                        <td class="tr1">Is Email Sent:</td>
                        <td class="tr1" colspan="4" align="left">
                            <select name="emailSent" class="txtbox" <%=conf%> onchange="ChangeFunction(this.value,'Is Email Sent')">
                                <%
                                    for (Map.Entry<String,String> YNpair:ynMap.entrySet())
                                    {
                                        String selected="";
                                        if((YNpair.getKey()).equals(innerhash.get("emailSent")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value=\""+(YNpair.getKey())+" \" "+selected+">"+(YNpair.getValue())+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>--%>

                    <tr <%=style%>>
                        <td class="tr1">Email Template Language: </td>
                        <td class="tr1" colspan="4" align="left"><select name='emailTemplateLang' <%=conf%> onchange="ChangeFunction(this.value,'Email Template Language')">
                            <%out.println(Functions.combovalLanguage(ESAPI.encoder().encodeForHTMLAttribute((String) innerhash.get("emailTemplateLang"))));  %></select></td>
                    </tr>

                    <tr <%=style%>>
                        <td class="tr1">Merchant Key:</td>
                        <td class="tr1" colspan="4" align="left">
                            <select name="isMerchantKey" class="txtbox" <%=conf%> onchange="ChangeFunction(this.value,'Merchant Key')">
                                <%
                                    for (Map.Entry<String,String> YNpair:ynMap.entrySet())
                                    {
                                        String selected="";
                                        if((YNpair.getKey()).equals(innerhash.get("isMerchantKey")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value=\""+(YNpair.getKey())+" \" "+selected+">"+(YNpair.getValue())+"</option>");
                                    }
                                %>
                                <%--<%
                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isMerchantKey"))));
                                %>--%>
                            </select>
                        </td>
                    </tr>

                    <tr <%=style%>>
                        <td class="tr1">Token Valid Days*: </td>
                        <td class="tr1"colspan="4" align="left" ><input type="text" class="txtbox1" size="30" name="tokenValidDays" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("tokenValidDays"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Token Valid Dayse')"> </td>
                    </tr>

                    <tr <%=style%>>
                        <td class="tr1">Logo Name: </td>
                        <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" size="30" name="logoName" value="<%=functions.isValueNull(ESAPI.encoder().encodeForHTML((String) innerhash.get("logoName")))?ESAPI.encoder().encodeForHTML((String) innerhash.get("logoName")):""%>"<%=conf%> onchange="ChangeFunction(this.value,'Logo Name')">
                        </td>
                    </tr><tr <%=style%>>
                    <td class="tr1">Logo Height: </td>
                    <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" size="30" name="logoHeight"  maxlength="3" object-fit: contain;
                                                                    onkeypress="return isNumberKey(event)"
                                                                    value="<%=functions.isValueNull(ESAPI.encoder().encodeForHTML((String) innerhash.get("logoheight")))?ESAPI.encoder().encodeForHTML((String) innerhash.get("logoheight")):""%>"<%=conf%> onchange="ChangeFunction(this.value,'Logo Height')">
                    </td>
                </tr>
                    <tr <%=style%>>
                        <td class="tr1">Logo Width: </td>
                        <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" size="30" name="logoWidth"  maxlength="3" object-fit: contain;
                                                                        onkeypress="return isNumberKey(event)"
                                                                        value="<%=functions.isValueNull(ESAPI.encoder().encodeForHTML((String) innerhash.get("logowidth")))?ESAPI.encoder().encodeForHTML((String) innerhash.get("logowidth")):""%>"<%=conf%> onchange="ChangeFunction(this.value,'Logo Width')">
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Partner Short Name:</td>
                        <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" size="30"
                                                                        name="partner_Short_Name"
                                                                        value="<%=functions.isValueNull(ESAPI.encoder().encodeForHTML((String) innerhash.get("partner_short_name")))?ESAPI.encoder().encodeForHTML((String) innerhash.get("partner_short_name")):""%>"<%=conf%> onchange="ChangeFunction(this.value,'Partner Short Name')">
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Role:</td>
                        <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" size="30"
                                                                        name="roles"
                                                                        value="<%=functions.isValueNull(roles)?roles:"" %>"disabled>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Partner Theme: </td>
                        <td class="tr1" colspan="4" align="left">
                            <select name="defaulttemplatetheme" class="txtbox" value="" style="width: 67%;" <%=conf%> onchange="ChangeFunction(this.value,'Partner Theme')">
                                <option value="" selected>--Select Theme--</option>
                                <%
                                    String selected = "";
                                    String key= "";
                                    for (String themename : thememap.keySet())
                                    {
                                        key = String.valueOf(themename);
                                        if (key.equals(innerhash.get("default_theme")))
                                            selected ="selected";
                                        else
                                            selected="";
                                %>
                                <option value="<%=key%>" <%=selected%>><%=key%>
                                </option>
                                <%
                                    }
                                %>
                            </select>
                        </td>
                    </tr>
                    <%--<tr><td colspan="4">&nbsp;</td></tr>--%>
                    <tr <%=style%>>
                        <td class="tr1">Support Email for Transaction Mail</td>
                        <td class="tr1" colspan="4" align="left">
                            <select name="supportemailfortransactionmail" class="txtbox" <%=conf%> onchange="ChangeFunction(this.value,'Support Email for Transaction Mail')">
                                <%
                                    if("Merchant".equals(innerhash.get("support_email_for_transaction_mail")))
                                    {%>
                                <option value="Partner">Partner</option>
                                <option value="Merchant" selected>Merchant</option>
                                <%}
                                else
                                {%>
                                <option value="Partner" selected>Partner</option>
                                <option value="Merchant">Merchant</option>
                                <%}%>
                            </select>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Terms URL: </td>
                        <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" size="30" name="termsUrl" value="<%=innerhash.get("termsUrl")==null?"":ESAPI.encoder().encodeForHTML((String) innerhash.get("termsUrl"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Terms URL')"> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Privacy URL: </td>
                        <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" size="30" name="privacyUrl" value="<%=innerhash.get("privacyUrl")==null?"":ESAPI.encoder().encodeForHTML((String) innerhash.get("privacyUrl"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Privacy URL')"> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Cookies URL: </td>
                        <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" size="30" name="cookiesUrl" value="<%=innerhash.get("cookiesUrl")==null?"":ESAPI.encoder().encodeForHTML((String) innerhash.get("cookiesUrl"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Cookies URL')"> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Action Executor Id :</td>
                        <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" size="30" name="actionExecutorId" value="<%=ESAPI.encoder().encodeForHTML(actionExecutorId)%>" disabled> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Action Executor Name : </td>
                        <td class="tr1" colspan="4" align="left"><input type="text" class="txtbox1" size="30" name="actionExecutorName" value="<%=ESAPI.encoder().encodeForHTML(actionExecutorName)%>" disabled> </td>
                    </tr>
                    <td  colspan="4" align="center" class="textb" style="margin-right: 50%"><h5><b><u>Contact Details</u></b></h5></td>

                    <tr class="tr1" <%=style%>>
                        <td class="textb">Main Contact*:</td>
                        <td>
                            <input class="txtbox" type="Text" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("contact_persons"))!=null?ESAPI.encoder().encodeForHTML((String) innerhash.get("contact_persons")):""%>" name="contact_persons" size="35" placeholder="Name" <%=conf%> onchange="ChangeFunction(this.value,'Main Contact')">
                        </td>
                        <td>
                            <input class="txtbox" type="Text" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("contact_emails"))!=null?ESAPI.encoder().encodeForHTML((String) innerhash.get("contact_emails")):""%>" name="contact_emails" size="35" placeholder="Email*" <%=conf%> onchange="ChangeFunction(this.value,'Main Contact Email')">
                        </td>

                        <td>
                            <input class="txtbox" type="Text" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("contact_ccmailid"))!=null?ESAPI.encoder().encodeForHTML((String) innerhash.get("contact_ccmailid")):""%>" name="contact_ccmailid" size="35" placeholder="Email Cc" <%=conf%> onchange="ChangeFunction(this.value,'Main Contact EmailCc')">
                        </td>
                    </tr>

                    <tr class="tr1" <%=style%>>
                        <td class="textb">Sales Contact:</td>
                        <td>
                            <input class="txtbox" type="Text" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("salescontactname"))!=null?ESAPI.encoder().encodeForHTML((String) innerhash.get("salescontactname")):""%>" name="sales_contactperson" size="35" placeholder="Name" <%=conf%> onchange="ChangeFunction(this.value,'Sales Contact')">
                        </td>
                        <td>
                            <input class="txtbox" type="Text" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("salesemail"))!=null?ESAPI.encoder().encodeForHTML((String) innerhash.get("salesemail")):""%>" name="salesemail" size="35" placeholder="Email*" <%=conf%> onchange="ChangeFunction(this.value,'Sales Contact Email')">
                        </td>

                        <td>
                            <input class="txtbox" type="Text" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("sales_ccemailid"))!=null?ESAPI.encoder().encodeForHTML((String) innerhash.get("sales_ccemailid")):""%>" name="sales_ccemailid" size="35" placeholder="Email Cc" <%=conf%> onchange="ChangeFunction(this.value,'Sales Contact EmailCc')">
                        </td>
                    </tr>
                    <tr class="tr1" <%=style%>>
                        <td class="textb">Billing Contact:</td>
                        <td>
                            <input class="txtbox" type="Text" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("billingcontactname"))!=null?ESAPI.encoder().encodeForHTML((String) innerhash.get("billingcontactname")):""%>" name="billing_contactperson" size="35" placeholder="Name" <%=conf%> onchange="ChangeFunction(this.value,'Billing Contact')">
                        </td>
                        <td>
                            <input class="txtbox" type="Text" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("billingemail"))!=null?ESAPI.encoder().encodeForHTML((String) innerhash.get("billingemail")):""%>" name="billingemail" size="35" placeholder="Email*" <%=conf%> onchange="ChangeFunction(this.value,'Billing Contact Email')">
                        </td>

                        <td>
                            <input class="txtbox" type="Text" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("billing_ccemailid"))!=null?ESAPI.encoder().encodeForHTML((String) innerhash.get("billing_ccemailid")):""%>" name="billing_ccemailid" size="35" placeholder="Email Cc" <%=conf%> onchange="ChangeFunction(this.value,'Billing Contact EmailCc')">
                        </td>
                    </tr>
                    <tr class="tr1" <%=style%>>
                        <td class="textb">Notify Contact:</td>
                        <td>
                            <input class="txtbox" type="Text" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("notifycontactname"))!=null?ESAPI.encoder().encodeForHTML((String) innerhash.get("notifycontactname")):""%>" name="notify_contactperson" size="35" placeholder="Name" <%=conf%> onchange="ChangeFunction(this.value,'Notify Contact')">
                        </td>
                        <td>
                            <input class="txtbox" type="Text" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("notifyemail"))!=null?ESAPI.encoder().encodeForHTML((String) innerhash.get("notifyemail")):""%>" name="notifyemail" size="35" placeholder="Email*" <%=conf%> onchange="ChangeFunction(this.value,'Notify Contact Email')">
                        </td>

                        <td>
                            <input class="txtbox" type="Text" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("notify_ccemailid"))!=null?ESAPI.encoder().encodeForHTML((String) innerhash.get("notify_ccemailid")):""%>" name="notify_ccemailid" size="35" placeholder="Email Cc" <%=conf%> onchange="ChangeFunction(this.value,'Notify Contact EmailCc')">
                        </td>
                    </tr>
                    <tr class="tr1" <%=style%>>
                        <td class="textb">Fraud Contact:</td>
                        <td>
                            <input class="txtbox" type="Text" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("fraudcontactname"))!=null?ESAPI.encoder().encodeForHTML((String) innerhash.get("fraudcontactname")):""%>" name="fraud_contactperson" size="35" placeholder="Name" <%=conf%> onchange="ChangeFunction(this.value,'Fraud Contact')">
                        </td>
                        <td>
                            <%--
                                        <input class="txtbox" type="Text" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("fraudemailid"))!=null?ESAPI.encoder().encodeForHTML((String) innerhash.get("fraudemailid")):""%>" name="fraudemailid" size="35" placeholder="Email*"><%=conf%>
                            --%>


                            <input class="txtbox" type="Text" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("fraudemailid"))!=null?ESAPI.encoder().encodeForHTML((String) innerhash.get("fraudemailid")):""%>" name="fraudemail" size="35" placeholder="Email*" <%=conf%> onchange="ChangeFunction(this.value,'Fraud Contact Email')">
                        </td>

                        <td>
                            <input class="txtbox" type="Text" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("fraud_ccemailid"))!=null?ESAPI.encoder().encodeForHTML((String) innerhash.get("fraud_ccemailid")):""%>" name="fraud_ccemailid" size="35" placeholder="Email Cc" <%=conf%> onchange="ChangeFunction(this.value,'Fraud Contact EmailCc')">
                        </td>
                    </tr>
                    <tr class="tr1" <%=style%>>
                        <td class="textb">Chargeback Contact:</td>
                        <td>
                            <input class="txtbox" type="Text" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("chargebackcontactname"))!=null?ESAPI.encoder().encodeForHTML((String) innerhash.get("chargebackcontactname")):""%>" name="cbcontact_name" size="35" placeholder="Name" <%=conf%> onchange="ChangeFunction(this.value,'Chargeback Contact')">
                        </td>
                        <td>
                            <input class="txtbox" type="Text" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("chargebackemailid"))!=null?ESAPI.encoder().encodeForHTML((String) innerhash.get("chargebackemailid")):""%>" name="cbcontact_mailid" size="35" placeholder="Email" <%=conf%> onchange="ChangeFunction(this.value,'Chargeback Contact Email')">
                        </td>

                        <td>
                            <input class="txtbox" type="Text" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("chargeback_ccemailid"))!=null?ESAPI.encoder().encodeForHTML((String) innerhash.get("chargeback_ccemailid")):""%>" name="chargeback_ccemailid" size="35" placeholder="Email Cc" <%=conf%> onchange="ChangeFunction(this.value,'Chargeback Contact EmailCc')">
                        </td>
                    </tr>
                    <tr class="tr1" <%=style%>>
                        <td class="textb">Refund Contact:</td>
                        <td>
                            <input class="txtbox" type="Text" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("refundcontactname"))!=null?ESAPI.encoder().encodeForHTML((String) innerhash.get("refundcontactname")):""%>" name="refundcontact_name" size="35" placeholder="Name" <%=conf%> onchange="ChangeFunction(this.value,'Refund Contact')">
                        </td>
                        <td>
                            <input class="txtbox" type="Text" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("refundemailid"))!=null?ESAPI.encoder().encodeForHTML((String) innerhash.get("refundemailid")):""%>" name="refundcontact_mailid" size="35" placeholder="Email" <%=conf%> onchange="ChangeFunction(this.value,'Refund Contact Email')">
                        </td>
                        <td>
                            <input class="txtbox" type="Text" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("refund_ccemailid"))!=null?ESAPI.encoder().encodeForHTML((String) innerhash.get("refund_ccemailid")):""%>" name="refund_ccemailid" size="35" placeholder="Email Cc" <%=conf%> onchange="ChangeFunction(this.value,'Refund Contact EmailCc')">
                        </td>
                    </tr>
                    <tr class="tr1" <%=style%>>
                        <td class="textb">Technical Contact</td>
                        <td>
                            <input class="txtbox" type="Text" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("technicalcontactname"))!=null?ESAPI.encoder().encodeForHTML((String) innerhash.get("technicalcontactname")):""%>" name="technicalcontact_name" size="35" placeholder="Name" <%=conf%> onchange="ChangeFunction(this.value,'Technical Contact')">
                        </td>
                        <td>
                            <input class="txtbox" type="Text" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("technicalemailid"))!=null?ESAPI.encoder().encodeForHTML((String) innerhash.get("technicalemailid")):""%>" name="technicalcontact_mailid" size="35" placeholder="Email" <%=conf%> onchange="ChangeFunction(this.value,'Technical Contact Email')">
                        </td>

                        <td>
                            <input class="txtbox" type="Text" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("technical_ccemailid"))!=null?ESAPI.encoder().encodeForHTML((String) innerhash.get("technical_ccemailid")):""%>" name="technical_ccemailid" size="35" placeholder="Email Cc" <%=conf%> onchange="ChangeFunction(this.value,'Technical Contact EmailCc')">
                        </td>
                    </tr>

                    <%
                            out.println("<tr>");
                            /*out.println("<td></td>");*/
                            out.println("<td colspan=3 align=center><input type=\"hidden\" name=\"partnerid\" value=\""+partnerid+"\"><input type=\"hidden\" name=\"action\" value=\""+action+"\"><input type=\"submit\" class=\"gotopage\" name=\"modify\" value=\"Save\" "+conf+"></td>");
                            /*out.println("<td></td>");*/
                            out.println("</tr>");
                        }
                    %>
                </table>
            </form>
        </div>
    </div>
</div>
<%
        //out.println(Functions.ShowMessage("Message","No record found"));
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>
</body>
</html>