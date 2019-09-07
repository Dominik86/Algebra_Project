package algebra.hr.database;

public class Log {
    private int ID;
    private String MSG;
    private String DATE_MSG;
    private int USER_ID;
    private String USERNAME;
    private String TYPE;

    public Log() {
    }

    public Log(int ID, String MSG, String DATE_MSG, int USER_ID, String USERNAME, String TYPE) {
        this.ID = ID;
        this.MSG = MSG;
        this.DATE_MSG = DATE_MSG;
        this.USER_ID = USER_ID;
        this.USERNAME = USERNAME;
        this.TYPE = TYPE;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getMSG() {
        return MSG;
    }

    public void setMSG(String MSG) {
        this.MSG = MSG;
    }

    public String getDATE_MSG() {
        return DATE_MSG;
    }

    public void setDATE_MSG(String DATE_MSG) {
        this.DATE_MSG = DATE_MSG;
    }

    public int getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(int USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }
}
