import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.BankManager;
import com.manager.GatewayManager;
import com.manager.vo.BankWireManagerVO;
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
 * User: Administrator
 * Date: 10/9/14
 * Time: 6:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class BankWireManagerList extends HttpServlet
{
    private  static Logger logger = new Logger(BankWireManagerList.class.getName());

    public void  doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        HttpSession session = request.getSession();

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            response.sendRedirect("/icici/sessionout.jsp");
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
        String bankwireid = request.getParameter("bankwiremangerid");
        String parent_bankwireId = request.getParameter("parent_bankwireId");

        //List of BankReceivedSettlementList Declaration
        List<BankWireManagerVO> bankWireManagerVOList= null;
        Calendar cal= Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date= null;
        RequestDispatcher rdError = request.getRequestDispatcher("/bankWireManager.jsp?MES=ERR&ctoken=" + user.getCSRFToken());
        RequestDispatcher rdSuccess = request.getRequestDispatcher("/bankWireManager.jsp?MES=Success&ctoken=" + user.getCSRFToken());

        try
        {

            ValidationErrorList validationErrorList = validateOptionalParameter(request);

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
            String accountId = request.getParameter("accountid");
            String pgTypeId = functions.splitGatewaySet(request.getParameter("pgtypeid"));
            //inserting gateway Details
            /*if (functions.isValueNull(pgTypeId))
            {
                gatewayTypeVO = gatewayManager.getGatewayTypeForPgTypeId(pgTypeId);
            }
            if (functions.isValueNull(accountId))
            {
                gatewayAccountVO = gatewayManager.getGatewayAccountForAccountId(accountId);
            }*/
            //inserting the values of Pagination
            paginationVO.setInputs("fromdate=" + fromDate + "&todate=" + toDate + "&accountid=" + accountId + "&pgtypeid=" + pgTypeId);
            paginationVO.setPageNo(Functions.convertStringtoInt(request.getParameter("SPageno"), 1));
            paginationVO.setPage(BankWireManagerList.class.getName());
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(request.getParameter("currentblock"), 1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(request.getParameter("SRecords"), 15));

            logger.debug("fromDate::"+inputDateVO.getsMinTransactionDate()+" toDate::"+inputDateVO.getsMaxTransactionDate());
            //account manager
            bankWireManagerVOList = bankManager.getBankWireManagerList(inputDateVO, paginationVO, accountId, pgTypeId,bankwireid,parent_bankwireId);

            //response
            request.setAttribute("paginationVO", paginationVO);
            request.setAttribute("BankWireManagerVOList", bankWireManagerVOList);
        }
        catch (Exception e)
        {
            logger.error("Main class exception::", e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        rdSuccess.forward(request, response);
    }
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        doPost(request, response);
    }
    private ValidationErrorList validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        //inputFieldsListMandatory.add(InputFields.PGTYPEID);
        inputFieldsListMandatory.add(InputFields.ACCOUNTID_SMALL);
        inputFieldsListMandatory.add(InputFields.FDATE);
        inputFieldsListMandatory.add(InputFields.TDATE);
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);
        inputFieldsListMandatory.add(InputFields.BANKWIREMANGERID);
        inputFieldsListMandatory.add(InputFields.PARENT_BANKWIREID);
        ValidationErrorList validationErrorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListMandatory,validationErrorList,true);
        return validationErrorList;
    }
}
