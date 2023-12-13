package com.allmagen.testtask.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table
public class ActionEntity {
    @EmbeddedId
    private UidTag uidTag;
    private int count;

    public ActionEntity() {
    }

    public ActionEntity(UidTag uidTag, int count) {
        this.uidTag = uidTag;
        this.count = count;
    }

    public UidTag getUidTag() {
        return uidTag;
    }

    public int getCount() {
        return count;
    }

    @Embeddable
    public static class UidTag implements Serializable {
        private String tag;

        @ManyToOne
        @JoinColumn(name = "uid", nullable = false)
        private ViewEntity viewEntity;

        public UidTag() {
        }

        public UidTag(String tag, ViewEntity viewEntity) {
            this.tag = tag;
            this.viewEntity = viewEntity;
        }

        public String getTag() {
            return tag;
        }

        public ViewEntity getViewEntity() {
            return viewEntity;
        }

        public void setViewEntity(ViewEntity viewEntity) {
            this.viewEntity = viewEntity;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UidTag uidTag = (UidTag) o;
            return Objects.equals(tag, uidTag.tag) && Objects.equals(viewEntity, uidTag.viewEntity);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tag, viewEntity);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActionEntity that = (ActionEntity) o;
        return count == that.count && Objects.equals(uidTag, that.uidTag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uidTag, count);
    }
}
