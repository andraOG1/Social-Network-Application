package com.example.demo.domain;

import java.util.Objects;

public class RelatiePrietenie extends Entity<Tuple<Long,Long>>{

    private StatusFriendRequest status;

    //constructor
    public RelatiePrietenie(StatusFriendRequest status)
    {
        this.status = status;
    }

    public StatusFriendRequest getStatus() {
        return status;
    }

    public void setStatus(StatusFriendRequest status)
    {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RelatiePrietenie that = (RelatiePrietenie) o;
        return Objects.equals(status,that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), status);
    }

    @Override
    public String toString() {
        return "RelatiePrietenie{" +
                "status=" + status +
                ", id=" + id +
                '}';
    }
}
