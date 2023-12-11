package com.allmagen.testtask.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(indexes = {
        @Index(name = "mmDmaX", columnList = "mmDma"),
        @Index(name = "siteIdX", columnList = "siteId")
})
public class ViewEntity implements Serializable {
    @Id
    @Column(unique = true)
    private String uid;

    private LocalDateTime regTime;
    private int fcImpChk;
    private int fcTimeChk;
    private int utmtr;
    private int mmDma;
    private String osName;
    private String model;
    private String hardware;
    private String siteId;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime getRegTime() {
        return regTime;
    }

    public int getFcImpChk() {
        return fcImpChk;
    }

    public int getFcTimeChk() {
        return fcTimeChk;
    }

    public int getUtmtr() {
        return utmtr;
    }

    public int getMmDma() {
        return mmDma;
    }

    public String getOsName() {
        return osName;
    }

    public String getModel() {
        return model;
    }

    public String getHardware() {
        return hardware;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setRegTime(LocalDateTime regTime) {
        this.regTime = regTime;
    }

    public void setFcImpChk(int fcImpChk) {
        this.fcImpChk = fcImpChk;
    }

    public void setFcTimeChk(int fcTimeChk) {
        this.fcTimeChk = fcTimeChk;
    }

    public void setUtmtr(int utmtr) {
        this.utmtr = utmtr;
    }

    public void setMmDma(int mmDma) {
        this.mmDma = mmDma;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setHardware(String hardware) {
        this.hardware = hardware;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    @Override
    public String toString() {
        return "ViewEntity{" +
                "id=" + uid +
                ", regTime=" + regTime +
                ", fcImpChk=" + fcImpChk +
                ", fcTimeChk=" + fcTimeChk +
                ", utmtr=" + utmtr +
                ", mmDma=" + mmDma +
                ", osName='" + osName + '\'' +
                ", model='" + model + '\'' +
                ", hardware='" + hardware + '\'' +
                ", siteId='" + siteId + '\'' +
                '}';
    }
}
