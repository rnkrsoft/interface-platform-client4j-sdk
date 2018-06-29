package javax.web.doc;


import com.rnkrsoft.interfaces.EnumIntegerCode;
import com.rnkrsoft.interfaces.EnumStringCode;

import javax.web.doc.annotation.ApidocElement;

public abstract class AbstractResponse implements RspCodeReadable, RspCodeWritable {
    @ApidocElement("应答码")
    String rspCode = "0000";
    @ApidocElement("应答描述")
    String rspDesc = "成功";

    @Override
    public final String getRspCode() {
        return rspCode;
    }

    @Override
    public final void setRspCode(EnumStringCode code) {
        this.rspCode = code.getCode();
        this.rspDesc = code.getDesc();
    }

    @Override
    public final String getRspDesc() {
        return rspDesc;
    }

    @Override
    public final void setRspDesc(String rspDesc) {
        this.rspDesc = rspDesc;
    }

    @Override
    public final void setRspCode(String rspCode) {
        this.rspCode = rspCode;
    }

    @Override
    public final void setRspCode(EnumIntegerCode code) {
        this.rspCode = Integer.toString(code.getCode());
        this.rspDesc = code.getDesc();
    }

    @Override
    public String toString() {
        return "AbstractResponse{" +
                "rspCode='" + rspCode + '\'' +
                ", rspDesc='" + rspDesc + '\'' +
                '}';
    }
}
