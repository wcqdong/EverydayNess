package org.evd.game.common;

import org.evd.game.annotation.SerializeClass;
import org.evd.game.base.ISerializable;

@SerializeClass
public class TestSer implements ISerializable {
    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
