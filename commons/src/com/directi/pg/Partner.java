package com.directi.pg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 9/12/13
 * Time: 1:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class Partner
{
    static Logger logger = new Logger(Partner.class.getName());
    public static Hashtable hash = null;
    static final String ROLE="partner";
    public Hashtable getPartnerDetails(String partnerid)
    {
        Connection con=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        
        Hashtable partnerDetails=new Hashtable();
        if(partnerid!=null)
        {
            try
            {
                con=Database.getConnection();
                
                String qry1="SELECT companysupportmailid,company_name,companyadminid,supporturl,siteurl,contact_emails,address,telno,salesemail,billingemail,companyfromemail,supportfromemail,notifyemail,smtp_password,smtp_user,smtp_port,smtp_host,superadminid FROM partners WHERE partnerId=?";
                pstmt=con.prepareStatement(qry1);
                pstmt.setString(1,partnerid);
                ResultSet rs1=pstmt.executeQuery();
                if(rs1.next())
                {
                    partnerDetails.put("COMPANY_ADMIN_EMAIL",rs1.getString("companyadminid"));
                    partnerDetails.put("COMPANY_SUPPORT_URL",rs1.getString("supporturl"));
                    partnerDetails.put("COMPANY_LIVE_URL",rs1.getString("siteurl"));
                    partnerDetails.put("contact_emails",rs1.getString("contact_emails"));
                    partnerDetails.put("address",rs1.getString("address"));
                    partnerDetails.put("telno",rs1.getString("telno"));
                    partnerDetails.put("COMPANY",rs1.getString("company_name"));
                    partnerDetails.put("COMPANY_SALES_EMAIL",rs1.getString("salesemail"));
                    partnerDetails.put("COMPANY_BILLING_EMAIL",rs1.getString("billingemail"));
                    partnerDetails.put("COMPANY_FROM_ADDRESS",rs1.getString("companyfromemail"));
                    partnerDetails.put("SUPPORT_FROM_ADDRESS",rs1.getString("supportfromemail"));
                    partnerDetails.put("COMPANY_NOTIFY_EMAIL",rs1.getString("notifyemail"));

                    partnerDetails.put("SuperAdminID",rs1.getString("superadminid"));
                    partnerDetails.put("SMTP_HOST",rs1.getString("smtp_host"));
                    partnerDetails.put("SMTP_PORT",rs1.getString("smtp_port"));
                    partnerDetails.put("SMTP_AUTH_USER",rs1.getString("smtp_user"));
                    partnerDetails.put("SMTP_AUTH_PWD",rs1.getString("smtp_password"));
                }
            }
            catch (SystemError e)
            {
                logger.error("SQLException",e);
            }
            catch (SQLException e)
            {
                logger.error("SQLException",e);
            }
            finally
            {
                Database.closeConnection(con);
            }
        }
        else
        {
            logger.error("Memberid is not provided");
        }
        return partnerDetails;
    }

    public Hashtable getPartnerDetailsByHost(String host)
    {
        Connection con=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;

        Hashtable partnerDetails=new Hashtable();
        if(host!=null)
        {
            /*if(host.equals("integration.transactworld.com"))
            {
                partnerDetails.put("LOGO","transactworldlogo.png");
                partnerDetails.put("PARTNERID","34");
                partnerDetails.put("PARTNERNAME","transactworld");

            }
            else if (host.equals("integration.paypapa.com"))
            {
                partnerDetails.put("LOGO","paypapa1.png");
                partnerDetails.put("PARTNERID","2");
                partnerDetails.put("PARTNERNAME","paypapa");

            }
            else
            {
                partnerDetails.put("LOGO","Pay2.png");
                partnerDetails.put("PARTNERID","1");
                partnerDetails.put("PARTNERNAME","");

            }*/


            try
            {
                con=Database.getConnection();

                String qry1="SELECT partnerId,partnerName,logoName,iconName,default_theme,current_theme,ispcilogo,faviconName FROM partners WHERE hosturl=?";
                pstmt=con.prepareStatement(qry1);
                pstmt.setString(1,host);
                ResultSet rs1=pstmt.executeQuery();
                if(rs1.next())
                {
                    partnerDetails.put("PARTNERID",rs1.getString("partnerId"));
                    partnerDetails.put("PARTNERNAME",rs1.getString("partnerName"));
                    partnerDetails.put("LOGO",rs1.getString("logoName"));
                    partnerDetails.put("ICON",rs1.getString("iconName"));
                    partnerDetails.put("DEFAULTTHEME",rs1.getString("default_theme"));
                    partnerDetails.put("CURRENTTHEME",rs1.getString("current_theme"));
                    partnerDetails.put("ispcilogo",rs1.getString("ispcilogo"));
                    partnerDetails.put("FAVICON",rs1.getString("faviconName"));

                }
            }
            catch (SystemError e)
            {
                logger.error("SQLException",e);
            }
            catch (SQLException e)
            {
                logger.error("SQLException",e);
            }
            finally
            {
                Database.closeConnection(con);
            }
        }
        else
        {
            logger.error("Memberid is not provided");
        }
        return partnerDetails;
    }


    public static void DeleteBoth(String login) throws SystemError
    {
        Connection con=null;
        try
        {
            con=Database.getConnection();
            String query=" delete from `user` where login=? and roles=?";
            PreparedStatement ps=con.prepareStatement(query.toString());
            ps.setString(1,login);
            ps.setString(2,ROLE);
            String dquery=" delete from `partners` where login=?";
            PreparedStatement ps1=con.prepareStatement(dquery.toString());
            ps1.setString(1,login);
        }
        catch (SystemError systemError)
        {
            /*systemError.printStackTrace(); */ //To change body of catch statement use File | Settings | File Templates.
            logger.error(" error ::", systemError);
            throw new SystemError("Error:"+systemError.getMessage());
        }
        catch (SQLException e)
        {
            /*e.printStackTrace();*/  //To change body of catch statement use File | Settings | File Templates.
            logger.error(" ERROR", e);
            throw new SystemError("Error:"+e.getMessage());
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}
