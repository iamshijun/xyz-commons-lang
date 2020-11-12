package xyz.asitanokibou.common.lang.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogicalType {
    LOGIC_EQ0("="),//sql
    LOGIC_EQ("=="),
    LOGIC_NE("!="),
    LOGIC_GT(">"),
    LOGIC_GE(">="),
    LOGIC_LT("<"),
    LOGIC_LE("<=");
    String exp;
}