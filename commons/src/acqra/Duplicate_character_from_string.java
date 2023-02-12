package acqra;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Admin on 4/6/2021.
 */
public class Duplicate_character_from_string
{
    public static void main(String[] args)
    {
        printDuplicateCharacters("");
        printDuplicateCharacters(" ");
        printDuplicateCharacters(null);
        printDuplicateCharacters("sagar");

    }
    public static void printDuplicateCharacters(String str)
    {
        //Below three are negative cases handled
        if(str==null)
        {
            System.out.println("Null string");
            return;
        }
        if(str.isEmpty())
        {
            System.out.println("Empty string");
            return;
        }
        if(str.length()==1)
        {
            System.out.println("Single char string");
            return;
        }
        char words[]=str.toCharArray();
        Map<Character,Integer>characterIntegerMap = new HashMap<Character,Integer>();
        for(Character chr:words)
        {
            if(characterIntegerMap.containsKey(chr))//it will check whether the chracter is their in characterIntegerMap or not
            {
                characterIntegerMap.put(chr,characterIntegerMap.get(chr)+1);//as it is repeating it will add here
            }
            else //if char is not repeated it will go in this condition
            {
                characterIntegerMap.put(chr,1);
            }
        }
        Set<Map.Entry<Character,Integer>> entrySet=characterIntegerMap.entrySet(); //print the map
        for(Map.Entry<Character,Integer>entry:entrySet)
        {
            if(entry.getValue()>1)
            {
                System.out.println(entry.getKey()+":"+entry.getValue());
            }

        }


    }
}
