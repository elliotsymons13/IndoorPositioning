package com.example.elliotsymons.positioningtestbed;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "fingerprint_point")
public class FingerprintPoint {



    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "ID")
    private int ID;

    public FingerprintPoint() {

    }


    public int getID() { return ID; }

    public void setID(int ID) {
        this.ID = ID;
    }

}
