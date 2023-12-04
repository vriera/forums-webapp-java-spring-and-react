package ar.edu.itba.paw.models;

import java.util.HashMap;
import java.util.Map;

public enum AccessType {
	ADMITTED,
	REQUESTED, // Request can be accepted or rejected by mod
	REQUEST_REJECTED, // Can request again
	INVITED, // Can accept or reject the invitation
	INVITE_REJECTED, // Can be invited again by mod
	LEFT, // No access to content, but can request access again as well as be invited
	BLOCKED, // Blocking stops the user from getting more invites from that community as well as accessing the content
	KICKED, // No access to content, but can request access again as well as be invited
	BANNED, // No access to content, but must be invited to join again
	NONE; // No relationship between the user and the community

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
