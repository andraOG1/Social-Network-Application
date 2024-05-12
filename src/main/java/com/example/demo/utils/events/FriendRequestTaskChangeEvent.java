package com.example.demo.utils.events;

import com.example.demo.domain.RelatiePrietenie;

public class FriendRequestTaskChangeEvent extends UserTaskChangeEvent<RelatiePrietenie>{
    private ChangeEventType type;
    private RelatiePrietenie data, oldData;

    public FriendRequestTaskChangeEvent(ChangeEventType type, RelatiePrietenie data) {
        super(type, data);
    }
    public FriendRequestTaskChangeEvent(ChangeEventType type, RelatiePrietenie data, RelatiePrietenie oldData) {
        super(type, data, oldData);
    }
}

