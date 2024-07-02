package org.evd.game.ConnService;

import org.evd.game.annotation.Serializable;
import org.evd.game.annotation.SerializerField;
import org.evd.game.base.ISerializable;

@Serializable
public class ConnInfoBase implements ISerializable {
    @SerializerField
    private int con1;

    public int getCon1() {
        return con1;
    }

    public void setCon1(int con1) {
        this.con1 = con1;
    }
}
