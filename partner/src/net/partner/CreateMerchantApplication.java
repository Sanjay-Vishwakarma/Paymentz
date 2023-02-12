package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ApplicationManager;
import com.dao.ApplicationManagerDAO;
import com.manager.vo.PaginationVO;
import com.vo.applicationManagerVOs.ApplicationManagerVO;

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
 * Created with IntelliJ IDEA.
 * User: kajal
 * Date: 3/30/15
 * Time: 2:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreateMerchantApplication  extends HttpServlet
{
    private static Logger logger = new Logger(CreateMerchantApplication.class.getName());
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {

        HttpSession session = request.getSession();


        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            logger.debug("Partner is logout ");
            response.sendRedirect("/partner/logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        Functions functions =new Functions();
        ValidationErrorList validationErrorList=null;
        //merchantDao instance
        ApplicationManager applicationManager=new ApplicationManager();
        ApplicationManagerDAO applicationManagerDAO=new ApplicationManagerDAO();
        //Vo initialization
        PaginationVO paginationVO = new PaginationVO();
        //List of ApplicationManagerList Declaration
        List<ApplicationManagerVO> applicationManagerVOList=null;
        RequestDispatcher rdError = request.getRequestDispatcher("/createmerchantapplication.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSuccessC = request.getRequestDispatcher("/createmerchantapplication.jsp?MES=Success&ctoken="+user.getCSRFToken());
        try{

            logger.debug("CURRENTBLOCK:::"+request.getParameter("currentblock"));

            validationErrorList=validateOptionalParameter(request);
            if(!validationErrorList.isEmpty())
            {
                request.setAttribute("error",validationErrorList);
                rdError.forward(request,response);
                return;
            }
            session.setAttribute("applicationManagerVO",null);
            session.setAttribute("navigationVO",null);
            //memberID and ApplicationId
            String memberId=null;
            String pid=request.getParameter("partnerid");

            if(null!=request.getParameter("apptoid") && !"".equals(request.getParameter("apptoid")))
            {
                memberId= request.getParameter("apptoid");
                String applicationId = request.getParameter("application_id");
                paginationVO.setPageNo(Functions.convertStringtoInt(request.getParameter("SPageno"), 1));
                paginationVO.setPage(CreateMerchantApplication.class.getName());
                paginationVO.setCurrentBlock(Functions.convertStringtoInt(request.getParameter("currentblock"), 1));
                paginationVO.setRecordsPerPage(Functions.convertStringtoInt(request.getParameter("SRecords"),15));
                paginationVO.setInputs("memberId="+memberId+"&applicationId="+applicationId);
                applicationManagerVOList=applicationManager.getPartnersNewSpecificMerchantApplicationManagerVO(memberId, (String) session.getAttribute("merchantid"),paginationVO);
            }
            else
            {
                if(functions.isValueNull(pid)){
                    memberId = request.getParameter("partnerid");//this is partnerid
                    paginationVO.setPageNo(Functions.convertStringtoInt(request.getParameter("SPageno"), 1));
                    paginationVO.setPage(CreateMerchantApplication.class.getName());
                    paginationVO.setCurrentBlock(Functions.convertStringtoInt(request.getParameter("currentblock"), 1));
                    paginationVO.setRecordsPerPage(Functions.convertStringtoInt(request.getParameter("SRecords"), 15));
                    paginationVO.setInputs("memberId=" + memberId);
                    applicationManagerVOList = applicationManager.getPartnersNewMerchantApplicationManagerVO1(memberId, paginationVO);
                }else
                {
                    memberId = (String) session.getAttribute("merchantid");//this is partnerid
                    paginationVO.setPageNo(Functions.convertStringtoInt(request.getParameter("SPageno"), 1));
                    paginationVO.setPage(CreateMerchantApplication.class.getName());
                    paginationVO.setCurrentBlock(Functions.convertStringtoInt(request.getParameter("currentblock"), 1));
                    paginationVO.setRecordsPerPage(Functions.convertStringtoInt(request.getParameter("SRecords"), 15));
                    paginationVO.setInputs("memberId=" + memberId);
                    applicationManagerVOList = applicationManager.getSuperPartnersNewMerchantApplicationManagerVO1(memberId, paginationVO);
                }

            }
/*

            //inserting MemberID & ApplicationID details
            applicationManagerVOList=applicationManager.getPartnersNewMerchantApplicationManagerVO(memberId);*/
            //response
            request.setAttribute("pid",pid);
            request.setAttribute("apptoid",request.getParameter("apptoid"));
            request.setAttribute("paginationVO",paginationVO);
            request.setAttribute("applicationManagerVOs",applicationManagerVOList);
            rdSuccessC.forward(request,response);

        }
        catch (Exception e)
        {
            logger.error("Main class exception::", e);
        }
    }

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {

    }
    private ValidationErrorList validateOptionalParameter(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.APPTOID);
        inputFieldsListMandatory.add(InputFields.APPLICATIONID);
        ValidationErrorList validationErrorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListMandatory,validationErrorList,true);
        return validationErrorList;
    }

}
