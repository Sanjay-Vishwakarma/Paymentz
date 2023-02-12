package com.directi.pg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;

public class ProofReminderCron
{
    static Logger logger = new Logger(ProofReminderCron.class.getName());

    StringBuffer icicitransidsbuf = null;
    StringBuffer mailbody = null;
    Connection conn = null;
    StringBuffer selectquery = null;
    String amount = null;
    String icicitransid = null;
    String merchantid = null;
    String description = null;
    String currency = null;
    String transactionOrderDescription = null;
    String name = null;
    String emailaddr = null;
    String customer_email = null;
    String ipaddress = null;
    String company_name = null;
    String date = null;
    String authid = null;

    String contact_emails = null;
    String daysremaining = null;

    int toid = 0;
    int prevtoid = 0;
    int count = 0;
    int rssize = 0;


    public static void main(String[] arg)
    {
        /*
		ProofReminderCron pr=new ProofReminderCron();
		pr.sendCustomerReminder(4,2);
		System.out.println("Called sendReminder 2 days");
		pr.sendCustomerReminder(4,3);
		System.out.println("Called sendReminder 3 days");
		pr.cancelProofRequired(4);
		System.out.println("Called cancelProofRequired");
		*/
    }

/*

	public void sendReminder(int mindays,int daysover)
	{
		toid=0;
		prevtoid=0;
		count=0;
		rssize=0;

		try
		{
			logger.info("Entering sendReminder for # days");

			conn=Database.getConnection();

			selectquery=new StringBuffer("select emailaddr,icicitransid,description,amount,name,toid,from_unixtime(dtstamp) as date from transaction_icicicredit  where");
			selectquery.append(" status='proofrequired'");
			selectquery.append(" and (TO_DAYS(now()) - TO_DAYS(from_unixtime(dtstamp))) ="+ daysover +" order by toid asc");

			mailbody=new StringBuffer("We have not yet received any AUTHORISATION DETAILS for the below High Value Transaction after "+ daysover +" days.\r\n\r\n");
			mailbody.append("Please ask your customers to send the AUTHORISATION  DETAILS as soon as possible").append("\r\n");
			mailbody.append("Transaction will be cancelled after ").append(mindays).append(" days from the time of authsuccesful.\r\n\r\n");

			logger.info(selectquery);
			ResultSet rs=Database.executeQuery(selectquery.toString(),conn);
			//rssize=rs.getFetchSize();
			logger.info("rssize "+rssize);

			while(rs.next())
			{
				//rssize++;
				icicitransid=rs.getString("icicitransid");
				description=rs.getString("description");
				amount=rs.getString("amount");
				toid=rs.getInt("toid");
				date=rs.getString("date");
				name=rs.getString("name");
				emailaddr=rs.getString("emailaddr");

				count++;

						if((toid!=prevtoid && prevtoid!=0))
						{

							ResultSet rs1=Database.executeQuery("select contact_emails from members where memberid="+prevtoid,conn);
							String contact_emails=rs1.getString("contact_emails");

							logger.info("calling SendMAil for Merchant - ");
							logger.info("called SendMAil for Merchant-capture");

							//count=0;
							mailbody=new StringBuffer("We have not yet received any AUTHORISATION DETAILS for the below High Value Transaction after "+ daysover +" days.\r\n\r\n");
							mailbody.append("Please ask your customers to send the AUTHORISATION  DETAILS as soon as possible").append("\r\n");
							mailbody.append("Transaction will be cancelled after ").append(mindays).append(" days from the time of authsuccesful.\r\n\r\n");

							mailbody.append("Trackingid : ").append("\t").append(icicitransid).append("\t");
							mailbody.append("Amount : ").append("\t").append(amount).append("\t");
							mailbody.append("Description : ").append("\t").append(description).append("\t");
							mailbody.append("Card Holders Name: ").append("\t").append(name).append("\r\n");
							mailbody.append("Date of Transaction").append("\t").append(date).append("\r\n\r\n\r\n");
						}
						else
						{
							mailbody.append("Trackingid : ").append("\t").append(icicitransid).append("\r\n");
							mailbody.append("Amount : ").append("\t").append(amount).append("\r\n");
							mailbody.append("Description : ").append("\t").append(description).append("\r\n");
							mailbody.append("Card Holders Name: ").append("\t").append(name).append("\r\n");
							mailbody.append("Date of Transaction").append("\t").append(date).append("\r\n\r\n\r\n");
						}

				prevtoid=toid;
				logger.info("prevtoid = toid " + (prevtoid==toid));

			}//while ends


			if(count>=1)
			{

							ResultSet rs1=Database.executeQuery("select contact_emails from members where memberid="+prevtoid,conn);
							String contact_emails=rs1.getString("contact_emails");

							logger.info("calling SendMAil for Merchant - ");
							Mail.sendmail(contact_emails,"support@.com","Proof Required Reminder",mailbody.toString());
							logger.info("called SendMAil for Merchant-capture");

					logger.info("Entering sendReminder for # days");
			}
		}
		catch(Exception ex)
		{
			logger.info("Status Failed : "+ex.toString());
		}

	}

*/

    public void sendCustomerReminder(String mail, int mindays, int daysover)
    {
        toid = 0;
        prevtoid = 0;
        count = 0;
        rssize = 0;

        String brandname = "";
        String sitename = "";

        try
        {
            logger.debug("Entering sendReminder for # days");

            conn = Database.getConnection();

            selectquery = new StringBuffer("select B.last_four,T.expdate,orderdescription,T.emailaddr,T.icicitransid,T.description,T.amount,T.name,T.toid,from_unixtime(T.dtstamp) as date,M.company_name,M.brandname,M.sitename,M.currency from transaction_icicicredit as T, members as M, bin_details as B where ");
            selectquery.append(" T.status='proofrequired'");
            selectquery.append(" and (TO_DAYS(now()) - TO_DAYS(from_unixtime(T.dtstamp))) =? and T.toid=M.memberid and T.icicitransid = B.icicitransid order by toid asc");



            PreparedStatement p1=conn.prepareStatement(selectquery.toString());
            p1.setInt(1,daysover);
            ResultSet rs = p1.executeQuery();


            while (rs.next())
            {
                //rssize++;
                icicitransid = rs.getString("icicitransid");
                description = rs.getString("description");
                amount = rs.getString("amount");
                toid = rs.getInt("toid");
                date = rs.getString("date");
                name = rs.getString("name");
                emailaddr = rs.getString("emailaddr");

                company_name = rs.getString("company_name");
                brandname = rs.getString("brandname");
                sitename = rs.getString("sitename");
                currency = rs.getString("currency");

                if (brandname.trim().equals(""))
                    brandname = company_name;

                if (sitename.trim().equals(""))
                    sitename = company_name;


                Hashtable taghash = new Hashtable();
                taghash.put("NAME", name);
                taghash.put("CCNUM", rs.getString("lastfourofccnum"));
                taghash.put("EXPDATE", PzEncryptor.decryptExpiryDate(rs.getString("expdate")));
                taghash.put("COMPANYNAME", company_name);
                taghash.put("BRANDNAME", brandname);
                taghash.put("SITENAME", sitename);
                taghash.put("AMOUNT", amount);
                taghash.put("TRANSAMOUNT", amount);
                taghash.put("CAPAMOUNT", "0.00"); //transaction shouldn't be captured yet.
                taghash.put("DESCRIPTION", description);
                taghash.put("ORDERDESCRIPTION", rs.getString("orderdescription"));
                taghash.put("TRACKINGID", icicitransid);
                taghash.put("DATE", date);
                taghash.put("CURRENCY", currency);

                String CUSTOMERHRT = Functions.replaceTag(mail, taghash);
                taghash=null;
                //count=0;
                /*
                mailbody=new StringBuffer("We have not yet received any AUTHORISATION DETAILS for the below High Value Transaction after "+ daysover +" days.\r\n\r\n");
                mailbody.append("Please send the AUTHORISATION  DETAILS as soon as possible").append("\r\n");
                mailbody.append("if we do not receive the AUTHORISATION DETAILS within ").append(mindays).append(" days from the time of Transaction then the transaction will be CANCELLED.\r\n\r\n");

                mailbody.append("Trackingid : ").append("\t").append(icicitransid).append("\t");
                mailbody.append("Amount : ").append("\t").append(amount).append("\t");
                mailbody.append("Company Name: ").append("\t").append(company_name).append("\t");
                mailbody.append("Description : ").append("\t").append(description).append("\t");
                mailbody.append("Card Holders Name: ").append("\t").append(name).append("\r\n");
                mailbody.append("Date of Transaction").append("\t").append(date).append("\r\n\r\n");

                mailbody.append("To view the Authorising Letter format go to").append("\r\n");
                mailbody.append("http://order..com").append("\r\n");
                */

                logger.debug("calling SendMAil for Customer");

                if (emailaddr != null)
                    Mail.sendHtmlMail(emailaddr, "Do_Not_Reply@tc.com", null, null, "Documents Required for Order No - " + description, CUSTOMERHRT);

                logger.debug("called SendMAil for Customer");

                logger.debug("emailaddr " + emailaddr);
                CUSTOMERHRT=null;
            }//while ends

        }
        catch (Exception ex)
        {
            logger.error("Status Failed : ",ex);
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }


    public void cancelProofRequired(int mindays)
    {
        toid = 0;
        prevtoid = 0;
        count = 0;
        rssize = 0;
        //String adminEmail = ApplicationProperties.getProperty("COMPANY_ADMIN_EMAIL");
        //String fromAddress = ApplicationProperties.getProperty("COMPANY_FROM_ADDRESS");

        try
        {
            logger.debug("Entering setStatus");
            icicitransidsbuf = new StringBuffer();

            conn = Database.getConnection();

            selectquery = new StringBuffer("select icici.*,from_unixtime(icici.dtstamp) as date,M.currency as currency  from transaction_icicicredit icici,members M  where");
            selectquery.append(" status='proofrequired'");
            selectquery.append(" and (TO_DAYS(now()) - TO_DAYS(from_unixtime(icici.dtstamp))) >? and icici.toid=M.memberid order by toid asc");

            mailbody = new StringBuffer("We have not received appropriate authorisatoin for the below High Value\r\n");
            mailbody.append("Transaction. This Transaction has therefore been cancelled. Please do not\r\n");
            mailbody.append("ship this order and inform the customer of the same\r\n\r\n");



            PreparedStatement p1=conn.prepareStatement(selectquery.toString());
            p1.setInt(1,mindays);
            ResultSet rs = p1.executeQuery();
            //rssize=rs.getFetchSize();
            logger.info("rssize " + rssize);

            while (rs.next())
            {
                //rssize++;
                icicitransid = rs.getString("icicitransid");
                description = rs.getString("description");
                transactionOrderDescription = rs.getString("orderdescription");
                amount = rs.getString("amount");
                toid = rs.getInt("toid");
                date = rs.getString("date");
                name = rs.getString("name");
                customer_email = rs.getString("emailaddr");
                ipaddress = rs.getString("ipaddress");
                currency = rs.getString("currency");

                count++;

                if ((toid != prevtoid && prevtoid != 0))
                {
                    //			if(rssize==1)
                    //			{
                    //				icicitransidsbuf.append(icicitransid+",");
                    //				mailbody.append(icicitransid).append("\t").append(description).append("\t").append(amount);
                    //			}

                    icicitransidsbuf.deleteCharAt(icicitransidsbuf.length() - 1);
                    logger.debug("icicitransidsbuf " + icicitransidsbuf);
                    StringBuffer updatequery = new StringBuffer("update transaction_icicicredit set status='authcancelled' where");
                    updatequery.append(" icicitransid IN (?) and status='proofrequired'");
                    //updatequery.append(" status='authsuccess'");
                    //updatequery.append(" and (TO_DAYS(now()) - TO_DAYS(timestamp)) >" + mindays);


                    PreparedStatement p2=conn.prepareStatement(updatequery.toString());
                    p2.setString(1,icicitransidsbuf.toString());
                    logger.debug("Number of rows Affected " + p2.executeUpdate());
                    logger.debug("leaving setStatus");
                    String s1="select contact_emails from members where memberid=?";
                    PreparedStatement p3=conn.prepareStatement(s1);
                    p3.setInt(1,prevtoid);
                    ResultSet rs1 = p3.executeQuery();
                    String contact_emails = rs1.getString("contact_emails");

                    logger.debug("calling SendMAil for Merchant - ");


                    //Mail.sendmail(contact_emails, fromAddress, null, adminEmail, "Transaction Cancelled", mailbody.toString());
                    logger.debug("called SendMAil for Merchant-capture");

                    //count=0;
                    icicitransidsbuf = new StringBuffer();

                    mailbody = new StringBuffer("We have not received appropriate authorisatoin for the below High Value\r\n");
                    mailbody.append("Transaction. This Transaction has therefore been cancelled. Please do not\r\n");
                    mailbody.append("ship this order and inform the customer of the same\r\n\r\n");

                    mailbody.append("Description: " + description + "\r\n");
                    mailbody.append("Order Description: " + transactionOrderDescription + "\r\n");
                    mailbody.append("Amount: " + currency + " " + amount + "\r\n");
                    mailbody.append("Card Holder : " + name + "\r\n");
                    mailbody.append("Tracking id: " + icicitransid + "\r\n");
                    mailbody.append("Date of Transaction: " + date + "\r\n");
                    mailbody.append("Customer Email Address: " + customer_email + "\r\n");
                    mailbody.append("IP	Address: " + ipaddress + "\r\n\r\n\r\n");


                }
                else
                {
                    mailbody.append("Description: " + description + "\r\n");
                    mailbody.append("Order Description: " + transactionOrderDescription + "\r\n");
                    mailbody.append("Amount: " + currency + " " + amount + "\r\n");
                    mailbody.append("Card Holder : " + name + "\r\n");
                    mailbody.append("Tracking id: " + icicitransid + "\r\n");
                    mailbody.append("Date of Transaction: " + date + "\r\n");
                    mailbody.append("Customer Email Address: " + customer_email + "\r\n");
                    mailbody.append("IP	Address: " + ipaddress + "\r\n\r\n\r\n");

                }


                prevtoid = toid;
                icicitransidsbuf.append(icicitransid + ",");
                logger.debug("prevtoid = toid " + (prevtoid == toid));

            }//while ends

            if (count >= 1)
            {
                icicitransidsbuf.deleteCharAt(icicitransidsbuf.length() - 1);
                logger.debug("icicitransidsbuf " + icicitransidsbuf);
                StringBuffer updatequery = new StringBuffer("update transaction_icicicredit set status='authcancelled' where");
                updatequery.append(" icicitransid IN (?) and status='proofrequired'");
                //updatequery.append(" status='authsuccess'");
                //updatequery.append(" and (TO_DAYS(now()) - TO_DAYS(timestamp)) >" + mindays);



                PreparedStatement p4=conn.prepareStatement(updatequery.toString());
                p4.setString(1,icicitransidsbuf.toString());
                logger.debug("Number of rows Affected " + p4.executeUpdate());
                logger.debug("leaving setStatus");
                String s1="select contact_emails from members where memberid=?" ;
                PreparedStatement pa=conn.prepareStatement(s1);
                pa.setInt(1,prevtoid);
                ResultSet rs1 =pa.executeQuery();
                String contact_emails = rs1.getString("contact_emails");

                logger.debug("calling SendMAil for Merchant - ");
                //Mail.sendmail(contact_emails, fromAddress, null, adminEmail, "Transaction Cancelled", mailbody.toString());
                logger.debug("called SendMAil for Merchant-capture");

            }
        }
        catch (Exception ex)
        {
            logger.error("Status Failed : ",ex);
        }
        finally
        {
            Database.closeConnection(conn);
        }

    }


}