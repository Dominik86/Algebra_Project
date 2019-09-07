package algebra.hr.database;

public class User {
    private int ID;
    private String USERNAME;
    private String PASSWORD;
    private String NAME_SURNAME;
    private String EMAIL;
    private String ROLE;
    private int PACKET_ID;
    private String PACKET_DATE_START;
    private String PACKET_DATE_END;
    private String USER_ID;
    private String PACKET_TYPE;
    private String PICTURE_NUMBER;

    public User() {
    }

    public User(int ID, String USERNAME, String PASSWORD, String NAME_SURNAME,
                String EMAIL, String ROLE, int PACKET_ID, String PACKET_DATE_START,
                String PACKET_DATE_END, String USER_ID, String PACKET_TYPE, String PICTURES_NUMBER) {
        this.ID = ID;
        this.USERNAME = USERNAME;
        this.PASSWORD = PASSWORD;
        this.NAME_SURNAME = NAME_SURNAME;
        this.EMAIL = EMAIL;
        this.ROLE = ROLE;
        this.PACKET_ID = PACKET_ID;
        this.PACKET_DATE_START = PACKET_DATE_START;
        this.PACKET_DATE_END = PACKET_DATE_END;
        this.USER_ID = USER_ID;
        this.PACKET_TYPE = PACKET_TYPE;
        this.PICTURE_NUMBER = PICTURES_NUMBER;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }

    public String getNAME_SURNAME() {
        return NAME_SURNAME;
    }

    public void setNAME_SURNAME(String NAME_SURNAME) {
        this.NAME_SURNAME = NAME_SURNAME;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getROLE() {
        return ROLE;
    }

    public void setROLE(String ROLE) {
        this.ROLE = ROLE;
    }

    public int getPACKET_ID() {
        return PACKET_ID;
    }

    public void setPACKET_ID(int PACKET_ID) {
        this.PACKET_ID = PACKET_ID;
    }

    public String getPACKET_DATE_START() {
        return PACKET_DATE_START;
    }

    public void setPACKET_DATE_START(String PACKET_DATE_START) {
        this.PACKET_DATE_START = PACKET_DATE_START;
    }

    public String getPACKET_DATE_END() {
        return PACKET_DATE_END;
    }

    public void setPACKET_DATE_END(String PACKET_DATE_END) {
        this.PACKET_DATE_END = PACKET_DATE_END;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getPACKET_TYPE() {
        return PACKET_TYPE;
    }

    public void setPACKET_TYPE(String PACKET_TYPE) {
        this.PACKET_TYPE = PACKET_TYPE;
    }

    public String getPICTURE_NUMBER() {
        return PICTURE_NUMBER;
    }

    public void setPICTURE_NUMBER(String PICTURE_NUMBER) {
        this.PICTURE_NUMBER = PICTURE_NUMBER;
    }
}
