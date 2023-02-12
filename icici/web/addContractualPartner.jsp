<%@ page import="com.directi.pg.Functions,com.directi.pg.Logger"%>
<%@ page import="com.manager.ApplicationManager" %>
<%@ page import="com.manager.ApplicationManagerService" %>
<%@ page import="com.payment.exceptionHandler.PZDBViolationException" %>
<%@ page import="com.vo.applicationManagerVOs.BankTypeVO" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.TreeMap" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 13/7/15
  Time: 4:50 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <title>Partner Details</title>

    <script type="">
        function isNumber(evt) {
            evt = (evt) ? evt : window.event;
            var charCode = (evt.which) ? evt.which : evt.keyCode;
            if (charCode > 31 && (charCode < 48 || charCode > 57)) {
                return false;
            }
            return true;
        }
    </script>

</head>
<script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-accountid.js"></script>
<body>
<%
    Logger log = new Logger("addContractualPartner.jsp");
    Functions functions= new Functions();

    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >

            <div class="panel-heading" >
                Add Contractual Partner
                <div style="float: right;">
                    <form action="/icici/contractualPartner.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" value="Contractual Partner" name="submit" class="addnewmember" style="width:100px ">
                            <i class="fa fa-arrow-left"></i>
                            &nbsp;&nbsp;Go Back
                        </button>
                    </form>
                </div>
            </div><br>

            <%
               String partnerId = Functions.checkStringNull(request.getParameter("partnerId"));
                String bankName = Functions.checkStringNull(request.getParameter("bankName"));
                String str="ctoken=" + ctoken;

                if(partnerId!=null)str = str + "&partnerid=" + partnerId;
                if(bankName!=null)str = str + "&bankName=" + bankName;
                ApplicationManager applicationManager = new ApplicationManager();

                try
                {
                    TreeMap<String,String> partnerMap = applicationManager.selectPartnerIdAndPartnerName();
            /*ArrayList<GatewayType> gatewayTypes = GatewayTypeService.getAllGatewayTypes();*/

                    TreeMap<String,BankTypeVO> bankTypeTreeMap = ApplicationManagerService.getAllPartnerBankTypeMap();

                    TreeMap<Integer,String> partneriddetails = applicationManager.getpartnerDetails();
                    String partnerid = "";
            %>
            <table  align="center" width="70%" cellpadding="2" cellspacing="2">
                <form action="/icici/servlet/AddContractualPartner?ctoken=<%=ctoken%>" method="post" name="forms" >
                    <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>" >

                    <%
                        String error=(String)request.getAttribute("error");
                        //System.out.println("ErrorMsg----"+error);
                        if(error!=null)
                        {
                            out.println("<center><font class=\"textb\"><b>"+error+"</b></font></center>");
                        }
                    %>


                    <tr>
                        <td>

                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center" >

                                <tr><td colspan="4">&nbsp;</td></tr>

                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Partner ID*</td>
                                    <td class="textb">:</td>
                                    <td class="textb">
                                        <select size="1" name="partnerId" id="bank" class="txtbox">
                                            <option value="">Select Partner ID</option>

                                            <%
                                                if(partneriddetails.size()>0)
                                                {
                                                    Iterator iterator = partneriddetails.entrySet().iterator();

                                                    while (iterator.hasNext())
                                                    {
                                                        Map.Entry<Integer, String> mapEntry = (Map.Entry<Integer, String>) iterator.next();
                                                        String key = String.valueOf(mapEntry.getKey());
                                                        String value = mapEntry.getValue();

                                                        String selected = "";
                                                        if(key.equalsIgnoreCase(partnerId))
                                                            selected = "selected";
                                                        else
                                                            selected = "";
                                            %>
                                            <option value="<%=key%>" <%=selected%> ><%=key%>-<%=value%></option>
                                            <%
                                                    }
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>
                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Bank Name*</td>
                                    <td class="textb">:</td>
                                    <td class="textb">
                                        <select size="1" id="accountid" name="bankName" class="txtbox"  style="width:200px;">
                                            <option data-bank="all" value="0" default>--All--</option>
                                            <%
                                                for(String gatewayType : bankTypeTreeMap.keySet())
                                                {
                                                    BankTypeVO gt = bankTypeTreeMap.get(gatewayType);
                                                    String gatewayPartner = gt.getPartnerId();
                                                    String gatewayValue = gt.getBankName();

                                                    String isSelected = "";
                                                    if(gatewayValue.equalsIgnoreCase(bankName)){isSelected = "selected";}
                                                    //System.out.println("gatewayType-->"+gatewayType);

                                            %>
                                            <option data-bank="<%=gatewayPartner%>" value="<%=gatewayValue%>" <%=isSelected%>> <%=gatewayValue+ "-" +gatewayPartner%> </option>
                                            <%
                                                }
                                            %>

                                        </select>
                                    </td>
                                </tr>
                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Contractual Partner ID</td>
                                    <td class="textb">:</td>
                                    <td class="textb">
                                        <input size="10" name="contractualpartid" maxlength="10" type="tel" onkeypress="return isNumber(event)"  class="txtbox">
                                    </td>
                                </tr>
                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Contractual Partner Name</td>
                                    <td class="textb">:</td>
                                    <td class="textb">
                                        <input type="text" name="contractualpartname" maxlength="100"  class="txtbox">
                                    </td>
                                </tr>

                                <tr><td colspan="4">&nbsp;</td></tr>

                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb"></td>
                                    <td class="textb"></td>
                                    <td>
                                        <input type="Submit" value="Save" class="buttonform">
                                    </td>
                                </tr>
                                <tr><td colspan="4">&nbsp;</td></tr>

                            </table>

                        </td>
                    </tr>
                </form>
            </table>
        </div>
    </div>
</div>

<%
    }
    catch (PZDBViolationException e)
    {
        out.println("<div class=\"reporttable\">");
        out.println(Functions.NewShowConfirmation("Result","Internal Error occurred : Please contact your Admin"));
        out.println("</div>");
    }
%>
<%
    if(functions.isValueNull((String)request.getAttribute("status")))
    {
        out.println("<div class=\"reporttable\">");
        out.println(Functions.NewShowConfirmation("Result",(String)request.getAttribute("status")));
        out.println("</div>");
    }
%>

</body>
</html>