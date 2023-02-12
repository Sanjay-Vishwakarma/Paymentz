package com.directi.pg;

import com.logicboxes.util.ApplicationProperties;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.*;

//import PaymentClient.*;

public class FraudAlert
{
    //static Category cat = Category.getInstance(Merchants.class.getName());
    static Logger Log = new Logger(Finance.class.getName());

    Hashtable memberHRmailhash = null;
    Hashtable memberMRmailhash = null;
    Hashtable memberLRmailhash = null;
    Hashtable memberproofmailhash = null;


    Hashtable memberemailaddresshash = new Hashtable();
    Hashtable membercompanynamehash = new Hashtable();
    Vector memberhighrisk = new Vector();
    Vector membermidrisk = new Vector();
    Vector memberlowrisk = new Vector();
    Vector memberproofrisk = new Vector();
    //static Vector unboilchar=new Vector();

    Vector unsubcribed = new Vector();

    String query = "";
    String startdt = "";
    String enddt = "";
    static String date = "";
    Connection con = null;

    public FraudAlert(Date dt) throws SystemError
    {

        Log.debug("enter inside fraud alter class");
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

            memberHRmailhash = new Hashtable();
            memberMRmailhash = new Hashtable();
            memberLRmailhash = new Hashtable();
            memberproofmailhash = new Hashtable();

            con = Database.getConnection();
            //      sendDiffBoilnameAlert(); //set recordset for diif boiled name alert transaction
            setCountryMismatchMail(); //set recordset for billing address mismatch data
            setMachineAlert(); //set recordset for machine alert transaction
            setIpAlert();      //set recordset for ip alert transaction
            setEmailAlert();   //set recordset for email alert transaction


            setLRMail();  //set recordset for low risk alert transaction
            setProofRequired();      //set recordset for proof require transaction

            setBehaviouralAlert();  // set recordsset for copy-paste transactions
            sendMailToMerchant();

        }
        catch (NumberFormatException nfe)
        {
            startdt = "-";
            enddt = "-";
        }
        finally
        {
            try
            {
                if (con != null)
                    con.close();
            }
            catch (SQLException sqle)
            {
                Log.error("Exception while closing connection in FraudAlert.java",sqle);
            }
        }

    }

    public void setMachineAlert() throws SystemError
    {
        Log.debug("Inside setMachineAlert");

        try
        {
            //select all mids which are used with different cards from last one month record.
            String query = "select mid,count(distinct(ccnum)) totalccnum from transaction_icicicredit where  mid is not null and  (TO_DAYS(now())-TO_DAYS(from_unixtime(transaction_icicicredit.dtstamp))<=31)  group by mid having totalccnum > 1";

            ResultSet rs0 = Database.executeQuery(query, con);

            StringBuffer csvmid = new StringBuffer();

            if (rs0.next())
                csvmid.append(rs0.getString("mid"));

            while (rs0.next())
            {
                csvmid.append("," + rs0.getString("mid"));
            }

            if (csvmid.length() > 0)
            {
                //select only those mids from above list on which at least one transaction was authsuccessful or proof require  from last one month record.
                StringBuffer sb1 = new StringBuffer("select distinct mid from transaction_icicicredit where mid in (?) and ");
                sb1.append("status in ('authsuccessful','proofrequired','capturesuccess','capturestarted','podsent') and  (TO_DAYS(now())-TO_DAYS(from_unixtime(transaction_icicicredit.dtstamp))<=31) ");



                csvmid = new StringBuffer();
                PreparedStatement ps=con.prepareStatement(sb1.toString());
                ps.setString(1,csvmid.toString());
                ResultSet rs = ps.executeQuery();

                while (rs.next())
                {
                    csvmid.append(rs.getString("mid") + ",");  //put in commaseperated string
                }

                // select all records for thses mid on which still transaction is not settled but one of transaction is on auth successful or proof require mode..
                if (csvmid.length() > 1)
                {
                    csvmid.deleteCharAt(csvmid.length() - 1);

                    //saperate out High risk and low risk fraud transaction mid
                    String boilquery = "select ccnum,boiledname,mid from transaction_icicicredit where mid in (?) and (TO_DAYS(now())-TO_DAYS(from_unixtime(transaction_icicicredit.dtstamp))<=31)  order by mid";
                    PreparedStatement p=con.prepareStatement(boilquery);
                    p.setString(1,csvmid.toString());
                    ResultSet rs1 = p.executeQuery();


                    StringBuffer csvHRmid = new StringBuffer();
                    StringBuffer csvMRmid = new StringBuffer();
                    String name = "";
                    String ccnum = "";
                    String ccnum1 = "";
                    String ccnum2 = "";
                    int diffcc = 0;
                    String mid = "";
                    boolean hrmid = false;
                    while (rs1.next())
                    {
                        if (!rs1.getString("mid").equals(mid))
                        {
                            ccnum = PzEncryptor.decryptPAN(rs1.getString("ccnum"));
                            ccnum1 = "";
                            ccnum2 = "";
                            diffcc = 0;
                            mid = rs1.getString("mid");
                            name = rs1.getString("boiledname");
                            hrmid = false;   //new mid is got so set boolean false
                            continue;
                        }
                        else     //we are attending same mid which is already set in HR alert mid list then continue looping till you get new mid so that it will not added in MRmid
                        {
                            // as it is same mid count howmany different cards used.As these are the mids on which more than one cards are used.
                            //if card numbers are higher then 3 then even if boil name is same it should come into high risk fraud alert

                            if (!rs1.getString("ccnum").equals(ccnum)) //if card is different
                            {
                                if (!ccnum1.equals(""))  //if we have assigned ccnum1 then check with it
                                {
                                    if (!ccnum1.equals(ccnum)) //if it is different from ccnum1 then
                                    {
                                        if (!ccnum2.equals(""))   // if we have assign ccnum2 then check with it
                                        {
                                            if (!ccnum2.equals(ccnum)) //if it is different then increase count
                                                diffcc += 1;
                                        }
                                        else
                                        {
                                            diffcc += 1;  //increase different card counter
                                            ccnum2 = rs1.getString("ccnum");  //assing to second temp card ccnum2
                                        }
                                    }

                                }
                                else
                                {
                                    diffcc += 1;  //increase different card counter
                                    ccnum1 = rs1.getString("ccnum");  //assing to first temp card ccnum1
                                }
                            }


                            if (hrmid)
                                continue;
                        }
                        if (!rs1.getString("boiledname").equals(name) || diffcc > 2)
                        {

                            csvHRmid.append(mid + ","); //once you found this mid is for high risk alert loop  till you get new mid
                            int index = csvMRmid.toString().lastIndexOf(mid); //remove same mid from csvMRmid if it is added
                            while (index != -1)
                            {
                                String tempstr = csvMRmid.substring(0, index);
                                csvMRmid = csvMRmid.delete(0, csvMRmid.length());
                                csvMRmid.append(tempstr); //logically if this mid is added in csvMRmid then it must be last

                                index = csvMRmid.toString().lastIndexOf(mid);
                            }
                            hrmid = true;
                            continue;
                        }
                        else    //if this mid is not added in HRmid then add it
                            csvMRmid.append(mid + ",");

                    }
                    if (csvHRmid.length() > 1)
                    {
                        csvHRmid.deleteCharAt(csvHRmid.length() - 1);
                        //prepare mailbody for Highrisk alert
                        //  query = "select mid,name,status,emailaddr,toid,icicitransid,amount,description,ipaddress,company_name,notifyemail,from_unixtime(transaction_icicicredit.dtstamp) as \"dtstamp\" from transaction_icicicredit,members where mid in(" + csvHRmid.toString() + ") and (TO_DAYS(now())-TO_DAYS(from_unixtime(transaction_icicicredit.dtstamp))<31) and ccnum is not null and  status in ('authfailed','failed','cancelled','authcancelled','authsuccessful','proofrequired') and memberid=toid order by toid,transaction_icicicredit.mid,transaction_icicicredit.dtstamp";

                        //all transaction should displayed
                        query = "select mid,name,status,emailaddr,toid,icicitransid,amount,description,ipaddress,company_name,notifyemail,from_unixtime(transaction_icicicredit.dtstamp) as \"dtstamp\" from transaction_icicicredit,members where mid in(?) and (TO_DAYS(now())-TO_DAYS(from_unixtime(transaction_icicicredit.dtstamp))<=31) and ccnum is not null and memberid=toid order by toid,transaction_icicicredit.mid,transaction_icicicredit.dtstamp";

                        PreparedStatement pstmt=con.prepareStatement(query);
                        pstmt.setString(1,csvHRmid.toString());
                        setHRMail(pstmt.executeQuery(), "Different Cards used from the same Machine with different name", "mid");


                    }

                    if (csvMRmid.length() > 1)
                    {
                        csvMRmid.deleteCharAt(csvMRmid.length() - 1);
                        //prepare mail body for middelrisk alret
                        query = "select mid,name,status,emailaddr,toid,icicitransid,amount,description,ipaddress,company_name,notifyemail,from_unixtime(transaction_icicicredit.dtstamp) as \"dtstamp\" from transaction_icicicredit,members where mid in(?) and (TO_DAYS(now())-TO_DAYS(from_unixtime(transaction_icicicredit.dtstamp))<=31) and ccnum is not null and memberid=toid order by toid,transaction_icicicredit.mid,transaction_icicicredit.dtstamp";

                        PreparedStatement pst=con.prepareStatement(query);
                        pst.setString(1,csvMRmid.toString());
                        setMRMail(pst.executeQuery(), "Different Cards used from the same Machine with same name", "mid");


                    }
                    ccnum = null;
                    ccnum1 = null;
                    ccnum2 = null;
                }
            }

        }
        catch (SQLException e)
        {

            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(con);
        }
        Log.debug("Leaving setMachineAlert");
    }


    public void setIpAlert() throws SystemError
    {
        Log.debug("Inside setIpAlert");

        try
        {
            String query = "select ipcode,count(distinct(ccnum)) totalccnum from transaction_icicicredit where  ipcode is not null and  (TO_DAYS(now())-TO_DAYS(from_unixtime(transaction_icicicredit.dtstamp))<=8)  group by ipcode having totalccnum >1";
            ResultSet rs0 = Database.executeQuery(query, con);

            StringBuffer csvip = new StringBuffer();

            if (rs0.next())
                csvip.append(rs0.getString("ipcode"));

            while (rs0.next())
            {
                csvip.append("," + rs0.getString("ipcode"));
            }

            if (csvip.length() > 0)
            {
                StringBuffer sb1 = new StringBuffer("select distinct ipcode from transaction_icicicredit where  ipcode in (?) and ");
                sb1.append(" status in ('authsuccessful','proofrequired','capturesuccess','capturestarted','podsent') and (TO_DAYS(now())-TO_DAYS(from_unixtime(transaction_icicicredit.dtstamp))<=8) ");



                csvip = new StringBuffer();
                PreparedStatement ps=con.prepareStatement(sb1.toString());
                ps.setString(1,csvip.toString());
                ResultSet rs = ps.executeQuery();

                while (rs.next())
                {
                    csvip.append(rs.getString("ipcode") + ",");
                }


                if (csvip.length() > 1)
                {
                    csvip.deleteCharAt(csvip.length() - 1);

                    //saperate out High risk and low risk fraud transaction mid
                    String boilquery = "select ccnum,boiledname,ipcode from transaction_icicicredit where ipcode in (?) and (TO_DAYS(now())-TO_DAYS(from_unixtime(transaction_icicicredit.dtstamp))<=8)  order by ipcode";
                    PreparedStatement p=con.prepareStatement(boilquery);
                    p.setString(1,csvip.toString());
                    ResultSet rs1 = p.executeQuery();


                    StringBuffer csvHRipcode = new StringBuffer();
                    StringBuffer csvMRipcode = new StringBuffer();
                    String name = "";
                    String ccnum = "";
                    String ccnum1 = "";
                    String ccnum2 = "";
                    int diffcc = 0;
                    String ipcode = "";
                    boolean hripcode = false;
                    while (rs1.next())
                    {
                        if (!rs1.getString("ipcode").equals(ipcode))
                        {
                            ccnum = rs1.getString("ccnum");
                            ccnum1 = "";
                            ccnum2 = "";
                            diffcc = 0;
                            ipcode = rs1.getString("ipcode");
                            name = rs1.getString("boiledname");
                            hripcode = false;   //new mid is got so set boolean false
                            continue;
                        }
                        else     //we are attending same mid which is already set in HR alert mid list then continue looping till you get new mid so that it will not added in MRmid
                        {
                            if (!rs1.getString("ccnum").equals(ccnum)) //if card is different
                            {
                                if (!ccnum1.equals(""))  //if we have assigned ccnum1 then check with it
                                {
                                    if (!ccnum1.equals(ccnum)) //if it is different from ccnum1 then
                                    {
                                        if (!ccnum2.equals(""))   // if we have assign ccnum2 then check with it
                                        {
                                            if (!ccnum2.equals(ccnum)) //if it is different then increase count
                                                diffcc += 1;
                                        }
                                        else
                                        {
                                            diffcc += 1;  //increase different card counter
                                            ccnum2 = rs1.getString("ccnum");  //assing to second temp card ccnum2
                                        }
                                    }

                                }
                                else
                                {
                                    diffcc += 1;  //increase different card counter
                                    ccnum1 = rs1.getString("ccnum");  //assing to first temp card ccnum1
                                }
                            }


                            if (hripcode)
                                continue;
                        }
                        if (!rs1.getString("boiledname").equals(name) || diffcc > 2)
                        {

                            csvHRipcode.append(ipcode + ","); //once you found this mid is for high risk alert loop  till you get new mid
                            int index = csvMRipcode.toString().lastIndexOf(ipcode); //remove same mid from csvMRmid if it is added
                            while (index != -1)
                            {
                                String tempstr = csvMRipcode.substring(0, index);
                                csvMRipcode = csvMRipcode.delete(0, csvMRipcode.length());
                                csvMRipcode.append(tempstr); //logically if this mid is added in csvMRmid then it must be last

                                index = csvMRipcode.toString().lastIndexOf(ipcode);
                            }
                            hripcode = true;
                            continue;
                        }
                        else    //if this mid is not added in HRmid then add it
                            csvMRipcode.append(ipcode + ",");

                    }
                    if (csvHRipcode.length() > 1)
                    {
                        csvHRipcode.deleteCharAt(csvHRipcode.length() - 1);
                        //prepare mailbody for Highrisk alert
                        query = "select ipcode,mid,name,status,emailaddr,toid,icicitransid,amount,description,ipaddress,company_name,notifyemail,from_unixtime(transaction_icicicredit.dtstamp) as \"dtstamp\" from transaction_icicicredit,members where ipcode in(?) and (TO_DAYS(now())-TO_DAYS(from_unixtime(transaction_icicicredit.dtstamp))<=8) and ccnum is not null and  memberid=toid order by toid,transaction_icicicredit.ipcode,transaction_icicicredit.dtstamp";

                        PreparedStatement pst=con.prepareStatement(query);
                        pst.setString(1,csvHRipcode.toString());
                        setHRMail(pst.executeQuery(), "Different Cards used from the same IP Address with different name", "ipcode");


                    }

                    if (csvMRipcode.length() > 1)
                    {
                        csvMRipcode.deleteCharAt(csvMRipcode.length() - 1);
                        //prepare mail body for middelrisk alret
                        query = "select ipcode,mid,name,status,emailaddr,toid,icicitransid,amount,description,ipaddress,company_name,notifyemail,from_unixtime(transaction_icicicredit.dtstamp) as \"dtstamp\" from transaction_icicicredit,members where ipcode in(?) and (TO_DAYS(now())-TO_DAYS(from_unixtime(transaction_icicicredit.dtstamp))<=8) and ccnum is not null and  memberid=toid order by toid,transaction_icicicredit.ipcode,transaction_icicicredit.dtstamp";

                        PreparedStatement pst=con.prepareStatement(query);
                        pst.setString(1,csvMRipcode.toString());
                        setMRMail(pst.executeQuery(), "Different Cards used from the same IP Address with same name", "ipcode");


                    }
                   ccnum = null;
                    ccnum1 = null;
                    ccnum2 = null;

                }
            }

        }
        catch (SQLException e)
        {

            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(con);
        }

        Log.debug("leaving setIpAlert");
    }


    public void setEmailAlert() throws SystemError
    {
        Log.debug("Inside setEmailAlert");

        try
        {
            String query = "select emailaddr,count(distinct(ccnum)) totalccnum from transaction_icicicredit where  emailaddr is not null and  (TO_DAYS(now())-TO_DAYS(from_unixtime(transaction_icicicredit.dtstamp))<=31)  group by emailaddr having totalccnum >1";

            ResultSet rs0 = Database.executeQuery(query, con);


            StringBuffer csvemail = new StringBuffer();

            if (rs0.next())
                csvemail.append("'" + rs0.getString("emailaddr") + "'");

            while (rs0.next())
            {
                csvemail.append(",'" + rs0.getString("emailaddr") + "'");
            }

            if (csvemail.length() > 0)
            {
                StringBuffer sb1 = new StringBuffer("select distinct emailaddr from transaction_icicicredit where  emailaddr in (?) and ");
                sb1.append(" status in ('authsuccessful','proofrequired','capturesuccess','capturestarted','podsent') and (TO_DAYS(now())-TO_DAYS(from_unixtime(transaction_icicicredit.dtstamp))<=31) ");



                csvemail = new StringBuffer();
                PreparedStatement pst=con.prepareStatement(sb1.toString());
                pst.setString(1,csvemail.toString());
                ResultSet rs = pst.executeQuery();

                while (rs.next())
                {
                    csvemail.append("'" + rs.getString("emailaddr") + "',");
                }


                if (csvemail.length() > 1)
                {
                    csvemail.deleteCharAt(csvemail.length() - 1);

                    //saperate out High risk and low risk fraud transaction emailaddr
                    String boilquery = "select ccnum,boiledname,emailaddr from transaction_icicicredit where emailaddr in (?) and (TO_DAYS(now())-TO_DAYS(from_unixtime(transaction_icicicredit.dtstamp))<=31)  order by emailaddr";
                    PreparedStatement ps=con.prepareStatement(boilquery);
                    ps.setString(1,csvemail.toString());
                    ResultSet rs1 = ps.executeQuery();


                    StringBuffer csvHRemail = new StringBuffer();
                    StringBuffer csvMRemail = new StringBuffer();
                    String name = "";
                    String ccnum = "";
                    String ccnum1 = "";
                    String ccnum2 = "";
                    int diffcc = 0;
                    String emailaddr = "";
                    boolean hremail = false;
                    while (rs1.next())
                    {
                        if (!rs1.getString("emailaddr").equals(emailaddr))
                        {
                            ccnum = rs1.getString("ccnum");
                            ccnum1 = "";
                            ccnum2 = "";
                            diffcc = 0;
                            emailaddr = rs1.getString("emailaddr");
                            name = rs1.getString("boiledname");
                            hremail = false;   //new emailaddr is got so set boolean false
                            continue;
                        }
                        else     //we are attending same emailaddr which is already set in HR alert emailaddr list then continue looping till you get new emailaddr so that it will not added in MRemailaddr
                        {
                            if (!rs1.getString("ccnum").equals(ccnum)) //if card is different
                            {
                                if (!ccnum1.equals(""))  //if we have assigned ccnum1 then check with it
                                {
                                    if (!ccnum1.equals(ccnum)) //if it is different from ccnum1 then
                                    {
                                        if (!ccnum2.equals(""))   // if we have assign ccnum2 then check with it
                                        {
                                            if (!ccnum2.equals(ccnum)) //if it is different then increase count
                                                diffcc += 1;
                                        }
                                        else
                                        {
                                            diffcc += 1;  //increase different card counter
                                            ccnum2 = rs1.getString("ccnum");  //assing to second temp card ccnum2
                                        }
                                    }

                                }
                                else
                                {
                                    diffcc += 1;  //increase different card counter
                                    ccnum1 = rs1.getString("ccnum");  //assing to first temp card ccnum1
                                }
                            }


                            if (hremail)
                                continue;
                        }
                        if (!rs1.getString("boiledname").equals(name) || diffcc > 2)
                        {

                            csvHRemail.append("'" + emailaddr + "',"); //once you found this emailaddr is for high risk alert loop  till you get new emailaddr

                            int index = csvMRemail.toString().lastIndexOf("'" + emailaddr + "'"); //remove same emailaddr from csvMRemailaddr if it is added
                            while (index != -1)
                            {
                                String tempstr = csvMRemail.substring(0, index);

                                csvMRemail = csvMRemail.delete(0, csvMRemail.length());

                                csvMRemail.append(tempstr); //logically if this emailaddr is added in csvMRemailaddr then it must be last

                                /* if(csvMRemail.length()>1)                   //if it is not the only one emailaddr which was added in MRemailaddr then
                                     csvMRemail.deleteCharAt(csvMRemail.length()-1); //remove last coma

                                 Log.info("csv MR after deleting last char="+csvMRemail);
                                */
                                index = csvMRemail.toString().lastIndexOf("'" + emailaddr + "'");
                            }
                            hremail = true;
                            continue;
                        }
                        else    //if this emailaddr is not added in HRemailaddr then add it
                            csvMRemail.append("'" + emailaddr + "',");

                    }
                    if (csvHRemail.length() > 1)
                    {
                        csvHRemail.deleteCharAt(csvHRemail.length() - 1);
                        //prepare mailbody for Highrisk alert
                        query = "select mid,name,status,emailaddr,toid,icicitransid,amount,description,ipaddress,company_name,notifyemail,from_unixtime(transaction_icicicredit.dtstamp) as \"dtstamp\" from transaction_icicicredit,members where emailaddr in(?) and (TO_DAYS(now())-TO_DAYS(from_unixtime(transaction_icicicredit.dtstamp))<=31) and ccnum is not null and memberid=toid order by toid,transaction_icicicredit.emailaddr,transaction_icicicredit.dtstamp";

                        PreparedStatement pa=con.prepareStatement(query);
                        pa.setString(1,csvHRemail.toString());
                        setHRMail(pa.executeQuery(), "Different Cards used from the same Email Address with different name", "emailaddr");


                    }

                    if (csvMRemail.length() > 1)
                    {
                        csvMRemail.deleteCharAt(csvMRemail.length() - 1);
                        //prepare mail body for middelrisk alret
                        query = "select mid,name,status,emailaddr,toid,icicitransid,amount,description,ipaddress,company_name,notifyemail,from_unixtime(transaction_icicicredit.dtstamp) as \"dtstamp\" from transaction_icicicredit,members where emailaddr in() and (TO_DAYS(now())-TO_DAYS(from_unixtime(transaction_icicicredit.dtstamp))<=31) and ccnum is not null and memberid=toid order by toid,transaction_icicicredit.emailaddr,transaction_icicicredit.dtstamp";

                        PreparedStatement pa=con.prepareStatement(query);
                        pa.setString(1,csvMRemail.toString());
                        setMRMail(pa.executeQuery(), "Different Cards used from the same Email Address with same name", "emailaddr");

                    }
                   ccnum = null;
                    ccnum1 = null;
                    ccnum2 = null;
                }
            }

        }
        catch (SQLException e)
        {

            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(con);
        }

        Log.debug("Leaving setEmailAlert");
    }


    public void setHRMail(ResultSet rs2, String heading, String type) throws SystemError
    {
        Log.debug("inside setHRMail for " + type);
        try
        {
            StringBuffer mailbody = null;
            heading = "<p ><font color=\"#800000\"><b>" + heading + "</b></font></p>";

            int prevtoid = 0;
            String emailaddress = "";
            String prevvalue = "";
            int prevcount = 1;
            String tr = "";
            while (rs2.next())
            {
                int toid = rs2.getInt("toid");

                emailaddress = rs2.getString("notifyemail");
                memberemailaddresshash.put(toid + "", emailaddress);
                membercompanynamehash.put(toid + "", rs2.getString("company_name"));
                memberhighrisk.add(toid + "");


                if (!prevvalue.equals(rs2.getString(type)))
                {
                    prevcount++;
                }


                tr = "tr" + (prevcount % 2 + 1);
                prevvalue = rs2.getString(type);


                if (prevtoid == 0)   //at start of table show headings
                {
                    //td="td1";
                    mailbody = new StringBuffer();
                    StringBuffer sbuf = (StringBuffer) memberHRmailhash.get(toid + ""); //start feeling mail hash for this member
                    if (sbuf != null)
                        mailbody.append(sbuf.toString());
                    mailbody.append(heading);

                    mailbody.append("<table border=1 >");
                    mailbody.append("<tr class=\"heading\">");
                    mailbody.append("<td>Trackingid</td>");
                    mailbody.append("<td>Description</td>");
                    mailbody.append("<td>Amount</td>");
                    mailbody.append("<td>Card Holder</td>");
                    mailbody.append("<td>Ip Address</td>");
                    mailbody.append("<td>Emailaddr</td>");
                    mailbody.append("<td>Status</td>");
                    mailbody.append("<td>Date</td>");
                    mailbody.append("</tr>");

                }

                if (prevtoid != toid && prevtoid != 0)
                {
                    //td="td1";
                    memberHRmailhash.put(prevtoid + "", mailbody.append("</table>"));
                    //memberemailaddresshash.put(prevtoid+"",rs2.getString("notifyemail"));
                    mailbody = new StringBuffer();
                    StringBuffer sbuf = (StringBuffer) memberHRmailhash.get(toid + "");
                    if (sbuf != null)
                        mailbody.append(sbuf.toString());

                    mailbody.append(heading);
                    mailbody.append("<table border=1 >");
                    mailbody.append("<tr class=\"heading\">");
                    mailbody.append("<td>Trackingid</td>");
                    mailbody.append("<td>Description</td>");
                    mailbody.append("<td>Amount</td>");
                    mailbody.append("<td>Card Holder</td>");
                    mailbody.append("<td>Ip Address</td>");
                    mailbody.append("<td>Emailaddr</td>");
                    mailbody.append("<td>Status</td>");
                    mailbody.append("<td>Date</td>");
                    mailbody.append("</tr>");

                }


                mailbody.append("<tr class=\"" + tr + "\">");
                mailbody.append("<td>&nbsp;" + rs2.getString("icicitransid") + "</td>");
                mailbody.append("<td>&nbsp;" + rs2.getString("description") + "</td>");
                mailbody.append("<td>&nbsp;" + rs2.getString("amount") + "</td>");
                mailbody.append("<td>&nbsp;" + rs2.getString("name") + "</td>");
                mailbody.append("<td>&nbsp;" + rs2.getString("ipaddress") + "</td>");
                mailbody.append("<td>&nbsp;" + rs2.getString("emailaddr") + "</td>");
                mailbody.append("<td>&nbsp;" + rs2.getString("status") + "</td>");
                mailbody.append("<td>&nbsp;" + rs2.getString("dtstamp") + "</td>");
                //mailbody.append("<td>"+rs2.getString("count")+"</td>");
                mailbody.append("</tr>");

                prevtoid = toid;

            }
            if (mailbody != null)
                memberHRmailhash.put(prevtoid + "", mailbody.append("</table>"));

            //memberemailaddresshash.put(prevtoid+"",emailaddress);

        }
        catch (SQLException e)
        {

            throw new SystemError("Error : " + e.getMessage());
        }

        Log.debug("leaving setHRMail");
    }

    public void setMRMail(ResultSet rs2, String heading, String type) throws SystemError
    {
        Log.debug("inside setMRMail");
        try
        {
            StringBuffer mailbody = null;
            heading = "<p ><font color=\"#800000\"><b>" + heading + "</b></font></p>";

            int prevtoid = 0;
            String emailaddress = "";
            String prevvalue = "";
            int prevcount = 1;
            String tr = "";
            while (rs2.next())
            {
                int toid = rs2.getInt("toid");

                emailaddress = rs2.getString("notifyemail");
                memberemailaddresshash.put(toid + "", emailaddress);
                membercompanynamehash.put(toid + "", rs2.getString("company_name"));
                membermidrisk.add(toid + "");


                if (!prevvalue.equals(rs2.getString(type)))
                {
                    prevcount++;
                }


                tr = "tr" + (prevcount % 2 + 1);
                prevvalue = rs2.getString(type);


                if (prevtoid == 0)   //at start of table show headings
                {
                    //td="td1";
                    mailbody = new StringBuffer();
                    StringBuffer sbuf = (StringBuffer) memberMRmailhash.get(toid + ""); //start feeling mail hash for this member
                    if (sbuf != null)
                        mailbody.append(sbuf.toString());

                    mailbody.append(heading);

                    mailbody.append("<table border=1 >");
                    mailbody.append("<tr class=\"heading\">");
                    mailbody.append("<td>Trackingid</td>");
                    mailbody.append("<td>Description</td>");
                    mailbody.append("<td>Amount</td>");
                    mailbody.append("<td>Card Holder</td>");
                    mailbody.append("<td>Ip Address</td>");
                    mailbody.append("<td>Emailaddr</td>");
                    mailbody.append("<td>Status</td>");
                    mailbody.append("<td>Date</td>");
                    mailbody.append("</tr>");

                }

                if (prevtoid != toid && prevtoid != 0)
                {
                    //td="td1";
                    memberMRmailhash.put(prevtoid + "", mailbody.append("</table>"));
                    //memberemailaddresshash.put(prevtoid+"",rs2.getString("notifyemail"));
                    mailbody = new StringBuffer();
                    StringBuffer sbuf = (StringBuffer) memberMRmailhash.get(toid + "");
                    if (sbuf != null)
                        mailbody.append(sbuf.toString());

                    mailbody.append(heading);
                    mailbody.append("<table border=1 >");
                    mailbody.append("<tr class=\"heading\">");
                    mailbody.append("<td>Trackingid</td>");
                    mailbody.append("<td>Description</td>");
                    mailbody.append("<td>Amount</td>");
                    mailbody.append("<td>Card Holder</td>");
                    mailbody.append("<td>Ip Address</td>");
                    mailbody.append("<td>Emailaddr</td>");
                    mailbody.append("<td>Status</td>");
                    mailbody.append("<td>Date</td>");
                    mailbody.append("</tr>");

                }


                mailbody.append("<tr class=\"" + tr + "\">");
                mailbody.append("<td>&nbsp;" + rs2.getString("icicitransid") + "</td>");
                mailbody.append("<td>&nbsp;" + rs2.getString("description") + "</td>");
                mailbody.append("<td>&nbsp;" + rs2.getString("amount") + "</td>");
                mailbody.append("<td>&nbsp;" + rs2.getString("name") + "</td>");
                mailbody.append("<td>&nbsp;" + rs2.getString("ipaddress") + "</td>");
                mailbody.append("<td>&nbsp;" + rs2.getString("emailaddr") + "</td>");
                mailbody.append("<td>&nbsp;" + rs2.getString("status") + "</td>");
                mailbody.append("<td>&nbsp;" + rs2.getString("dtstamp") + "</td>");
                //mailbody.append("<td>"+rs2.getString("count")+"</td>");
                mailbody.append("</tr>");

                prevtoid = toid;

            }
            if (mailbody != null)
                memberMRmailhash.put(prevtoid + "", mailbody.append("</table>"));

            //memberemailaddresshash.put(prevtoid+"",emailaddress);

        }
        catch (SQLException e)
        {

            throw new SystemError("Error : " + e.getMessage());
        }
        Log.debug("leaving setMRMail ");

    }


    public void setLRMail() throws SystemError
    {
        Log.debug("inside setLRMail");
        try
        {
            query = "select name,status,emailaddr,toid,icicitransid,amount,description,authacquirerresponsecode,authqsiresponsecode,ipaddress,company_name,notifyemail,from_unixtime(transaction_icicicredit.dtstamp) as \"dtstamp\"  from transaction_icicicredit,members where authqsiresponsedesc=' not sufficient fund' and transaction_icicicredit.dtstamp > unix_timestamp(?) and transaction_icicicredit.dtstamp < unix_timestamp(?)  and memberid=toid order by toid,transaction_icicicredit.dtstamp";
            PreparedStatement p=con.prepareStatement(query);
            p.setString(1,startdt);
            p.setString(2,enddt);
            ResultSet rs2 = p.executeQuery();


            StringBuffer mailbody = null;
            StringBuffer sb = new StringBuffer();
            //heading="<tr bgcolor=\"#006699\"><td colspan=6><font color=\"#ffffff\">"+ heading +"</font></td></tr>";
            sb.append("<table>");
            sb.append("<tr><td>");
            sb.append("<b>LOW RISK FRAUD ALERTS</b><br>");
            sb.append("When a card holder tries a transaction and it fails - it would arise ");
            sb.append("suspicion. Primarily because most fraudsters have a list of cards some of ");
            sb.append("which work and some which don't. Most of the cards that do not work would be ");
            sb.append("because they have run out of funds. Therefore any transaction thatfails due ");
            sb.append("to a \"Insufficient Funds\" error is suspicious. Below is a list of all ");
            sb.append("transactions that have failed on your website due to similar suspicious ");
            sb.append("reasons.Below is the list of such transaction for the past one day.It is possible that the same person purchased the same order using ");
            sb.append("another Card which succeeded. You are therefore advised to check all orders ");
            sb.append("which match the below orders and have happened in the same day.");
            sb.append("</td></tr>");
            sb.append("</table><br><br>");

            sb.append("<table border=1>");
            sb.append("<tr class=\"heading\">");
            sb.append("<td>Trackingid</td>");
            sb.append("<td>Description</td>");
            sb.append("<td>Amount</td>");
            sb.append("<td>Card Holder</td>");
            sb.append("<td>Ip Address</td>");
            sb.append("<td>Emailaddr</td>");
            sb.append("<td>Date</td>");
            sb.append("</tr>");


            int prevtoid = 0;
            String emailaddress = "";

            while (rs2.next())
            {
                int toid = rs2.getInt("toid");

                //initilaise the first merchant
                emailaddress = rs2.getString("notifyemail");
                memberemailaddresshash.put(toid + "", emailaddress);
                membercompanynamehash.put(toid + "", rs2.getString("company_name"));
                memberlowrisk.add(toid + "");
                if (prevtoid == 0)
                {
                    mailbody = new StringBuffer();
                    mailbody.append(sb.toString());

                }

                if (prevtoid != toid && prevtoid != 0)
                {
                    memberLRmailhash.put(prevtoid + "", mailbody.append("</table>"));
                    mailbody = new StringBuffer();
                    mailbody.append(sb.toString());
                }

                mailbody.append("<tr class=\"tr1\">");
                mailbody.append("<td>&nbsp;" + rs2.getString("icicitransid") + "</td>");
                mailbody.append("<td>&nbsp;" + rs2.getString("description") + "</td>");
                mailbody.append("<td>&nbsp;" + rs2.getString("amount") + "</td>");
                mailbody.append("<td>&nbsp;" + rs2.getString("name") + "</td>");
                mailbody.append("<td>&nbsp;" + rs2.getString("ipaddress") + "</td>");
                mailbody.append("<td>&nbsp;" + rs2.getString("emailaddr") + "</td>");
                mailbody.append("<td>&nbsp;" + rs2.getString("dtstamp") + "</td>");
                //mailbody.append("<td>"+rs2.getString("authacquirerresponsecode")+"</td>");
                //mailbody.append("<td>"+rs2.getString("authqsiresponsecode")+"</td>");
                mailbody.append("</tr>");

                prevtoid = toid;

            }
            if (mailbody != null)
                memberLRmailhash.put(prevtoid + "", mailbody.append("</table>"));

            //memberemailaddresshash.put(prevtoid+"",emailaddress);

        }
        catch (SQLException e)
        {

            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(con);
        }

        Log.debug("leaving setLRMail");
    }


    public void setProofRequired() throws SystemError
    {
        Log.debug("inside setProofRequired");
        String company = ApplicationProperties.getProperty("COMPANY");

        try
        {
            query = "select name,status,emailaddr,toid,icicitransid,amount,description,company_name,ipaddress,notifyemail,from_unixtime(transaction_icicicredit.dtstamp) as \"dtstamp\"  from transaction_icicicredit,members where status ='proofrequired' and memberid=toid order by toid,transaction_icicicredit.dtstamp";

            ResultSet rs2 = Database.executeQuery(query, con);


            StringBuffer mailbody = null;
            StringBuffer sb = new StringBuffer();
            //heading="<tr bgcolor=\"#006699\"><td colspan=6><font color=\"#ffffff\">"+ heading +"</font></td></tr>";
            sb.append("<table>");
            sb.append("<tr><td>");
            sb.append("<b>PROOF PENDING TRANSACTION LIST</b><br>");
            sb.append("Based on rules and preferences set for your Merchant account, the following ");
            sb.append("Transactions are currently in \"proofpending\" mode. This means that ");
            sb.append(company + " awaits a fax document from the End Customer with the Front and ");
            sb.append("back of their card, and an authority letter before these transactions may be ");
            sb.append("captured. These transactions would be cancelled within 4 days of the ");
            sb.append("Transaction date if these documents are not recvd from the Customer.");
            sb.append("</td></tr>");
            sb.append("</table><br><br>");
            sb.append("<table border=1>");
            sb.append("<tr  class=\"heading\">");
            sb.append("<td>Trackingid</td>");
            sb.append("<td>Description</td>");
            sb.append("<td>Amount</td>");
            sb.append("<td>Card Holder</td>");
            sb.append("<td>Ip Address</td>");
            sb.append("<td>Emailaddr</td>");
            sb.append("<td>Date</td>");
            sb.append("</tr>");


            int prevtoid = 0;
            String emailaddress = "";

            while (rs2.next())
            {
                int toid = rs2.getInt("toid");

                //initilaise the first merchant
                emailaddress = rs2.getString("notifyemail");
                memberemailaddresshash.put(toid + "", emailaddress);
                membercompanynamehash.put(toid + "", rs2.getString("company_name"));
                memberproofrisk.add(toid + "");
                if (prevtoid == 0)
                {
                    mailbody = new StringBuffer();
                    mailbody.append(sb.toString());
                }

                if (prevtoid != toid && prevtoid != 0)
                {
                    memberproofmailhash.put(prevtoid + "", mailbody.append("</table>"));
                    mailbody = new StringBuffer();
                    mailbody.append(sb.toString());
                }

                mailbody.append("<tr class=\"tr1\">");
                mailbody.append("<td>&nbsp;" + rs2.getString("icicitransid") + "</td>");
                mailbody.append("<td>&nbsp;" + rs2.getString("description") + "</td>");
                mailbody.append("<td>&nbsp;" + rs2.getString("amount") + "</td>");
                mailbody.append("<td>&nbsp;" + rs2.getString("name") + "</td>");
                mailbody.append("<td>&nbsp;" + rs2.getString("ipaddress") + "</td>");
                mailbody.append("<td>&nbsp;" + rs2.getString("emailaddr") + "</td>");
                mailbody.append("<td>&nbsp;" + rs2.getString("dtstamp") + "</td>");
                //mailbody.append("<td>"+rs2.getString("count")+"</td>");
                mailbody.append("</tr>");

                prevtoid = toid;

            }
            //	Log.debug("mailbody "+ mailbody);
            if (mailbody != null)
                memberproofmailhash.put(prevtoid + "", mailbody.append("</table>"));

            //memberemailaddresshash.put(prevtoid+"",emailaddress);

        }
        catch (SQLException e)
        {

            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(con);
        }
        Log.debug("leaving setProofRequired");

    }


    public void setCountryMismatchMail() throws SystemError
    {
        Log.debug("inside setCountryMismatchMail");

        try
        {
            //select all last 24 hrs transactions which are successfully authorised or proof required

            query = "select icicitransid,country,ipaddress from transaction_icicicredit where status in ('authsuccessful','proofrequired','capturesuccess','capturestarted','podsent') and dtstamp >= unix_timestamp(?) and dtstamp <= unix_timestamp(?) order by toid,dtstamp";
            PreparedStatement p=con.prepareStatement(query);
            p.setString(1,startdt);
            p.setString(2,enddt);
            ResultSet rs0 = p.executeQuery();
            StringBuffer csvtrackingid = new StringBuffer();
            int count = 0;


            while (rs0.next())
            {
                String country = rs0.getString("country");
                String countryinrec = getCountry(rs0.getString("ipaddress"));

                if (countryinrec == null)  // if we don't have country in ipmap table for particular IP then skip it.
                    continue;

                if (!country.equals(countryinrec))
                {
                    if (count == 0)
                        csvtrackingid.append(rs0.getString("icicitransid"));
                    else
                        csvtrackingid.append("," + rs0.getString("icicitransid"));

                    count++;
                }

            }
            if (csvtrackingid.length() > 0)
            {
                //prepare mailbody for Highrisk alert
                query = "select transaction_icicicredit.country,name,status,emailaddr,toid,icicitransid,amount,description,ipaddress,company_name,notifyemail,from_unixtime(transaction_icicicredit.dtstamp) as \"dtstamp\" from transaction_icicicredit,members where icicitransid in(?) and status in ('authsuccessful','proofrequired','capturesuccess','capturestarted','podsent') and transaction_icicicredit.dtstamp >= unix_timestamp(?) and transaction_icicicredit.dtstamp <= unix_timestamp(?) and memberid=toid order by toid,transaction_icicicredit.dtstamp";

                PreparedStatement ps=con.prepareStatement(query);
                ps.setString(1,csvtrackingid.toString());
                ps.setString(2,startdt);
                ps.setString(3,enddt);
                setHRMail(ps.executeQuery(), "Billing Address MisMatch", "icicitransid");
            }

        }
        catch (SQLException e)
        {
            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(con);
        }

        Log.debug("leaving setCountryMismatchMail");

    }

    public void setBehaviouralAlert() throws SystemError
    {
        Log.info("Inside setBehaviouralAlert()");
        try
        {
            String id = "";
            //select all transaction which are authsuccess,proofrequired,capturestarted,capturesuccess or podsent and either name or card number was copy pasted
            StringBuffer query = new StringBuffer("select icicitransid from transaction_icicicredit ");
            query.append(" where cccp='Y' and namecp='Y' and addrcp='Y' ");
            query.append(" and status in ('authsuccessful','proofrequired','capturesuccess','capturestarted','podsent') ");
            query.append(" and transaction_icicicredit.dtstamp >= unix_timestamp(?) and transaction_icicicredit.dtstamp <= unix_timestamp(?)");


            PreparedStatement p=con.prepareStatement(query.toString());
            p.setString(1,startdt);
            p.setString(2,enddt);
            ResultSet rs = p.executeQuery();
            while (rs.next())
            {
                id = id + "," + rs.getString(1);
            }

            if (id.length() > 0)
                id = id.substring(1);
            else
                id = null;

            query = new StringBuffer("select name,status,emailaddr,toid,icicitransid,amount,description,ipaddress,company_name,notifyemail,from_unixtime(transaction_icicicredit.dtstamp) as \"dtstamp\" from transaction_icicicredit,members");
            query.append(" where ( cccp='Y' or namecp='Y' or addrcp='Y' ) ");
            query.append(" and status in ('authsuccessful','proofrequired','capturesuccess','capturestarted','podsent') ");
            query.append(" and transaction_icicicredit.dtstamp >= unix_timestamp(?) and transaction_icicicredit.dtstamp <= unix_timestamp(?) ");
            query.append(" and memberid=toid ");
            query.append(" and icicitransid not in (?)");
            query.append(" order by toid,transaction_icicicredit.dtstamp");



            PreparedStatement ps=con.prepareStatement(query.toString());
            ps.setString(1,startdt);
            ps.setString(2,enddt);
            ps.setString(3,id);
            setMRMail(ps.executeQuery(), "Behaviour Risk", "icicitransid");

            //select all transaction which are authsuccess,proofrequired,capturestarted,capturesuccess or podsent and both name and card number was copy pasted
            query = new StringBuffer("select name,status,emailaddr,toid,icicitransid,amount,description,ipaddress,company_name,notifyemail,from_unixtime(transaction_icicicredit.dtstamp) as \"dtstamp\" from transaction_icicicredit,members");
            query.append(" where cccp='Y' and namecp='Y' and addrcp='Y'");
            query.append(" and status in ('authsuccessful','proofrequired','capturesuccess','capturestarted','podsent')");
            query.append(" and transaction_icicicredit.dtstamp >= unix_timestamp(?) and transaction_icicicredit.dtstamp <= unix_timestamp(?)");
            query.append(" and memberid=toid ");
            query.append(" order by toid,transaction_icicicredit.dtstamp");


            PreparedStatement pst=con.prepareStatement(query.toString());
            pst.setString(1,startdt);
            pst.setString(2,enddt);
            setHRMail(pst.executeQuery(), "Behaviour Risk", "icicitransid");

        }
        catch (Exception e)
        {
            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(con);
        }
        Log.debug("Leaving setBehaviouralAlert()");
    }

    public void sendMailToMerchant() throws SystemError
    {

        Log.debug("inside sendMailToMerchant");
        String company = ApplicationProperties.getProperty("COMPANY");
        try
        {
            String unsubcribequery = "select memberid from members where mitigationmail='N'";
            ResultSet rs = Database.executeQuery(unsubcribequery, con);


            while (rs.next())
            {
                unsubcribed.add(rs.getString("memberid"));
            }

            Log.debug("unsubcribe members=" + unsubcribed.toString());

        }
        catch (Exception ex)
        {
            Log.error("Exception ",ex);
        }

        Enumeration enu = memberemailaddresshash.keys();

        while (enu.hasMoreElements())
        {
            String toid = (String) enu.nextElement();
            String emailaddress = (String) memberemailaddresshash.get(toid);
            String companyname = (String) membercompanynamehash.get(toid);

            if (!unsubcribed.contains(toid))
            {

                StringBuffer sb = new StringBuffer();
                sb.append("<html><head><style>\r\n.tr1{background:#FaEEE0;text-valign: center;text-align: LEFT;font-size:11px;font-family:arial,verdana,helvetica}\r\n\r\n.heading{background:#800000;text-valign: center;text-align: LEFT;font-size:12px;color:#ffffff;font-family:arial,verdana,helvetica}\r\n.tr2{background:#fffeec;text-valign: center;text-align: LEFT;font-size:11px;font-family:arial,verdana,helvetica}\r\n</style></head><body text=\"#800000\"><table>");
                sb.append("<tr><td>");
                sb.append("<b>Member ID:</b> " + toid + "<br>");
                sb.append("<b>Company Name:</b> " + companyname);
                sb.append("</td></tr>");
                sb.append("<tr><td>&nbsp;</td></tr>");
                sb.append("<tr><td>");
                sb.append(company + " has implemented several Fraud mitigation measures to minimise ");
                sb.append("fraud on your website. We send you a daily Fraud Alert which will allow you ");
                sb.append("to identify suspicious transactions on your website. Please check each of ");
                sb.append("these transactions before delivering the goods since they could possibly be ");
                sb.append("fraudulent transactions:");
                sb.append("</td></tr>");
                sb.append("<tr><td>&nbsp;</td></tr>");

                if (memberhighrisk.contains(toid))
                {
                    sb.append("<tr><td>");
                    sb.append("<b>HIGH RISK FRAUD ALERTS</b><br>");
                    sb.append("Our Fraud detection engine assigns a Risk percentage to transactions based ");
                    sb.append("on a VAST accumulated database of information and previous transactions. The ");
                    sb.append("below list of transactions are identified as high-risk transactions and it ");
                    sb.append("is suggested that you check EACH of these transactions below. These ");
                    sb.append("transactions have been authorised, but will have to be captured manually by ");
                    sb.append("you if you determine that the transaction is valid. ");
                    sb.append("</td></tr>");
                    sb.append("</table><br><br>");
                    sb.append(((StringBuffer) memberHRmailhash.get(toid)).toString());
                    sb.append("<br><br>");
                }
                if (membermidrisk.contains(toid))
                {

                    sb.append("<table><tr><td>");
                    sb.append("<b>MEDIUM RISK FRAUD ALERTS</b><br>");
                    sb.append("The below list of transactions are identified as medium-risk transactions ");
                    sb.append(" and it is suggested that you check EACH of these transactions below.");
                    sb.append("</td></tr>");
                    sb.append("</table><br>");
                    sb.append(((StringBuffer) memberMRmailhash.get(toid)).toString());
                    sb.append("<br><br>");
                }
                if (memberlowrisk.contains(toid))
                {
                    sb.append(((StringBuffer) memberLRmailhash.get(toid)).toString());
                    sb.append("<br><br>");
                }
                if (memberproofrisk.contains(toid))
                {
                    sb.append(((StringBuffer) memberproofmailhash.get(toid)).toString());
                    sb.append("<br><br>");
                }

                sb.append("</body></html>");

                //String adminEmail = ApplicationProperties.getProperty("COMPANY_ADMIN_EMAIL");
                //String fromAddress = ApplicationProperties.getProperty("COMPANY_FROM_ADDRESS");
                Log.debug("sending mail to merchant " + toid + emailaddress);
                //Mail.sendHtmlMail(emailaddress, fromAddress, null, adminEmail, company + " RISK MITIGATION and FRAUD REPORT for " + date, sb.toString());
                Log.debug("sent mail to merchant " + toid + emailaddress);
            }

        }


        Log.debug("emailaddresses are =" + memberemailaddresshash);

    }

    private String getCountry(String ipaddress)
    {
        //Log.info( "Inside getCountry" );
        String country = null;
        long tempcode = -9999;
        int ipcode = -9999;

        try
        {
            ipcode = InetAddress.getByName(ipaddress).hashCode();
            tempcode = ipcode - 2147483647;
            tempcode = tempcode + 2147483647L;

            query = "select country from ipmap where startip<=? and endip>=?";
            PreparedStatement pa=con.prepareStatement(query);
            pa.setLong(1,tempcode);
            pa.setLong(2,tempcode);
            ResultSet rs1 = pa.executeQuery();
            if (rs1.next())
                country = rs1.getString("country");

        }
        catch (UnknownHostException e)
        {
            Log.error(ipaddress + "ipaddress is either null or not in properformat.", e);
        }
        catch (SQLException e)
        {
            Log.error("Exception in getCountry() in FraudAlert.java",e);
        }

        catch (Exception error)
        {
            Log.error("Exception in getCountry() in FraudAlert.java",error);
        }
        finally
        {
            Database.closeConnection(con);
        }

        //   Log.info( "leaving getCountry" );
        return country;

    }


}
