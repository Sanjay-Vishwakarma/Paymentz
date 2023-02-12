package com.transaction.utils;

import com.directi.pg.Database;
import com.directi.pg.SystemError;
import com.payment.response.PZShortResponseStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Jinesh on 10/30/2015.
 */
public class TransactionWSUtils
{
    public String getAccountFromTerminal(String terminanlId)
    {
        String accountId = "";
        Connection con = null;
        try
        {
            con = Database.getConnection();
            String sql = "SELECT accountid,memberid FROM `member_account_mapping` WHERE terminalid=?";
            PreparedStatement p1 = con.prepareStatement(sql);
            p1.setString(1, terminanlId);
            ResultSet rs = p1.executeQuery();

            if (rs.next())
            {
                accountId = rs.getString("accountid");
            }
        }
        catch (SystemError se)
        {

        }
        catch (SQLException e)
        {

        }
        return accountId;
    }

    public String getAccountIdfromMemberId(String toid, int paymenttype, int cardtype)
    {
        String accountId="0";
        Connection con = null;

        try
        {
            con=Database.getConnection();
            String query2 = "select accountid,isActive,max_transaction_amount,min_transaction_amount,terminalid from member_account_mapping where memberid=? and paymodeid=? and cardtypeid=? and isActive='Y'";
            PreparedStatement pstmt2 = con.prepareStatement(query2);
            pstmt2.setString(1, toid);
            pstmt2.setInt(2, paymenttype);
            pstmt2.setInt(3, cardtype);
            ResultSet rs2 = pstmt2.executeQuery();
            if (rs2.next())
            {
                accountId = rs2.getString("accountid");
            }
        }
        catch (Exception e)
        {

        }
        finally
        {
            Database.closeConnection(con);
        }

        return accountId;
    }

    public String getFinalStatus(String multiStatus)
    {
        Map sMap = new HashMap();
        String rStringBuffer ="success,success,error,error,success";
        String finalStatus = "";

        String []splitStatus = multiStatus.split(",");
        int sCount = 0;
        int eCount = 0;
        int fCount = 0;

        int iSucessStaus = 0;
        int iFailedStaus = 0;
        int iErrorStaus = 0;


        for(int i=0;i<splitStatus.length;i++)
        {
            //System.out.println("if---" + splitStatus[i]);
            if(splitStatus[i].equalsIgnoreCase(PZShortResponseStatus.SUCCESS.toString()))
                sMap.put("success",++sCount);
            else if(splitStatus[i].equalsIgnoreCase(PZShortResponseStatus.FAILED.toString()))
                sMap.put("fail",++fCount);
            else if(splitStatus[i].equalsIgnoreCase(PZShortResponseStatus.ERROR.toString()))
                sMap.put("error",++eCount);
        }

        if(sMap.get("success")!=null)
        {
            iSucessStaus = Integer.parseInt(sMap.get("success").toString());
        }

        if(sMap.get("fail")!=null)
        {
            iFailedStaus = Integer.parseInt(sMap.get("fail").toString());
        }

        if(sMap.get("error")!=null)
        {
            iErrorStaus = Integer.parseInt(sMap.get("error").toString());
        }

        //System.out.println("buffer---" + iSucessStaus + "--" + iFailedStaus + "--" + iErrorStaus);

        if(iSucessStaus>0 && iFailedStaus==0 && iErrorStaus==0)
        {
            finalStatus = PZShortResponseStatus.SUCCESS.toString();
        }
        else if((iSucessStaus>0 && iFailedStaus>0 && iErrorStaus==0) ||(iSucessStaus>0 && iFailedStaus==0 && iErrorStaus>0) || (iSucessStaus>0 && iFailedStaus>0 && iErrorStaus>0))
        {
            finalStatus = PZShortResponseStatus.PARTIAL_SUCCESS.toString();
        }
        else if((iSucessStaus==0 && iFailedStaus>0))
        {
            finalStatus = PZShortResponseStatus.FAILED.toString();
        }
        else if((iSucessStaus==0 && iFailedStaus==0 && iErrorStaus>0))
        {
            finalStatus = PZShortResponseStatus.ERROR.toString();
        }
        return finalStatus;
    }

    public static void main(String[] args)
    {
        TransactionWSUtils t = new TransactionWSUtils();
        //System.out.println(t.getAccountIdfromMemberId("10390",1,1));
    }
}
