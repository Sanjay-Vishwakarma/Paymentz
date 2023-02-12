<%@ page errorPage="error.jsp"
         import="com.directi.pg.Functions,com.directi.pg.TransactionEntry,com.logicboxes.util.ApplicationProperties,
                 org.owasp.esapi.errors.ValidationException" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="java.util.*" %>
<%@ page import="com.manager.vo.RecurringBillingVO" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="com.manager.vo.ManualRebillResponseVO" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.TerminalVO" %>

<%@ include file="Top.jsp" %>


<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
  session.setAttribute("submit","Recurring");

%>

<html>
<head>
  <style type="text/css">

    #main{background-color: #ffffff}

    :target:before {
      content: "";
      display: block;
      height: 50px;
      margin: -50px 0 0;
    }

    .table > thead > tr > th {font-weight: inherit;}

    :target:before {
      content: "";
      display: block;
      height: 90px;
      margin: -50px 0 0;
    }

    footer{border-top:none;margin-top: 0;padding: 0;}

    /********************Table Responsive Start**************************/

    .table#myTable > thead > tr > th {font-weight: inherit;text-align: center;}

    @media (max-width: 640px){

      table#myTable {border: 0;}

      /*table tr {
          padding-top: 20px;
          padding-bottom: 20px;
          display: block;
      }*/

      table#myTable thead { display: none;}

      #myTable tr:nth-child(odd), #myTable tr:nth-child(even) {background: #ffffff;}

      table#myTable td {
        display: block;
        border-bottom: none;
        padding-left: 0;
        padding-right: 0;
      }

      table#myTable td:before {
        content: attr(data-label);
        float: left;
        width: 100%;
        font-weight: bold;
      }

      #myTable tr:nth-child(odd) {background: #cacaca!important;}

    }

    table#myTable {
      width: 100%;
      max-width: 100%;
      border-collapse: collapse;
      margin-bottom: 20px;
      display: table;
      border-collapse: separate;
      /*border-color: grey;*/
    }

    #myTable thead {
      display: table-header-group;
      vertical-align: middle;
      border-color: inherit;

    }
    /*#myTable tr:nth-child(odd) {background: #F9F9F9;}*/

    #myTable tr {
      display: table-row;
      vertical-align: inherit;
      border-color: inherit;
    }

    #myTable th {padding-right: 1em;text-align: left;font-weight: bold;}

    #myTable td, #myTable th {display: table-cell;vertical-align: inherit;}

    #myTable tbody {
      display: table-row-group;
      vertical-align: middle;
      border-color: inherit;
    }

    #myTable td {
      padding-top: 6px;
      padding-bottom: 6px;
      padding-left: 10px;
      padding-right: 10px;
      vertical-align: top;
      border-bottom: none;
    }

    .table#myTable>thead>tr>th, .table#myTable>tbody>tr>th, .table#myTable>tfoot>tr>th, .table#myTable>thead>tr>td, .table#myTable>tbody>tr>td, .table#myTable>tfoot>tr>td{border-top: 1px solid #ddd;}

    /********************Table Responsive Ends**************************/
  </style>
</head>
<title> <%=company%> | Recurring Billing Module</title>

<body class="bodybackground">
<%--<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading">
        Recurring Module
      </div>

      <form action="/merchant/servlet/RecurringModule?ctoken=<%=ctoken%>" name="form" method="post">
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <br>
        <table  align="center" width="80%" cellpadding="2" cellspacing="2" style="margin-left:60px; ">
          <tr>
            <td>
              <%
                String uId = "";
                if(session.getAttribute("role").equals("submerchant"))
                {
                  uId = (String) session.getAttribute("userid");
                }
                else
                {
                  uId = (String) session.getAttribute("merchantid");
                }

                StringBuffer terminalBuffer = new StringBuffer();
                String terminalid = "";
                String errormsg1 = (String) request.getAttribute("error");
                if (errormsg1 != null)
                {
                  //out.println("<center><li class=\"alert alert-danger alert-dismissable\"><b>"+errormsg1+"<br></b></li></center>");
                  out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg1 + "</h5>");
                }

                ManualRebillResponseVO manualRebillResponseVO = (ManualRebillResponseVO) request.getAttribute("recurringstatus");
                String resAmount = manualRebillResponseVO.getAmount();
                String trackingid = manualRebillResponseVO.getTrackingId();
                String orderid = manualRebillResponseVO.getDescription();
                String status = manualRebillResponseVO.getStatus();
                String message = manualRebillResponseVO.getErrorMessage();
                String billingDescriptor = manualRebillResponseVO.getBillingDescriptor();
              %>

              <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Recurring Billing Id</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input name="rbid" size="10" class="txtbox">
                  </td>


                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">First Six</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input name="firstsix" size="10" class="txtbox">
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Last Four</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input name="lastfour" size="10" class="txtbox">
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

                <tr>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Tracking Id</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input name="trackingid" size="10" class="txtbox">
                  </td>



                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Card Holder Name</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input name="name" size="10" class="txtbox">
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Terminal ID</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <select size="1" name="terminalid" class="txtbox">
                      <option value="all">All</option>
                      <%
                        terminalBuffer.append("(");
                        TerminalManager terminalManager=new TerminalManager();
                        List<TerminalVO> terminalVOList=terminalManager.getMemberandUserTerminalList(uId, String.valueOf(session.getAttribute("role")));
                        for (TerminalVO terminalVO:terminalVOList)
                        {
                          String str1 = "";
                          System.out.println("terminal---"+terminalid+"--vo--"+terminalVO.getTerminalId());

                          if(terminalid.equals(terminalVO.getTerminalId()))
                          {
                            str1= "selected";
                          }
                          if(terminalBuffer.length()!=0 && terminalBuffer.length()!=1)
                          {
                            terminalBuffer.append(",");
                          }
                          terminalBuffer.append(terminalVO.getTerminalId());
                      %>
                      <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(terminalVO.getTerminalId())%>" <%=str1%>> <%=ESAPI.encoder().encodeForHTML(terminalVO.getTerminalId()+"-"+terminalVO.getPaymentName()+"-"+terminalVO.getCardType())%> </option>
                      <%
                        }
                        terminalBuffer.append(")");
                      %>
                    </select>
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
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" ></td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <button type="submit" class="buttonform" style="margin-left:40px; ">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
        <input type="hidden" value="<%=terminalBuffer.toString()%>" name="terminalbuffer">
      </form>
    </div>
  </div>
</div>
<div class="reporttable">
&lt;%&ndash;<div class="row" >
  <div class="col-lg-12">
    <div class="panel panel-default" >&ndash;%&gt;
      <div class="panel-heading" >
        Transaction Status
      </div><br><br>
      <p class="textb" align="center" style="font-weight: bold;font-size:12px; ">

      <table width="70%" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr>
          <td>
            <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">

              <tr>
                <td width="21%">&nbsp;</td>
                <td width="10%">&nbsp;</td>
                <td colspan="6" width="78%" class="textb">
                </td>
              </tr>
              <tr>
                <td width="21%">
                  <div align="left" class="textb"><b>Tracking ID</b></div>
                </td>
                <td width="10%" ><strong>:</strong></td>
                <td width="78%" colspan="6" class="textb"><%=trackingid%>
                </td>
              </tr>
              <tr>
                <td >&nbsp;</td>
                <td ></td>
                <td colspan="6" ></td>
              </tr>
              <tr>
                <td class="textb"><b>Order Amount</b></td>
                <td ><strong>:</strong></td>
                <td colspan="6" class="textb"><%=resAmount%>
                  &nbsp;&nbsp;</td>
              </tr>
              <tr>
                <td >&nbsp;</td>
                <td ></td>
                <td colspan="6" ></td>
              </tr>
              <tr>
                <td class="textb"><b>Description</b></td>
                <td ><strong>:</strong></td>
                <td colspan="6" class="textb"><%=orderid%></td>
              </tr>
              <tr>
                <td >&nbsp;</td>
                <td ></td>
                <td colspan="6" ></td>
              </tr>

              <%
                if(status.equalsIgnoreCase("Y"))
                {
                  status = "Approved";
                }
                else
                {
                  status = "Failed";
                }
              %>

              <tr>
                <td class="textb"><b>Status</b></td>
                <td><strong>:</strong></td>
                <td colspan="6" class="textb"><%=status%></td>
              </tr>

              <tr>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td colspan="6">&nbsp;</td>
              </tr>

              <tr>
                <td class="textb"><b>Message</b></td>
                <td><strong>:</strong></td>
                <td colspan="6" class="textb"><%=message%></td>
              </tr>
              <tr>
                <td width="21%">&nbsp;</td>
                <td width="10%">&nbsp;</td>
                <td colspan="6" width="78%">&nbsp;</td>
              </tr>
              <tr>
                <td class="textb"><b>Billing Descriptor</b></td>
                <td><strong>:</strong></td>
                <td colspan="6" class="textb"><%=billingDescriptor%></td>
              </tr>
              <tr>
                <td width="21%">&nbsp;</td>
                <td width="1%">&nbsp;</td>
                <td colspan="6" width="78%">
                </td>
              </tr>
              <tr>
                <td class="textb"></td>
                <td><strong></strong></td>
                <td colspan="6" class="textb">

                </td>
              </tr>

            </table>
          </td>
        </tr>
      </table>
    &lt;%&ndash;</div>
  </div>&ndash;%&gt;
</div>--%>

<%
  String uId = "";
  if(session.getAttribute("role").equals("submerchant"))
  {
    uId = (String) session.getAttribute("userid");
  }
  else
  {
    uId = (String) session.getAttribute("merchantid");
  }

  StringBuffer terminalBuffer = new StringBuffer();
  String terminalid = "";
  String errormsg1 = (String) request.getAttribute("error");
  if (errormsg1 != null)
  {
    //out.println("<center><li class=\"alert alert-danger alert-dismissable\"><b>"+errormsg1+"<br></b></li></center>");
    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg1 + "</h5>");
  }

  ManualRebillResponseVO manualRebillResponseVO = (ManualRebillResponseVO) request.getAttribute("recurringstatus");
  String resAmount = manualRebillResponseVO.getAmount();
  String trackingid = manualRebillResponseVO.getTrackingId();
  String orderid = manualRebillResponseVO.getDescription();
  String status = manualRebillResponseVO.getStatus();
  String message = manualRebillResponseVO.getErrorMessage();
  String billingDescriptor = manualRebillResponseVO.getBillingDescriptor();
%>

<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">

      <div class="row reporttable">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">

            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Transaction Status</strong>

              </h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <div class="widget-content padding">
              <div id="horizontal-form">
                <div class="table table-responsive">
                  <table class="table table-striped table-bordered" id="textheadid" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                    <tr >
                      <td class="tr0" align="center" style="color: white;border-bottom: 1px solid #ddd;">Tracking ID</td>
                      <td class="tr1" align="center"><%=trackingid%></td>
                    </tr>
                    <tr >
                      <td class="tr0" align="center" style="color: white;border-bottom: 1px solid #ddd;">Order Amount </td>
                      <td class="tr1" align="center"><%=resAmount%></td>
                    </tr>
                    <%-- <tr >
                         <td class="tr0" align="center" style="color: white;border-bottom: 1px solid #ddd;">Currency </td>
                         <td class="tr1" align="center"><%=currency%></td>
                     </tr>--%>
                    <tr >
                      <td class="tr0" align="center" style="color: white;border-bottom: 1px solid #ddd;">Description </td>
                      <td class="tr1" align="center"><%=orderid%></td>
                    </tr>
                    <%
                      if(status.equalsIgnoreCase("Y"))
                      {
                        status = "Approved";
                      }
                      else
                      {
                        status = "Failed";
                      }
                    %>
                    <tr >
                      <td class="tr0" align="center" style="color: white;border-bottom: 1px solid #ddd;">Status </td>
                      <td class="tr1" align="center"><%=status%></td>
                    </tr>
                    <tr >
                      <td class="tr0" align="center" style="color: white;border-bottom: 1px solid #ddd;">Message </td>
                      <td class="tr1" align="center"><%=message%></td>
                    </tr>

                    <tr >
                      <td class="tr0" align="center" style="color: white;">Billing Descriptor </td>
                      <td class="tr1" align="center"><%=billingDescriptor%></td>
                    </tr>
                  </table>


                  <%--<div class="form-group col-md-12 has-feedback" align="center">
                  <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Tracking ID: </label>

              </div>

              <div class="form-group col-md-12 has-feedback" align="center">
                  <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Order Amount: </label>
                  <%=resAmount%>
              </div>

              <div class="form-group col-md-12 has-feedback" align="center">
                  <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Description: </label>
                  <%=orderid%>
              </div>


              <%
                  if(status.equalsIgnoreCase("Y"))
                  {
                      status = "Approved";
                  }
                  else if(status.equalsIgnoreCase("P"))
                  {
                      status="Pending";
                  }
                  else
                  {
                      status = "Failed";
                  }
              %>

              <div class="form-group col-md-12 has-feedback" align="center">
                  <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Status: </label>
                  <%=status%>
              </div>

              <div class="form-group col-md-12 has-feedback" align="center">
                  <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Message: </label>
                  <%=message%>
              </div>

              <%
                  if(request.getAttribute("mandateId")!=null)
                  {
              %>
              <div class="form-group col-md-12 has-feedback" align="center">
                  <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">MandateId: </label>
                  <%=(String)request.getAttribute("mandateId")%>
              </div>


              <%
                  }
              %>
              <div class="form-group col-md-12 has-feedback" align="center">
                  <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Billing Descriptor: </label>
                  <%=functions.isValueNull(billingDescriptor)?billingDescriptor:""%>
              </div>--%>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

</body>
</html>