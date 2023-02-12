import com.directi.pg.*;
import com.directi.pg.core.GatewayType;
import com.manager.GatewayManager;
import com.manager.enums.ActivityLogParameters;
import com.manager.vo.ActivityTrackerVOs;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 3/2/14
 * Time: 8:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class editGatewayTypeDetails extends HttpServlet
{
    static Logger log = new Logger(editGatewayTypeDetails.class.getName());
    ResourceBundle rb=LoadProperties.getProperty("com.directi.pg.3CharCountryList");
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        Functions functions=new Functions();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        boolean flag = true;
        String EOL = "<BR>";
        String errormsg = "<center><font class=\"textb\"><b>"+"Following information are incorrect:-"+EOL+"</b></font></center>";
        ActivityTrackerVOs activityTrackerVOs = new ActivityTrackerVOs();

        String pgtypeid="";
        String gateway = "";
        String currency ="";
        String name ="";
        String action;
        Integer chargepercentage = 0;
        Integer taxpercentage = 0;
        Integer withdrawalcharge = 0;
        Integer reversalcharge = 0;
        Integer chargebackcharge= 0;
        Integer chargesaccount= 0;
        Integer taxaccount = 0;
        Integer highriskamount = 0;
        String addmessage = "";
        String address = "";
        String gateway_table_name = "";
        String timedifferencenormal = "";
        String timedifferencedaylight = "";
        String agentid = "";
        String partnerid = "";
        String bankIp = "";
        String pspcode ="";
        String key ="";
        String wsservice ="";
        String wspassword ="";
        String bank_emailid="";
        String excessCapturePercentage="";
        String isCvvOptional="";
        String country=req.getParameter("country");
        String gatewayCountry="";
        String limitresettime="";
        String Login=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String onchangedValues = req.getParameter("onchangedvalue");


        try
        {
            if(functions.isValueNull(country)){
                String arr[]=country.split("\\|");
                gatewayCountry=rb.getString(arr[0]);
            }else{
                log.debug("enter valid Country");
                errormsg = "<center><font class=\"textb\"><b>"+errormsg + "Please Enter valid Country." + EOL +"</b></font></center>";
                flag = false;
            }
            pgtypeid = ESAPI.validator().getValidInput("pgtypeid",req.getParameter("pgtypeid"),"Numbers",10,false);
            action = ESAPI.validator().getValidInput("action",req.getParameter("action"),"SafeString",30,false);
            if(!ESAPI.validator().isValidInput("gateway",(String) req.getParameter("gateway"),"alphanum",100,false))
            {
                log.debug("enter valid Gateway");
                errormsg = "<center><font class=\"textb\"><b>"+errormsg + "Please Enter valid Gateway." + EOL +"</b></font></center>";
                flag = false;
            }
            else
            {
                gateway = req.getParameter("gateway");
            }
            if (!ESAPI.validator().isValidInput("currency",(String) req.getParameter("currency"),"Description",3,false))
            {
                log.debug("Enter valid Currency");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Please enter valid  Currency."+EOL +"</b></font></center>";
                flag = false;
            }
            else
            {
                currency = req.getParameter("currency");
            }
            if (!(ESAPI.validator().isValidInput("isCvvOptional",(String) req.getParameter("isCvvOptional"),"SafeString",1,false)))
            {
                log.debug("Invalid Invalid Is Cvv Optional.");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid Is Cvv Optional."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                isCvvOptional = req.getParameter("isCvvOptional");
            }
            if (!ESAPI.validator().isValidInput("name",(String) req.getParameter("name"),"contactName",100,false))
            {   log.debug("PLS enter Name ");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Please Enter valid name. "+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                name = req.getParameter("name");
            }
            if (!ESAPI.validator().isValidInput("chargepercentage",(String) req.getParameter("chargepercentage"),"OnlyNumber",10,false))
            {
                log.debug("PLS enter valid chargepercentage ");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Please Enter valid chargepercentage."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                if(!req.getParameter("chargepercentage").equalsIgnoreCase("null"))
                    chargepercentage = Integer.parseInt(req.getParameter("chargepercentage"));
            }
            if (!ESAPI.validator().isValidInput("taxpercentage",(String) req.getParameter("taxpercentage"),"OnlyNumber",10,false))
            {
                log.debug("PLS enter velid taxpercentage ");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Please Enter valid taxpercentage."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                if(!req.getParameter("taxpercentage").equalsIgnoreCase("null"))
                    taxpercentage = Integer.parseInt(req.getParameter("taxpercentage"));
            }
            if (!ESAPI.validator().isValidInput("withdrawalcharge",(String) req.getParameter("withdrawalcharge"),"OnlyNumber",10,false))
            {
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Please enter Valid withdrawalcharge."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                if(!req.getParameter("withdrawalcharge").equalsIgnoreCase("null"))
                    withdrawalcharge = Integer.parseInt(req.getParameter("withdrawalcharge"));
            }

            if (!ESAPI.validator().isValidInput("reversalcharge",(String) req.getParameter("reversalcharge"),"OnlyNumber",10,false))
            {
                log.debug("PLS enter valid reversalcharge ");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Please enter valid reversalcharge."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                if(!req.getParameter("reversalcharge").equalsIgnoreCase("null"))
                    reversalcharge = Integer.parseInt(req.getParameter("reversalcharge"));
            }
            if (!ESAPI.validator().isValidInput("chargebackcharge",(String) req.getParameter("chargebackcharge"),"OnlyNumber",10,false))
            {
                log.debug("PLS enter valid chargebackcharge ");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Please enter valid chargebackcharge."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                if(!req.getParameter("chargebackcharge").equalsIgnoreCase("null"))
                    chargebackcharge = Integer.parseInt(req.getParameter("chargebackcharge"));
            }
            if (!ESAPI.validator().isValidInput("chargesaccount",(String) req.getParameter("chargesaccount"),"OnlyNumber",10,false))
            {
                log.debug("PLS enter valid chargesaccount ");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Please enter valid chargesaccount."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                if(!req.getParameter("chargesaccount").equalsIgnoreCase("null"))
                    chargesaccount = Integer.parseInt(req.getParameter("chargesaccount"));
            }
            if (!ESAPI.validator().isValidInput("taxaccount",(String) req.getParameter("taxaccount"),"OnlyNumber",10,false))
            {
                log.debug("PLS enter valid taxaccount ");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Please enter valid taxaccount."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                if(!req.getParameter("taxaccount").equalsIgnoreCase("null"))
                    taxaccount = Integer.parseInt(req.getParameter("taxaccount"));
            }

            if (!ESAPI.validator().isValidInput("highriskamount",(String) req.getParameter("highriskamount"),"OnlyNumber",10,false))
            {
                log.debug("PLS enter valid highriskamount ");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Please enter valid highriskamount."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                if(!req.getParameter("highriskamount").equalsIgnoreCase("null"))
                    highriskamount = Integer.parseInt(req.getParameter("highriskamount"));
            }
            if (!ESAPI.validator().isValidInput("address",(String) req.getParameter("address"),"Description",100,false) && req.getParameter("address").length() > 0)
            {
                log.debug("PLS enter valid address ");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Please Enter valid Sales address."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                address = req.getParameter("address");
            }
            if (!ESAPI.validator().isValidInput("gateway_table_name",(String) req.getParameter("gateway_table_name"),"SafeString",100,true) && req.getParameter("gateway_table_name").length() > 0 && req.getParameter("gateway_table_name") != null)
            {
                log.debug("PLS enter valid gateway table name ");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Please Enter valid gateway table name."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                gateway_table_name = req.getParameter("gateway_table_name");
            }
            if (!ESAPI.validator().isValidInput("timedifferencenormal",(String) req.getParameter("timedifferencenormal"),"time",16,false) )
            {
                log.debug("Please Enter valid Time Difference Normal");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Please Enter valid Time Difference Normal."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                timedifferencenormal = req.getParameter("timedifferencenormal");
            }
            if (!ESAPI.validator().isValidInput("timedifferencedaylight",(String) req.getParameter("timedifferencedaylight"),"time",16,false) )
            {
                log.debug("Please Enter valid Time Difference DayLight ");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Please Enter valid Time Difference DayLight."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                timedifferencedaylight = req.getParameter("timedifferencedaylight");

            }
            /*if (!ESAPI.validator().isValidInput("agentid",req.getParameter("agentid"),"Numbers",10,false) )
            {
                log.debug("Please Enter valid Agent ID ");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Please Enter valid Agent ID."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                agentid = req.getParameter("agentid");

            }
            if (!ESAPI.validator().isValidInput("partnerid",req.getParameter("partnerid"),"Numbers",10,false) )
            {
                log.debug("Please Enter valid Partner ID ");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Please Enter valid Partner ID."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                partnerid = req.getParameter("partnerid");
            }*/

            if (!ESAPI.validator().isValidInput("bankip",req.getParameter("bankip"),"IPAddress",50,true) )
            {
                log.debug("Please Enter valid Bank IP Address ");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Please Enter valid Bank IP Address."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                bankIp = req.getParameter("bankip");
            }
            if(!ESAPI.validator().isValidInput("pspcode",req.getParameter("pspcode"),"alphanum",100,true))
            {
                log.debug("Please Enter valid PSP code");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Please Enter valid PSP Code."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                pspcode = req.getParameter("pspcode");
            }
            if(!ESAPI.validator().isValidInput("key",req.getParameter("key"),"Description",100,true))
            {
                log.debug("Please Enter valid Key");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Please Enter valid Key."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                key = req.getParameter("key");
            }
            if(!ESAPI.validator().isValidInput("wsservice",req.getParameter("wsservice"),"Description",50,true))
            {
                log.debug("Please Enter valid WS Service ");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Please Enter valid WS Service."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                wsservice = req.getParameter("wsservice");
            }
            if(!ESAPI.validator().isValidInput("wspassword",req.getParameter("wspassword"),"NewPassword",50,true))
            {
                log.debug("Please Enter valid WS Password");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Please Enter valid WS Password."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                wspassword = req.getParameter("wspassword");
            }

            if (!ESAPI.validator().isValidInput("bank_emailid",req.getParameter("bank_emailid"),"Email",50,true) )
            {
                log.debug("Please Enter valid Bank Email ID ");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Please Enter valid Bank Email ID."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                bank_emailid = req.getParameter("bank_emailid");
            }
            if (!ESAPI.validator().isValidInput("excessCapturePercentage",req.getParameter("excessCapturePercentage"),"AmountStr",20,false) )
            {
                log.debug("Please Enter valid Excess Capture Amount ");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Please Enter valid Capture Amount."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                excessCapturePercentage = req.getParameter("excessCapturePercentage");
            }


            if (!ESAPI.validator().isValidInput("limitresettime",(String) req.getParameter("limitresettime"),"time",16,false) )
            {
                log.debug("Please Enter valid Limit Reset Time ");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Please Enter valid Limit Reset Time."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                limitresettime = req.getParameter("limitresettime");

            }

        }
        catch (ValidationException e)
        {
            log.error("Exception :::::",e);
            flag = false;
        }

        if(flag)
        {
            GatewayType gatewayType=new GatewayType();
            gatewayType.setPgTypeId(pgtypeid);
            gatewayType.setGateway(gateway);
            gatewayType.setCurrency(currency);
            gatewayType.setName(name);
            gatewayType.setTableName(gateway_table_name);
            gatewayType.setChargePercentage(chargepercentage);
            gatewayType.setWithdrawalCharge(withdrawalcharge);
            gatewayType.setTaxPercentage(taxpercentage);
            gatewayType.setReversalCharge(reversalcharge);
            gatewayType.setChargebackCharge(chargebackcharge);
            gatewayType.setChargesAccount(chargesaccount);
            gatewayType.setTaxAccount(taxaccount);
            gatewayType.setHighRiskAmount(highriskamount);
            gatewayType.setAddress(address);
            gatewayType.setTime_difference_normal(timedifferencenormal);
            gatewayType.setTime_difference_daylight(timedifferencedaylight);
            /*gatewayType.setAgentId(agentid);
            gatewayType.setPartnerId(partnerid);*/
            gatewayType.setBank_ipaddress(bankIp);
            gatewayType.setPspCode(pspcode);
            gatewayType.setKey(key);
            gatewayType.setWsService(wsservice);
            gatewayType.setWsPassword(wspassword);
            gatewayType.setBank_emailid(bank_emailid);
            gatewayType.setExcessCapturePercentage(excessCapturePercentage);
            gatewayType.setIsCvvOptional(isCvvOptional);
            gatewayType.setLimitresettime(limitresettime);
            gatewayType.setCountry(gatewayCountry);
            GatewayManager gatewayManager=new GatewayManager();
            errormsg=gatewayManager.updateGatewayType(gatewayType);
            log.debug("Creating Activity for edit Gatway account");
            String remoteAddr = Functions.getIpAddress(req);
            int serverPort = req.getServerPort();
            String servletPath = req.getServletPath();
            String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
            if(functions.isValueNull(onchangedValues))
            {
                activityTrackerVOs.setInterface(ActivityLogParameters.ADMIN.toString());
                activityTrackerVOs.setUser_name(Login + "-" + actionExecutorId);
                activityTrackerVOs.setRole(ActivityLogParameters.ADMIN.toString());
                activityTrackerVOs.setAction(ActivityLogParameters.EDIT.toString());
                activityTrackerVOs.setModule_name(ActivityLogParameters.GATEWAY_MASTER.toString());
                activityTrackerVOs.setLable_values(onchangedValues);
                activityTrackerVOs.setDescription(ActivityLogParameters.PGTYPEID.toString() + "-" + pgtypeid);
                activityTrackerVOs.setIp(remoteAddr);
                activityTrackerVOs.setHeader(header);
                try
                {
                    AsyncActivityTracker asyncActivityTracker = AsyncActivityTracker.getInstance();
                    asyncActivityTracker.asyncActivity(activityTrackerVOs);
                }
                catch (Exception e)
                {
                    log.error("Exception while AsyncActivityLog::::", e);
                }
            }


            req.setAttribute("message",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/gatewayInterface.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        else
        {

            req.setAttribute("message",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/viewGatewayTypeDetails?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
    }
}