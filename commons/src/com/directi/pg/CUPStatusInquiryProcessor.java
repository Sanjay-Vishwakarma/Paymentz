package com.directi.pg;

import com.payment.request.PZInquiryRequest;
import com.payment.AbstractPaymentProcess;
import com.payment.response.PZInquiryResponse;
import com.payment.cup.core.CupPaymentProcess;

import java.util.Hashtable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: a135654
 * Date: Apr 13, 2013
 * Time: 4:09:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class CUPStatusInquiryProcessor
{
    public void processCUPStatusInquiry(Hashtable ht)
    {
        //TODO:Get all AuthStarted Transactions
        //TODO:Inquire the status at CUP end
        //TODO:Update in our System.
        Connection con=null;
        ResultSet rs = null;
        PreparedStatement pS=null;
        PZInquiryRequest pzIR =null;
        PZInquiryResponse pzIRsp =null;


        try
        {
            con = Database.getConnection();
            String q1= "select trackingid,accountid,toid,fromtype from transaction_common where status='authstarted' and fromtype='CUP'";
            pS=con.prepareStatement(q1);
            rs = pS.executeQuery();
            while(rs.next())
            {
                pzIR = new PZInquiryRequest();
                pzIR.setTrackingId(rs.getInt("trackingid"));
                pzIR.setAccountId(rs.getInt("accountid"));
                pzIR.setMemberId(rs.getInt("toid"));
                AbstractPaymentProcess app = new CupPaymentProcess();
                pzIRsp = app.inquiry(pzIR);
            }
        }
        catch(SystemError se)
        {

        }
        catch(SQLException sse)
        {

        }
    }
}
