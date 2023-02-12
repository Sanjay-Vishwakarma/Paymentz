package com.manager.dao;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.enums.OperationTypeEnum;
import com.manager.enums.RuleTypeEnum;
import com.manager.vo.PaginationVO;
import com.manager.vo.PayIfeTableInfo;
import com.manager.vo.businessRuleVOs.BusinessProfile;
import com.manager.vo.businessRuleVOs.RuleOperation;
import com.manager.vo.riskRuleVOs.ProfileVO;
import com.manager.vo.riskRuleVOs.RiskProfile;
import com.manager.vo.riskRuleVOs.RuleVO;
import com.manager.vo.userProfileVOs.MemberDetails;
import com.manager.vo.userProfileVOs.MerchantVO;
import com.manager.vo.userProfileVOs.TemplateVO;
import com.manager.vo.userProfileVOs.UserSetting;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;

import java.sql.*;
import java.util.*;

/**
 * Created by Pradeep on 26/08/2015.
 */
public class ProfileManagementDAO
{
    private static Logger log = new Logger(ProfileManagementDAO.class.getName());
    private static Functions functions = new Functions();

    public static void main(String args[])
    {
        List<com.manager.vo.businessRuleVOs.RuleVO> ruleVOList = new ArrayList<com.manager.vo.businessRuleVOs.RuleVO>();
        com.manager.vo.businessRuleVOs.RuleVO ruleVO = new com.manager.vo.businessRuleVOs.RuleVO();
        ruleVO.setId("1");
        ruleVOList.add(ruleVO);

        ruleVO = new com.manager.vo.businessRuleVOs.RuleVO();
        ruleVO.setId("2");
        ruleVOList.add(ruleVO);

        ruleVO = new com.manager.vo.businessRuleVOs.RuleVO();
        ruleVO.setId("4");
        ruleVOList.add(ruleVO);

        ruleVO = new com.manager.vo.businessRuleVOs.RuleVO();
        ruleVO.setId("3");
        ruleVOList.add(ruleVO);

        ruleVO = new com.manager.vo.businessRuleVOs.RuleVO();
        ruleVO.setId("1");
        //System.out.println(ruleVOList.get(ruleVOList.indexOf(ruleVO)).getId());


    }

    public RuleVO getSingleRiskRule(String id,String ruleName) throws PZDBViolationException
    {
        Connection con=null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;
        ResultSet rsRiskRule=null;

        int counter=1;
        RuleVO ruleVO=null;
        try
        {
            con= Database.getConnection();
            StringBuffer query=new StringBuffer("Select id,name,description,label from risk_rule where id>0 ");
            if(functions.isValueNull(id))
            {
                query.append(" and id =?");
            }
            if(functions.isValueNull(ruleName))
            {
                query.append(" and name =?");
            }
            preparedStatement=con.prepareStatement(query.toString());

            if(functions.isValueNull(id))
            {
                preparedStatement.setString(counter,id);
                counter++;
            }
            if(functions.isValueNull(ruleName))
            {
                preparedStatement.setString(counter,ruleName);
                counter++;
            }

            resultSet=preparedStatement.executeQuery();
            if(resultSet.next())
            {
                ruleVO=new RuleVO();
                ruleVO.setId(resultSet.getString("id"));
                ruleVO.setName(resultSet.getString("name"));
                ruleVO.setLabel(resultSet.getString("label"));
                ruleVO.setDescription(resultSet.getString("description"));
            }


        }
        catch (SystemError systemError)
        {
            log.error("error while fetching record from risk rule",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getSingleRiskRule()", null, "Common", "DB Connection Issue", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("SQL Error while fetching record from risk rule",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getSingleRiskRule()", null, "Common", "SQL Query Exception", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }

        return ruleVO;
    }

    public boolean insertRiskDefinition(RuleVO ruleVO) throws PZDBViolationException
    {
        Connection con=null;
        PreparedStatement psInsert;
        try
        {
            con= Database.getConnection();
            String insert="insert into risk_rule(name,description,label,rule_type,query) values (?,?,?,?,?)";
            psInsert=con.prepareStatement(insert);
            psInsert.setString(1,ruleVO.getName());
            psInsert.setString(2,ruleVO.getDescription());
            psInsert.setString(3,ruleVO.getLabel());
            if(functions.isValueNull(ruleVO.getRuleType()))
                psInsert.setString(4,ruleVO.getRuleType());
            else
                psInsert.setString(4, RuleTypeEnum.COMPARATOR.name());
            if(functions.isValueNull(ruleVO.getQuery()))
                psInsert.setString(5,ruleVO.getQuery());
            else
                psInsert.setNull(5,Types.VARCHAR);

            int result=psInsert.executeUpdate();
            if(result>0)
            {
                ResultSet resultSet=psInsert.getGeneratedKeys();
                if(resultSet.next())
                {
                    ruleVO.setId(resultSet.getString(1));
                }
                return true;
            }

        }
        catch (SystemError systemError)
        {
            log.error("error while inserting record from risk rule",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "insertRiskDefinition()", null, "Common", "DB Connection Issue", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("SQL Error while inserting record from risk rule",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "insertRiskDefinition()", null, "Common", "SQL Query Exception", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }

        return false;
    }

    public boolean updateRiskDefinition(RuleVO ruleVO,String riskRuleId) throws PZDBViolationException
    {
        Connection con=null;
        PreparedStatement psUpdate;
        try
        {
            con= Database.getConnection();
            String update="update risk_rule set name=? ,description=? ,label=?,query=? where id=?";
            psUpdate=con.prepareStatement(update);
            psUpdate.setString(1,ruleVO.getName());
            psUpdate.setString(2,ruleVO.getDescription());
            psUpdate.setString(3,ruleVO.getLabel());
            if(functions.isValueNull(ruleVO.getQuery()))
                psUpdate.setString(4,ruleVO.getQuery());
            else
                psUpdate.setNull(4,Types.VARCHAR);
            psUpdate.setString(5,riskRuleId);

            int result=psUpdate.executeUpdate();
            if(result>0)
            {
                return true;
            }

        }
        catch (SystemError systemError)
        {
            log.error("error while inserting record from risk rule",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "insertRiskDefinition()", null, "Common", "DB Connection Issue", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("SQL Error while inserting record from risk rule",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "insertRiskDefinition()", null, "Common", "SQL Query Exception", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }

        return false;
    }

    public boolean deleteRiskDefinition(String riskRuleId) throws PZDBViolationException
    {
        Connection con=null;
        PreparedStatement psUpdate;
        try
        {
            con= Database.getConnection();
            String update="delete from risk_rule where id=?";
            psUpdate=con.prepareStatement(update);
            psUpdate.setString(1,riskRuleId);

            int result=psUpdate.executeUpdate();
            if(result>0)
            {
                return true;
            }

        }
        catch (SystemError systemError)
        {
            log.error("error while inserting record from risk rule",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "insertRiskDefinition()", null, "Common", "DB Connection Issue", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch(MySQLIntegrityConstraintViolationException mySqlException)
        {
            log.error(" System error for deleting rule::",mySqlException);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "deleteRiskDefinition()", null, "Common", "Connection exception while deleting rule", PZDBExceptionEnum.FOREIGN_KEY_CONSTRAINT, null, mySqlException.getMessage(), mySqlException.getCause());
        }

        catch (SQLException e)
        {
            log.error("SQL Error while inserting record from risk rule",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "insertRiskDefinition()", null, "Common", "SQL Query Exception", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }

        return false;
    }

    public List<ProfileVO> getListOfRiskProfileVO(String partnerId,String id,String orderBy,PaginationVO paginationVO) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt=null, psCountOfRiskProfile = null;
        ResultSet resultSet=null,resultSetCount = null;
        List<ProfileVO> riskProfileVOs=new ArrayList<ProfileVO>();

       // String countQuery=null;

        boolean andApply=false;
        int count = 1;
        try
        {
            //con= Database.getConnection();
            con= Database.getRDBConnection();

            StringBuilder query = new StringBuilder("Select * from `risk_profile` ");
            if(functions.isValueNull(partnerId) || functions.isValueNull(id))
            {
                query.append(" where");
            }

            if(functions.isValueNull(partnerId))
            {
                query.append(" partner_id = ? ");
                andApply=true;
            }
            if(functions.isValueNull(id))
            {
                if(andApply)
                {
                    query.append(" and");
                }
                query.append(" profileid = ? ");
                andApply=true;
            }

            if(functions.isValueNull(orderBy))
            {
                query.append("order by "+orderBy);
            }

            StringBuilder countQuery=new StringBuilder("Select Count(*) from ( ");
            countQuery.append(query.toString());
            countQuery.append(") as temp");

            if(paginationVO!=null)
                query.append("  limit "+paginationVO.getStart()+","+paginationVO.getEnd());

            pstmt=con.prepareStatement(query.toString());
            if(functions.isValueNull(partnerId))
            {
                pstmt.setString(count,partnerId);
                count++;

            }
            if(functions.isValueNull(id))
            {
                pstmt.setString(count, id);
                count++;

            }
            resultSet=pstmt.executeQuery();
            while(resultSet.next())
            {

                ProfileVO profileVO=new ProfileVO();
                profileVO.setId(resultSet.getString("profileid"));
                profileVO.setName(resultSet.getString("profile_name"));
                riskProfileVOs.add(profileVO);
            }


            if(paginationVO!=null)
            {
                count = 1;
                psCountOfRiskProfile = con.prepareStatement(countQuery.toString());
                if(functions.isValueNull(partnerId))
                {
                    psCountOfRiskProfile.setString(count,partnerId);
                    count++;

                }
                if(functions.isValueNull(id))
                {
                    psCountOfRiskProfile.setString(count, id);
                    count++;

                }
                resultSetCount = psCountOfRiskProfile.executeQuery();
                if (resultSetCount.next())
                {
                    paginationVO.setTotalRecords(resultSetCount.getInt(1));
                }
            }
        }

        catch (SQLException e)
        {
            log.error("SQL Exception while getting risk Profile details",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getListOfRiskProfileVO()", null, "common", "Sql exception while getting Profile details", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            log.error("SQL Exception while getting risk Profile details",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getListOfRiskProfileVO()", null, "common", "Sql exception while getting Profile details", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closeResultSet(resultSetCount);
            Database.closePreparedStatement(pstmt);
            Database.closePreparedStatement(psCountOfRiskProfile);
            Database.closeConnection(con);
        }
        return riskProfileVOs;

    }


    public boolean uniqueRiskProfileName(String profileName, String partnerid) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt=null;
        ResultSet resultSet=null;
        boolean flag = false;
        try
        {
            con= Database.getRDBConnection();
            StringBuilder query = new StringBuilder("SELECT * FROM risk_profile WHERE profile_name = ? AND partner_id = ?");
            pstmt=con.prepareStatement(query.toString());
            pstmt.setString(1, profileName);
            pstmt.setString(2, partnerid);
            log.debug("pstmt:::"+pstmt);
            resultSet=pstmt.executeQuery();
            while(resultSet.next())
            {
                flag = true;
            }
        }

        catch (SQLException e)
        {
            log.error("SQL Exception while getting risk Profile details",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getListOfRiskProfileVO()", null, "common", "Sql exception while getting Profile details", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            log.error("SQL Exception while getting risk Profile details",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getListOfRiskProfileVO()", null, "common", "Sql exception while getting Profile details", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return flag;

    }

    public boolean uniqueBusinessProfileName(String profileName, String partnerid) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt=null;
        ResultSet resultSet=null;
        boolean flag = false;
        try
        {
            con= Database.getRDBConnection();
            StringBuilder query = new StringBuilder("SELECT * FROM business_profile WHERE profile_name = ? AND partner_id = ?");
            pstmt=con.prepareStatement(query.toString());
            pstmt.setString(1, profileName);
            pstmt.setString(2, partnerid);
            resultSet=pstmt.executeQuery();
            while(resultSet.next())
            {
                flag = true;
            }
        }

        catch (SQLException e)
        {
            log.error("SQL Exception while getting risk Profile details",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getListOfRiskProfileVO()", null, "common", "Sql exception while getting Profile details", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            log.error("SQL Exception while getting risk Profile details",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getListOfRiskProfileVO()", null, "common", "Sql exception while getting Profile details", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return flag;

    }

    public RiskProfile getRiskProfileWithAllDetails(String partnerId,String profileId,PaginationVO paginationVO) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt=null;
        ResultSet resultSet=null,resultSetCount=null;
        RiskProfile businessProfileVO=new RiskProfile();
        Map<String, ProfileVO> profileVOMap=new HashMap<String, ProfileVO>();
        List<RuleVO> roleRuleVOs=null;
        List<com.manager.vo.riskRuleVOs.RuleOperation> ruleOperations=null;
        int count = 1;
        boolean and=false;
        //String countQuery=null;
        try
        {
            con= Database.getRDBConnection();
            StringBuilder query = new StringBuilder("SELECT BP.profile_id AS profileId,BR.id AS ruleid,BR.name AS rulename,BR.description AS ruledesc,BR.label AS rulelabel,BR.rule_type AS RuleType,BR.query AS QUERY,BP.isApplicable AS ruleapplicable ,BRO.value1 AS rulevalue1,BRO.value2 AS rulevalue2,BM.profile_name AS Profilename,BRO.operationId AS operationId,BRO.inputName AS inputName,BRO.regex AS regex,BRO.operator AS ruleoperator,BRO.comparator AS comparator,BRO.inputType AS inputType,BRO.enumValue AS enumValue,BRO.isMandatory AS isMandatory,BP.score as riskScore FROM `risk_profile_rule` AS BP JOIN `risk_profile` AS BM ON BP.profile_id = BM.profileid JOIN `risk_rule` AS BR ON BR.id=BP.ruleid JOIN risk_rule_operation AS BRO ON BR.id=BRO.ruleId");

            if(functions.isValueNull(partnerId) || functions.isValueNull(profileId))
            {
                query.append(" where ");
            }
            if(functions.isValueNull(partnerId))
            {
                query.append(" BM.partner_id =?");
                and=true;
            }
            if(functions.isValueNull(profileId))
            {
                if(and)
                {
                    query.append(" and ");
                }
                query.append(" BP.profile_id =?");
                and=true;
            }
            query.append(" ORDER BY BP.profile_id ASC,BR.id ASC,BRO.operationId ASC ");
            StringBuilder countQuery=new StringBuilder("Select Count(*) from ( ");
            countQuery.append(query.toString());
            countQuery.append(") as temp");
            if(paginationVO!=null)
                query.append("  limit "+paginationVO.getStart()+","+paginationVO.getEnd());
            pstmt=con.prepareStatement(query.toString());
            if(functions.isValueNull(partnerId))
            {
                pstmt.setString(count,partnerId);
                count++;
            }
            if(functions.isValueNull(profileId))
            {
                pstmt.setString(count, profileId);
            }
            resultSet=pstmt.executeQuery();
            while(resultSet.next())
            {

                ProfileVO profileVO = new ProfileVO();
                profileVO.setId(resultSet.getString("profileId"));
                profileVO.setName(resultSet.getString("Profilename"));
                RuleVO ruleVO = new RuleVO();
                ruleVO.setId(resultSet.getString("ruleid"));
                ruleVO.setName(resultSet.getString("rulename"));
                ruleVO.setIsApplicable("Y".equals(resultSet.getString("ruleapplicable")) ? true : false);

                ruleVO.setLabel(resultSet.getString("rulelabel"));
                ruleVO.setDescription(resultSet.getString("ruledesc"));
                ruleVO.setRuleType(resultSet.getString("RuleType"));

                ruleVO.setQuery(resultSet.getString("Query"));
                ruleVO.setScore(resultSet.getInt("riskScore"));

                com.manager.vo.riskRuleVOs.RuleOperation ruleOperation =new com.manager.vo.riskRuleVOs.RuleOperation();
                ruleOperation.setId(resultSet.getString("operationId"));
                ruleOperation.setRegex(resultSet.getString("regex"));
                ruleOperation.setInputName(resultSet.getString("inputName"));
                ruleOperation.setOperator(resultSet.getString("ruleoperator"));
                ruleOperation.setDataType(resultSet.getString("inputType"));
                ruleOperation.setEnumValue(resultSet.getString("enumValue"));
                ruleOperation.setValue1(resultSet.getString("rulevalue1"));
                ruleOperation.setValue2(resultSet.getString("rulevalue2"));
                ruleOperation.setComparator(resultSet.getString("comparator"));
                ruleOperation.setMandatory("Y".equalsIgnoreCase(resultSet.getString("isMandatory"))?true:false);



                if(profileVOMap.containsKey(profileVO.getId()))
                {
                    ProfileVO profileVOInner=profileVOMap.get(profileVO.getId());
                    profileVOInner.setId(profileVO.getId());
                    profileVOInner.setName(profileVO.getName());

                    if(profileVOInner.getRules()==null)
                    {
                        roleRuleVOs=new ArrayList<RuleVO>();
                        ruleOperations=new ArrayList<com.manager.vo.riskRuleVOs.RuleOperation>();
                        ruleOperations.add(ruleOperation);
                        ruleVO.setRuleOperation(ruleOperations);
                        roleRuleVOs.add(ruleVO);
                        profileVOInner.setRules(roleRuleVOs);
                    }
                    else
                    {
                        roleRuleVOs=profileVOInner.getRules();
                        //CollectionUtils.exists(roleRuleVOs, Predicate<ro>)
                        log.debug("Index of object in LIST:::"+roleRuleVOs.indexOf(ruleVO));
                        if(roleRuleVOs.indexOf(ruleVO)>=0)
                        {
                            RuleVO ruleVO1 =roleRuleVOs.get(roleRuleVOs.indexOf(ruleVO));
                            if(ruleVO1.getRuleOperation()!=null)
                            {
                                ruleOperations = ruleVO1.getRuleOperation();
                                ruleOperations.add(ruleOperation);
                            }
                            else
                            {
                                ruleOperations=new ArrayList<com.manager.vo.riskRuleVOs.RuleOperation>();
                                ruleOperations.add(ruleOperation);
                            }
                            ruleVO1.setRuleOperation(ruleOperations);
                        }
                        else
                        {
                            ruleOperations=new ArrayList<com.manager.vo.riskRuleVOs.RuleOperation>();
                            ruleOperations.add(ruleOperation);
                            ruleVO.setRuleOperation(ruleOperations);
                            roleRuleVOs.add(ruleVO);
                        }

                        //roleRuleVOs.remove(,ruleVO);
                        //roleRuleVOs.add(,ruleVO);


                    }
                    profileVOMap.put(profileVO.getId(),profileVOInner);

                }
                else
                {
                    ProfileVO profileVOInner=new ProfileVO();
                    profileVOInner.setId(profileVO.getId());
                    profileVOInner.setName(profileVO.getName());
                    roleRuleVOs=new ArrayList<RuleVO>();
                    ruleOperations=new ArrayList<com.manager.vo.riskRuleVOs.RuleOperation>();
                    ruleOperations.add(ruleOperation);
                    ruleVO.setRuleOperation(ruleOperations);
                    roleRuleVOs.add(ruleVO);
                    profileVOInner.setRules(roleRuleVOs);
                    profileVOMap.put(profileVO.getId(), profileVOInner);
                }
            }
            businessProfileVO.setProfiles(profileVOMap);

            if(paginationVO!=null)
            {
                count = 1;
                PreparedStatement psCountOfBusinessProfile = con.prepareStatement(countQuery.toString());
                if (functions.isValueNull(partnerId))
                {
                    psCountOfBusinessProfile.setString(count, partnerId);
                    count++;
                }
                if (functions.isValueNull(profileId))
                {
                    psCountOfBusinessProfile.setString(count, profileId);
                }
                resultSetCount = psCountOfBusinessProfile.executeQuery();
                if (resultSetCount.next())
                {
                    paginationVO.setTotalRecords(resultSetCount.getInt(1));
                }
            }
        }

        catch (SQLException e)
        {
            log.error("Exception while getting the data:::",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getBusinessProfileWithAllDetails()", null, "common", "Sql exception while getting Profile details", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            log.error("Exception while getting the data:::",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getBusinessProfileWithAllDetails()", null, "common", "Sql exception while getting Profile details", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return businessProfileVO;

    }

    public List<RuleVO> getListOfRiskRule(RuleVO ruleVO,PaginationVO paginationVO) throws PZDBViolationException
    {
        Connection con=null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;
        ResultSet rsRiskRule=null;

        int counter=1;
        List<RuleVO> risRuleVOList = new ArrayList<RuleVO>();
        try
        {
            con= Database.getRDBConnection();
            StringBuffer query=new StringBuffer("Select id,name,description,label from risk_rule where id>0 ");
            if(ruleVO!=null && functions.isValueNull(ruleVO.getName()))
            {
                query.append(" and name =?");
            }
            if(ruleVO!=null && functions.isValueNull(ruleVO.getId()))
            {
                query.append(" and id =?");
            }
            String countQuery="Select Count(*) from ("+query.toString()+") as temp";
            if(paginationVO!=null)
                query.append("  limit "+paginationVO.getStart()+","+paginationVO.getEnd());
            preparedStatement=con.prepareStatement(query.toString());

            if(ruleVO!=null && functions.isValueNull(ruleVO.getName()))
            {
                preparedStatement.setString(counter,ruleVO.getName());
                counter++;
            }
            if(ruleVO!=null && functions.isValueNull(ruleVO.getId()))
            {
                preparedStatement.setString(counter,ruleVO.getId());
                counter++;
            }

            resultSet=preparedStatement.executeQuery();
            while(resultSet.next())
            {
                RuleVO ruleVO1=new RuleVO();
                ruleVO1.setId(resultSet.getString("id"));
                ruleVO1.setName(resultSet.getString("name"));
                ruleVO1.setDescription(resultSet.getString("description"));
                ruleVO1.setLabel(resultSet.getString("label"));
                risRuleVOList.add(ruleVO1);
            }

            if(paginationVO!=null)
            {
                counter = 1;
                PreparedStatement psCountOfRiskRule = con.prepareStatement(countQuery);
                if (ruleVO != null && functions.isValueNull(ruleVO.getName()))
                {
                    psCountOfRiskRule.setString(counter, ruleVO.getName());
                    counter++;
                }
                if (ruleVO != null && functions.isValueNull(ruleVO.getId()))
                {
                    psCountOfRiskRule.setString(counter, ruleVO.getId());
                    counter++;
                }

                rsRiskRule = psCountOfRiskRule.executeQuery();
                if (rsRiskRule.next())
                {
                    paginationVO.setTotalRecords(rsRiskRule.getInt(1));
                }
            }

        }
        catch (SystemError systemError)
        {
            log.error("error while fetching record from risk rule",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getListOfRiskRule()", null, "Common", "DB Connection Issue", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("SQL Error while fetching record from risk rule",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getListOfRiskRule()", null, "Common", "SQL Query Exception", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return risRuleVOList;
    }

    public boolean insertRiskRuleOperation(List<com.manager.vo.riskRuleVOs.RuleOperation> ruleOperationList,String riskRuleId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        String query=null;
        try
        {

            conn = Database.getConnection();
            query = "INSERT INTO risk_rule_operation(ruleId,inputName,regex,operator,comparator,sequence,value1,value2,isMandatory)VALUES(?,?,?,?,?,?,?,?,?)";
            pstmt= conn.prepareStatement(query);
            if(ruleOperationList!=null)
            {
                int sequence=1;
                for (com.manager.vo.riskRuleVOs.RuleOperation ruleOperation:ruleOperationList)
                {

                    pstmt.setString(1, riskRuleId);

                    pstmt.setString(2, ruleOperation.getInputName());

                    if(functions.isValueNull(ruleOperation.getRegex()))
                    {
                        pstmt.setString(3, ruleOperation.getRegex());
                    }
                    else
                    {
                        pstmt.setNull(3, Types.VARCHAR);
                    }

                    if(functions.isValueNull(ruleOperation.getOperator()))
                    {
                        pstmt.setString(4, ruleOperation.getOperator());
                    }
                    else
                    {
                        pstmt.setNull(4, Types.VARCHAR);
                    }
                    if(functions.isValueNull(ruleOperation.getComparator()))
                    {
                        pstmt.setString(5, ruleOperation.getComparator());
                    }
                    else
                    {
                        pstmt.setNull(5, Types.VARCHAR);
                    }
                    pstmt.setInt(6, sequence);
                    if(functions.isValueNull(ruleOperation.getValue1()))
                    {
                        pstmt.setString(7, ruleOperation.getValue1());
                    }
                    else
                    {
                        pstmt.setNull(7, Types.VARCHAR);
                    }

                    if(functions.isValueNull(ruleOperation.getValue2()))
                    {
                        pstmt.setString(8, ruleOperation.getValue2());
                    }
                    else
                    {
                        pstmt.setNull(8, Types.VARCHAR);
                    }
                    if(ruleOperation.isMandatory())
                    {
                        pstmt.setString(9,"Y");
                    }
                    else
                    {
                        pstmt.setString(9,"N");
                    }

                    pstmt.addBatch();
                    sequence++;
                }

                int[] k = pstmt.executeBatch();
                if (k.length>0)
                {
                    return true;
                }
            }

        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "insertBusinessProfile()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "insertBusinessProfile()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return false;
    }

    public RuleVO getSingleRiskRuleWithOperation(String id) throws PZDBViolationException
    {
        Connection con=null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;
        // ResultSet rsbusinessRule=null;

        int counter=1;
        RuleVO ruleVO=null;
        List<com.manager.vo.riskRuleVOs.RuleOperation> ruleOperationList=new ArrayList<com.manager.vo.riskRuleVOs.RuleOperation>();
        try
        {
            con= Database.getRDBConnection();
            StringBuffer query=new StringBuffer("Select RRU.id,RRU.name,RRU.description,RRU.label,RRU.rule_type,RRU.query,RRO.operationId,RRO.inputName,RRO.regex,RRO.operator,RRO.comparator,RRO.sequence,RRO.value1,RRO.value2,RRO.inputType,RRO.enumValue,RRO.isMandatory from risk_rule as RRU Left JOIN risk_rule_operation as RRO ON RRU.id=RRO.ruleId where RRU.id>0 ");
            if(functions.isValueNull(id))
            {
                query.append(" and RRU.id =?");
            }
            query.append(" ORDER BY RRO.sequence ASC");

            preparedStatement=con.prepareStatement(query.toString());

            if(functions.isValueNull(id))
            {
                preparedStatement.setString(counter,id);
                counter++;
            }

            resultSet=preparedStatement.executeQuery();
            while(resultSet.next())
            {
                ruleVO=new RuleVO();
                ruleVO.setId(resultSet.getString("id"));
                ruleVO.setName(resultSet.getString("name"));
                ruleVO.setLabel(resultSet.getString("label"));
                ruleVO.setDescription(resultSet.getString("description"));
                ruleVO.setRuleType(resultSet.getString("rule_type"));
                ruleVO.setQuery(resultSet.getString("query"));

                com.manager.vo.riskRuleVOs.RuleOperation ruleOperation = new com.manager.vo.riskRuleVOs.RuleOperation();
                ruleOperation.setId(resultSet.getString("operationId"));
                ruleOperation.setInputName(resultSet.getString("inputName"));
                ruleOperation.setRegex(resultSet.getString("regex"));
                ruleOperation.setOperator(resultSet.getString("operator"));
                ruleOperation.setValue1(resultSet.getString("value1"));
                ruleOperation.setValue2(resultSet.getString("value2"));
                ruleOperation.setComparator(resultSet.getString("comparator"));
                ruleOperation.setDataType(resultSet.getString("inputType"));
                ruleOperation.setEnumValue(resultSet.getString("enumValue"));
                ruleOperation.setMandatory("Y".equalsIgnoreCase(resultSet.getString("isMandatory"))?true:false);
                ruleOperationList.add(ruleOperation);
            }
        }
        catch (SystemError systemError)
        {
            log.error("error while fetching record from business rule",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getSinglebusinessRule()", null, "Common", "DB Connection Issue", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("SQL Error while fetching record from business rule",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getSinglebusinessRule()", null, "Common", "SQL Query Exception", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        ruleVO.setRuleOperation(ruleOperationList);

        return ruleVO;
    }

    public boolean deleteRiskRuleOperation(String riskRuleId) throws PZDBViolationException
    {
        Connection con=null;
        PreparedStatement psUpdate;
        try
        {
            con= Database.getConnection();
            String update="DELETE FROM  risk_rule_operation where ruleId=?";
            psUpdate=con.prepareStatement(update);
            psUpdate.setString(1,riskRuleId);

            int result=psUpdate.executeUpdate();
            if(result>0)
            {
                return true;
            }

        }
        catch (SystemError systemError)
        {
            log.error("error while deleting record from business rule operation",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "deleteBusinessRuleOperation()", null, "Common", "DB Connection Issue", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("SQL Error while deleting record from business rule opeartion",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "deleteBusinessRuleOperation()", null, "Common", "SQL Query Exception", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }

        return false;
    }

    //update for RiskProfileMapping

    //insert for RiskProfileMapping //TODO
    public boolean insertRiskProfileMapping(ProfileVO profileVO,String partnerID) throws PZDBViolationException
    {

        Connection conn = null;
        PreparedStatement pstmt=null;
        String query=null;
        try
        {

            conn = Database.getConnection();
            query = "INSERT INTO risk_profile(profile_name,partner_id)VALUES(?,?)";
            pstmt= conn.prepareStatement(query);

            if(functions.isValueNull(profileVO.getName()))
            {

                pstmt.setString(1,profileVO.getName());
            }
            else
            {
                pstmt.setNull(1, Types.VARCHAR);
            }

            if(functions.isValueNull(partnerID))
            {
                pstmt.setString(2,partnerID);
            }


            int k = pstmt.executeUpdate();
            if (k>0)
            {
                ResultSet resultSet=pstmt.getGeneratedKeys();
                while(resultSet.next())
                {
                    profileVO.setId(resultSet.getString(1));
                }
                return true;
            }

        }
        catch (SystemError systemError)
        {
            log.error("Leaving insertRiskProfileMapping throwing SQL Exception as System Error :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "insertRiskProfileMapping()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("Leaving insertRiskProfileMapping throwing SQL Exception as System Error :::: ",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "insertRiskProfileMapping()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return false;
    }

    //insert for RiskProfile

    public boolean updateRiskProfileMapping(ProfileVO profileVO)
    {
        Connection con = null;
        try
        {
            con= Database.getConnection();
            String update="update `risk_profile` set profile_name =? where profileid =?";
            PreparedStatement psupdateRiskProfileMapping = con.prepareStatement(update.toString());
            psupdateRiskProfileMapping.setString(1,profileVO.getName());
            psupdateRiskProfileMapping.setString(2,profileVO.getId());
            log.debug(" update query::"+update);
            int rss=psupdateRiskProfileMapping.executeUpdate();
            if(rss>0)
            {
                return true;
            }

        }
        catch (SystemError systemError)
        {
            log.error(" System error for updateRiskProfileMapping::",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQL exception for updateRiskProfileMapping::",e);
        }
        finally {
            Database.closeConnection(con);
        }
        return false;
    }

    //update for RiskProfile

    public boolean insertRiskProfile(RuleVO ruleVO,String profileId) throws PZDBViolationException
    {

        Connection conn = null;
        PreparedStatement pstmt=null;
        String query=null;
        try
        {

            conn = Database.getConnection();
            query = "INSERT INTO risk_profile_rule(profile_id,ruleid,isApplicable,score)VALUES(?,?,?,?)";
            pstmt= conn.prepareStatement(query);

            pstmt.setString(1,profileId);

            pstmt.setString(2,ruleVO.getId());

            if(ruleVO.isApplicable())
            {
                pstmt.setString(3,"Y");
            }
            else
            {
                pstmt.setString(3,"N");
            }

            pstmt.setInt(4,ruleVO.getScore());



            int k = pstmt.executeUpdate();
            if (k>0)
            {
                return true;
            }

        }
        catch (SystemError systemError)
        {
            log.error("Leaving insertRiskProfileMapping throwing SQL Exception as System Error :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "insertRiskProfileMapping()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("Leaving insertRiskProfileMapping throwing SQL Exception as System Error :::: ",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "insertRiskProfileMapping()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return false;
    }

    public boolean updateRiskProfile(RuleVO ruleVO,String riskProfileId)
    {
        Connection con = null;
        try
        {
            con= Database.getConnection();
            String update="update `risk_profile_rule` set ruleid =?,isApplicable=?,score=? where id =?";
            PreparedStatement psupdateRiskProfile = con.prepareStatement(update.toString());
            psupdateRiskProfile.setString(1,ruleVO.getId()); //ruleID set ???
            psupdateRiskProfile.setString(2,ruleVO.isApplicable()?"Y":"N");
            psupdateRiskProfile.setString(3,String.valueOf(ruleVO.getScore()));
            psupdateRiskProfile.setString(4,riskProfileId);
            log.debug(" update query::"+update);
            int rss=psupdateRiskProfile.executeUpdate();
            if(rss>0)
            {
                return true;
            }

        }
        catch (SystemError systemError)
        {
            log.error(" System error for updateRiskProfile::",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQL exception for updateRiskProfile::",e);
        }
        finally {
            Database.closeConnection(con);
        }
        return false;
    }

    public boolean deleteRuleRelatedForProfile(String profileId) throws PZDBViolationException
    {
        Connection con = null;
        try
        {
            con= Database.getConnection();
            String delete="Delete from `risk_profile_rule` where profile_id =?";
            PreparedStatement psDelete = con.prepareStatement(delete.toString());
            psDelete.setString(1,profileId);
            int rss=psDelete.executeUpdate();
            if(rss>0)
            {
                return true;
            }

        }
        catch (SystemError systemError)
        {
            log.error(" System error for updateRiskProfile::",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "deleteRuleRelatedForProfile()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("SQL exception for updateRiskProfile::",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "deleteRuleRelatedForProfile()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally {
            Database.closeConnection(con);
        }
        return false;
    }

    public boolean deleteRiskProfile(String profileId) throws PZDBViolationException
    {
        Connection con = null;
        try
        {
            con= Database.getConnection();
            String delete="Delete from `risk_profile` where profileid =?";
            PreparedStatement psDelete = con.prepareStatement(delete.toString());
            psDelete.setString(1,profileId);
            int rss=psDelete.executeUpdate();
            if(rss>0)
            {
                return true;
            }

        }
        catch (SystemError systemError)
        {
            log.error(" System error for deleting profile::",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "deleteRiskProfile()", null, "Common", "Connection exception while deleting profile", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch(MySQLIntegrityConstraintViolationException mySqlException)
        {
            log.error(" System error for deleting profile::",mySqlException);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "deleteRiskProfile()", null, "Common", "Connection exception while deleting profile", PZDBExceptionEnum.FOREIGN_KEY_CONSTRAINT, null, mySqlException.getMessage(), mySqlException.getCause());
        }
        catch (SQLException e)
        {
            log.error("SQL exception for updateRiskProfile::",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "deleteRiskProfile()", null, "Common", "Connection exception while deleting profile", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally {
            Database.closeConnection(con);
        }
        return false;
    }

    public com.manager.vo.businessRuleVOs.RuleVO getSingleBusinessRule(String id,String ruleName) throws PZDBViolationException
    {
        Connection con=null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;
        // ResultSet rsbusinessRule=null;

        int counter=1;
        com.manager.vo.businessRuleVOs.RuleVO ruleVO=null;
        try
        {
            con= Database.getRDBConnection();
            StringBuffer query=new StringBuffer("Select id,name,description,label,rule_type,query from business_rule where id>0 ");
            if(functions.isValueNull(id))
            {
                query.append(" and id =?");
            }
            if(functions.isValueNull(ruleName))
            {
                query.append(" and name =?");
            }
            preparedStatement=con.prepareStatement(query.toString());

            if(functions.isValueNull(id))
            {
                preparedStatement.setString(counter,id);
                counter++;
            }
            if(functions.isValueNull(ruleName))
            {
                preparedStatement.setString(counter,ruleName);
                counter++;
            }

            resultSet=preparedStatement.executeQuery();
            if(resultSet.next())
            {
                ruleVO=new com.manager.vo.businessRuleVOs.RuleVO();
                ruleVO.setId(resultSet.getString("id"));
                ruleVO.setName(resultSet.getString("name"));
                ruleVO.setLabel(resultSet.getString("label"));
                ruleVO.setDescription(resultSet.getString("description"));
                ruleVO.setRuleType(resultSet.getString("rule_type"));
                ruleVO.setQuery(resultSet.getString("query"));
            }


        }
        catch (SystemError systemError)
        {
            log.error("error while fetching record from business rule",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getSinglebusinessRule()", null, "Common", "DB Connection Issue", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("SQL Error while fetching record from business rule",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getSinglebusinessRule()", null, "Common", "SQL Query Exception", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }

        return ruleVO;
    }

    public com.manager.vo.businessRuleVOs.RuleVO getSingleBusinessRuleWithOperation(String id) throws PZDBViolationException
    {
        Connection con=null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;
        // ResultSet rsbusinessRule=null;

        int counter=1;
        com.manager.vo.businessRuleVOs.RuleVO ruleVO=null;
        List<RuleOperation> ruleOperationList=new ArrayList<RuleOperation>();
        try
        {
            con= Database.getRDBConnection();
            StringBuffer query=new StringBuffer("Select BRU.id,BRU.name,BRU.description,BRU.label,BRU.rule_type,BRU.query,BRO.operationId,BRO.inputName,BRO.regex,BRO.operator,BRO.comparator,BRO.sequence,BRO.inputType,BRO.enumValue,BRO.isMandatory,BRO.operationType from business_rule as BRU Left JOIN business_rule_operation as BRO ON BRU.id=BRO.ruleId where BRU.id>0 ");
            if(functions.isValueNull(id))
            {
                query.append(" and BRU.id =?");
            }
            query.append(" ORDER BY BRO.sequence ASC");

            preparedStatement=con.prepareStatement(query.toString());

            if(functions.isValueNull(id))
            {
                preparedStatement.setString(counter,id);
                counter++;
            }

            resultSet=preparedStatement.executeQuery();
            while(resultSet.next())
            {
                ruleVO=new com.manager.vo.businessRuleVOs.RuleVO();
                ruleVO.setId(resultSet.getString("id"));
                ruleVO.setName(resultSet.getString("name"));
                ruleVO.setLabel(resultSet.getString("label"));
                ruleVO.setDescription(resultSet.getString("description"));
                ruleVO.setRuleType(resultSet.getString("rule_type"));
                ruleVO.setQuery(resultSet.getString("query"));

                RuleOperation ruleOperation = new RuleOperation();
                ruleOperation.setId(resultSet.getString("operationId"));
                ruleOperation.setInputName(resultSet.getString("inputName"));
                ruleOperation.setRegex(resultSet.getString("regex"));
                ruleOperation.setOperator(resultSet.getString("operator"));
                ruleOperation.setDataType(resultSet.getString("inputType"));
                ruleOperation.setEnumValue(resultSet.getString("enumValue"));
                ruleOperation.setComparator(resultSet.getString("comparator"));
                ruleOperation.setMandatory("Y".equalsIgnoreCase(resultSet.getString("isMandatory")) ? true : false);
                ruleOperation.setOperationType(resultSet.getString("operationType"));
                ruleOperationList.add(ruleOperation);
            }
        }
        catch (SystemError systemError)
        {
            log.error("error while fetching record from business rule",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getSinglebusinessRule()", null, "Common", "DB Connection Issue", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("SQL Error while fetching record from business rule",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getSinglebusinessRule()", null, "Common", "SQL Query Exception", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        ruleVO.setRuleOperation(ruleOperationList);

        return ruleVO;
    }

    public boolean insertBusinessDefinition(com.manager.vo.businessRuleVOs.RuleVO ruleVO) throws PZDBViolationException
    {
        Connection con=null;
        PreparedStatement psInsert;
        try
        {
            con= Database.getConnection();
            String insert="insert into business_rule(name,description,label,rule_type,query) values (?,?,?,?,?)";
            psInsert=con.prepareStatement(insert);
            psInsert.setString(1,ruleVO.getName());
            psInsert.setString(2,ruleVO.getDescription());
            psInsert.setString(3,ruleVO.getLabel());
            psInsert.setString(4, ruleVO.getRuleType());

            if(functions.isValueNull(ruleVO.getQuery()))
            {
                psInsert.setString(5, ruleVO.getQuery());
            }
            else
            {
                psInsert.setNull(5, Types.VARCHAR);
            }

            int result=psInsert.executeUpdate();
            if(result>0)
            {
                ResultSet resultSet=psInsert.getGeneratedKeys();
                if(resultSet.next())
                {
                    ruleVO.setId(resultSet.getString(1));
                }
                return true;
            }

        }
        catch (SystemError systemError)
        {
            log.error("error while inserting record from business rule",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "insertBusinessDefinition()", null, "Common", "DB Connection Issue", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("SQL Error while inserting record from business rule",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "insertBusinessDefinition()", null, "Common", "SQL Query Exception", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }

        return false;
    }

    public boolean insertBusinessRuleOperation(List<com.manager.vo.businessRuleVOs.RuleOperation> ruleOperationList,String businessRuleId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        String query=null;
        try
        {

            conn = Database.getConnection();
            query = "INSERT INTO business_rule_operation(ruleId,inputName,regex,operator,comparator,sequence,inputType,enumValue,isMandatory,operationType)VALUES(?,?,?,?,?,?,?,?,?,?)";
            pstmt= conn.prepareStatement(query);
            if(ruleOperationList!=null)
            {
                int sequence=1;
                for (RuleOperation ruleOperation:ruleOperationList)
                {

                    pstmt.setString(1, businessRuleId);

                    pstmt.setString(2, ruleOperation.getInputName());

                    if(functions.isValueNull(ruleOperation.getRegex()))
                    {
                        pstmt.setString(3, ruleOperation.getRegex());
                    }
                    else
                    {
                        pstmt.setNull(3, Types.VARCHAR);
                    }

                    if(functions.isValueNull(ruleOperation.getOperator()))
                    {
                        pstmt.setString(4, ruleOperation.getOperator());
                    }
                    else
                    {
                        pstmt.setNull(4, Types.VARCHAR);
                    }
                    if(functions.isValueNull(ruleOperation.getComparator()))
                    {
                        pstmt.setString(5, ruleOperation.getComparator());
                    }
                    else
                    {
                        pstmt.setNull(5, Types.VARCHAR);
                    }
                    pstmt.setInt(6, sequence);
                    if(functions.isValueNull(ruleOperation.getDataType()))
                    {
                        pstmt.setString(7, ruleOperation.getDataType());
                    }
                    else
                    {
                        pstmt.setString(7, "VARCHAR");
                    }

                    if(functions.isValueNull(ruleOperation.getEnumValue()))
                    {
                        pstmt.setString(8, ruleOperation.getEnumValue());
                    }
                    else
                    {
                        pstmt.setNull(8, Types.VARCHAR);
                    }
                    if(ruleOperation.isMandatory())
                    {
                        pstmt.setString(9,"Y");
                    }
                    else
                    {
                        pstmt.setString(9,"N");
                    }
                    if(functions.isValueNull(ruleOperation.getOperationType()))
                    {
                        pstmt.setString(10,ruleOperation.getOperationType());
                    }
                    else
                    {
                        pstmt.setString(10, OperationTypeEnum.EXECUTION.name());
                    }

                    pstmt.addBatch();
                    sequence++;
                }

                int[] k = pstmt.executeBatch();
                if (k.length>0)
                {
                    return true;
                }
            }

        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "insertBusinessProfile()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "insertBusinessProfile()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return false;
    }

    public boolean updateBusinessDefinition(com.manager.vo.businessRuleVOs.RuleVO ruleVO,String businessRuleId) throws PZDBViolationException
    {
        Connection con=null;
        PreparedStatement psUpdate;
        try
        {
            con= Database.getConnection();
            String update="update business_rule set name=? ,description=? ,label=? ,rule_type=?,query=? where id=?";
            psUpdate=con.prepareStatement(update);
            psUpdate.setString(1,ruleVO.getName());
            psUpdate.setString(2,ruleVO.getDescription());
            psUpdate.setString(3,ruleVO.getLabel());
            psUpdate.setString(4,ruleVO.getRuleType());

            if(functions.isValueNull(ruleVO.getQuery()))
            {
                psUpdate.setString(5, ruleVO.getQuery());
            }
            else
            {
                psUpdate.setNull(5, Types.VARCHAR);
            }

            psUpdate.setString(6,businessRuleId);

            int result=psUpdate.executeUpdate();
            if(result>0)
            {
                return true;
            }

        }
        catch (SystemError systemError)
        {
            log.error("error while inserting record from business rule",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "updateBusinessDefinition()", null, "Common", "DB Connection Issue", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("SQL Error while inserting record from business rule",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "updateBusinessDefinition()", null, "Common", "SQL Query Exception", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }

        return false;
    }

    public boolean deleteBusinessDefinition(String businessRuleId) throws PZDBViolationException
    {
        Connection con=null;
        PreparedStatement psUpdate;
        try
        {
            con= Database.getConnection();
            String update="DELETE from  business_rule where id=?";
            psUpdate=con.prepareStatement(update);
            psUpdate.setString(1,businessRuleId);

            int result=psUpdate.executeUpdate();
            if(result>0)
            {
                return true;
            }

        }
        catch (SystemError systemError)
        {
            log.error("error while deleting record from business rule",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "deleteBusinessDefinition()", null, "Common", "DB Connection Issue", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch(MySQLIntegrityConstraintViolationException mySqlException)
        {
            log.error(" System error for deleting rule::",mySqlException);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "deleteBusinessDefinition()", null, "Common", "Connection exception while deleting rule", PZDBExceptionEnum.FOREIGN_KEY_CONSTRAINT, null, mySqlException.getMessage(), mySqlException.getCause());
        }
        catch (SQLException e)
        {
            log.error("SQL Error while deleting record from business rule",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "deleteBusinessDefinition()", null, "Common", "SQL Query Exception", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }

        return false;
    }

    //Business Profile Query

    public boolean deleteBusinessRuleOperation(String businessRuleId) throws PZDBViolationException
    {
        Connection con=null;
        PreparedStatement psUpdate;
        try
        {
            con= Database.getConnection();
            String update="DELETE FROM  business_rule_operation where ruleId=?";
            psUpdate=con.prepareStatement(update);
            psUpdate.setString(1,businessRuleId);

            int result=psUpdate.executeUpdate();
            if(result>0)
            {
                return true;
            }

        }
        catch (SystemError systemError)
        {
            log.error("error while deleting record from business rule operation",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "deleteBusinessRuleOperation()", null, "Common", "DB Connection Issue", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("SQL Error while deleting record from business rule opeartion",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "deleteBusinessRuleOperation()", null, "Common", "SQL Query Exception", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }

        return false;
    }

    public List<com.manager.vo.businessRuleVOs.ProfileVO> getListOfBusinessProfileVO(String partnerId,String id,String orderBy,PaginationVO paginationVO) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt=null, psCountOfBusinessProfile = null;
        ResultSet resultSet=null,resultSetCount = null ;
        List<com.manager.vo.businessRuleVOs.ProfileVO> businessProfileVOs=new ArrayList<com.manager.vo.businessRuleVOs.ProfileVO>();
        //String countQuery=null;
        StringBuilder countQuery=new StringBuilder();
        int count = 1;

        boolean andApply=false;

        try
        {
            //con= Database.getConnection();
            con= Database.getRDBConnection();

            StringBuilder query = new StringBuilder("Select * from `business_profile` ");
            if(functions.isValueNull(partnerId) || functions.isValueNull(id))
            {
                query.append(" where");

            }

            if(functions.isValueNull(partnerId))
            {
                query.append(" partner_id = ? ");
                andApply=true;
            }
            if(functions.isValueNull(id))
            {
                if(andApply)
                {
                    query.append(" and");
                }
                query.append(" profileid = ? ");
                andApply=true;
            }

            if(functions.isValueNull(orderBy))
            {
                query.append("order by "+orderBy);
            }

            countQuery.append("Select Count(*) from (").append(query.toString()).append(") as temp");
            if(paginationVO!=null)
                query.append("  limit "+paginationVO.getStart()+","+paginationVO.getEnd());

            pstmt=con.prepareStatement(query.toString());
            if(functions.isValueNull(partnerId))
            {
                pstmt.setString(count,partnerId);
                count++;

            }
            if(functions.isValueNull(id))
            {
                pstmt.setString(count, id);
                count++;

            }

            resultSet=pstmt.executeQuery();
            while(resultSet.next())
            {

                com.manager.vo.businessRuleVOs.ProfileVO profileVO=new com.manager.vo.businessRuleVOs.ProfileVO();
                profileVO.setId(resultSet.getString("profileid"));
                profileVO.setName(resultSet.getString("profile_name"));
                businessProfileVOs.add(profileVO);
            }

            if(paginationVO!=null)
            {
                count = 1;
                psCountOfBusinessProfile = con.prepareStatement(countQuery.toString());
                if(functions.isValueNull(partnerId))
                {
                    psCountOfBusinessProfile.setString(count,partnerId);
                    count++;

                }
                if(functions.isValueNull(id))
                {
                    psCountOfBusinessProfile.setString(count, id);
                    count++;

                }
                resultSetCount = psCountOfBusinessProfile.executeQuery();
                if (resultSetCount.next())
                {
                    paginationVO.setTotalRecords(resultSetCount.getInt(1));
                }
            }

        }

        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getListOfBusinessProfileVO()", null, "common", "Sql exception while getting Profile details", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getListOfBusinessProfileVO()", null, "common", "Sql exception while getting Profile details", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closeResultSet(resultSetCount);
            Database.closePreparedStatement(pstmt);
            Database.closePreparedStatement(psCountOfBusinessProfile);
            Database.closeConnection(con);
        }
        return businessProfileVOs;

    }

    public BusinessProfile getBusinessProfileWithAllDetails(String partnerId,String profileId,PaginationVO paginationVO) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt=null;
        ResultSet resultSet=null,resultSetCount=null;
        BusinessProfile businessProfileVO=new BusinessProfile();
        Map<String, com.manager.vo.businessRuleVOs.ProfileVO> profileVOMap=new HashMap<String, com.manager.vo.businessRuleVOs.ProfileVO>();
        List<com.manager.vo.businessRuleVOs.RuleVO> roleRuleVOs=null;
        List<com.manager.vo.businessRuleVOs.RuleOperation> ruleOperations=null;
        int count = 1;
        boolean and=false;
        StringBuilder countQuery=new StringBuilder();
        try
        {
            con= Database.getConnection();
            StringBuilder query = new StringBuilder("SELECT BP.profile_id AS profileId,BR.id AS ruleid,BR.name AS rulename,BR.description AS ruledesc,BR.label AS rulelabel,BR.rule_type as RuleType,BR.query as Query,BP.isApplicable AS ruleapplicable ,BP.value1 AS rulevalue1,BP.value2 AS rulevalue2,BM.profile_name as Profilename,BRO.operationId AS operationId,BRO.inputName AS inputName,BRO.regex AS regex,BRO.operator AS ruleoperator,BRO.comparator as comparator,BRO.inputType as inputType,BRO.enumValue as enumValue,BRO.isMandatory as isMandatory,BRO.operationType as operationType FROM `business_profile_rule` AS BP JOIN `business_profile` AS BM ON BP.profile_id = BM.profileid JOIN `business_rule` AS BR ON BR.id=BP.ruleid JOIN business_rule_operation as BRO ON BR.id=BRO.ruleId AND BP.operationid=BRO.operationId");

            if(functions.isValueNull(partnerId) || functions.isValueNull(profileId))
            {
                query.append(" where ");
            }
            if(functions.isValueNull(partnerId))
            {
                query.append(" BM.partner_id =?");
                and=true;
            }
            if(functions.isValueNull(profileId))
            {
                if(and)
                {
                    query.append(" and ");
                }
                query.append(" BP.profile_id =?");
                and=true;
            }
            query.append(" ORDER BY BP.profile_id ASC,BR.id ASC,BRO.operationId ASC ");
            countQuery.append("Select Count(*) from (").append(query.toString()).append(") as temp");
            if(paginationVO!=null)
                query.append("  limit "+paginationVO.getStart()+","+paginationVO.getEnd());
            pstmt=con.prepareStatement(query.toString());
            if(functions.isValueNull(partnerId))
            {
                pstmt.setString(count,partnerId);
                count++;
            }
            if(functions.isValueNull(profileId))
            {
                pstmt.setString(count, profileId);
            }
            resultSet=pstmt.executeQuery();
            while(resultSet.next())
            {

                com.manager.vo.businessRuleVOs.ProfileVO profileVO = new com.manager.vo.businessRuleVOs.ProfileVO();
                profileVO.setId(resultSet.getString("profileId"));
                profileVO.setName(resultSet.getString("Profilename"));
                com.manager.vo.businessRuleVOs.RuleVO ruleVO = new com.manager.vo.businessRuleVOs.RuleVO();
                ruleVO.setId(resultSet.getString("ruleid"));
                ruleVO.setName(resultSet.getString("rulename"));
                ruleVO.setIsApplicable("Y".equals(resultSet.getString("ruleapplicable")) ? true : false);

                ruleVO.setLabel(resultSet.getString("rulelabel"));
                ruleVO.setDescription(resultSet.getString("ruledesc"));
                ruleVO.setRuleType(resultSet.getString("RuleType"));

                ruleVO.setQuery(resultSet.getString("Query"));

                com.manager.vo.businessRuleVOs.RuleOperation ruleOperation =new RuleOperation();
                ruleOperation.setId(resultSet.getString("operationId"));
                ruleOperation.setRegex(resultSet.getString("regex"));
                ruleOperation.setInputName(resultSet.getString("inputName"));
                ruleOperation.setOperator(resultSet.getString("ruleoperator"));
                ruleOperation.setDataType(resultSet.getString("inputType"));
                ruleOperation.setEnumValue(resultSet.getString("enumValue"));
                ruleOperation.setValue1(resultSet.getString("rulevalue1"));
                ruleOperation.setValue2(resultSet.getString("rulevalue2"));
                ruleOperation.setComparator(resultSet.getString("comparator"));
                ruleOperation.setOperationType(resultSet.getString("operationType"));
                ruleOperation.setMandatory("Y".equalsIgnoreCase(resultSet.getString("isMandatory"))?true:false);



                if(profileVOMap.containsKey(profileVO.getId()))
                {
                    com.manager.vo.businessRuleVOs.ProfileVO profileVOInner=profileVOMap.get(profileVO.getId());
                    profileVOInner.setId(profileVO.getId());
                    profileVOInner.setName(profileVO.getName());

                    if(profileVOInner.getRules()==null)
                    {
                        roleRuleVOs=new ArrayList<com.manager.vo.businessRuleVOs.RuleVO>();
                        ruleOperations=new ArrayList<RuleOperation>();
                        ruleOperations.add(ruleOperation);
                        ruleVO.setRuleOperation(ruleOperations);
                        roleRuleVOs.add(ruleVO);
                        profileVOInner.setRules(roleRuleVOs);
                    }
                    else
                    {
                        roleRuleVOs=profileVOInner.getRules();
                        //CollectionUtils.exists(roleRuleVOs, Predicate<ro>)
                        log.debug("Index of object in LIST:::"+roleRuleVOs.indexOf(ruleVO));
                        if(roleRuleVOs.indexOf(ruleVO)>=0)
                        {
                            com.manager.vo.businessRuleVOs.RuleVO ruleVO1 =roleRuleVOs.get(roleRuleVOs.indexOf(ruleVO));
                            if(ruleVO1.getRuleOperation()!=null)
                            {
                                ruleOperations = ruleVO1.getRuleOperation();
                                ruleOperations.add(ruleOperation);
                            }
                            else
                            {
                                ruleOperations=new ArrayList<RuleOperation>();
                                ruleOperations.add(ruleOperation);
                            }
                            ruleVO1.setRuleOperation(ruleOperations);
                        }
                        else
                        {
                            ruleOperations=new ArrayList<RuleOperation>();
                            ruleOperations.add(ruleOperation);
                            ruleVO.setRuleOperation(ruleOperations);
                            roleRuleVOs.add(ruleVO);
                        }

                        //roleRuleVOs.remove(,ruleVO);
                        //roleRuleVOs.add(,ruleVO);


                    }
                    profileVOMap.put(profileVO.getId(),profileVOInner);

                }
                else
                {
                    com.manager.vo.businessRuleVOs.ProfileVO profileVOInner=new com.manager.vo.businessRuleVOs.ProfileVO();
                    profileVOInner.setId(profileVO.getId());
                    profileVOInner.setName(profileVO.getName());
                    roleRuleVOs=new ArrayList<com.manager.vo.businessRuleVOs.RuleVO>();
                    ruleOperations=new ArrayList<RuleOperation>();
                    ruleOperations.add(ruleOperation);
                    ruleVO.setRuleOperation(ruleOperations);
                    roleRuleVOs.add(ruleVO);
                    profileVOInner.setRules(roleRuleVOs);
                    profileVOMap.put(profileVO.getId(), profileVOInner);
                }
            }
            businessProfileVO.setProfiles(profileVOMap);

            if(paginationVO!=null)
            {
                count = 1;
                PreparedStatement psCountOfBusinessProfile = con.prepareStatement(countQuery.toString());
                if (functions.isValueNull(partnerId))
                {
                    psCountOfBusinessProfile.setString(count, partnerId);
                    count++;
                }
                if (functions.isValueNull(profileId))
                {
                    psCountOfBusinessProfile.setString(count, profileId);
                }
                resultSetCount = psCountOfBusinessProfile.executeQuery();
                if (resultSetCount.next())
                {
                    paginationVO.setTotalRecords(resultSetCount.getInt(1));
                }
            }
        }

        catch (SQLException e)
        {
            log.error("Exception while getting the data:::",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getBusinessProfileWithAllDetails()", null, "common", "Sql exception while getting Profile details", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            log.error("Exception while getting the data:::",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getBusinessProfileWithAllDetails()", null, "common", "Sql exception while getting Profile details", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return businessProfileVO;

    }

    public List<com.manager.vo.businessRuleVOs.RuleVO> getListOfBusinessRule(com.manager.vo.businessRuleVOs.RuleVO ruleVO,PaginationVO paginationVO) throws PZDBViolationException
    {
        Connection con=null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;
        ResultSet rsBusinessRule=null;

        int counter=1;
        List<com.manager.vo.businessRuleVOs.RuleVO> businessRuleVOList = new ArrayList<com.manager.vo.businessRuleVOs.RuleVO>();
        try
        {
            con= Database.getRDBConnection();
            StringBuffer query=new StringBuffer("Select id,name,description,label,rule_type,query from business_rule where id>0 ");
            if(ruleVO!=null && functions.isValueNull(ruleVO.getName()))
            {
                query.append(" and name =?");
            }
            if(ruleVO!=null && functions.isValueNull(ruleVO.getId()))
            {
                query.append(" and id =?");
            }
            String countQuery="Select Count(*) from ("+query.toString()+") as temp";
            if(paginationVO!=null)
                query.append("  limit "+paginationVO.getStart()+","+paginationVO.getEnd());
            preparedStatement=con.prepareStatement(query.toString());

            if(ruleVO!=null && functions.isValueNull(ruleVO.getName()))
            {
                preparedStatement.setString(counter,ruleVO.getName());
                counter++;
            }
            if(ruleVO!=null && functions.isValueNull(ruleVO.getId()))
            {
                preparedStatement.setString(counter,ruleVO.getId());
                counter++;
            }

            resultSet=preparedStatement.executeQuery();
            while(resultSet.next())
            {
                com.manager.vo.businessRuleVOs.RuleVO ruleVO1=new com.manager.vo.businessRuleVOs.RuleVO();
                ruleVO1.setId(resultSet.getString("id"));
                ruleVO1.setName(resultSet.getString("name"));
                ruleVO1.setDescription(resultSet.getString("description"));
                ruleVO1.setLabel(resultSet.getString("label"));
                ruleVO1.setRuleType(resultSet.getString("rule_type"));
                ruleVO1.setQuery(resultSet.getString("query"));
                businessRuleVOList.add(ruleVO1);
            }

            if(paginationVO!=null)
            {
                counter = 1;
                PreparedStatement psCountOfBusinessRule = con.prepareStatement(countQuery);
                if (ruleVO != null && functions.isValueNull(ruleVO.getName()))
                {
                    psCountOfBusinessRule.setString(counter, ruleVO.getName());
                    counter++;
                }
                if (ruleVO != null && functions.isValueNull(ruleVO.getId()))
                {
                    psCountOfBusinessRule.setString(counter, ruleVO.getId());
                    counter++;
                }

                rsBusinessRule = psCountOfBusinessRule.executeQuery();
                if (rsBusinessRule.next())
                {
                    paginationVO.setTotalRecords(rsBusinessRule.getInt(1));
                }
            }

        }
        catch (SystemError systemError)
        {
            log.error("error while fetching record from business rule",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getListOfBusinessRule()", null, "Common", "DB Connection Issue", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("SQL Error while fetching record from business rule",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getListOfBusinessRule()", null, "Common", "SQL Query Exception", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return businessRuleVOList;
    }

    /**
     * Getting the rule Map according the name and Id
     * @param ruleVO
     * @param paginationVO
     * @return
     * @throws com.payment.exceptionHandler.PZDBViolationException
     */
    public Map<String, com.manager.vo.businessRuleVOs.RuleVO> getMapOfBusinessRule(com.manager.vo.businessRuleVOs.RuleVO ruleVO, PaginationVO paginationVO) throws PZDBViolationException
    {
        Connection con=null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;
        ResultSet rsBusinessRule=null;
        StringBuilder countQuery=new StringBuilder();
        int counter=1;
        Map<String,com.manager.vo.businessRuleVOs.RuleVO> businessRuleVOList = new HashMap<String, com.manager.vo.businessRuleVOs.RuleVO>();
        try
        {
            con= Database.getRDBConnection();
            StringBuilder query=new StringBuilder("Select BRU.id,BRU.name,BRU.description,BRU.label,BRU.rule_type,BRU.query,BRO.operationId,BRO.inputName,BRO.regex,BRO.operator,BRO.comparator,BRO.sequence,BRO.inputType,BRO.enumValue,BRO.operationType from business_rule as BRU Left JOIN business_rule_operation as BRO ON BRU.id=BRO.ruleId where BRU.id>0");
            if(ruleVO!=null && functions.isValueNull(ruleVO.getName()))
            {
                query.append(" and BRU.name =?");
            }
            if(ruleVO!=null && functions.isValueNull(ruleVO.getId()))
            {
                query.append(" and BRU.id =?");
            }
            query.append(" ORDER BY BRO.sequence ASC");
            //String countQuery="Select Count(*) from ("+query.toString()+") as temp";
            countQuery.append("Select Count(*) from (").append(query.toString()).append(") as temp");
            if(paginationVO!=null)
                query.append("  limit "+paginationVO.getStart()+","+paginationVO.getEnd());
            preparedStatement=con.prepareStatement(query.toString());

            if(ruleVO!=null && functions.isValueNull(ruleVO.getName()))
            {
                preparedStatement.setString(counter,ruleVO.getName());
                counter++;
            }
            if(ruleVO!=null && functions.isValueNull(ruleVO.getId()))
            {
                preparedStatement.setString(counter,ruleVO.getId());
                counter++;
            }

            resultSet=preparedStatement.executeQuery();
            while(resultSet.next())
            {
                com.manager.vo.businessRuleVOs.RuleVO ruleVO1=new com.manager.vo.businessRuleVOs.RuleVO();
                ruleVO1.setId(resultSet.getString("id"));
                ruleVO1.setName(resultSet.getString("name"));
                ruleVO1.setDescription(resultSet.getString("description"));
                ruleVO1.setLabel(resultSet.getString("label"));
                ruleVO1.setRuleType(resultSet.getString("rule_type"));
                ruleVO1.setQuery(resultSet.getString("query"));
                if(businessRuleVOList.containsKey(ruleVO1.getId()))
                {

                    RuleOperation ruleOperation = new RuleOperation();
                    ruleOperation.setId(resultSet.getString("operationId"));
                    ruleOperation.setRegex(resultSet.getString("regex"));
                    ruleOperation.setInputName(resultSet.getString("inputName"));
                    ruleOperation.setOperator(resultSet.getString("operator"));
                    ruleOperation.setDataType(resultSet.getString("inputType"));
                    ruleOperation.setEnumValue(resultSet.getString("enumValue"));
                    ruleOperation.setComparator(resultSet.getString("comparator"));
                    ruleOperation.setOperationType(resultSet.getString("operationType"));
                    if(businessRuleVOList.get(ruleVO1.getId()).getRuleOperation()!=null)
                    {
                        List<RuleOperation> ruleOperations=businessRuleVOList.get(ruleVO1.getId()).getRuleOperation();
                        ruleOperations.add(ruleOperation);
                        ruleVO1.setRuleOperation(ruleOperations);
                    }
                    else
                    {
                        List<RuleOperation> ruleOperations = new ArrayList<RuleOperation>();
                        ruleOperations.add(ruleOperation);
                        ruleVO1.setRuleOperation(ruleOperations);
                    }

                }
                else
                {
                    List<RuleOperation> ruleOperations = new ArrayList<RuleOperation>();
                    RuleOperation ruleOperation = new RuleOperation();
                    ruleOperation.setId(resultSet.getString("operationId"));
                    ruleOperation.setRegex(resultSet.getString("regex"));
                    ruleOperation.setInputName(resultSet.getString("inputName"));
                    ruleOperation.setOperator(resultSet.getString("operator"));
                    ruleOperation.setDataType(resultSet.getString("inputType"));
                    ruleOperation.setEnumValue(resultSet.getString("enumValue"));
                    ruleOperation.setComparator(resultSet.getString("comparator"));
                    ruleOperation.setOperationType(resultSet.getString("operationType"));

                    ruleOperations.add(ruleOperation);
                    ruleVO1.setRuleOperation(ruleOperations);
                }
                businessRuleVOList.put(ruleVO1.getId(), ruleVO1);
            }

            if(paginationVO!=null)
            {
                counter = 1;
                PreparedStatement psCountOfBusinessRule = con.prepareStatement(countQuery.toString());
                if (ruleVO != null && functions.isValueNull(ruleVO.getName()))
                {
                    psCountOfBusinessRule.setString(counter, ruleVO.getName());
                    counter++;
                }
                if (ruleVO != null && functions.isValueNull(ruleVO.getId()))
                {
                    psCountOfBusinessRule.setString(counter, ruleVO.getId());
                    counter++;
                }

                rsBusinessRule = psCountOfBusinessRule.executeQuery();
                if (rsBusinessRule.next())
                {
                    paginationVO.setTotalRecords(rsBusinessRule.getInt(1));
                }
            }

        }
        catch (SystemError systemError)
        {
            log.error("error while fetching record from business rule",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getListOfBusinessRule()", null, "Common", "DB Connection Issue", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("SQL Error while fetching record from business rule",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getListOfBusinessRule()", null, "Common", "SQL Query Exception", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return businessRuleVOList;
    }

    //update for BusinessProfileMapping

    //insert for BusinessProfileMapping
    public boolean insertBusinessProfileMapping(com.manager.vo.businessRuleVOs.ProfileVO profileVO,String partnerID) throws PZDBViolationException
    {

        Connection conn = null;
        PreparedStatement pstmt=null;
        String query=null;
        try
        {

            conn = Database.getConnection();
            query = "INSERT INTO business_profile(profile_name,partner_id)VALUES(?,?)";
            pstmt= conn.prepareStatement(query);

            if(functions.isValueNull(profileVO.getName()))
            {

                pstmt.setString(1,profileVO.getName());
            }
            else
            {
                pstmt.setNull(1, Types.VARCHAR);
            }

            if(functions.isValueNull(partnerID))
            {
                pstmt.setString(2,partnerID);
            }


            int k = pstmt.executeUpdate();
            if (k>0)
            {
                ResultSet resultSet=pstmt.getGeneratedKeys();
                while(resultSet.next())
                {
                    profileVO.setId(resultSet.getString(1));
                }
                return true;
            }

        }
        catch (SystemError systemError)
        {
            log.error("Leaving insertBusinessProfileMapping throwing SQL Exception as System Error :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "insertBusinessProfileMapping()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("Leaving insertBusinessProfileMapping throwing SQL Exception as System Error :::: ",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "insertBusinessProfileMapping()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return false;
    }

    //insert for BusinessProfile

    public boolean updateBusinessProfileMapping(com.manager.vo.businessRuleVOs.ProfileVO profileVO,String partnerID) throws PZDBViolationException
    {
        Connection con = null;
        try
        {
            con= Database.getConnection();
            String update="update `business_profile` set profile_name =?,partner_id =? where profileid =?";
            PreparedStatement psupdateBusinessProfileMapping = con.prepareStatement(update.toString());
            psupdateBusinessProfileMapping.setString(1,profileVO.getName());
            psupdateBusinessProfileMapping.setString(2,partnerID);
            psupdateBusinessProfileMapping.setString(3,profileVO.getId());
            int rss=psupdateBusinessProfileMapping.executeUpdate();
            if(rss>0)
            {
                return true;
            }

        }
        catch (SystemError systemError)
        {
            log.error(" System error for updateBusinessProfileMapping::",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "updateBusinessProfileMapping()", null, "common", "Connection exception while connecting to Database", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("SQL exception for updateBusinessProfileMapping::",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "updateBusinessProfileMapping()", null, "common", "Connection exception while connecting to Database", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally {
            Database.closeConnection(con);
        }
        return false;
    }

    /*public boolean insertBusinessProfileDatabaseValue(Map<String,QueryInfo> queryInfoMap,String business_profile_id) throws PZDBViolationException
    {

        Connection conn = null;
        PreparedStatement pstmt=null;
        String query=null;
        try
        {

            conn = Database.getConnection();
            query = "INSERT INTO business_profile_rule_database(profile_rule_id,comparator,field_name,type,value)VALUES(?,?,?,?,?)";
            pstmt= conn.prepareStatement(query);
            if(queryInfoMap!=null)
            {
                for (Map.Entry<String,QueryInfo> queryInfoEntry:queryInfoMap.entrySet())
                {
                    QueryInfo queryInfo= queryInfoEntry.getValue();

                    pstmt.setString(1, business_profile_id);

                    if(functions.isValueNull(queryInfo.getComparator()))
                    {
                        pstmt.setString(2, queryInfo.getComparator());
                    }
                    else
                    {
                        pstmt.setNull(2, Types.VARCHAR);
                    }
                    if(functions.isValueNull(queryInfo.getName()))
                    {
                        pstmt.setString(3, queryInfo.getName());
                    }
                    else
                    {
                        pstmt.setNull(3, Types.VARCHAR);
                    }

                    if(functions.isValueNull(queryInfo.getType()))
                    {
                        pstmt.setString(4, queryInfo.getType());
                    }
                    else
                    {
                        pstmt.setNull(4, Types.VARCHAR);
                    }
                    if(functions.isValueNull(queryInfo.getValue()))
                    {
                        pstmt.setString(5, queryInfo.getValue());
                    }
                    else
                    {
                        pstmt.setNull(5, Types.VARCHAR);
                    }

                    pstmt.addBatch();
                }

                int[] k = pstmt.executeBatch();
                if (k.length>0)
                {
                    return true;
                }
            }

        }
        catch (SystemError systemError)
        {
            log.error("Leaving insertBusinessProfile throwing SQL Exception as System Error :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(),"insertBusinessProfile()",null,"Common","System error while connecting to  table ",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("Leaving insertBusinessProfile throwing SQL Exception as System Error :::: ",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(),"insertBusinessProfile()",null,"Common","System error while connecting to  table ",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return false;
    }*/

    //update for BusinessProfile

    public boolean insertBusinessProfile(com.manager.vo.businessRuleVOs.RuleVO ruleVO,String profileId) throws PZDBViolationException
    {

        Connection conn = null;
        PreparedStatement pstmt=null;
        String query=null;
        try
        {

            conn = Database.getConnection();
            query = "INSERT INTO business_profile_rule(profile_id,ruleid,operationid,isApplicable,value1,value2)VALUES(?,?,?,?,?,?)";
            pstmt= conn.prepareStatement(query);

            log.debug("profile ID:::"+profileId);
            if(ruleVO.getRuleOperation()!=null)
            {
                for(RuleOperation ruleOperation:ruleVO.getRuleOperation())
                {
                    pstmt.setString(1, profileId);

                    pstmt.setString(2, ruleVO.getId());

                    pstmt.setString(3, ruleOperation.getId());

                    if (ruleVO.isApplicable())
                    {
                        pstmt.setString(4, "Y");
                    }
                    else
                    {
                        pstmt.setString(4, "N");
                    }

                    if(functions.isValueNull(ruleOperation.getValue1()))
                    {
                        pstmt.setString(5, ruleOperation.getValue1());
                    }
                    else
                    {
                        pstmt.setNull(5, Types.VARCHAR);
                    }

                    if(functions.isValueNull(ruleOperation.getValue2()))
                    {
                        pstmt.setString(6, ruleOperation.getValue2());
                    }
                    else
                    {
                        pstmt.setNull(6, Types.VARCHAR);
                    }

                    pstmt.addBatch();
                }

                int[] k = pstmt.executeBatch();
                if (k.length>0)
                {
                    return true;
                }
            }

        }
        catch (SystemError systemError)
        {
            log.error("Leaving insertBusinessProfile throwing SQL Exception as System Error :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "insertBusinessProfile()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("Leaving insertBusinessProfile throwing SQL Exception as System Error :::: ",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "insertBusinessProfile()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return false;
    }

    public boolean updateBusinessProfile(com.manager.vo.businessRuleVOs.RuleVO ruleVO,String profileId) throws PZDBViolationException
    {
        Connection con = null;
        try
        {
            con= Database.getConnection();
            String update="update `business_profile_rule` set profile_id =?,ruleid =?,isApplicable=?,value1=?,value2=? where id =?";
            PreparedStatement psupdateBusinessProfile = con.prepareStatement(update.toString());
            psupdateBusinessProfile.setString(1,profileId);
            psupdateBusinessProfile.setString(2,ruleVO.getId());
            if(ruleVO.isApplicable())
            {
                psupdateBusinessProfile.setString(3,"Y");
            }
            else
            {
                psupdateBusinessProfile.setString(3,"N");
            }
            //psupdateBusinessProfile.setString(4,ruleVO.getValue1());
            //psupdateBusinessProfile.setString(5,ruleVO.getValue2());

            log.debug(" update query::"+update);
            int rss=psupdateBusinessProfile.executeUpdate();
            if(rss>0)
            {
                return true;
            }

        }
        catch (SystemError systemError)
        {
            log.error(" System error for updateBusinessProfile::",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "updateBusinessProfile()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("SQL exception for updateBusinessProfile::",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "updateBusinessProfile()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally {
            Database.closeConnection(con);
        }
        return false;
    }

    /**
     * Delete the Business profiulr related to the Profile
     * @param profileId
     * @return
     */
    public boolean deleteBusinessRuleForProfile(String profileId) throws PZDBViolationException
    {
        Connection con = null;
        try
        {
            con= Database.getConnection();
            String delete="Delete from `business_profile_rule` where profile_id =?";
            PreparedStatement psDelete = con.prepareStatement(delete.toString());
            psDelete.setString(1,profileId);
            int rss=psDelete.executeUpdate();
            if(rss>0)
            {
                return true;
            }

        }
        catch (SystemError systemError)
        {
            log.error(" System error for updateRiskProfile::",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "deleteBusinessRuleForProfile()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("SQL exception for updateRiskProfile::",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "deleteBusinessRuleForProfile()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally {
            Database.closeConnection(con);
        }
        return false;
    }


    //get User Profile Details

    public boolean deleteBusinessProfile(String profileId) throws PZDBViolationException
    {
        Connection con = null;
        try
        {
            con= Database.getConnection();
            String delete="Delete from `business_profile` where profileid =?";
            PreparedStatement psDelete = con.prepareStatement(delete.toString());
            psDelete.setString(1,profileId);
            int rss=psDelete.executeUpdate();
            if(rss>0)
            {
                return true;
            }

        }
        catch (SystemError systemError)
        {
            log.error(" System error for deleting profile::",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "deleteBusinessProfile()", null, "Common", "Connection exception while deleting profile", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch(MySQLIntegrityConstraintViolationException mySqlException)
        {
            log.error(" System error for deleting profile::",mySqlException);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "deleteBusinessProfile()", null, "Common", "Connection exception while deleting profile", PZDBExceptionEnum.FOREIGN_KEY_CONSTRAINT, null, mySqlException.getMessage(), mySqlException.getCause());
        }
        catch (SQLException e)
        {
            log.error("SQL exception for updateRiskProfile::",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "deleteBusinessProfile()", null, "Common", "Connection exception while deleting profile", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally {
            Database.closeConnection(con);
        }
        return false;
    }

    public HashMap<String, MerchantVO> getMapOfUserSetting(String memberid,String partnerId,PaginationVO paginationVO)throws PZDBViolationException
    {
        Connection con=null;
        PreparedStatement preparedStatement=null, psCountOfUserSetting = null;
        ResultSet resultSet=null;
        ResultSet rsUserSetting=null;
        StringBuilder countQuery=new StringBuilder();
        int counter=1;
        HashMap<String,MerchantVO> merchantVOHashMap=new HashMap<String, MerchantVO>();
        try
        {
            //con= Database.getConnection();
            con= Database.getRDBConnection();
            StringBuilder query=new StringBuilder("Select * from user_profile where id>0 ");
            if(functions.isValueNull(memberid))
            {
                query.append(" and memberid =?");
            }
            if(functions.isValueNull(partnerId))
            {
                query.append(" and partnerid =?");
            }
            countQuery.append("Select Count(*) from (").append(query.toString()).append(") as temp");

            if(paginationVO!=null)
                query.append("  limit "+paginationVO.getStart()+","+paginationVO.getEnd());
            preparedStatement=con.prepareStatement(query.toString());

            if(functions.isValueNull(memberid))
            {
                preparedStatement.setString(counter,memberid);
                counter++;
            }
            if(functions.isValueNull(partnerId))
            {
                preparedStatement.setString(counter,partnerId);
                counter++;
            }
            log.debug("Query::::"+query.toString());
            log.debug("1::::"+memberid);
            log.debug("2::::"+partnerId);
            resultSet=preparedStatement.executeQuery();
            while(resultSet.next())
            {
                MerchantVO merchantVO=new MerchantVO();
                merchantVO.setRiskProfile(resultSet.getString("risk_profileid"));
                merchantVO.setBusinessProfile(resultSet.getString("business_profileid"));
                merchantVO.setCurrency(resultSet.getString("currency"));
                merchantVO.setDefaultMode(resultSet.getString("default_mode"));
                merchantVO.setOfflineTransactionURL(resultSet.getString("offline_transactionurl"));
                merchantVO.setOnlineTransactionURL(resultSet.getString("online_transactionurl"));
                merchantVO.setOnlineThreshold(resultSet.getInt("online_threshold"));
                merchantVO.setOfflineThreshold(resultSet.getInt("offline_threshold"));
                merchantVO.setAddressVerification(resultSet.getString("addressVerification"));
                merchantVO.setAddressDetailDisplay(resultSet.getString("addressDetailDisplay"));
                merchantVO.setAutoRedirect(resultSet.getString("autoRedirect"));

                merchantVOHashMap.put(resultSet.getString("id"),merchantVO);
            }

            if(paginationVO!=null)
            {
                counter = 1;
                psCountOfUserSetting = con.prepareStatement(countQuery.toString());
                if (functions.isValueNull(memberid))
                {
                    psCountOfUserSetting.setString(counter, memberid);
                    counter++;
                }
                if (functions.isValueNull(partnerId))
                {
                    psCountOfUserSetting.setString(counter, partnerId);
                    counter++;
                }

                rsUserSetting = psCountOfUserSetting.executeQuery();
                if (rsUserSetting.next())
                {
                    paginationVO.setTotalRecords(rsUserSetting.getInt(1));
                }
            }

        }
        catch (SystemError systemError)
        {
            log.error("error while fetching record from User Profile Details",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getListOfRiskRule()", null, "Common", "DB Connection Issue", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("SQL Error while fetching record from User Profile Details",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getListOfRiskRule()", null, "Common", "SQL Query Exception", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closeResultSet(rsUserSetting);
            Database.closePreparedStatement(preparedStatement);
            Database.closePreparedStatement(psCountOfUserSetting);
            Database.closeConnection(con);
        }
        return merchantVOHashMap;
    }

    public UserSetting getSingleUserProfile(String userProfileId) throws PZDBViolationException
    {
        Connection con=null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;
        ResultSet rsUserProfile=null;

        UserSetting userSetting=null;
        try
        {
            con= Database.getRDBConnection();
            //String query= "SELECT UP.*,M.clkey,M.company_name,M.contact_emails,M.autoredirect,P.addressdetaildisplay,P.addressvalidation,P.clkey FROM user_profile AS UP JOIN members AS M JOIN partners AS P ON UP.memberid=M.memberid AND UP.partnerid=P.partnerId WHERE UP.id =?";
            String query= "SELECT UP.*,P.clkey,P.addressvalidation,P.addressdetaildisplay,P.autoRedirect,P.contact_emails,P.partnerName FROM user_profile AS UP JOIN partners AS P WHERE UP.partnerid=P.partnerId AND UP.id=?";
            preparedStatement=con.prepareStatement(query);
            preparedStatement.setString(1,userProfileId);
            resultSet=preparedStatement.executeQuery();
            log.debug("query for userprofile----"+preparedStatement);

            if(resultSet.next())
            {
                userSetting=new UserSetting();
                Map<String,MemberDetails> memberDetailsHashMap = new HashMap<String, MemberDetails>();

                MemberDetails memberDetails = new MemberDetails();
                MerchantVO merchantVO  = new MerchantVO();
                merchantVO.setPartnerId(resultSet.getString("partnerId"));
                //merchantVO.setMemberId(resultSet.getString("memberid"));
                merchantVO.setKey(resultSet.getString("clkey"));
                merchantVO.setCurrency(resultSet.getString("currency"));
                merchantVO.setCompanyName(resultSet.getString("partnerName"));
                merchantVO.setContactEmail(resultSet.getString("contact_emails"));
                merchantVO.setAutoRedirect(resultSet.getString("autoRedirect"));
                merchantVO.setAddressVerification(resultSet.getString("addressvalidation"));
                merchantVO.setAddressDetailDisplay(resultSet.getString("addressdetaildisplay"));
                merchantVO.setDefaultMode(resultSet.getString("default_mode"));
                merchantVO.setRiskProfile(resultSet.getString("risk_profileid"));
                merchantVO.setBusinessProfile(resultSet.getString("business_profileid"));
                merchantVO.setOfflineTransactionURL(resultSet.getString("offline_transactionurl"));
                merchantVO.setOnlineTransactionURL(resultSet.getString("online_transactionurl"));
                merchantVO.setOnlineThreshold(resultSet.getInt("online_threshold"));
                merchantVO.setOfflineThreshold(resultSet.getInt("offline_threshold"));

                TemplateVO templateVO = new TemplateVO();
                templateVO.setBackgroundColor(resultSet.getString("background"));
                templateVO.setForegroundColor(resultSet.getString("foreground"));
                templateVO.setFontColor(resultSet.getString("fontcolor"));
                templateVO.setLogo(resultSet.getString("logo"));

                memberDetails.setUserSetting(merchantVO);
                memberDetails.setTemplateSetting(templateVO);
                memberDetailsHashMap.put(resultSet.getString("id"),memberDetails);

                userSetting.setMembers(memberDetailsHashMap);
            }
        }
        catch (SystemError systemError)
        {
            log.error("error while fetching record from User Profile Details",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getSingleUserProfile()", null, "Common", "DB Connection Issue", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("error while fetching record from User Profile Details",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getSingleUserProfile()", null, "Common", "DB Connection Issue", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return userSetting;
    }

    //Get all details of the User profile for the partner
    public UserSetting getMapUserProfile(String partnerId) throws PZDBViolationException
    {
        Connection con=null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;

        Map<String,MemberDetails> memberDetailsHashMap = new HashMap<String, MemberDetails>();
        UserSetting userSetting=new UserSetting();
        try
        {
            con= Database.getRDBConnection();
            //String query= "Select UP.*,M.clkey,M.company_name,M.contact_emails,M.autoredirect,UP.addressVerification,UP.addressDetailDisplay from user_profile as UP join members as M on UP.memberid=M.memberid where UP.partnerid =?";
            String query= "SELECT UP.*,P.addressdetaildisplay,P.addressvalidation,P.clkey,P.autoRedirect,P.partnerId,P.partnerName,P.contact_emails FROM user_profile AS UP JOIN partners AS P  WHERE UP.partnerid =? AND UP.partnerid=P.partnerId";
            preparedStatement=con.prepareStatement(query);
            preparedStatement.setString(1,partnerId);
            resultSet=preparedStatement.executeQuery();
            log.debug("query for generate xml----"+preparedStatement);

            while(resultSet.next())
            {
                MemberDetails memberDetails = new MemberDetails();
                MerchantVO merchantVO  = new MerchantVO();
                merchantVO.setPartnerId(resultSet.getString("partnerId"));
                //merchantVO.setMemberId(resultSet.getString("memberid"));
                merchantVO.setKey(resultSet.getString("clkey"));
                merchantVO.setCurrency(resultSet.getString("currency"));
                merchantVO.setCompanyName(resultSet.getString("partnerName"));
                merchantVO.setContactEmail(resultSet.getString("contact_emails"));
                merchantVO.setAddressVerification(resultSet.getString("addressvalidation"));
                merchantVO.setAddressDetailDisplay(resultSet.getString("addressdetaildisplay"));
                merchantVO.setAutoRedirect(resultSet.getString("autoRedirect"));
                merchantVO.setDefaultMode(resultSet.getString("default_mode"));
                merchantVO.setRiskProfile(resultSet.getString("risk_profileid"));
                merchantVO.setBusinessProfile(resultSet.getString("business_profileid"));
                merchantVO.setOfflineTransactionURL(resultSet.getString("offline_transactionurl"));
                merchantVO.setOnlineTransactionURL(resultSet.getString("online_transactionurl"));
                merchantVO.setOnlineThreshold(resultSet.getInt("online_threshold"));
                merchantVO.setOfflineThreshold(resultSet.getInt("offline_threshold"));

                TemplateVO templateVO = new TemplateVO();
                templateVO.setBackgroundColor(resultSet.getString("background"));
                templateVO.setForegroundColor(resultSet.getString("foreground"));
                templateVO.setFontColor(resultSet.getString("fontcolor"));
                templateVO.setLogo(resultSet.getString("logo"));

                memberDetails.setUserSetting(merchantVO);
                memberDetails.setTemplateSetting(templateVO);
                memberDetailsHashMap.put(resultSet.getString("id"),memberDetails);
            }
            userSetting.setMembers(memberDetailsHashMap);
        }
        catch (SystemError systemError)
        {
            log.error("error while fetching record from User Profile Details",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getSingleUserProfile()", null, "Common", "DB Connection Issue", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("error while fetching record from User Profile Details",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getSingleUserProfile()", null, "Common", "DB Connection Issue", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return userSetting;
    }

    public boolean insertUserProfile(MerchantVO merchantVO,TemplateVO templateVO,String partnerId) throws PZDBViolationException
    {
        Connection con=null;
        PreparedStatement preparedStatement=null;
        int resultSet=0;
        ResultSet rsUserProfile=null;

        UserSetting userSetting=null;
        try
        {
            con= Database.getConnection();
            String insert= " INSERT INTO `user_profile`(`risk_profileid`,`business_profileid`,`partnerid`,`currency`,`default_mode`,`offline_transactionurl`,`online_transactionurl`,`online_threshold`,`offline_threshold`,`background`,`foreground`,`fontcolor`,`logo`,`addressVerification`,`addressDetailDisplay`,`autoRedirect`) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            preparedStatement=con.prepareStatement(insert);
            log.debug("Insert query in insertUserProfile method----"+preparedStatement);
            //preparedStatement.setString(1,merchantVO.getMemberId());
            preparedStatement.setString(1,merchantVO.getRiskProfile());
            preparedStatement.setString(2,merchantVO.getBusinessProfile());
            preparedStatement.setString(3,partnerId);
            preparedStatement.setString(4,merchantVO.getCurrency());
            preparedStatement.setString(5,merchantVO.getDefaultMode());
            preparedStatement.setString(6,merchantVO.getOfflineTransactionURL());
            preparedStatement.setString(7,merchantVO.getOnlineTransactionURL());
            preparedStatement.setString(8,String.valueOf(merchantVO.getOnlineThreshold()));
            preparedStatement.setString(9,String.valueOf(merchantVO.getOfflineThreshold()));
            preparedStatement.setString(10,templateVO.getBackgroundColor());
            preparedStatement.setString(11,templateVO.getForegroundColor());
            preparedStatement.setString(12,templateVO.getFontColor());
            preparedStatement.setString(13,templateVO.getLogo());
            preparedStatement.setString(14,merchantVO.getAddressVerification());
            preparedStatement.setString(15,merchantVO.getAddressDetailDisplay());
            preparedStatement.setString(16,merchantVO.getAutoRedirect());
            resultSet=preparedStatement.executeUpdate();

            if(resultSet>0)
            {
               return true;

            }
        }
        catch (SystemError systemError)
        {
            log.error("error while fetching record from User Profile Details",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "insertUserProfile()", null, "Common", "DB Connection Issue", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("error while fetching record from User Profile Details",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "insertUserProfile()", null, "Common", "DB Connection Issue", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return false;
    }

    public boolean updateUserProfile(MerchantVO merchantVO,TemplateVO templateVO,String profileId) throws PZDBViolationException
    {
        Connection con=null;
        PreparedStatement preparedStatement=null;
        int resultSet=0;
        ResultSet rsUserProfile=null;

        UserSetting userSetting=null;
        try
        {
            con= Database.getConnection();
            String update= " update  `user_profile` set `risk_profileid`=?,`business_profileid`=?,`currency`=?,`default_mode`=?,`offline_transactionurl`=?,`online_transactionurl`=?,`online_threshold`=?,`offline_threshold`=?,`background`=?,`foreground`=?,`fontcolor`=?,`logo`=?,addressVerification=?,addressDetailDisplay=?,autoRedirect=? where id=?";
            preparedStatement=con.prepareStatement(update);
            log.debug("update query in updateUserProfile method----"+preparedStatement);
            //preparedStatement.setString(1,merchantVO.getMemberId());
            preparedStatement.setString(1,merchantVO.getRiskProfile());
            preparedStatement.setString(2,merchantVO.getBusinessProfile());
            preparedStatement.setString(3,merchantVO.getCurrency());
            preparedStatement.setString(4,merchantVO.getDefaultMode());
            preparedStatement.setString(5,merchantVO.getOfflineTransactionURL());
            preparedStatement.setString(6,merchantVO.getOnlineTransactionURL());
            preparedStatement.setString(7,String.valueOf(merchantVO.getOnlineThreshold()));
            preparedStatement.setString(8,String.valueOf(merchantVO.getOfflineThreshold()));
            preparedStatement.setString(9,templateVO.getBackgroundColor());
            preparedStatement.setString(10,templateVO.getForegroundColor());
            preparedStatement.setString(11,templateVO.getFontColor());
            preparedStatement.setString(12,templateVO.getLogo());
            preparedStatement.setString(13,merchantVO.getAddressVerification());
            preparedStatement.setString(14,merchantVO.getAddressDetailDisplay());
            preparedStatement.setString(15,merchantVO.getAutoRedirect());
            preparedStatement.setString(16,profileId);
            resultSet=preparedStatement.executeUpdate();

            if(resultSet>0)
            {
                return  true;
            }
        }
        catch (SystemError systemError)
        {
            log.error("error while fetching record from User Profile Details",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "updateUserProfile()", null, "Common", "DB Connection Issue", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("error while fetching record from User Profile Details",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "updateUserProfile()", null, "Common", "DB Connection Issue", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return false;
    }

    public boolean deleteUserProfile(String profileId) throws PZDBViolationException
    {
        Connection con = null;
        try
        {
            con= Database.getConnection();
            String delete="Delete from `user_profile` where id =?";
            PreparedStatement psDelete = con.prepareStatement(delete.toString());
            psDelete.setString(1,profileId);
            int rss=psDelete.executeUpdate();
            if(rss>0)
            {
                return true;
            }

        }
        catch (SystemError systemError)
        {
            log.error(" System error for deleting profile::",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "deleteUserProfile()", null, "Common", "Connection exception while deleting profile", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("SQL exception for updateRiskProfile::",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "deleteUserProfile()", null, "Common", "Connection exception while deleting profile", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally {
            Database.closeConnection(con);
        }
        return false;
    }

    //Pay Ife table info
    public Map<String,List<PayIfeTableInfo>> getAllPayIfeTableInfo(String tableId,String tableName) throws PZDBViolationException
    {
        Connection con = null;
        ResultSet resultSet =null;
        Map<String,List<PayIfeTableInfo>> payIfeTableInfoMap= new HashMap<String, List<PayIfeTableInfo>>();
        try
        {
            con= Database.getRDBConnection();
            StringBuffer query=new StringBuffer("Select PT.tableid,PT.table_name,PT.Description,PT.aliasname,PF.field_name,PF.description,PF.data_type from payife_tables as PT JOIN payife_fields as PF on PT.tableid =PF.tableid where PT.tableid >=0");
            if(functions.isValueNull(tableId))
            {
                query.append("PT.tableid =?");
            }
            if(functions.isValueNull(tableName))
            {
                query.append("PT.table_name=?");
            }
            PreparedStatement psQuery = con.prepareStatement(query.toString());
            if(functions.isValueNull(tableId))
            {
                psQuery.setString(1, tableId);
            }
            if(functions.isValueNull(tableName))
            {
                psQuery.setString(2, tableName);
            }

            resultSet=psQuery.executeQuery();
            while(resultSet.next())
            {
                PayIfeTableInfo payIfeTableInfo = new PayIfeTableInfo();
                payIfeTableInfo.setTableId(resultSet.getString("tableid"));
                payIfeTableInfo.setTableName(resultSet.getString("table_name"));
                payIfeTableInfo.setTableAliasName(resultSet.getString("aliasname"));
                payIfeTableInfo.setFieldName(resultSet.getString("field_name"));
                payIfeTableInfo.setDescription(resultSet.getString("description"));
                payIfeTableInfo.setDataType(resultSet.getString("data_type"));

                List<PayIfeTableInfo> payIfeTableInfos=null;
                if(payIfeTableInfoMap.containsKey(payIfeTableInfo.getTableId()))
                {
                    payIfeTableInfos=payIfeTableInfoMap.get(payIfeTableInfo.getTableId());
                    payIfeTableInfos.add(payIfeTableInfo);
                }
                else
                {
                    payIfeTableInfos = new ArrayList<PayIfeTableInfo>();
                    payIfeTableInfos.add(payIfeTableInfo);

                }
                payIfeTableInfoMap.put(payIfeTableInfo.getTableId(),payIfeTableInfos);

            }

        }
        catch (SystemError systemError)
        {
            log.error(" System error for deleting profile::",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getAllPayIfeTableInfo()", null, "Common", "Connection exception while getting all tables information ", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("SQL exception for updateRiskProfile::",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getAllPayIfeTableInfo()", null, "Common", "Connection exception while getting all tables information ", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally {
            Database.closeConnection(con);
        }
        return payIfeTableInfoMap;
    }

    public Map<String,PayIfeTableInfo> getAllPayIfeFieldsInfo(String tableId,String tableName,Set<String> tableAliasName) throws PZDBViolationException
    {
        Connection con = null;
        ResultSet resultSet =null;
        Map<String,PayIfeTableInfo> payIfeTableInfoMap= new HashMap<String, PayIfeTableInfo>();
        try
        {
            con= Database.getRDBConnection();
            StringBuffer query=new StringBuffer("Select PT.tableid,PT.table_name,PT.aliasname,PF.field_name,PF.description,PF.data_type,PF.enum_value from payife_tables as PT JOIN payife_fields as PF on PT.tableid =PF.tableid where PT.tableid >=0");
            if(functions.isValueNull(tableId))
            {
                query.append("PT.tableid =?");
            }
            if(functions.isValueNull(tableName))
            {
                query.append("PT.table_name=?");
            }
            PreparedStatement psQuery = con.prepareStatement(query.toString());
            if(functions.isValueNull(tableId))
            {
                psQuery.setString(1, tableId);
            }
            if(functions.isValueNull(tableName))
            {
                psQuery.setString(2, tableName);
            }

            resultSet=psQuery.executeQuery();
            while(resultSet.next())
            {
                PayIfeTableInfo payIfeTableInfo = new PayIfeTableInfo();
                payIfeTableInfo.setTableId(resultSet.getString("tableid"));
                payIfeTableInfo.setTableName(resultSet.getString("table_name"));
                payIfeTableInfo.setTableAliasName(resultSet.getString("aliasname"));
                payIfeTableInfo.setFieldName(resultSet.getString("field_name"));
                payIfeTableInfo.setDescription(resultSet.getString("description"));
                payIfeTableInfo.setDataType(resultSet.getString("data_type"));
                payIfeTableInfo.setEnumValue(resultSet.getString("enum_value"));

                payIfeTableInfoMap.put(payIfeTableInfo.getTableAliasName()+"."+payIfeTableInfo.getFieldName(),payIfeTableInfo);
                if(tableAliasName!=null)
                {
                    tableAliasName.add(payIfeTableInfo.getTableAliasName());
                }
            }

        }
        catch (SystemError systemError)
        {
            log.error(" System error for deleting profile::",systemError);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getAllPayIfeTableInfo()", null, "Common", "Connection exception while getting all tables information ", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("SQL exception for updateRiskProfile::",e);
            PZExceptionHandler.raiseDBViolationException(ProfileManagementDAO.class.getName(), "getAllPayIfeTableInfo()", null, "Common", "Connection exception while getting all tables information ", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally {
            Database.closeConnection(con);
        }
        return payIfeTableInfoMap;
    }

    public TreeMap<String, String> getUsernameList(String roles)
    {
        TreeMap dataHash = new TreeMap();
        Connection conn = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            String query = "select login from user where roles=? ORDER BY login ASC";
            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1,roles);
            log.debug("Active memberid query::::"+p);
            ResultSet resultSet = p.executeQuery();

            while(resultSet.next())
            {
                dataHash.put(resultSet.getString("login"),resultSet.getString("login"));
            }
        }
        catch (SQLException se)
        {
            log.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se)
        {
            log.error("System Error ", se);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return dataHash;
    }

}
