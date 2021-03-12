package com.settings;

public enum ConfigNodes {

	VERSION_HEADER("version", "", ""),
	VERSION(
			"version.version",
			"",
			"# This is the current version.  Please do not edit."),
	LAST_RUN_VERSION(
			"version.last_run_version",
			"",
			"# This is for showing the changelog on updates.  Please do not edit."),
	LANGUAGE(
			"language",
			"english.yml",
			"# The language file you wish to use"),
	TOWNY_RPG(
			"towny.rpg",
			"",
			"############################################################",
			"# +------------------------------------------------------+ #",
			"# |                Towny RPG settings                 | #",
			"# +------------------------------------------------------+ #",
			"############################################################",
			""),
	TOWNY_RPG_ENABLED(
			"towny.rpg.enabled",
			"true",
			"",
			"# If true, thr TownyRPG system is enabled.",
			"# if false, the TownyRPG system is disabled."),

	TOWNY_RPG_TOOLS_DEGRADE_ON_DEATH(
			"towny.rpg.tools.degrade.on.death",
			"0.2",
			"",
			"# Dropping items on death was a staple of MMORPG's around the year 2000",
			"# The MMORPG industry recognised over the last decade that this pattern is simply not fun for the majority of players.",
			"# Thus the MMORPG industry has moved on, with most games (e.g. WOW, LOTRO, Everquest) no longer removing any items on death",
			"# In Towny, the pattern would be problematic, as item loss would often contribute to the political power of an enemy.",
			"# In TownyRPG, the pattern would be even worse, as items would be lost more frequently due to ignored Towny PVP protections.",
			"# Thus TownyRPG has no room for such a pattern, and players keep items on death as per most modern MMORPGs.",
			"# A tools degrade is also applied, with configurable rate.");

	private final String Root;
	private final String Default;
	private String[] comments;

	ConfigNodes(String root, String def, String... comments) {

		this.Root = root;
		this.Default = def;
		this.comments = comments;
	}

	/**
	 * Retrieves the root for a config option
	 *
	 * @return The root for a config option
	 */
	public String getRoot() {

		return Root;
	}

	/**
	 * Retrieves the default value for a config path
	 *
	 * @return The default value for a config path
	 */
	public String getDefault() {

		return Default;
	}

	/**
	 * Retrieves the comment for a config path
	 *
	 * @return The comments for a config path
	 */
	public String[] getComments() {

		if (comments != null) {
			return comments;
		}

		String[] comments = new String[1];
		comments[0] = "";
		return comments;
	}

}
