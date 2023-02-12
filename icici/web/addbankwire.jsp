AN<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="com.manager.vo.payoutVOs.SettlementCycleVO" %>
<%@ include file="functions.jsp" %>
<%@ include file="index.jsp" %>
<%--
  Created by IntelliJ IDEA.
  User: Sandip
  Date: 6/30/2017
  Time: 1:49 PM
  To change this template use File | Settings | File Templates.
--%>
<%!
  Logger logger = new Logger("whitelabelinvoice.jsp");
  Functions functions = new Functions();
%>
<html>
<head>
  <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="http://code.jquery.com/jquery-latest.js"></script>
  <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>

  <script>
    $(document).ready(function() {
      $('#sandbox-container input').datepicker({});
      $(".datepicker").datepicker({dateFormat: 'yy-mm-dd'});
      $('#file').change(function(event){
//        alert("please upload the file");
        var form = $('#f1')[0];
        alert('form::'+form);
        // Create an FormData object
        var data = new FormData(form);

        $.ajax({
          type: "POST",
          enctype: 'multipart/form-data',
          url: "/icici/servlet/AjaxFileUpload?",
          data: data,
          processData: false,
          contentType: false,
          cache: false,
          timeout: 600000,
          success: function (data) {
            alert("success::"+data);
            $("#result").text(data);
            console.log("SUCCESS : ", data);
            $("#btnSubmit").prop("disabled", false);

          },
          error: function (e) {
            alert("failed::"+data);
            $("#result").text(e.responseText);
            console.log("ERROR : ", e);
            $("#btnSubmit").prop("disabled", false);

          }
        });
      });
      $('#cycleid').change(function(event){
        var cycleId=$('#cycleid').val();
        var ctoken=$('#ctoken').val();

        $.get('/icici/servlet/GetSettlementCycleDetails?',{cycleid:cycleId,ctoken:ctoken},function(responseText){

          $('#startdate').val('');
          $('#starttime').val('');

          $('#enddate').val('');
          $('#endtime').val('');

          $('#declinecovereddate').val('');
          $('#declinecoveredtime').val('');

          $('#reversecovereddate').val('');
          $('#reversecoveredtime').val('');

          $('#chargebackcovereddate').val('');
          $('#chargebackcoveredtime').val('');

          var json = JSON.parse(responseText);

          $('#startdate').val(json['startDate']);
          $('#starttime').val(json['startTime']);

          $('#enddate').val(json['endDate']);
          $('#endtime').val(json['endTime']);

          $('#declinecovereddate').val(json['coveredDate']);
          $('#declinecoveredtime').val(json['coveredTime']);

          $('#reversecovereddate').val(json['coveredDate']);
          $('#reversecoveredtime').val(json['coveredTime']);

          $('#chargebackcovereddate').val(json['coveredDate']);
          $('#chargebackcoveredtime').val(json['coveredTime']);

        });
      });
    });
  </script>

  <script>



    /*function confirmsubmitreg(){
     var r = window.confirm("Are you sure to create bank wire?");
     if (r == true){
     document.getElementById("f1").submit();
     }
     else{
     return false;
     }
     }*/

    function submitOnAccount()
    {
      document.f1.action="/icici/addbankwire.jsp?ctoken=<%=ctoken%>";
      document.f1.submit();
    }

    function getSettlementCycleDetails(ctoken)
    {
      //alert("ctoken::"+ctoken);
      var cycleId = document.getElementById("cycleid").value;
      var cycleId = document.getElementById("cycleid").value;
      document.f1.action = "/icici/servlet/GetSettlementCycleDetails?ctoken=" + ctoken + "&cycleid=" + cycleId;
      document.f1.submit();
    }



  </script>
  <title>Bank Wire</title>
</head>
<body>
<%
  ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    String accountId=request.getParameter("accountid");
    if(accountId==null){
      accountId="";
    }

    String settlementCycleId=request.getParameter("cycleid");
    if(settlementCycleId==null){
      settlementCycleId="";
    }

    Hashtable accountDetails = GatewayAccountService.getCommonAccountDetail();

    SettlementCycleVO settlementCycleVO=(SettlementCycleVO)request.getAttribute("settlementCycleVO");

    String bankStartDate="";
    String bankStartTime="";

    String bankEndDate="";
    String bankEndTime="";

    String declineCoveredDate="";
    String declineCoveredTime="";

    String reversedCoveredDate="";
    String reversedCoveredTime="";

    String chargebackCoveredDate="";
    String chargebackCoveredTime="";

    if(settlementCycleVO!=null){

      String startDateArr[]=settlementCycleVO.getStartDate().split(" ");
      String endDateArr[]=settlementCycleVO.getEndDate().split(" ");

      bankStartDate=startDateArr[0];
      bankStartTime=startDateArr[1];

      bankEndDate=endDateArr[0];
      bankEndTime=endDateArr[1];

      declineCoveredDate=bankEndDate;
      declineCoveredTime=bankEndTime;

      reversedCoveredDate=bankEndDate;
      reversedCoveredTime=bankEndTime;

      chargebackCoveredDate=bankEndDate;
      chargebackCoveredTime=bankEndTime;
    }
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading">
        Bank Wire Manager
        <div style="float: right;">
          <%--<form action="/icici/wlPartnerInvoiceList.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" name="submit" class="addnewmember">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Invoice List
            </button>
          </form>--%>
        </div>
      </div>
      <form name="f1" id="f1" action="/icici/servlet/WhitelableInvoice?ctoken=<%=ctoken%>" method="post" ENCTYPE="multipart/form-data" >
        <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
        <table align="center" width="65%">
          <tbody>
          <tr>
            <td>
              <table width="75%" align="center">
                <tbody>
                <tr>
                  <td colspan="7">&nbsp;</td>
                </tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Bank Account*</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 1px" width="50%" class="textb">
                    <select id="accountid" name="accountid" class="txtbox" <%--onchange="submitOnAccount()"--%>>
                      <option value="" ></option>
                      <%
                        Enumeration enumeration = accountDetails.keys();
                        Integer key =null;
                        GatewayAccount gatewayAccount = null;
                        while (enumeration.hasMoreElements())
                        {
                          String selected = "";
                          key = (Integer) enumeration.nextElement();
                          gatewayAccount = (GatewayAccount)accountDetails.get(key);
                          int acId = gatewayAccount.getAccountId();
                          String currency = gatewayAccount.getCurrency();
                          String mid = gatewayAccount.getMerchantId();
                          if(String.valueOf(gatewayAccount.getAccountId()).equals(accountId))
                          {
                            selected="selected";
                          }
                      %>
                      <option value="<%=gatewayAccount.getAccountId()%>" <%=selected%>><%=acId+"-"+currency+"-"+mid%></option>
                      <%
                        }
                      %>
                    </select>
                  </td>
                </tr>
                <tr>
                  <td colspan="7">&nbsp;</td>
                </tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Settlement Cycle*</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 1px" width="50%" class="textb">
                    <select name="cycleid" id="cycleid" class="txtbox" <%--onchange="getSettlementCycleDetails('<%=ctoken%>')"--%>>
                      <option value=""></option>
                      <%
                        Connection connection=null;
                        PreparedStatement preparedStatement=null;
                        ResultSet resultSet=null;
                        try
                        {
                          String query="select settlement_cycle_id,start_date,end_date from settlement_cycle_master where status='Pending' /*and accountid="+request.getParameter("accountid")+"*/";
                          connection=Database.getConnection();
                          preparedStatement=connection.prepareStatement(query);
                          resultSet=preparedStatement.executeQuery();
                          while(resultSet.next()){
                            String cycleId=resultSet.getString("settlement_cycle_id");
                            String startDate=resultSet.getString("start_date");
                            String endDate=resultSet.getString("end_date");
                            String selected="";
                            if(cycleId.equals(settlementCycleId))
                            {
                              selected="selected";
                            }
                      %>
                      <option value="<%=cycleId%>" <%=selected%>> <%=cycleId+"-"+startDate.split(" ")[0]+" to "+endDate.split(" ")[0]%></option>
                      <%
                          }
                        }
                        catch (Exception e){
                          logger.error("Exception:::::"+e);
                        }
                        finally
                        {
                          Database.closeConnection(connection);
                        }
                      %>
                    </select>
                  </td>
                </tr>

                <tr>
                  <td colspan="7">&nbsp;</td>
                </tr>

                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Start Date*</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td colspan="2" class="textb"><input type="text"
                                                       value=""
                                                       class="txtbox"
                                                       readonly
                                                       name="startdate"
                                                       id="startdate"
                                                       style="width: 48%;"></td>
                  <td colspan="2" align="left" class="textb"><input maxlength="10"
                                                                    type="text"
                                                                    class="txtbox"
                                                                    name="starttime"
                                                                    id="starttime"
                                                                    readonly
                                                                    value=""
                                                                    placeholder="(HH:MM:SS)">
                  </td>
                </tr>

                <tr>
                  <td colspan="7">&nbsp;</td>
                </tr>

                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">End Date*</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td colspan="2" class="textb"><input type="text"
                                                       value=""
                                                       readonly
                                                       class="txtbox"
                                                       name="enddate"
                                                       id="enddate"
                                                       style="width: 48%;"></td>
                  <td colspan="2" align="left" class="textb" width="43%">
                    <input
                            maxlength="10"
                            type="text"
                            class="txtbox"
                            name="endtime"
                            id="endtime"
                            value=""
                            readonly
                            placeholder="(HH:MM:SS)"></td>
                </tr>

                <tr>
                  <td colspan="7">&nbsp;</td>
                </tr>

                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Declined Date*</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td colspan="2" class="textb"><input type="text"
                                                       value=""
                                                       readonly
                                                       class="txtbox"
                                                       name="declinecovereddate"
                                                       id="declinecovereddate"
                                                       style="width: 48%;"></td>
                  <td colspan="2" align="left" class="textb" width="43%">
                    <input
                            maxlength="10"
                            type="text"
                            class="txtbox"
                            name="declinecoveredtime"
                            id="declinecoveredtime"
                            value=""
                            readonly
                            placeholder="(HH:MM:SS)"></td>
                </tr>

                <tr>
                  <td colspan="7">&nbsp;</td>
                </tr>

                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Reversed Date*</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td colspan="2" class="textb"><input type="text"
                                                       value=""
                                                       readonly
                                                       class="txtbox"
                                                       name="reversecovereddate"
                                                       id="reversecovereddate"
                                                       style="width: 48%;"></td>
                  <td colspan="2" align="left" class="textb" width="43%">
                    <input
                            maxlength="10"
                            type="text"
                            class="txtbox"
                            name="reversecoveredtime"
                            id="reversecoveredtime"
                            value=""
                            readonly
                            placeholder="(HH:MM:SS)"></td>
                </tr>

                <tr>
                  <td colspan="7">&nbsp;</td>
                </tr>

                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Chargeback Date*</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td colspan="2" class="textb"><input type="text"
                                                       value=""
                                                       readonly
                                                       class="txtbox"
                                                       name="chargebackcovereddate"
                                                       id="chargebackcovereddate"
                                                       style="width: 48%;"></td>
                  <td colspan="2" align="left" class="textb" width="43%"><input
                          maxlength="10"
                          type="text"
                          class="txtbox"
                          name="chargebackcoveredtime"
                          id="chargebackcoveredtime"
                          value=""
                          readonly
                          placeholder="(HH:MM:SS)"></td>
                </tr>

                <tr>
                  <td colspan="7">&nbsp;</td>
                </tr>

                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Processing Amount:</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td colspan="2" class="txtbox"><input class="txtbox" type="text" name="" style="width: 48%;" placeholder=""></td>
                  <td colspan="2" align="left" class="txtbox" width="43%"></td>
                </tr>

                <tr>
                  <td colspan="7">&nbsp;</td>
                </tr>

                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Gross Amount:</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td colspan="2" class="txtbox"><input class="txtbox" type="text" name="" style="width: 48%;" placeholder=""></td>
                  <td colspan="2" align="left" class="txtbox" width="43%"></td>
                </tr>

                <tr>
                  <td colspan="7">&nbsp;</td>
                </tr>

                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Net Final Amount:</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td colspan="2" class="txtbox"><input class="txtbox" type="text" name="" style="width: 48%;" placeholder=""></td>
                  <td colspan="2" align="left" class="txtbox" width="43%"></td>
                </tr>

                <tr>
                  <td colspan="7">&nbsp;</td>
                </tr>

                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Unpaid Amount:</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td colspan="2" class="txtbox"><input class="txtbox" type="text" name="" style="width: 48%;" placeholder=""></td>
                  <td colspan="2" align="left" class="txtbox" width="43%"></td>
                </tr>

                <tr>
                  <td colspan="7">&nbsp;</td>
                </tr>

                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Paid*</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 1px" width="50%" class="textb">
                    <select name="ispaid" class="txtbox">
                      <option value=""></option>
                      <option value="Y">Y</option>
                      <option value="N">N</option>
                    </select>
                  </td>
                </tr>

                <tr>
                  <td colspan="7">&nbsp;</td>
                </tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Day Light Saving*</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 1px" width="50%" class="textb">
                    <select name="isdlsaving" class="txtbox">
                      <option value=""></option>
                      <option value="Y">Y</option>
                      <option value="N">N</option>
                    </select>
                  </td>
                </tr>


                <tr>
                  <td colspan="7">&nbsp;</td>
                </tr>

                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">RR Released*</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 1px" width="50%" class="textb">
                    <select name="rrreleased" class="txtbox">
                      <option value=""></option>
                      <option value="Y">Y</option>
                      <option value="N">N</option>
                    </select>
                  </td>
                </tr>

                <tr>
                  <td colspan="7">&nbsp;</td>
                </tr>

                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">RR Release Date*</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td colspan="2" class="textb"><input type="text" readonly class="datepicker"
                                                       name="rrdate" style="width: 48%;"></td>
                  <td colspan="2" align="left" class="textb"><input maxlength="10"
                                                                    type="text"
                                                                    class="txtbox"
                                                                    name="rrtime"
                                                                    value=""
                                                                    placeholder="(HH:MM:SS)">
                  </td>
                </tr>

                <tr>
                  <td colspan="7">&nbsp;</td>
                </tr>

                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Report File Name</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td colspan="2" class="txtbox"><input class="txtbox" type="text" name="" style="width: 48%;" placeholder=""></td>
                  <td colspan="2" align="left" class="txtbox" width="43%"></td>
                </tr>

                <tr>
                  <td colspan="7">&nbsp;</td>
                </tr>

                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Settlement File:</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td colspan="2" class="txtbox"><input name="file" type="file" id="file" value="choose File"></td>
                  <td colspan="2" align="left" class="txtbox" width="43%"></td>
                </tr>
                <tr>
                  <td colspan="7">&nbsp;</td>
                </tr>

                <tr>
                  <td colspan="7" style="padding: 3px" width="50%" class="textb" align="center">
                    <button type="submit" class="buttonform" value="Add" style="width:150px ">
                      <i class="fa fa-sign-in"></i>
                      &nbsp;&nbsp;Create
                    </button>
                  </td>
                </tr>

              </table>
          </tbody>
        </table>
        </tbody>
      </form>
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
