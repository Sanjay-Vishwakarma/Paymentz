<%@ page import="com.directi.pg.SystemError" %>
<%@ page import="com.aciworldwide.commerce.gateway.plugins.NotEnoughDataException" %>
<%@ page import="com.aciworldwide.commerce.gateway.plugins.e24TranPipe" %>
<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ page import="com.directi.pg.*" %>
<%@ page import="com.directi.pg.core.*" %>
<%@ page import="com.directi.pg.core.handler.ACIKitHandler" %>
<%@ page import="com.directi.pg.core.paymentgateway.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.logicboxes.util.Util" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.*" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2//EN">
<%@ page language="java" session="false" isErrorPage="false" %>


<HTML>

<HEAD>
    <META HTTP-EQUIV="Content-Type" CONTENT="text/html;CHARSET=iso-8859-1">
    <TITLE>Settle TRANSACTION </TITLE>
</HEAD>

<BODY bgcolor='#83a1C6'>

<%
out.println("Transactions are being settled ......");
int accountId = Integer.parseInt(request.getParameter("accountid"));
try
{
settleSBMTransactions(accountId,out);
}
catch(Exception e )
{
	out.println(Util.getStackTrace(e));
}

out.println("Transactions are settled");


%>

<%!

static Logger log = new Logger("settle.jsp");
static Hashtable currency_code = new Hashtable();

private static void settleSBMTransactions(int accountId,JspWriter out)
        throws Exception
    {
        List allSBMAccounts = getSBMAccounts();
        GatewayAccount account;
		Iterator it = allSBMAccounts.iterator();

        while(it.hasNext())
		{			
			account = (GatewayAccount)it.next();
			if(account.getAccountId() == accountId)			
			{
				settleTransactions(account,out);
				break;
			}
		}

    }

private static List getSBMAccounts()
    {
        List allAccounts = AbstractPaymentGateway.getAccounts();
        List allSBMAccounts = new ArrayList();
        Iterator i$ = allAccounts.iterator();
        do
        {
            if(!i$.hasNext())
                break;
            GatewayAccount account = (GatewayAccount)i$.next();
            String gatewayType = account.getGateway();
            if("sbm".equals(gatewayType))
                allSBMAccounts.add(account);
        } while(true);
        return allSBMAccounts;
    }

private static void settleTransactions(GatewayAccount account,JspWriter out)
        throws SystemError,java.io.IOException,SQLException
    {
		if(account == null)
			return;

        TransactionEntry transactionEntry;
        java.sql.Connection conn;
        String merchantId;
        int accountId;
        transactionEntry = new TransactionEntry();
        conn = null;
        merchantId = account.getMerchantId();
        accountId = account.getAccountId();
        try
        {
            conn = Database.getConnection();
            String query = (new StringBuilder()).append("select T.toid,T.description,T.amount,T.chargeper,T.fixamount,T.taxper,T.icicitransid,T.accountid from transaction_icicicredit T where T.status = 'podsent' and T.accountid =").append(accountId).toString();
            ResultSet rs = Database.executeQuery(query, conn);
            do
            {
                if(!rs.next())
                    break;
                int toid = rs.getInt("toid");
                String description = rs.getString("description");
                BigDecimal amount = new BigDecimal(rs.getString("amount"));
                amount.setScale(2, 4);
                String chargeper = rs.getString("chargeper");
                BigDecimal chargePer = new BigDecimal(chargeper);
                String transFixFee = rs.getString("fixamount");
                String taxPer = rs.getString("taxper");
                int trackingId = rs.getInt("icicitransid");
                AbstractPaymentGateway pg = AbstractPaymentGateway.getGateway(String.valueOf(accountId));
                Hashtable statusDetails = pg.getStatus(String.valueOf(trackingId));
              //  Hashtable statusDetails = getStatus(String.valueOf(trackingId),String.valueOf(accountId),out);
                String status = (String)statusDetails.get("status");
				out.println("<BR>");
				out.println("Tracking id ="+trackingId + " Status = " +status);
				out.println("<BR><BR>");
                if("APPROVED".equalsIgnoreCase(status))
                {
//                    log.info("Transaction captured at gateway so settling the transaction");
                    int transId = transactionEntry.creditTransaction(toid, merchantId, description, amount, chargePer, transFixFee, account, taxPer, trackingId);
                } else
                if("voided".equalsIgnoreCase(status))
                {
  //                  log.info("VOIDED on SBM so update details ");
                    query = (new StringBuilder()).append("update transaction_icicicredit set status='authcancelled',captureresult='captured transaction voided on gateway' where icicitransid=").append(trackingId).append(" and status='podsent'").toString();
                }
            } while(true);
        }
        catch(SQLException e)
        {
            throw new SystemError((new StringBuilder()).append("Error :  ").append(Util.getStackTrace(e)).toString());
        }
		finally
		{
        Database.closeConnection(conn);
		}
    
    }

	public static String getResourcePath(String aliasName)
    {
	    String RESOURCE_PATH = ApplicationProperties.getProperty("RESOURCE_PATH");
		String FILE_SEPARATOR = System.getProperty("file.separator");
		if(FILE_SEPARATOR != null && !FILE_SEPARATOR.equals(RESOURCE_PATH.substring(RESOURCE_PATH.length() - 1, RESOURCE_PATH.length())))
            RESOURCE_PATH = (new StringBuilder()).append(RESOURCE_PATH).append(FILE_SEPARATOR).toString();
        log.info((new StringBuilder()).append("Returning resourse path as ").append(RESOURCE_PATH).append(aliasName).append(FILE_SEPARATOR).toString());
        return (new StringBuilder()).append(RESOURCE_PATH).append(aliasName).append(FILE_SEPARATOR).toString();
    }

public static String getCurrencyCode(String cur)
        throws SystemError,SQLException
    {
		
		String currCode = (String)currency_code.get(cur);
	if(currCode == null)
		{
		Connection conn = null;
		try		
			{
				
				conn = Database.getConnection();
				String currency;
				String curr_code;
				for(ResultSet rs = Database.executeQuery("select * from currency_code ", conn); rs.next(); currency_code.put(currency, curr_code))
				{
					currency = rs.getString("currency");
					curr_code = rs.getString("currencycode");
				}
			
			return currCode;
			}
			finally
			{
				 if(conn != null)
					conn.close();
			}
		}

		return (String)currency_code.get(cur);
    }

public static Hashtable getStatus(String trackingID, String accountId,JspWriter out)
        throws SystemError,java.io.IOException,SQLException
    {
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        e24TranPipe e24tp = new e24TranPipe();
        e24tp.setAlias(account.getAliasName());
        e24tp.setResourcePath(getResourcePath(account.getAliasName()));
        e24tp.setAction("8");
        e24tp.setTrackId(trackingID);
        e24tp.setCurrencyCode(getCurrencyCode(account.getCurrency()));
        String id = null;
        String code = null;
        String receiptNo = null;
        String qsiResponseCode = null;
        String qsiResponseDesc = null;
        String status = null;
        int result;
        try
        {
            e24tp.setDebug(true);
            result = e24tp.performTransaction();
        }
        catch(NotEnoughDataException e)
        {
            log.info("calling SendMAil for  as Authentication Error");
            Mail.sendAdminMail((new StringBuilder()).append("Error while authentication for trackingID-").append(trackingID).toString(), e.getMessage());
            log.info("called SendMAil for ");
            throw new SystemError("There was an Error while posting data to bank. Please contact your merchant.");
        }
//        writeToFile(e24tp.getDebugMsg(), request_log_path);
		log.debug("Result code "+ result);
        if(result == -1)
        {
            id = "";
            code = "";
            receiptNo = "";
            qsiResponseCode = e24tp.getError();
            qsiResponseDesc = e24tp.getErrorText();
            status = "Error";
        } else
        if(result == 0)
        {
            id = e24tp.getTransId();
            code = e24tp.getAuth();
            receiptNo = e24tp.getRef();
            qsiResponseDesc = e24tp.getResult();
            qsiResponseCode = e24tp.getResponseCode();
            status = qsiResponseDesc;
            if("00".equals(qsiResponseCode))
                qsiResponseCode = "0";
            log.debug((new StringBuilder()).append("id=").append(id).toString());
            log.debug((new StringBuilder()).append("code=").append(code).toString());
            log.debug((new StringBuilder()).append("receiptNo=").append(receiptNo).toString());
            log.debug((new StringBuilder()).append("qsiResponseDesc =").append(qsiResponseDesc).toString());
            log.debug((new StringBuilder()).append("qsiResponseCode =").append(qsiResponseCode).toString());
        } else
        {
            log.info((new StringBuilder()).append("Tracking ID we have=").append(trackingID).append(" tracking id we got=").append(e24tp.getTrackId()).toString());
            throw new SystemError("There was an Error while gettinfg Details. Please contact your System Administrator");
        }
        out.println((new StringBuilder()).append("Debug : ").append(e24tp.getDebugMsg()).toString());
		out.println("<BR><BR>");
        return processHashForGetDetails(trackingID, status, id, code, receiptNo, qsiResponseCode, qsiResponseDesc);
    }

 public static Hashtable processHashForGetDetails(String trackingId, String status, String id, String code, String receiptNo, String qsiResponseCode, String qsiResponseDesc)
    {
        Hashtable returnHash = new Hashtable();
        returnHash.put("id", id);
        returnHash.put("status", status);
        returnHash.put("trackingid", trackingId);
        if(Functions.parseData(code) != null)
            returnHash.put("code", code);
        else
            returnHash.put("code", "Could not retrive");
        if(Functions.parseData(receiptNo) != null)
            returnHash.put("receiptno", receiptNo);
        else
            returnHash.put("receiptno", "Could not retrive");
        if(Functions.parseData(qsiResponseCode) != null)
            returnHash.put("qsiresponsecode", qsiResponseCode);
        else
            returnHash.put("qsiresponsecode", "Could not retrive");
        if(Functions.parseData(qsiResponseDesc) != null)
            returnHash.put("qsiresponsedesc", qsiResponseDesc);
        else
            returnHash.put("qsiresponsedesc", "Could not retrive");
        return returnHash;
    }

%>
</BODY>
</HTML>
