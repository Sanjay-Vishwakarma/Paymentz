package com.manager.dao;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.logicboxes.error.SqlException;
import com.manager.vo.FileDetailsVO;
import com.manager.vo.gatewayVOs.GatewayTypeVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;

import java.sql.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 2/4/15
 * Time: 5:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class GatewayTypeDAO
{
    private Logger logger=new Logger(GatewayTypeDAO.class.getName());
    private Functions functions = new Functions();

    public boolean isGatewayTypeAvailable(GatewayType gatewayType)
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        boolean status=false;
        try
        {
            conn= Database.getRDBConnection();
            String selectQuery = "select gateway,currency from gateway_type where gateway = ? and currency = ?";
            pstmt = conn.prepareStatement(selectQuery);
            pstmt.setString(1,gatewayType.getGateway());
            pstmt.setString(2,gatewayType.getCurrency());
            rs = pstmt.executeQuery();
            if(rs.next())
            {
                status=true;
            }

        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::"+systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::"+e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }

        return status;
    }
    public String addNewGatewayType(GatewayType gatewayType)
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        String result = "";
        Functions functions=new Functions();
        try
        {
            conn = Database.getConnection();
            String query = "Insert into gateway_type(gateway,currency,name,chargepercentage,taxpercentage,withdrawalcharge,reversalcharge,chargebackcharge,chargesaccount,taxaccount,highriskamount,address,gateway_table_name,time_difference_normal,time_difference_daylight,partnerid,agentid,bank_ipaddress,pspcode,`key`,wsservice,wspassword,bank_emailid,isCvvOptional,actionExecutorId,actionExecutorName,country)" + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, gatewayType.getGateway());
            pstmt.setString(2, gatewayType.getCurrency());
            pstmt.setString(3, gatewayType.getName());
            pstmt.setInt(4, gatewayType.getChargebackCharge());
            pstmt.setInt(5, gatewayType.getTaxPercentage());
            pstmt.setInt(6, gatewayType.getWithdrawalCharge());
            pstmt.setInt(7, gatewayType.getReversalCharge());
            pstmt.setInt(8, gatewayType.getChargebackCharge());
            pstmt.setInt(9, gatewayType.getChargesAccount());
            pstmt.setInt(10, gatewayType.getTaxAccount());
            pstmt.setInt(11, gatewayType.getHighRiskAmount());
            if (functions.isValueNull(gatewayType.getAddress()))
                pstmt.setString(12, gatewayType.getAddress());
            else
                pstmt.setNull(12, Types.VARCHAR);
            if (functions.isValueNull(gatewayType.getTableName()))
            {
                pstmt.setString(13, gatewayType.getTableName());
            }
            else
            {
                pstmt.setNull(13, Types.VARCHAR);
            }
            pstmt.setString(14, gatewayType.getTime_difference_normal());
            pstmt.setString(15, gatewayType.getTime_difference_normal());
            pstmt.setString(16, gatewayType.getPartnerId());
            pstmt.setString(17, gatewayType.getAgentId());
            pstmt.setString(18, gatewayType.getBank_ipaddress());
            pstmt.setString(19, gatewayType.getPspCode());
            pstmt.setString(20, gatewayType.getKey());
            pstmt.setString(21, gatewayType.getWsService());
            pstmt.setString(22, gatewayType.getWsPassword());
            pstmt.setString(23,gatewayType.getBank_emailid());
            pstmt.setString(24,gatewayType.getIsCvvOptional());
            pstmt.setString(25,gatewayType.getActionExecutorId());
            pstmt.setString(26,gatewayType.getActionExecutorName());
            pstmt.setString(27,gatewayType.getCountry());
            int i = pstmt.executeUpdate();
            if (i > 0)
            {
                GatewayTypeService.loadGatewayTypes();

                ResultSet rs = pstmt.getGeneratedKeys();
                rs.next();
                int pgtypeid = rs.getInt(1);

                String query1 = "insert into gatewaytype_partner_mapping(id,pgtypeid,partnerid,creation_date)VALUES (null,?,?,unix_timestamp(now()))";
                pstmt = conn.prepareStatement(query1);
                pstmt.setInt(1, pgtypeid);
                pstmt.setString(2,gatewayType.getPartnerId());
                int k = pstmt.executeUpdate();

                String query2 = "insert into gatewaytype_agent_mapping(id,pgtypeid,agentid,creation_date)VALUES (null,?,?,unix_timestamp(now()))";
                pstmt = conn.prepareStatement(query2);
                pstmt.setInt(1,pgtypeid);
                pstmt.setString(2,gatewayType.getAgentId());
                int j = pstmt.executeUpdate();
                if (k>0 & j > 0)
                {
                    result = "New Bank Configured Successfully";
                }
                else
                {
                    result = "New Bank Configuration Failed";
                }
            }
            else
            {
                result="New Bank Configuration Failed";
            }
        }
        catch (SqlException ee){
            logger.error("Exceptoin : ",ee);
            result = "Error : " + ee.getMessage();
        }
        catch (Exception e){
            logger.error("Exceptoin : ",e);
            result = e.getMessage();
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return result;
    }
    public String updateGatewayType(GatewayType gatewayType)
    {
        Functions functions=new Functions();
        String errorMessage="";
        Connection conn = null;
        String query = "UPDATE gateway_type SET gateway=?,currency=?,name=?,chargepercentage=?,taxpercentage=?,withdrawalcharge=?,reversalcharge=?,chargebackcharge=?,chargesaccount=?,taxaccount=?,highriskamount=?,address=?,gateway_table_name=?,time_difference_normal=?,time_difference_daylight =?,bank_ipaddress=?,pspcode=?,`key`=?,wsservice=?,wspassword=?,bank_emailid=?,excessCapturePercentage=?,isCvvOptional=?,country=?, limitresettime=? WHERE pgtypeid=?";
        try
        {
            conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, gatewayType.getGateway());
            ps.setString(2, gatewayType.getCurrency());
            ps.setString(3, gatewayType.getName());
            ps.setInt(4,gatewayType.getChargePercentage());
            ps.setInt(5,gatewayType.getTaxPercentage());
            ps.setInt(6,gatewayType.getWithdrawalCharge());
            ps.setInt(7,gatewayType.getReversalCharge());
            ps.setInt(8, gatewayType.getChargebackCharge());
            ps.setInt(9, gatewayType.getChargesAccount());
            ps.setInt(10, gatewayType.getTaxAccount());
            ps.setInt(11, gatewayType.getHighRiskAmount());
            ps.setString(12,gatewayType.getAddress());
            if(functions.isValueNull(gatewayType.getTableName()))
                ps.setString(13,gatewayType.getTableName());
            else
                ps.setNull(13, Types.VARCHAR);
            ps.setString(14,gatewayType.getTime_difference_normal());
            ps.setString(15,gatewayType.getTime_difference_daylight());
           /* ps.setString(16,gatewayType.getAgentId());
            ps.setString(17,gatewayType.getPartnerId());*/
            ps.setString(16,gatewayType.getBank_ipaddress());
            ps.setString(17,gatewayType.getPspCode());
            ps.setString(18,gatewayType.getKey());
            ps.setString(19,gatewayType.getWsService());
            ps.setString(20,gatewayType.getWsPassword());
            ps.setString(21,gatewayType.getBank_emailid());
            ps.setString(22,gatewayType.getExcessCapturePercentage());
            ps.setString(23,gatewayType.getIsCvvOptional());
            ps.setString(24,gatewayType.getCountry());
            ps.setString(25,gatewayType.getLimitresettime());
            ps.setString(26,gatewayType.getPgTypeId());

            logger.error("query statement for limit reset time========>"+ps);
            int i =ps.executeUpdate();
            if(i!=0)
            {
                errorMessage="<font class=\"textb\">"+"<center>"+"<b>"+"Record Updated Successfully"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                GatewayTypeService.loadGatewayTypes();
            }
            else
            {
                errorMessage="<font class=\"textb\">"+"<center>"+"<b>"+"Update Failed"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Sql Exception while updating gatewaytype:::::",systemError);
            errorMessage="<font class=\"textb\">"+"<center>"+"<b>"+ systemError.getMessage() +"</center>"+"</font>"+"<BR><BR>";
        }
        catch (SQLException e)
        {
            logger.error("Exception while updating gatewaytype : " + e.getMessage());
            errorMessage="<font class=\"textb\">"+"<center>"+"<b>"+ e.getMessage() +"</center>"+"</font>"+"<BR><BR>";
        }
        catch (Exception e)
        {
            logger.error("Exception while updating gatewaytype ",e);
            errorMessage="<font class=\"textb\">"+"<center>"+"<b>"+ e.getMessage() +"</center>"+"</font>"+"<BR><BR>";
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return errorMessage;
    }
    public boolean isRefereedToBank(String agentId,String pgTypeId) throws SystemError,SQLException
    {
        Connection conn = null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        boolean isValid=false;
        try
        {
            conn = Database.getRDBConnection();
            String query="SELECT * FROM gateway_type WHERE agentid=? AND pgtypeid=? ";
            preparedStatement=conn.prepareStatement(query);
            preparedStatement.setString(1,agentId);
            preparedStatement.setString(2,pgTypeId);
            rs=preparedStatement.executeQuery();
            if(rs.next())
            {
                isValid=true;
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return isValid;
    }
    public boolean isPartnerRefereedToBank(String partnerId,String pgTypeId) throws SystemError,SQLException
    {
        Connection conn = null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        boolean isValid=false;
        try
        {
            conn = Database.getRDBConnection();
            String query="SELECT * FROM gateway_type WHERE partnerid=? AND pgtypeid=? ";
            preparedStatement=conn.prepareStatement(query);
            preparedStatement.setString(1,partnerId);
            preparedStatement.setString(2,pgTypeId);
            rs=preparedStatement.executeQuery();
            if(rs.next())
            {
                isValid=true;
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return isValid;
    }
    public boolean isRefereedToBankAnyBank(String agentId) throws SystemError,SQLException
    {
        Connection conn = null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        boolean isValid=false;
        try
        {
            conn = Database.getRDBConnection();
            String query="SELECT * FROM gateway_type WHERE agentid=? ";
            preparedStatement=conn.prepareStatement(query);
            preparedStatement.setString(1,agentId);
            rs=preparedStatement.executeQuery();
            if(rs.next())
            {
                isValid=true;
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return isValid;
    }
    public boolean isPartnerRefereedToAnyBank(String partnerId) throws SystemError,SQLException
    {
        Connection conn = null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        boolean isValid=false;
        try
        {
            conn = Database.getRDBConnection();
            String query="SELECT * FROM gateway_type WHERE partnerid=? ";
            preparedStatement=conn.prepareStatement(query);
            preparedStatement.setString(1,partnerId);
            rs=preparedStatement.executeQuery();
            if(rs.next())
            {
                isValid=true;
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return isValid;
    }
    public List<GatewayType> getGatewayTypeRefereedByAgent(String agentId)throws SystemError,SQLException
    {
        Connection conn = null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        List<GatewayType> gatewayTypeList=new ArrayList<GatewayType>();
        GatewayType gatewayType=null;
        try
        {
            conn = Database.getRDBConnection();
            String query="SELECT * FROM gateway_type WHERE agentid=? ";
            preparedStatement=conn.prepareStatement(query);
            preparedStatement.setString(1,agentId);
            rs=preparedStatement.executeQuery();
            while(rs.next())
            {
                gatewayType=new GatewayType(rs);
                gatewayTypeList.add(gatewayType);
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return gatewayTypeList;
    }
    public List<GatewayType> getGatewayTypeRefereedByPartner(String partnerId)throws SystemError,SQLException
    {
        Connection conn = null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        List<GatewayType> gatewayTypeList=new ArrayList<GatewayType>();
        GatewayType gatewayType=null;
        try
        {
            conn = Database.getRDBConnection();
            String query="SELECT * FROM gateway_type WHERE partnerid=? ";
            //String query="SELECT gtype.pgtypeid,gtype.gateway,gtype.name,gtype.currency,gtpm.partnerid FROM gateway_type AS gtype JOIN gatewaytype_partner_mapping AS gtpm ON gtype.pgtypeid=gtpm.pgtypeid AND gtpm.partnerid=?";
            preparedStatement=conn.prepareStatement(query);
            preparedStatement.setString(1,partnerId);
            rs=preparedStatement.executeQuery();
            while(rs.next())
            {
                gatewayType=new GatewayType(rs);
                gatewayTypeList.add(gatewayType);
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return gatewayTypeList;
    }

    public TreeMap<String, String> getGatewayTypePartner(String partnerId)throws SystemError,SQLException


    {
        Connection conn = null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        TreeMap<String, String> gatewayTypeList=new TreeMap<>();
        GatewayType gatewayType=null;
        try
        {
            conn = Database.getRDBConnection();
            //String query="SELECT * FROM gateway_type WHERE partnerid=? ";
            String query="SELECT gtype.pgtypeid,gtype.gateway,gtype.name,gtype.currency, gtpm.partnerid FROM gateway_type AS gtype JOIN gatewaytype_partner_mapping AS gtpm ON gtype.pgtypeid=gtpm.pgtypeid AND gtpm.partnerid=?";
            preparedStatement=conn.prepareStatement(query);
            preparedStatement.setString(1,partnerId);
            rs=preparedStatement.executeQuery();
            while(rs.next())
            {
                gatewayTypeList.put(rs.getString("gateway")+"-"+rs.getString("currency")+"-"+rs.getString("pgtypeid"), rs.getString("gateway")+"-"+rs.getString("currency"));
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return gatewayTypeList;
    }

    public TreeMap<String, String> getIssuingBankForMerchant(String merchantid)throws SystemError,SQLException
    {
        logger.debug("inside getGatewayTypeForMerchant:::");
        Connection conn = null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        TreeMap<String, String> issuingBankList=new TreeMap<>();
        try
        {
            conn = Database.getRDBConnection();
           // String query="SELECT gtype.pgtypeid,gtype.gateway,gtype.currency FROM gateway_type AS gtype JOIN gateway_accounts AS gc, member_account_mapping AS mam WHERE gtype.pgtypeid = gc.pgtypeid AND gc.accountid=mam.accountid AND mam.memberid=?";
            String query="SELECT issuing_bank  FROM bin_details AS b JOIN transaction_common AS t ON b.accountid = t.accountid WHERE b.issuing_bank!='null' AND t.toid = ?";
            preparedStatement=conn.prepareStatement(query);
            preparedStatement.setString(1,merchantid);
            rs=preparedStatement.executeQuery();
            logger.debug("preparedStatement::::::::::::::::::"+preparedStatement);
            while(rs.next())
            {
                issuingBankList.put(rs.getString("issuing_bank"), rs.getString("issuing_bank"));
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return issuingBankList;
    }
    public TreeMap<String, String> getIssuingBankForPartnert(String partnername)throws SystemError,SQLException
    {
        logger.debug("inside getIssuingBankForPartnert:::");
        Connection conn = null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        TreeMap<String, String> issuingBankList=new TreeMap<>();
        try
        {
            conn = Database.getRDBConnection();
            String query="SELECT issuing_bank  FROM bin_details AS b JOIN transaction_common AS t ON b.accountid = t.accountid WHERE b.issuing_bank IS NOT NULL AND b.issuing_bank !='' AND t.totype = ? LIMIT 0,1000";
            preparedStatement=conn.prepareStatement(query);
            preparedStatement.setString(1,partnername);
            rs=preparedStatement.executeQuery();
            logger.debug("preparedStatement for partner:::"+preparedStatement);
            while(rs.next())
            {
                issuingBankList.put(rs.getString("issuing_bank"), rs.getString("issuing_bank"));
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return issuingBankList;
    }

    public List<GatewayType> getPartnerProcessingBanksList(String partnerId) throws SystemError, SQLException
    {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        List<GatewayType> gatewayTypeList = new ArrayList<GatewayType>();
        GatewayType gatewayType = null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT gt.* FROM gateway_type as gt join gatewaytype_partner_mapping as gtpm on gt.pgtypeid=gtpm.pgtypeid WHERE gtpm.partnerid=? ";
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, partnerId);
            rs = preparedStatement.executeQuery();
            while (rs.next())
            {
                gatewayType = new GatewayType(rs);
                gatewayTypeList.add(gatewayType);
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return gatewayTypeList;
    }
    public int getPartnerProcessingBanks(String partnerId)throws SystemError,SQLException
    {
        Connection conn = null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        int count=0;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            String query="SELECT COUNT(*) as 'count' FROM gatewaytype_partner_mapping WHERE partnerid=?";
            preparedStatement=conn.prepareStatement(query);
            preparedStatement.setString(1,partnerId);
            rs=preparedStatement.executeQuery();
            while(rs.next())
            {
              count=rs.getInt("count");
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return count;
    }

    public boolean updateTemplateNameOfGatewayType(FileDetailsVO fileDetailsVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement preparedStatement=null;
        int rs=0;
        try
        {
            conn = Database.getConnection();
            String updateQuery="update gateway_type Set templatename =? WHERE pgtypeid=? ";
            preparedStatement=conn.prepareStatement(updateQuery);
            preparedStatement.setString(1,fileDetailsVO.getFilename());
            preparedStatement.setString(2,fileDetailsVO.getFieldName());
            rs=preparedStatement.executeUpdate();
            if(rs>0)
            {
                GatewayTypeService.loadGatewayTypes();
                return true;

            }
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(GatewayTypeDAO.class.getName(),"updateTemplateNameOfGatewayType()",null,"common","SQL Exception:::", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(GatewayTypeDAO.class.getName(), "updateTemplateNameOfGatewayType()", null, "common", "System Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseDBViolationException(GatewayTypeDAO.class.getName(), "updateTemplateNameOfGatewayType()", null, "common", "System Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return false;
    }

    /**
     * Get all gateway linked with that partner and template mapped
     * @param partnerId
     * @return
     * @throws PZDBViolationException
     */
    public List<GatewayTypeVO> getAllGatewayForPartner(String partnerId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;

        List<GatewayTypeVO> gatewayTypeVOs = new ArrayList<GatewayTypeVO>();
        try
        {
            conn = Database.getRDBConnection();
            String updateQuery="SELECT GPM.pgtypeid as pgtypeid,GPM.defaultApplication as defaultApplication FROM gatewaytype_partner_mapping as GPM JOIN gateway_type as GT on GT.pgtypeid=GPM.pgtypeid where GPM.partnerid =? and GT.templatename IS NOT NULL ORDER BY pgtypeid";
            preparedStatement=conn.prepareStatement(updateQuery);
            preparedStatement.setString(1,partnerId);

            resultSet=preparedStatement.executeQuery();

            while(resultSet.next())
            {
                GatewayTypeVO gatewayTypeVO = new GatewayTypeVO();
                gatewayTypeVO.setPgTYypeId(resultSet.getString("pgtypeid"));
                gatewayTypeVO.setDefaultApplication("Y".equals(resultSet.getString("defaultApplication"))?true:false);
                gatewayTypeVO.setGatewayType(GatewayTypeService.getGatewayType(gatewayTypeVO.getPgTYypeId()));

                gatewayTypeVOs.add(gatewayTypeVO);
            }
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(GatewayTypeDAO.class.getName(),"getAllGatewayForPartner()",null,"common","SQL Exception:::", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(GatewayTypeDAO.class.getName(), "getAllGatewayForPartner()", null, "common", "System Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseDBViolationException(GatewayTypeDAO.class.getName(), "getAllGatewayForPartner()", null, "common", "System Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return gatewayTypeVOs;
    }


    public boolean updateDefaultApplicationGatewayForPartnerAndPgTypeId(String partnerId,String pgTypeId,boolean defaultApplication) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement preparedStatement=null;
        int resultSet=0;

        List<GatewayTypeVO> gatewayTypeVOs = new ArrayList<GatewayTypeVO>();
        try
        {
            conn = Database.getConnection();
            StringBuffer updateQuery=new StringBuffer("update gatewaytype_partner_mapping set defaultApplication=? where partnerid =? ");

            if(functions.isValueNull(pgTypeId))
            {
               updateQuery.append(" and pgtypeid=? ");
            }

            preparedStatement=conn.prepareStatement(updateQuery.toString());
            preparedStatement.setString(1,(defaultApplication?"Y":"N"));
            preparedStatement.setString(2,partnerId);
            if(functions.isValueNull(pgTypeId))
            {
                preparedStatement.setString(3, pgTypeId);
            }
            resultSet=preparedStatement.executeUpdate();

            while(resultSet>0)
            {
                return true;
            }
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(GatewayTypeDAO.class.getName(),"updateDefaultApplicationGatewayForPartnerAndPgTypeId()",null,"common","SQL Exception:::", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(GatewayTypeDAO.class.getName(), "updateDefaultApplicationGatewayForPartnerAndPgTypeId()", null, "common", "System Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return false;
    }

    public Set<String> getGatewayList(String partnerid, String merchantid, String accoutId) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Set<String> gatewaySet = new HashSet<String>();

        try
        {
            con = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("SELECT DISTINCT gt.gateway FROM members AS m JOIN member_account_mapping AS mam ON m.memberid = mam.memberid JOIN gateway_accounts AS ga ON mam.accountid = ga.accountid JOIN gateway_type AS gt ON gt.pgtypeid = ga.pgtypeid WHERE  m.partnerid =" +partnerid);
            if(functions.isValueNull(merchantid) && !merchantid.equals("0"))
            {
                query.append(" and m.memberid ="+merchantid);
            }
            rs = Database.executeQuery(query.toString(), con);
            while (rs.next())
            {
                gatewaySet.add(rs.getString("gateway"));
            }
            logger.debug("gatewayList:::::"+gatewaySet);
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::", e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getGatewayList", null, "Common", "SQLException while connecting to transaction common  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getGatewayList()", null, "Common", "SystemError while connecting to transaction common  table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return gatewaySet;
    }
}