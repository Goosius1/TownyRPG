package com.settings;

import com.TownyRPG;
import com.utils.FileMgmt;

import java.io.File;
import java.io.IOException;

public class Settings {
	private static com.settings.CommentedConfiguration config, newConfig;
	private static File battleIconFile;
	public static final String BATTLE_BANNER_FILE_NAME = "crossedswords.png";

	public static boolean loadSettingsAndLang() {
		TownyRPG rpg = TownyRPG.getTownyRPG();
		boolean loadSuccessFlag = true;

		try {
			Settings.loadConfig(rpg.getDataFolder().getPath() + File.separator + "config.yml", rpg.getVersion());
		} catch (Exception e) {
            System.err.println(rpg.prefix + "Config.yml failed to load! Disabling!");
			loadSuccessFlag = false;
        }

		// Some list variables do not reload upon loadConfig.
		TownyRPGSettings.resetSpecialCaseVariables();
		
		try {
			Translation.loadLanguage(rpg.getDataFolder().getPath() + File.separator, "english.yml");
		} catch (Exception e) {
	        System.err.println(TownyRPG.prefix + "Language file failed to load! Disabling!");
			loadSuccessFlag = false;
	    }

		//Extract images
		try {
			battleIconFile = FileMgmt.extractImageFile(BATTLE_BANNER_FILE_NAME);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(SiegeWar.prefix + "Could not load images! Disabling!");
			loadSuccessFlag = false;
		}

		//Schedule next battle session
		try {
			SiegeWarBattleSessionUtil.scheduleNextBattleSession();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(SiegeWar.prefix + "Problem Scheduling Battle Session! Disabling!");
			loadSuccessFlag = false;
		}

		return loadSuccessFlag;
	}
	
	public static void loadConfig(String filepath, String version) throws Exception {
		if (FileMgmt.checkOrCreateFile(filepath)) {
			File file = new File(filepath);

			// read the config.yml into memory
			config = new com.settings.CommentedConfiguration(file);
			if (!config.load())
				throw new IOException("Failed to load Config!");

			setDefaults(version, file);
			config.save();
		}
	}

	public static void addComment(String root, String... comments) {

		newConfig.addComment(root.toLowerCase(), comments);
	}

	private static void setNewProperty(String root, Object value) {

		if (value == null) {
			value = "";
		}
		newConfig.set(root.toLowerCase(), value.toString());
	}

	private static void setProperty(String root, Object value) {

		config.set(root.toLowerCase(), value.toString());
	}

	public static String getLastRunVersion(String currentVersion) {

		return getString(com.settings.ConfigNodes.LAST_RUN_VERSION.getRoot(), currentVersion);
	}

	/**
	 * Builds a new config reading old config data.
	 */
	private static void setDefaults(String version, File file) {

		newConfig = new com.settings.CommentedConfiguration(file);
		newConfig.load();

		for (com.settings.ConfigNodes root : com.settings.ConfigNodes.values()) {
			if (root.getComments().length > 0)
				addComment(root.getRoot(), root.getComments());
			if (root.getRoot() == com.settings.ConfigNodes.VERSION.getRoot())
				setNewProperty(root.getRoot(), version);
			else if (root.getRoot() == com.settings.ConfigNodes.LAST_RUN_VERSION.getRoot())
				setNewProperty(root.getRoot(), getLastRunVersion(version));
			else
				setNewProperty(root.getRoot(), (config.get(root.getRoot().toLowerCase()) != null) ? config.get(root.getRoot().toLowerCase()) : root.getDefault());
		}

		config = newConfig;
		newConfig = null;
	}

	public static String getString(String root, String def) {

		String data = config.getString(root.toLowerCase(), def);
		if (data == null) {
			sendError(root.toLowerCase() + " from config.yml");
			return "";
		}
		return data;
	}

	private static void sendError(String msg) {

		System.out.println("Error could not read " + msg);
	}

	public static boolean getBoolean(com.settings.ConfigNodes node) {

		return Boolean.parseBoolean(config.getString(node.getRoot().toLowerCase(), node.getDefault()));
	}

	public static double getDouble(com.settings.ConfigNodes node) {

		try {
			return Double.parseDouble(config.getString(node.getRoot().toLowerCase(), node.getDefault()).trim());
		} catch (NumberFormatException e) {
			sendError(node.getRoot().toLowerCase() + " from config.yml");
			return 0.0;
		}
	}

	public static int getInt(com.settings.ConfigNodes node) {

		try {
			return Integer.parseInt(config.getString(node.getRoot().toLowerCase(), node.getDefault()).trim());
		} catch (NumberFormatException e) {
			sendError(node.getRoot().toLowerCase() + " from config.yml");
			return 0;
		}
	}

	public static String getString(com.settings.ConfigNodes node) {

		return config.getString(node.getRoot().toLowerCase(), node.getDefault());
	}

	public static void setLastRunVersion(String currentVersion) {

		setProperty(com.settings.ConfigNodes.LAST_RUN_VERSION.getRoot(), currentVersion);
		config.save();
	}

	public static File getBattleIconFile() {
		return battleIconFile;
	}
}
