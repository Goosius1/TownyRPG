package com.settings;


public class TownyRPGSettings {


	protected static void resetSpecialCaseVariables() {
		//mapSneakingItems.clear();
		//worldsWithSiegeWarEnabled.clear();
		//battleSessionsForbiddenBlockMaterials.clear();
		//battleSessionsForbiddenBucketMaterials.clear();
	}

	public static boolean isEnabled() {
		return Settings.getBoolean(ConfigNodes.TOWNY_RPG_ENABLED);
	}

	public static double getToolsDegradeOnDeath() {
		return Settings.getDouble(ConfigNodes.TOWNY_RPG_TOOLS_DEGRADE_ON_DEATH);
	}

}
