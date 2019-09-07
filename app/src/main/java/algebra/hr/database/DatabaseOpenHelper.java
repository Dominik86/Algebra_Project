package algebra.hr.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 8;
    private static final String DATABASE_NAME = "AADBDTDatabase";

    private static final String TABLE_USER = "User";
    public static final String ID_USER = "id";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String NAME_SURNAME = "name_surname";
    public static final String EMAIL = "email";
    public static final String ROLE = "role";
    public static final String PACKET_ID = "packet_id";
    public static final String PACKET_DATE_START = "packet_date_start";
    public static final String PACKET_DATE_END = "packet_date_end";
    public static final String LOGIN_USER_ID = "login_user_id";
    public static final String PACKET_TYPE = "packet_type";

    private static final String TABLE_PACKET = "Packet";
    public static final String ID_PACKET = "id";
    public static final String TYPE = "type";
    public static final String PRICE = "price";
    public static final String ACTIVATION_DATE = "activation_date";
    public static final String NEW_ACTIVATION_DATE = "new_activation_date";
    public static final String DAILY_LIMIT = "daily_limit";
    public static final String MAX_PICTURE_UPLOAD = "max_picture_upload";
    public static final String IS_UPDATED = "is_updated";

    private static final String TABLE_PICTURES = "Picture";
    public static final String ID_PICTURE = "id";
    public static final String DESCRIPTION = "description";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String FORMAT = "format";
    public static final String USER_ID = "user_id";
    public static final String FLAG = "flag";
    public static final String PHOTO = "photo";
    public static final String PICTURE_MSG = "msg";
    public static final String PICTURE_USERNAME = "username";

    private static final String TABLE_TAG = "Picture_Tag";
    public static final String ID_TAG = "id";
    public static final String TAG_TEXT = "description";
    public static final String TAG_DATE = "create_date";
    public static final String PICTURE_ID = "picture_id";

    private static final String TABLE_LOG = "Logs";
    public static final String ID_LOG = "id";
    public static final String LOG_MSG = "log_msg";
    public static final String LOG_DATE = "log_date";
    public static final String LOG_USER_ID = "user_id";

    SQLiteDatabase db;
    String CREATE_TABLE_USER, CREATE_TABLE_PACKET, CREATE_TABLE_PICTURES,
        CREATE_TABLE_TAG, CREATE_TABLE_LOG;
    ContentValues values;

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    private void createTables(SQLiteDatabase db) {
        CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + " ("
                + ID_USER + " integer primary key autoincrement, "
                + USERNAME + " text, "
                + PASSWORD + " text, "
                + NAME_SURNAME + " text, "
                + EMAIL + " text, "
                + ROLE + " text, "
                + PACKET_ID + " integer, "
                + PACKET_DATE_START + " text, "
                + PACKET_DATE_END + " text, "
                + LOGIN_USER_ID + " text, "
                + PACKET_TYPE + " text)";
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL("INSERT INTO " + TABLE_USER +
                "(" + USERNAME + ", " + PASSWORD + ", " + NAME_SURNAME + ", " + EMAIL + ", " + ROLE + ", "
                + PACKET_ID + ", " + PACKET_DATE_START + ", " + PACKET_DATE_END + ", " + LOGIN_USER_ID + ", " + PACKET_TYPE + ")" +
                "VALUES ('', '', 'Anonymus', '', 'Anonimn', '1', '" + getCurrentDateTime() + "', '', '', 'FREE');");

        CREATE_TABLE_PACKET = "CREATE TABLE " + TABLE_PACKET + " ("
                + ID_PACKET + " integer primary key autoincrement, "
                + TYPE + " text, "
                + PRICE + " double, "
                + ACTIVATION_DATE + " text, "
                + NEW_ACTIVATION_DATE + " text, "
                + DAILY_LIMIT + " integer, "
                + MAX_PICTURE_UPLOAD + " integer, "
                + IS_UPDATED + " text)";
        db.execSQL(CREATE_TABLE_PACKET);
        db.execSQL("INSERT INTO " + TABLE_PACKET +
                "(" + TYPE + ", " + PRICE + ", " + ACTIVATION_DATE + ", " + NEW_ACTIVATION_DATE + ", "
                + DAILY_LIMIT + ", " + MAX_PICTURE_UPLOAD + ", " + IS_UPDATED + ")" +
                "VALUES ('Free', 0.00, '" + getCurrentDateTime() + "', '', 0, 0, '0');");

        CREATE_TABLE_PICTURES = "CREATE TABLE " + TABLE_PICTURES + " ("
                + ID_PICTURE + " integer primary key autoincrement, "
                + DESCRIPTION + " text, "
                + WIDTH + " integer, "
                + HEIGHT + " integer, "
                + FORMAT + " text, "
                + USER_ID + " integer, "
                + FLAG + " text, "
                + PHOTO + " blob, "
                + PICTURE_MSG + " text, "
                + PICTURE_USERNAME + " text)";
        db.execSQL(CREATE_TABLE_PICTURES);

        CREATE_TABLE_TAG = "CREATE TABLE " + TABLE_TAG + " ("
                + ID_TAG + " integer primary key autoincrement, "
                + TAG_TEXT + " text, "
                + TAG_DATE + " text, "
                + PICTURE_ID + " integer)";
        db.execSQL(CREATE_TABLE_TAG);

        CREATE_TABLE_LOG = "CREATE TABLE " + TABLE_LOG + " ("
                + ID_LOG + " integer primary key autoincrement, "
                + LOG_MSG + " text, "
                + LOG_DATE + " text, "
                + LOG_USER_ID + " integer)";
        db.execSQL(CREATE_TABLE_LOG);
    }

    private String getCurrentDateTime() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(c);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
        super.onConfigure(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PACKET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PICTURES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOG);

        onCreate(db);
    }

    public void InsertPicture(Picture picture) {
        db = this.getWritableDatabase();

        values = new ContentValues();
        values.put(DESCRIPTION, picture.getDESCRIPTION());
        values.put(WIDTH, picture.getWIDTH());
        values.put(HEIGHT, picture.getHEIGHT());
        values.put(FORMAT, picture.getFORMAT());
        values.put(USER_ID, picture.getUSER_ID());
        values.put(FLAG, picture.getFLAG());
        values.put(PHOTO, picture.getDATA());
        values.put(PICTURE_MSG, picture.getDATEADDES());
        values.put(PICTURE_USERNAME, picture.getUSERNAME());

        db.insert(TABLE_PICTURES, null, values);
        db.close();
    }

    public void InsertTag(Tag tag) {
        db = this.getWritableDatabase();

        values = new ContentValues();
        values.put(TAG_TEXT, tag.getTAG_TEXT());
        values.put(TAG_DATE, tag.getTAG_DATE());
        values.put(PICTURE_ID, tag.getPICTURE_ID());

        db.insert(TABLE_TAG, null, values);
        db.close();
    }

    public void InsertLog(Log log) {
        db = this.getWritableDatabase();

        values = new ContentValues();
        values.put(LOG_MSG, log.getMSG());
        values.put(LOG_DATE, log.getDATE_MSG());
        values.put(LOG_USER_ID, log.getUSER_ID());

        db.insert(TABLE_LOG, null, values);
        db.close();
    }

    public User getUser() {
        db = this.getReadableDatabase();
        User obj = new User();

        try {
            Cursor cursor = db.rawQuery(("SELECT * FROM " + TABLE_USER + " WHERE " + ID_USER + " = 1;"), null);

            try {
                if (cursor.moveToFirst()) {
                    do {
                        obj.setID(cursor.getInt(0));
                        obj.setUSERNAME(cursor.getString(1));
                        obj.setPASSWORD(cursor.getString(2));
                        obj.setNAME_SURNAME(cursor.getString(3));
                        obj.setEMAIL(cursor.getString(4));
                        obj.setROLE(cursor.getString(5));
                        obj.setPACKET_ID(cursor.getInt(6));
                        obj.setPACKET_DATE_START(cursor.getString(7));
                        obj.setPACKET_DATE_END(cursor.getString(8));
                        obj.setUSER_ID(cursor.getString(9));
                        obj.setPACKET_TYPE(cursor.getString(10));
                        obj.setPICTURE_NUMBER("0");
                    } while (cursor.moveToNext());
                }
            } finally {
                try {
                    cursor.close();
                } catch (Exception ignore) {
                }
            }
        } finally {
            try {
                db.close();
            } catch (Exception ignore) {
            }
        }

        return obj;
    }

    public Packet getPacket() {
        db = this.getReadableDatabase();
        Packet obj = new Packet();

        try {
            Cursor cursor = db.rawQuery(("SELECT * FROM " + TABLE_PACKET + " WHERE " + ID_PACKET + " = 1;"), null);

            try {
                if (cursor.moveToFirst()) {
                    do {
                        obj.setID(cursor.getInt(0));
                        obj.setTYPE(cursor.getString(1));
                        obj.setPRICE(cursor.getDouble(2));
                        obj.setACTIVATION_DATE(cursor.getString(3));
                        obj.setNEW_ACTIVATION_DATE(cursor.getString(4));
                        obj.setDAILY_LIMIT(cursor.getInt(5));
                        obj.setMAX_PICTURE_UPLOAD(cursor.getInt(6));
                        if (cursor.getString(7).equals("1")) {
                            obj.setIS_UPDATED(true);
                        } else {
                            obj.setIS_UPDATED(false);
                        }
                    } while (cursor.moveToNext());
                }
            } finally {
                try {
                    cursor.close();
                } catch (Exception ignore) {
                }
            }
        } finally {
            try {
                db.close();
            } catch (Exception ignore) {
            }
        }

        return obj;
    }

    public Picture getPicture(int id) {
        db = this.getReadableDatabase();
        Picture obj = new Picture();

        try {
            Cursor cursor = db.rawQuery(("SELECT * FROM " + TABLE_PICTURES + " WHERE " + ID_PICTURE + " = " + id + ";"), null);

            try {
                if (cursor.moveToFirst()) {
                    do {
                        obj.setID(cursor.getInt(0));
                        obj.setDESCRIPTION(cursor.getString(1));
                        obj.setWIDTH(cursor.getInt(2));
                        obj.setHEIGHT(cursor.getInt(3));
                        obj.setFORMAT(cursor.getString(4));
                        obj.setUSER_ID(cursor.getInt(5));
                        obj.setFLAG(cursor.getString(6));
                        obj.setDATA(cursor.getBlob(7));
                        obj.setDATEADDES(cursor.getString(8));
                        obj.setUSERNAME(cursor.getString(9));
                    } while (cursor.moveToNext());
                }
            } finally {
                try {
                    cursor.close();
                } catch (Exception ignore) {
                }
            }
        } finally {
            try {
                db.close();
            } catch (Exception ignore) {
            }
        }

        return obj;
    }

    public List<Picture> getAllPictures(String whereClause) {
        db = this.getReadableDatabase();
        ArrayList<Picture> list = new ArrayList<Picture>();
        String selectQuery = "SELECT * FROM " + TABLE_PICTURES + whereClause;
        android.util.Log.v("primjer", "selectQuery-" + selectQuery);

        try {
            Cursor cursor = db.rawQuery(selectQuery, null);

            try {
                if (cursor.moveToFirst()) {
                    do {
                        Picture obj = new Picture();
                        obj.setID(cursor.getInt(0));
                        obj.setDESCRIPTION(cursor.getString(1));
                        obj.setWIDTH(cursor.getInt(2));
                        obj.setHEIGHT(cursor.getInt(3));
                        obj.setFORMAT(cursor.getString(4));
                        obj.setUSER_ID(cursor.getInt(5));
                        obj.setFLAG(cursor.getString(6));
                        obj.setDATA(cursor.getBlob(7));
                        obj.setDATEADDES(cursor.getString(8));
                        obj.setUSERNAME(cursor.getString(9));
                        obj.setLISTA_TAGOVA(getPictureTags(cursor.getInt(0)));

                        list.add(obj);
                    } while (cursor.moveToNext());
                }

            } finally {
                try {
                    cursor.close();
                } catch (Exception ignore) {
                }
            }
        } finally {
            try {
                db.close();
            } catch (Exception ignore) {
            }
        }

        return list;
    }

    public List<Tag> getPictureTags(int pictureID) {
        db = this.getReadableDatabase();
        ArrayList<Tag> list = new ArrayList<Tag>();
        String selectQuery = "SELECT * FROM " + TABLE_TAG + " where " + PICTURE_ID + " = " + pictureID;

        try {
            Cursor cursor = db.rawQuery(selectQuery, null);

            try {
                if (cursor.moveToFirst()) {
                    do {
                        Tag obj = new Tag();
                        obj.setID(cursor.getInt(0));
                        obj.setTAG_TEXT(cursor.getString(1));
                        obj.setTAG_DATE(cursor.getString(2));
                        obj.setPICTURE_ID(cursor.getInt(3));

                        list.add(obj);
                    } while (cursor.moveToNext());
                }

            } finally {
                try {
                    cursor.close();
                } catch (Exception ignore) {
                }
            }
        } finally {
            try {
                db.close();
            } catch (Exception ignore) {
            }
        }

        return list;
    }

    public List<Log> getLogs(String whereClause) {
        db = this.getReadableDatabase();
        ArrayList<Log> list = new ArrayList<Log>();
        String selectQuery = "SELECT * FROM " + TABLE_LOG + whereClause;

        try {
            Cursor cursor = db.rawQuery(selectQuery, null);

            try {
                if (cursor.moveToFirst()) {
                    do {
                        Log obj = new Log();
                        obj.setID(cursor.getInt(0));
                        obj.setMSG(cursor.getString(1));
                        obj.setDATE_MSG(cursor.getString(2));
                        obj.setUSER_ID(cursor.getInt(3));

                        list.add(obj);
                    } while (cursor.moveToNext());
                }

            } finally {
                try {
                    cursor.close();
                } catch (Exception ignore) {
                }
            }
        } finally {
            try {
                db.close();
            } catch (Exception ignore) {
            }
        }

        return list;
    }

    public void UpdateUser(User user) {
        db = this.getWritableDatabase();

        values = new ContentValues();
        values.put(USERNAME, user.getUSERNAME());
        values.put(PASSWORD, user.getPASSWORD());
        values.put(NAME_SURNAME, user.getNAME_SURNAME());
        values.put(EMAIL, user.getEMAIL());
        values.put(ROLE, user.getROLE());
        values.put(PACKET_ID, user.getPACKET_ID());
        values.put(PACKET_DATE_START, user.getPACKET_DATE_START());
        values.put(PACKET_DATE_END, user.getPACKET_DATE_END());
        values.put(LOGIN_USER_ID, user.getUSER_ID());
        values.put(PACKET_TYPE, user.getPACKET_TYPE());

        db.update(TABLE_USER, values, "id = ? ", new String[]{"" + user.getID()});
        db.close();
    }

    public void UpdatePacket(Packet packet) {
        db = this.getWritableDatabase();

        values = new ContentValues();
        values.put(TYPE, packet.getTYPE());
        values.put(PRICE, packet.getPRICE());
        values.put(ACTIVATION_DATE, packet.getACTIVATION_DATE());
        values.put(NEW_ACTIVATION_DATE, packet.getNEW_ACTIVATION_DATE());
        values.put(DAILY_LIMIT, packet.getDAILY_LIMIT());
        values.put(MAX_PICTURE_UPLOAD, packet.getMAX_PICTURE_UPLOAD());
        values.put(IS_UPDATED, packet.isIS_UPDATED());

        db.update(TABLE_PACKET, values, "id = ? ", new String[]{"" + packet.getID()});
        db.close();
    }

    public void UpdatePicture(Picture picture) {
        db = this.getWritableDatabase();

        values = new ContentValues();
        values.put(DESCRIPTION, picture.getDESCRIPTION());
        values.put(WIDTH, picture.getWIDTH());
        values.put(HEIGHT, picture.getHEIGHT());
        values.put(FORMAT, picture.getFORMAT());
        values.put(USER_ID, picture.getUSER_ID());
        values.put(FLAG, picture.getFLAG());

        db.update(TABLE_PICTURES, values, "id = ? ", new String[]{"" + picture.getID()});
        db.close();
    }

    public void UpdateTag(Tag tag) {
        db = this.getWritableDatabase();

        values = new ContentValues();
        values.put(TAG_TEXT, tag.getTAG_TEXT());
        values.put(TAG_DATE, tag.getTAG_DATE());
        values.put(PICTURE_ID, tag.getPICTURE_ID());

        db.update(TABLE_TAG, values, "id = ? ", new String[]{"" + tag.getID()});
        db.close();
    }

    public int getLastPictureId() {
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + ID_PICTURE + " FROM `" + TABLE_PICTURES + "` " + "ORDER BY `id` DESC LIMIT 1", null);
        int cnt = 0;

        try {
            if (cursor != null) {
                cursor.moveToFirst();
                cnt = Integer.parseInt(cursor.getString(0));
            }
        } catch (Exception e) {
            return 0;
        }

        return cnt;
    }

    public ArrayList<Cursor> getData(String Query) {
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[]{"mesage"};
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);

        try {
            String maxQuery = Query;
            Cursor c = sqlDB.rawQuery(maxQuery, null);
            Cursor2.addRow(new Object[]{"Success"});
            alc.set(1, Cursor2);

            if (null != c && c.getCount() > 0) {
                alc.set(0, c);
                c.moveToFirst();

                return alc;
            }
            return alc;
        } catch (Exception ex) {
            Cursor2.addRow(new Object[]{"" + ex.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        }
    }
}
