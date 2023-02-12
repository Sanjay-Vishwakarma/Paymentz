import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.manager.dao.BlacklistDAO;
import com.manager.vo.BlacklistVO;
import com.manager.vo.PaginationVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahima on 1/9/2016.
 */
public class BlockCountry extends HttpServlet
{
    private static Logger log = new Logger(BlockCountry.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        Merchants merchants = new Merchants();
        Functions functions=new Functions();
        BlacklistDAO blacklistDAO=new BlacklistDAO();
        PaginationVO paginationVO =new PaginationVO();
        List<BlacklistVO> listOfCountry = null;

        if (!merchants.isLoggedIn(session))
        {   log.debug("member is logout ");
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        String memberid = (String) session.getAttribute("merchantid");
        String countryCodeTelcc = "";
        String country = "";
        String error="";
        String unblock=req.getParameter("unblock");
        error = error + validateOptionalParameters(req);
        if (functions.isValueNull(error))
        {
            req.setAttribute("error",error);
            RequestDispatcher rd = req.getRequestDispatcher("/blockCountry.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        countryCodeTelcc = req.getParameter("countryList");
        paginationVO.setPageNo(Functions.convertStringtoInt(req.getParameter("SPageno"),1));
        paginationVO.setPage(BlockCountry.class.getName());
        paginationVO.setCurrentBlock(Functions.convertStringtoInt(req.getParameter("currentblock"),1));
        paginationVO.setRecordsPerPage(Functions.convertStringtoInt(req.getParameter("SRecords"),15));
        paginationVO.setInputs("&countryList="+countryCodeTelcc);

        if(functions.isValueNull(error)){
            req.setAttribute("error",error);
            RequestDispatcher rd = req.getRequestDispatcher("/blockCountry.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        if(!"Select Country".equalsIgnoreCase(countryCodeTelcc) && null!=countryCodeTelcc)
        {
            String[] sData = countryCodeTelcc.split("\\|");
            country = sData[3];
        }

        try
        {
            listOfCountry=blacklistDAO.getBlockedCountryPage(country, "",memberid,paginationVO);
            String[] ids=req.getParameterValues("id");
            if("unblock".equalsIgnoreCase(unblock))
            {
                for(String id : ids)
                {
                    blacklistDAO.unblockCountry(id);
                }
                error="Records Unblocked Successfully.";
                listOfCountry=blacklistDAO.getBlockedCountryPage(country, "", memberid, paginationVO);
            }
        }
        catch (PZDBViolationException e)
        {
            log.error("PZDBViolationException In BlockCountry::::",e);
        }

        req.setAttribute("error",error);
        req.setAttribute("merchantid",memberid);
        req.setAttribute("listOfCountry",listOfCountry);
        req.setAttribute("paginationVO",paginationVO);
        RequestDispatcher rd = req.getRequestDispatcher("/blockCountry.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
        return;
    }
    private String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.ACCOUNTID_SMALL);
        inputFieldsListOptional.add(InputFields.PAGENO);
        inputFieldsListOptional.add(InputFields.RECORDS);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional,errorList,true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }
}
