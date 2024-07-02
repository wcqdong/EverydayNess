package org.evd.game.gencode.struct;

import org.evd.game.gencode.AptUtils;

public class ParamStruct {
    public String paramType;
    public String paramTypeWrapper;
    public String paramName;

    public ParamStruct(String paramType, String paramName) {
        this.paramType = AptUtils.typeToBase(paramType);
        this.paramTypeWrapper = AptUtils.typeToWrapper(paramType);
        this.paramName = paramName;
    }

    @Override
    public String toString() {
        return paramType + " " + paramName;
    }
}
