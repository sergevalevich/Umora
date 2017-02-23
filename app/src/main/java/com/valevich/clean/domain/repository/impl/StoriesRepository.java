package com.valevich.clean.domain.repository.impl;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqldelight.SqlDelightStatement;
import com.valevich.clean.database.DatabaseHelper;
import com.valevich.clean.database.converters.DbStoryConverter;
import com.valevich.clean.database.model.StoryEntity;
import com.valevich.clean.domain.model.Story;
import com.valevich.clean.domain.repository.IRepository;
import com.valevich.clean.domain.repository.specification.SqlDelightSpecification;
import com.valevich.clean.rx.utils.SchedulersTransformer;

import java.util.List;

import rx.Observable;
import timber.log.Timber;


public class StoriesRepository implements IRepository<Story,SqlDelightSpecification<StoryEntity>> {
    private DatabaseHelper databaseHelper;

    private final StoryEntity.Update_row rowUpdateStatement;
    private final StoryEntity.Insert_row rowInsertStatement;

    public StoriesRepository(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
        this.rowUpdateStatement = new StoryEntity.Update_row(databaseHelper.getWritableDatabase());
        this.rowInsertStatement = new StoryEntity.Insert_row(databaseHelper.getWritableDatabase());
    }

    @Override
    public void add(Iterable<Story> items) {
        BriteDatabase.Transaction transaction = databaseHelper.startTransaction();
        try {
            for (Story story : items) {
                add(story);
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
        Timber.d("Stories inserted");
    }

    @Override
    public void add(Story story) {
        rowInsertStatement.bind(story.getText(),
                story.getTextLow(),
                story.getSite(),
                story.getCategoryName(),
                story.getText(),
                story.getDate());
        databaseHelper.insert(StoryEntity.TABLE_NAME, rowInsertStatement.program);
    }

    @Override
    public Observable<Story> update(Story story) {
        return Observable.defer(() -> {
            Timber.d("Updating story on %s", Thread.currentThread().getName());
            rowUpdateStatement.bind(story.isBookMarked(),story.getText());
            databaseHelper.updateDelete(StoryEntity.TABLE_NAME,rowUpdateStatement.program);
            return Observable.just(story);
        }).compose(SchedulersTransformer.INSTANCE.applySchedulers());
    }

    @Override
    public Observable<List<Story>> get(SqlDelightSpecification<StoryEntity> specification) {
        SqlDelightStatement statement = specification.getStatement();
        return databaseHelper.get(StoryEntity.TABLE_NAME, statement.statement, statement.args, specification.getMapper())
                .map(DbStoryConverter::getStoriesByDbEntity)
                .compose(SchedulersTransformer.INSTANCE.applySchedulers());
    }
}
