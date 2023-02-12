package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.BankManager;
import com.manager.dao.BankDao;
import com.manager.dao.PartnerDAO;
import com.manager.vo.BankWireManagerVO;
import com.manager.vo.InputDateVO;
import com.manager.vo.PaginationVO;
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
 * Created by Admin on 1/31/2020.
 */
public class BankWireManager extends HttpServlet
{
    private static Logger logger=new Logger(BankWireManager.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws IOException,ServletException{
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws IOException,ServletException
    {
        HttpSession session = request.getSession();
        PartnerFunctions partner=new PartnerFunctions();
        session.setAttribute("submit","bankWire");
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!partner.isLoggedInPartner(session))
        {   logger.debug("Partner is logout ");
            response.sendRedirect("/icici/sessionout.jsp");
            return;
        }
        BankManager bankManager = new BankManager();
        PaginationVO paginationVO = new PaginationVO();
        InputDateVO inputDateVO = new InputDateVO();
        Functions functions=new Functions();
        PartnerFunctions partnerFunctions=new PartnerFunctions();
        BankDao bankDao=new BankDao();

        List<BankWireManagerVO> bankWireManagerVOList= null;
        RequestDispatcher rdError = request.getRequestDispatcher("/bankWireManager.jsp?MES=ERR&ctoken=" + user.getCSRFToken());
        RequestDispatcher rdSuccess = request.getRequestDispatcher("/bankWireManager.jsp?MES=Success&ctoken=" + user.getCSRFToken());
        String partnerId=(String)session.getAttribute("merchantid");
        System.out.println("PartnerId::::"+partnerId);

        try
        {
            ValidationErrorList validationErrorList = validateOptionalParameter(request);
            if (!validationErrorList.isEmpty())
            {
                request.setAttribute("error", validationErrorList);
                rdError.forward(request, response);
                return;
            }
            String accountId = request.getParameter("accountid");
            String bankWireId = request.getParameter("bankwiremangerid");
            String fromDate = "";
            Calendar cal= Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date= null;
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
            paginationVO.setInputs("&accountid=" + accountId + "&bankWireId=" + bankWireId);
            paginationVO.setPageNo(Functions.convertStringtoInt(request.getParameter("SPageno"), 1));
            paginationVO.setPage(BankWireManager.class.getName());
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(request.getParameter("currentblock"), 1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(request.getParameter("SRecords"), 15));


            String listOfPartnerId=partnerFunctions.getListOfSubPartner(partnerId);
            String listOfAccountId=bankDao.getListOfAccountIdMappedWithPartner(listOfPartnerId);
            //account manager
            bankWireManagerVOList = bankManager.getBankWireManagerListBasedOnPartner(inputDateVO,paginationVO, accountId, bankWireId,listOfAccountId);

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
    private ValidationErrorList validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.BANKWIREMANGERID);
        inputFieldsListMandatory.add(InputFields.ACCOUNTID_SMALL);
        inputFieldsListMandatory.add(InputFields.FDATE);
        inputFieldsListMandatory.add(InputFields.TDATE);
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);
        ValidationErrorList validationErrorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListMandatory,validationErrorList,true);
        return validationErrorList;
    }
}