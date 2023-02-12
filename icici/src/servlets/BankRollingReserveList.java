import com.directi.pg.*;
import com.manager.BankManager;
import com.manager.GatewayManager;
import com.manager.vo.BankRollingReserveVO;
import com.manager.vo.InputDateVO;
import com.manager.vo.PaginationVO;
import com.manager.vo.gatewayVOs.GatewayAccountVO;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

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
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 9/4/14
 * Time: 3:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class BankRollingReserveList extends HttpServlet
{
    private static Logger logger = new Logger(BankRollingReserveList.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        String errormsg = "";
        String EOL = "<BR>";

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        RequestDispatcher rd = request.getRequestDispatcher("/bankRollingReserveList.jsp?ctoken="+user.getCSRFToken());
        Functions functions = new Functions();
        try
        {
            validateOptionalParameter(request);
        }
        catch (ValidationException e)
        {
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            logger.debug("message..." + e.getMessage());
            request.setAttribute("message",errormsg);
            rd.forward(request, response);
        }

        String fdate = request.getParameter("fdate");
        String tdate = request.getParameter("tdate");
        String fmonth = request.getParameter("fmonth");
        String tmonth = request.getParameter("tmonth");
        String fyear = request.getParameter("fyear");
        String tyear = request.getParameter("tyear");

        int records=15;
        int pageno=1;

        int start = 0; // start index
        int end = 0; // end index

        Calendar rightNow = Calendar.getInstance();

        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

        if (fmonth == null) fmonth = "" + (rightNow.get(rightNow.MONTH));
        if (tmonth == null) tmonth = "" + (rightNow.get(rightNow.MONTH));

        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);

        String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
        String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");

        pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(request.getParameter("SRecords"),15);

        start = (pageno - 1) * records;
        end = records;

        InputDateVO inputDateVO=new InputDateVO();
        inputDateVO.setFdtstamp(fdtstamp);
        inputDateVO.setTdtstamp(tdtstamp);
        try
        {
            String accountId = "";
            String gateway = "";

            if (!request.getParameter("accountid").equals("0"))
            {
                accountId= request.getParameter("accountid");
                request.setAttribute("accountid",accountId);
            }

            if (functions.isValueNull(request.getParameter("pgtypeid")) && !"0".equals(request.getParameter("pgtypeid")) && "0".equals(request.getParameter("accountid")))
            {
                String gatewayArr[] = request.getParameter("pgtypeid").split("-");
                gateway = gatewayArr[0];

                Set<String> accountids = getAccountIds(gateway);
                accountId = String.valueOf(accountids).replace("[","").replace("]","");
                request.setAttribute("pgtypeid", gateway);
            }
            request.setAttribute("pgtypeid",gateway);
            GatewayManager  gatewayManager=new GatewayManager();
            BankManager bankManager=new BankManager();
            PaginationVO paginationVO=new PaginationVO();

            GatewayAccountVO gatewayAccountVO = gatewayManager.getGatewayAccountForAccountId(accountId);

            paginationVO.setInputs("fmonth="+fmonth+"&fdate="+fdate+"&fyear="+fyear+"&tmonth="+tmonth+"&tdate="+tdate+"&tyear="+tyear+ "&accountid=" + accountId);
            paginationVO.setPageNo(pageno);
            paginationVO.setPage(BankRollingReserveList.class.getName());
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(request.getParameter("currentblock"), 1));
            paginationVO.setRecordsPerPage(records);

            List<BankRollingReserveVO> bankRollingReserveList=null;
            bankRollingReserveList = bankManager.getBankRollingReserveList(inputDateVO, paginationVO, gatewayAccountVO);

            request.setAttribute("paginationVO", paginationVO);
            request.setAttribute("BankRollingReserveVOs", bankRollingReserveList);
        }
        catch (SystemError systemError)
        {
            logger.error("systemError:::",systemError);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (Exception e)
        {
            logger.error("message..." + e.getMessage());
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        request.setAttribute("message",errormsg);
        rd.forward(request, response);
    }

    public Set<String> getAccountIds(String gateway)
    {
        Functions functions = new Functions();
        Set<String> accountIds = new HashSet<String>();
        StringBuffer query = new StringBuffer();
        Hashtable hash = null;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt=null;
        if(functions.isValueNull(gateway))
        {
            try
            {
                conn = Database.getRDBConnection();
                query = new StringBuffer("select A.accountid from gateway_accounts A, gateway_type T where A.pgtypeid = T.pgtypeid and T.gateway=?");
                pstmt=conn.prepareStatement(query.toString());
                pstmt.setString(1,gateway);
                rs = pstmt.executeQuery();
                while(rs.next())
                {
                    accountIds.add((String)rs.getString("accountid"));
                }
            }
            catch (SQLException se)
            {
                logger.error("SQLException in ListAccountID---",se);
            }
            catch (SystemError se)
            {
                logger.error("SystemError in ListAccountID---",se);
            }
            finally
            {
                Database.closeResultSet(rs);
                Database.closePreparedStatement(pstmt);
                Database.closeConnection(conn);
            }
        }
        return accountIds;
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);
        inputFieldsListMandatory.add(InputFields.ACCOUNTID_SMALL);
        inputFieldsListMandatory.add(InputFields.FROMDATE);
        inputFieldsListMandatory.add(InputFields.TODATE);
        inputFieldsListMandatory.add(InputFields.FROMMONTH);
        inputFieldsListMandatory.add(InputFields.TOMONTH);
        inputFieldsListMandatory.add(InputFields.FROMYEAR);
        inputFieldsListMandatory.add(InputFields.TOYEAR);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
}
