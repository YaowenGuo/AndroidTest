package org.webrtc;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PeerConnection {
    public static enum AdapterType {
        UNKNOWN(0),
        ETHERNET(1),
        WIFI(2),
        CELLULAR(4),
        VPN(8),
        LOOPBACK(16),
        ADAPTER_TYPE_ANY(32),
        CELLULAR_2G(64),
        CELLULAR_3G(128),
        CELLULAR_4G(256),
        CELLULAR_5G(512);

        public final Integer bitMask;
        private static final Map<Integer, AdapterType> BY_BITMASK = new HashMap();

        private AdapterType(Integer bitMask) {
            this.bitMask = bitMask;
        }

        @Nullable
        @CalledByNative("AdapterType")
        static PeerConnection.AdapterType fromNativeIndex(int nativeIndex) {
            return (PeerConnection.AdapterType)BY_BITMASK.get(nativeIndex);
        }

        static {
            PeerConnection.AdapterType[] var0 = values();
            int var1 = var0.length;

            for(int var2 = 0; var2 < var1; ++var2) {
                PeerConnection.AdapterType t = var0[var2];
                BY_BITMASK.put(t.bitMask, t);
            }

        }
    }
}
