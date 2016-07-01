package com.example.abhilash.newsforyou.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;


public class NewsColumns {
    @DataType(DataType.Type.INTEGER)
    @AutoIncrement  @PrimaryKey
    public static final String _ID = "_id";

    @DataType(DataType.Type.TEXT)  @NotNull

    public static final String NAME = "name";

    @DataType(DataType.Type.TEXT)  @NotNull

    public static final String URL = "url";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String DESCRIPTION = "description";

    @DataType(DataType.Type.TEXT)

    public static final String IMAGE = "image";

    @DataType(DataType.Type.TEXT)

    public static final String CATEGORY = "category";

    @DataType(DataType.Type.TEXT)

    public static final String PROVIDER = "provider";

    @DataType(DataType.Type.TEXT)
    public static final String TIME = "time";

    @DataType(DataType.Type.TEXT)
    public static final String IS_FAV = "is_fav";
    @DataType(DataType.Type.TEXT)
    public static final String SEARCH = "search";
}
