package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import org.codehaus.jettison.json.JSONObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Created by Admin on 11/16/2018.
 */
public class EmiServlet extends HttpServlet
{
    private static Logger log = new Logger(EmiServlet.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(EmiServlet.class.getName());

    public void doPost(HttpServletRequest req , HttpServletResponse res) throws IOException , ServletException {


        HttpSession session = req.getSession(true);
        String ctoken = req.getParameter("ctoken");
        session.setAttribute("ctoken", ctoken);

        transactionLogger.debug("----inside EmiServlet----"+ctoken);
        for(Object key : req.getParameterMap().keySet())
        {
            log.error("----for loop EmiServlet-----" + key + "=" + req.getParameter((String) key) + "--------------");
            transactionLogger.error("----for loop EmiServlet-----" + key + "=" + req.getParameter((String) key) + "--------------");
        }


        Enumeration en=req.getParameterNames();

        while (en.hasMoreElements()){
            String key = (String) en.nextElement();
            String value=req.getParameter(key);

            transactionLogger.debug("key----"+key+"-----"+value);
        }

        BufferedReader bf=req.getReader();

        StringBuffer sb= new StringBuffer();

        String str="";
        while ((str=bf.readLine())!=null)
        {
            sb.append(str);
        }

        Functions functions= new Functions();
        PrintWriter ps=res.getWriter();
        String memberId="";
        String cardType="";
        String binRouting ="";
        String firstSix="";
        String cardNo = "";
        String terminalId="";
        String emiSupportFlag="";
        String payMode="";


        try
        {
            if(functions.isValueNull(sb.toString()))
            {
                JSONObject jsonObject= new JSONObject(sb.toString());

                if(jsonObject!=null)
                {
                    memberId= jsonObject.getString("memberId");
                    cardType= jsonObject.getString("cardType");
                    binRouting= jsonObject.getString("binRouting");
                    firstSix= jsonObject.getString("firstSix");
                    terminalId= jsonObject.getString("terminal");
                    payMode= jsonObject.getString("payMode");
                    cardNo= jsonObject.getString("cardNo");

                    cardType= GatewayAccountService.getCardId(cardType);
                    payMode= GatewayAccountService.getPaymentId(payMode);

                    transactionLogger.debug("cardTYpe-----"+cardType);
                    transactionLogger.debug("payMode-----"+payMode);

                    terminalId = getRoutedTerminal(firstSix,memberId,cardType,binRouting,payMode,cardNo);
                    transactionLogger.error(" new terminal ----------- "+terminalId);

                    emiSupportFlag=getEmiSupportFlag(firstSix,memberId,cardType,binRouting,terminalId,payMode,cardNo);

                    transactionLogger.debug("emiSupportFlag-----"+emiSupportFlag);
                    transactionLogger.debug("cardType-----"+cardType);
                    transactionLogger.debug("payMode-----"+payMode);

                    JSONObject jsonObject1 = getDetails(memberId,terminalId);
                    jsonObject1.put("emiSupport", emiSupportFlag);
                    log.debug(" emi Servlet ctoken ="+ctoken);
                    res.setContentType("application/json");
                    res.setStatus(200);
                    ps.write(jsonObject1.toString());
                    ps.flush();
                    return;

                }
            }
        }
        catch (org.codehaus.jettison.json.JSONException e)
        {
            transactionLogger.error("JSONException-----",e);
        }

    }


    public static JSONObject getDetails(String memberid, String terminalid){

        JSONObject jsonObject =null;
        Functions functions= new Functions();
        Connection conn = null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        if(functions.isValueNull(memberid))
        {
            transactionLogger.debug("---inside--=-getDetails--");
            jsonObject= new JSONObject();
            try
            {
                conn = Database.getConnection();
                //String sql = "select startdate ,enddate,emi_period from emi_configuration where memberId=?";
                String sql = "SELECT FROM_UNIXTIME(startdate,\"%Y-%m-%d %H:%i:%s\") AS startDate, FROM_UNIXTIME(enddate,\"%Y-%m-%d %H:%i:%s\") AS endDate,emi_period FROM emi_configuration WHERE memberId=? and terminalid=? and enable='Y'";

                ps = conn.prepareStatement(sql);
                ps.setString(1, memberid);
                ps.setString(2, terminalid);
                rs = ps.executeQuery();
                while (rs.next())
                {
                    jsonObject.put("startdate", rs.getString("startdate"));
                    jsonObject.put("enddate", rs.getString("enddate"));
                    jsonObject.put("emi_period", rs.getString("emi_period"));
                }
                transactionLogger.debug("query-----"+ps);
            }
            catch (SystemError se)
            {
                transactionLogger.error("SystemError-----", se);
            }
            catch (SQLException e)
            {
                transactionLogger.error("SQLException-----", e);
            }
            catch (org.codehaus.jettison.json.JSONException j)
            {
                transactionLogger.error("JSONException-----", j);
            }
            finally
            {
                Database.closeConnection(conn);
                Database.closePreparedStatement(ps);
                Database.closeResultSet(rs);
            }
        }
        return jsonObject;
    }

    public String getRoutedTerminal(String startBin,String memberId,String cardtypeid,String binRouting, String payMode, String cardNo)
    {
        transactionLogger.error("----- in getRoutedTerminal -----");
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String terminalId = "";

        try
        {
            con = Database.getConnection();
            if (!"N".equalsIgnoreCase(binRouting))
            {
                String sqlQuery = "";
                String bin = "";
                if ("Card".equalsIgnoreCase(binRouting))
                {
                    sqlQuery = "SELECT terminalid FROM member_account_mapping AS mam, whitelist_bins AS wb WHERE ? BETWEEN CONCAT(startBin,startCard) AND CONCAT(endBin,endCard) AND cardtypeid=? AND paymodeid=? AND wb.memberid=? AND mam.memberid=wb.memberId AND mam.accountid=wb.accountId";
                    bin = cardNo;
                }
                else
                {
                    sqlQuery = "SELECT terminalid FROM member_account_mapping AS mam, whitelist_bins AS wb WHERE ? BETWEEN startBin AND endBin AND cardtypeid=? AND paymodeid=? AND wb.memberid=? AND mam.memberid=wb.memberId AND mam.accountid=wb.accountId";
                    bin = startBin;
                }
                ps = con.prepareStatement(sqlQuery);
                ps.setString(1, bin);
                ps.setString(2, cardtypeid);
                ps.setString(3, payMode);
                ps.setString(4, memberId);
                rs = ps.executeQuery();
                if (rs.next())
                {
                    terminalId = rs.getString("terminalid");
                }

                transactionLogger.debug("query-----"+ps);
            }
        }
        catch (Exception e)
        {

        }
        finally
        {
            Database.closeConnection(con);
            Database.closePreparedStatement(ps);
            Database.closeResultSet(rs);
        }
        return terminalId;
    }
    public static  String getEmiSupportFlag(String startBin,String memberId,String cardtypeid,String binRouting,String terminalId ,String payMode, String cardNo)
    {
        Connection con=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        String emiSupportFlag="";

        try{
            con=Database.getConnection();
            if(!"N".equalsIgnoreCase(binRouting))
            {
                String sqlQuery= "";
                String bin = "";
                if("Card".equalsIgnoreCase(binRouting))
                {
                    sqlQuery = "SELECT emi_support FROM member_account_mapping WHERE accountid=(SELECT accountId FROM whitelist_bins WHERE ? BETWEEN CONCAT(startBin,startCard) AND CONCAT(endBin,endCard) AND memberId=?) AND cardtypeid=? AND paymodeid=? AND memberid=?;";
                    bin = cardNo;
                }
                else
                {
                    sqlQuery = "SELECT emi_support FROM member_account_mapping WHERE accountid=(SELECT accountId FROM whitelist_bins WHERE ? BETWEEN startBin AND endBin AND memberId=?) AND cardtypeid=? AND paymodeid=? AND memberid=?;";
                    bin = startBin;
                }
                ps=con.prepareStatement(sqlQuery);
                ps.setString(1,bin);
                ps.setString(2,memberId);
                ps.setString(3,cardtypeid);
                ps.setString(4,payMode);
                ps.setString(5,memberId);
                rs=ps.executeQuery();
                if (rs.next())
                {
                    emiSupportFlag=rs.getString("emi_support");
                }
            }
            else
            {
                String sqlQuery="SELECT emi_support FROM member_account_mapping WHERE terminalId=? AND memberid=?;";
                ps=con.prepareStatement(sqlQuery);
                ps.setString(1,terminalId);
                ps.setString(2,memberId);
                rs=ps.executeQuery();
                if (rs.next())
                {
                    emiSupportFlag=rs.getString("emi_support");
                }
            }

            transactionLogger.error("sqlQuery-----"+ps);
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError-----",se);

        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException-----",e);
        }
        finally
        {
            Database.closeConnection(con);
            Database.closePreparedStatement(ps);
            Database.closeResultSet(rs);
        }
        return emiSupportFlag;
    }


}
