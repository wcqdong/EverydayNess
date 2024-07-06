package org.evd.game.ConnService;

import org.evd.game.annotation.SerializeClass;
import org.evd.game.annotation.SerializeField;
import org.evd.game.base.ISerializable;

import java.util.ArrayList;
import java.util.List;

@SerializeClass
public class CoInfo implements ISerializable {

    @SerializeField
    public String aa;

    @SerializeField
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
