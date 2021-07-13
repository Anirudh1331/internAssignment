package com.example.intern;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MyDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void addCountry(Country country);

    @Query("select * from countries")
    public List<Country> getCountry();

    @Query("delete from countries where 1")
    public void deleteAllCountry();

    @Query("SELECT * FROM countries ORDER BY name LIMIT 1")
    LiveData<Country> loadlastTask();
}
