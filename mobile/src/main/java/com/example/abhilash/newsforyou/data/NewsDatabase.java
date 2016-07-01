package com.example.abhilash.newsforyou.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by sam_chordas on 10/5/15.
 */


@Database(version = NewsDatabase.VERSION)
public class NewsDatabase {
    private NewsDatabase() {
    }

    public static final int VERSION = 1;

    @Table(NewsColumns.class)
    public static final String NEWS = "BingNews";
}
