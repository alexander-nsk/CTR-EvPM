package com.allmagen.testtask.model;

import javax.persistence.*;

@Entity
@Table
public class ActionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String uid;

    private String tag;

    public String getTag() {
        return tag;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "ActionEntity{" +
                "uid='" + uid + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}
