package practice;

import org.apache.commons.codec.language.bm.Lang;

import java.util.EnumSet;

/**
 * Created by Admin on 2/15/2021.
 */
public class enumsetconcept
{
    enum iplteams
    {
        SRH,
        RCB,
        MI,
        CSK,
        DD

    }

    public static void main(String[] args)
    {
        EnumSet<iplteams> enumSet = EnumSet.allOf(iplteams.class);
        System.out.println("enumSet--"+enumSet);
    }
}
