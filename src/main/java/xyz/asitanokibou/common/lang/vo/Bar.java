package xyz.asitanokibou.common.lang.vo;

import java.io.Serializable;

public class Bar implements Serializable {

    public Integer getNumber(){
        return 1;
    }

    public Integer getNumber(Bar bar) {
        return 2;
    }
}
