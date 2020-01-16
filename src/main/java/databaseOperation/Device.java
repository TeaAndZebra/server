package databaseOperation;

import java.util.List;
import java.util.Map;

public class Device {
    private String devId;
    private String frame;
    private String encodec;
    private String resolution;
    private String codeRate;
    public Device(String devId, String codeRate, String encodec, String frame, String resolution) {
        this.devId = devId;
        this.codeRate = codeRate;
        this.encodec = encodec;
        this.frame = frame;
        this.resolution = resolution;
    }

    Map<String, Map<String, List<String>>> links = null;
    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getCodeRate() {
        return codeRate;
    }

    public void setCodeRate(String codeRate) {
        this.codeRate = codeRate;
    }

    public String getEncodec() {
        return encodec;
    }

    public void setEncodec(String encodec) {
        this.encodec = encodec;
    }

    public String getFrame() {
        return frame;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }


    public Map<String, Map<String, List<String>>> getLinks() {
        return links;
    }

    public void setLinks(Map<String, Map<String, List<String>>> links) {
        this.links = links;
    }
    @Override
    public String toString() {
        return "Device{" +
                "devId='" + devId + '\'' +
                ", codeRate='" + codeRate + '\'' +
                ", encodec='" + encodec + '\'' +
                ", frame='" + frame + '\'' +
                ", resolution='" + resolution + '\'' +
                ", links=" + links +
                '}';
    }

}
