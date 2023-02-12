package com.directi.pg;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.*;

public class CaptureReminderToMerchant
{

    static Logger logger = new Logger(CaptureReminderToMerchant.class.getName());

    public static void main(String[] arg)
    {
        CaptureReminderToMerchant cr = new CaptureReminderToMerchant();
        cr.sendReminder(3);
    }

    public void sendReminder(int mindays)
    {

        Connection conn = null;
        StringBuffer selectquery = null;
        String amount = null;
        String icicitransid = null;
        String authid = null;
        String toid=null;
        String rsdescription= null;
        String cardholdername =null;
        int count = 0;

        //Addded for pending captures as per merchant
         HashMap<String,Collection> membersMap = new HashMap<String,Collection>();

         Collection<Hashtable> listOfpendingCapture = null;

         Hashtable transDetails = null;


        try
        {
           logger.debug("Entering sendReminder ");

            count = 0;

            conn = Database.getConnection();

            selectquery = new StringBuffer("select name,description,toid,icicitransid,authid,captureamount,icicimerchantid from transaction_icicicredit  where");
            selectquery.append(" status='capturestarted'");
            selectquery.append(" and (TO_DAYS(now()) - TO_DAYS(timestamp)) >=?" );
            selectquery.append(" order by timestamp asc");


            PreparedStatement pstmt=conn.prepareStatement(selectquery.toString());
            pstmt.setInt(1,mindays);
            ResultSet rs = pstmt.executeQuery();


            while (rs.next())
            {

                        icicitransid = rs.getString("icicitransid");
                        authid = rs.getString("authid");
                        amount = rs.getString("captureamount");
                        toid = rs.getString("toid");
                        rsdescription = rs.getString("description");
                        cardholdername =  rs.getString("name");

                        // preparing collections of pending captures as per merchant
                        transDetails = new Hashtable();
                        transDetails.put("icicitransid",icicitransid);
                        transDetails.put("authid",authid);
                        transDetails.put("captureamount",amount);
                        transDetails.put("description",rsdescription);
                        transDetails.put("cardholdername",cardholdername);


                        if(membersMap.get(toid)==null)
                        {

                            listOfpendingCapture = new ArrayList<Hashtable>();
                            listOfpendingCapture.add(transDetails);
                            membersMap.put(toid,listOfpendingCapture);


                        }
                        else
                        {
                             listOfpendingCapture = membersMap.get(toid);
                             listOfpendingCapture.add(transDetails);
                             membersMap.put(toid,listOfpendingCapture);
                        }



            }//while ends

        // Sending reminder mail to merchants for pending captures
        Set members = membersMap.keySet();

        for(Object memberid : members )
        {  /*
            try
            {
            Transaction.sendCaptureReminderMail((ArrayList)membersMap.get(memberid),(String)memberid);
            }
            catch(SystemError se)
            {
               logger.error("Error while sending capture reminder mail:",se);
            }  */
            logger.info("called SendMAil");
        }



        }
        catch (Exception ex)
        {   logger.error("Status Failed :",ex);
            //System.out.println("Status Failed : " + ex.toString());
        }

    }





}
