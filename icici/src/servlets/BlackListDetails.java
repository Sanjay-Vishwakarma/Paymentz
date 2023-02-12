import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.BlacklistManager;
import com.manager.dao.BlacklistDAO;
import com.manager.vo.BlacklistVO;
import com.manager.vo.PaginationVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
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
 * Created by 123 on 5/2/2015.
 */
public class BlackListDetails extends HttpServlet
{
    private static Logger log = new Logger(BlackListDetails.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        BlacklistDAO blacklistDAO = new BlacklistDAO();
        BlacklistVO blacklistVO = null;
        List<BlacklistVO> listOfCard = null;
        PaginationVO paginationVO = new PaginationVO();
        Functions function=new Functions();
        BlacklistManager blacklistManager= new BlacklistManager();

        String error = "";
       // error = error+validateParameters(req);
        String firstSix = req.getParameter("firstsix");
        String lastFour = req.getParameter("lastfour");
        String reason = req.getParameter("reason");
        String action = req.getParameter("unblock");
        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;
        StringBuilder sErrorMessage = new StringBuilder();
        String msg = "";
        String EOL="<BR>";
        int count=0;
        String remark = req.getParameter("remark");

        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        try
        {
            error=error+validateOptionalParameters(req);
            if(!function.isValueNull(firstSix) && !function.isValueNull(lastFour))
            {
                firstSix="";
                lastFour="";
            }
            else if(function.isValueNull(firstSix) && !function.isValueNull(lastFour))
            {
                lastFour="";
            }
            else if(!function.isValueNull(firstSix) && function.isValueNull(lastFour))
            {
                firstSix="";
            }
/*            else if(function.isValueNull(remark))
            {
                remark="-";
            }*/
            paginationVO.setPageNo(Functions.convertStringtoInt(req.getParameter("SPageno"), 1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(req.getParameter("SRecords"), 15));
            if("search".equalsIgnoreCase(req.getParameter("sbtn")))
            {
                if (error!= null && !error.equals(""))
                {
                    req.setAttribute("error",error);
                    RequestDispatcher rd = req.getRequestDispatcher("/blockedCardList.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
                else if (function.isValueNull(firstSix) && function.isValueNull(lastFour))
                {
                    listOfCard = blacklistDAO.getBlackListedCardsPages(firstSix, lastFour, actionExecutorId, actionExecutorName, remark, paginationVO, reason);
                }
                else
                {
                    listOfCard = blacklistDAO.getBlackListedCardsPages(firstSix, lastFour, actionExecutorId, actionExecutorName, remark, paginationVO, reason);
                }
            }

            if ("block".equalsIgnoreCase(req.getParameter("bbtn")))
            {
                if (!ESAPI.validator().isValidInput("firstsix", firstSix, "FirstSixcc", 6, false))
                {
                    log.debug("Invalid first six digits");
                    sErrorMessage.append("Invalid First Six and FirstSix should not be empty" + EOL);
                }
                if (!ESAPI.validator().isValidInput("lastfour", lastFour, "LastFourcc", 4, false))
                {
                    log.debug("Invalid last four digits");
                    sErrorMessage.append("Invalid last four and LastFour should not be empty" + EOL);
                }
                if (!ESAPI.validator().isValidInput("reason", reason, "Description", 100, false))
                {
                    sErrorMessage.append("Invalid Blacklist Reason or Blacklist Reason should not be empty." + EOL);
                }
                if (sErrorMessage.length() > 0)
                {
                    req.setAttribute("sErrorMessage", sErrorMessage.toString());
                    RequestDispatcher rd = req.getRequestDispatcher("/blockedCardList.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }

                if (!function.isValueNull(sErrorMessage.toString()))
                {
                    if ("block".equalsIgnoreCase(req.getParameter("bbtn")) && function.isValueNull(firstSix) && function.isValueNull(lastFour) && function.isValueNull(reason))
                    {
                        if(error!=null && !error.equals(""))
                        {
                            req.setAttribute("error",error);
                            RequestDispatcher rd = req.getRequestDispatcher("/blockedCardList.jsp?ctoken=" + user.getCSRFToken());
                            rd.forward(req, res);
                            return;
                        }
                        else
                        {
                            count = blacklistManager.insertBlacklistedCards(firstSix, lastFour, actionExecutorId, actionExecutorName, reason, remark);
                            if (count != 1)
                            {
                                msg = firstSix + "******" + lastFour + " is already blocked";
                            }
                            else
                            {
                                msg = firstSix + "******" + lastFour + " is Blocked successfully";
                            }
                        }
                        listOfCard = blacklistDAO.getBlackListedCardsPages(firstSix, lastFour, actionExecutorId, actionExecutorName, remark, paginationVO, reason);
                    }
                }
            }
            paginationVO.setPage(BlackListDetails.class.getName());
            paginationVO.setInputs("&firstsix=" + firstSix + "&lastfour=" + lastFour + "&remark=" + remark);
            log.error("Remark -> " + remark);
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(req.getParameter("currentblock"), 1));

            listOfCard = blacklistDAO.getBlackListedCardsPages(firstSix, lastFour,actionExecutorId,actionExecutorName,remark, paginationVO,reason);
            log.error("listOfCards -> " + listOfCard);


            if("unblock".equalsIgnoreCase(action))
            {
                String[] ids=req.getParameterValues("id");
                for(String id : ids)
                {
                    blacklistDAO.deleteBlockedCard(id);
                }
                error = "<center><font class=\"textb\"><b> Cards Unblocked successfully<br></b></font></center>";
                listOfCard = blacklistDAO.getBlackListedCardsPages(firstSix,lastFour,actionExecutorId,actionExecutorName,remark,paginationVO,reason);
                log.error("listOfCards -> " + listOfCard);
            }
        }
        catch (PZDBViolationException dbe)
        {
            log.debug("db exception---"+dbe);
            PZExceptionHandler.handleDBCVEException(dbe,"","Fetching Blacklisted Cards");
        }
        req.setAttribute("paginationVO", paginationVO);
        req.setAttribute("error", error);
        req.setAttribute("msg",msg);
        req.setAttribute("listofcards",listOfCard);

        RequestDispatcher rd = req.getRequestDispatcher("/blockedCardList.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }

    private String validateParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        //String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.FIRST_SIX);
        inputFieldsListOptional.add(InputFields.LAST_FOUR);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req, inputFieldsListOptional, errorList, true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage();
                }
            }
        }
        return error;
    }
    private String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields>  inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.PAGENO);
        inputFieldsListOptional.add(InputFields.RECORDS);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req, inputFieldsListOptional, errorList, true);

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

