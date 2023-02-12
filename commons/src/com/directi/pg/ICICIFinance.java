package com.directi.pg;

import java.io.*;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.util.*;

//import PaymentClient.*;

@Deprecated
public class ICICIFinance

{
    //static Category cat = Category.getInstance(Merchants.class.getName());
    static Logger Log = new Logger(ICICIFinance.class.getName());
    String loop = "<#LOOP#>";
    String end = "<#END#>";

    int looplength = loop.length();
    int endlength = end.length();

    StringBuffer netmerchant = null;
    StringBuffer withdrawal = null;
    StringBuffer reversal = null;
    StringBuffer chargeback = null;
    StringBuffer internaltrf = null;
    StringBuffer revofchbk = null;

    static boolean isnetmerchant = false;
    static boolean iswithdrawal = false;
    static boolean isreversal = false;
    static boolean ischargeback = false;
    static boolean isinternaltrf = false;
    static boolean isrevofchbk = false;
    static boolean isreversalofchargeback = false;


    String startdt = "";
    String enddt = "";
    static String date = "";
    Hashtable emailhash = null;


    public static void main(String[] args)
    {
        Log.debug("ICICIFinance !");

        try
        {
            FileInputStream fis = new FileInputStream("finance.properties");

            DataInputStream in = new DataInputStream(fis);
            Properties prop = new Properties();

            prop.load(in);
            fis.close();
            in.close();
            Hashtable emailhash = new Hashtable();

            Enumeration enu = prop.propertyNames();
            while (enu.hasMoreElements())
            {
                String key = (String) enu.nextElement();
                emailhash.put(key, prop.getProperty(key));
            }

            sendMail(emailhash, new java.util.Date());

        }
        catch (Exception ex)
        {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            Log.info(sw.toString());
        }

        Log.debug("End Finance !");

    }

    public static void sendMail(Hashtable emailhash, Date dt) throws SystemError
    {
        try
        {

            isnetmerchant = false;
            iswithdrawal = false;
            isreversal = false;
            ischargeback = false;
            isinternaltrf = false;
            isrevofchbk = false;
            isreversalofchargeback = false;


            Log.debug("Finance emailhash " + emailhash);
            //emailhash=emailhash1;

            Merchants.refresh();

            ICICIFinance fn = new ICICIFinance(emailhash, dt);
            Log.debug("Inside sendMail");
            //String date=" - "+(new Date()).toString();


            String subject = "ICICI - Merchant Report" + date;
            StringBuffer mail = new StringBuffer();
            mail.append("<html><head><style>\r\n.tr1{text-valign: center;text-align: LEFT;font-size:14px;font-family:arial,verdana,helvetica}\r\n\r\n.heading{background:#800000;text-valign: center;text-align: LEFT;font-size:14px;color:#ffffff;font-family:arial,verdana,helvetica}\r\n</style></head><body text=\"#800000\"><p align=center><b>Daily Report - " + date + "</b></p><hr>");

            String message = fn.netMerchant();
            if (isnetmerchant)
                mail.append(message + "<br><br><hr>");

            message = fn.netWithdrawals();
            if (iswithdrawal)
                mail.append(message + "<br><br><hr>");

            message = fn.netChargebacks();
            if (ischargeback)
                mail.append(message + "<br><br><hr>");

            message = fn.netReversals();
            if (isreversal)
                mail.append(message + "<br><br><hr>");

            message = fn.netInternalTrf();
            if (isinternaltrf)
                mail.append(message + "<br><br><hr>");

            message = fn.reversalOfChargeback();
            if (isreversalofchargeback)
                mail.append(message + "<br><br><hr>");

            mail.append("</body></html>");

            if (isnetmerchant || iswithdrawal || isreversal || ischargeback || isinternaltrf || isrevofchbk || isreversalofchargeback)
                //Mail.sendHtmlMail((String) emailhash.get("financetoemail"), (String) emailhash.get("financefromemail"), (String) emailhash.get("financeccemail"), (String) emailhash.get("financebccemail"), subject, mail.toString());

            Log.debug("leaving sendMail");

        }
        catch (Exception ex)
        {   Log.error("Exception :::::::::",ex);
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));

        }
    }

    public ICICIFinance(Hashtable emailhash1, Date dt)
    {
        this.emailhash = emailhash1;

        java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
        java.util.GregorianCalendar stcal = new java.util.GregorianCalendar();
        java.util.GregorianCalendar encal = new java.util.GregorianCalendar();

        try
        {
            cal.setTime(dt);
            stcal.setTime(dt);
            encal.setTime(dt);

            stcal.add(Calendar.DATE, -2);
            cal.add(Calendar.DATE, -1);

            startdt = "" + stcal.get(Calendar.YEAR) + "-" + Integer.toString(stcal.get(Calendar.MONTH) + 1) + "-" + (stcal.get(Calendar.DATE)) + " 23:59:59";
            enddt = "" + encal.get(Calendar.YEAR) + "-" + Integer.toString(encal.get(Calendar.MONTH) + 1) + "-" + (encal.get(Calendar.DATE)) + " 00:00:00";
            date = " " + cal.get(Calendar.YEAR) + "-" + Integer.toString(cal.get(Calendar.MONTH) + 1) + "-" + (cal.get(Calendar.DATE));

            //startdt="2002-02-11 23:59:59";
            //enddt="2002-02-13 00:00:00";

            //	internal trfs
            //	startdt="2002-01-06 23:59:59";
            //	enddt="2002-02-09 00:00:00";
        }
        catch (NumberFormatException nfe)
        {
            startdt = "-";
            enddt = "-";
        }


        try
        {
            String temp = null;
            netmerchant = new StringBuffer();
            withdrawal = new StringBuffer();
            reversal = new StringBuffer();
            chargeback = new StringBuffer();
            internaltrf = new StringBuffer();
            revofchbk = new StringBuffer();
            Log.debug("Retriving path");
            Log.debug("Path = " + emailhash.get("pathtotemplate"));
            String pathtotemplate = (String) emailhash.get("pathtotemplate");
            Log.debug("pathtotemplate " + pathtotemplate);

            BufferedReader rin = new BufferedReader(new FileReader(pathtotemplate + "NetMerchant.template"));

            //InputStream is = getClass().getResourceAsStream("csv.properties");


            while ((temp = rin.readLine()) != null)
            {
                netmerchant.append(temp + "\r\n");
            }
            rin.close();




            rin = new BufferedReader(new FileReader(pathtotemplate + "Withdrawals.template"));
            while ((temp = rin.readLine()) != null)
            {
                withdrawal.append(temp + "\r\n");
            }
            rin.close();


            rin = new BufferedReader(new FileReader(pathtotemplate + "Reversals.template"));
            while ((temp = rin.readLine()) != null)
            {
                reversal.append(temp + "\r\n");
            }
            rin.close();

            rin = new BufferedReader(new FileReader(pathtotemplate + "Chargebacks.template"));
            while ((temp = rin.readLine()) != null)
            {
                chargeback.append(temp + "\r\n");
            }
            rin.close();

            rin = new BufferedReader(new FileReader(pathtotemplate + "InternalTrfs.template"));
            while ((temp = rin.readLine()) != null)
            {
                internaltrf.append(temp + "\r\n");
            }
            rin.close();


            rin = new BufferedReader(new FileReader(pathtotemplate + "ReversalsofChargebacks.template"));
            while ((temp = rin.readLine()) != null)
            {
                revofchbk.append(temp + "\r\n");
            }
            rin.close();


            Log.debug("Template File Set");
        }
        catch (FileNotFoundException ex)
        {
            Log.error("Letter Template File Not Found",ex);
            //out.println("Raw File Not Found"+ex.toString());
            return;
        }
        catch (Exception ex)
        {
            Log.error("Letter Template File Not Found",ex);
            //out.println("Raw File Not Found"+ex.toString());
            return;
        }
    }

    public String netMerchant() throws SystemError, Exception
    {
        Log.debug("Inside generateMail");
        Merchants merchants = new Merchants();

        StringBuffer tempbuf = null;
        try
        {


            int fpos = (netmerchant.toString()).indexOf(loop);
            int lpos = (netmerchant.toString()).indexOf(end);

            String str = netmerchant.substring(fpos + looplength, lpos);
            tempbuf = new StringBuffer(netmerchant.substring(0, fpos));

            BigDecimal debit = new BigDecimal("0");
            BigDecimal fees = new BigDecimal("0");

            //Net Merchant Total

            Connection cn= Database.getConnection();
            StringBuffer sb1 = new StringBuffer("select toid, sum(amount) as sum from transactions where ");
            sb1.append(" dtstamp > unix_timestamp(?) ");
            sb1.append(" and dtstamp < unix_timestamp(?) ");
            sb1.append(" and type = 'icicicredit'");
            sb1.append(" group by toid");
            sb1.append(" order by toid");


            StringBuffer sb2 = new StringBuffer("select fromid, sum(amount) as sum from transactions where ");
            sb2.append(" dtstamp > unix_timestamp(?) ");
            sb2.append(" and dtstamp < unix_timestamp(?) ");
            sb2.append(" and type = 'icicicreditcharges'");
            sb2.append(" group by fromid");
            sb2.append(" order by fromid");
            PreparedStatement p1=cn.prepareStatement(sb1.toString());
            p1.setString(1,startdt);
            p1.setString(2,enddt);
            Hashtable credithash = Database.getNameValueHashFromResultSet(p1.executeQuery());
            PreparedStatement p2=cn.prepareStatement(sb2.toString());
            p2.setString(1,startdt);
            p2.setString(2,enddt);
            Hashtable chargeshash = Database.getNameValueHashFromResultSet(p2.executeQuery());

            int creditsize = credithash.size();
            int chargessize = chargeshash.size();

            Enumeration enu = null;
            String merchantid = null;

            if (creditsize >= chargessize)
                enu = credithash.keys();
            else
                enu = chargeshash.keys();

            //Log.info(sb1.toString());
            //ResultSet result = Database.executeQuery(sb1.toString(),Database.getConnection());

            Hashtable hash = new Hashtable();
            //			Hashtable credittemphash=null;
            //			Hashtable chargestemphash=null;

            while (enu.hasMoreElements())
            {
                isnetmerchant = true;
                merchantid = (String) enu.nextElement();

//					credittemphash=(Hashtable)credithash.get(""+i);
//					chargestemphash=(Hashtable)chargeshash.get(""+i);

                BigDecimal amt = new BigDecimal(0);
                BigDecimal chargesamt = new BigDecimal(0);

                if (credithash.get(merchantid) != null)
                    amt = new BigDecimal((String) credithash.get(merchantid));

                if (chargeshash.get(merchantid) != null)
                    chargesamt = new BigDecimal((String) chargeshash.get(merchantid));

                fees = fees.add(chargesamt);

                //	String amt=result.getString("sum");
                //	//String company_name=result.getString("company_name");
                String company_name = merchants.getCompany(merchantid);

                //if(amt==null)amt="0";
                if (company_name == null) company_name = "&nbsp";

                debit = debit.add(amt);
                hash.put("COMPANYNAME", company_name);
                hash.put("MERCHANTID", merchantid);

                amt = amt.subtract(chargesamt);
                hash.put("CREDIT", amt.toString());


                tempbuf.append(Functions.replaceTag(str, hash));
            }

            tempbuf.append(netmerchant.substring(lpos + endlength));

            //Total ICICI fees
            /*
                   sb1 = new StringBuffer("select sum(amount) as sum, count(*) as count from transactions where ");
                   sb1.append(" dtstamp > unix_timestamp('" + startdt + "') " );
                   sb1.append(" and dtstamp < unix_timestamp('" + enddt + "') " );
                   sb1.append(" and type = 'creditcharges'");
                   Log.info(sb1.toString());

                   result = Database.executeQuery(sb1.toString(),Database.getConnection());


                   hash=new Hashtable();
                   if(result.next())
                   {
                       String fees=result.getString("sum");
                       if(fees==null)fees="0";
                       hash.put("FEES",fees);
                   }

                   */
            hash.put("FEES", fees.toString());
            hash.put("DEBIT", debit.toString());

            tempbuf = new StringBuffer((Functions.replaceTag(tempbuf.toString(), hash)));

/*

                //Chargebacks
                sb1 = new StringBuffer("select memberid, company_name, sum(amount) as sum, count(*) as count from members a, transactions b where ");
                sb1.append(" dtstamp > unix_timestamp('" + startdt + "') " );
                sb1.append(" dtstamp < unix_timestamp('" + enddt + "') " );
                sb1.append(" and type = 'chargeback'");
                sb1.append(" and a.memberid=b.toid");
                sb1.append(" group by memberid");
                sb1.append(" order by memberid");
                Log.info(sb1.toString());
                result = Database.executeQuery(sb1.toString(),Database.getConnection());


                //Reversal
                sb1 = new StringBuffer("select memberid, company_name, sum(amount) as sum, count(*) as count from members a,transactions b, members c ");
                sb1.append(" b.dtstamp > unix_timestamp('" + startdt + "') " );
                sb1.append(" b.dtstamp < unix_timestamp('" + enddt + "') " );
                sb1.append(" and (type = 'reversal' or type ='reversalofcharges' or type ='chargesforreversal'");
                sb1.append(" and a.memberid = b.toid and c.memberid = b.fromid ");
                sb1.append(" group by b.type,a.memberid,b.fromid");
                sb1.append(" order by b.fromid,b.type");
                Log.info(sb1.toString());
                result = Database.executeQuery(sb1.toString(),Database.getConnection());
*/
        }
        catch (SQLException e)
        {
            Log.error("Leaving Finance throwing SQL Exception as System Error : ", e);
            throw new SystemError("Error : " + e.getMessage());
        }
        return tempbuf.toString();

    }


    public String netWithdrawals() throws SystemError, Exception
    {
        Log.debug("Inside netwithdrawals");
        Merchants merchants = new Merchants();
        StringBuffer tempbuf = null;
        Connection cn= Database.getConnection();
        try
        {
            int fpos = (withdrawal.toString()).indexOf(loop);
            int lpos = (withdrawal.toString()).indexOf(end);

            String str = withdrawal.substring(fpos + looplength, lpos);
            tempbuf = new StringBuffer(withdrawal.substring(0, fpos));

            //Net Merchant Withdrawal
            StringBuffer sb1 = new StringBuffer("select toid, sum(amount) as sum from transactions where ");
            sb1.append(" dtstamp > unix_timestamp(?) ");
            sb1.append(" and dtstamp < unix_timestamp(?) ");
            sb1.append(" and type = 'icicicredit'");
            sb1.append(" group by toid");
            sb1.append(" order by toid");


            StringBuffer sb2 = new StringBuffer("select fromid, sum(amount) as sum from transactions where ");
            sb2.append(" dtstamp > unix_timestamp(?) ");
            sb2.append(" and dtstamp < unix_timestamp(?) ");
            sb2.append(" and type = 'icicicreditcharges'");
            sb2.append(" group by fromid");
            sb2.append(" order by fromid");
            PreparedStatement p1=cn.prepareStatement(sb1.toString());
            p1.setString(1,startdt);
            p1.setString(2,enddt);
            Hashtable withdrawalhash = Database.getNameValueHashFromResultSet(p1.executeQuery());
            PreparedStatement p2=cn.prepareStatement(sb2.toString());
            p2.setString(1,startdt);
            p2.setString(2,enddt);
            Hashtable withdrawalchargeshash = Database.getNameValueHashFromResultSet(p2.executeQuery());

            int creditsize = withdrawalhash.size();
            int chargessize = withdrawalchargeshash.size();

            Enumeration enu = null;
            String merchantid = null;

            if (creditsize >= chargessize)
                enu = withdrawalhash.keys();
            else
                enu = withdrawalchargeshash.keys();

            Hashtable hash = new Hashtable();
            BigDecimal bd = null;

            while (enu.hasMoreElements())
            {
                iswithdrawal = true;
                merchantid = (String) enu.nextElement();


                String amt = (String) withdrawalhash.get(merchantid);
                String fromname = merchants.getCompany(merchantid);

                if (amt == null) amt = "0";
                if (fromname == null) fromname = "&nbsp";

                bd = new BigDecimal(amt);

                hash.put("MERCHANTID", merchantid);
                hash.put("COMPANYNAME", fromname);
                hash.put("CREDIT", amt);
                hash.put("FEES", "0");
                hash.put("DEBIT", "0");

                amt = (String) withdrawalchargeshash.get(merchantid);
                if (amt == null) amt = "0";
                hash.put("FEES", amt);
                bd = bd.add(new BigDecimal(amt));
                hash.put("DEBIT", bd.toString());


                tempbuf.append(Functions.replaceTag(str, hash));
            }

            tempbuf.append(withdrawal.substring(lpos + endlength));

        }
        catch (SQLException e)
        {
            Log.error("Leaving Finance throwing SQL Exception as System Error : ",e);
            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(cn);
        }
        return tempbuf.toString();

    }


    public String netChargebacks() throws SystemError, Exception
    {
        Log.debug("Inside netChargebacks");
        Merchants merchants = new Merchants();
        StringBuffer tempbuf = null;
        Connection con=Database.getConnection();

        try
        {


            int fpos = (chargeback.toString()).indexOf(loop);
            int lpos = (chargeback.toString()).indexOf(end);

            String str = chargeback.substring(fpos + looplength, lpos);
            tempbuf = new StringBuffer(chargeback.substring(0, fpos));

            //Chargebacks

            StringBuffer sb1 = new StringBuffer("select fromid,toid, sum(amount) as sum, count(*) as count from transactions where transid<41131 and ");
            sb1.append(" dtstamp > unix_timestamp(?) ");
            sb1.append(" and  dtstamp < unix_timestamp(?) ");
            sb1.append(" and type = 'icicichargeback'");
            sb1.append(" group by toid,fromid");
            sb1.append(" order by fromid");

            PreparedStatement p1=con.prepareStatement(sb1.toString());
            p1.setString(1,startdt);
            p1.setString(2,enddt);
            ResultSet result = p1.executeQuery();

            Hashtable hash = new Hashtable();

            while (result.next())
            {
                ischargeback = true;
                String amt = result.getString("sum");
                //String company_name=result.getString("company_name");
                String company_name = merchants.getCompany(result.getString("fromid"));

                if (amt == null) amt = "0";
                if (company_name == null) company_name = "&nbsp";

                hash.put("MERCHANTID", result.getString("fromid"));
                hash.put("COMPANYNAME", company_name);
                hash.put("DEBIT", amt);
                hash.put("CREDIT", amt);

                tempbuf.append(Functions.replaceTag(str, hash));
            }

            tempbuf.append(chargeback.substring(lpos + endlength));

        }
        catch (SQLException e)
        {
            Log.error("Leaving Finance throwing SQL Exception as System Error : ",e);
            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return tempbuf.toString();

    }

    public String netReversals() throws SystemError, Exception
    {
        Log.debug("Inside netReversals");
        StringBuffer tempbuf = null;
        Merchants merchants = new Merchants();
        Connection con= Database.getConnection();
        try
        {
            int fpos = (reversal.toString()).indexOf(loop);
            int lpos = (reversal.toString()).indexOf(end);

            String str = reversal.substring(fpos + looplength, lpos);
            tempbuf = new StringBuffer(reversal.substring(0, fpos));

            //reversals
            /*
                   StringBuffer sb1 = new StringBuffer("select memberid, company_name, sum(amount) as sum, count(*) as count from members a, transactions b where ");
                   sb1.append(" b.dtstamp > unix_timestamp('" + startdt + "') " );
                   sb1.append(" and  b.dtstamp < unix_timestamp('" + enddt + "') " );
                   sb1.append(" and b.type in('reversal','chargesforreversal','reversalofcharges')");
                   sb1.append(" and a.memberid=b.toid and ");
                   sb1.append(" group by b.type,a.memberid");
                   sb1.append(" order by a.memberid,b.type");
               */

            StringBuffer sb1 = new StringBuffer("select fromid,sum(amount) as sum from transactions where transid<41131 and ");
            sb1.append(" (dtstamp > unix_timestamp(?) ");
            sb1.append(" and dtstamp < unix_timestamp(?)) ");
            sb1.append(" and type in('icicireversal')");
            sb1.append(" group by fromid");
            sb1.append(" order by transid");

            StringBuffer sb2 = new StringBuffer("select fromid,sum(amount) as sum from transactions where transid<41131 and ");
            sb2.append(" (dtstamp > unix_timestamp(?) ");
            sb2.append(" and dtstamp < unix_timestamp(?)) ");
            sb2.append(" and type in('icicichargesforreversal')");
            sb2.append(" group by fromid");
            sb2.append(" order by transid");

            StringBuffer sb3 = new StringBuffer("select toid,sum(amount) as sum from transactions where transid<41131 and ");
            sb3.append(" (dtstamp > unix_timestamp(?) ");
            sb3.append(" and dtstamp < unix_timestamp(?)) ");
            sb3.append(" and type in('icicireversalofcharges')");
            sb3.append(" group by toid");
            sb3.append(" order by transid");

            //ResultSet result = Database.executeQuery(sb1.toString(),Database.getConnection());
            PreparedStatement p1=con.prepareStatement(sb1.toString());
            p1.setString(1,startdt);
            p1.setString(2,enddt);
            Hashtable reversalhash = Database.getNameValueHashFromResultSet(p1.executeQuery());
            PreparedStatement p2=con.prepareStatement(sb2.toString());
            p2.setString(1,startdt);
            p2.setString(2,enddt);
            Hashtable chargesforreversalhash = Database.getNameValueHashFromResultSet(p2.executeQuery());
            PreparedStatement p3=con.prepareStatement(sb3.toString());
            p3.setString(1,startdt);
            p3.setString(2,enddt);
            Hashtable reversalofchargeshash = Database.getNameValueHashFromResultSet(p3.executeQuery());

            int reversalhashsize = reversalhash.size();
            int chargesforreversalhashsize = chargesforreversalhash.size();
            int reversalofchargeshashsize = reversalofchargeshash.size();

            Enumeration enu = null;
            String merchantid = null;

            if (reversalhashsize >= chargesforreversalhashsize)
            {
                if (reversalhashsize >= reversalofchargeshashsize)
                    enu = reversalhash.keys();
                else
                    enu = reversalofchargeshash.keys();
            }
            else
            {
                if (chargesforreversalhashsize >= reversalofchargeshashsize)
                    enu = chargesforreversalhash.keys();
                else
                    enu = reversalofchargeshash.keys();
            }

            Hashtable hash = new Hashtable();
            BigDecimal bd = null;

            while (enu.hasMoreElements())
            {
                isreversal = true;
                merchantid = (String) enu.nextElement();
                bd = new BigDecimal("0");

                String amt = (String) reversalhash.get(merchantid);
                String fromname = merchants.getCompany(merchantid);

                if (amt == null) amt = "0";
                if (fromname == null) fromname = "&nbsp";

                bd = bd.add(new BigDecimal(amt));

                hash.put("COMPANYNAME", fromname);
                hash.put("MERCHANTID", merchantid);
                hash.put("AMOUNT", amt);


                amt = (String) reversalofchargeshash.get(merchantid);
                if (amt == null) amt = "0";
                hash.put("COMMREFUND", amt);
                bd = bd.subtract(new BigDecimal(amt));


                amt = (String) chargesforreversalhash.get(merchantid);
                if (amt == null) amt = "0";
                hash.put("FEES", amt);
                bd = bd.add(new BigDecimal(amt));

                hash.put("REFUND", bd.toString());


                tempbuf.append(Functions.replaceTag(str, hash));
            }

            tempbuf.append(reversal.substring(lpos + endlength));


        }
        catch (SQLException e)
        {
            Log.error("Leaving Finance throwing SQL Exception as System Error : ",e);
            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return tempbuf.toString();

    }


    public String netInternalTrf() throws SystemError, Exception
    {

        Log.debug("Inside netinternaltrfs");
        StringBuffer tempbuf = null;
        Merchants merchants = new Merchants();
        Connection con=Database.getConnection();
        try
        {


            int fpos = (internaltrf.toString()).indexOf(loop);
            int lpos = (internaltrf.toString()).indexOf(end);

            String str = internaltrf.substring(fpos + looplength, lpos);
            tempbuf = new StringBuffer(internaltrf.substring(0, fpos));

            //internaltrfs
            /*
                   StringBuffer sb1 = new StringBuffer("select memberid, company_name, sum(amount) as sum, count(*) as count from members a, transactions b where ");
                   sb1.append(" b.dtstamp > unix_timestamp('" + startdt + "') " );
                   sb1.append(" and  b.dtstamp < unix_timestamp('" + enddt + "') " );
                   sb1.append(" and b.type in('internaltrf','chargesforinternaltrf','internaltrfofcharges')");
                   sb1.append(" and a.memberid=b.toid and ");
                   sb1.append(" group by b.type,a.memberid");
                   sb1.append(" order by a.memberid,b.type");
               */

            StringBuffer sb1 = new StringBuffer("select * from transactions b where transid<41131 and ");
            sb1.append(" (b.dtstamp > unix_timestamp(?) ");
            sb1.append(" and b.dtstamp < unix_timestamp(?)) ");
            sb1.append(" and b.type in ('internaltransfer','othercharges') ");
            //sb1.append(" and a.memberid = b.toid and c.memberid = b.fromid ");
            //sb1.append(" group by b.type,a.memberid,b.fromid");
            sb1.append(" order by b.transid");


            PreparedStatement p1=con.prepareStatement(sb1.toString());
            p1.setString(1, startdt);
            p1.setString(2,enddt);
            ResultSet result = p1.executeQuery();

            Hashtable hash = new Hashtable();
            BigDecimal bd = null;

            while (result.next())
            {
                isinternaltrf = true;
                bd = new BigDecimal("0");

                String amt = result.getString("amount");
                int fromid = result.getInt("fromid");
                int toid = result.getInt("toid");

                String fromname = merchants.getCompany("" + fromid);
                String toname = merchants.getCompany("" + toid);

                if (amt == null) amt = "0";
                if (fromname == null) fromname = "&nbsp";
                if (toname == null) toname = "&nbsp";

                bd = bd.add(new BigDecimal(amt));

                if (fromid == 222)
                    fromname += "(ICICI CC BANK A/C No .1298)";

                if (toid == 36)
                    toname += "(income towards setup/late-capture fees)";

                hash.put("FROMCOMPANYNAME", fromname);
                hash.put("TOCOMPANYNAME", toname);
                hash.put("FROMID", "" + fromid);
                hash.put("TOID", "" + toid);
                hash.put("CREDIT", amt);
                hash.put("DEBIT", amt);


                tempbuf.append(Functions.replaceTag(str, hash));
            }

            tempbuf.append(internaltrf.substring(lpos + endlength));


        }
        catch (SQLException e)
        {
            Log.error("Leaving Finance throwing SQL Exception as System Error : ", e);
            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(con);
        }

        return tempbuf.toString();

    }


    public String reversalOfChargeback() throws SystemError, Exception
    {

        Log.debug("Inside reversalOfChargeback");
        Merchants merchants = new Merchants();
        StringBuffer tempbuf = null;
        Connection con=Database.getConnection();
        try
        {


            int fpos = (revofchbk.toString()).indexOf(loop);
            int lpos = (revofchbk.toString()).indexOf(end);

            String str = revofchbk.substring(fpos + looplength, lpos);
            tempbuf = new StringBuffer(revofchbk.substring(0, fpos));

            //Chargebacks reversal

            StringBuffer sb1 = new StringBuffer("select fromid,toid, sum(amount) as sum, count(*) as count from transactions where transid<41131 and ");
            sb1.append(" dtstamp > unix_timestamp(?) ");
            sb1.append(" and  dtstamp < unix_timestamp(?) ");
            sb1.append(" and type = 'chargebackreversal'");
            sb1.append(" group by toid");
            sb1.append(" order by toid");

            PreparedStatement p1=con.prepareStatement(sb1.toString());
            p1.setString(1, startdt);
            p1.setString(2,enddt);
            ResultSet result = p1.executeQuery();

            Hashtable hash = new Hashtable();

            while (result.next())
            {
                isreversalofchargeback = true;
                String amt = result.getString("sum");
                //String company_name=result.getString("company_name");
                String company_name = merchants.getCompany(result.getString("toid"));

                if (amt == null) amt = "0";
                if (company_name == null) company_name = "&nbsp";

                hash.put("MERCHANTID", result.getString("toid"));
                hash.put("COMPANYNAME", company_name);
                hash.put("DEBIT", amt);
                hash.put("CREDIT", amt);

                tempbuf.append(Functions.replaceTag(str, hash));
            }

            tempbuf.append(revofchbk.substring(lpos + endlength));


        }
        catch (SQLException e)
        {
            Log.error("Leaving Finance throwing SQL Exception as System Error : ",e);
            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return tempbuf.toString();

    }
}


