package com.valevich.clean.presentation.ui.utils;

import rx.functions.Action1;

public class RxPager {

    private static final int NOT_REQUESTED = -1;

    private final int pageSize;
    private int size = 0;
    private int requested = NOT_REQUESTED;
    private Action1<Integer> onRequest;

    public RxPager(int pageSize, Action1<Integer> onRequest) {
        this.pageSize = pageSize;
        this.onRequest = onRequest;
    }

    public void next() {
        if (size % pageSize == 0 && requested != size) {
            requested = size;
            onRequest.call(size / pageSize);
        }
    }

    public void received(int itemCount) {
        size += itemCount;
    }

}

