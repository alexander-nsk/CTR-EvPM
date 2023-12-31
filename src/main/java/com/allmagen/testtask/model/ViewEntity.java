package com.allmagen.testtask.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity class representing views.
 * Views are persisted in a relational database using JPA.
 **/
@Entity
@Table(name = "views_table",
        indexes = {
                @Index(name = "mmDmaIndex", columnList = "mmDma"),
                @Index(name = "siteIdIndex", columnList = "siteId"),
                @Index(name = "regTimeIndex", columnList = "regTime")
        })
public class ViewEntity {
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

    public ViewEntity() {
    }

    public ViewEntity(String uid) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ViewEntity that = (ViewEntity) o;
        return fcImpChk == that.fcImpChk && fcTimeChk == that.fcTimeChk && utmtr == that.utmtr && mmDma == that.mmDma && Objects.equals(uid, that.uid) && Objects.equals(regTime, that.regTime) && Objects.equals(osName, that.osName) && Objects.equals(model, that.model) && Objects.equals(hardware, that.hardware) && Objects.equals(siteId, that.siteId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, regTime, fcImpChk, fcTimeChk, utmtr, mmDma, osName, model, hardware, siteId);
    }
}
