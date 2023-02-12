package com.vo.applicationManagerVOs;

import com.enums.DefinedAcroFields;
import com.validators.BankInputName;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Niket on 15-09-2016.
 */
public class AppValidationVO
{
    private static Map<String,List<BankTypeVO>> memberGatewayListMap;

    private static Map<String,Map<Boolean,Set<DefinedAcroFields>>> memberAcroFieldsValidation;

    private static Map<String,Map<Integer,Map<Boolean,Set<BankInputName>>>> fullValidationMapOfSet;

    private static Map<String,Map<Integer,Map<Boolean,Set<BankInputName>>>> dependencyFullValidationMapOfSet;

    private static Map<String,Map<Integer,Set<BankInputName>>> otherFullValidationMapOfSet;



    public static Map<String, List<BankTypeVO>> getMemberGatewayListMap()
    {
        return memberGatewayListMap;
    }

    public static void setMemberGatewayListMap(Map<String, List<BankTypeVO>> memberGatewayListMap)
    {
        AppValidationVO.memberGatewayListMap = memberGatewayListMap;
    }

    public static Map<String, Map<Boolean, Set<DefinedAcroFields>>> getMemberAcroFieldsValidation()
    {
        return memberAcroFieldsValidation;
    }

    public static void setMemberAcroFieldsValidation(Map<String, Map<Boolean, Set<DefinedAcroFields>>> memberAcroFieldsValidation)
    {
        AppValidationVO.memberAcroFieldsValidation = memberAcroFieldsValidation;
    }

    public static Map<String, Map<Integer, Map<Boolean, Set<BankInputName>>>> getFullValidationMapOfSet()
    {
        return fullValidationMapOfSet;
    }

    public static void setFullValidationMapOfSet(Map<String, Map<Integer, Map<Boolean, Set<BankInputName>>>> fullValidationMapOfSet)
    {
        AppValidationVO.fullValidationMapOfSet = fullValidationMapOfSet;
    }

    public static Map<String, Map<Integer, Map<Boolean, Set<BankInputName>>>> getDependencyFullValidationMapOfSet()
    {
        return dependencyFullValidationMapOfSet;
    }

    public static void setDependencyFullValidationMapOfSet(Map<String, Map<Integer, Map<Boolean, Set<BankInputName>>>> dependencyFullValidationMapOfSet)
    {
        AppValidationVO.dependencyFullValidationMapOfSet = dependencyFullValidationMapOfSet;
    }

    public static Map<String, Map<Integer, Set<BankInputName>>> getOtherFullValidationMapOfSet()
    {
        return otherFullValidationMapOfSet;
    }

    public static void setOtherFullValidationMapOfSet(Map<String, Map<Integer, Set<BankInputName>>> otherFullValidationMapOfSet)
    {
        AppValidationVO.otherFullValidationMapOfSet = otherFullValidationMapOfSet;
    }

    public Map<Integer, Map<Boolean, Set<BankInputName>>> getSpecificValidationMapOfSet(String memberId)
    {
        if(fullValidationMapOfSet!=null && fullValidationMapOfSet.containsKey(memberId))
        {
            return fullValidationMapOfSet.get(memberId);
        }
        else
        {
            return null;
        }
    }

    public List<BankTypeVO> getMemberGatewayList(String memberId)
    {
        if(memberGatewayListMap!=null && memberGatewayListMap.containsKey(memberId))
        {
            return memberGatewayListMap.get(memberId);
        }
        else
        {
            return null;
        }
    }

    public Map<Integer, Map<Boolean, Set<BankInputName>>> getSpecificDependencyValidation(String memberId)
    {
        if(dependencyFullValidationMapOfSet!=null && dependencyFullValidationMapOfSet.containsKey(memberId))
            return dependencyFullValidationMapOfSet.get(memberId);

        return null;
    }

    public Map<Integer, Set<BankInputName>> getSpecificOtherOptionalValidation(String memberId)
    {
        if(otherFullValidationMapOfSet.containsKey(memberId))
            return otherFullValidationMapOfSet.get(memberId);

        return null;
    }
}
