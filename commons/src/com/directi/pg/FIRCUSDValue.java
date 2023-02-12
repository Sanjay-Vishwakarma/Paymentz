/**
 * Created by IntelliJ IDEA.
 * User: divyesh.p
 * Date: Feb 27, 2004
 * Time: 04:43:56 PM
 */
package com.directi.pg;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class FIRCUSDValue
{
    static Logger logger = new Logger(FIRCUSDValue.class.getName());

    public static void main(String[] arg)
    {
        if (arg == null || arg.length <= 1)
        {
            logger.debug(" Please provide file path and merchant id");
            System.exit(0);
        }
        FIRCUSDValue fval = new FIRCUSDValue();
        fval.insertUSD(arg[0], arg[1]);
    }

    public void insertUSD(String filePath, String iciciMerchantId)
    {
        StringBuffer dataBuf = new StringBuffer();
        PreparedStatement pselect = null;
        PreparedStatement pupdate = null;
        ResultSet rs = null;
        BufferedWriter wr = null;
        try
        {
            wr = new BufferedWriter(new FileWriter("/opt/jakarta-tomcat-4.1.29/common/classes/com/directi/pg/UPDQUERY.txt"));
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String temp = null;

            while ((temp = br.readLine()) != null)
            {
                dataBuf.append(temp + "\r\n");
            }
            br.close();
        }
        catch (IOException e)
        {
            logger.error(" Exception occured while opening or reading file ", e);
            logger.error(" Exception occured  while opening or reading file ", e);
            return;
        }

        StringTokenizer stz = new StringTokenizer(dataBuf.toString(), "\r\n");
        try
        {
            Connection conn = Database.getConnection();
            pselect = conn.prepareStatement("select transid from transaction_icicicredit where (captureamount=? or refundamount=?) and (captureid=? or refundid=?) and icicimerchantid=?");
            pupdate = conn.prepareStatement("update icici_firc set usdval=?,convrate=? where transid=? and amount=?");
            double absTxnAmount = 0, txnAmount = 0, conversionRate = 0, usdAmount = 0;
            long finno = 0;
            int i = 1;
            ArrayList ar = new ArrayList(50000);
            int cnt = 0;
            String strSql = "select captureamount, refundamount, captureid, refundid, transid from transaction_icicicredit where icicimerchantid=? and transid <> 0";
            PreparedStatement ps=conn.prepareStatement(strSql);
            ps.setString(1,iciciMerchantId);
            ResultSet divRS = ps.executeQuery();

            while (divRS.next())
            {
                ar.add(divRS.getString("captureamount") + "");
                ar.add(divRS.getString("refundamount") + "");
                ar.add(divRS.getLong("captureid") + "");
                ar.add(divRS.getLong("refundid") + "");
                ar.add(divRS.getLong("transid") + "");
                cnt++;
            }

            while (stz.hasMoreElements())
            {
                String value = ((String) stz.nextElement()).trim();
                if (value != null && value.trim() != "")
                {
                    StringTokenizer innerStz = new StringTokenizer(value, "^");
                    absTxnAmount = 0;
                    txnAmount = 0;
                    conversionRate = 0;
                    usdAmount = 0;
                    finno = 0;
                    if (innerStz.hasMoreElements())
                    {
                        txnAmount = Double.parseDouble(((String) innerStz.nextElement()).trim());
                        if (txnAmount < 0)
                            absTxnAmount = -txnAmount;
                        else
                            absTxnAmount = txnAmount;
                        finno = Long.parseLong(((String) innerStz.nextElement()).trim());
                        conversionRate = Double.parseDouble(((String) innerStz.nextElement()).trim());
                        usdAmount = Double.parseDouble(((String) innerStz.nextElement()).trim());

/*old                        pselect.setDouble(1,absTxnAmount);
                        pselect.setDouble(2,absTxnAmount);
                        pselect.setLong(3,finno);
                        pselect.setLong(4,finno);
                        pselect.setString(5,iciciMerchantId);
                        rs = pselect.executeQuery();                      
//System.out.println("  select transid from transaction_icicicredit where (captureamount="+absTxnAmount+" or refundamount="+absTxnAmount+") and (captureid="+finno+" or refundid="+finno+") and icicimerchantid="+iciciMerchantId);                        
                        if(rs.next()){
*/
                        boolean flag = false;
                        int j = 0;
                        double inTxnAmount = 0, inTxnAmount1 = 0;
                        for (; j < cnt; j++)
                        {
                            try
                            {
                                inTxnAmount = Double.parseDouble(ar.get(j * 5) + "");
                                inTxnAmount1 = Double.parseDouble(ar.get((j * 5) + 1) + "");
                            }
                            catch (Exception e)
                            {
                            }
                            if ((absTxnAmount == inTxnAmount || absTxnAmount == inTxnAmount1) &&
                                    (ar.get((j * 5) + 2).equals(finno + "") || ar.get((j * 5) + 3).equals(finno + "")))
                            {
                                flag = true;
                                break;
                            }
                        }
                        if (flag)
                        {
                            pupdate.setDouble(1, usdAmount);
                            pupdate.setDouble(2, conversionRate);
//                            pupdate.setInt(3, rs.getInt("transid"));
                            pupdate.setInt(3, Integer.parseInt("" + ar.get((j * 5) + 4)));
                            pupdate.setDouble(4, txnAmount);
                            int x = pupdate.executeUpdate();
                            if (x < 1)
                                //System.out.println("\nNot Updated " + i + " transid=" + ar.get((j * 5) + 4) + " amt=" + txnAmount);
                            pupdate.clearParameters();
                        }// end if flag
                    }// end if hasMoreElements
                }// end if value != null
                System.out.print(" i=" + i++);
//                divRS.beforeFirst();
            }// end while
//            conn.commit();
            wr.close();
            pupdate.close();
        }
        catch (SystemError se)
        {   logger.error("System Error occure",se);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            se.printStackTrace(pw);
            

        }
        catch (Exception e)
        {   logger.error("Exception occure",e);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

        }
    }
}


