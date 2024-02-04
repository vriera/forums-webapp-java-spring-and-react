package ar.edu.itba.paw.models;

import java.util.HashMap;
import java.util.Map;

public enum AccessType {
    ADMITTED, REQUESTED, REQUEST_REJECTED, INVITED, INVITE_REJECTED, LEFT, BLOCKED_COMMUNITY, KICKED, BANNED;

    private static final Map<Integer, AccessType> map = new HashMap<>();

    static {
        for (AccessType type : AccessType.values()) {
            map.put(type.ordinal(), type);
        }
    }

    public static AccessType valueOf(int value) {
        return map.get(value);
    }


}
