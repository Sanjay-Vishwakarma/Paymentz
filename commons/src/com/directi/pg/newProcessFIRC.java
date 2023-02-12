package com.directi.pg;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

public class newProcessFIRC
{
    public static void main(String[] args)
    {
        Logger logger = new Logger(newProcessFIRC.class.getName());
        if (args.length != 5)
        {

            logger.debug("Give fifth argument for DEFAULT icicimerchantid ");
            System.exit(0);

        }
        else
        {
            logger.debug(processFIRCFiles(args[0], args[1], args[2], args[3], args[4]).replaceAll("<br>", "\n"));
        }
    }

    public static String processFIRCFiles(String pathToFirc, String fircFileName, String grossAmount, String iciciMerchantId, String defaultMerchantId)

    {
        StringBuffer result = new StringBuffer();
        StringBuffer fircBuf = new StringBuffer();
        Connection conn =null;

        int totalCount = 0, countselect = 0, countinsert = 0;
        try
        {
            conn = Database.getConnection();
            result.append("Processing file: " + fircFileName);
            if (iciciMerchantId != null)
            {   String query=" select * from member_pg_preferences where icicimerchantid = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1,iciciMerchantId);
                ResultSet rs = pstmt.executeQuery();
                if (!rs.next())
                {
                    if (!iciciMerchantId.equals(defaultMerchantId))
                    {
                        throw new Exception("<br>Merchant Id not Found : " + iciciMerchantId);
                    }
                }
            }
            else
            {
                throw new Exception("<br>Please Enter valid Merchant Id");
            }
            if (fircFileName == null)
            {
                throw new FileNotFoundException("<br>FIRC File Not found");
            }
            BufferedReader rin = new BufferedReader(new FileReader(pathToFirc + fircFileName));
            String temp = null;

            while ((temp = rin.readLine()) != null)
            {
                fircBuf.append(temp + "\r\n");
            }
            rin.close();
        }
        catch (Exception ex)
        {
            return result.append("<br>" + ex.getMessage() + "<br>").toString();
        }

        String rawdata = fircBuf.toString();
        StringTokenizer stz = new StringTokenizer(rawdata, "\r\n");
        StringTokenizer tempstz = new StringTokenizer(rawdata, "\r\n");
        try
        {


            //first cross check total amount with amount passed as argument which will be amount written in firc file
            int recsnotinserted = 0;
            String skippedids = "";
            String tempselect = null;
            StringBuffer tempQuery = new StringBuffer();

            Vector vecKnownFTs = new Vector();
            Vector vecUnknownFTs = new Vector();
            String fileNameWithoutExt = fircFileName.substring(0, fircFileName.indexOf("."));
            FileOutputStream newfout = null;
            DataOutputStream newout = null;

            while (tempstz.hasMoreElements())
            {
                String tempvalue = ((String) tempstz.nextElement()).trim();
                StringTokenizer tempinnerStz = new StringTokenizer(tempvalue, ",");
                totalCount++;

                tempselect = new String("select captureid,refundid,status,captureamount,refundamount,transid,toid,date_format(from_unixtime(dtstamp),'%d-%m-%Y') as \"date\",date_format(from_unixtime(dtstamp),'%m%Y') as \"batch\",month(from_unixtime(dtstamp)) as transmonth,month(from_unixtime(unix_timestamp(timestamp))) as modmonth,date_format(transaction_icicicredit.timestamp,'%m%Y') as revbatch from transaction_icicicredit where ");
                StringBuffer tempsbQuery = new StringBuffer();

                if (tempinnerStz.hasMoreElements())
                {
                    String tempamount = ((String) tempinnerStz.nextElement()).trim();
                    String amount = ""; //if amount is in negetive then make it possitive
                    int index = tempamount.indexOf("-");
                    if (index != -1)
                    {
                        amount = tempamount.substring(index + 1);
                    }
                    else
                    {
                        amount = tempamount;
                    }
                    String tempFT = ((String) tempinnerStz.nextElement()).trim();
                    String usdVal = ((String) tempinnerStz.nextElement()).trim();
                    String convrate = ((String) tempinnerStz.nextElement()).trim();

                    vecUnknownFTs.add(tempFT); //collect all FTs which will be later used to check which FTs are not there in our database
                    StringBuffer captureQuery = new StringBuffer();
                    captureQuery.append(" captureamount =? and status in('settled','chargeback') " + "and captureid=?");
                    captureQuery.append(" and icicimerchantid=?");

                    boolean recordFound = false;
//                    result.append("+<br>Capture Query : "+ tempselect + captureQuery.toString() + "<br>");
                    PreparedStatement p1=conn.prepareStatement(tempselect + captureQuery.toString());
                    p1.setString(1,amount);
                    p1.setString(2,tempFT);
                    p1.setString(3,iciciMerchantId);
                    ResultSet rs = p1.executeQuery();
                    if (!rs.next())
                    {
                        StringBuffer refundQuery = new StringBuffer();
                        refundQuery.append("  refundamount =? and status = 'reversed' " + " and refundid=?");
                        refundQuery.append(" and icicimerchantid=?");
//                        result.append("<br>Refund query :"+ tempselect + refundQuery.toString() + "<br>");
                        PreparedStatement p2=conn.prepareStatement(tempselect + refundQuery.toString());
                        p2.setString(1,amount);
                        p2.setString(2,tempFT);
                        p2.setString(3,iciciMerchantId);
                        rs = p2.executeQuery();
                        if (rs.next())
                        {
                            recordFound = true;
                        }
                        else
                        {
                            recordFound = false;
                        }
                    }
                    else
                    {
                        recordFound = true;
                    }
                    if (recordFound)
                    {
                        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
                        StringBuffer sbInsQuery = new StringBuffer("insert into icici_firc (transid,date,memberid,amount,batchno,dtstamp,usdval,convrate) values (");
                        sbInsQuery.append(ESAPI.encoder().encodeForSQL(me,rs.getString("transid")) + ",");
                        sbInsQuery.append("'" + ESAPI.encoder().encodeForSQL(me,rs.getString("date")) + "',");
                        sbInsQuery.append(ESAPI.encoder().encodeForSQL(me,rs.getString("toid")) + ",");
                        String actualAmount = "";
                        String query="select count(*)as \"count\",transid from icici_firc where transid=? group by transid";
                        PreparedStatement ps=conn.prepareStatement(query);
                        ps.setString(1,rs.getString("transid"));
                        ResultSet rs1 = ps.executeQuery();
                        if (rs.getString("status").equalsIgnoreCase("settled"))
                        {
                            if (!rs1.next())
                            {
                                actualAmount = rs.getString("captureamount");
                                sbInsQuery.append(ESAPI.encoder().encodeForSQL(me,actualAmount) +",");
                                sbInsQuery.append(ESAPI.encoder().encodeForSQL(me,rs.getString("batch")) + ",");
                                vecKnownFTs.add(rs.getString("captureid")); // add FTs for which we got records.
                            }
                            else
                            {
                                vecKnownFTs.add(rs.getString("captureid")); // add FTs for which we got records.
                                recsnotinserted++;
                                continue;
                            }
                        }
                        else
                        if (rs.getString("status").equalsIgnoreCase("reversed") || rs.getString("status").equalsIgnoreCase("chargeback"))
                        {
                            //check whether we have any entry for this transid
                            if (!rs1.next()) //we need to make positive entry first
                            {
                                actualAmount = rs.getString("captureamount");

                                StringBuffer sb = new StringBuffer(sbInsQuery.toString());
                                sb.append(actualAmount + ",");
                                sb.append(rs.getString("batch") + ",");
                                sb.append("unix_timestamp(now()),");
                                sb.append(usdVal + ",");
                                sb.append(convrate + ")");

                                //make first entry here second negetive entry will be done afterword
                                Database.executeUpdate(sb.toString(), conn);
                                //vecKnownFTs.add(rs.getString("captureid"));
                                // if transaction month and reverse/chargeback month are same we need to make both entry
                                vecKnownFTs.add(rs.getString("refundid")); // add refundid for which we got records.
                                if (rs.getInt("transmonth") != rs.getInt("modmonth"))
                                {
                                    //I am assuming that all firc files will run in sequece of month
                                    //so if reverse/chargeback month is not same as transaction date and we had not a single entry
                                    // the running firc file is for positive entry for this transid and negetive entry will get in next month
                                    // as positive entry is made previously continue..
                                    //if the month is same then I will allow it to go ahead and put send insert for negetive entry in following code
                                    //vecKnownFTs.add(rs.getString("captureid")); // add FTs for which we got records.
                                    skippedids = skippedids + "," + rs.getString("captureid");
                                    recsnotinserted++;
                                    continue;
                                }
                                //if month is same then following code will make negetive entry
                            }
                            else
                            if (rs1.getInt("count") == 2) //we have one positive entry and one negetive entry so no need to make entry
                            {
                                vecKnownFTs.add(rs.getString("captureid")); // add FTs for which we got records.
                                vecKnownFTs.add(rs.getString("refundid"));
                                skippedids = skippedids + "," + rs.getString("captureid");
                                recsnotinserted++;
                                continue;
                            }
                            //System.out.println("Before If ");
                            //we have positive entry for this particular record so make only negetive entry
                            if (rs.getString("status").equalsIgnoreCase("reversed"))
                                actualAmount = "-" + rs.getString("refundamount");
                            else //in case of chargeback there want be partial chargeback
                                actualAmount = "-" + rs.getString("captureamount");

                            sbInsQuery.append(ESAPI.encoder().encodeForSQL(me,actualAmount)+",");
                            sbInsQuery.append(ESAPI.encoder().encodeForSQL(me,rs.getString("revbatch")) + ",");
                        }

                        sbInsQuery.append("unix_timestamp(now()),");
                        sbInsQuery.append(ESAPI.encoder().encodeForSQL(me,usdVal) + ",");
                        sbInsQuery.append(ESAPI.encoder().encodeForSQL(me,convrate) + ")");

                        String insQuery = sbInsQuery.toString();
                        countinsert++;
                        Database.executeUpdate(insQuery, conn);
                    }
                }
            }

            if (vecUnknownFTs.removeAll(vecKnownFTs)) // remove all FTs that we have in our table so vector will now contain only those FTs for which we don't have records.
            {
                if (!vecUnknownFTs.isEmpty())
                {
                    newfout = new FileOutputStream(pathToFirc + fileNameWithoutExt + "_FT_not_found.txt");
                    newout = new DataOutputStream(newfout);
                    Iterator it = vecUnknownFTs.iterator();
                    newout.writeBytes("<br><br>Records for Below FT(s) are not found : \r\n\r\n");
                    result.append("<br><br>Records for Below FT(s) are not found  : <br>");
                    while (it.hasNext())
                    {
                        String data = (String) it.next();
                        newout.writeBytes(data + "  |  ");
                        result.append(data + "|");
                    }
                    newout.writeBytes("\r\nHere you will get ids on which transactions are reversed/chargeback and we could not find record due to lack of data like refundid/chargebackid.\r\n You need to check thses FT from ICICI merchant interface.\r\nand get tracking id. Then you can manually make -ve entry for all firc \r\nthat are already given to them and now that transaction has been reversed.\r\n\r\n ");
                }
            }
            result.append("<br><br>total records in file=" + totalCount + "<br>total insert=" + countinsert + "<br>skipped insert =" + recsnotinserted);
            if (skippedids.length() > 0)
            {
                result.append("<br><br> Below FTs are skipeed as negetive entries for these ids will be done in next month firc <br>" + skippedids);
            }
            if (newout != null)
            {
                newout.close();
            }
        }
        catch (Exception e)
        {
            result.append("<br> Invalid File  <br>");
        }
        return result.toString();

    }

}