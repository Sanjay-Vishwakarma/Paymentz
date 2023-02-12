package net.partner;

import com.directi.pg.*;
import com.manager.dao.AccountDAO;
import com.manager.dao.BankDao;
import com.manager.vo.payoutVOs.WireVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 8/3/15
 * Time: 7:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListMerchantWireRandomCharges extends HttpServlet
{
    Logger logger=new Logger(ListMerchantWireRandomCharges.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        doProcess(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        doProcess(request,response);
    }
    public void doProcess(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        HttpSession session = request.getSession();
        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session)){
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        Connection conn = null;
        int records=15;
        int pageno=1;

        String memberId=request.getParameter("memberid");
        String terminalId=request.getParameter("terminalid");
        String bankwireId=request.getParameter("bankwireid");
        String settledId=request.getParameter("settledid");
        String action=request.getParameter("action");
        String errorMsg="";
        String EOL="<BR>";

        logger.error("bankwireId:::::"+bankwireId);
        logger.error("settledId:::::"+settledId);
        logger.error("action:::::"+action);

        Hashtable hash = null;
        Functions functions=new Functions();
        StringBuffer sb=new StringBuffer();
        AccountDAO accountDAO=new AccountDAO();
        BankDao bankDao=new BankDao();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        RequestDispatcher rd=request.getRequestDispatcher("/listMerchantWireRandomCharges.jsp?ctoken="+user.getCSRFToken());

        if(!ESAPI.validator().isValidInput("memberid", memberId, "Numbers", 20,true))
        {
            sb.append("Invalid Member Id,");
        }
        if(!ESAPI.validator().isValidInput("terminalid", terminalId, "Numbers", 5,true))
        {
            sb.append("Invalid Terminal Id,");
        }
        if(!ESAPI.validator().isValidInput("bankwireid", bankwireId, "Numbers", 5,true))
        {
            sb.append("Invalid Bankwire Id,");
        }
        if(sb.length()>0)
        {
            logger.error("Validation Failed===="+sb.toString());
            request.setAttribute("statusMsg",sb.toString());
            rd.forward(request,response);
            return;
        }

        int start = 0; // start index
        int end = 0; // end index

        pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(request.getParameter("SRecords"), 15);
        start = (pageno - 1) * records;
        end = records;
        ResultSet rs = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("select merchantrdmchargeid,memberid,terminalid,chargename,chargerate,valuetype,chargecounter,chargeamount,chargevalue,chargeremark from merchant_random_charges where merchantrdmchargeid>0 ");
            StringBuffer countquery = new StringBuffer("select count(*) from merchant_random_charges where merchantrdmchargeid>0 ");


            if(functions.isValueNull(memberId))
            {
                query.append(" and memberid='"+memberId+"'");
                countquery.append(" and memberid='"+memberId+"'");
            }
            if(functions.isValueNull(terminalId))
            {
                query.append(" and terminalid='"+terminalId+"'");
                countquery.append(" and terminalid='"+terminalId+"'");
            }
            if(functions.isValueNull(bankwireId))
            {
                query.append(" and bankwireid='"+bankwireId+"'");
                countquery.append(" and bankwireid='"+bankwireId+"'");
            }
            query.append(" order by merchantrdmchargeid desc LIMIT " + start + "," + end);

            logger.debug("Query:-" + query);
            logger.debug("CountQuery:-" + countquery);

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));

            request.setAttribute("transdetails", hash);

            if(action.equalsIgnoreCase("delete"))
            {
                boolean status=deletePayoutReport(settledId,terminalId,bankwireId);
                if(status)
                {
                    errorMsg= "<center><font class=\"text\" face=\"arial\"><b>Record Deletion successful." + EOL + "</b></font></center>";
                    List<WireVO> cycleId=accountDAO.getCycleId(bankwireId);
                    if(cycleId==null || cycleId.size()==0){
                        bankDao.updateTheStatusDelete(bankwireId,"BankWireGenerated");
                    }
                }
                else
                {
                    errorMsg = "<center><font class=\"text\" face=\"arial\"><b>Record Deletion Failed." + EOL + "</b></font></center>";
                }
                request.setAttribute("statusMsg",errorMsg);
                rd.forward(request, response);
                return;
            }
        }
        catch (SystemError s)
        {
            logger.error("System error while perform select query", s);
            sb.append("System error while perform select query"+s);
        }
        catch (SQLException e)
        {
            logger.error("SQL error", e);
            sb.append("SQL Exception while perform select query"+ e);
        }
        catch (Exception s)
        {
            logger.error("System error while cycleId Query", s);
            sb.append("System error while perform cycleId Query"+s);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        request.setAttribute("statusMsg",sb.toString());
        request.setAttribute("statusMsg",errorMsg);
        rd.forward(request,response);
        return;
    }
    public boolean deletePayoutReport(String mappingid,String terminalId,String settlementCycleNO)
    {
        boolean flag=false;
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        try
        {
            connection= Database.getConnection();
            String qry="DELETE FROM bank_merchant_settlement_master WHERE cycleid=? AND terminalId=? ";
            preparedStatement=connection.prepareStatement(qry);
            preparedStatement.setString(1,settlementCycleNO);
            preparedStatement.setString(2,terminalId);
            int i=preparedStatement.executeUpdate();
            logger.error("Query:::::"+preparedStatement);
            logger.error("i:::::"+i);

            if(i==1)
            {
                String qry1 = "DELETE FROM merchant_wiremanager WHERE settledid=? AND terminalid=? AND settlementcycle_no=?";
                String qry2 = "DELETE FROM member_settlementcycle_details WHERE cycleid=? AND terminalid=?";
                preparedStatement = connection.prepareStatement(qry1);
                preparedStatement.setString(1, mappingid);
                preparedStatement.setString(2, terminalId);
                preparedStatement.setString(3, settlementCycleNO);
                int j = preparedStatement.executeUpdate();
                logger.error("Query1:::::" + preparedStatement);
                logger.error("j:::::" + j);

                preparedStatement = connection.prepareStatement(qry2);
                preparedStatement.setString(1, settlementCycleNO);
                preparedStatement.setString(2, terminalId);
                int k = preparedStatement.executeUpdate();
                logger.error("Query2:::::" + preparedStatement);
                logger.error("j:::::" + k);

                if(i==1 && j==1 && k==1){
                    flag=true;
                }
            }
            else
            {
                logger.error("Something is wrong while fetching details from all the tables.");
                return flag;
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Error while fetching data from merchant_wiremanager",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL error while fetching data from merchant_wiremanager", e);
        }
        finally {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return flag;
    }
}
