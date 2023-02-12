package com.directi.pg;

import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.opus.epg.sfa.java.Merchant;
import com.opus.epg.sfa.java.PGResponse;
import com.opus.epg.sfa.java.PGSearchResponse;
import com.opus.epg.sfa.java.PostLib;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class AutoCapture
{

    static Logger logger = new Logger(AutoCapture.class.getName());

    public static void main(String[] arg)
    {
        try
        {
            Merchants.refresh();
        }
        catch (SystemError systemError)
        {
            logger.error("Error in Main" , systemError);
        }
        capture();
    }

    public static void capture()
    {
        Merchants merchants = new Merchants();

        StringBuffer mailbody = new StringBuffer();
        mailbody.append("<html><head><style>\r\n.tr1{background:#FaEEE0;text-valign: center;text-align: LEFT;font-size:11px;font-family:arial,verdana,helvetica}\r\n\r\n.heading{background:#800000;text-valign: center;text-align: LEFT;font-size:12px;color:#ffffff;font-family:arial,verdana,helvetica}\r\n.tr2{background:#fffeec;text-valign: center;text-align: LEFT;font-size:11px;font-family:arial,verdana,helvetica}\r\n</style></head><body text=\"#800000\">");
        mailbody.append("<table><tr><td>");

        StringBuffer successmailbody = new StringBuffer("<br>Following Transaction have been Captured Successfully.<br><br>");
        successmailbody.append("<table border=1>");
        successmailbody.append("<tr  class=\"heading\">");
        successmailbody.append("<td>Trackingid</td>");
        successmailbody.append("<td>Description</td>");
        successmailbody.append("<td>Amount</td>");
        successmailbody.append("</tr>");

        Connection conn = null;
        Hashtable merchantHash = new Hashtable();
        StringBuffer merchantMailBody = null;
        boolean updateSuccess = false;

        try
        {
            logger.debug("Entering capture()");

            conn = Database.getConnection();

            //select all transactions who are on capturestarted since last 10 minutes
            StringBuffer selectquery = new StringBuffer("select description,toid,icicitransid,authid,authcode,authreceiptno,captureamount,icicimerchantid,accountid from transaction_icicicredit  where");
            selectquery.append(" status='capturestarted'");
            selectquery.append(" and unix_timestamp(now()) - unix_timestamp(timestamp) >600");
            selectquery.append(" order by timestamp asc");


            ResultSet rs = Database.executeQuery(selectquery.toString(), conn);

            while (rs.next())
            {
                //rssize++;
                String icicitransid = rs.getString("icicitransid");
                String authid = rs.getString("authid");
                String authcode = rs.getString("authcode");
                String authRRN = rs.getString("authreceiptno");
                String captureamount = rs.getString("captureamount");
                String icicimerchantid = rs.getString("icicimerchantid");
                String toid = rs.getString("toid");
                String description = rs.getString("description");
                String accountId = rs.getString("accountid");

                logger.debug("Captureing for merchantid=" + toid + " for icicitransid=" + icicitransid);

                if (merchantHash.get(toid) == null)
                {
                    merchantMailBody = new StringBuffer();
                    merchantMailBody.append(mailbody.toString());
                    merchantMailBody.append("<b>Member ID:</b> " + toid + "<br>");

                    try
                    {
                        String companyname = merchants.getCompany(toid);
                        merchantMailBody.append("<b>Company Name:</b> " + companyname);
                    }
                    catch (Exception e)
                    {
                        logger.error("Company name not found for memberid ",e);
                    }

                    merchantMailBody.append("</td></tr>");
                    merchantMailBody.append("<tr><td>&nbsp;</td></tr></table>");
                    merchantMailBody.append(successmailbody.toString());
                }
                else
                    merchantMailBody = (StringBuffer) merchantHash.get(toid);

                //retrieve data from bank if it is captured there else capture it

                Merchant oMerchant = new Merchant();
                oMerchant.setMerchantOnlineInquiry(icicimerchantid, icicitransid);
                PostLib oPostLib = new PostLib();

                PGSearchResponse oPgSearchResp = oPostLib.postStatusInquiry(oMerchant);
                ArrayList oPgRespArr = oPgSearchResp.getPGResponseObjects();
                if (oPgRespArr != null)
                {
                    logger.debug("Got array in response so traverse thro' history with size ");
                    if (oPgRespArr.size() >= 2)
                    {
                        for (int i = 1; i < oPgRespArr.size(); i++) //First row will be always for preAuthorisation
                        {
                            logger.info("Transaction was already attempted for capture so get details");
                            PGResponse oPgResp = (PGResponse) oPgRespArr.get(i);
                            String transType = oPgResp.getTxnType();
                            //if transaction already captured then retrieve details
                            logger.debug("transaction type=" + transType + " Response code=" + oPgResp.getRespCode());

                            if (Functions.parseData(transType) != null && transType.equals("Auth") && (oPgResp.getRespCode()).equals("0"))
                            {
                                logger.debug("Transaction details got");
                                String captureid = oPgResp.getEpgTxnId();
                                String capturecode = oPgResp.getAuthIdCode();
                                String captureRRN = oPgResp.getRRN();
                                String query = "update transaction_icicicredit set captureid=?,capturecode=?,capturereceiptno=?,status='capturesuccess',captureresult='Capture Done using Cron.' where icicitransid=? and status='capturestarted'";

                                PreparedStatement pstmt=conn.prepareStatement(query);
                                pstmt.setString(1,captureid);
                                pstmt.setString(2,capturecode);
                                pstmt.setString(3,captureRRN);
                                pstmt.setString(4,icicitransid);
                                int num = pstmt.executeUpdate();
                                logger.debug("Number of tranactions occured " + num);
                                if (num == 1)
                                {
                                    updateSuccess = true;
                                    // Start : Added for Action and Status Entry in Action History table

                                    ActionEntry entry = new ActionEntry();
                                    int actionEntry = entry.actionEntry(icicitransid,"",ActionEntry.ACTION_CAPTURE_SUCCESSFUL,ActionEntry.STATUS_CAPTURE_SUCCESSFUL);
                                    entry.closeConnection();

                                    // End : Added for Action and Status Entry in Action History table

                                }

                                break;
                            }
                        }

                        if (!updateSuccess)
                        {
                            logger.debug("Capture attempt failed so capturing again....");
                            //ICICIPaymentGateway icici = new ICICIPaymentGateway(accountId);
                            AbstractPaymentGateway paymentGateway = AbstractPaymentGateway.getGateway(accountId);
                            Hashtable hash = null;

                            hash = paymentGateway.processCapture(icicitransid, captureamount, authid, authcode, authRRN);

                            if (hash != null && ((String) hash.get("captureqsiresponsecode")).equals("0"))
                            {
                                logger.debug("Transaction captured successfully for trackingid--");
                                String captureid = (String) hash.get("captureid");
                                String capturecode = (String) hash.get("capturecode");
                                String captureRRN = (String) hash.get("capturereceiptno");

                                String query = "update transaction_icicicredit set captureid=?,capturecode=?,capturereceiptno=?,status='capturesuccess',captureresult='Capture Done using Cron.' where icicitransid=? and status='capturestarted'";

                                PreparedStatement pstmt=conn.prepareStatement(query);
                                pstmt.setString(1,captureid);
                                pstmt.setString(2,capturecode);
                                pstmt.setString(3,captureRRN);
                                pstmt.setString(4,icicitransid);
                                int num = pstmt.executeUpdate();
                                logger.debug("Number of tranactions occured " + num);
                                if (num == 1)
                                {
                                    updateSuccess = true;

                                    // Start : Added for Action and Status Entry in Action History table

                                    ActionEntry entry = new ActionEntry();
                                    int actionEntry = entry.actionEntry(icicitransid,"",ActionEntry.ACTION_CAPTURE_SUCCESSFUL,ActionEntry.STATUS_CAPTURE_SUCCESSFUL);
                                    entry.closeConnection();

                                    // End : Added for Action and Status Entry in Action History table
                                }
                            }

                        }

                    }
                    else
                    {
                        logger.debug("Transaction was not captured");
                        PGResponse oPgResp = (PGResponse) oPgRespArr.get(0);
                        String transType = oPgResp.getTxnType();
                        if (Functions.parseData(transType) != null && transType.equals("PreAuth") && (oPgResp.getRespCode()).equals("0"))
                        {
                            //preauth was successful so capture it
                            logger.debug("Authorisation was successful so capturing ......");
                            AbstractPaymentGateway paymentGateway = AbstractPaymentGateway.getGateway(accountId);
                            //ICICIPaymentGateway icici = new ICICIPaymentGateway(toid);
                            Hashtable hash = null;

                            hash = paymentGateway.processCapture(icicitransid, captureamount, authid, authcode, authRRN);

                            if (hash != null && ((String) hash.get("captureqsiresponsecode")).equals("0"))
                            {
                                logger.debug("Transaction captured successfully for trackingid--" + icicitransid);
                                String captureid = (String) hash.get("captureid");
                                String capturecode = (String) hash.get("capturecode");
                                String captureRRN = (String) hash.get("capturereceiptno");

                                String query = "update transaction_icicicredit set captureid=?,capturecode=?,capturereceiptno=?,status='capturesuccess',captureresult='Capture Done using Cron.' where icicitransid=? and status='capturestarted'";

                                PreparedStatement pstmt=conn.prepareStatement(query);
                                pstmt.setString(1,captureid);
                                pstmt.setString(2,capturecode);
                                pstmt.setString(3,captureRRN);
                                pstmt.setString(4,icicitransid);
                                int num = pstmt.executeUpdate();
                                logger.debug("Number of tranactions occured " + num);
                                if (num == 1)
                                {
                                    updateSuccess = true;

                                    // Start : Added for Action and Status Entry in Action History table

                                    ActionEntry entry = new ActionEntry();
                                    int actionEntry = entry.actionEntry(icicitransid,"",ActionEntry.ACTION_CAPTURE_SUCCESSFUL,ActionEntry.STATUS_CAPTURE_SUCCESSFUL);
                                    entry.closeConnection();

                                    // End : Added for Action and Status Entry in Action History table
                                }
                            }
                        }

                        //  logger.info("Not attempting for capture because of problem at ICICI");
                    }
                    if (updateSuccess)
                    {
                        merchantMailBody.append("<tr class=\"tr1\">");
                        merchantMailBody.append("<td>&nbsp;" + icicitransid + "</td>");
                        merchantMailBody.append("<td>&nbsp;" + description + "</td>");
                        merchantMailBody.append("<td>&nbsp;" + captureamount + "</td>");
                        merchantMailBody.append("</tr>");
                        merchantHash.put(toid, merchantMailBody);
                        updateSuccess = false;

                    }

                }
                logger.debug("Sleeping for 10 sec.");
                Thread.sleep(10000); //sleep for 5 seconds as ICICI asking for delay
            }//while ends

            Enumeration merchantEnum = merchantHash.keys();

            while (merchantEnum.hasMoreElements())
            {
                String memberid = (String) merchantEnum.nextElement();
                StringBuffer mailBody = (StringBuffer) merchantHash.get(memberid);
                mailBody.append("</table><br><br>");
                String query= "select contact_emails from members where memberid=?";
                PreparedStatement pstmt=conn.prepareStatement(query);
                pstmt.setString(1,memberid);
                ResultSet rs1 = pstmt.executeQuery();
                if (rs1.next())
                {
                    String contact_emails = rs1.getString("contact_emails");
                    logger.debug("calling SendMAil for Merchant -capture ");
                    logger.debug("called SendMAil for Merchant-capture");
                }

            }
        }
        catch (Exception ex)
        {
            logger.error("Status Failed : ",ex);
        }
        finally {
            Database.closeConnection(conn);
        }

        logger.debug("Leaving capture");

    }
}
