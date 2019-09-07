package algebra.hr.database;

public class Packet {
    private int ID;
    private String TYPE;
    private Double PRICE;
    private String ACTIVATION_DATE;
    private String NEW_ACTIVATION_DATE;
    private int DAILY_LIMIT;
    private int MAX_PICTURE_UPLOAD;
    private boolean IS_UPDATED;

    public Packet() {
    }

    public Packet(int ID, String TYPE, Double PRICE, String ACTIVATION_DATE,
                  String NEW_ACTIVATION_DATE, int DAILY_LIMIT, int MAX_PICTURE_UPLOAD,
                  boolean IS_UPDATED) {
        this.ID = ID;
        this.TYPE = TYPE;
        this.PRICE = PRICE;
        this.ACTIVATION_DATE = ACTIVATION_DATE;
        this.NEW_ACTIVATION_DATE = NEW_ACTIVATION_DATE;
        this.DAILY_LIMIT = DAILY_LIMIT;
        this.MAX_PICTURE_UPLOAD = MAX_PICTURE_UPLOAD;
        this.IS_UPDATED = IS_UPDATED;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    public Double getPRICE() {
        return PRICE;
    }

    public void setPRICE(Double PRICE) {
        this.PRICE = PRICE;
    }

    public String getACTIVATION_DATE() {
        return ACTIVATION_DATE;
    }

    public void setACTIVATION_DATE(String ACTIVATION_DATE) {
        this.ACTIVATION_DATE = ACTIVATION_DATE;
    }

    public String getNEW_ACTIVATION_DATE() {
        return NEW_ACTIVATION_DATE;
    }

    public void setNEW_ACTIVATION_DATE(String NEW_ACTIVATION_DATE) {
        this.NEW_ACTIVATION_DATE = NEW_ACTIVATION_DATE;
    }

    public int getDAILY_LIMIT() {
        return DAILY_LIMIT;
    }

    public void setDAILY_LIMIT(int DAILY_LIMIT) {
        this.DAILY_LIMIT = DAILY_LIMIT;
    }

    public int getMAX_PICTURE_UPLOAD() {
        return MAX_PICTURE_UPLOAD;
    }

    public void setMAX_PICTURE_UPLOAD(int MAX_PICTURE_UPLOAD) {
        this.MAX_PICTURE_UPLOAD = MAX_PICTURE_UPLOAD;
    }

    public boolean isIS_UPDATED() {
        return IS_UPDATED;
    }

    public void setIS_UPDATED(boolean IS_UPDATED) {
        this.IS_UPDATED = IS_UPDATED;
    }
}
