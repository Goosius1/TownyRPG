package com;

import com.palmergames.bukkit.util.Version;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;
import com.settings.Settings;

import java.io.File;

public class TownyRPG extends JavaPlugin {
	
	private static TownyRPG plugin;
	public static String prefix = "[TownyRPG] ";
	private static Version requiredTownyVersion = Version.fromString("0.96.7.4");
	private static boolean isError;  //Indicates if plugin got an error on startup and is in safe mode

	public static TownyRPG getTownyRPG() {
		return plugin;
	}

	public File getJarFile() {
		return getFile();
	}

    @Override
    public void onEnable() {
    	
    	plugin = this;
    	
    	printSickASCIIArt();
    	
        if (!townyVersionCheck(getTownyVersion())) {
            System.err.println(prefix + "Towny version does not meet required minimum version: " + requiredTownyVersion.toString());
            isError = true;
        } else {
            System.out.println(prefix + "Towny version " + getTownyVersion() + " found.");
        }
        
        if (!Settings.loadSettingsAndLang())
        	isError = true;

        registerCommands();
        
        if (Bukkit.getPluginManager().getPlugin("Towny").isEnabled())
        	TownyRPGController.loadAll();

		if(isError) {
			System.err.println(prefix + "TownyRPG is in safe mode. Dynmap integration disabled.");
		} else {
			Plugin dynmap = Bukkit.getPluginManager().getPlugin("dynmap");
			if (dynmap != null) {
				System.out.println(prefix + "SiegeWar found Dynmap plugin, enabling Dynmap support.");
				DynmapTask.setupDynmapAPI((DynmapAPI) dynmap);
			} else {
				System.out.println(prefix + "Dynmap plugin not found.");
			}
		}

		if(isError) {
			System.err.println(prefix + "SiegeWar is in safe mode. Cannons integration disabled.");
		} else {
			Plugin cannons = Bukkit.getPluginManager().getPlugin("Cannons");
			if (cannons != null) {
				cannonsPluginDetected = true;
				if (SiegeWarSettings.isCannonsIntegrationEnabled()) {
					System.out.println(prefix + "SiegeWar found Cannons plugin, enabling Cannons support.");
					System.out.println(prefix + "Cannons support enabled.");
				}
			} else {
				cannonsPluginDetected = false;
				System.out.println(prefix + "Cannons plugin not found.");
			}
		}

		registerListeners();

		if(isError) {
			System.err.println(prefix + "SiegeWar did not load successfully, and is now in safe mode.");
		} else {
			System.out.println(prefix + "SiegeWar loaded successfully.");
		}
    }
    
    @Override
    public void onDisable() {
    	DynmapTask.endDynmapTask();
    	System.err.println(prefix + "Shutting down....");
    }

	public String getVersion() {
		return this.getDescription().getVersion();
	}
	
    private boolean townyVersionCheck(String version) {
        return Version.fromString(version).compareTo(requiredTownyVersion) >= 0;
    }

    private String getTownyVersion() {
        return Bukkit.getPluginManager().getPlugin("Towny").getDescription().getVersion();
    }
	
	private void registerListeners() {
		PluginManager pm = Bukkit.getServer().getPluginManager();
		
		if (isError)
			pm.registerEvents(new SiegeWarSafeModeListener(), this);
		else {
			pm.registerEvents(new SiegeWarActionListener(this), this);
			pm.registerEvents(new SiegeWarBukkitEventListener(this), this);		
			pm.registerEvents(new SiegeWarTownyEventListener(this), this);
			pm.registerEvents(new SiegeWarNationEventListener(this), this);
			pm.registerEvents(new SiegeWarTownEventListener(this), this);
			pm.registerEvents(new SiegeWarPlotEventListener(this), this);
			if(cannonsPluginDetected)
				pm.registerEvents(new SiegeWarCannonsListener(this), this);
		}
	}

	private void registerCommands() {
		if(isError) {
			System.err.println(prefix + "SiegeWar is in safe mode. SiegeWar commands not registered");
		} else {
			getCommand("siegewar").setExecutor(new SiegeWarCommand());
			getCommand("siegewaradmin").setExecutor(new SiegeWarAdminCommand());
		}
	}

	private void printSickASCIIArt() {
		System.out.println("Towny RPG");
	}

	public static boolean isError() {
		return isError;
	}
}
