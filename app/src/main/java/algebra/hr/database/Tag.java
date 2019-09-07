package algebra.hr.database;

public class Tag {
    private int ID;
    private String TAG_TEXT;
    private String TAG_DATE;
    private int PICTURE_ID;

    public Tag() {
    }

    public Tag(int ID, String TAG_TEXT, String TAG_DATE, int PICTURE_ID) {
        this.ID = ID;
        this.TAG_TEXT = TAG_TEXT;
        this.TAG_DATE = TAG_DATE;
        this.PICTURE_ID = PICTURE_ID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTAG_TEXT() {
        return TAG_TEXT;
    }

    public void setTAG_TEXT(String TAG_TEXT) {
        this.TAG_TEXT = TAG_TEXT;
    }

    public String getTAG_DATE() {
        return TAG_DATE;
    }

    public void setTAG_DATE(String TAG_DATE) {
        this.TAG_DATE = TAG_DATE;
    }

    public int getPICTURE_ID() {
        return PICTURE_ID;
    }

    public void setPICTURE_ID(int PICTURE_ID) {
        this.PICTURE_ID = PICTURE_ID;
    }
}
