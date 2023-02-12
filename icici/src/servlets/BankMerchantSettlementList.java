import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.BankManager;
import com.manager.GatewayManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.BankMerchantSettlementVO;
import com.manager.vo.InputDateVO;
import com.manager.vo.MerchantDetailsVO;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 9/10/14
 * Time: 3:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class BankMerchantSettlementList extends HttpServlet
{
    private static Logger logger = new Logger(BankMerchantSettlementList.class.getName());
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
        //merchantDao instance
        MerchantDAO merchantDAO = new MerchantDAO();
        //Vo initialization
        InputDateVO inputDateVO = new InputDateVO();
        PaginationVO paginationVO = new PaginationVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        GatewayAccountVO gatewayAccountVO = new GatewayAccountVO();
        GatewayTypeVO gatewayTypeVO = new GatewayTypeVO();

        //List of BankMerchantSettlementList Declaration
        List<BankMerchantSettlementVO> bankMerchantSettlementVOList= null;



        try{

            ValidationErrorList validationErrorList=validateOptionalParameter(request);
            //ValidationErrorList validationErrorList1=validateMandatoryParameter(request);

            RequestDispatcher rdError = request.getRequestDispatcher("/bankMerchantSettlementMaster.jsp?MES=ERR&ctoken=" + user.getCSRFToken());
            RequestDispatcher rdSuccess = request.getRequestDispatcher("/bankMerchantSettlementMaster.jsp?MES=Success&ctoken="+user.getCSRFToken());
            if(!validationErrorList.isEmpty())
            {
                request.setAttribute("error",validationErrorList);
                rdError.forward(request, response);
                return;
            }
            /*if(!validationErrorList1.isEmpty())
            {
                request.setAttribute("error",validationErrorList1);
                rdError.forward(request,response);
                return;
            }*/

            //accountId and pgTypeId
            String accountId2 = request.getParameter("accountid");
            String merchantId= "";

            //System.out.println("memberid in list"+request.getParameter("toid"));

            if(!request.getParameter("toid").equals("0"))
            {
                merchantId= request.getParameter("toid");
                request.setAttribute("toid",merchantId);
            }



            //new pagination
            String accountId = "";
            if(!("0").equals(request.getParameter("accountid")))
            {
                accountId= request.getParameter("accountid");
                request.setAttribute("accountid",accountId);
            }

            String pgtypeId=functions.splitGatewaySet(request.getParameter("pgtypeid"));
            if(!pgtypeId.equals("0"))
            {
                request.setAttribute("pgtypeid",pgtypeId);
            }
            else {
                pgtypeId="";
            }
            String currency = request.getParameter("currency");

            //inserting merchant details
            if(functions.isValueNull(merchantId))
            {
                merchantDetailsVO=merchantDAO.getMemberDetails(merchantId);
            }
            //inserting gateway Details
            //System.out.println("accountid----" + accountId);

            if(functions.isValueNull(accountId) && !accountId.equalsIgnoreCase("0"))
            {
                gatewayAccountVO=gatewayManager.getGatewayAccountForAccountId(accountId);
            }
            //System.out.println("accountid2----" + gatewayAccountVO.getAccountId());
            //inserting the values of Pagination
            paginationVO.setInputs("accountid="+accountId+"&toid="+merchantId+"&pgtypeid="+ pgtypeId+"&currency="+currency);
            paginationVO.setPageNo(Functions.convertStringtoInt(request.getParameter("SPageno"), 1));
            paginationVO.setPage(BankMerchantSettlementList.class.getName());
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(request.getParameter("currentblock"), 1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(request.getParameter("SRecords"), 15));
            gatewayTypeVO.setPgTYypeId(pgtypeId);



            //account manager
            bankMerchantSettlementVOList=bankManager.getBankMerchantSettlementList(inputDateVO, paginationVO, gatewayAccountVO, merchantDetailsVO,gatewayTypeVO);

            //response
            request.setAttribute("paginationVO",paginationVO);
            request.setAttribute("BankMerchantSettlementVOs",bankMerchantSettlementVOList);
            rdSuccess.forward(request,response);

        }
        catch (Exception e)
        {
            logger.error("Main class exception::", e);
        }
    }

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {

    }
    private ValidationErrorList validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.ACCOUNTID_SMALL);
        inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.ACCOUNTID_SMALL);
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);
        ValidationErrorList validationErrorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListMandatory,validationErrorList,true);
        return validationErrorList;
    }
    /*private ValidationErrorList validateMandatoryParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.TOID);
        ValidationErrorList validationErrorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListMandatory,validationErrorList,false);
        return validationErrorList;
    }*/
}