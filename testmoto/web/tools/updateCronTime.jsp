<%@ page
        import="com.logicboxes.cron.AlarmEntry,com.logicboxes.cron.CronManager,java.util.Date, java.util.Hashtable, java.util.Iterator, java.util.List" %>
<%
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
                System.out.println("className : " + className);
                //alarm.updateAlarmTime();
                //--- Add the modified Cron back to the queue.
                cronManager.addCron(now);
                //cronManager.addCron(alarm);
                break;
            }
        }
    }

%>
<head>
    <title>Update Cron Time</title>
    <link rel="stylesheet" type="text/css" href="./styles.css">

</head>

<body>
Alarm settings for <BR>
Class: <%=updateClass%><BR>
Method: <%=updateMethod%><BR>
have been updated.

<p><a href="/testmoto/tools/listCrons.jsp">Return to Cron List</a>
</p>
</body>