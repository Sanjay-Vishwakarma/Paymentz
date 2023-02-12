package com.directi.pg;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.*;

public class Finance
{

    static Logger Log = new Logger(Finance.class.getName());
    String loop = "<#LOOP#>";
    String end = "<#END#>";

    Connection conn = null;
    static StringBuffer members = null;
    static Hashtable merchantHash = new Hashtable();

    int looplength = loop.length();
    int endlength = end.length();

    StringBuffer netmerchant = null;
    StringBuffer withdrawal = null;
    StringBuffer reversalwithdrawal = null;
    StringBuffer reversal = null;
    StringBuffer chargeback = null;
    StringBuffer internaltrf = null;
    StringBuffer revofchbk = null;

    static boolean isnetmerchant = false;
    static boolean iswithdrawal = false;
    static boolean isreversalwithdrawal = false;
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
        Log.debug("Finance !");

        try
        {
            ResourceBundle rb = LoadProperties.getProperty("com.directi.pg.finance");

            Hashtable emailhash = new Hashtable();

            Enumeration enu = rb.getKeys();
            while (enu.hasMoreElements())
            {
                String key = (String) enu.nextElement();
                emailhash.put(key, rb.getString(key));
            }

            //sendMail(emailhash, new java.util.Date());

        }
        catch (Exception ex)
        {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            Log.error("Exception Finance",ex);
        }

        Log.debug("End Finance !");

    }

    public static void sendMail(Hashtable emailhash, Date dt)
    {
        String booleanBuf = " not in ";
        String id = "";
        try
        {
            ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.ICICI");
            id = RB.getString("MERCHANTID");
        }
        catch (Exception e)
        {
            Log.error(" Error in getting merchant id ",e);
        }
        StringBuffer mail = null;
        Finance fn = null;
        try
        {
            Log.debug("Emailhash: " + emailhash);

            Merchants.refresh();

            fn = new Finance(emailhash, dt);
            Log.debug("Inside sendMail");
            Enumeration enumMerchant = merchantHash.keys();


            int i = 0;
            while (i <= merchantHash.size())
            {
                isnetmerchant = false;
                iswithdrawal = false;
                isreversalwithdrawal = false;
                isreversal = false;
                ischargeback = false;
                isinternaltrf = false;
                isrevofchbk = false;
                isreversalofchargeback = false;

                String subject = "Merchant (" + id + ") Report for " + date;
                mail = new StringBuffer();
                mail.append(
                        "<html><head><style>\r\n.tr1{text-valign: center;text-align: LEFT;font-size:14px;font-family:arial,verdana,helvetica}\r\n\r\n.heading{background:#800000;text-valign: center;text-align: LEFT;font-size:14px;color:#ffffff;font-family:arial,verdana,helvetica}\r\n</style></head><body text=\"#800000\"><p align=center><b>Daily Report - " + date + "</b></p><hr>");

                String message = fn.netMerchant(booleanBuf);
                if (isnetmerchant)
                    mail.append(message + "<br><br><hr>");

                message = fn.netWithdrawals(booleanBuf);
                if (iswithdrawal)
                    mail.append(message + "<br><br><hr>");

                message = fn.netReversalWithdrawals(booleanBuf);
                if (isreversalwithdrawal)
                    mail.append(message + "<br><br><hr>");


                message = fn.netChargebacks(booleanBuf);
                if (ischargeback)
                    mail.append(message + "<br><br><hr>");

                message = fn.netReversals(booleanBuf);
                if (isreversal)
                    mail.append(message + "<br><br><hr>");

                message = fn.netInternalTrf(booleanBuf);
                if (isinternaltrf)
                    mail.append(message + "<br><br><hr>");

                message = fn.reversalOfChargeback(booleanBuf);
                if (isreversalofchargeback)
                    mail.append(message + "<br><br><hr>");


                mail.append("</body></html>");

                Log.debug("isnetmerchant " + isnetmerchant);
                Log.debug("iswithdrawal " + iswithdrawal);
                Log.debug("isreversalwithdrawal " + isreversalwithdrawal);
                Log.debug("isreversal " + isreversal);
                Log.debug("ischargeback " + ischargeback);
                Log.debug("isinternaltrf " + isinternaltrf);
                Log.debug("isrevofchbk " + isrevofchbk);
                Log.debug("isreversalofchargeback " + isreversalofchargeback);

                if (isnetmerchant || iswithdrawal || isreversalwithdrawal || isreversal || ischargeback || isinternaltrf || isrevofchbk || isreversalofchargeback)
                {
                    Log.debug("sending mail");
                    /*Mail.sendHtmlMail((String) emailhash.get("financetoemail"), (String) emailhash.get("financefromemail"),
                            (String) emailhash.get("financeccemail"), (String) emailhash.get("financebccemail"),
                            subject, mail.toString());*/
                    Log.debug("mail sent");
                }
                booleanBuf = " in "; //rest of members will have in query
                if (enumMerchant.hasMoreElements())
                    id = (String) enumMerchant.nextElement();
                else
                    break;
                members = new StringBuffer((String) merchantHash.get(id));
                i++;
            }

            Log.debug("leaving sendMail");
        }
        catch (Exception ex)
        {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            Log.error("Exception occure",ex);
        }
        finally
        {
            if (fn != null)
            {
                fn.closeconnection(); // closing connection.
            }
        }
    }

    public Finance(Hashtable emailhash1, Date dt)
    {
        this.emailhash = emailhash1;
        try
        {
            conn = Database.getConnection();
        }
        catch (Exception e)
        {
            Log.error("Error in getting connection ",e);
        }

        try
        {

            String strSql = "select distinct memberid,icicimerchantid from member_pg_preferences where memberid <> 0";
            members = new StringBuffer("(");
            ResultSet rs = Database.executeQuery(strSql, conn);
            String merchantId = "";
            String memberIds = "";

            while (rs.next())
            {
                String id = rs.getString(1);
                String mid = rs.getString(2);

                members.append(id + ",");

                if (!merchantId.equals(mid) && !"".equals(merchantId))
                {
                    memberIds = memberIds.substring(0, memberIds.length() - 1);
                    merchantHash.put(merchantId, "(" + memberIds + ")");

                    memberIds = id + ",";
                    merchantId = mid;

                }
                else
                {
                    memberIds = memberIds + id + ",";
                    merchantId = mid;
                }

            }

            if (Functions.parseData(memberIds) != null)
            {
                memberIds = memberIds.substring(0, memberIds.length() - 1);
                merchantHash.put(merchantId, "(" + memberIds + ")");
            }

            if (members.length() > 1)
                members.deleteCharAt(members.length() - 1);
            members.append(")");

            Log.debug("merchantHash " + merchantHash);
            Log.debug(" members: " + members);
        }
        catch (Exception e)
        {
            Log.error("Error in getting member ids " ,e);
        }

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

            startdt = Functions.converttomillisec(stcal.get(Calendar.MONTH) + "", stcal.get(Calendar.DATE) + "",
                    stcal.get(Calendar.YEAR) + "", "23", "59", "59");
            enddt = Functions.converttomillisec(encal.get(Calendar.MONTH) + "", encal.get(Calendar.DATE) + "",
                    encal.get(Calendar.YEAR) + "", "0", "0", "0");

            date = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DATE);
            //startdt="2002-02-11 23:59:59";
            //enddt="2002-02-13 00:00:00";
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
            reversalwithdrawal = new StringBuffer();
            reversal = new StringBuffer();
            chargeback = new StringBuffer();
            internaltrf = new StringBuffer();
            revofchbk = new StringBuffer();
            Log.debug("Retrieving path");
            Log.debug("Path = " + emailhash.get("pathtotemplate"));
            String pathtotemplate = (String) emailhash.get("pathtotemplate");

            pathtotemplate = pathtotemplate + "com/directi/pg/";
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
            Log.debug("Before Gertting file");
            rin = new BufferedReader(new FileReader(pathtotemplate + "reversalofwithdrawal.template"));
            while ((temp = rin.readLine()) != null)
            {
                reversalwithdrawal.append(temp + "\r\n");
            }
            rin.close();
            Log.debug("After Gertting file");

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
            Log.error("Letter Template File Not Found", ex);
            //out.println("Raw File Not Found"+ex.toString());
            return;
        }
    }

    /**
     * Method to close connection
     */
    public void closeconnection()
    {
        Database.closeConnection(conn);
    }

    public String netMerchant(String queryBuffer) throws SystemError, Exception
    {
        Log.debug("Inside netMerchant");
        Merchants merchants = new Merchants();
        //Connection conn=Database.getConnection();
        StringBuffer tempbuf = null;
        try
        {
//            Log.info("Net merchant: "+netmerchant.toString());
            int fpos = (netmerchant.toString()).indexOf(loop);
            int lpos = (netmerchant.toString()).indexOf(end);

            String str = netmerchant.substring(fpos + looplength, lpos);
            tempbuf = new StringBuffer(netmerchant.substring(0, fpos));

            BigDecimal debit = new BigDecimal("0");
            BigDecimal fees = new BigDecimal("0");
            BigDecimal tax = new BigDecimal("0");
            //Net Merchant Total
            StringBuffer sb1 = new StringBuffer("select toid, sum(amount) as sum, count(*) as count from transactions where ");
            sb1.append(" dtstamp >=?");
            sb1.append(" and dtstamp <=?");
            sb1.append(" and toid ?");
            sb1.append(" and type = 'credit' group by toid order by toid");


            StringBuffer sb2 = new StringBuffer("select fromid, sum(amount) as sum, count(*) as count from transactions where ");
            sb2.append(" dtstamp >=?");
            sb2.append(" and dtstamp <=?");
            sb2.append(" and fromid ?");
            sb2.append(" and type = 'creditcharges' group by fromid order by fromid");


            StringBuffer sb3 = new StringBuffer("select toid,fromid, sum(amount) as sum, count(*) as count from transactions where ");
            sb3.append(" dtstamp >=?");
            sb3.append(" and dtstamp <=?");
            sb3.append(" and fromid ?");
            sb3.append(" and type = 'taxoncreditcharges' group by fromid order by fromid");

            PreparedStatement p1=conn.prepareStatement(sb1.toString());
            p1.setString(1,startdt);
            p1.setString(2,enddt);
            p1.setString(3,queryBuffer + members);

            Hashtable credithash = Database.getHashFromResultSet(p1.executeQuery());

            PreparedStatement p2=conn.prepareStatement(sb2.toString());
            p2.setString(1,startdt);
            p2.setString(2,enddt);
            p2.setString(3,queryBuffer + members);
            Hashtable chargeshash = Database.getHashFromResultSet(p2.executeQuery());

            PreparedStatement p3=conn.prepareStatement(sb3.toString());
            p3.setString(1,startdt);
            p3.setString(2,enddt);
            p3.setString(3,queryBuffer + members);
            Hashtable taxonchargeshash = Database.getHashFromResultSet(p3.executeQuery());
            int creditsize = credithash.size();

            Hashtable hash = new Hashtable();
            Hashtable credittemphash = null;
            Hashtable chargestemphash = null;
            Hashtable taxonchargetempshash = null;
            for (int i = 1; i <= creditsize; i++)
            {
                isnetmerchant = true;

                credittemphash = (Hashtable) credithash.get("" + i);
                chargestemphash = (Hashtable) chargeshash.get("" + i);
                taxonchargetempshash = (Hashtable) taxonchargeshash.get("" + i);
                BigDecimal amt = new BigDecimal((String) credittemphash.get("sum"));
                BigDecimal chargesamt = new BigDecimal((String) chargestemphash.get("sum"));
                BigDecimal taxamt = null;
                if (taxonchargetempshash == null)
                    taxamt = new BigDecimal("0");
                else
                    taxamt = new BigDecimal((String) taxonchargetempshash.get("sum"));
                fees = fees.add(chargesamt);
                tax = tax.add(taxamt);
                String company_name = merchants.getCompany((String) credittemphash.get("toid"));

                if (company_name == null) company_name = "&nbsp;";

                debit = debit.add(amt);
                hash.put("COMPANYNAME", company_name);
                hash.put("MERCHANTID", credittemphash.get("toid"));

                amt = amt.subtract(chargesamt);
                amt = amt.subtract(taxamt);
                hash.put("CREDIT", amt.toString());

                Log.info(debit.toString());
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
            hash.put("TAX", tax.toString());
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
            Log.error("Leaving Finance throwing SQL Exception as System Error : ",e);
            throw new SystemError("Error : " + e.getMessage());
        }
        return tempbuf.toString();
    }

    public String netWithdrawals(String queryBuffer) throws SystemError, Exception
    {
        Log.debug("Inside netwithdrawals");
        StringBuffer tempbuf = null;
        Merchants merchants = new Merchants();
        try
        {
//            Log.info(withdrawal.toString());
            int fpos = (withdrawal.toString()).indexOf(loop);
            int lpos = (withdrawal.toString()).indexOf(end);

            String str = withdrawal.substring(fpos + looplength, lpos);
            tempbuf = new StringBuffer(withdrawal.substring(0, fpos));

            BigDecimal debit = new BigDecimal("0");
            //Net Merchant Withdrawal
            StringBuffer sb1 = new StringBuffer("select toid,fromid, sum(amount) as sum, count(*) as count from transactions where ");
            sb1.append(" (dtstamp >=?");
            sb1.append(" and dtstamp <=? ) ");
            sb1.append(" and fromid ?");
            sb1.append(" and type='withdrawal'");
            //sb1.append(" and (type='taxonwithdrawal' or type='withdrawal' or type='withdrawalcharges') ");
            sb1.append(" group by toid,fromid order by fromid");


            StringBuffer sb2 = new StringBuffer("select toid,fromid, sum(amount) as sum, count(*) as count from transactions where ");
            sb2.append(" (dtstamp >=?");
            sb2.append(" and dtstamp <=? ) ");
            sb2.append(" and fromid ?");
            sb2.append(" and type='withdrawalcharges'");
            //sb1.append(" and (type='taxonwithdrawal' or type='withdrawal' or type='withdrawalcharges') ");
            sb2.append(" group by toid,fromid order by fromid");


            StringBuffer sb3 = new StringBuffer("select toid,fromid, sum(amount) as sum, count(*) as count from transactions where ");
            sb3.append(" (dtstamp >=?");
            sb3.append(" and dtstamp <=? ) ");
            sb3.append(" and fromid ?");
            sb3.append(" and type='taxonwithdrawalcharges'");
            //sb1.append(" and (type='taxonwithdrawal' or type='withdrawal' or type='withdrawalcharges') ");
            sb3.append(" group by toid,fromid order by fromid");

            PreparedStatement p1=conn.prepareStatement(sb1.toString());
            p1.setString(1,startdt);
            p1.setString(2,enddt);
            p1.setString(3,queryBuffer + members);
            Hashtable withdrawhash = Database.getHashFromResultSet(p1.executeQuery());

            PreparedStatement p2=conn.prepareStatement(sb2.toString());
            p2.setString(1,startdt);
            p2.setString(2,enddt);
            p2.setString(3,queryBuffer + members);
            Hashtable chargeshash = Database.getHashFromResultSet(p2.executeQuery());

            PreparedStatement p3=conn.prepareStatement(sb3.toString());
            p3.setString(1,startdt);
            p3.setString(2,enddt);
            p3.setString(3,queryBuffer + members);
            Hashtable taxonchargeshash = Database.getHashFromResultSet(p3.executeQuery());
            int withdrawsize = withdrawhash.size();

            Hashtable hash = new Hashtable();
            Hashtable withdrawtemphash = null;
            Hashtable chargestemphash = null;
            Hashtable taxonchargetempshash = null;

            //ResultSet result = Database.executeQuery(sb1.toString(), conn);

            BigDecimal bd = null;

            for (int i = 1; i <= withdrawsize; i++)
            {
                iswithdrawal = true;
                debit = new BigDecimal("0");
                withdrawtemphash = (Hashtable) withdrawhash.get("" + i);
                chargestemphash = (Hashtable) chargeshash.get("" + i);
                taxonchargetempshash = (Hashtable) taxonchargeshash.get("" + i);
                BigDecimal amt = new BigDecimal((String) withdrawtemphash.get("sum"));
                BigDecimal chargesamt = new BigDecimal((String) chargestemphash.get("sum"));
                BigDecimal taxamt = null;
                if (taxonchargetempshash == null)
                    taxamt = new BigDecimal("0");
                else
                    taxamt = new BigDecimal((String) taxonchargetempshash.get("sum"));

                String company_name = merchants.getCompany((String) withdrawtemphash.get("fromid"));

                if (company_name == null) company_name = "&nbsp;";

                debit = debit.add(amt);
                debit = debit.add(chargesamt);
                debit = debit.add(taxamt);

                hash.put("COMPANYNAME", company_name);
                hash.put("MERCHANTID", withdrawtemphash.get("fromid"));

                hash.put("CREDIT", amt.toString());
                hash.put("FEES", chargesamt.toString());
                hash.put("TAX", taxamt.toString());
                hash.put("DEBIT", debit.toString());
                Log.info(debit.toString());
                tempbuf.append(Functions.replaceTag(str, hash));
            }
            tempbuf.append(withdrawal.substring(lpos + endlength));
        }
        catch (SQLException e)
        {
            Log.error("Leaving Finance throwing SQL Exception as System Error : ",e);
            throw new SystemError("Error : " + e.getMessage());
        }
        return tempbuf.toString();
    }

    public String netReversalWithdrawals(String queryBuffer) throws SystemError, Exception
    {
        Log.debug("Inside netReversalWithdrawals");
        StringBuffer tempbuf = null;
        Merchants merchants = new Merchants();
        try
        {
//            Log.info(withdrawal.toString());
            Log.debug("rev " + reversalwithdrawal);
            int fpos = (reversalwithdrawal.toString()).indexOf(loop);
            int lpos = (reversalwithdrawal.toString()).indexOf(end);
            Log.debug("Start " + fpos + " End " + lpos);
            String str = reversalwithdrawal.substring(fpos + looplength, lpos);
            tempbuf = new StringBuffer(reversalwithdrawal.substring(0, fpos));

            //Net Merchant Withdrawal
            StringBuffer sb1 = new StringBuffer("select toid,fromid, sum(amount) as sum, count(*) as count from transactions where ");
            sb1.append(" (dtstamp >=?");
            sb1.append(" and dtstamp <=? ) ");
            sb1.append(" and fromid ?");
//            sb1.append(" and type in ('withdrawal','withdrawalcharges') ");
            sb1.append(" and type ='reversalofwithdrawal'");
            sb1.append(" group by toid,fromid order by transid,fromid");

            StringBuffer sb2 = new StringBuffer("select toid,fromid, sum(amount) as sum, count(*) as count from transactions where ");
            sb2.append(" (dtstamp >=?");
            sb2.append(" and dtstamp <=? ) ");
            sb2.append(" and toid ?");
//            sb1.append(" and type in ('withdrawal','withdrawalcharges') ");
            sb2.append(" and type ='reversalofwithdrawalcharges'");
            sb2.append(" group by toid,fromid order by transid,fromid");

            StringBuffer sb3 = new StringBuffer("select toid,fromid, sum(amount) as sum, count(*) as count from transactions where ");
            sb3.append(" (dtstamp >=?");
            sb3.append(" and dtstamp <=? ) ");
            sb3.append(" and toid ?");
//            sb1.append(" and type in ('withdrawal','withdrawalcharges') ");
            sb3.append(" and type ='reversaloftaxonwithdrawalcharges'");
            sb3.append(" group by toid,fromid order by transid,fromid");

            PreparedStatement p1=conn.prepareStatement(sb1.toString());
            p1.setString(1,startdt);
            p1.setString(2,enddt);
            p1.setString(3,queryBuffer + members);
            Hashtable revwithdrawhash = Database.getHashFromResultSet(p1.executeQuery());
            PreparedStatement p2=conn.prepareStatement(sb2.toString());
            p2.setString(1,startdt);
            p2.setString(2,enddt);
            p2.setString(3,queryBuffer + members);
            Hashtable chargeshash = Database.getHashFromResultSet(p2.executeQuery());
            PreparedStatement p3=conn.prepareStatement(sb3.toString());
            p3.setString(1,startdt);
            p3.setString(2,enddt);
            p3.setString(3,queryBuffer + members);
            Hashtable taxonchargeshash = Database.getHashFromResultSet(p3.executeQuery());
            int revwithdrawsize = revwithdrawhash.size();

            Hashtable hash = new Hashtable();
            Hashtable revwithdrawtemphash = null;
            Hashtable chargestemphash = null;
            Hashtable taxonchargetempshash = null;
            BigDecimal credit = new BigDecimal("0");

            for (int i = 1; i <= revwithdrawsize; i++)
            {
                isreversalwithdrawal = true;

                revwithdrawtemphash = (Hashtable) revwithdrawhash.get("" + i);
                chargestemphash = (Hashtable) chargeshash.get("" + i);
                taxonchargetempshash = (Hashtable) taxonchargeshash.get("" + i);
                BigDecimal amt = new BigDecimal((String) revwithdrawtemphash.get("sum"));
                BigDecimal chargesamt = new BigDecimal((String) chargestemphash.get("sum"));
                BigDecimal taxamt = null;
                if (taxonchargetempshash == null)
                    taxamt = new BigDecimal("0");
                else
                    taxamt = new BigDecimal((String) taxonchargetempshash.get("sum"));


                credit = credit.add(amt);
                credit = credit.add(chargesamt);
                credit = credit.add(taxamt);

                String fromname = merchants.getCompany((String) revwithdrawtemphash.get("toid"));
                hash.put("MERCHANTID", (String) revwithdrawtemphash.get("toid"));
                hash.put("COMPANYNAME", fromname);

                hash.put("DEBIT", amt.toString());
                hash.put("FEES", chargesamt.toString());
                hash.put("CREDIT", credit.toString());
                hash.put("TAX", taxamt.toString());
                tempbuf.append(Functions.replaceTag(str, hash));
            }
            tempbuf.append(reversalwithdrawal.substring(lpos + endlength));
        }
        catch (SQLException e)
        {
            Log.error("Leaving Finance throwing SQL Exception as System Error : ",e);
            throw new SystemError("Error : " + e.getMessage());
        }
        return tempbuf.toString();
    }

    public String netChargebacks(String queryBuffer) throws SystemError, Exception
    {
        Log.debug("Inside netChargebacks");
        StringBuffer tempbuf = null;
        Merchants merchants = new Merchants();
        try
        {
//            Log.info(chargeback.toString());
            int fpos = (chargeback.toString()).indexOf(loop);
            int lpos = (chargeback.toString()).indexOf(end);

            String str = chargeback.substring(fpos + looplength, lpos);
            tempbuf = new StringBuffer(chargeback.substring(0, fpos));

            //Chargebacks
            StringBuffer sb1 = new StringBuffer("select fromid,toid, sum(amount) as sum, count(*) as count from transactions where ");
            sb1.append(" dtstamp >=?");
            sb1.append(" and  dtstamp <=?");
            sb1.append(" and fromid ?");
            sb1.append(" and type = 'chargeback' group by toid,fromid order by fromid");

            PreparedStatement p1=conn.prepareStatement(sb1.toString());
            p1.setString(1,startdt);
            p1.setString(2,enddt);
            p1.setString(3,queryBuffer + members);
            ResultSet result = p1.executeQuery();

            Hashtable hash = new Hashtable();

            while (result.next())
            {
                ischargeback = true;
                String amt = result.getString("sum");
                //String company_name=result.getString("company_name");
                String company_name = merchants.getCompany(result.getString("fromid"));

                if (amt == null) amt = "0";
                if (company_name == null) company_name = "&nbsp;";

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
            Log.error("Leaving Finance throwing SQL Exception as System Error : " , e);
            throw new SystemError("Error : " + e.getMessage());
        }
        return tempbuf.toString();
    }

    public String netReversals(String queryBuffer) throws SystemError, Exception
    {
        Log.debug("Inside netReversals");
        StringBuffer tempbuf = null;
        Merchants merchants = new Merchants();
        try
        {
//            Log.info(reversal.toString());
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
            StringBuffer sb1 = new StringBuffer("select fromid,toid,sum(amount) as sum, count(*) as count from transactions where ");
            sb1.append(" (dtstamp >=?");
            sb1.append(" and dtstamp <=? ) ");
            sb1.append(" and fromid ?");
            sb1.append(" and type='reversal'");
            sb1.append(" group by toid,fromid");


            StringBuffer sb2 = new StringBuffer("select fromid,toid,sum(amount) as sum, count(*) as count from transactions where ");
            sb2.append(" (dtstamp >=?" );
            sb2.append(" and dtstamp <=? ) ");
            sb2.append(" and fromid ?");
//            sb1.append(" and type in ('reversal','chargesforreversal','reversalofcharges')");
            sb2.append(" and type='chargesforreversal'");
            sb2.append(" group by toid,fromid");


            StringBuffer sb3 = new StringBuffer("select fromid,toid,sum(amount) as sum, count(*) as count from transactions where ");
            sb3.append(" (dtstamp >=?");
            sb3.append(" and dtstamp <=? ) ");
            sb3.append("  and toid ?");
//            sb1.append(" and type in ('reversal','chargesforreversal','reversalofcharges')");
            sb3.append(" and type='reversalofcharges'");
            sb3.append(" group by toid,fromid");

            StringBuffer sb4 = new StringBuffer("select fromid,toid,sum(amount) as sum, count(*) as count from transactions where ");
            sb4.append(" (dtstamp >=?");
            sb4.append(" and dtstamp <=? ) ");
            sb4.append(" and fromid ?");
//            sb1.append(" and type in ('reversal','chargesforreversal','reversalofcharges')");
            sb4.append(" and type='taxonchargesforreversal'");
            sb4.append(" group by toid,fromid");

            StringBuffer sb5 = new StringBuffer("select fromid,toid,sum(amount) as sum, count(*) as count from transactions where ");
            sb5.append(" (dtstamp >=?");
            sb5.append(" and dtstamp <=? ) ");
            //if(queryBuffer.indexOf("not")!=-1)
            sb5.append(" and toid ?");
            //else
            //sb5.append(" and toid not"+queryBuffer+members);
//            sb1.append(" and type in ('reversal','chargesforreversal','reversalofcharges')");
            sb5.append(" and type='reversaloftaxoncharges'");
            sb5.append(" group by toid,fromid");

            PreparedStatement p1=conn.prepareStatement(sb1.toString());
            p1.setString(1,startdt);
            p1.setString(2,enddt);
            p1.setString(3,queryBuffer + members);
            Hashtable reversalhash = Database.getHashFromResultSet(p1.executeQuery());
            int revwithdrawsize = reversalhash.size();

            Hashtable chargeshash = null;
            Hashtable revchargeshash = null;
            Hashtable taxonchargeshash = null;
            Hashtable revtaxonchargeshash = null;
            if (revwithdrawsize > 0)
            {
                PreparedStatement p2=conn.prepareStatement(sb2.toString());
                p2.setString(1,startdt);
                p2.setString(2,enddt);
                p2.setString(3,queryBuffer + members);
                chargeshash = Database.getHashFromResultSet(p2.executeQuery());
                PreparedStatement p3=conn.prepareStatement(sb3.toString());
                p3.setString(1,startdt);
                p3.setString(2,enddt);
                p3.setString(3,queryBuffer + members);
                revchargeshash = Database.getHashFromResultSet(p3.executeQuery());
                PreparedStatement p4=conn.prepareStatement(sb4.toString());
                p4.setString(1,startdt);
                p4.setString(2,enddt);
                p4.setString(3,queryBuffer + members);
                taxonchargeshash = Database.getHashFromResultSet(p4.executeQuery());
                PreparedStatement p5=conn.prepareStatement(sb5.toString());
                p5.setString(1,startdt);
                p5.setString(2,enddt);
                p5.setString(3,queryBuffer + members);
                revtaxonchargeshash = Database.getHashFromResultSet(p5.executeQuery());
            }


            Hashtable hash = new Hashtable();
            Hashtable reversaltemphash = null;
            Hashtable chargestemphash = null;
            Hashtable revchargestemphash = null;
            Hashtable taxonchargestemphash = null;
            Hashtable revtaxonchargestemphash = null;

            BigDecimal refund = null;
            for (int i = 1; i <= revwithdrawsize; i++)
            {
                isreversal = true;
                reversaltemphash = (Hashtable) reversalhash.get("" + i);
                String fromid = (String) reversaltemphash.get("fromid");

                chargestemphash = (Hashtable) chargeshash.get("" + i);
                revchargestemphash = (Hashtable) revchargeshash.get("" + i);
                taxonchargestemphash = (Hashtable) taxonchargeshash.get("" + i);

                for (int j = 1; j <= revtaxonchargeshash.size(); j++)
                {
                    Hashtable temphash = (Hashtable) revtaxonchargeshash.get("" + j);
                    if (fromid.equals((String) temphash.get("toid")))
                    {
                        revtaxonchargestemphash = temphash;
                        break;
                    }
                }


                Log.debug(" reversaltemphash = " + reversaltemphash);
                Log.debug("chargestemphash=        " + chargestemphash);
                Log.debug("revchargestemphash=     " + revchargestemphash);
                Log.debug("taxonchargestemphash=   " + taxonchargestemphash);
                Log.debug("revtaxonchargestemphash=" + revtaxonchargestemphash + "\n");


                refund = new BigDecimal("0");
                BigDecimal revrsal = new BigDecimal((String) reversaltemphash.get("sum"));
                BigDecimal charge = new BigDecimal((String) chargestemphash.get("sum"));
                BigDecimal revcharge = new BigDecimal((String) revchargestemphash.get("sum"));

                BigDecimal taxoncharge = null;
                BigDecimal revtax = null;

                if (taxonchargestemphash == null) taxoncharge = new BigDecimal("0");
                else taxoncharge = new BigDecimal((String) taxonchargestemphash.get("sum"));

                if (revtaxonchargestemphash == null) revtax = new BigDecimal("0");
                else revtax = new BigDecimal((String) revtaxonchargestemphash.get("sum"));
                // REFUND = AMOUNT - COMMREFUND + FEES + TAXONCHRG - REVTAX
                // refund = reversal - reversalofcharges + chargesforreversal +taxonchargesforreversal - reversaloftaxoncharges
                refund = refund.add(revrsal);
                refund = refund.add(charge);
                refund = refund.subtract(revcharge);
                refund = refund.add(taxoncharge);
                refund = refund.subtract(revtax);

                String fromname = merchants.getCompany((String) reversaltemphash.get("fromid"));
                if (fromname == null) fromname = "&nbsp;";
                hash.put("COMPANYNAME", fromname);
                hash.put("MERCHANTID", (String) reversaltemphash.get("fromid"));


                hash.put("AMOUNT", revrsal.toString());
                hash.put("FEES", charge.toString());
                hash.put("TAXONCHRG", taxoncharge.toString());
                hash.put("REVTAX", revtax.toString());
                hash.put("COMMREFUND", revcharge.toString());
                hash.put("REFUND", refund.toString());
                tempbuf.append(Functions.replaceTag(str, hash));
            }

            tempbuf.append(reversal.substring(lpos + endlength));
        }
        catch (SQLException e)
        {
            Log.error("Leaving Finance throwing SQL Exception as System Error : ",e);
            throw new SystemError("Error : " + e.getMessage());
        }
        return tempbuf.toString();
    }

    public String netInternalTrf(String queryBuffer) throws SystemError, Exception
    {
        Log.debug("Inside netinternaltrfs");
        StringBuffer tempbuf = null;
        Merchants merchants = new Merchants();
        try
        {
//            Log.info(internaltrf.toString());
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
            StringBuffer sb1 = new StringBuffer("select * from transactions where ");
            sb1.append(" (dtstamp >=?");
            sb1.append(" and dtstamp <=? ) ");
            sb1.append(" and fromid ?");/*@todo check the query*/
//            sb1.append(" and type in ('internaltransfer','othercharges') ");
            sb1.append(" and (type='internaltransfer' or type='othercharges') ");
            //sb1.append(" and a.memberid = b.toid and c.memberid = b.fromid ");
            //sb1.append(" group by b.type,a.memberid,b.fromid");
            sb1.append(" order by transid");

            PreparedStatement p1=conn.prepareStatement(sb1.toString());
            p1.setString(1,startdt);
            p1.setString(2,enddt);
            p1.setString(3,queryBuffer + members);
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
                if (fromname == null) fromname = "&nbsp;";
                if (toname == null) toname = "&nbsp;";

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
            Log.error("Leaving Finance throwing SQL Exception as System Error : ",e);
            throw new SystemError("Error : " + e.getMessage());
        }

        return tempbuf.toString();
    }

    public String reversalOfChargeback(String queryBuffer) throws SystemError, Exception
    {
        Log.debug("Inside reversalOfChargeback");
        StringBuffer tempbuf = null;
        Merchants merchants = new Merchants();
        try
        {
//            Log.info(revofchbk.toString());
            int fpos = (revofchbk.toString()).indexOf(loop);
            int lpos = (revofchbk.toString()).indexOf(end);

            String str = revofchbk.substring(fpos + looplength, lpos);
            tempbuf = new StringBuffer(revofchbk.substring(0, fpos));

            //Chargebacks reversal
            StringBuffer sb1 = new StringBuffer("select fromid,toid, sum(amount) as sum, count(*) as count from transactions where ");
            sb1.append(" dtstamp >=?");
            sb1.append(" and  dtstamp <=?");
            sb1.append(" and toid ?");
            sb1.append(" and type = 'chargebackreversal' group by toid order by toid");

            PreparedStatement p1=conn.prepareStatement(sb1.toString());
            p1.setString(1,startdt);
            p1.setString(2,enddt);
            p1.setString(3,queryBuffer + members);
            ResultSet result = p1.executeQuery();

            Hashtable hash = new Hashtable();

            while (result.next())
            {
                isreversalofchargeback = true;
                String amt = result.getString("sum");
                //String company_name=result.getString("company_name");
                String company_name = merchants.getCompany(result.getString("toid"));

                if (amt == null) amt = "0";
                if (company_name == null) company_name = "&nbsp;";

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

        return tempbuf.toString();
    }

}
