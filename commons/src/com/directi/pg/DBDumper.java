/**
 * Created by IntelliJ IDEA.
 * User: jignesh.r
 * Date: Dec 6, 2006
 * Time: 6:41:36 PM
 * To change this template use File | Settings | File Templates.
 */
package com.directi.pg;

import com.logicboxes.util.ApplicationProperties;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.zip.ZipInputStream;


public class DBDumper
{
    private static Logger log = new Logger(DBDumper.class.getName());
    private static String downloadURL = null;
    private static String fileOutPath = null;

    static
    {
        downloadURL = ApplicationProperties.getProperty("IP_TO_COUNTRY_DOWNOAD_URL");
        fileOutPath = ApplicationProperties.getProperty("MPR_FILE_STORE");
    }

    public static void loadIpToCountryFile() throws SystemError
    {
        long startTime = System.currentTimeMillis();

        try
        {
            downloadIpToCountryFile();
            updateIpToCountryDatabase();
            log.debug("total time taken to load ip to country list = " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds");
        }
        catch (Exception e)
        {
            log.error("Error while downlaoding and updating db for ip country map",e);
            Mail.sendAdminMail("Error while downlaoding and updating db for ip country map", Functions.getStackTrace(e));
        }
    }

    private static void updateIpToCountryDatabase() throws Exception
    {
        // The "load data" command of mysql will only work if web server and db server on the samr machine.
        // else refer help On parameter "--local-infile" for mysql startup.
        Connection conn = null;
        try
        {

            String infile = fileOutPath + "/ip_country_" + Calendar.getInstance().get(Calendar.MONTH) + "_.txt";
            File f = new File(infile);
            if (f.exists() || f.length() > 0)
            {
                conn = Database.getConnection();
                Database.setAutoCommit(conn, false);
                String deleteQuery = "delete from ipmap";
                Database.executeQuery(deleteQuery, conn);
                String query = "load data infile '" + infile + "' into table ipmap fields terminated by ',' LINES TERMINATED BY '\\n' (startip,endip,country)";
                Database.executeQuery(query, conn);
                Database.commit(conn);
            }
        }
        catch (SystemError systemError)
        {
            log.error("Error while updating to db :",systemError);
            Database.rollback(conn);
            throw systemError;
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }

    private static void downloadIpToCountryFile() throws Exception
    {
        log.debug("Entering block to download IP to Country File..");
        log.debug("Download URL : ");
        FileWriter f = null;
        ZipInputStream zIs = null;
        InputStream is = null;
        try
        {
            URL fileDownloadUrl = new URL(downloadURL);
            URLConnection conn = fileDownloadUrl.openConnection();
            is = conn.getInputStream();
            zIs = new ZipInputStream(is);
            zIs.getNextEntry();
            BufferedReader in = new BufferedReader(new InputStreamReader(zIs));
            String inputLine;
            String ipToCountryFile = fileOutPath + "/ip_country_" + Calendar.getInstance().get(Calendar.MONTH) + "_.txt";
            f = new FileWriter(ipToCountryFile);
            log.debug("Writing data to File...");
            while ((inputLine = in.readLine()) != null)
            {
                StringTokenizer st = new StringTokenizer(inputLine, ",");

                if (st.hasMoreElements())
                {
                    String data = st.nextElement() + "," + st.nextElement() + "," + st.nextElement();
                    data = data.replaceAll("\"", "");
                    f.write(data + "\n");
                }
            }
        }
        catch (MalformedURLException e)
        {
            log.error("Incorrect download url : ",e);
            throw e;
        }
        catch (IOException ie)
        {
            log.error("Error while reading/writing ip_country file: ",ie);
            throw ie;
        }
        finally
        {
            f.close();
            is.close();
            zIs.close();
        }
        log.debug("File download Complete..");
    }

}

