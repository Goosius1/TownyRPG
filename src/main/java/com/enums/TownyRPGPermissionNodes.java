package com.enums;

/**
 * 
 * @author LlmDl
 *
 */
public enum TownyRPGPermissionNodes {

	TOWNYRPG_ADMIN_COMMAND("townyrpg.command.admin.*");

	private String value;

	/**
	 * Constructor
	 * 
	 * @param permission - Permission.
	 */
	TownyRPGPermissionNodes(String permission) {

		this.value = permission;
	}

	/**
	 * Retrieves the permission node
	 * 
	 * @return The permission node
	 */
	public String getNode() {

		return value;
	}

	/**
	 * Retrieves the permission node
	 * replacing the character *
	 * 
	 * @param replace - String
	 * @return The permission node
	 */
	public String getNode(String replace) {

		return value.replace("*", replace);
	}

	public String getNode(int replace) {

		return value.replace("*", replace + "");
	}
}
