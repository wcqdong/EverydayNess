package org.evd.game.StageService;

import org.evd.game.annotation.SerializeClass;
import org.evd.game.base.ISerializable;

@SerializeClass
public class TestSer implements ISerializable {

    int a;

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }
}
