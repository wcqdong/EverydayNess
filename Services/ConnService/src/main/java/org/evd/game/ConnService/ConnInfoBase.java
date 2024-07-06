package org.evd.game.ConnService;

import org.evd.game.annotation.SerializeClass;
import org.evd.game.annotation.SerializeField;
import org.evd.game.base.ISerializable;

@SerializeClass
public class ConnInfoBase implements ISerializable {
    @SerializeField
    private int con1;

    public int getCon1() {
        return con1;
    }

    public void setCon1(int con1) {
        this.con1 = con1;
    }
}
