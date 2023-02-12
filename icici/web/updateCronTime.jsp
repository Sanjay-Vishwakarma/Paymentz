<%@ page
        import="com.logicboxes.cron.AlarmEntry,com.logicboxes.cron.CronManager,org.owasp.esapi.User, java.util.Date, java.util.Hashtable, java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ include file="index.jsp"%>

<%   ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {


    CronManager cronManager = CronManager.getInstance();
    List list;

    String updateClass = request.getParameter("className");
    String updateMethod = request.getParameter("methodName");

    if ((updateClass != null && !"".equals(updateClass)) && (updateMethod != null && !"".equals(updateMethod)))
    {
        list = cronManager.getAllCrons();
        for (Iterator i = list.iterator(); i.hasNext();)
        {
            AlarmEntry alarm = (AlarmEntry) i.next();
            Hashtable args = alarm.args;
            String className = alarm.className;//(String) args.get("classname");
            String methodName = alarm.methodName;//(String) args.get("methodname");
            if (updateClass.equals(className) && updateMethod.equals(methodName))
            {
                /*--- Crons are stored in a SortedSet with sorting on the alarm time.
                    Since we are changing the alarm time, we need to remove the Cron and then add it
                    so that it gets inserted in the correct position.
                */
                //cronManager.removeCron(alarm);

                /*String dayofmonth = ApplicationProperties.getProperty(methodName + ".dayofmonth");
                if(dayofmonth != null)
                {
                    try
                    {
                        int dayOfMonth = Integer.parseInt(dayofmonth);
                        alarm.dayOfMonth = dayOfMonth;
                    }
                    catch(NumberFormatException e)
                    {
                    }
                }

                String dayofweek = ApplicationProperties.getProperty(methodName + ".dayofweek");
                if(dayofweek != null)
                {
                    try
                    {
                        int dayOfWeek = Integer.parseInt(dayofweek);
                        alarm.dayOfWeek = dayOfWeek;
                    }
                    catch(NumberFormatException e)
                    {
                    }
                }

                String hour = ApplicationProperties.getProperty(methodName + ".hour");
                if(hour != null)
                {
                    try
                    {
                        int Hour = Integer.parseInt(hour);
                        alarm.hour = Hour;
                    }
                    catch(NumberFormatException e)
                    {
                    }
                }

                String min = ApplicationProperties.getProperty(methodName + ".min");
                if(min != null)
                {
                    try
                    {
                        int minute = Integer.parseInt(min);
                        alarm.minute = minute;
                    }
                    catch(NumberFormatException e)
                    {
                    }
                }*/

                AlarmEntry now = new AlarmEntry(new Date(System.currentTimeMillis() + 6000),
                                                alarm.className,
                                                alarm.methodName,
                                                args);

                //alarm.updateAlarmTime();
                //--- Add the modified Cron back to the queue.
                cronManager.addCron(now);
                //cronManager.addCron(alarm);
                break;
            }
        }
    }

%>
<html>
<head>
    <title>Update Cron Time</title>
    <link rel="stylesheet" type="text/css" href="./styles.css">

</head>

<body>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Alarm Settings Details
            </div>
            <table width="100%">
                <tr>
                    <td>
                        &nbsp;
                    </td>
                </tr>
                <tr>
                    <td class="textb" align="left" style="font-weight: bold;font-size:13px ">
                        &nbsp;&nbsp;&nbsp;Class: <%=updateClass%>
                    </td>
                </tr>
                <tr>
                    <td>
                        &nbsp;
                    </td>
                </tr>
                <tr>
                    <td class="textb" align="" style="font-weight: bold;font-size:13px">
                        &nbsp;&nbsp;&nbsp;Method: <%=updateMethod%>
                    </td>
                </tr>
                <tr>
                    <td>
                        &nbsp;
                    </td>
                </tr>
                <tr>
                    <td class="textb" align="left" style="font-weight: bold;font-size:13px;margin-right:15px;">
                        &nbsp;&nbsp;&nbsp;Status:Running
                    </td>
                </tr>
                <tr>
                    <td>
                        &nbsp;
                    </td>
                </tr>
                <%--<tr>
                    <td class="textb" align="left" style="font-weight: bold;font-size:13px ">
                        <a href="/icici/listCrons.jsp?ctoken=<%=ctoken%>">Return to Cron List</a>
                    </td>
                </tr>--%>

            </table>
     </div>
    </div>
</div>

</body>
</html>

<%
    }
    else
    {
    response.sendRedirect("/icici/logout.jsp");
    return;
    }
%>