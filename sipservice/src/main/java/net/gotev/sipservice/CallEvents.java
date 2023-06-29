package net.gotev.sipservice;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * Call Event class is container for CallStates like
 * {@link CallScreenState OngoingCallState},
 * @see CallScreenState <br>
 * {@link IncomingCallUpdate IncomingCallState}
 * @see CallState <br>
 *
 * implements {@link Parcelable} so that it's instances can be written to
 * and restored from a {@link Parcel}
 *
 * @author rajantalwar
 * @version 1.0
 */
public class CallEvents {

    /**
     * ScreenUpdate class for OngoingCallScreen State
     */
    public static class ScreenUpdate implements Parcelable {
        public final boolean forceUpdate;
        public static CallScreenState callScreenState;

        /**
         *
         * @param callScreenState current state of call for more info refer to
         * {@link CallScreenState}
         * @param forceUpdate boolean @deprecated
         */
        public ScreenUpdate(CallScreenState callScreenState, boolean forceUpdate) {
            this.callScreenState = callScreenState;
            this.forceUpdate = forceUpdate;
        }

        protected ScreenUpdate(Parcel in) {
            forceUpdate = in.readByte() != 0;
        }

        public static final Creator<ScreenUpdate> CREATOR = new Creator<ScreenUpdate>() {
            @Override
            public ScreenUpdate createFromParcel(Parcel in) {
                return new ScreenUpdate(in);
            }

            @Override
            public ScreenUpdate[] newArray(int size) {
                return new ScreenUpdate[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeByte((byte) (forceUpdate ? 1 : 0));
        }

        @NonNull
        @Override
        public String toString() {
            return "ScreenUpdate {" +
                    " forceUpdate = " + forceUpdate +
                    " CallScreenState = "+ callScreenState+
                    " }";
        }
    }

    /**
     * class containing incoming call state for more info refer to {@link CallState}
     */
    public static class IncomingCallUpdate {
        public CallState state;

        public IncomingCallUpdate() {
        }
    }

}
