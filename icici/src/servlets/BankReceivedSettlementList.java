import com.directi.pg.*;

import com.manager.BankManager;
import com.manager.GatewayManager;
import com.manager.vo.BankRecievedSettlementCycleVO;
import com.manager.vo.InputDateVO;
import com.manager.vo.PaginationVO;
import com.manager.vo.gatewayVOs.GatewayAccountVO;
import com.manager.vo.gatewayVOs.GatewayTypeVO;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: NIKET
 * Date: 8/30/14
 * Time: 7:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class BankReceivedSettlementList extends HttpServlet
{
    private static Logger logger = new Logger(BankReceivedSettlementList.class.getName());
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        HttpSession session = request.getSession();

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        Functions functions =new Functions();
        //Manager instance
        BankManager bankManager = new BankManager();
        GatewayManager gatewayManager = new GatewayManager();
        //Vo initialization
        InputDateVO inputDateVO = new InputDateVO();
        PaginationVO paginationVO = new PaginationVO();

        GatewayAccountVO gatewayAccountVO = new GatewayAccountVO();
        GatewayTypeVO gatewayTypeVO = new GatewayTypeVO();

        //List of BankReceivedSettlementList Declaration
        List<BankRecievedSettlementCycleVO> bankSettlementCycleVOList= null;
        Calendar cal= Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date= null;


        try
        {

            ValidationErrorList validationErrorList = validateOptionalParameter(request);

            RequestDispatcher rdError = request.getRequestDispatcher("/bankRecievedSettlementMaster.jsp?MES=ERR&ctoken=" + user.getCSRFToken());
            RequestDispatcher rdSuccess = request.getRequestDispatcher("/bankRecievedSettlementMaster.jsp?MES=Success&ctoken=" + user.getCSRFToken());
            if (!validationErrorList.isEmpty())
            {
                request.setAttribute("error", validationErrorList);
                rdError.forward(request, response);
                return;
            }
            //aplly when datePicker is been applied instead of dropDown
            String fromDate = "";
            if (functions.isValueNull(request.getParameter("fromdate")))
            {
                fromDate = request.getParameter("fromdate");

                date = sdf.parse(fromDate);
                cal.setTime(date);
                String fdate = String.valueOf(cal.get(Calendar.DATE));
                String fmonth = String.valueOf(cal.get(Calendar.MONTH));
                String fyear = String.valueOf(cal.get(Calendar.YEAR));

                String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");

                inputDateVO.setFdate(fdate);
                inputDateVO.setFmonth(fmonth);
                inputDateVO.setFyear(fyear);

                inputDateVO.setFdtstamp(fdtstamp);

            }
            else
            {
                inputDateVO.setFdtstamp(String.valueOf(new Long(Calendar.getInstance().get(Calendar.YEAR))/1000));
            }
            String toDate = "";
            if (functions.isValueNull(request.getParameter("todate")))
            {
                toDate = request.getParameter("todate");

                date = sdf.parse(toDate);
                cal.setTime(date);
                String tdate = String.valueOf(cal.get(Calendar.DATE));
                String tmonth = String.valueOf(cal.get(Calendar.MONTH));
                String tyear = String.valueOf(cal.get(Calendar.YEAR));

                String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");

                inputDateVO.setTdate(tdate);
                inputDateVO.setTmonth(tmonth);
                inputDateVO.setTyear(tyear);

                inputDateVO.setTdtstamp(tdtstamp);
            }
            else
            {
                inputDateVO.setTdtstamp(String.valueOf(new Long(Calendar.getInstance().getTime().getTime())/1000));
            }
            //accountId and pgTypeId
            String accountId = "";
            if (!request.getParameter("accountid").equalsIgnoreCase("0"))
            {
                 accountId = request.getParameter("accountid");
            }

            String pgTypeId = request.getParameter("pgtypeid");
            //inserting gateway Details
            if (functions.isValueNull(pgTypeId))
            {
                gatewayTypeVO = gatewayManager.getGatewayTypeForPgTypeId(pgTypeId);
            }
            if (functions.isValueNull(accountId))
            {
                gatewayAccountVO = gatewayManager.getGatewayAccountForAccountId(accountId);
            }
            //inserting the values of Pagination
            paginationVO.setInputs("fromdate=" + fromDate + "&todate=" + toDate + "&accountid=" + accountId + "&pgtypeid=" + pgTypeId);
            paginationVO.setPageNo(Functions.convertStringtoInt(request.getParameter("SPageno"), 1));
            paginationVO.setPage(BankReceivedSettlementList.class.getName());
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(request.getParameter("currentblock"), 1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(request.getParameter("SRecords"), 15));

            logger.debug("fromDate::"+inputDateVO.getsMinTransactionDate()+" toDate::"+inputDateVO.getsMaxTransactionDate());
            //account manager
            bankSettlementCycleVOList = bankManager.getBankSettlementCycleList(inputDateVO, paginationVO, gatewayAccountVO, gatewayTypeVO);

            //response
            request.setAttribute("paginationVO", paginationVO);
            request.setAttribute("BankSettlementCycleVOs", bankSettlementCycleVOList);
            rdSuccess.forward(request, response);

        }
        catch (Exception e)
        {
            logger.error("Main class exception::", e);
        }
    }

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        doPost(request,response);
    }
    private ValidationErrorList validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.GATEWAY);
        inputFieldsListMandatory.add(InputFields.ACCOUNTID_SMALL);
        inputFieldsListMandatory.add(InputFields.FDATE);
        inputFieldsListMandatory.add(InputFields.TDATE);
        ValidationErrorList validationErrorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListMandatory,validationErrorList,true);
        return validationErrorList;
    }
}
