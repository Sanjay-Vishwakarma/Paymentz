package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ChargebackManager;
import com.manager.vo.InputDateVO;
import com.manager.vo.PaginationVO;
import com.manager.vo.TransactionVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
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
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Ajit on 28/11/2018.
 */
public class PartnerFraudNotification extends HttpServlet
{
        private static Logger log = new Logger(PartnerFraudNotification.class.getName());
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
        {
            doPost(request, response);
        }
        public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
        {
            HttpSession session                 = req.getSession();
            User user                           =  (User)session.getAttribute("ESAPIUserSessionKey");
            PartnerFunctions partnerFunctions   = new PartnerFunctions();
            RequestDispatcher rd                = req.getRequestDispatcher("/fraudNotification.jsp?ctoken="+user.getCSRFToken());
            if (!partnerFunctions.isLoggedInPartner(session))
            {
                res.sendRedirect("/partner/logout.jsp");
                return;
            }

            ChargebackManager chargebackManager = new ChargebackManager();
            TransactionVO transactionVO         = new TransactionVO();
            PaginationVO paginationVO           = new PaginationVO();
            InputDateVO inputDateVO             = new InputDateVO();
            List<TransactionVO> cbVOList        = null;
            Functions functions                 = new Functions();
            String error                        = "";
            String fdate      = null;
            String tdate      = null;
            String fmonth     = null;
            String tmonth     = null;
            String fyear      = null;
            String tyear      = null;
            String fdtstamp   = null;
            String tdtstamp   = null;

            try
            {
                error = error + validateOptionalParameters(req);
                if(!ESAPI.validator().isValidInput("STrackingid",req.getParameter("STrackingid"),"OnlyNumber",100,true))
                {
                    error   = error + "Invalid TrackingID."+"<BR>";
                }
                if(functions.isValueNull(error))
                {
                    PZExceptionHandler.raiseConstraintViolationException("FraudNotification.java", "doPost()", null, "icici", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, null, null);
                }

                String trackingId   = req.getParameter("STrackingid");
                String paymentId    = req.getParameter("paymentid");
                String firstsix     = req.getParameter("firstsix");
                String lastfour     = req.getParameter("lastfour");
                String emailAddr    = req.getParameter("emailaddr");
                String ipAddress    = req.getParameter("ipaddress");
                String name         = req.getParameter("name");
                String accountId    = req.getParameter("accountid");
                String paymodeId    = req.getParameter("paymodeid");
                String cardTypeId   = req.getParameter("cardtypeid");
                String status       = req.getParameter("status");
                String description  = req.getParameter("description");
                String firstName    = req.getParameter("firstname");
                String lastName     = req.getParameter("lastname");
                String country      = req.getParameter("country");
                String amount       = req.getParameter("amount");
                String pgtypeid     = req.getParameter("pgtypeid");
                String fromdate     = req.getParameter("fromdate");
                String todate       = req.getParameter("todate");
                String partnerid    = req.getParameter("partnerid");
                String currency     = "";
                String gateway      = "";
                String gateway_name = "";
                String memberId     = "";

                if (req.getParameter("pgtypeid")!=null && req.getParameter("pgtypeid").split("-").length == 3 && !req.getParameter("pgtypeid").equalsIgnoreCase(""))
                {
                    gateway         = req.getParameter("pgtypeid").split("-")[2];
                    currency        = req.getParameter("pgtypeid").split("-")[1];
                    gateway_name    = req.getParameter("pgtypeid").split("-")[0];
                }
                if (!(req.getParameter("pgtypeid").split("-").length == 3))
                {
                    gateway_name = req.getParameter("pgtypeid");
                }
                String toidList     = null;
                String pid          = req.getParameter("pid");
                String partnerId    = String.valueOf(session.getAttribute("partnerId"));
                String partner;
                if(functions.isValueNull(pid) && partnerFunctions.isPartnerSuperpartnerMapped(pid,partnerId)){
                    partner = pid;
                }else if(!functions.isValueNull(pid)){
                    partner = partnerId;
                }
                else
                {
                    error   = "Invalid Partner Mapping.";
                    req.setAttribute("error",error);
                    rd.forward(req, res);
                    return;
                }

                try
                {
                    if (functions.isValueNull(req.getParameter("toid")))
                    {
                        if(functions.isValueNull(pid)&& partnerFunctions.isPartnerMemberMapped(req.getParameter("toid"), pid)){
                            toidList = req.getParameter("toid");
                        }else if(!functions.isValueNull(pid) && partnerFunctions.isPartnerSuperpartnerMembersMapped(req.getParameter("toid"), partnerid)){
                            toidList = req.getParameter("toid");
                        }
                        else
                        {
                            error="Invalid Partner Member configuration.";
                            req.setAttribute("error",error);
                            rd.forward(req, res);
                            return;
                        }
                    }
                    else
                    {
                        if(functions.isValueNull(pid)){
                            toidList = partnerFunctions.getPartnerMemberRS(pid);
                        }else{
                            toidList = partnerFunctions.getSuperpartnersMemberRS(String.valueOf(session.getAttribute("partnerId")));
                        }
                    }
                }
                catch (Exception e)
                {
                    log.debug("Exception::::" + e);
                }

               /* if (!functions.isValueNull(gateway_name) && !functions.isValueNull(accountId) && !functions.isValueNull(memberId))
                {
                    req.setAttribute("errorMsg", "Kindly Provide Bank Name And Bank Account ID.");
                    rd.forward(req, res);
                    return;

                }
                if(!functions.isValueNull(gateway_name) && !functions.isValueNull(accountId) || !functions.isValueNull(gateway_name) && !functions.isValueNull(memberId) || !functions.isValueNull(memberId) && !functions.isValueNull(accountId))
                {
                    req.setAttribute("errorMsg", "Kindly provide the mandatory fields.");
                    rd.forward(req, res);
                    return;
                }*/

                transactionVO.setTrackingId(trackingId);
                transactionVO.setPaymentId(paymentId);
                transactionVO.setFirstSix(firstsix);
                transactionVO.setLastFour(lastfour);
                transactionVO.setEmailAddr(emailAddr);
                transactionVO.setIpAddress(ipAddress);
                transactionVO.setCustFirstName(firstName);
                transactionVO.setCustLastName(lastName);
                transactionVO.setToid(toidList);
                transactionVO.setAccountId(accountId);
                transactionVO.setPaymodeid(paymodeId);
                transactionVO.setCardTypeId(cardTypeId);
                transactionVO.setStatus(status);
                transactionVO.setOrderDesc(description);
                transactionVO.setCountry(country);
                transactionVO.setAmount(amount);
                transactionVO.setCurrency(currency);
                transactionVO.setName(name);
                transactionVO.setGatewayName(gateway_name);

                Calendar cal            = Calendar.getInstance();
                SimpleDateFormat sdf    = new SimpleDateFormat("dd/MM/yyyy");

                Date date= null;
                try
                {
                    if(functions.isFutureDateComparisonWithFromAndToDate(fromdate, todate, "dd/MM/yyyy"))
                    {
                        req.setAttribute("catchError","Invalid From & To date");
                        rd.forward(req, res);
                        return;
                    }

                    date    = sdf.parse(fromdate);
                    cal.setTime(date);
                    fdate   = String.valueOf(cal.get(Calendar.DATE));
                    fmonth  = String.valueOf(cal.get(Calendar.MONTH));
                    fyear   = String.valueOf(cal.get(Calendar.YEAR));

                    //to Date
                    date    = sdf.parse(todate);
                    cal.setTime(date);
                    tdate   = String.valueOf(cal.get(Calendar.DATE));
                    tmonth  = String.valueOf(cal.get(Calendar.MONTH));
                    tyear   = String.valueOf(cal.get(Calendar.YEAR));

                    log.debug("From date dd::" + fdate + " MM;;" + fmonth + " YY::" + fyear + " To date dd::" + tdate + " MM::" + tmonth + " YY::" + tyear);
                    fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
                    tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");
                }
                catch (Exception e)
                {
                    log.debug("Exception:::"+e);
                }

                inputDateVO.setFdtstamp(fdtstamp);
                inputDateVO.setTdtstamp(tdtstamp);


                paginationVO.setInputs("paymentid="+paymentId+"&STrackingid=" + transactionVO.getTrackingId()+"&firstsix="+transactionVO.getFirstSix() +"&lastfour="+transactionVO.getLastFour() +"&emailaddr="+transactionVO.getEmailAddr() +"&toid="+transactionVO.getToid() +"&accountid="+transactionVO.getAccountId() +"&status="+transactionVO.getStatus() +"&description="+transactionVO.getOrderDesc() +"&name="+transactionVO.getName()+"&pgtypeid="+pgtypeid +"&currency="+currency+ "&fromdate=" + fromdate + "&todate=" + todate);
                paginationVO.setPageNo(Functions.convertStringtoInt(req.getParameter("SPageno"), 1));
                paginationVO.setPage(PartnerFraudNotification.class.getName());
                paginationVO.setCurrentBlock(Functions.convertStringtoInt(req.getParameter("currentblock"),1));
                paginationVO.setRecordsPerPage(Functions.convertStringtoInt(req.getParameter("SRecords"),15));

                cbVOList = chargebackManager.getFraudActionWithPartner(transactionVO, toidList,inputDateVO,paginationVO);

                req.setAttribute("cbVO",cbVOList);
                req.setAttribute("paginationVO",paginationVO);
                req.setAttribute("error",error);

                rd.forward(req, res);
            }

            catch (PZConstraintViolationException cve)
            {

                log.error("PZConstraintViolationException:::::",cve);
                req.setAttribute("error",cve.getPzConstraint().getMessage());
                rd.forward(req, res);
                return;
            }
            catch (PZDBViolationException dbe)
            {
               
                log.error("PZDBViolationException:::::", dbe);
                req.setAttribute("error","Internal Error occurred : Please contact your Admin");
                rd.forward(req, res);
                return;
            }
        }
        private String validateOptionalParameters(HttpServletRequest req)
        {
            InputValidator inputValidator = new InputValidator();
            String error = "";
            String EOL = "<BR>";
            List<InputFields>  inputFieldsListOptional = new ArrayList<InputFields>();
            inputFieldsListOptional.add(InputFields.COMMASEPRATED_TRACKINGID_TRA);
            inputFieldsListOptional.add(InputFields.PAYMENTID);
            inputFieldsListOptional.add(InputFields.FIRST_SIX);
            inputFieldsListOptional.add(InputFields.LAST_FOUR);
            inputFieldsListOptional.add(InputFields.EMAILADDR);
            inputFieldsListOptional.add(InputFields.IPADDRESS);
            inputFieldsListOptional.add(InputFields.CUSTNAME);
            inputFieldsListOptional.add(InputFields.TOID);
            inputFieldsListOptional.add(InputFields.ACCOUNTID_SMALL);
            inputFieldsListOptional.add(InputFields.PAYMODE);
            inputFieldsListOptional.add(InputFields.CARDTYPE);
            inputFieldsListOptional.add(InputFields.DESC);
            inputFieldsListOptional.add(InputFields.ORDERDESC);
            inputFieldsListOptional.add(InputFields.FROMDATE);
            inputFieldsListOptional.add(InputFields.TODATE);
            inputFieldsListOptional.add(InputFields.FROMMONTH);
            inputFieldsListOptional.add(InputFields.TOMONTH);
            inputFieldsListOptional.add(InputFields.FROMYEAR);
            inputFieldsListOptional.add(InputFields.TOYEAR);
            inputFieldsListOptional.add(InputFields.STARTTIME);
            inputFieldsListOptional.add(InputFields.ENDTIME);
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
