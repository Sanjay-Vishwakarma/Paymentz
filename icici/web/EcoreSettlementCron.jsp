<%--
  Created by IntelliJ IDEA.
  User: jignesh.r
  Date: Jul 11, 2006
  Time: 1:20:54 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="ecoretab.jsp" %>
<%
    session.setAttribute("submit","Settle Cron");
    String memberid=nullToStr(request.getParameter("memberid"));
%>
<html>
<head>
    <title>Process FIRC Files</title>
    <script type="text/javascript" language="javascript">
        function submitForm()
        {
            var form=document.FIRCForm;
            if(isNaN(parseInt(form.merchantId.value)))
            {
                alert("Please enter numeric value for Merchant ID.");
                return false;
            }
            if(form.File.value.length == 0)
            {
                alert("Please enter valid FIRC file to upload.");
                return false;
            }
            if(form.startdate.value.length == 0)
            {
                alert("Please enter Start Date.");
                return false;
            }
            if(form.enddate.value.length == 0)
            {
                alert("Please enter End Date.");
                return false;
            }
            form.submit();
        }

        function check() {
            var  retpath = document.FIRCForm.File.value;
            var pos = retpath.lastIndexOf(".");
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
     </script>

        <%--function format_date_field(y, m, d) {
            return '' + y + '-' +
                    ((m > 9) ? m : '0' + m) + '-' +
                    ((d > 9) ? d : '0' + d);
        }
        m_list = Array("January", "February", "March",
                "April",   "May",      "June",
                "July",    "August",   "September",
                "October", "November", "December");
        //target = '';
        targets = Array();

        function set_date_field(y, m, d, t_idx) {
            eval(targets[t_idx] + ' = unescape(\'' + escape(format_date_field(y, m, d)) + '\')' );
        }

        function show_cal(target_a) {
            var d = new Date();
            show_cal_m(d.getFullYear(), d.getMonth(), target_a);
        }

        function show_cal_m(y,m, target_a) {
            t_idx = targets.push(target_a) - 1;
            write_cal_pop(y,m, open('about:blank','cal_win'+t_idx,'width=175,height=150'), t_idx);
        }

        function write_cal_pop(y,m, cal_win, t_idx) {
            cal_win.document.open();
            cal_win.document.writeln('<html><head><title>Date Picker</title>');
            cal_win.document.writeln('<style type="text/css">');
            cal_win.document.writeln('td {');
            cal_win.document.writeln('	font-family: Verdana, Arial, Helvetica, sans-serif;');
            cal_win.document.writeln('	font-size: 9pt;');
            cal_win.document.writeln('	color: #000000;');
            cal_win.document.writeln('	background-color: #CCCCCC;');
            cal_win.document.writeln('	text-align: center;');
            cal_win.document.writeln('}');
            cal_win.document.writeln('th {');
            cal_win.document.writeln('	font-family: Verdana, Arial, Helvetica, sans-serif;');
            cal_win.document.writeln('	font-size: 9pt;');
            cal_win.document.writeln('	color: #000000;');
            cal_win.document.writeln('	background-color: #CCE0FF;');
            cal_win.document.writeln('	text-align: center;');
            cal_win.document.writeln('}');
            cal_win.document.writeln('a {');
            cal_win.document.writeln('   color: #000000;');
            cal_win.document.writeln('   text-decoration: none;');
            cal_win.document.writeln('}');
            cal_win.document.writeln('</style></head><body leftmargin=2 topmargin=2 marginwidth=2 marginheight=2>');


            var one_day     = 1000 * 60 * 60 * 24;
            //var next_month  = (m==11) ? 0 : m + 1;

            d = new Date(y,m,1, 0,0,0,0);
            d = new Date(d.valueOf() - (one_day * 6));

            while (d.getDay() != 1) {
                d = new Date(d.valueOf() + one_day);
            }

            cal_win.document.writeln('<table align="center" width="100%" height="100%">');
            cal_win.document.writeln('<tr>');

            //PREV
            cal_win.document.write('<th><a href="javascript: opener.write_cal_pop(');
            if (m==0) {  cal_win.document.write((y-1) + ', 11'); }
            else      {  cal_win.document.write(y + ', ' + (m-1)); }
            cal_win.document.writeln(', self, ' + t_idx + ');">&lt;</a></td>');

            //CUR.
            cal_win.document.writeln('<th colspan=5><b>' + m_list[m] + ' ' + y + '</b></td>');

            //NEXT
            cal_win.document.write('<th><a href="javascript: opener.write_cal_pop(');
            if (m==11) {  cal_win.document.write((y+1) + ', 0');    }
            else       {  cal_win.document.write(y + ', ' + (m+1)); }
            cal_win.document.writeln(', self, ' + t_idx + ');">&gt;</a></td>');

            cal_win.document.writeln('</tr>');

            //day of week header
            cal_win.document.writeln('<tr><th>M</th><th>T</th><th>W</th><th>T</th><th>F</th><th>S</th><th>S</th></tr>');

            //DAYS
            day_count = 0;
            while (day_count < 42) {
                day_count++

                if (d.getDay() == 1) { cal_win.document.writeln('<tr>'); }
                cal_win.document.writeln('<td>' + (d.getMonth()==m ? '<b>' : ''));
                cal_win.document.writeln('<a href="javascript: opener.set_date_field(' +
                        d.getFullYear() + ', ' + (d.getMonth()+1) + ', ' + d.getDate() +
                        ', ' + t_idx + '); self.close();">' +  d.getDate() + '</a>');

                cal_win.document.writeln(d.getMonth()==m ? '</b></td>' : '</td>');
                //next
                //d = new Date(d.valueOf() + one_day); //replaced to stop double day in oct error
                d.setDate(d.getDate() + 1);
                //if (d.getMonth() == next_month && d.getDay() == 1) {  break;  }
            }


            cal_win.document.writeln('</table>');
            cal_win.document.writeln('</body></html>');
            cal_win.document.close();
        }
    </script>

--%>
    <%--You can edit this function if you need the date in a different format--%>
        <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>
                <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>

    <script type="text/javascript">
        $('#sandbox-container input').datepicker({

        });
    </script>
    <script>
        $(function() {
            $( ".datepicker" ).datepicker();
//            $("#yourinput").datepicker( "setDate" , "7/11/2005" );

        });
    </script>
</head>

<body class="bodybackground">

    <form name = "FIRCForm" action="/icici/ecoresettlementfile.jsp?ctoken=<%=ctoken%>" method="post" ENCTYPE="multipart/form-data">
        <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
        <div class="row" style="margin-top: 0px">
            <div class="col-lg-12">
                <div class="panel panel-default"style="margin-top: 0px">
                    <div class="panel-heading" >

                    Ecore Settlement Files
                    </div>


                    <table align="center" width="70%" cellpadding="2" cellspacing="2">

                        <tbody>
                        <tr>
                            <td>

                                <table border="0" cellpadding="5" cellspacing="0" width="70%" align="center">


                                    <tbody>
                                    <tr><td colspan="4">&nbsp;</td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                                        <td style="padding: 3px" width="33%" class="textb" for="mid">Merchant ID</td>
                                        <td style="padding: 3px" width="20%" class="textb">:</td>
                                        <td style="padding: 3px" width="50%" class="textb">
                                           <%-- <input name="merchantId" type="text" value="" class="txtbox">--%>
                                               <input name="toid" id="mid" value="<%=memberid%>" class="txtbox" autocomplete="on">
                                        </td>

                                    </tr>

                                    <tr>
                                        <td style="padding: 3px" class="textb">&nbsp;</td>
                                        <td style="padding: 3px" class="textb">Settlement File</td>
                                        <td style="padding: 3px" class="textb">:</td>
                                        <td style="padding: 3px">
                                            <input name="File" type="file" value="">
                                        </td>
                                    </tr>

                                    <tr>
                                        <td style="padding: 3px" class="textb">&nbsp;</td>
                                        <td style="padding: 3px" class="textb">Starting Date</td>
                                        <td style="padding: 3px" class="textb">:</td>
                                        <td style="padding: 3px">
                                            <input type="text" readonly class="datepicker" name="startdate">
                                        </td>
                                    </tr>

                                    <tr>
                                        <td style="padding: 3px" class="textb">&nbsp;</td>
                                        <td style="padding: 3px" class="textb">Ending Date</td>
                                        <td style="padding: 3px" class="textb">:</td>
                                        <td style="padding: 3px">
                                            <input type="text" readonly class="datepicker" name="enddate">

                                        </td>
                                    </tr>



                                    <tr>
                                        <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                                        <td style="padding: 3px" width="43%" class="textb"></td>
                                        <td style="padding: 3px" width="5%" class="textb"></td>
                                        <td style="padding: 3px" width="50%" class="textb">
                                            <button name="mybutton" type="submit" value="Upload" onclick="return check()" class="buttonform">
                                                Upload</button>

                                        </td>
                                    </tr>

                                    </tbody>
                                </table>

                            </td>
                        </tr>
                        </tbody>
                    </table>


                </div>
            </div>
        </div>
    </form>
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