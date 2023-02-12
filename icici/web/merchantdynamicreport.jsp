<%@ page import="com.directi.pg.Functions"%>
<%@ include file="index.jsp"%>
<%@ include file="functions.jsp"%>
<%@ page import="com.directi.pg.Logger"%>
<%@ page import="com.manager.BankManager" %>
<%@ page import="com.manager.vo.BankWireManagerVO" %>
<%@ page import="com.manager.vo.ChargeVO" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="java.util.*" %>
<%--
  Created by IntelliJ IDEA.
  User: Uday
  Date: 17/6/17
  Time: 2:55 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
  Logger logger=new Logger("manageMerchantRandomCharge.jsp");
%>
<html>
<head>
  <style>
    .inp-box{
      width: 47%;
      margin: 5px 2px;
    }
  </style>
  <script>
    $(function ()
    {
      $(".datepicker").datepicker({dateFormat: 'yy-mm-dd'});
    });
    function getSettlementCurrency(ctoken)
    {
      var bankWireId = document.getElementById("bankwireid").value;
      if (bankWireId == null || bankWireId=='' || bankWireId=='null'){
        alert('Please select bankwire');
        return false;
      }
      else{
        document.f1.action = "/icici/servlet/GetSettlementCurrencyDetails?ctoken=" + ctoken + "&bankwireid=" + bankWireId;
        document.f1.submit();
      }
    }
    function ToggleAll(checkbox)
    {
      flag = checkbox.checked;
      var checkboxes = document.getElementsByName("terminalId");
      var total_boxes = checkboxes.length;

      for(i=0; i<total_boxes; i++ )
      {
        checkboxes[i].checked =flag;
      }
    }
    function Submit()
    {
      var checkboxes = document.getElementsByName("terminalId");
      var total_boxes = checkboxes.length;
      flag = false;

      for(i=0; i<total_boxes; i++ )
      {
        if(checkboxes[i].checked)
        {
          flag= true;
          if(!checkboxSelect(checkboxes[i])){
            console.log("returned false");
            return
          }
          break;
        }
      }
      if(!flag)
      {
        alert("Select at least one record");
        return false;
      }
      if (confirm("Do you really want to Generate Settlement Report."))
      {
        document.TerminalDetails.submit();
      }
    }

    function checkboxSelect(el){
      var tr = $(el).closest('tr');
      var valid = "false";
      if(el.checked)
      {
        tr.find('input[type=text]').each(function(index){
          if(tr.find('input[type=text]')[index].disabled == false){
            if(tr.find('input[type=text]')[index].value == "")
            {
              alert("Please fill in all the values");
              valid = false;
              return false;
            }
            else{
              valid = true;
              return true;
            }
          }
        })

      }

      return valid;

    }

    function isNumberKey(evt)
    {
      console.log("in is number key ----",evt)
      var charCode = (evt.which) ? evt.which : evt.keyCode

      if ($.inArray(charCode, [46, 8, 9, 27, 13, 110, 190, 118]) !== -1 ||
                // Allow: Ctrl+A,Ctrl+C,Ctrl+V, Command+A
              ((charCode == 65 || charCode == 86 || charCode == 67) && (evt.ctrlKey === true || evt.metaKey === true)) ||
                // Allow: home, end, left, right, down, up
              (charCode >= 35 && charCode <= 40))
      {
        // let it happen, don't do anything
        return;
      }

      if (charCode > 31 && (charCode < 48 || charCode > 57))
      {
        evt.preventDefault();
      }

      onPasteNumCheck(evt)
      return true;
    }

    function onPasteNumCheck(evt)
    {
      console.log("event ---",evt.target)
      var regex = new RegExp('^[0-9\.]+$');
      if (regex.test(document.getElementById(evt.target.id).value))
      {
        return true;
      }
      else
      {
        document.getElementById(evt.target.id).value = "";
        return false;
      }
    }

  </script>
  <title> Merchant Payout Report</title>
</head>
<body>
<%
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    BankManager bankManager = new BankManager();
    StringBuffer dynamicChargeIds=new StringBuffer();
    StringBuffer settlementCurrency=new StringBuffer();
    TreeMap<String, BankWireManagerVO> bankWiresMap = bankManager.getBankWiresForRandomCharges();
    String bankWireId=request.getParameter("bankwireid");

    Set<String> settlementCurrSet = (Set) request.getAttribute("settlementCurrSet");
    HashMap<TerminalVO,List<ChargeVO>> terminalVOListHashMap=(HashMap)request.getAttribute("stringListHashMap");
    String processingCurrency =(String)request.getAttribute("processingCurrency");
    String settleCurrency=(String)request.getAttribute("settlementCurrency");
    List<String> stringList=(List)request.getAttribute("result");
    HashMap<TerminalVO,String> currencyConversionRate=(HashMap)request.getAttribute("currencyConversionRate");
    List<ChargeVO> chargeVOList=null;
    List<String> chargeList=new ArrayList<>();
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Merchant Payout Report
        <div style="float: right;margin-left: 5px;">
          <form  id="consolidatatedreport" action="/icici/consolidatedReport.jsp?ctoken=<%=ctoken%>" method="POST">
            <input type="submit"  class="buttonform"  value="Consolidated Report ">
          </form>
        </div>
        <div style="float: right;">
          <form  id="merchantdynamicreport" action="/icici/merchantdynamicreport.jsp?ctoken=<%=ctoken%>" method="POST">
            <input type="submit"  class="buttonform"  value="Dynamic Report ">
          </form>
        </div>

        <%--<div style="float: right;">
            <form  id="payoutcronfrm" action="/icici/payoutcron.jsp?ctoken=<%=ctoken%>" method="POST">
                <input type="button"  class="buttonform"  value="Payout Cron " onclick="payoutcronconfirm()">
            </form>
        </div>--%>
        <div style="float: right;margin-right: 5px;">
          <form  id="" action="/icici/listMerchantPayoutReport.jsp?ctoken=<%=ctoken%>" method="POST">
            <input type="submit"  class="buttonform"  value="Payout Report">
          </form>
        </div>
      </div>
      <form action="/icici/servlet/MerchantPayoutReport?ctoken=<%=ctoken%>" method="post" name="f1">
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <table align="center" width="65%" cellpadding="2" cellspacing="2">
          <tbody>
          <tr>
            <td>
              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                <tbody>
                <tr><td colspan="4">&nbsp;</td>
                <tr>
                <tr><td align="center" colspan="4" class="textb">
                  <%
                    String message=(String)request.getAttribute("statusMsg");
                    Functions functions=new Functions();
                    if(functions.isValueNull(message) )
                    {
                      out.println(message);
                    }
                  %>
                </td>
                </tr>
                <tr><td colspan="4">&nbsp;</td>
                <tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb">BankWire Id*</td>
                  <td width="3%" class="textb">:</td>
                  <td width="12%" class="textb">
                    <select name="bankwireid" id="bankwireid" class="txtbox" style="width: 165px" onchange=" return getSettlementCurrency('<%=ctoken%>')">
                      <option value="" selected></option>
                      <%
                        for (String bankId : bankWiresMap.keySet())
                        {
                          BankWireManagerVO bankWireManagerVO = bankWiresMap.get(bankId);
                          String accountId =bankWireManagerVO.getAccountId();
                          String MID = bankWireManagerVO.getMid();
                          String value = bankId + "-" + accountId + "-" + MID;
                          String isSelected = "";
                          if (bankId.equalsIgnoreCase(bankWireId))
                            isSelected = "selected";
                      %>
                      <option value="<%=bankId%>" <%=isSelected%>> <%=value%></option>
                      <%
                        }
                      %>
                    </select>
                  </td>
                </tr>
                <tr>
                  <td colspan="4">&nbsp;</td>
                <tr>
                    <%
                    if(settlementCurrSet!=null)
                    {
                    %>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Processing Currency</td>
                  <td class="textb">:</td>
                  <td colspan="4" class="textb"><%=processingCurrency%></td>
                </tr>
                <tr>
                  <td colspan="7">&nbsp;</td>
                </tr>
                <%
                  for(String currency:settlementCurrSet)
                  {
                    if(!settlementCurrSet.contains(null))
                    {
                      if(settlementCurrency.length()>0)
                      {
                        settlementCurrency.append(",");
                      }
                      settlementCurrency.append(currency);
                %>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb"><%=currency%>&nbsp; Conversion Rate
                    *
                  </td>
                  <td class="textb">:</td>
                  <td colspan="4" class="textb">
                    <input maxlength="10" type="text" class="txtbox" id="<%=currency%>_conversion_rate" onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)"
                           name="<%=currency%>_conversion_rate" value=""
                           style="width:33%;">
                  </td>
                </tr>
                <tr>
                  <td colspan="7">&nbsp;</td>
                </tr>
                <%
                    }
                  }
                %>
                <input type="hidden" name="processingcurrency"
                       value="<%=settlementCurrency.toString()%>">
                <tr>
                    <%
                     }
                    %>
                <tr>
                  <td colspan="4">&nbsp;</td>
                  <%--<tr>
                      <%
                     if(terminalVOListHashMap!=null)
                      {

                    Set set=terminalVOListHashMap.keySet();
                    Iterator iterator=set.iterator();
                    while (iterator.hasNext()){
                    TerminalVO terminalVO=(TerminalVO)iterator.next();
                    chargeVOList=terminalVOListHashMap.get(terminalVO);
                    %>
                  <tr>
                    <td class="textb">&nbsp;</td>
                    <td class="textb"></td>
                    <td class="textb">:</td>
                    <td colspan="4" class="textb"><%=terminalVO%></td>
                  </tr>
                  <%
                    for(ChargeVO chargeVO:chargeVOList){
                      if(dynamicChargeIds.length()>0)
                      {
                        dynamicChargeIds.append(",");
                      }
                      dynamicChargeIds.append(chargeVO.getMappingid());
                  %>
                  <tr>
                    <td class="textb">&nbsp;</td>
                    <td class="textb"><%=chargeVO.getChargename()%>&nbsp;:*
                    </td>
                    <td class="textb">:</td>
                    <td colspan="4" class="textb">
                      <input maxlength="10" type="text" class="txtbox"
                             name="<%=chargeVO.getMappingid()%>_counter" value=""
                             style="width:33%;" placeHolder="Counter">&nbsp;&nbsp;&nbsp;&nbsp;

                      <input maxlength="10" type="text" class="txtbox"
                             name="<%=chargeVO.getMappingid()%>_amount" value=""
                             style="width:33%;" placeHolder="Amount">
                    </td>
                  </tr>
                  <tr>
                    <td colspan="7">&nbsp;</td>
                  </tr>--%>
                  <%--<%
                    }
                  %>--%>
                  <%--<input type="hidden" name="dynamicmappingids"
                         value="<%=dynamicChargeIds.toString()%>">--%>

                  <input type="hidden" name="step" value="1">
                  <%--<%
                    }
                  %>--%>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb"></td>
                  <td style="padding: 3px" width="5%" class="textb"></td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <button type="submit" name="action" value="next" class="buttonform">
                      <i class="fa fa-sign-in"></i>
                      &nbsp;&nbsp;Next
                    </button>
                  </td>
                </tr>
                </tbody>
              </table>
            </td>
          </tr>
          </tbody>
        </table>
      </form>

      <div style="width: 100%">
        <form name="TerminalDetails" action="/icici/servlet/MerchantPayoutReport?ctoken=<%=ctoken%>" method="post">
          <input type="hidden" name="bankwireid" value="<%=bankWireId%>">
          <input type="hidden" name="settlementCurrency" value="<%=settleCurrency%>">
          <input type="hidden" name="processingcurrency" value="<%=processingCurrency%>">
          <%
            List<TerminalVO> terminalList=(List)request.getAttribute("terminalList");
            List<TerminalVO> settledList=(List)request.getAttribute("settledList");
            if(terminalList!=null)
            {
          %>
          <div style="overflow-x:auto;overflow-y: hidden;width: 100%;">
            <table align=center style="" border="1" class="table table-striped table-bordered table-hover table-green dataTable">
              <thead>
              <tr>
                <td valign="middle" width="" align="center" class="th0"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
                <td width="" valign="middle" align="center" class="th0">Terminal Id</td>
                <td width="" valign="middle" align="center" class="th0">Member Id</td>
                <td width="" valign="middle" align="center" class="th0">Account Id</td>
                <td width="" valign="middle" align="center" class="th0">Processing Currency</td>
                <%
                  if(functions.isValueNull(settleCurrency))
                  {
                %>
                <td width="" valign="middle" align="center" class="th0">Settlement Currency</td>
                <td width="" valign="middle" align="center" class="th0">Conversion Rate</td>
                <%
                  }
                  if(terminalVOListHashMap!=null && terminalVOListHashMap.size()>0)
                  {
                    for(TerminalVO terminalVO:terminalList)
                    {
                      chargeVOList=terminalVOListHashMap.get(terminalVO);
                      if(chargeVOList!=null && chargeVOList.size()>0)
                      {
                        for (ChargeVO chargeVO:chargeVOList)
                        {
                          if(!chargeList.contains(chargeVO.getChargename()))
                          {
                %>
                <td width="" valign="middle" align="center" class="th0"><%=chargeVO.getChargename()%></td>
                <%
                            chargeList.add(chargeVO.getChargename());
                          }
                        }
                      }
                    }
                  }
                %>
              </tr>
              </thead>
              <%
                String curency="";
                for(TerminalVO terminalVO:terminalList)
                {
                  String disable="";
                  chargeVOList=terminalVOListHashMap.get(terminalVO);
                  if(settledList!=null)
                  {
                    for (TerminalVO terminalVO1 : settledList)
                    {
                      if (terminalVO1.getTerminalId().equals(terminalVO.getTerminalId()))
                      {
                        disable="disabled";
                        break;
                      }
                    }
                  }
              %>
              <tr>
                <td align="center" class="tr0"><input type="checkbox" name="terminalId" value="<%=terminalVO.getTerminalId()%>" <%=disable%> ></td>
                <td align="center" class="tr0"><%=terminalVO.getTerminalId()%></td>
                <td align="center" class="tr0"><%=terminalVO.getMemberId()%><input type="hidden" name="memberid_<%=terminalVO.getTerminalId()%>" value="<%=terminalVO.getMemberId()%>"></td>
                <td align="center" class="tr0"><%=terminalVO.getAccountId()%><input type="hidden" name="accountid_<%=terminalVO.getTerminalId()%>" value="<%=terminalVO.getAccountId()%>" ></td>
                <td align="center" class="tr0"><%=processingCurrency%><input type="hidden" name="processingCurrency%>" value="<%=processingCurrency%>" ></td>
                <%
                  if(currencyConversionRate.get(terminalVO.getSettlementCurrency())!=null)
                  {
                %>
                <td align="center" class="tr0"><%=terminalVO.getSettlementCurrency()%><input type="hidden" name="settlementCurrency_<%=terminalVO.getTerminalId()%>" value="<%=terminalVO.getSettlementCurrency()%>"  <%=disable%>></td>
                <td align="center" class="tr0"><input maxlength="10" type="text" class="txtbox" name="currencyConversionRate_<%=terminalVO.getTerminalId()%>" id="currencyConversionRate_<%=terminalVO.getTerminalId()%>" value="<%=currencyConversionRate.get(terminalVO.getSettlementCurrency())%>" onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)" <%=disable%>  ></td>
                <%
                }
                else if(functions.isValueNull(settleCurrency))
                {
                  curency=currencyConversionRate.get(terminalVO.getSettlementCurrency())==null?"":currencyConversionRate.get(terminalVO.getSettlementCurrency());
                %>
                <td align="center" class="tr0"><%=terminalVO.getSettlementCurrency()%><input type="hidden" name="settlementCurrency_<%=terminalVO.getTerminalId()%>" value="<%=terminalVO.getSettlementCurrency()%>" ></td>
                <td align="center" class="tr0"><input maxlength="10" type="text" class="txtbox" name="currencyConversionRate_<%=terminalVO.getTerminalId()%>" value="<%=curency%>" <%if(!functions.isValueNull(curency)){%> disabled<%}%>></td>
                <%
                  }
                  boolean isChargeNotAvailable=true;
                  int count=0;
                  if(chargeVOList!=null && chargeVOList.size()>0)
                  {
                    for (ChargeVO chargeVO:chargeVOList)
                    {
                      if(dynamicChargeIds.length()>0){
                        dynamicChargeIds.append(",");
                      }
                      dynamicChargeIds.append(chargeVO.getMappingid());
                      isChargeNotAvailable=false;
                      count++;
                %>
                <td align="center" class="tr0"><input maxlength="10" class="txtbox inp-box" type="text" name="<%=chargeVO.getMappingid()%>_counter" id="<%=chargeVO.getMappingid()%>_counter" placeHolder="Counter" value="" required onkeypress="return isNumberKey(event)"  <%=disable%> onkeyup="onPasteNumCheck(event)" /><input maxlength="10" class="txtbox inp-box" type="text" placeHolder="Amount" name="<%=chargeVO.getMappingid()%>_amount" id="<%=chargeVO.getMappingid()%>_amount" value=""  <%=disable%> required onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)" /></td>
                <%
                    }
                    if(count!=chargeList.size())
                      isChargeNotAvailable=true;
                  }
                  if (isChargeNotAvailable)
                  {
                    for(int i=0 ; i<chargeList.size()-count ;i++)
                    {
                %>
                <td align="center" class="tr0"><input maxlength="10" class="txtbox inp-box" type="text" name="" value="" disabled><input maxlength="10" class="txtbox inp-box" type="text" name="" value="" disabled></td>
                <%
                      }
                    }
                  }
                %>
              </tr>
            </table>
            <input type="hidden" name="dynamicmappingids" value="<%=dynamicChargeIds%>">
            <div style="text-align: center">
              <button type="button" class="buttonform" onclick="return Submit();" style="display: inline-block;">
                <i class="fa fa-sign-in"></i>
                &nbsp;&nbsp;Submit
              </button>
            </div>
          </div>
          <%
            }
          %>
        </form>
      </div>
      <%
        if(stringList!=null){
      %>
      <table align=center style="width:80%" border="1" class="table table-striped table-bordered table-hover table-green dataTable">
        <thead>
        <tr>
          <td width="2%"valign="middle" align="center" class="th0">Cycle Id</td>
          <td width="8%" valign="middle" align="center" class="th0">Member Id</td>
          <td width="8%" valign="middle" align="center" class="th0">Account Id</td>
          <td width="8%" valign="middle" align="center" class="th0">Terminal Id</td>
          <td width="8%" valign="middle" align="center" class="th0">Status</td>
          <td width="8%" valign="middle" align="center" class="th0">Description</td>
        </tr>
        </thead>
        <%
          for(String s:stringList)
          {
            String responseArr[]=s.split(":");
        %>
        <tr>
          <td align="center" class="tr0"><%=responseArr[0]%></td>
          <td align="center" class="tr1"><%=responseArr[1]%></td>
          <td align="center" class="tr0"><%=responseArr[2]%></td>
          <td align="center" class="tr1"><%=responseArr[3]%></td>
          <td align="center" class="tr1"><%=responseArr[4]%></td>
          <td align="center" class="tr1"><%=responseArr[5]%></td>
        </tr>
        <%
          }
        %>
      </table>
      <%
        }
      %>
      </br>
      </br>
    </div>
  </div>
</div>
<%
  }
  else
  {
    response.sendRedirect("/icici/logout.jsp");
    return;
  }
%>
</body>
</html>
