// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   oldProcessFIRC.java

package com.directi.pg;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.Properties;
import java.util.StringTokenizer;

// Referenced classes of package com.directi.pg:
//            Functions, Database

class oldProcessFIRC
{

    oldProcessFIRC()
    {
    }

    public static void main(String args[])
    {
        Logger logger = new Logger(oldProcessFIRC.class.getName());
        if (args.length != 3)
        {
            System.exit(0);
        }
        Object obj = null;
        String s1 = null;
        Object obj1 = null;
        int i = 0;
        boolean flag = false;
        int j = 0;
        try
        {
            FileInputStream fileinputstream = new FileInputStream(args[0] + "/ICICI.properties");
            DataInputStream datainputstream = new DataInputStream(fileinputstream);
            Properties properties = new Properties();
            properties.load(datainputstream);
            fileinputstream.close();
            datainputstream.close();
            s1 = properties.getProperty("PATHTOFIRC");
        }
        catch (Exception exception)
        {
            logger.error(args[0]);
           logger.error("ICICI Merchanid Not Found" + exception.toString());
            return;
        }
        StringBuffer stringbuffer = new StringBuffer();
        try
        {
            String s;
            if (args.length > 0)
                s = args[1];
            else
                throw new FileNotFoundException();
            BufferedReader bufferedreader = new BufferedReader(new FileReader(s1 + s));
            for (String s3 = null; (s3 = bufferedreader.readLine()) != null;)
                stringbuffer.append(s3 + "\r\n");

            bufferedreader.close();
        }
        catch (Exception exception1)
        {
            StringWriter stringwriter = new StringWriter();
            PrintWriter printwriter = new PrintWriter(stringwriter);
            exception1.printStackTrace(printwriter);
            logger.error(Functions.ShowMessage("Raw File Not Found", stringwriter.toString()));
            return;
        }
        String s2 = stringbuffer.toString();
        StringTokenizer stringtokenizer = new StringTokenizer(s2, "\r\n");
        StringTokenizer stringtokenizer1 = new StringTokenizer(s2, "\r\n");
        try
        {
            Connection connection = Database.getConnection();

            int k = 0;
            String s4 = new String("select sum(amount),count(*) from transaction_icicicredit where");
            StringBuffer stringbuffer1 = new StringBuffer();
            while (stringtokenizer1.hasMoreElements())
            {
                String s5 = ((String) stringtokenizer1.nextElement()).trim();
                StringTokenizer stringtokenizer2 = new StringTokenizer(s5, "^");
                i++;
                if (stringtokenizer2.hasMoreElements())
                {
                    String s6 = ((String) stringtokenizer2.nextElement()).trim();
                    String s7 = ((String) stringtokenizer2.nextElement()).trim();
                    String s8 = ((String) stringtokenizer2.nextElement()).trim();
                    String s9 = ((String) stringtokenizer2.nextElement()).trim();
                    if (k > 0)
                        stringbuffer1.append(" or ");
                    stringbuffer1.append(" (amount =" + s9);
                    stringbuffer1.append(" and status='settled' and captureresult like '%" + s8.substring(6) + "%')");
                }
                k++;
            }

            FileOutputStream fileoutputstream = new FileOutputStream(args[0] + "/fircQuery.txt");
            DataOutputStream dataoutputstream = new DataOutputStream(fileoutputstream);
            dataoutputstream.writeBytes(s4 + stringbuffer1.toString());
            ResultSet resultset = Database.executeQuery(s4 + stringbuffer1.toString(), connection);
            resultset.next();
            if (resultset.getDouble(1) != Double.parseDouble(args[2]))
            s4 = new String("select amount,transid,toid,date_format(from_unixtime(dtstamp),'%d-%m-%Y') as \"date\",date_format(from_unixtime(dtstamp),'%m%Y') as \"batch\" from transaction_icicicredit where ");
            for (ResultSet resultset1 = Database.executeQuery(s4 + stringbuffer1.toString(), connection); resultset1.next();)
            {
                StringBuffer stringbuffer2 = new StringBuffer("insert into icici_firc (transid,date,memberid,amount,batchno,dtstamp) values (");
                stringbuffer2.append("? ,");
                stringbuffer2.append("? ,");
                stringbuffer2.append("? ,");
                stringbuffer2.append("? ,");
                stringbuffer2.append("? ,");
                stringbuffer2.append("unix_timestamp(now()))");
                String s10 = stringbuffer2.toString();
                PreparedStatement pstmt=connection.prepareStatement(stringbuffer2.toString());
                pstmt.setString(1,resultset1.getString("transid"));
                pstmt.setString(2,resultset1.getString("date"));
                pstmt.setString(3,resultset1.getString("toid"));
                pstmt.setString(4,resultset1.getString("amount"));
                pstmt.setString(5,resultset1.getString("batch"));
                pstmt.setString(6,resultset1.getString("transid"));
                pstmt.setString(7,resultset1.getString("amount"));
                Database.executeUpdate(s10, connection);
                j++;
            }
        }
        catch (Exception exception2)
        {
            StringWriter stringwriter1 = new StringWriter();
            PrintWriter printwriter1 = new PrintWriter(stringwriter1);
            exception2.printStackTrace(printwriter1);
            logger.error(Functions.ShowMessage("stacktrace", stringwriter1.toString()));
        }
    }
}
