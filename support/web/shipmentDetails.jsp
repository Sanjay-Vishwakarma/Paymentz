<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>SHIPMENT DETAILS</title>
    <script>

    </script>
</head>
<body  class="bodybackground">
<%@ include file="custSuppDash.jsp"%>
<%!String classname="shipmentDetails.jsp";%>

<%  HashMap shipmentdetails;
    session.setAttribute("submit","UPDATE SHIPMENT DETAILS");
    shipmentdetails = (HashMap) request.getAttribute("shipmetdetails");
    String check,str;
    str="ctoken="+ctoken;
    if(request.getAttribute("check")==null){
        check="";
    }else{
        check= (String) request.getAttribute("check");}
    Hashtable statushash=new Hashtable();
    statushash.put("Procesed","Processed");
    statushash.put("Dispatched","Dispatched");
    statushash.put("Shipped","Shipped");
    statushash.put("Local","Local");
    HashMap inn;
    String error="";
    String blank="";
    String success="";
    int records = 0;
    int totalrecords = 0;
    int currentblock = 1;
    try
    {
//        records = Integer.parseInt((String) tranDetail.get("records"));
        records=shipmentdetails.size();
        totalrecords = Integer.parseInt((request.getAttribute("totalrecords").toString()));
        currentblock = Integer.parseInt(request.getParameter("currentblock").toString());
    }
    catch (Exception ex)
    {
    }
    if(shipmentdetails!=null)
    {
        shipmentdetails.remove("records");
        shipmentdetails.remove("totalrecords");
    }
    int pageno =1;
    int pagerecords=30;
    try
    {
        pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Spageno",request.getParameter("SPageno"),"Numbers",3,true), 1);
        pagerecords = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Srecord",request.getParameter("SRecords"),"Numbers",3,true), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));

    }
    catch(ValidationException e)
    {
        pageno = 1;
        pagerecords = 30;
    }
    int srNo=(pageno-1)*pagerecords;
%>
<br><br><br><br><br>

     <br><br>

    <div class="reporttable">
        <form  action="/support/servlet/Updateshipment?ctoken=<%=ctoken%>" method="post">
            <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <%
            if("no".equals(check))

            {
                out.println("<font class=\"textb\" size=\"2\"><b>PLEASE CHECK IN THE CORRESPONDING TRACKING ID</b></font> ");
            }
            if(!(request.getAttribute("error")).equals(""))
            {
                error= (String) request.getAttribute("error");
                out.println("<font class=\"textb\"><b> Invalid data in</b></font>");
                error=error.replace("&lt;BR&gt;","<BR>");
                out.println("<font class=\"textb\" size=\"2\"><b><br>"+error+"</b></font>");
            }
            if(!(request.getAttribute("blank")).equals(""))
            {
                blank= (String) request.getAttribute("blank");
                out.println("<font class=\"textb\" size=\"2\"><b>Values for the selected record not filled properly</b></font>");
                error=error.replace("&lt;BR&gt;","<BR>");
                out.println("<font class=\"textb\" size=\"2\"><b><br>"+blank+"</b></font>");

            }

            if(!(request.getAttribute("success")).equals(""))
            {
                success= (String) request.getAttribute("success");
                out.println("<font class=\"textb\" size=2><b>Updated Successfully for</b></font>");
                error=error.replace("&lt;BR&gt;","<BR>");
                out.println("<font class=\"textb\" size=2><b><br>"+success+"</b></font>");
            }
        %>
        <%
            if(shipmentdetails!=null && totalrecords!=0 && records>0)
            {
        %>
    <table  border="0" cellspacing="0" cellpadding="2" bordercolor="#FFFFFF" color="#FFFFFF" width="100%">
        <tr>
            <td>
                <table border="0" cellpadding="5" cellspacing="2" width="100%" color="#ffffff"  class="table table-striped table-bordered table-hover table-green dataTable">
                    <thead>
                    <tr>
                        <td width="2%" class="th0">Sr No</td>
                        <td width="4%" class="th0">Check</td>
                        <td width="13%" class="th1">Tracking Id</td>
                        <td width="11%" class="th0">Order Id</td>
                        <td width="17%" class="th1">Merchant Id</td>
                        <td width="25%" class="th0">CardHolder's&nbsp;Name</td>
                        <td width="8%" class="th1">Amount</td>
                        <td width="10%" class="th0">POD</td>
                        <td width="10%" class="th0">Pod Batch</td>
                        <td width="20%" class="th0">Status Of Shipment</td>
                    </tr>
                    </thead>
                    <%

                        for(int i =0; i<shipmentdetails.size();i++)
                        {

                            inn=(HashMap)shipmentdetails.get(i);
                            String trackingId = inn.get("trackingid").toString();
                            String OrderId =inn.get("description").toString();
                            String MerchantId=inn.get("merchantid").toString();
                            String customername=inn.get("customername").toString();
                            String amount=inn.get("amount").toString();

                    %>
                    <tbody>
                    <tr style="font-size: 15px">
                        <td  height="30px" width="2%" style="font-size: 12px"class="tr1" align="CENTER"><%=srNo+1%></td>
                        <td  height="30px" width="8%"class="tr1" align="CENTER"><center><input  type="checkbox" name="checkbox" value="<%=trackingId%>" ></center></td>
                        <td  height="30px" width="9%" style="font-size: 12px"class="tr1" align="CENTER"><%=trackingId%></td>
                        <td  height="30px" width="11%" style="font-size: 12px"class="tr1" align="CENTER"><%=OrderId%></td>
                        <td  height="30px" width="10%" style="font-size: 12px"class="tr1" align="CENTER"><%=MerchantId%></td>
                        <td  height="30px" width="12%" style="font-size: 12px"class="tr1" align="CENTER"><%=customername%></td>
                        <td  height="30px" width="9%" style="font-size: 12px"class="tr1" align="CENTER"><%=amount%></td>
                        <td  height="30px" width="9%"class="tr1" align="CENTER"><input type="text" value="" name="pod<%=trackingId%>" size="15" class="txtbox" style="font-size: 12px;width: 130px"> </td>
                        <td  height="30px" width="9%"class="tr1" align="CENTER"><input type="text" value="" name="podbatch<%=trackingId%>" size="15" class="txtbox" style="font-size: 12px;width: 130px"></td>
                        <td  height="30px" width="12%"class="tr1" align="CENTER"><select name="shipstatus<%=trackingId%>" class="txtbox" style="font-size: 12px;width:130px">
                            <option value="">NONE</option>
                            <%
                                Enumeration enu = statushash.keys();
                                String key = "";
                                String value = "";


                                while (enu.hasMoreElements())
                                {
                                    key = (String) enu.nextElement();
                                    value = (String) statushash.get(key);

                            %>
                            <option value=<%=key%>><%=value%></option>
                            <%
                                }
                            %>
                        </select></td>

                    </tr></tbody>
                    <%
                      srNo++;
                        }
                    %>
                    <thead >
                    <td  align="left" class="th0">Total Records : <%=totalrecords%></td>
                    <td  align="right" class="th0">Page No : <%=pageno%></td>
                    <td class="th0">&nbsp;</td>
                    <td class="th0">&nbsp;</td>
                    <td class="th0">&nbsp;</td>
                    <td class="th0">&nbsp;</td>
                    <td class="th0">&nbsp;</td>
                    <td class="th0">&nbsp;</td>
                    <td class="th0">&nbsp;</td>
                    <td class="th0">&nbsp;</td>
                    </thead>
                    <tr>&nbsp;</tr>
                </table>
                <br>
                <table style="margin-left: 85%">
                    <td width="130px" class="textb" align="right">
                        <button type="submit" class="buttonform"name="B1" style="width: 130px">
                            <span><i class="fa fa-clock-o" style="float: left"></i></span>
                            Update Shipment</button>
                    </td>
                </table>
            </td>
        </tr>
        </table>

</form>
                <div>
                    <jsp:include page="page.jsp" flush="true">
                        <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                        <jsp:param name="numrows" value="<%=pagerecords%>"/>
                        <jsp:param name="pageno" value="<%=pageno%>"/>
                        <jsp:param name="str" value="<%=str%>"/>
                        <jsp:param name="page" value="ShipmentDetails"/>
                        <jsp:param name="currentblock" value="<%=currentblock%>"/>
                        <jsp:param name="orderby" value=""/>
                    </jsp:include>
                </div></td></tr></table>
    <%
        }
        else
        {
            out.println(Functions.NewShowConfirmation("Sorry","No records found."));
        }
    %>
</div>




</body>
</html>