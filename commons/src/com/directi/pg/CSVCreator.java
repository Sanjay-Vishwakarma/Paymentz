package com.directi.pg;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;

public class CSVCreator
{
    static Logger logger = new Logger(CSVCreator.class.getName());

    FileOutputStream fo = null;
    DataOutputStream dos = null;
    Hashtable hash = null;
    Hashtable innerhash = null;
    StringBuffer query = null;
    StringBuffer listcsv = null;

    Properties props = null;


    StringBuffer commaseptransid = null;
    //variable
    public String mindays = null;
    String filename = null;
    String toemail = null;
    String fromemail = null;
    String ccemail = null;
    String bccemail = null;
    String subject = null;

    String pathtocsv = null;

    BigDecimal totalamount = null;
    BigDecimal amount = null;


    public static void main(String[] args)
    {
        try
        {
            if (args.length < 3)
            {
                logger.debug("Usage....pass !");
                logger.debug("1st argument : memberids ");
                logger.debug("2nd argument : merchantid");
                logger.debug("3rd argument : Y if you want query with notin memberid,or N if you want query for this memberid only. ");

                return;
            }

            CSVCreator csv = new CSVCreator();
            //csv.generate(args[0],args[1],args[2]);
            csv.generate();

        }
        catch (Exception e)
        {
            logger.error("Exception in CSVCreator",e);
        }
    }

    public CSVCreator()
    {
        InputStream is = getClass().getResourceAsStream("csv.properties");
        props = new Properties();
        //filename=getBatchName();


        try
        {
            logger.debug("Inside try");
            props.load(is);
            mindays = (String) props.getProperty("mindays");
            toemail = (String) props.getProperty("toemail");
            fromemail = (String) props.getProperty("fromemail");
            ccemail = (String) props.getProperty("ccemail");
            bccemail = (String) props.getProperty("bccemail");
            subject = (String) props.getProperty("subject");
            pathtocsv = (String) props.getProperty("pathtocsv");

            logger.debug("mindays " + mindays);
            logger.debug("toemail " + toemail);
            logger.debug("fromemail " + fromemail);
            logger.debug("ccemail " + ccemail);
            logger.debug("bccemail " + bccemail);
            logger.debug("subject " + subject);
            logger.debug("pathtocsv " + pathtocsv);

        }
        catch (Exception e)
        {
            logger.error("Exception occure in constructor",e);
        }
    }


    public void generate() throws SystemError
    {
        logger.debug("Inside generate");
        //where status=capturesuccess
        //set status podsent
        //icicitransid,pod,amount


        try
        {
            int size = 0;

            query = new StringBuffer();

            query.append("select T.icicitransid,T.icicimerchantid,T.pod,T.captureamount,M.company_name as company from members as M,transaction_icicicredit as T where");
            query.append(" M.memberid=T.toid");
            query.append(" and pod is not null");
            query.append(" and status='capturesuccess'");
            query.append(" order by icicimerchantid,icicitransid asc");

            ResultSet rs = Database.executeQuery(query.toString(), Database.getConnection());

            String previousicicimerchantid = null;
            String icicimerchantid = null;
            totalamount = new BigDecimal(0.0);

            listcsv = new StringBuffer();
            commaseptransid = new StringBuffer(" ");

            while (rs.next())
            {
                int icicitransid = rs.getInt("icicitransid");
                String pod = rs.getString("pod");
                String company = rs.getString("company");
                icicimerchantid = rs.getString("icicimerchantid");

                amount = new BigDecimal(rs.getString("captureamount"));
                commaseptransid.append(icicitransid + ",");

                if (previousicicimerchantid != null && !previousicicimerchantid.equals(icicimerchantid))
                {
                    createFile(getFileName(previousicicimerchantid), listcsv.toString(), previousicicimerchantid, commaseptransid, totalamount.toString());
                    listcsv = new StringBuffer();
                    commaseptransid = new StringBuffer(" ");
                    totalamount = new BigDecimal(0.0);
                    previousicicimerchantid = icicimerchantid;
                }
                else
                    previousicicimerchantid = icicimerchantid;

                totalamount = totalamount.add(amount);
                listcsv.append(icicitransid);
                listcsv.append("," + pod);
                listcsv.append("," + amount);
                listcsv.append("," + company);
                listcsv.append("\r\n");

            }

            //finally
            if (icicimerchantid != null)
                createFile(getFileName(icicimerchantid), listcsv.toString(), icicimerchantid, commaseptransid, totalamount.toString());

            logger.debug("leaving generate");

        }
        catch (Exception e)
        {
            logger.error("Exception",e);
            throw new SystemError(e.toString());
        }
    }

    private String getBatchName()
    {
        return "" + System.currentTimeMillis() / 1000;
    }

    private String getFileName(String memberid)
    {
        return memberid + "-" + System.currentTimeMillis() / 1000;
    }

    void createFile(String filename, String data, String icicimerchantid, StringBuffer commaseptransid, String totalamount) throws SystemError
    {
        Connection con=null;
        try

        {
            fo = new FileOutputStream(pathtocsv + filename + ".csv");
            dos = new DataOutputStream(fo);
            dos.writeBytes(listcsv.toString());
            commaseptransid.deleteCharAt(commaseptransid.length() - 1);

            String tempsubject = "";
            tempsubject = icicimerchantid + "-" + subject + " " + new Date() + " - Batch NO : " + filename + " - amount " + totalamount;

            logger.debug("calling SendMail for bank");
            Mail.sendmail(toemail, fromemail, ccemail, bccemail, tempsubject, listcsv.toString());
            logger.debug("called SendMail for bank");

            //updating d
            if (commaseptransid.length() > 0)
            {
                con=Database.getConnection();
                StringBuffer updatequery = new StringBuffer("update transaction_icicicredit set status='podsent',podbatch=? where");
                updatequery.append(" icicitransid IN(?)");

                PreparedStatement ps=con.prepareStatement(updatequery.toString());
                ps.setString(1,getBatchName());
                ps.setString(2,commaseptransid.toString());
                logger.debug("Number of rows Affected " + ps.executeUpdate());

            }
        }
        catch (Exception ex)
        {
            throw new SystemError(ex.toString());
        }
        finally
        {
            Database.closeConnection(con);
        }
    }


}
