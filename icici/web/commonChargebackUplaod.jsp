<%--
  Created by IntelliJ IDEA.
  User: Nishant
  Date: 4/26/13
  Time: 1:48 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="java.util.List" %>
<%@ include file="index.jsp" %>
<html>
<head>
  <title>Bank Details> Bulk ChargeBack Upload</title>
  <%-- <script type="text/javascript" language="JavaScript">
     function check()
     {
       var  retpath = document.FIRCForm.File.value;
       var pos = retpath.indexOf(".");
       var filename="";
       if (pos != -1)
         filename = retpath.substring(pos + 1);
       else
         filename = retpath;
       if (filename==('xls')) {
         return true;
       }
       alert('Please select a .xls file instead!');
       return false;
     }
 --%>
  <%--<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.7.7/xlsx.core.min.js"></script>--%>
  <script type="text/javascript" src="/icici/javascript/xlsx.flow.js"></script>

  <%--<script>
    $(document).ready(function(){
      $('#File').change(handleFile);
    });

    function handleFile(e)
    {
      document.getElementById("submit").disabled = false;
      var  retpath = document.FIRCForm.File.value;
      var pos = retpath.indexOf(".");
      var filename="";
      if (pos != -1)
        filename = retpath.substring(pos + 1);
      else
        filename = retpath;
      if (filename==('xls'))
      {
        //Get the files from Upload control
        var files = e.target.files;
        var i, f;
        //Loop through files
        for (i = 0, f = files[i]; i != files.length; ++i)
        {
          var reader = new FileReader();
          var name = f.name;
          reader.onload = function (e)
          {
            var data = e.target.result;

            var result;
            var binary = "";

            var bytes = new Uint8Array(e.target.result);

            var length = bytes.byteLength;

            for (var i = 0; i < length; i++)
            {
              binary += String.fromCharCode(bytes[i]);
            }

            var workbook = XLSX.read(binary, {type: 'binary', cellDates: true, cellStyles: true});

            var sheet_name_list = workbook.SheetNames;
            sheet_name_list.forEach(function (y)
            { /* iterate through sheets */
              //Convert the cell value to Json
              var roa = XLSX.utils.sheet_to_json(workbook.Sheets[y]);
              if (roa.length > 0)
              {
                result = roa;
              }
            });
            //Get the first column first cell value
            if (result == undefined)
            {
              document.getElementById("submit").disabled = true;
              alert("Please select a valid xls file!")
            }

          };
          reader.readAsArrayBuffer(f);
        }
      }else{
        alert('Please select a .xls file instead!');
        document.getElementById("submit").disabled = true;
      }
    }

  </script>--%>


  <script>
    $(function () {

        $('#File').change(function () {
          document.getElementById("submit").disabled = false;
          var  retpath = document.FIRCForm.File.value;
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
                  document.getElementById("submit").disabled = true;
                }

              };
              fileReader.readAsArrayBuffer(file);
            }
          }
          else{
            alert('Please select a .xls file instead!');
            document.getElementById("submit").disabled = true;
          }
        });

    });
  </script>

</head>
<body>
<%
  ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    String gatewayName = "";
    String error= (String) request.getAttribute("ERROR");
    gatewayName = Functions.checkStringNull(request.getParameter("gateway")) == null ? "" : request.getParameter("gateway");
    List<String> bankNames = GatewayTypeService.loadGateway();
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading">
        Bulk Chargeback Upload
        <div style="float: right;">
          <form action="/icici/bulkChargebackUpload.jsp?ctoken=<%=ctoken%>" method="POST">
            <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
            <button type="submit" class="addnewmember">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Upload Common Chargeback
            </button>
          </form>
        </div>
      </div>
      <br>
      <form name = "FIRCForm" action="/icici/servlet/CommonChargebackUpload?ctoken=<%=ctoken%>" method="post" ENCTYPE="multipart/form-data" >
        <table  border="0" cellpadding="5" cellspacing="0" align="center">
          <tr>
            <td>
              <table  border="0" cellpadding="5" cellspacing="0" align="center">
                <tr>
                  <td colspan="7"><%
                    String message = (String) request.getAttribute("res");
                    if (message != null)
                    {
                      out.println(Functions.ShowMessage("Message", message));
                    }
                    if (error != null)
                    {
                      out.println(error);
                    }
                  %></td>
                </tr>
                <tr>
                  <td colspan="6"></td>
                </tr>
                <tr>
                  <td colspan="6"></td>
                </tr>
                <tr>
                  <td colspan="6"></td>
                </tr>
                <tr>
                  <td class="textb" align="left" colspan="2"><b>Bank Name:</b></td>
                  <td class="textb" colspan="4" align="center">
                    <select class="txtbox" name="gateway" style="width: 280px">
                      <option value="">Select Bank Name</option>
                      <%
                        StringBuilder sb = new StringBuilder();
                        for (String gatewayType : bankNames)
                        {
                          String st = "";
                          if (gatewayType != null)
                          {
                            if (gatewayName.equalsIgnoreCase(gatewayType))
                              st = "<option value='" + gatewayType + "'selected>" + gatewayType + "</option>";
                            else
                              st = "<option value='" + gatewayType + "'>" + gatewayType + "</option>";
                            sb.append(st);
                          }
                        }
                      %>
                      <%=sb.toString()%>
                    </select>
                  </td>
                </tr>
                <tr>
                  <td colspan="6">&nbsp;</td>
                </tr>
                <tr>
                  <td class="textb" colspan="2" align="left"><b>Bank Chargeback File:</b></td>
                  <td colspan="4" align="center"><input name="File" id="File" type="file" value="choose File"
                                                        style="width: 280px" accept="application/vnd.ms-excel"></td>
                </tr>
                <tr>
                  <td colspan="6">&nbsp;</td>
                </tr>
                <tr>
                  <td colspan="6" align="center">
                    <button name="mybutton" type="submit" id="submit" value="Upload" class="buttonform" >Upload</button>
                  </td>
                </tr>
                <tr>
                  <td colspan="6">&nbsp;</td>
                </tr>
              </table>
        </table>
      </form>
    </div>
  </div>
</div>
<%}
else
{
  response.sendRedirect("/icici/logout.jsp");
  return;

}%>
</body>
</html>