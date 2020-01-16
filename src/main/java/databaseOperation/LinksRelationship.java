package databaseOperation;

public class LinksRelationship {
    private String devSource;
    private String devDestination;
    private String type;
    private int channel;

    public LinksRelationship(String devSource, String devDestination, String type, int channel) {
        this.devSource = devSource;
        this.devDestination = devDestination;
        this.type = type;
        this.channel = channel;
    }

    public String getDevSource() {
        return devSource;
    }

    public void setDevSource(String devSource) {
        this.devSource = devSource;
    }

    public String getDevDestination() {
        return devDestination;
    }

    public void setDevDestination(String devDestination) {
        this.devDestination = devDestination;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "LinksRelationship{" +
                "devSource='" + devSource + '\'' +
                ", devDestination='" + devDestination + '\'' +
                ", type='" + type + '\'' +
                ", channel=" + channel +
                '}';
    }
}
