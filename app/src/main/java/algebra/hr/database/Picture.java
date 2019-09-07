package algebra.hr.database;

import java.util.ArrayList;
import java.util.List;

public class Picture {
    private int ID;
    private String DESCRIPTION;
    private int WIDTH;
    private int HEIGHT;
    private String FORMAT;
    private int USER_ID;
    private String FLAG;
    private byte[] DATA;
    private String DATEADDES;
    private String USERNAME;
    private List<Tag> LISTA_TAGOVA = new ArrayList<>();

    public Picture() {
    }

    public Picture(int ID, String DESCRIPTION, int WIDTH, int HEIGHT,
                   String FORMAT, int USER_ID, String FLAG,
                   byte[] DATA, String DATEADDES, String USERNAME,
                   List<Tag> LISTA_TAGOVA) {
        this.ID = ID;
        this.DESCRIPTION = DESCRIPTION;
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        this.FORMAT = FORMAT;
        this.USER_ID = USER_ID;
        this.FLAG = FLAG;
        this.DATA = DATA;
        this.DATEADDES = DATEADDES;
        this.USERNAME = USERNAME;
        this.LISTA_TAGOVA = LISTA_TAGOVA;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public int getWIDTH() {
        return WIDTH;
    }

    public void setWIDTH(int WIDTH) {
        this.WIDTH = WIDTH;
    }

    public int getHEIGHT() {
        return HEIGHT;
    }

    public void setHEIGHT(int HEIGHT) {
        this.HEIGHT = HEIGHT;
    }

    public String getFORMAT() {
        return FORMAT;
    }

    public void setFORMAT(String FORMAT) {
        this.FORMAT = FORMAT;
    }

    public int getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(int USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getFLAG() {
        return FLAG;
    }

    public void setFLAG(String FLAG) {
        this.FLAG = FLAG;
    }

    public byte[] getDATA() {
        return DATA;
    }

    public void setDATA(byte[] DATA) {
        this.DATA = DATA;
    }

    public String getDATEADDES() {
        return DATEADDES;
    }

    public void setDATEADDES(String DATEADDES) {
        this.DATEADDES = DATEADDES;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public List<Tag> getLISTA_TAGOVA() {
        return LISTA_TAGOVA;
    }

    public void setLISTA_TAGOVA(List<Tag> LISTA_TAGOVA) {
        this.LISTA_TAGOVA = LISTA_TAGOVA;
    }
}
