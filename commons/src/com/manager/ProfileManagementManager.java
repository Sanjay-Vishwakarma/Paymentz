package com.manager;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.dao.ProfileManagementDAO;
import com.manager.enums.*;
import com.manager.vo.PaginationVO;
import com.manager.vo.PayIfeTableInfo;
import com.manager.vo.businessRuleVOs.BusinessProfile;
import com.manager.vo.riskRuleVOs.ProfileVO;
import com.manager.vo.riskRuleVOs.RiskProfile;
import com.manager.vo.riskRuleVOs.RuleOperation;
import com.manager.vo.riskRuleVOs.RuleVO;
import com.manager.vo.userProfileVOs.MerchantVO;
import com.manager.vo.userProfileVOs.TemplateVO;
import com.manager.vo.userProfileVOs.UserSetting;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by NIKET on 26/08/2015.
 */
public class ProfileManagementManager
{
    private static Logger logger = new Logger(PartnerManager.class.getName());
    ProfileManagementDAO profileManagementDAO=new ProfileManagementDAO();
    private Functions functions = new Functions();


    //OPTION TAG formation

    /**
     * This is to get the Rule type drop down options
     * @param selectedItem
     * @return
     */
    public StringBuffer getOptionTagForRuleType(String selectedItem)
    {
        StringBuffer optionList= new StringBuffer("<option value=\"\">Select Rule Type</option>");

        for(RuleTypeEnum ruleType: RuleTypeEnum.values())
        {
            if(functions.isValueNull(selectedItem) && (ruleType.name()).equals(selectedItem))
            {
                optionList.append(" <option value=\""+ruleType.name()+"\"   selected >"+ruleType.name()+"</option>");
            }
            else
            {
                optionList.append("  <option value=\""+ruleType.name()+"\"  >"+ruleType.name()+"</option>");
            }

        }
        return optionList;
    }

    /**
     * For getting the option tag for all input name
     * @param selectedItem
     * @return
     */
    public StringBuffer getOptionTagForInputName(List<String> selectedItem,List<InputNameEnum> notIncludeInputName)
    {
        StringBuffer optionList= new StringBuffer("<option value=''>Select Input Name</option>");

        for(InputNameEnum inputName: InputNameEnum.values())
        {
            if(notIncludeInputName!=null && notIncludeInputName.contains(inputName))
            {

            }
            else
            {
                if (selectedItem!=null && selectedItem.contains(inputName.name()))
                {
                    optionList.append(" <option value='" + inputName.name() + "'   selected >" + inputName.name() + "</option>");
                }
                else
                {
                    optionList.append("  <option value='" + inputName.name() + "' >" + inputName.name() + "</option>");
                }
            }

        }
        return optionList;
    }

    /**
     * For getting the option tag for all input + Output name
     * @param selectedItem
     * @return
     */
    public StringBuffer getOptionTagForOutputName(List<String> selectedItem,List<InputNameEnum> notIncludeInputName,List<OutputNameEnum> notIncludeOutputName)
    {
        StringBuffer optionList= new StringBuffer("<option value=''>Select Input Name</option>");

        for(InputNameEnum inputName: InputNameEnum.values())
        {
            if(notIncludeInputName!=null && notIncludeInputName.contains(inputName))
            {

            }
            else
            {
                if (selectedItem!=null && selectedItem.contains(inputName.name()))
                {
                    optionList.append(" <option value='" + inputName.name() + "'   selected >" + inputName.name() + "</option>");
                }
                else
                {
                    optionList.append("  <option value='" + inputName.name() + "' >" + inputName.name() + "</option>");
                }
            }

        }
        for(OutputNameEnum outputNameEnum: OutputNameEnum.values())
        {
            if(notIncludeOutputName!=null && notIncludeOutputName.contains(outputNameEnum))
            {

            }
            else
            {
                if (selectedItem!=null && selectedItem.contains(outputNameEnum.name()))
                {
                    optionList.append(" <option value='" + outputNameEnum.name() + "'   selected >" + outputNameEnum.name() + "</option>");
                }
                else
                {
                    optionList.append("  <option value='" + outputNameEnum.name() + "' >" + outputNameEnum.name() + "</option>");
                }
            }

        }
        return optionList;
    }

    public StringBuffer getOptionTagForDataType(String selectedItem)
    {
        StringBuffer optionList= new StringBuffer();

        for(DataType inputName: DataType.values())
        {

                if (functions.isValueNull(selectedItem) && selectedItem.equals(inputName.name()))
                {
                    optionList.append(" <option value='" + inputName.name() + "'   selected >" + inputName.toString() + "</option>");
                }
                else
                {
                    optionList.append("  <option value='" + inputName.name() + "' >" + inputName.toString() + "</option>");
                }
        }
        return optionList;
    }

    public StringBuffer getOptionTagForOperationType(String selectedItem)
    {
        StringBuffer optionList= new StringBuffer();

        for(OperationTypeEnum operationTypeEnum: OperationTypeEnum.values())
        {

                if (functions.isValueNull(selectedItem) && selectedItem.equals(operationTypeEnum.name()))
                {
                    optionList.append("<option value='" + operationTypeEnum.name() + "'   selected >" + operationTypeEnum.toString() + "</option>");
                }
                else
                {
                    optionList.append("<option value='" + operationTypeEnum.name() + "' >" + operationTypeEnum.toString() + "</option>");
                }
        }
        return optionList;
    }

    public StringBuffer getOptionTagForComparator(String selectedItem,List<ComparatorEnum> notIncludeComparator)
    {
        StringBuffer optionList= new StringBuffer("<option value=''>Select Comparator</option>");

        for(ComparatorEnum comparatorEnum: ComparatorEnum.values())
        {
            if(notIncludeComparator!=null && notIncludeComparator.contains(comparatorEnum))
            {

            }
            else
            {
                if (functions.isValueNull(selectedItem) && (comparatorEnum.name()).equals(selectedItem))
                {
                    optionList.append(" <option value='" + comparatorEnum.name() + "'   selected >" + comparatorEnum.toString() + "</option>");
                }
                else
                {
                    optionList.append("  <option value='" + comparatorEnum.name() + "' >" + comparatorEnum.toString() + "</option>");
                }
            }

        }
        return optionList;
    }


    public void getOptionTagForDatabaseRuleType(Map<String,PayIfeTableInfo>  payIfeTableInfoMap,Set<String> tableAliasName,String query,StringBuffer selectOptionTag,StringBuffer fromOptionTag,StringBuffer whereOptionTag,StringBuffer groupOptionTag,StringBuffer payIfeTableArray,List<AggregateFunctions> selectAggregateFunction,List<String> joinList,List<String> onList,List<String> whereOperator,List<String> whereInputName,List<String> whereComparator,List<String> selectFieldList,List<String> fromTableList,List<String> whereFieldList,List<String> groupFieldList)
    {



            List<String> selectField=new ArrayList<String>();
            List<String> fromList=new ArrayList<String>();
            List<String> whereList=new ArrayList<String>();
            List<String> groupList=new ArrayList<String>();
            AggregateFunctions aggregateFunctions=null;
            if(functions.isValueNull(query))
            {
                Pattern pattern = Pattern.compile("(SELECT .* FROM)", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(query);

                String selectItemFromTable = "";
                while (matcher.find())
                {
                    selectItemFromTable = matcher.group().substring(matcher.group().indexOf(" "), matcher.group().lastIndexOf(" ")).trim();
                }

                String tableFields[]  =selectItemFromTable.split(",");
                for(int i=0;i<=tableFields.length-1;i++)
                {
                    Pattern patternInside = Pattern.compile("\\([a-z A_Z \\.]*\\)", Pattern.CASE_INSENSITIVE);
                    Matcher matcherInside = patternInside.matcher(tableFields[i]);

                    if(matcherInside.find())
                    {
                        selectField.add(matcherInside.group().replaceAll("\\(", "").replaceAll("\\)", "")) ;
                        aggregateFunctions= AggregateFunctions.getEnum(tableFields[i].substring(0, tableFields[i].indexOf("(")));
                        selectAggregateFunction.add(aggregateFunctions);
                    }
                    else
                    {
                        selectField.add(tableFields[i]);
                        selectAggregateFunction.add(null);
                    }

                }
                String patternForFrom=null;
                if(query.toUpperCase().contains("WHERE"))
                {
                    patternForFrom="(FROM .* WHERE)";
                }
                else
                {
                    patternForFrom="(FROM .* )";
                }
                Pattern patternFrom = Pattern.compile(patternForFrom, Pattern.CASE_INSENSITIVE);
                Matcher matcherFrom = patternFrom.matcher(query);


                while (matcherFrom.find())
                {
                    String fromClause=matcherFrom.group().substring(matcherFrom.group().indexOf(" "), matcherFrom.group().lastIndexOf(" ")).trim();
                    String delim = "(RIGHT JOIN|JOIN|LEFT JOIN|[\\s+]*) ";
                    String stringTokenizer[]=fromClause.split(delim);
                    int countOfTable=1;
                    int indexOfCurrentWord=0;
                    int indexOfPreviousWord=0;
                    for(int i=0;i<stringTokenizer.length;i++)
                    {
                        String fromTable=stringTokenizer[i].trim();
                        logger.debug("FIELD NAME:::"+fromTable);
                        if("ON".equals(fromTable))
                        {
                            logger.debug("ON NAME:::"+fromTable);
                            String[] onClause=stringTokenizer[++i].trim().split("=");
                            logger.debug(onClause[0]);
                            if(onClause.length>1)
                            {
                                onList.add(onClause[0]);
                                onList.add(onClause[1]);
                            }
                            else
                            {
                                onList.add(onClause[0]);
                                onList.add(null);
                            }
                        }
                        else if ("as".equals(fromTable))
                        {
                            ++i;
                        }
                        else if(!functions.isValueNull(fromTable))
                        {

                        }
                        else
                        {
                            logger.debug("ELSE FIELD NAME:::"+fromTable);
                            indexOfPreviousWord=indexOfCurrentWord;
                            indexOfCurrentWord=fromClause.indexOf(fromTable,indexOfCurrentWord);
                            if(countOfTable>=2)
                            {

                                String subStringFrom=fromClause.substring(indexOfPreviousWord,indexOfCurrentWord);
                                logger.debug("SUBSTRING FROM:::"+subStringFrom);
                                if(subStringFrom.lastIndexOf(JoinEnum.JOIN.name())>0)
                                {
                                    if(subStringFrom.lastIndexOf(JoinEnum.RIGHT.name())>0)
                                    {
                                        joinList.add(JoinEnum.RIGHT.toString());
                                    }
                                    else if(subStringFrom.lastIndexOf(JoinEnum.LEFT.name())>0)
                                    {
                                        joinList.add(JoinEnum.LEFT.toString());
                                    }
                                    else
                                    {
                                        joinList.add(JoinEnum.JOIN.toString());
                                    }
                                }
                                else
                                {
                                    joinList.add(null);
                                }



                            }
                            fromList.add(fromTable);
                            countOfTable++;

                        }

                    }



                }

                String patternStyle="";
                boolean containsGroup=false;
                if(query.toLowerCase().contains("group"))
                {
                    patternStyle="(WHERE .* GROUP)";
                    containsGroup=true;
                }
                else
                {
                    patternStyle="(WHERE .*)";
                }

                Pattern patternWhere = Pattern.compile(patternStyle, Pattern.CASE_INSENSITIVE);
                Matcher matcherWhere = patternWhere.matcher(query);

                logger.debug("Where query break..."+patternStyle);

                if(matcherWhere.find())
                {
                    String whereCondition="";

                    if(containsGroup)
                    {
                        whereCondition=  matcherWhere.group().substring(matcherWhere.group().indexOf(" ") + 1, matcherWhere.group().lastIndexOf(" ") - 1);
                    }
                    else
                    {
                        whereCondition=  matcherWhere.group().substring(matcherWhere.group().indexOf(" ") + 1);
                    }

                    logger.debug("Total Where:::"+whereCondition);
                    String singleWhere[]=whereCondition.split("\\s+");
                    for(int i=0;i<=singleWhere.length-1;i++)
                    {
                        logger.debug("singleWhere::"+singleWhere[i]);
                        Pattern whereInsidePattern=Pattern.compile("^[a-z A-Z]*[<=> \\?]{1,2}");
                        Matcher whereInsideMatcher = whereInsidePattern.matcher(singleWhere[i]);

                        if(ComparatorEnum.getEnum(singleWhere[i])!=null)
                        {
                            logger.debug("Is Comparator::"+singleWhere[i]);
                            whereComparator.add(ComparatorEnum.getEnum(singleWhere[i]).toString());
                        }
                        else if(whereInsideMatcher.find())
                        {
                            logger.debug("Matcher::"+singleWhere[i]);
                            whereOperator.add(whereInsideMatcher.group());
                        }
                        else
                        {
                            logger.debug("ELSE condition::"+singleWhere[i]);
                            String delim = "<=>";
                            StringTokenizer stringTokenizer = new StringTokenizer(singleWhere[i],delim);

                            int singleWhereClauseCurrent=0;
                            int singleWhereClausePrevious=0;
                            String currentField="";
                            String previousField=null;
                            while(stringTokenizer.hasMoreTokens())
                            {
                                singleWhereClausePrevious=singleWhereClauseCurrent;
                                previousField=currentField;
                                String fieldName=stringTokenizer.nextToken().trim();
                                logger.debug("After Delimiting::"+fieldName);
                                currentField=fieldName;
                                singleWhereClauseCurrent=singleWhere[i].indexOf(fieldName);
                                if(payIfeTableInfoMap!=null && payIfeTableInfoMap.containsKey(fieldName))
                                {
                                    logger.debug("Inside Field for where::" + fieldName);
                                    whereList.add(fieldName);



                                }
                                else if(fieldName.contains("#") || fieldName.contains("#"))
                                {
                                    logger.debug("Where Input Name::"+fieldName);
                                    whereInputName.add((fieldName.replace("#","").replace("#","")).trim());
                                }
                                else if(fieldName.contains("'") || fieldName.contains("'"))
                                {
                                    logger.debug("Where Input Name::"+fieldName);
                                    whereInputName.add((fieldName.replace("'","").replace("'","")).trim());
                                }

                                if(functions.isValueNull(previousField))
                                {
                                    logger.debug("inner condition::"+singleWhere[i].substring(singleWhere[i].indexOf(previousField) + previousField.length(), singleWhere[i].indexOf(currentField)-1));
                                    whereOperator.add(singleWhere[i].substring(singleWhere[i].indexOf(previousField) + previousField.length(), singleWhere[i].indexOf(currentField)-1));

                                }
                            }
                        }
                    }

                }

                Pattern patternGroup = Pattern.compile("(GROUP BY .*)", Pattern.CASE_INSENSITIVE);
                Matcher matcherGroup = patternGroup.matcher(query);
                if(matcherGroup.find())
                {
                    String groupBy=matcherGroup.group().replaceFirst("GROUP", "").replaceFirst("BY", "").trim();

                    String singleGroupBy[]=groupBy.split(",");
                    for(int i=0;i<=singleGroupBy.length-1;i++)
                    {
                        groupList.add(singleGroupBy[i]);
                    }
                }


            }

        Set<String> tableExist=new HashSet<String>();
        int i=0;
        for(Map.Entry<String,PayIfeTableInfo> payIfeTableInfoEntry:payIfeTableInfoMap.entrySet())
        {
            PayIfeTableInfo payIfeTableInfo=payIfeTableInfoEntry.getValue();
            if(selectOptionTag!=null)
            {
                if(selectField.contains((payIfeTableInfoEntry.getKey())))
                {
                    selectOptionTag.append(" <option value='"+payIfeTableInfoEntry.getKey()+"'   selected >"+payIfeTableInfo.getTableAliasName()+" "+payIfeTableInfo.getFieldName()+"</option>");
                }
                else
                {
                    selectOptionTag.append(" <option value='"+payIfeTableInfoEntry.getKey()+"'   >"+payIfeTableInfo.getTableAliasName()+" "+payIfeTableInfo.getFieldName()+"</option>");
                }
            }


            if (fromOptionTag != null)
            {
                if(!tableExist.contains(payIfeTableInfo.getTableId()))
                {
                    if (fromList.contains((payIfeTableInfo.getTableName())))
                    {
                        fromOptionTag.append(" <option value='" + payIfeTableInfo.getTableName() + "'  id="+payIfeTableInfo.getTableAliasName()+" selected >" + payIfeTableInfo.getTableName() +" as "+payIfeTableInfo.getTableAliasName() +"</option>");
                    }
                    else
                    {
                        fromOptionTag.append(" <option value='" + payIfeTableInfo.getTableName() + "'  id="+payIfeTableInfo.getTableAliasName()+" >" + payIfeTableInfo.getTableName() +"</option>");
                    }
                    tableExist.add(payIfeTableInfo.getTableId());
                }
            }
            if(whereOptionTag!=null)
            {
                if(whereList.contains((payIfeTableInfoEntry.getKey())))
                {
                    whereOptionTag.append(" <option value='"+payIfeTableInfoEntry.getKey()+"'   selected >"+payIfeTableInfo.getTableName()+" "+payIfeTableInfo.getFieldName()+"</option>");
                }
                else
                {
                    whereOptionTag.append(" <option value='"+payIfeTableInfoEntry.getKey() + "'   >"+payIfeTableInfo.getTableName() + " "+payIfeTableInfo.getFieldName() + "</option>");
                }
            }
            if(groupOptionTag!=null)
            {
                if(groupList.contains((payIfeTableInfoEntry.getKey())))
                {
                    groupOptionTag.append(" <option value='"+payIfeTableInfoEntry.getKey()+"'   selected >"+payIfeTableInfo.getTableName()+" "+payIfeTableInfo.getFieldName()+"</option>");
                }
                else
                {
                    groupOptionTag.append(" <option value='"+payIfeTableInfoEntry.getKey() + "'   >"+payIfeTableInfo.getTableName() + " "+payIfeTableInfo.getFieldName() + "</option>");
                }
            }
            payIfeTableArray.append("payIFETableArray["+i+"]=\""+(functions.isValueNull(payIfeTableInfoEntry.getKey())?payIfeTableInfoEntry.getKey():"")+":"+(functions.isValueNull(payIfeTableInfo.getDataType())?payIfeTableInfo.getDataType():"")+":"+(functions.isValueNull(payIfeTableInfo.getEnumValue())?payIfeTableInfo.getEnumValue():"")+"\";");
            i++;
        }

        selectFieldList.addAll(selectField);
        fromTableList.addAll(fromList);
        whereFieldList.addAll(whereList);
        groupFieldList.addAll(groupList);
    }

    public StringBuffer getOptionTagForAggregateFunctionsQuery(String selectedItem,List<AggregateFunctions> notIncludeAggregateFunctions)
    {
        StringBuffer optionList= new StringBuffer("<option value=''>Select Aggregate Functions</option>");

        for(AggregateFunctions aggregateFunctions: AggregateFunctions.values())
        {
            if(notIncludeAggregateFunctions!=null && notIncludeAggregateFunctions.contains(aggregateFunctions))
            {

            }
            else
            {
                if (functions.isValueNull(selectedItem) && (aggregateFunctions.name()).equals(selectedItem))
                {
                    optionList.append(" <option value='" + aggregateFunctions.name() + "'   selected >" + aggregateFunctions.toString() + "</option>");
                }
                else
                {
                    optionList.append("  <option value='" + aggregateFunctions.name() + "' >" + aggregateFunctions.toString() + "</option>");
                }
            }

        }
        return optionList;
    }


    public StringBuffer getOptionTagForJoinTableQuery(String   selectedItem)
    {
        StringBuffer optionList= new StringBuffer("<option value=''>Select Join</option>");

        for(JoinEnum join: JoinEnum.values())
        {
                if (functions.isValueNull(selectedItem) && (join.toString()).equals(selectedItem))
                {
                    optionList.append(" <option value='" + join.toString() + "'   selected >" + join.toString() + "</option>");
                }
                else
                {
                    optionList.append("  <option value='" + join.toString() + "' >" + join.toString() + "</option>");
                }
        }
        return optionList;
    }

    /**
     * This is for dropdown to work with ID in javascript
     * @param ruleVOList
     * @param ruleIdAndOptionMap
     * @param selectedRuleId
     * @return
     */
    public StringBuffer getOptionTagForRiskRule(List<RuleVO>  ruleVOList,Map<String,String> ruleIdAndOptionMap,String selectedRuleId)
    {
        StringBuffer optionList= new StringBuffer("<option value=\"\">Select One Rule</option>");

        for(RuleVO ruleVO:ruleVOList)
        {
            if(functions.isValueNull(selectedRuleId) && (ruleVO.getId()).equals(selectedRuleId))
            {
                optionList.append(" <option value=\""+ruleVO.getId()+"\"  id=\""+ruleIdAndOptionMap.get(ruleVO.getId())+"\" selected >"+ruleVO.getName()+"</option>");
            }
            else
            {
                optionList.append("  <option value=\""+ruleVO.getId()+"\"  id=\""+ruleIdAndOptionMap.get(ruleVO.getId())+"\">"+ruleVO.getName()+"</option>");
            }

        }
        return optionList;
    }



    /**
     * This is for normal dropdown
     * @param ruleVOList
     * @param selectedRuleId
     * @return
     */
    public StringBuffer getOptionTagForRiskRuleWithoutId(List<RuleVO>  ruleVOList,String selectedRuleId)
    {
        StringBuffer optionList= new StringBuffer("<option value=\"\">Select One Rule</option>");

        for(RuleVO ruleVO:ruleVOList)
        {
            if(functions.isValueNull(selectedRuleId) && (ruleVO.getId()).equals(selectedRuleId))
            {
                optionList.append(" <option value=\""+ruleVO.getId()+"\"   selected >"+ruleVO.getName()+"</option>");
            }
            else
            {
                optionList.append("  <option value=\""+ruleVO.getId()+"\"  >"+ruleVO.getName()+"</option>");
            }

        }
        return optionList;
    }

    /**
     * This is for dropdown with Id for javascript
     * @param ruleVOList
     * @param ruleIdAndOptionMap
     * @param selectedRuleId
     * @return
     */
    public StringBuffer getOptionTagForBusinessRule(Map<String,com.manager.vo.businessRuleVOs.RuleVO>  ruleVOList,Map<String,String> ruleIdAndOptionMap,String selectedRuleId)
    {
        StringBuffer optionList= new StringBuffer("<option value=\"\">Select One Rule</option>");

        for(Map.Entry<String,com.manager.vo.businessRuleVOs.RuleVO> ruleVOPair:ruleVOList.entrySet())
        {
            com.manager.vo.businessRuleVOs.RuleVO ruleVO=ruleVOPair.getValue();
            if(functions.isValueNull(selectedRuleId) && (ruleVO.getId()).equals(selectedRuleId))
            {
                optionList.append(" <option value=\""+ruleVO.getId()+"\"  id=\""+ruleIdAndOptionMap.get(ruleVO.getId())+"\" selected >"+ruleVO.getName()+"</option>");
            }
            else
            {
                optionList.append("  <option value=\""+ruleVO.getId()+"\"  id=\""+ruleIdAndOptionMap.get(ruleVO.getId())+"\">"+ruleVO.getName()+"</option>");
            }

        }
        return optionList;
    }

    /**
     * This is normal dropdown
     * @param ruleVOList
     * @param selectedRuleId
     * @return
     */
    public StringBuffer getOptionTagForBusinessRuleWithoutId(List<com.manager.vo.businessRuleVOs.RuleVO>  ruleVOList,String selectedRuleId)
    {
        StringBuffer optionList= new StringBuffer("<option value=\"\">Select One Rule</option>");

        for(com.manager.vo.businessRuleVOs.RuleVO ruleVO:ruleVOList)
        {
            if(functions.isValueNull(selectedRuleId) && (ruleVO.getId()).equals(selectedRuleId))
            {
                optionList.append(" <option value=\""+ruleVO.getId()+"\"   selected >"+ruleVO.getName()+"</option>");
            }
            else
            {
                optionList.append(" <option value=\""+ruleVO.getId()+"\" >"+ruleVO.getName()+"</option>");
            }

        }
        return optionList;
    }

    // select operator

    public  StringBuffer getOptionTagforOperators(String selectedItem)
    {
        Functions functions= new Functions();

        StringBuffer optionList= new StringBuffer(" <option value=''>Select a Operator</option>");

        for (PZOperatorEnums pzOperatorEnums: PZOperatorEnums.values())
        {
            if(functions.isValueNull(selectedItem) && (pzOperatorEnums.name()).equals(selectedItem))
            {
                optionList.append("<option value='"+pzOperatorEnums.name()+"' selected>"+pzOperatorEnums.name()+"</option>");
            }
            else
            {
                optionList.append("<option value='"+pzOperatorEnums.name()+"' >"+pzOperatorEnums.name()+"</option>");
            }
        }

        return optionList;
    }

    public  StringBuffer getOptionTagForOperatorsForSymbol(String selectedItem)
    {
        Functions functions= new Functions();

        StringBuffer optionList= new StringBuffer("<option value=''>Select a Operator</option>");

        for (PZOperatorEnums pzOperatorEnums: PZOperatorEnums.values())
        {
            if(functions.isValueNull(selectedItem) && (pzOperatorEnums.name()).equals(selectedItem))
            {
                optionList.append("<option value='"+pzOperatorEnums.name()+"' selected>"+pzOperatorEnums.toString()+"</option>");
            }
            else
            {
                optionList.append("<option value='"+pzOperatorEnums.name()+"' >"+pzOperatorEnums.toString()+"</option>");
            }
        }

        return optionList;
    }

    //RISK RULE & profile

    /**
     * Getting Single RiskRule
     * @param id
     * @param ruleName
     * @return
     * @throws com.payment.exceptionHandler.PZDBViolationException
     */
    public RuleVO getSingleRiskDetails(String id,String ruleName) throws PZDBViolationException
    {
        return profileManagementDAO.getSingleRiskRule(id, ruleName);
    }

    /**
     * insrting Rule Defination
     * @param ruleVO
     * @return
     * @throws com.payment.exceptionHandler.PZDBViolationException
     */
    public boolean insertRuleDefinition(RuleVO ruleVO) throws PZDBViolationException
    {
        return profileManagementDAO.insertRiskDefinition(ruleVO);
    }

    /**
     * updating RuleDefinition on ruleId
     * @param ruleVO
     * @param riskRuleId
     * @return
     * @throws com.payment.exceptionHandler.PZDBViolationException
     */
    public boolean updateRuleDefinition(RuleVO ruleVO,String riskRuleId) throws PZDBViolationException
    {
        return profileManagementDAO.updateRiskDefinition(ruleVO, riskRuleId);
    }

    public boolean deleteRuleDefinition(String riskRuleId) throws PZDBViolationException
    {
        return  profileManagementDAO.deleteRiskDefinition(riskRuleId);
    }

    public boolean insertRiskRuleOperations(List<RuleOperation> ruleOperations,String riskRuleId) throws PZDBViolationException
    {
        return profileManagementDAO.insertRiskRuleOperation(ruleOperations, riskRuleId);
    }

    public boolean deleteRiskRuleOperations(String riskRuleId) throws PZDBViolationException
    {
        return profileManagementDAO.deleteRiskRuleOperation(riskRuleId);
    }

    public RuleVO getSingleRiskRuleWithRuleOperation(String riskRuleId) throws PZDBViolationException
    {
       return profileManagementDAO.getSingleRiskRuleWithOperation(riskRuleId);
    }
    /*get all risk profile according to partnerId and profileId
     * T
     * @param partnerId
     * @param profileId
     * @return
     */
    public List<ProfileVO> getListOfRiskProfileVO(String partnerId, String profileId, String orderBy,PaginationVO paginationVO) throws PZDBViolationException
    {
        return profileManagementDAO.getListOfRiskProfileVO(partnerId, profileId, orderBy, paginationVO);
    }

    public RiskProfile getRiskProfileWithAllDetails(String partnerId,String profileId,PaginationVO paginationVO) throws PZDBViolationException
    {
        return profileManagementDAO.getRiskProfileWithAllDetails(partnerId, profileId, paginationVO);
    }

    public List<RuleVO> getListOfRiskRuleDetails(RuleVO ruleVO,PaginationVO paginationVO) throws PZDBViolationException
    {
        return profileManagementDAO.getListOfRiskRule(ruleVO,paginationVO);
    }



    public boolean insertRiskProfileMapping(ProfileVO profileVO,String partnerId) throws PZDBViolationException
    {
        return profileManagementDAO.insertRiskProfileMapping(profileVO,partnerId);
    }

    public boolean uniqueRiskProfileName(String profileName, String partnerid) throws PZDBViolationException
    {
        return profileManagementDAO.uniqueRiskProfileName(profileName, partnerid);
    }

    public boolean uniqueBusinessProfileName(String profileName, String partnerid) throws PZDBViolationException
    {
        return profileManagementDAO.uniqueBusinessProfileName(profileName, partnerid);
    }

    public boolean updateRiskProfileMapping(ProfileVO profileVO) throws PZDBViolationException
    {
        return profileManagementDAO.updateRiskProfileMapping(profileVO);
    }

    public boolean insertRiskProfile(RuleVO ruleVO,String partnerId) throws PZDBViolationException
    {
        return profileManagementDAO.insertRiskProfile(ruleVO,partnerId);
    }

    public boolean updateRiskProfile(RuleVO ruleVO,String partnerId) throws PZDBViolationException
    {
        return profileManagementDAO.updateRiskProfile(ruleVO, partnerId);
    }


    public boolean deleteRiskProfileForProfile(String profileId) throws PZDBViolationException
    {
        return profileManagementDAO.deleteRuleRelatedForProfile(profileId);
    }

    public boolean deleteRiskProfile(String profileId) throws PZDBViolationException
    {
        return profileManagementDAO.deleteRiskProfile(profileId);
    }



    //Business Rule & profile

    public com.manager.vo.businessRuleVOs.RuleVO getSingleBusinessDetails(String id,String ruleName) throws PZDBViolationException
    {
        return profileManagementDAO.getSingleBusinessRule(id, ruleName);
    }

    public com.manager.vo.businessRuleVOs.RuleVO getSingleBusinessRuleWithOperation(String id) throws PZDBViolationException
    {
        return profileManagementDAO.getSingleBusinessRuleWithOperation(id);
    }

    public boolean insertBusinessDefinition(com.manager.vo.businessRuleVOs.RuleVO ruleVO) throws PZDBViolationException
    {
        return profileManagementDAO.insertBusinessDefinition(ruleVO);
    }

    public boolean insertBusinessRuleOperation(List<com.manager.vo.businessRuleVOs.RuleOperation> ruleOperations,String businessRuleId) throws PZDBViolationException
    {
        return profileManagementDAO.insertBusinessRuleOperation(ruleOperations, businessRuleId);
    }

    public boolean updateBusinessDefinition(com.manager.vo.businessRuleVOs.RuleVO ruleVO,String riskRuleId) throws PZDBViolationException
    {
        return profileManagementDAO.updateBusinessDefinition(ruleVO, riskRuleId);
    }

    public boolean deleteBusinessRuleOperation(String businessRuleId) throws PZDBViolationException
    {
        return profileManagementDAO.deleteBusinessRuleOperation(businessRuleId);
    }

    public boolean deleteBusinessDefinition(String businessRuleId) throws PZDBViolationException
    {
        return profileManagementDAO.deleteBusinessDefinition(businessRuleId);
    }


    public List<com.manager.vo.businessRuleVOs.ProfileVO> getListOfBusinessProfileVO(String partnerId,String profileId,String orderBy,PaginationVO paginationVO) throws PZDBViolationException
    {
        return profileManagementDAO.getListOfBusinessProfileVO(partnerId, profileId, orderBy,paginationVO);
    }

    public BusinessProfile getBusinessProfileWithAllDetails(String partnerId,String profileId,PaginationVO paginationVO) throws PZDBViolationException
    {
        return profileManagementDAO.getBusinessProfileWithAllDetails(partnerId, profileId, paginationVO);
    }

    public List<com.manager.vo.businessRuleVOs.RuleVO> getListOfBusinessRuleDetails(com.manager.vo.businessRuleVOs.RuleVO ruleVO,PaginationVO paginationVO) throws PZDBViolationException
    {
        return profileManagementDAO.getListOfBusinessRule(ruleVO, paginationVO);
    }

    /**
     * Getting the map if all the rule details according to rule name or rule id
     * @param ruleVO
     * @param paginationVO
     * @return
     * @throws com.payment.exceptionHandler.PZDBViolationException
     */
    public Map<String, com.manager.vo.businessRuleVOs.RuleVO> getMapOfBusinessRuleDetails(com.manager.vo.businessRuleVOs.RuleVO ruleVO, PaginationVO paginationVO) throws PZDBViolationException
    {
        return profileManagementDAO.getMapOfBusinessRule(ruleVO, paginationVO);
    }

    public boolean insertBusinessProfileMapping(com.manager.vo.businessRuleVOs.ProfileVO profileVO,String partnerId) throws PZDBViolationException
    {
        return profileManagementDAO.insertBusinessProfileMapping(profileVO, partnerId);
    }

    public boolean updateBusinessProfileMapping(com.manager.vo.businessRuleVOs.ProfileVO profileVO,String partnerId) throws PZDBViolationException
    {
        return profileManagementDAO.updateBusinessProfileMapping(profileVO,partnerId);
    }

    public boolean insertBusinessProfile(com.manager.vo.businessRuleVOs.RuleVO ruleVO,String partnerId) throws PZDBViolationException
    {
        return profileManagementDAO.insertBusinessProfile(ruleVO, partnerId);
    }

   /* public boolean insertBusinessProfileDatabaseValue(Map<String,QueryInfo> queryInfoMap,String business_profile_id) throws PZDBViolationException
    {
        return profileManagementDAO.insertBusinessProfileDatabaseValue(queryInfoMap, business_profile_id);
    }*/

    public boolean updateBusinessProfile(com.manager.vo.businessRuleVOs.RuleVO ruleVO,String partnerId) throws PZDBViolationException
    {
        return  profileManagementDAO.updateBusinessProfile(ruleVO,partnerId);
    }

    public boolean deleteBusinessProfileForProfile(String profileId) throws PZDBViolationException
    {
        return profileManagementDAO.deleteBusinessRuleForProfile(profileId);
    }

    public boolean deleteBusinessProfile(String profileId) throws PZDBViolationException
    {
        return profileManagementDAO.deleteBusinessProfile(profileId);
    }

    public Map<String, MerchantVO> getMapOfUserSetting(String memberid,String partnerId,PaginationVO paginationVO) throws PZDBViolationException
    {
        return profileManagementDAO.getMapOfUserSetting(memberid,partnerId,paginationVO);
    }


    public UserSetting getSingleUserProfile(String userProfileId) throws PZDBViolationException
    {
            return profileManagementDAO.getSingleUserProfile(userProfileId);
    }

    public UserSetting getMapUserProfile(String userProfileId) throws PZDBViolationException
    {
            return profileManagementDAO.getMapUserProfile(userProfileId);
    }

    public boolean insertUserProfile(MerchantVO merchantVO,TemplateVO templateVO,String partnerId) throws PZDBViolationException
    {
            return profileManagementDAO.insertUserProfile(merchantVO,templateVO,partnerId);
    }

    public boolean updateUserProfile(MerchantVO merchantVO,TemplateVO templateVO,String userProfileId) throws PZDBViolationException
    {
            return profileManagementDAO.updateUserProfile(merchantVO, templateVO, userProfileId);
    }



    public boolean deleteUserProfileById(String profileId) throws PZDBViolationException
    {
        return  profileManagementDAO.deleteUserProfile(profileId);
    }




    //Generate XML for profile

    public boolean generateRiskProfileXML(RiskProfile riskProfile,HttpServletResponse response) throws PZTechnicalViolationException
    {
        ServletOutputStream servletOutputStream=null;
        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(RiskProfile.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            response.setHeader("Content-Disposition", "attachment; filename=\"riskProfile.xml\"");
            servletOutputStream=response.getOutputStream();
            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(riskProfile,servletOutputStream);
        }
        catch (JAXBException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ProfileManagementManager.class.getName(), "generateRiskProfileXML()", null, "common", "Exception while genrating xml file from Object", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ProfileManagementManager.class.getName(), "generateRiskProfileXML()", null, "common", "Exception while genrating xml file from Object", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            try
            {
                if(servletOutputStream!=null)
                {
                    servletOutputStream.flush();
                    servletOutputStream.close();
                }
            }
            catch(IOException e)
            {
                logger.error("Exception while closing IO stream ::",e);

            }
        }
        return true;
    }

    public boolean generateBusinessProfileXML(BusinessProfile businessProfile,HttpServletResponse response) throws PZTechnicalViolationException
    {
        ServletOutputStream servletOutputStream=null;
        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(BusinessProfile.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            response.setHeader("Content-Disposition", "attachment; filename=\"businessProfile.xml\"");
            servletOutputStream=response.getOutputStream();
            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(businessProfile,servletOutputStream);
        }
        catch (JAXBException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ProfileManagementManager.class.getName(), "generateBusinessProfileXML()", null, "common", "Exception while genrating xml file from Object", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ProfileManagementManager.class.getName(), "generateBusinessProfileXML()", null, "common", "Exception while genrating xml file from Object", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            try
            {
                if(servletOutputStream!=null)
                {
                    servletOutputStream.flush();
                    servletOutputStream.close();
                }
            }
            catch(IOException e)
            {
                logger.error("Exception while closing IO stream ::",e);

            }
        }
        return true;
    }

    public boolean generateUserProfileXML(UserSetting userSetting,HttpServletResponse response) throws PZTechnicalViolationException
    {
        ServletOutputStream servletOutputStream=null;
        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(UserSetting.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            response.setHeader("Content-Disposition", "attachment; filename=\"userProfile.xml\"");
            servletOutputStream=response.getOutputStream();
            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(userSetting,servletOutputStream);
        }
        catch (JAXBException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ProfileManagementManager.class.getName(), "generateUserProfileXML()", null, "common", "Exception while genrating xml file from Object", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ProfileManagementManager.class.getName(), "generateUserProfileXML()", null, "common", "Exception while genrating xml file from Object", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            try
            {
                if(servletOutputStream!=null)
                {
                    servletOutputStream.flush();
                    servletOutputStream.close();
                }
            }
            catch(IOException e)
            {
                logger.error("Exception while closing IO stream ::",e);

            }
        }
        return true;
    }

    public StringBuffer getOptionForRiskProfile(List<ProfileVO> riskProfileVOs,String selected)
    {
        StringBuffer optionList= new StringBuffer("<option value=\"\">Select One Risk Profile</option>");

        for(ProfileVO profileVO:riskProfileVOs)
        {
            if(functions.isValueNull(selected) && (profileVO.getId()).equals(selected))
            {
                optionList.append(" <option value=\""+profileVO.getId()+"\"  selected >"+""+profileVO.getId()+"-"+profileVO.getName()+"</option>");
            }
            else
            {
                optionList.append(" <option value=\""+profileVO.getId()+"\"  >"+""+profileVO.getId()+"-"+profileVO.getName()+"</option>");
            }

        }
        return optionList;
    }

    public StringBuffer getOptionForBusinessProfile(List<com.manager.vo.businessRuleVOs.ProfileVO> businessProfileVOs,String selected)
    {
        StringBuffer optionList= new StringBuffer("<option value=\"\">Select One Business Profile</option>");

        for(com.manager.vo.businessRuleVOs.ProfileVO profileVO:businessProfileVOs)
        {
            if(functions.isValueNull(selected) && (profileVO.getId()).equals(selected))
            {
                optionList.append(" <option value=\""+profileVO.getId()+"\"  selected >"+""+profileVO.getId()+"-"+profileVO.getName()+"</option>");
            }
            else
            {
                optionList.append(" <option value=\""+profileVO.getId()+"\"  >"+""+profileVO.getId()+"-"+profileVO.getName()+"</option>");
            }

        }
        return optionList;
    }

    //PayIfe table information retrieval
    public Map<String, List<PayIfeTableInfo>> getAllPayIfeTableInformation(String tableId, String tableName) throws PZDBViolationException
    {
        return profileManagementDAO.getAllPayIfeTableInfo(tableId, tableName);
    }

    public Map<String, PayIfeTableInfo> getAllPayIfeFieldsInformation(String tableId, String tableName,Set<String> tableAliasName) throws PZDBViolationException
    {
        return profileManagementDAO.getAllPayIfeFieldsInfo(tableId, tableName,tableAliasName);
    }

    public static void main(String[] args)
    {
       /* String delim = "<=>";
        StringTokenizer st = new StringTokenizer("table.dhddhcdcdcdddvdrgerg>=? and table.dgdgdg >= ?", delim);
        while (st.hasMoreTokens()) {
            System.out.println(st.nextToken());
        }

        String whereCondition="table.dhddhcdcdcdddvdrgerg>=? and table.dgdgdg >= ?";
        String singleWhere[]=whereCondition.split("\\s+");
        for(int i=0;i<=singleWhere.length-1;i++)
        {
            Pattern whereInsidePattern=Pattern.compile("^[a-z A-Z]*[<=> \\?]{1,2}");
            Matcher matcher = whereInsidePattern.matcher(singleWhere[i]);
            if(matcher.find())
            {
                System.out.println("Inside:::"+singleWhere[i]);
            }
            else
                System.out.println("DDD"+singleWhere[i]);
        }*/


        /*Pattern whereInsidePattern=Pattern.compile("^[a-z A-Z]*[<=>]{1,2}");
        Matcher whereInsideMatcher = whereInsidePattern.matcher("dd.single>=dd.ddddd");

         if(whereInsideMatcher.find())
        {
            System.out.println("Found::"+whereInsideMatcher.group());
        }*/
        String query = "Select MAX(OR.cardholder_firstname),GB.boiledname From orders as OR JOIN  global_bin_details as GB ON OR.telno=GB.isblacklisted  JOIN global_details as GB ON OR.telno=GB.isblacklisted WHERE OR.cardholder_firstname<<#TOID#> AND GB.boiledname<<#USERID#> AND OR.totype><#TOID#> GROUP BY OR.cardholder_firstname,GB.boiledname,OR.totype,OR.cardtype";

        Pattern pattern = Pattern.compile("(SELECT .* FROM)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(query);

        String selectItemFromTable = "";
        while (matcher.find())
        {
            selectItemFromTable = matcher.group().substring(matcher.group().indexOf(" "), matcher.group().lastIndexOf(" ")).trim();
        }

        String tableFields[] = selectItemFromTable.split(",");
        for (int i = 0; i <= tableFields.length - 1; i++)
        {
            Pattern patternInside = Pattern.compile("\\([a-z A_Z \\_\\.]*\\)", Pattern.CASE_INSENSITIVE);
            Matcher matcherInside = patternInside.matcher(tableFields[i]);
            //System.out.println("table Field::" + tableFields[i]);
            if (matcherInside.find())
            {
                //selectField.add(matcherInside.group().replaceAll("\\(", "").replaceAll("\\)", "")) ;
                AggregateFunctions aggregateFunctions = AggregateFunctions.getEnum(tableFields[i].substring(0, tableFields[i].indexOf("(")));
                //selectAggregateFunction.add(aggregateFunctions);
                //System.out.println("Matcher find::" + aggregateFunctions.name());
            }
            else
            {
                //selectField.add(tableFields[i]);
                //selectAggregateFunction.add(null);
                logger.debug("Normal Field::" + tableFields[i]);
            }

        }
        Pattern patternFrom = Pattern.compile("(FROM .* WHERE)", Pattern.CASE_INSENSITIVE);
        Matcher matcherFrom = patternFrom.matcher(query);


        while (matcherFrom.find())
        {
            String fromClause = matcherFrom.group().substring(matcherFrom.group().indexOf(" "), matcherFrom.group().lastIndexOf(" ")).trim();
            String delim = "(RIGHT JOIN|JOIN|LEFT JOIN|[\\s+]*) ";
            //StringTokenizer stringTokenizer = new StringTokenizer(fromClause, delim);
            String stringTokenizer[]=fromClause.split(delim);
            int countOfTable = 1;
            int indexOfCurrentWord = 0;
            int indexOfPreviousWord = 0;
            String tablePreviousWord = null;
            String tableCurrentWord = null;
            for(int i=0;i<stringTokenizer.length;i++)
            {
                String fromTable = stringTokenizer[i];
                //System.out.println("FIELD NAME:::" + fromTable);
                if ("ON".equalsIgnoreCase(fromTable))
                {
                    //System.out.println("ON NAME:::" + fromTable);
                    String[] onClause = stringTokenizer[++i].trim().split("=");
                    //System.out.println(onClause[0]);
                    if (onClause.length > 1)
                    {
                        logger.debug("LEFT ON " + onClause[0]);
                       logger.debug("RIGHT ON " + onClause[1]);
                    }
                    else
                    {
                        logger.debug("LEFT ON " + onClause[0]);
                        //System.out.println("RIGHT ON " + "");
                    }
                }
                else if ("as".equals(fromTable))
                {
                    ++i;
                }
                else if("".equals(fromTable))
                {

                }
                else
                {
                    //System.out.println("ELSE FIELD NAME:::" + fromTable);
                    indexOfPreviousWord = indexOfCurrentWord;
                    tablePreviousWord=tableCurrentWord;
                    indexOfCurrentWord = fromClause.indexOf(fromTable, indexOfCurrentWord);
                    tableCurrentWord=fromTable;
                    if (countOfTable  >= 2)
                    {
                        //System.out.println(indexOfPreviousWord+" "+indexOfCurrentWord+" total SIZE::"+fromClause.length());
                        String subStringFrom = fromClause.substring(indexOfPreviousWord, indexOfCurrentWord);
                        //System.out.println("SUBSTRING FROM:::" + subStringFrom);
                        if (subStringFrom.lastIndexOf(JoinEnum.JOIN.name()) > 0)
                        {
                            if (subStringFrom.lastIndexOf(JoinEnum.RIGHT.name()) > 0)
                            {
                                logger.debug(JoinEnum.RIGHT.toString());
                            }
                            else if (subStringFrom.lastIndexOf(JoinEnum.LEFT.name()) > 0)
                            {
                                logger.debug(JoinEnum.LEFT.toString());
                            }
                            else
                            {
                               logger.debug(JoinEnum.JOIN.toString());
                            }
                        }
                        else
                        {
                            logger.debug("null");
                        }

                    }

                    countOfTable++;

                }

            }
        }


        Pattern patternGroup = Pattern.compile("(GROUP BY .*)", Pattern.CASE_INSENSITIVE);
        Matcher matcherGroup = patternGroup.matcher(query);
        if(matcherGroup.find())
        {
            //System.out.println("Full Group by::"+matcherGroup.group().toLowerCase().replaceFirst("group", "").replaceFirst("by","").indexOf(" "));
            String groupBy=matcherGroup.group().toLowerCase().replaceFirst("group", "").replaceFirst("by", "").trim();

            String singleGroupBy[]=groupBy.split(",");
            for(int i=0;i<=singleGroupBy.length-1;i++)
            {
                logger.debug("Group By:::"+singleGroupBy[i]);
            }
        }

        String selectItem="COUNT(OR.game)";
        //System.out.println(selectItem.substring(selectItem.indexOf("(")+1,selectItem.indexOf(")")));
    }

    public TreeMap<String, String> getUsernameList(String role) throws PZDBViolationException
    {
        return profileManagementDAO.getUsernameList(role);
    }

}
