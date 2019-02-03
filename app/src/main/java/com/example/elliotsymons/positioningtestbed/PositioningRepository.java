package com.example.elliotsymons.positioningtestbed;

import android.app.Application;

public class PositioningRepository {

    PositioningRepository(Application application) {
        PositioningRoomDatabase db = PositioningRoomDatabase.getDatabase(application);
        //TODO
    }
}
