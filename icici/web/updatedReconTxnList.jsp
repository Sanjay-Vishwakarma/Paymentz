<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="static com.directi.pg.Functions.convertStringtoInt" %>
<%@ page import="java.util.*" %>
<%@ include file="index.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<META content="text/html; charset=windows-1252" http-equiv=Content-Type>
<meta http-equiv="Expires" content="0">
<meta http-equiv="Pragma" content="no-cache">
<%--
  Created by IntelliJ IDEA.
  User: Namrata Bari
  Date: 6/11/2020
  Time: 2:13 PM
  To change this template use File | Settings | File Templates.
--%>
<%
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

  TreeMap<String,String> statushash = new TreeMap<>();

  statushash.put("authfailed", "Auth Failed");
  statushash.put("authstarted", "Auth Started");
  statushash.put("authstarted_3D", "Auth Started 3D");
  statushash.put("chargeback", "Chargeback");
  statushash.put("cancelstarted", "Cancel Inititated");
  statushash.put("capturestarted", "Capture Started");
  statushash.put("capturesuccess", "Capture Success");
  statushash.put("payoutcancelsuccessful","Payout Cancel Started");
  statushash.put("payoutstarted", "Payout Started");
  statushash.put("markedforreversal", "Reversal Request Sent");
%>
<html>
<head>
  <title></title>
  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script src="/icici/css/jquery-ui.min.js"></script>
  <script src="/icici/javascript/autocomplete.js"></script>
  <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>
  <script>
    $(function () {

      $('#File').change(function () {
        document.getElementById("Upload").disabled = false;
        var  retpath = document.FIRCForm.File.value;
        console.log(retpath);
        var pos = retpath.indexOf(".");
        var filename="";
        if (pos != -1)
          filename = retpath.substring(pos + 1);
        else
          filename = retpath;
        if (filename==('xls'))
        {
          var files = $('#File').get(0).files;
          if (files.length > 0)
          {
            var file = files[0];

            var fileReader = new FileReader();
            fileReader.onloadend = function (e)
            {
              var arr = (new Uint8Array(e.target.result)).subarray(0, 4);
              var header = '';
              for (var i = 0; i < arr.length; i++)
              {
                header += arr[i].toString(16);
              }

              if(header != "d0cf11e0"){
                alert('Please select a .xls file instead!');
                document.getElementById("Upload").disabled = true;
              }

            };
            fileReader.readAsArrayBuffer(file);
          }
        }
        else{
          alert('Please select a .xls file instead!');
          document.getElementById("Upload").disabled = true;
        }
      });

    });
  </script>

  <script language="javascript">
    function ToggleAll(checkbox)
    {
      flag = checkbox.checked;
      var checkboxes = document.getElementsByName("trackingid");
      var total_boxes = checkboxes.length;

      for(i=0; i<total_boxes; i++ )
      {
        checkboxes[i].checked =flag;
      }
    }

    /*function checkRefundAmount(trackingid)
     {
     var fieldname = "refundamount_"+trackingid;
     if (isNaN(document.getElementById(fieldname).value) || (document.getElementById(fieldname).value) <=0)
     {
     alert("Please Enter a Valid Refund Amount greater than 0 for tracking id "+trackingid);
     return false;
     }
     return true;
     }*/

  </script>

</head>
<body>
<%
  String memberid=nullToStr(request.getParameter("toid"));
  String accountID=nullToStr(request.getParameter("accountid"));
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    String str="";
    String fdate=null;
    String tdate=null;
    String fmonth=null;
    String tmonth=null;
    String fyear=null;
    String tyear=null;
    String toid=null;
    String accountid=null;
    String trackingid=null;
    String discription=null;
    String paymentId=null;
    String status;
    try
    {
      fdate = ESAPI.validator().getValidInput("fdate",request.getParameter("fdate"),"Days",2,true);
      tdate = ESAPI.validator().getValidInput("tdate",request.getParameter("tdate"),"Days",2,true);
      fmonth = ESAPI.validator().getValidInput("fmonth",request.getParameter("fmonth"),"Months",2,true);
      tmonth =  ESAPI.validator().getValidInput("tmonth",request.getParameter("tmonth"),"Months",2,true);
      fyear = ESAPI.validator().getValidInput("fyear",request.getParameter("fyear"),"Years",4,true);
      tyear = ESAPI.validator().getValidInput("tyear",request.getParameter("tyear"),"Years",4,true);
      toid =  ESAPI.validator().getValidInput("toid",request.getParameter("toid"),"Numbers",15,true);
      accountid =  ESAPI.validator().getValidInput("accountid",request.getParameter("accountid"),"Numbers",15,true);
      trackingid = ESAPI.validator().getValidInput("trackingid",request.getParameter("trackingid"),"Numbers",15,true);
      paymentId =  ESAPI.validator().getValidInput("paymentnumber",request.getParameter("paymentnumber"),"SafeString",100,true);
      discription = ESAPI.validator().getValidInput("description",request.getParameter("description"),"SafeString",50,true);
      status =  ESAPI.validator().getValidInput("status",request.getParameter("status"),"SafeString",50,true);
    }
    catch(ValidationException e)
    {

    }
    Calendar rightNow = Calendar.getInstance();
    String currentyear= "" + rightNow.get(rightNow.YEAR);
    if (fdate == null) fdate = "" + 1;
    if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);
    if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
    if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);
    if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
    if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);
    status = Functions.checkStringNull(request.getParameter("status"));

    str = str + "ctoken=" + ctoken;
    if (fdate != null) str = str + "&fdate=" + fdate;
    if (tdate != null) str = str + "&tdate=" + tdate;
    if (fmonth != null) str = str + "&fmonth=" + fmonth;
    if (tmonth != null) str = str + "&tmonth=" + tmonth;
    if (fyear != null) str = str + "&fyear=" + fyear;
    if (tyear != null) str = str + "&tyear=" + tyear;
    if (status != null) str = str + "&status=" + status;
    if (toid != null) str = str + "&toid=" + toid;
    if (accountid != null) str = str + "&accountid=" + accountid;
    if (trackingid != null) str = str + "&trackingid=" + trackingid;
    if (paymentId != null) str = str + "&paymentnumber=" + paymentId;
    if (discription != null) str = str + "&description=" + discription;
    int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
    int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading">
        Reconciliation Transaction List
      </div>
      <form name = "FIRCForm" action="/icici/servlet/UpdateReconciliationTxn?ctoken=<%=ctoken%>" method="post" ENCTYPE="multipart/form-data" >
        <table style="margin-top: 0.5%;margin-left: 48%;">
          <tr>
            <td class="textb" colspan="2" align="left"><b>Upload Transaction List File</b></td>
            <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
            <td align="center"><input name="File" id="File" type="file" value="choose File"
                                      style="width: 280px" accept="application/vnd.ms-excel"></td>
            <td class="textb">
              <button type="submit" id="Upload" class="buttonform" style="height: 24px;">
                <i class="fa fa-clock-o"></i>
                &nbsp;&nbsp;Upload
              </button>
            </td>
          </tr>
        </table>
      </form>
      <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken" id="ctoken">

      <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">

        <tr>
          <td>
            <form action="/icici/servlet/CommonReconList?ctoken=<%=ctoken%>" method="post" name="forms" >
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">


                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >From</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <select size="1" name="fdate" class="textb" value="<%=request.getParameter("fdate")==null?"":request.getParameter("fdate")%>" >
                      <%
                        if (fdate != null)
                          out.println(Functions.dayoptions(1, 31, fdate));
                        else
                          out.println(Functions.printoptions(1, 31));
                      %>
                    </select>
                    <select size="1" name="fmonth" class="textb" value="<%=request.getParameter("fmonth")==null?"":request.getParameter("fmonth")%>">
                      <%
                        if (fmonth != null)
                          out.println(Functions.newmonthoptions(1, 12, fmonth));
                        else
                          out.println(Functions.printoptions(1, 12));
                      %>
                    </select>
                    <select size="1" name="fyear" class="textb" value="<%=request.getParameter("fyear")==null?"":request.getParameter("fyear")%>">
                      <%
                        if (fyear != null)
                          out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), fyear));
                        else
                          out.println(Functions.printoptions(2005, 2013));
                      %>
                    </select>
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >To</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <select size="1" name="tdate" class="textb">

                      <%
                        if (tdate != null)
                          out.println(Functions.dayoptions(1, 31, tdate));
                        else
                          out.println(Functions.printoptions(1, 31));
                      %>
                    </select>

                    <select size="1" name="tmonth" class="textb">

                      <%
                        if (tmonth != null)
                          out.println(Functions.newmonthoptions(1, 12, tmonth));
                        else
                          out.println(Functions.printoptions(1, 12));
                      %>
                    </select>

                    <select size="1" name="tyear" class="textb" style="width:54px;">

                      <%
                        if (tyear != null)
                          out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), tyear));
                        else
                          out.println(Functions.printoptions(2005, 2013));
                      %>
                    </select>
                  </td>

                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >Account ID</td>
                  <td width="3%" class="textb"></td>
                  <td width="10%" class="textb">
                    <input name="accountid" id="accountid1" value="<%=accountID%>" class="txtbox" autocomplete="on">
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >Merchant ID</td>
                  <td width="3%" class="textb"></td>
                  <td width="10%" class="textb">
                    <input name="toid" id="memberid1" value="<%=memberid%>" class="txtbox" autocomplete="on">

                    <%--</td>
                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="8%" class="textb" >Account ID</td>
                    <td width="3%" class="textb"></td>
                    <td width="10%" class="textb">
                        <input name="accountid" id="accountid1" value="<%=accountID%>" class="txtbox" autocomplete="on">
                    </td>--%>
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
                  <td width="8%" class="textb" >Tracking ID</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input maxlength="500" type="text" name="trackingid" class="txtbox"  value="<%=request.getParameter("trackingid")==null?"":request.getParameter("trackingid")%>">
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">Payment Order NO</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input maxlength="100" type="text" name="paymentnumber" style="width:300px;" class="txtbox"  value="<%=request.getParameter("paymentnumber")==null?"":request.getParameter("paymentnumber")%>">
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Order ID</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input maxlength="15" type="text" name="description" class="txtbox" value="<%=request.getParameter("description")==null?"":request.getParameter("description")%>">
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
                  <td width="8%" class="textb" >Status</td>
                  <td width="3%" class="textb" ></td>
                  <td width="12%" class="textb">
                    <select size="1" name="status" class="txtbox"   >
                      <option value="">All</option>
                      <%
                        Set statusSet = statushash.keySet();
                        Iterator iterator=statusSet.iterator();
                        String selected = "";
                        String key = "";
                        String value = "";

                        while (iterator.hasNext())
                        {
                          key = (String)iterator.next();
                          value = (String) statushash.get(key);

                          if (key.equals(status))
                            selected = "selected";
                          else
                            selected = "";

                      %>
                      <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(key)%>" <%=selected%>><%=ESAPI.encoder().encodeForHTML(value)%></option>
                      <%
                        }
                      %>
                    </select>
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>

                  <td width="12%" class="textb">
                    <button type="submit" class="buttonform">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </td>
                </tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                </tr>
              </table>
            </form>
          </td>
        </tr>
      </table>

    </div>
  </div>
</div>

<div class="reporttable">
  <%
    String Msg = (String) request.getAttribute("msg");
    String Result = (String) request.getAttribute("Result");

  %>
  <table align="center" width="80%" cellpadding="2" cellspacing="2" ><tr><td>

  <table bgcolor="#ecf0f1" width="100%" align="center" cellpadding="0" cellspacing="0">

    <tr height=30>
      <td colspan="3" bgcolor="#34495e"  class="texthead" align="center"><font color="#FFFFFF" size="2" face="Open Sans,Helvetica Neue,Helvetica,Arial,Palatino Linotype', 'Book Antiqua, Palatino, serif">  <%=Msg%></font></td>
      </tr>

    <tr><td>&nbsp;</td></tr>

    <tr><td align="center" class="textb"><%=Result%></td></tr>
    <tr><td>&nbsp;</td></tr>
    </table> </tr></td> </table>

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
<%!
  public static String nullToStr(String str)
  {
    if(str == null)
      return "";
    return str;
  }
%>