package xyz.asitanokibou.common.lang.model;

import lombok.Data;

@Data
public class Foo implements LogicDeletable{
    String name;
    Boolean delFlag;
}
