package com.custom.mybatis.mapper;

import java.sql.JDBCType;
import javax.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class CustomExampleDynamicSqlSupport {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final CustomExample customExample = new CustomExample();

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Integer> rowid = customExample.rowid;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> data = customExample.data;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final class CustomExample extends SqlTable {
        public final SqlColumn<Integer> rowid = column("rowid", JDBCType.INTEGER);

        public final SqlColumn<String> data = column("data", JDBCType.VARCHAR);

        public CustomExample() {
            super("CustomExample");
        }
    }
}