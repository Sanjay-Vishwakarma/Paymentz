package com.directi.ipmap.parser;

//import com.directi.ipmap.IPMapUtil;
import com.directi.pg.Logger;
import org.apache.regexp.RE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;

public class NewIPMapParser
{
    private static Logger log = new Logger(NewIPMapParser.class.getName());

    public NewIPMapParser()
    {
    }

    public static void main(String args[])
            throws Exception
    {
        String s = args[0];
        NewIPMapParser ipmapparser = new NewIPMapParser();
        Hashtable hashtable = ipmapparser.start(s);
        if (hashtable != null)
        {
            log.debug("Saving ipmap to " + s + ".out");
            ipmapparser.save(hashtable, s + ".out");
            log.debug("After saving ipmap to " + s + ".out");
        }
    }

    public Hashtable start(String fileName) throws Exception
    {

        String line = null;
        Hashtable countries = new Hashtable();
        BufferedReader in = new BufferedReader(new FileReader(fileName));

        // I will get format like 192.168.1.255|192.165.234.1|IN

        RE re = new RE("[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}\\|[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}\\|[a-zA-Z]{2}");

        line = in.readLine();

        //loop through the stuff we're interested in
        log.debug("Getting inside while");
        while (line != null) //Only check if there is line to parse. We will want to continue further parsing even if some lines in between the file does not match the re in which case we would skip those lines. But end ONLY if EOF.
        {
            if (re.match(line))
            {

                int start = line.indexOf("|");
                int end = line.indexOf("|", start + 1);

                String country = line.substring(end + 1).trim();

                String startIP = line.substring(0, start);
                String endIP = line.substring(start + 1, end);

               // long startAddr = IPMapUtil.getLongIP(startIP);
               // long endAddr = IPMapUtil.getLongIP(endIP);

                //log.debug(" startAddr " + startAddr + " and endaddr " + endAddr + " country " + country);

                if (country != null)
                {
                    //  cat.debug("FInal country update "+ country + " IP "+ ipv4);
                 //   update(countries, country, startAddr, endAddr);
                }
            }//end of if(re.match(line))
            line = in.readLine();
        }//while ends

        log.debug("Getting outside while");


        return countries;

    }//start ends


    public void save(Hashtable hashtable, String s)
            throws Exception
    {
        BufferedWriter bufferedwriter = new BufferedWriter(new FileWriter(s), 0x100000);
        int i = 0;
        for (Enumeration enumeration = hashtable.keys(); enumeration.hasMoreElements();)
        {
            String s1 = (String) enumeration.nextElement();
            LinkedList linkedlist = (LinkedList) hashtable.get(s1);
            i++;
            long al[] = coalate(linkedlist);
            log.debug("Countries count " + hashtable.size() + " Current row " + i + " arr.length " + al.length);
            for (int j = 0; j < al.length; j += 2)
            {
                bufferedwriter.write(s1 + "|");
                bufferedwriter.write(al[j] + "|");
                bufferedwriter.write("" + al[j + 1]);
                bufferedwriter.newLine();
            }

        }

        bufferedwriter.flush();
        bufferedwriter.close();
    }

    private long[] coalate(LinkedList linkedlist)
            throws Exception
    {
        Long along[] = (Long[]) linkedlist.toArray(new Long[0]);
        long al[] = new long[along.length];
        for (int i = 0; i < along.length; i++)
            al[i] = along[i].longValue();

        Arrays.sort(al);
        long al1[] = new long[al.length];
        for (boolean flag = false; !flag;)
        {
            flag = true;
            int j = 0;
            for (int l = 0; j < al.length; l += 2)
            {
                long l1 = al[j];
                long l2 = al[j + 1];
                if (j < al.length - 2)
                {
                    if (al[j + 2] == l2 + 1L || al[j + 2] == l2)
                    {
                        al[j + 1] = al[j + 3];
                        al1[l] = l1;
                        al1[l + 1] = al[j + 3];
                        j += 4;
                    }
                    else
                    {
                        al1[l] = l1;
                        al1[l + 1] = l2;
                        j += 2;
                    }
                    if (j < al.length)
                        flag = false;
                    else
                        flag = true;
                }
                else
                {
                    al1[l] = al[j];
                    al1[l + 1] = al[j + 1];
                    j += 2;
                    flag = true;
                }
            }

            al = al1;
        }

        int k;
        for (k = 0; k < al.length && al[k] > 0L; k++) ;
        long al2[] = new long[k];
        for (int i1 = 0; i1 < k; i1++)
            al2[i1] = al1[i1];

        return al2;
    }

    public void update(Hashtable hashtable, String s, long l, long l1)
            throws Exception
    {
        LinkedList linkedlist = (LinkedList) hashtable.get(s);
        if (linkedlist == null)
        {
            linkedlist = new LinkedList();
            linkedlist.add(new Long(l));
            linkedlist.add(new Long(l1));
            hashtable.put(s, linkedlist);
        }
        else
        {
            linkedlist.add(new Long(l));
            linkedlist.add(new Long(l1));
            hashtable.put(s, linkedlist);
        }
    }

    private static Connection getDBConn()
            throws Exception
    {
        log.debug("Entering getDBConn");
        try
        {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:25432/test99", "azaidi", "arsalanz");
            connection.setAutoCommit(false);
            log.debug("Leaving getDBConn");
            return connection;
        }
        catch (SQLException sqlexception)
        {
            log.error("------SQL Exception !! ----- ",sqlexception);
            while (sqlexception != null)
            {
                log.error("Error Message : " + sqlexception.getMessage());
                log.error("SQL State : " + sqlexception.getSQLState());
                log.error("Error Code : " + sqlexception.getErrorCode());
                sqlexception = sqlexception.getNextException();

            }
            throw sqlexception;
        }
    }
}
