package com.custom.mybatis.mapper;

import static com.custom.mybatis.mapper.CustomExampleDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import com.custom.mybatis.model.CustomExample;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.annotation.Generated;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.delete.DeleteDSLCompleter;
import org.mybatis.dynamic.sql.delete.render.DeleteStatementProvider;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.insert.render.MultiRowInsertStatementProvider;
import org.mybatis.dynamic.sql.select.CountDSLCompleter;
import org.mybatis.dynamic.sql.select.SelectDSLCompleter;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.dynamic.sql.update.UpdateDSLCompleter;
import org.mybatis.dynamic.sql.update.UpdateModel;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;

@Mapper
public interface CustomExampleMapper {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    BasicColumn[] selectList = BasicColumn.columnList(rowid, data);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @DeleteProvider(type=SqlProviderAdapter.class, method="delete")
    int delete(DeleteStatementProvider deleteStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    int insert(InsertStatementProvider<CustomExample> insertStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @InsertProvider(type=SqlProviderAdapter.class, method="insertMultiple")
    int insertMultiple(MultiRowInsertStatementProvider<CustomExample> multipleInsertStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("CustomExampleResult")
    Optional<CustomExample> selectOne(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="CustomExampleResult", value = {
        @Result(column="rowid", property="rowid", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="data", property="data", jdbcType=JdbcType.VARCHAR)
    })
    List<CustomExample> selectMany(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    int update(UpdateStatementProvider updateStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, customExample, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, customExample, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int deleteByPrimaryKey(Integer rowid_) {
        return delete(c -> 
            c.where(rowid, isEqualTo(rowid_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insert(CustomExample record) {
        return MyBatis3Utils.insert(this::insert, record, customExample, c ->
            c.map(rowid).toProperty("rowid")
            .map(data).toProperty("data")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertMultiple(Collection<CustomExample> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, customExample, c ->
            c.map(rowid).toProperty("rowid")
            .map(data).toProperty("data")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertSelective(CustomExample record) {
        return MyBatis3Utils.insert(this::insert, record, customExample, c ->
            c.map(rowid).toPropertyWhenPresent("rowid", record::getRowid)
            .map(data).toPropertyWhenPresent("data", record::getData)
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<CustomExample> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, customExample, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<CustomExample> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, customExample, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<CustomExample> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, customExample, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<CustomExample> selectByPrimaryKey(Integer rowid_) {
        return selectOne(c ->
            c.where(rowid, isEqualTo(rowid_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, customExample, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateAllColumns(CustomExample record, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(rowid).equalTo(record::getRowid)
                .set(data).equalTo(record::getData);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(CustomExample record, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(rowid).equalToWhenPresent(record::getRowid)
                .set(data).equalToWhenPresent(record::getData);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKey(CustomExample record) {
        return update(c ->
            c.set(data).equalTo(record::getData)
            .where(rowid, isEqualTo(record::getRowid))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKeySelective(CustomExample record) {
        return update(c ->
            c.set(data).equalToWhenPresent(record::getData)
            .where(rowid, isEqualTo(record::getRowid))
        );
    }
}