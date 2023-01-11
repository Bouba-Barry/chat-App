package chatUdp.entity;

import java.sql.Date;

public class Invitation {

    private int id;
    private int sender_id;
    private int receiver_id;
    private Date create_at;
    private String etat;

    public Invitation(){}
    public Invitation(int sender_id, int receiver_id, String etat){
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.etat = etat;
    }
    public Invitation(int id, int sender_id, int receiver_id, String etat){
        this.id = id;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.etat = etat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public int getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(int receiver_id) {
        this.receiver_id = receiver_id;
    }

    public Date getCreate_at() {
        return create_at;
    }

    public void setCreate_at(Date create_at) {
        this.create_at = create_at;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }
}
