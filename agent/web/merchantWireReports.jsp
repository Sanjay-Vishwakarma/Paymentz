<%@ page import="org.owasp.esapi.User" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="java.util.*" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.payoutVOs.MerchantWireVO" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 8/11/14
  Time: 3:18 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp"%>
<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("agentname"));%>
<%!
  private static Logger logger=new Logger("merchantWireReports.jsp");
  private static TerminalManager terminalManager = new TerminalManager();
%>

<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <script language="javascript">
    function getPdfFile(settleid)
    {
      if (confirm("Do You Really Want To Download The Selected File?"))
      {
        document.getElementById("pdfform"+settleid).submit();
      }
    }
    function getExcelFile(settleid)
    {
      if (confirm("Do You Really Want To Download The Selected File?"))
      {
        document.getElementById("viewsettletransfile"+settleid).submit();
      }
    }
  </script>
  <script type="text/javascript" language="JavaScript" src="/agent/javascript/merchant_terminal.js"></script>

  <title><%=company%> | Merchant Wire Reports</title>

  <style type="text/css">
    #myTable th{text-align: center;}

    #myTable td{
      font-family: Open Sans;
      font-size: 13px;
      font-weight: 600;
    }

    #myTable .button3 {
      text-indent: 0!important;
      width: 100%;
      height: inherit!important;
      display: block;
      color: #000000;
      padding: 0px;
      background: transparent!important;
      border: 0px solid #dedede;
      text-align: center!important;
      font-family: "Open Sans","Helvetica Neue",Helvetica,Arial,sans-serif;
      font-size: 12px;
    }
  </style>

  <link href="/agent/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/agent/datepicker/datepicker/bootstrap-datepicker.js"></script>
  <script src="/agent/NewCss/libs/jquery-icheck/icheck.min.js"></script>


</head>
<body class="bodybackground">
<%--<link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
<script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>--%>


<script language="javascript">
  function isint(form)
  {
    if (isNaN(form.numrows.value))
      return false;
    else
      return true;
  }
</script>
<script type="text/javascript">
    $('#sandbox-container input').datepicker({
    });
</script>
<script>
    $(function() {
        $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
    });
</script>
<%--<script>
    function download()
    {
        var answer = confirm("do you want to Download File?");
        if(answer == true)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
</script>--%>
<%
  session.setAttribute("submit","Member Wire Report");
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    Hashtable memberidDetails=agent.getAgentMemberDetailList((String) session.getAttribute("merchantid"));
    List<TerminalVO> terminalVO = terminalManager.getTerminalListByAgent(session.getAttribute("merchantid").toString());
    String memberid=request.getParameter("memberid");

    Functions functions = new Functions();
    //String terminalid = request.getParameter("terminal");
    String str="";
    String fdate=null;
    String tdate=null;

    String toid=null;
    String accountid=null;
    String terminalid=null;
    try
    {
      /*fdate = ESAPI.validator().getValidInput("fromdate",request.getParameter("fromdate"),"Days",10,true);
      tdate = ESAPI.validator().getValidInput("todate",request.getParameter("todate"),"Days",10,true);*/

      toid = ESAPI.validator().getValidInput("toid",request.getParameter("toid"),"Numbers",10,true);
      accountid = ESAPI.validator().getValidInput("accountid",request.getParameter("accountid"),"Numbers",10,true);
      terminalid = ESAPI.validator().getValidInput("terminalid",request.getParameter("terminalid"),"Numbers",10,true);
    }
    catch(Exception e)
    {
      logger.error("Exception while select",e);
      //out.println("<div class=\"reporttable\" style=\"margin-top:100px\">");
      //out.println(Functions.NewShowConfirmation1("Sorry", "Internal error while accessing data."));
      //out.println("</div>");
    }
    Date date = new Date();
    SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

    String Date = originalFormat.format(date);
    date.setDate(1);
    String fromDates = originalFormat.format(date);

    fdate = Functions.checkStringNull(request.getParameter("fromdate")) == null ? fromDates : request.getParameter("fromdate");
    tdate = Functions.checkStringNull(request.getParameter("todate")) == null ? Date : request.getParameter("todate");

    Calendar rightNow = Calendar.getInstance();
    if (fdate == null) fdate = "" + 1;
    if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

    String currentyear= ""+rightNow.get(rightNow.YEAR);
    str = str + "ctoken=" + ctoken;
    if (fdate != null) str = str + "&fromdate=" + fdate;
    if (tdate != null) str = str + "&todate=" + tdate;

    if (toid != null) str = str + "&toid=" + toid;
    if (accountid != null) str = str + "&accountid=" + accountid;
    if (terminalid != null) str = str + "&terminalid=" + terminalid;

%>
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">
      <form  name="form" method="post" action="/agent/net/MerchantWireReports?ctoken=<%=ctoken%>">
        <div class="row">
          <div class="col-sm-12 portlets ui-sortable">
            <div class="widget">
              <div class="widget-header transparent">
                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=company%>'s Wire Report</strong></h2>
                <div class="additional-btn">
                  <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                  <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                  <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                </div>
              </div>
              <%
                String error=(String ) request.getAttribute("error");
                if(functions.isValueNull(error))
                {
                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error + "</h5>");
                }

                String catchError=(String ) request.getAttribute("catchError");
                if(functions.isValueNull(catchError))
                {
                  out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + catchError + "</h5>");
                }
              %>
              <div class="widget-content padding">
                <div id="horizontal-form">
                  <input type="hidden" value="<%=ctoken%>" name="ctoken">
                  <%--<%
                      if(request.getParameter("MES")!=null)
                      {
                          String mes=request.getParameter("MES");
                          if(mes.equals("ERR"))
                          {
                              if(request.getAttribute("error")!=null)
                              {
                                  ValidationErrorList error= (ValidationErrorList) request.getAttribute("error");
                                  for(Object errorList : error.errors())
                                  {
                                      ValidationException ve = (ValidationException) errorList;
                                      out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + ve.getMessage() + "</h5>");
                                  }
                              }
                              else if(request.getAttribute("catchError")!=null)
                              {
                                  out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + request.getAttribute("catchError") + "</h5>");
                              }
                          }
                          else if(mes.equals("FILEERR"))
                          {
                              String errorF= (String) request.getAttribute("file");
                              if("no".equals(errorF))
                              {
                                  out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;File not found</h5>");
                              }
                              else
                              {
                                  ValidationErrorList error= (ValidationErrorList) request.getAttribute("error");
                                  for(Object errorList : error.errors())
                                  {
                                      ValidationException ve = (ValidationException) errorList;
                                      out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + ve.getMessage() + "</h5>");
                                  }
                              }
                          }
                      }
                  %>--%>

                  <div class="form-group col-md-4">
                    <label>From</label>
                    <input type="text" name="fromdate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">

                  </div>

                  <div class="form-group col-md-4">
                    <label>To</label>
                    <input type="text" name="todate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">

                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label>Is Paid</label>
                    <select size="1" name="ispaid" class="form-control">
                      <option value="">ALL</option>
                      <option value="paid">Paid</option>
                      <option value="unpaid">Unpaid</option>
                    </select>
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label>Merchant ID</label>
                    <select size="1" name="memberid" id="mid" class="form-control">
                      <option value="">--All--</option>
                      <%
                        String selected3 = "";
                        String key3 = "";
                        String value3 = "";
                        TreeMap treeMap = new TreeMap(memberidDetails);
                        Iterator itr = treeMap.keySet().iterator();
                        while (itr.hasNext())
                        {
                          key3 = (String) itr.next();
                          value3 = treeMap.get(key3).toString();
                          if (key3.equals(memberid))
                            selected3 = "selected";
                          else
                            selected3 = "";
                      %>
                      <option value="<%=key3%>" <%=selected3%> > <%=key3%>---<%=value3%></option>
                      <%
                        }
                      %>
                    </select>
                  </div>
                  <div class="form-group col-md-4 has-feedback">
                    <label >Terminal ID</label>
                    <select size="1" name="terminalid" id="tid" class="form-control">
                      <option value="">--All--</option>
                      <%
                        Iterator i=terminalVO.iterator();
                        String  terminalid2="";
                        //Map memberTerminal = new HashMap();
                        String terminalSelect = "";
                        String active = "";
                        for(TerminalVO terminalVO1 : terminalVO)
                        {
                          if (terminalVO1.getTerminalId().equals(terminalid))
                            terminalSelect = "selected";
                          else
                            terminalSelect = "";

                          if (terminalVO1.getIsActive().equalsIgnoreCase("Y"))
                          {
                            active = "Active";
                          }
                          else
                          {
                            active = "InActive";
                          }
                      %>
                      <option data-group="<%=terminalVO1.getMemberId()%>" value="<%=terminalVO1.getTerminalId()%>" <%=terminalSelect%>>
                        <%=terminalVO1.getTerminalId()%> - <%=terminalVO1.getPaymentTypeName()%> - <%=terminalVO1.getCardType()%> - <%=terminalVO1.getCurrency()%> - <%=active%>
                      </option>
                      <%
                        }
                      %>

                    </select>
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label style="color: transparent;">Search</label>
                    <button type="submit" name="B1" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-clock-o"></i>&nbsp;&nbsp;Search</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </form>


      <div class="row">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Summary Data</strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding" style="overflow-y: auto;">
              <%

                List<MerchantWireVO> merchantWireVOList = (List<MerchantWireVO>) request.getAttribute("merchantWireVOList");
                //String error = (String) request.getAttribute("error");
                if(merchantWireVOList != null)
                {
                  if(merchantWireVOList.size() > 0)
                  {
                    PaginationVO paginationVO = (PaginationVO) request.getAttribute("paginationVO");
                    paginationVO.setInputs(paginationVO.getInputs()+"&ctoken="+ctoken);
                    int srno=((paginationVO.getPageNo()-1)*paginationVO.getRecordsPerPage())+1;
              %>
              <table id="myTable" class="display table table table-striped table-bordered dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                <thead>
                <tr style="background-color: #7eccad !important;color: white;">
                  <th>Sr No</th>
                  <th>Terminal ID</th>
                  <th>Merchant ID</th>
                  <th>Settled Date</th>
                  <th>Start Date</th>
                  <th>End Date</th>
                  <th>Funded Amount</th>
                  <th>Unpaid Amount</th>
                  <th>Currency</th>
                  <th>Status</th>
                  <th>Report File</th>
                  <th>Transaction File</th>
                </tr>
                </thead>
                <tbody>
                <%
                  String style="class=td1";
                  SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );

                  for(MerchantWireVO merchantWireVO : merchantWireVOList)
                  {
                    String settleDate = "";
                    String settleStartDate = "";
                    String settleEndDate = "";
                    if(merchantWireVO.getSettleDate()!=null)
                    {
                      settleDate = targetFormat.format(targetFormat.parse(merchantWireVO.getSettleDate()));
                    }
                    if(merchantWireVO.getSettleDate()!=null)
                    {
                      settleStartDate = targetFormat.format(targetFormat.parse(merchantWireVO.getSettlementStartDate()));
                    }
                    if(merchantWireVO.getSettleDate()!=null)
                    {
                      settleEndDate = targetFormat.format(targetFormat.parse(merchantWireVO.getSettlementEndDate()));
                    }
                %>


                <tr>
                  <td valign="middle" data-label="Sr No" align="center"> <%=srno%></td>
                  <td valign="middle" data-label="Terminal ID" align="center"> <%=merchantWireVO.getTerminalId()%></td>
                  <td valign="middle" data-label="Merchant ID" align="center"> <%=merchantWireVO.getMemberId()%></td>
                  <td valign="middle" data-label="Settled Date" align="center"> <%=settleDate%></td>
                  <td valign="middle" data-label="Start Date" align="center"><%=settleStartDate%></td>
                  <td valign="middle" data-label="End Date" align="center"> <%=settleEndDate%></td>
                  <td valign="middle" data-label="Funded Amount" align="center"> <%=merchantWireVO.getNetFinalAmount()%></td>
                  <td valign="middle" data-label="Unpaid Amount" align="center"> <%=merchantWireVO.getUnpaidAmount()%></td>
                  <td valign="middle" data-label="Currency" align="center"> <%=merchantWireVO.getCurrency()%></td>
                  <td valign="middle" data-label="Status" align="center"> <%=merchantWireVO.getStatus()%></td>

                  <td valign="middle" data-label="Report File" align="center">
                    <form id="pdfform<%=merchantWireVO.getSettledId()%>" action="/agent/net/ActionMerchantWireReports?ctoken=<%=ctoken%>" method="post">
                      <input type="hidden" name="mappingid" value=<%=merchantWireVO.getSettledId()%>>
                      <input type="hidden" name="pdffile" value="<%=merchantWireVO.getReportFileName()%>">
                      <input type="hidden" name="action" value="sendPdfFile">
                    </form>
                    <a href="javascript: getPdfFile(<%=merchantWireVO.getSettledId()%>)"><img width="50" height="auto" border="0" src="/agent/images/pdflogo.png"></a>
                  </td>
                  <td valign="middle" data-label="Transaction File" align="center">
                    <form id="viewsettletransfile<%=merchantWireVO.getSettledId()%>" action="/agent/net/ActionMerchantWireReports?ctoken=<%=ctoken%>" method="post">
                      <input type="hidden" name="mappingid" value=<%=merchantWireVO.getSettledId()%>>
                      <input type="hidden" name="action" value="sendExcelFile">
                    </form>
                    <a href="javascript: getExcelFile(<%=merchantWireVO.getSettledId()%>)">
                      <img width="50" height="auto" border="0" src="/agent/images/excel.jpg">
                    </a>
                  </td>
                </tr>

                <%
                    srno++;
                  }
                %>
                </tbody>
              </table>
            </div>
            <div id="showingid"><strong>Showing Page <%=paginationVO.getPageNo()%> of <%=paginationVO.getTotalRecords()%> records</strong></div>
            <jsp:include page="page.jsp" flush="true">
              <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
              <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
              <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
              <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
              <jsp:param name="page" value="MerchantWireReports"/>
              <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
              <jsp:param name="orderby" value=""/>
            </jsp:include>
            <%
                }
                else
                {
                  out.println(Functions.NewShowConfirmation1("Result","No Record Found"));
                }
              }
              else
              {
                out.println(Functions.NewShowConfirmation1("Result","No Record Found"));
              }
            %>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
<%
  }
  else
  {
    response.sendRedirect("/agent/logout.jsp");
    return;
  }
%>
</body>
</html>