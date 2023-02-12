<%@ page import="com.directi.pg.Functions,org.owasp.esapi.ESAPI"%>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="java.util.*" %>
<%@ page import="com.manager.vo.gatewayVOs.GatewayTypeVO" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.manager.dao.MerchantDAO" %>
<%@ page import="com.manager.GatewayManager" %>
<%@ page import="com.manager.vo.gatewayVOs.GatewayAccountVO" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ include file="index.jsp"%>
<%@ include file="functions.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 23/7/15
  Time: 6:53 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title> Merchant Random Charges</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  Logger logger=new Logger("randomChargesInMerchantWire");
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading" >
        Bank Wire Manager
        <div style="float: right;">
          <form action="/icici/bankWireManager.jsp?ctoken=<%=ctoken%>" method="POST">

            <button type="submit" class="addnewmember" value="Add" name="submit" style="width:300px ">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add Bank Wire Manager
            </button>
          </form>
        </div>
      </div>
      <%
        int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
        int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

        if(request.getParameter("MES")!=null)
        {
          String mes=request.getParameter("MES");
          if(mes.equals("ERR"))
          {
            ValidationErrorList error= (ValidationErrorList) request.getAttribute("error");
            for(Object errorList : error.errors())
            {
              ValidationException ve = (ValidationException) errorList;
              out.println("<center><font class=\"textb\">" + ve.getMessage() + "</font></center>");
            }
          }

        }
      %>
      <form action="/icici/servlet/BankWireManagerList?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <%!
          private static Logger logger = new Logger("test");
          MerchantDAO merchantDAO = new MerchantDAO();
        %>
        <%
          GatewayManager gatewayManager = new GatewayManager();


          List<GatewayTypeVO> gatewayTypeVOList=gatewayManager.getListOfAllGatewayType();
          List<GatewayAccountVO> gatewayAccountVOList= gatewayManager.getListOfAllGatewayAccount();

          String selected="";
          HashMap<String,String> dropdown = new HashMap<String, String>();
          dropdown.put("N","NO");
          dropdown.put("Y","YES");

          try
          {
            session.setAttribute("submit","Reports");
            String fromdate = null;
            String todate = null;
            try
            {
              Date date = new Date();
              SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

              String Date = originalFormat.format(date);

              fromdate = Functions.checkStringNull(request.getParameter("fromdate")) == null ?"" : request.getParameter("fromdate");
              todate = Functions.checkStringNull(request.getParameter("todate")) == null ? "" : request.getParameter("todate");
            }
            catch (Exception e)
            {
              logger.error("JSP page exception ::",e);
            }

        %>
        <table  align="center" width="100%" cellpadding="2" cellspacing="2" style="margin-left:1%;">

          <tr>
            <td>

              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">


                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >From</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input size="6" name="fromdate" readonly class="datepicker" style="width: 142px;height: 25px;" value="<%=fromdate%>">

                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >To</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input size="6" name="todate" style="width: 142px;height: 25px;" readonly class="datepicker" value="<%=todate%>">

                  </td>
                  <td width="6%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Gateway</td>
                  <td width="3%" class="textb"></td>
                  <td width="10%" class="textb">
                    <select name="pgtypeid" class="txtbox"><option value="" selected></option>
                      <%
                        for(GatewayTypeVO gatewayTypeVO : gatewayTypeVOList)
                        {
                          GatewayType gatewayType = gatewayTypeVO.getGatewayType();
                          String gateway=gatewayType.getGateway();
                          String currency=gatewayType.getCurrency();
                          out.println("<option value=\""+gatewayType.getPgTypeId()+"\" >"+gateway+"-"+currency+"</option>");
                        }
                      %>
                    </select>
                  </td>
                  </td>

                </tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>
                </tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >Account Id</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb" colspan="5">
                    <select name="accountid"><option value="" selected></option>
                      <%
                        for(GatewayAccountVO gatewayAccountVO : gatewayAccountVOList)
                        {
                          GatewayAccount gatewayAccount = gatewayAccountVO.getGatewayAccount();
                          int accountId  = gatewayAccount.getAccountId();
                          String merchantId = gatewayAccount.getMerchantId();
                          String displayName = gatewayAccount.getDisplayName();
                          out.println("<option value=\""+accountId+"\" >"+accountId+"-"+merchantId+"-"+displayName+"</option>");
                        }
                      %>
                    </select>
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb"></td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <button type="submit" class="buttonform" >
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb"></td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">

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
<div class="reporttable">
  <%
    Functions functions=new Functions();
    Hashtable hash = (Hashtable)request.getAttribute("transdetails");
    Hashtable temphash=null;
    int records=0;
    int totalrecords=0;

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
      hash = (Hashtable)request.getAttribute("transdetails");
    }
    if(records>0)
    {
  %>
  <div id="containrecord" align="center">
    <h4>Random Charges Applied In BankWire</h4>
  </div>
  <table align=center width="70%" class="table table-striped table-bordered table-hover table-green dataTable">
    <thead>
    <tr>
      <td width="4%" class="th0">Sr No</td>
      <td width="5%"  class="th0">Merchant ID</td>
      <td width="5%" class="th1">Terminal ID</td>
      <td width="15%" class="th0">Charge Name</td>
      <td width="4%" class="th1">Charge Rate</td>
      <td width="4%" class="th1">Charge Counter</td>
      <td width="4%" class="th1">Charge Amount</td>
      <td width="4%" class="th1">Charge Value</td>
      <td width="5%" class="th1">Charge Value Type</td>
      <td width="20%" class="th1">Charge Remark</td>
    </tr>
    </thead>
      <%
        String style="class=td1";
        String ext="light";
        for(int pos=1;pos<=records;pos++)
        {
            String id=Integer.toString(pos);
            int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);
            if(pos%2==0)
            {
                style="class=tr0";
                ext="dark";
            }
            else
            {
                style="class=tr1";
                ext="light";
            }
            temphash=(Hashtable)hash.get(id);
            out.println("<tr>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+srno+ "</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("memberid"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("terminalid"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargename"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargerate"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargecounter"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargeamount"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargevalue"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("valuetype"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargeremark"))+"</td>");
            out.println("</tr>");
        }
    }
      else if(functions.isValueNull((String)request.getAttribute("statusMsg")))
      {
      out.println(Functions.NewShowConfirmation("Result",(String)request.getAttribute("statusMsg")));
      }
      else
      {
         out.println(Functions.NewShowConfirmation("Result", "Random Charges Not Founds For Current Wire."));
      }
  %>
</div>
<%
    }
    catch (Exception e)
    {

    }
  }
  else
  {
    response.sendRedirect("/icici/logout.jsp");
    return;
  }
%>
</body>
</html>
