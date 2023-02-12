package com.fraud.at;
import com.fraud.vo.FSGenericResponseVO;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 7/31/14
 * Time: 3:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class ATResponseVO extends FSGenericResponseVO
{
    String internal_trans_id;
    String rec;
    Double score;
    String third_party;
    JSONObject jsonObject;

    public String getInternal_trans_id()
    {
        return internal_trans_id;
    }

    public void setInternal_trans_id(String internal_trans_id)
    {
        this.internal_trans_id = internal_trans_id;
    }

    public String getRec()
    {
        return rec;
    }

    public void setRec(String rec)
    {
        this.rec = rec;
    }

    public Double getScore()
    {
        return score;
    }

    public void setScore(Double score)
    {
        this.score = score;
    }

    public String getThird_party()
    {
        return third_party;
    }

    public void setThird_party(String third_party)
    {
        this.third_party = third_party;
    }
    public JSONObject getJsonObject()
    {
        return jsonObject;
    }
    public void setJsonObject(JSONObject jsonObject)
    {
        this.jsonObject = jsonObject;
    }

    public HashMap getHashMap()
    {
        HashMap responseMap = new HashMap();
        responseMap.put("internal_trans_id", this.internal_trans_id);
        responseMap.put("status", this.status);
        responseMap.put("description", this.description);
        responseMap.put("rec", this.rec);
        responseMap.put("score", this.score );
        responseMap.put("third_party", this.third_party );
        responseMap.put("jsonObject", this.jsonObject );
        return responseMap;
    }
    public void setHashMap(HashMap responseHash)
    {
        this.internal_trans_id = (String)responseHash.get("internal_trans_id");
        this.status =String.valueOf(responseHash.get("status"));
        if(responseHash.containsKey("desc"))
        {
          this.description = (String) responseHash.get("desc");
        }
         else
          this.description = (String) responseHash.get("description");
        this.rec = (String) responseHash.get("rec");
        if(responseHash.containsKey("score"))
        {
          this.score =new Double(responseHash.get("score").toString());
        }
        this.third_party = (String) responseHash.get("third_party");
        this.jsonObject =(JSONObject)responseHash.get("jsonObject");
    }

}
