<%@ page import="java.util.*"%>
<%@ page import="com.logicboxes.util.Util"%>
<%@ page import="com.logicboxes.cron.CronManager"%>
<%@ page import="com.logicboxes.cron.AlarmEntry"%>
<head>
    <title>List of Active Crons</title>
    <link rel="stylesheet" type="text/css" href="./styles.css">

    <script language="Javascript">
        function removeCron(className, methodName)
        {
            if (confirm("Are you sure you want to remove this Cron?"))
            {
                document.cron.className.value = className;
                document.cron.methodName.value = methodName;
                document.cron.doWhat.value = 'Remove';
                document.cron.action = 'updateCronList.jsp';
                document.cron.submit();
            }
        }

        function updateCron(className, methodName)
        {
            document.cron.className.value = className;
            document.cron.methodName.value = methodName;
            document.cron.action = 'updateCronTime.jsp';
            document.cron.submit();
        }

        function reloadList()
        {
            document.cron.doWhat.value = 'Reload';
            document.cron.action = 'updateCronList.jsp';
            document.cron.submit();
        }

        function restoreCron(className, methodName)
        {
            if (confirm("Are you sure you want to Restore this Cron?"))
            {
                document.cron.className.value = className;
                document.cron.methodName.value = methodName;
                document.cron.doWhat.value = 'Restore';
                document.cron.action = 'updateCronList.jsp';
                document.cron.submit();
            }
        }

    </script>

</head>

<body>

<%
    try{
    CronManager cronManager = CronManager.getInstance();
    List list = cronManager.getAllCrons();

%>
<font color=red>Red</font> = Non-Idempotent<br>
Current time: <%="<B>" + new Date() + "</B>"%>
<table width="90%" border="1" bordercolorlight="#336699" bordercolordark="#FFFFFF">
    <!--	<tr>
     <td colspan="4"><input type="button" value="Reload Cron List" onclick="reloadList()"></td>
     </tr>  -->
    <tr>
        <td colspan="4">&nbsp;</td>
    </tr>

    <form name="cron" action="" method="post">
        <input type="hidden" name="className" value="">
        <input type="hidden" name="methodName" value="">
        <input type="hidden" name="doWhat" value="">
    </form>
    <tr>
        <td><FONT class=headerfont>Cron Params</font></td>
        <td><FONT class=headerfont>Next Alarm</font></td>
        <td><FONT class=headerfont>Remove Cron</font></td>
        <td><FONT class=headerfont>Run Cron</font></td>
        <!--
          <td class="tableheadings"><font face="verdana" size="2">Cron Class</font></td>
          <td class="tableheadings"><font face="verdana" size="2">Cron Method</font></td>
          -->
    </tr>
    <%
        int ct = 1;
        int count  = 0 ;
        try
        {
            for (Iterator i = list.iterator(); i.hasNext();)
            {
                if (ct > 2)
                    ct = 1;
                out.println("<tr class=\"td" + ct + "\">");
                AlarmEntry alarm = (AlarmEntry) i.next();
                Hashtable args = alarm.args;
                String className = alarm.className;//(String) args.get("classname");
                String methodName = alarm.methodName;//(String) args.get("methodname");
                if(!alarm.isIdempotent)
                {
                 out.println("<td><font face=\"verdana\" size=\"1\" color=red>Class: " + className + "<BR>Method: " + methodName + "</font></td>");
                }else
                {
                 out.println("<td><font face=\"verdana\" size=\"1\">Class: " + className + "<BR>Method: " + methodName + "</font></td>");
                }
                out.println("<td><font face=\"verdana\" size=\"1\">" + new Date(alarm.alarmTime) + "</font></td>");
                String remove = "removeCron('" + className + "','" + methodName + "')";
                String update = "updateCron('" + className + "','" + methodName + "')";
                //out.println("Call: " + call);
                out.println("<td><input type=\"button\" value=\"Remove\" onclick=\"" + remove + "\"></td>");
                out.println("<td><input type=\"button\" value=\"Run Now\" onclick=\"" + update + "\"></td>");
                //out.println("<td><font face=\"verdana\" size=\"1\">" + alarm.className + "</font></td>");
                //out.println("<td><font face=\"verdana\" size=\"1\">" + alarm.methodName + "</font></td>");
                out.println("</tr>");
                ct++;
                count++;
            }
        }
        catch (Exception e)
        {
            out.println("ERROR: in listActiveCrons" + e);
        }
    %>
</table>

Total No Of Crons = <%=count%>

<%
    list = cronManager.getRemovedCrons();
%>
<h4>List of Removed Crons</h4>
<table width="90%" border="1" bordercolorlight="#336699" bordercolordark="#FFFFFF">
    <tr>
        <td><FONT class=headerfont>Cron Params</font></td>
        <td><FONT class=headerfont>Next Alarm</font></td>
        <td><FONT class=headerfont>Restore Cron</font></td>
        <!--
          <td class="tableheadings"><font face="verdana" size="2">Cron Class</font></td>
          <td class="tableheadings"><font face="verdana" size="2">Cron Method</font></td>
          -->
    </tr>
    <%
        ct = 1;
        count = 0;
        try
        {
            for (Iterator i = list.iterator(); i.hasNext();)
            {
                if (ct > 2)
                    ct = 1;
                out.println("<tr class=\"td" + ct + "\">");
                AlarmEntry alarm = (AlarmEntry) i.next();
                //Hashtable args = alarm.args;
                String className = alarm.className; //(String) get("classname");
                String methodName = alarm.methodName;//(String) args.get("methodname");
                out.println("<td><font face=\"verdana\" size=\"1\">Class: " + className + "<BR>Method: " + methodName + "</font></td>");
                out.println("<td><font face=\"verdana\" size=\"1\">" + new Date(alarm.alarmTime) + "</font></td>");
                String restore = "restoreCron('" + className + "','" + methodName + "')";
		        out.println("<td><input type=\"button\" value=\"Restore\" onclick=\"" + restore + "\"></td>");
                out.println("</tr>");
                ct++;
                count++;
            }
        }
        catch (Exception e)
        {
            out.println("ERROR in listRemovedCrons: " + e);
        }
    %>
</table>
Total No Of Removed Crons = <%=count%> <br>
<%
  }
  catch(NoClassDefFoundError ne){
        out.println("ERROR in listRemovedCrons:"+Util.getStackTrace(ne));

  }
    catch (Throwable t){
        out.println("ERROR in listRemovedCrons: "+t);
    }
%>
<a href="index.htm">Go To Index</a>
</body>