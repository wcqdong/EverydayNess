package org.evd.gencode.struct;

import org.evd.gencode.Utils;

public class ParamStruct {
    public String paramType;
    public String paramTypeWrapper;
    public String paramName;

    public ParamStruct(String paramType, String paramName) {
        this.paramType = Utils.typeToBase(paramType);
        this.paramTypeWrapper = Utils.typeToWrapper(paramType);
        this.paramName = paramName;
    }

    @Override
    public String toString() {
        return paramType + " " + paramName;
    }
}
