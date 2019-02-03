package com.example.elliotsymons.positioningtestbed;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

@Database(entities = {MapPoint.class}, version = 1) //FIXME not word, what?
public abstract class PositioningRoomDatabase extends RoomDatabase {
    private static volatile PositioningRoomDatabase INSTANCE;

    //TODO add specific methods




    static PositioningRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (PositioningRoomDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            PositioningRoomDatabase.class, "word_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();

                }
            }
        }
        return INSTANCE;
    }


    //Code for repopulating database on startup
    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        //private final WordDao mDao;

        PopulateDbAsync(PositioningRoomDatabase db) {
            //mDao = db.wordDao();
            //TODO
        }

        @Override
        protected Void doInBackground(final Void... params) {
            /*mDao.deleteAll();
            Word word = new Word("Hello");
            mDao.insert(word);
            word = new Word("World");
            mDao.insert(word);*/
            //TODO
            return null;
        }
    }
}
