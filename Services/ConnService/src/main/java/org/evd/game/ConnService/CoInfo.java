package org.evd.game.ConnService;

import org.evd.game.annotation.Serializable;
import org.evd.game.annotation.SerializerField;
import org.evd.game.base.ISerializable;

import java.util.ArrayList;
import java.util.List;

@Serializable
public class CoInfo implements ISerializable {

    @SerializerField
    public String aa;

    @SerializerField
    public List<String> list = new ArrayList<>();

    public String getAa() {
        return aa;
    }

    public void setAa(String s) {
        aa = s;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
