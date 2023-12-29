package com.allmagen.testtask.model;

import jakarta.persistence.*;

import java.util.Objects;

/**
 * Entity class representing actions.
 * Actions are persisted in a relational database using JPA.
 **/
@Entity
@Table(name = "actions_table",
        indexes = {
                @Index(name = "uidIndex", columnList = "uid"),
                @Index(name = "tagIndex", columnList = "tag")
        })
public class ActionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "uid", nullable = false)
    private ViewEntity viewEntity;

    private String tag;
    private int count;

    public ActionEntity() {
    }

    public ActionEntity(ViewEntity viewEntity, String tag) {
        this.viewEntity = viewEntity;
        this.tag = tag;
    }

    /**
     * For tests
     */
    public ActionEntity(Long id, ViewEntity viewEntity, String tag) {
        this.id = id;
        this.viewEntity = viewEntity;
        this.tag = tag;
    }

    public ViewEntity getViewEntity() {
        return viewEntity;
    }

    public String getTag() {
        return tag;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActionEntity action = (ActionEntity) o;
        return count == action.count && Objects.equals(id, action.id) && Objects.equals(viewEntity, action.viewEntity) && Objects.equals(tag, action.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, viewEntity, tag, count);
    }
}
