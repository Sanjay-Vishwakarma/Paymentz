package com.directi.pg;

import com.logicboxes.util.ApplicationProperties;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.ResourceBundle;

public class ChangeReserve
{
    static Logger logger = new Logger(ChangeReserve.class.getName());
    static String mailBody = "";
    static int memberIds[];
    static int reserves[];
    static String companynames[];
    static int size;
    static String deposits[][];
    static String withdrawals[][];
    static Connection cn;

    public ChangeReserve()
    {
        logger.debug("Entering Constuctor");
    }

    public static void main(String ar[])
    {
        checkAndUpdateMembers();
    }

    static void getTemplate()
    {
        logger.debug("Entering getTemplate");
        StringBuffer bufMailBody = new StringBuffer();
        ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.template");
        String path = RB.getString("PATH");
        try
        {
            FileInputStream fin = new FileInputStream(path + "NegativeBalanceMail.template");
            BufferedReader br = new BufferedReader(new InputStreamReader(fin));
            String temp = null;

            while ((temp = br.readLine()) != null)
                bufMailBody.append(temp + "\r\n");

            fin.close();
        }
        catch (Exception e)
        {
            logger.error("getTemplate() " ,e);
        }
        mailBody = bufMailBody.toString();
        logger.debug("Leaving getTemplate ");
    }

    public static void checkAndUpdateMembers()
    {
        getConnection();
        getTemplate();
        getMembers();
        getBalance();

        String strSql = "update members set reserves=?,reserve_reason=? where memberid=?";
        PreparedStatement ps = null;
        try
        {
            ps = cn.prepareStatement(strSql);
        }
        catch (Exception e)
        {
            logger.error("checkAndUpdateMembers() ",e);
        }
        Hashtable mailHash = new Hashtable();
        for (int i = 0; i < size; i++)
        {
            float balance = 0, reserve = 0;
            int memberid = memberIds[i];
            BigDecimal depositedamount = null, withdrawedamount = null;
            try
            {
                String tmp1 = getDepositWithdrawal(memberid, deposits);
                String tmp2 = getDepositWithdrawal(memberid, withdrawals);
                if (!tmp1.equals(""))
                    depositedamount = new BigDecimal(tmp1);
                else
                    depositedamount = new BigDecimal("0");
                if (!tmp2.equals(""))
                    withdrawedamount = new BigDecimal(tmp2);
                else
                    withdrawedamount = new BigDecimal("0");
                balance = (depositedamount.subtract(withdrawedamount)).floatValue();
                reserve = balance * -100;
            }
            catch (Exception e)
            {
                logger.error("Exception occure in chargeReserve ",e);
                return;
            }
            if ((int) reserve > reserves[i])
            {
                mailHash.put("MERCHANTID", "" + memberid);
                mailHash.put("COMPANYNAME", getCompanyName(memberid));
                mailHash.put("BALANCE", "" + balance);
//                mailHash.put("RESERVES",""+(reserves[i]/100));
                mailHash.put("NEWRESERVES", "" + (reserve / 100));
                String mailBody = Functions.replaceTag(ChangeReserve.mailBody, mailHash);
                try
                {
                    ps.setFloat(1, reserve);
                    ps.setString(2, "Reserve set by Cron due to -ve balance");
                    ps.setInt(3, memberid);
                    if (ps.executeUpdate() > 0)
                    {
                        String adminEmail = ApplicationProperties.getProperty("COMPANY_ADMIN_EMAIL");
                        //String fromAddress = ApplicationProperties.getProperty("COMPANY_FROM_ADDRESS");
                        logger.debug("Reserve for memberid " + memberid + " set to " + reserve);
                    }
                    else
                        logger.debug("Error in setting reserve for memberid " + memberid);
                }
                catch (Exception e)
                {
                    logger.error("checkAndUpdateMembers() ", e);
                    return;
                }
            }
        }
        try
        {
            ps.close();
            ps = null;
        }
        catch (Exception e)
        {
            logger.error("checkAndUpdateMembers() " , e);
        }
        closeConnection();
    }

    static String getNotifyEmailAddress(int memberid)
    {
        logger.debug("Entering getNotifyEmailAddress");
        String email = "";
        String strSql = "select notifyemail from members where memberid=?";
        try
        {   PreparedStatement pstmt=cn.prepareStatement(strSql);
            pstmt.setInt(1,memberid);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                email = rs.getString("notifyemail");
            }
            rs.close();
        }
        catch (Exception e)
        {
            logger.error("getNotifyEmailAddress() ", e);
        }
        logger.debug("Leaving getNotifyEmailAddress ");
        return email;
    }

    static void getBalance()
    {
        logger.debug("Entering getBalance");
        try
        {
            String sql1 = "select count(distinct toid) as total from transactions where totype = 'tc'";
            ResultSet rs1 = Database.executeQuery(sql1, cn);
            rs1.next();
            int depositSize = rs1.getInt("total");
            deposits = new String[depositSize][2];
            rs1.close();
            sql1 = "select sum(amount) as amount, toid from transactions where totype = 'tc' group by toid";
            rs1 = Database.executeQuery(sql1, cn);
            for (int i = 0; rs1.next(); i++)
            {
                deposits[i][0] = rs1.getString("toid");
                String tempamt = rs1.getString("amount");
                if (tempamt != null)
                    deposits[i][1] = new BigDecimal(tempamt).toString();
                else
                    deposits[i][1] = "0";
            }
            rs1.close();
            String sql2 = "select count(distinct fromid) as total from transactions where fromtype = 'tc'";
            ResultSet rs2 = Database.executeQuery(sql2, cn);
            rs2.next();
            int withdrawalSize = rs2.getInt("total");
            withdrawals = new String[withdrawalSize][2];
            rs2.close();
            sql2 = "select sum(amount) as amount, fromid from transactions where fromtype = 'tc' group by fromid";
            rs2 = Database.executeQuery(sql2, cn);
            for (int i = 0; rs2.next(); i++)
            {
                withdrawals[i][0] = rs2.getString("fromid");
                String tempamt = rs2.getString("amount");
                if (tempamt != null)
                    withdrawals[i][1] = new BigDecimal(tempamt).toString();
                else
                    withdrawals[i][1] = "0";
            }
            rs2.close();
            logger.debug("leaving getBalance");
        }
        catch (Exception e)
        {
            logger.error("getBalance() ",e);
        }
    }

    static String getDepositWithdrawal(int merchantId, String arr[][])
    {
        String retvalue = "";
        String mId = "" + merchantId;
        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i][0].equals(mId))
            {
                retvalue = arr[i][1];
                break;
            }
        }
        return retvalue;
    }

    static String getCompanyName(int merchantId)
    {
        String retvalue = "";
        for (int i = 0; i < memberIds.length; i++)
        {
            if (merchantId == memberIds[i])
            {
                retvalue = companynames[i];
                break;
            }
        }
        return retvalue;
    }

    static void getConnection()
    {
        try
        {
            cn = Database.getConnection();
        }
        catch (Exception e)
        {
            logger.error("getConnection() ",e);
        }
    }

    static void closeConnection()
    {
        try
        {
            Database.closeConnection(cn);
        }
        catch (Exception e)
        {
            logger.error("closeConnection() ",e);
        }
    }

    static void getMembers()
    {
        logger.debug("Entering getMembers");
        String strSql = "select count(*) from members where memberid <> 222 and (activation='Y' or activation='N')";
        try
        {
            ResultSet rs = Database.executeQuery(strSql, cn);
            if (rs.next())
            size = rs.getInt(1);
            rs.close();
            memberIds = new int[size];
            reserves = new int[size];
            companynames = new String[size];
            strSql = "select memberid,reserves,company_name from members where memberid <> 222 and (activation='Y' or activation='N')";
            rs = Database.executeQuery(strSql, cn);
            for (int i = 0; rs.next(); ++i)
            {
                memberIds[i] = rs.getInt("memberid");
                reserves[i] = rs.getInt("reserves");
                companynames[i] = rs.getString("company_name");
            }
            rs.close();
        }
        catch (Exception e)
        {
            logger.error("getMembers() Exception ",e);
        }
        logger.debug("Leaving getMembers");
    }

}

