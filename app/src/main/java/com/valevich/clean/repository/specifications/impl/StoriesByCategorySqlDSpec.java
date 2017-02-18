package com.valevich.clean.repository.specifications.impl;

import com.squareup.sqldelight.RowMapper;
import com.squareup.sqldelight.SqlDelightStatement;
import com.valevich.clean.database.model.StoryEntity;
import com.valevich.clean.domain.model.Category;
import com.valevich.clean.repository.specifications.SqlDelightSpecification;


public class StoriesByCategorySqlDSpec implements SqlDelightSpecification<StoryEntity> {

    private Category category;
    private int limit;
    private int offset;

    private final SqlDelightStatement statement;

    public StoriesByCategorySqlDSpec(Category category, int limit, int offset) {
        this.category = category;
        this.limit = limit;
        this.offset = offset;
        this.statement = StoryEntity.FACTORY.select_by_category(
                category.getSite(),
                category.getName(),
                limit,
                offset);
    }

    @Override
    public String getQuery() {
        return statement.statement;
    }

    @Override
    public String[] getArgs() {
        return statement.args;
    }

    @Override
    public RowMapper<StoryEntity> getMapper() {
        return StoryEntity.FACTORY.select_by_categoryMapper();
    }
}