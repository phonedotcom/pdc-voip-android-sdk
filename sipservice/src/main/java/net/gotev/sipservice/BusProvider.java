/*
 * Copyright (c) 2023 Phone.com®, All Rights Reserved.
 */
package net.gotev.sipservice;


import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * BusProvider class is used for publishing some events and performing some actions when received
 */
public class BusProvider {

    private static BusProvider mInstance;

    private final PublishSubject<Object> mBus = PublishSubject.create();

    /**
     * Method for providing singleton instance of BusProvider
     *
     * @return singleton instance of BusProvider
     */
    public static BusProvider getInstance() {
        if (mInstance == null) {
            mInstance = new BusProvider();
        }
        return mInstance;
    }


    /**
     * Method for publishing events
     *
     * @param object event which is to be published
     */
    public void send(Object object) {
        mBus.onNext(object);
    }

    /**
     * Method for listening to events and performing actions when received
     *
     * @return the event which is to be observed
     */
    public Observable<Object> toObservable() {
        return mBus;
    }

}
