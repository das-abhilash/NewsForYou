package com.example.abhilash.newsforyou.data;


import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by sam_chordas on 10/5/15.
 */


@ContentProvider(authority = NewsProvider.AUTHORITY, database = NewsDatabase.class)
public class NewsProvider {
    public static final String AUTHORITY = "com.example.abhilash.newsforyou.data";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path {
        String News= "news";
    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = NewsDatabase.NEWS)
    public static class News {
        @ContentUri(
                path = Path.News,
                type = "vnd.android.cursor.dir/news"
        )
        public static final Uri CONTENT_URI = buildUri(Path.News);

        @InexactContentUri(
                name = "NEWS_ID",
                path = Path.News + "/*",
                type = "vnd.android.cursor.item/news",
                whereColumn = NewsColumns._ID,
                pathSegment = 1
        )
        public static Uri ID(String ID) {
            return buildUri(Path.News, ID);
        }
    }
}

