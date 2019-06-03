package cn.demomaster.huan.doctorbaselibrary.model.api;

/**
 * @author squirrel桓
 * @date 2019/1/8.
 * description：
 */
public class AppointmentTimeChangeCommunication {


    /**
     * id : 1
     * trxId : 20
     * sequence : 1
     * senderRole : P
     * sender : 2
     * receiver : 3
     * content : 2019-01-20 19:00:00
     * status : 1
     * createAt : Jan 8, 2019 10:38:50 AM
     */

    private int id;
    private int trxId;
    private int sequence;
    private String senderRole;
    private int sender;
    private int receiver;
    private String sourceId;
    private String content;
    private String status;
    private String createAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTrxId() {
        return trxId;
    }

    public void setTrxId(int trxId) {
        this.trxId = trxId;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public int getReceiver() {
        return receiver;
    }

    public void setReceiver(int receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
}
