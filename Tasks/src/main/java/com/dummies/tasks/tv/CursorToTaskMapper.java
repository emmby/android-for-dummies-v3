package com.dummies.tasks.tv;

import android.database.Cursor;
import android.support.v17.leanback.database.CursorMapper;

import com.dummies.tasks.provider.TaskProvider;

class CursorToTaskMapper extends CursorMapper {
    int idIndex;
    int titleIndex;
    int notesIndex;

    @Override
    protected void bindColumns(Cursor cursor) {
        idIndex =
            cursor.getColumnIndexOrThrow(TaskProvider.COLUMN_TASKID);
        titleIndex =
            cursor.getColumnIndexOrThrow(TaskProvider.COLUMN_TITLE);
        notesIndex =
            cursor.getColumnIndexOrThrow(TaskProvider.COLUMN_NOTES);
    }

    @Override
    protected Task bind(Cursor cursor) {
        // Get the values of the fields
        long id = cursor.getLong(idIndex);
        String title = cursor.getString(titleIndex);
        String notes = cursor.getString(notesIndex);

        Task t = new Task();
        t.id=id;
        t.title=title;
        t.notes=notes;
        return t;
    }
}
