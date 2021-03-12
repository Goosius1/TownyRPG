package com.tasks;

import com.TownyRPG;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.util.StringMgmt;
import com.races.Race;
import com.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynmapTask {

    static DynmapAPI api;
    static MarkerAPI markerapi;
    static boolean stop;
    static MarkerSet set;
    static Map<String, Marker> playerMarkersMap = new HashMap<String, Marker>();
    final static String PEACEFUL_BANNER_ICON_ID = "fire";
    final static String BATTLE_BANNER_ICON_ID = "siegewar.battle";

    public static void setupDynmapAPI(DynmapAPI _api) {
        api = _api;
        markerapi = api.getMarkerAPI();
        if (markerapi == null) {
            System.err.println(TownyRPG.prefix + "Error loading dynmap marker API!");
            return;
        }

        set = markerapi.getMarkerSet("townyrpg.players.markerset");
        if (set == null) {
            set = markerapi.createMarkerSet("townyrpg.players.markerset", "TownyRPGPlayers", null, false);
        } else
            set.setMarkerSetLabel("TownyRPGPlayers");

        if (set == null) {
            System.err.println(TownyRPG.prefix + "Error creating TownyRPGPlayers marker set");
            return;
        }

        //Create the race icons here
        InputStream png;
        for(Race race: Settings.getRaces()) {
            png = TownyRPG.getTownyRPG().getResource(race.getIconFileName());
            markerapi.createMarkerIcon("townyrpg." + race.getId(), race.getName(), png);
        }

        startDynmapTask();
        System.out.println(TownyRPG.prefix + "Dynmap support enabled.");
    }

    public static void startDynmapTask() {
        stop = false;
        Bukkit.getScheduler().runTaskTimerAsynchronously(SiegeWar.getSiegeWar(), () -> {
            if (!stop) {
                hideMapSneakingPlayers();
                displaySieges();
            }
        }, 40l, 300l);
    }

    public static void endDynmapTask() {
        stop = true;
    }

    /**
     * Remove markers belonging to sieges that have ended
     * Also change any icons if required (between peaceful icon & battle icon)
     */
    private static void displaySieges() {
        for (Marker marker : new ArrayList<Marker>(playerMarkersMap.values())) {
            try {
                Siege siege = SiegeController.getSiege(marker.getLabel().replaceAll(".+: ", "").replaceAll(" ", "#"));

                if (!SiegeController.hasActiveSiege(siege.getDefendingTown())) {
                    //Delete marker if siege is over
                    marker.deleteMarker();
                    playerMarkersMap.remove(marker.getMarkerID());

                } else if (marker.getMarkerIcon().getMarkerIconID().equals(PEACEFUL_BANNER_ICON_ID)) {
                    /*
                     * Change to battle icon if siege is in progress,
                     * and battle is active.
                     */
                    if (siege.getStatus() == SiegeStatus.IN_PROGRESS
                        && BattleSession.getBattleSession().isActive()
                        && (siege.getAttackerBattlePoints() > 0
                            || siege.getDefenderBattlePoints() > 0
                            || siege.getBannerControllingSide() != SiegeSide.NOBODY
                            || siege.getBannerControlSessions().size() > 0)) {
                        marker.setMarkerIcon(markerapi.getMarkerIcon(BATTLE_BANNER_ICON_ID));
                    }

                } else if (marker.getMarkerIcon().getMarkerIconID().equals(BATTLE_BANNER_ICON_ID)) {
                    /*
                     * Change to peaceful icon if siege is no longer in progress,
                     * or battle is no longer active.
                     */
                    if (siege.getStatus() != SiegeStatus.IN_PROGRESS
                        || !BattleSession.getBattleSession().isActive()
                        || (siege.getAttackerBattlePoints() == 0
                            && siege.getDefenderBattlePoints() == 0
                            && siege.getBannerControllingSide() == SiegeSide.NOBODY
                            && siege.getBannerControlSessions().size() == 0)) {
                        marker.setMarkerIcon(markerapi.getMarkerIcon(PEACEFUL_BANNER_ICON_ID));
                    }
                }
            } catch (NotRegisteredException e) {
                marker.deleteMarker();
                playerMarkersMap.remove(marker.getMarkerID());
            }
        }

        for (Siege siege : SiegeController.getSieges()) {
            String name = Translation.of("dynmap_siege_title", siege.getName().replace("#", " "));
            try {
                if (siege.getStatus().isActive()) {
                    //If anyone is in a BC session or on the BC list, it is a fire & swords icon
                    //otherwise just fire
                    MarkerIcon siegeIcon;
                    if(siege.getBannerControllingSide() == SiegeSide.NOBODY
                            && siege.getBannerControlSessions().size() == 0) {
                        siegeIcon = markerapi.getMarkerIcon(PEACEFUL_BANNER_ICON_ID);
                    } else {
                        siegeIcon = markerapi.getMarkerIcon(BATTLE_BANNER_ICON_ID);
                    }
                    List<String> lines = new ArrayList<>();
                    lines.add(Translation.of("dynmap_siege_attacker", siege.getAttackingNation().getName()));
                    lines.add(Translation.of("dynmap_siege_defender", siege.getDefendingTown().getName()));
                    lines.add(Translation.of("dynmap_siege_status", siege.getStatus().getName()));
                    lines.add(Translation.of("dynmap_battle_points", siege.getSiegeBalance()));
                    lines.add(Translation.of("dynmap_siege_time_left", siege.getTimeRemaining()));
                    if (TownyEconomyHandler.isActive())
                        lines.add(Translation.of("dynmap_siege_war_chest", TownyEconomyHandler.getFormattedBalance(siege.getWarChestAmount())));
                    lines.add(Translation.of("dynmap_siege_banner_control", siege.getBannerControllingSide().name().charAt(0) + siege.getBannerControllingSide().name().substring(1).toLowerCase()));
                    lines.add(Translation.of("dynmap_siege_battle_score", siege.getFormattedAttackerBattlePoints(), siege.getFormattedDefenderBattlePoints()));
                    lines.add(Translation.of("dynmap_siege_battle_time_left", siege.getFormattedBattleTimeRemaining()));

                    String desc = "<b>" + name + "</b><hr>" + StringMgmt.join(lines, "<br>");
                    Location siegeLoc = siege.getFlagLocation();
                    double siegeX = siegeLoc.getX();
                    double siegeZ = siegeLoc.getZ();
                    String siegeMarkerId = siege.getName();
                    Marker siegeMarker = set.findMarker(siegeMarkerId);
                    if (siegeMarker == null) {
                        set.createMarker(siegeMarkerId, name, siegeLoc.getWorld().getName(), siegeX, 64,
                                siegeZ, siegeIcon, false);
                        
                        siegeMarker = set.findMarker(siegeMarkerId);
                        siegeMarker.setLabel(name);
                        siegeMarker.setDescription(desc);
                    } else {
                        siegeMarker.setLabel(name);
                        siegeMarker.setDescription(desc);
                    }
                    playerMarkersMap.put(siegeMarkerId, siegeMarker);
                }
            } catch (Exception ex) {
                System.err.println(SiegeWar.prefix + "Problem adding siege marker for siege: " + name);
                ex.printStackTrace();
            }
        }

    }

    /**
     * This method hides players who are 'map sneaking'.
     * It also un-hides players who are not.
     */
    private static void hideMapSneakingPlayers() {
        if (!SiegeWarSettings.getWarSiegeMapSneakingEnabled())
            return;

        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

        for (Player player : onlinePlayers) {
            api.assertPlayerInvisibility(player, true, SiegeWar.getSiegeWar());

            if (player.hasMetadata(SiegeWarDynmapUtil.MAP_SNEAK_METADATA_ID)) {
                // Hide from dynmap if map sneaking
                api.assertPlayerInvisibility(player, true, SiegeWar.getSiegeWar());
            } else {
                // Otherwise don't hide
                api.assertPlayerInvisibility(player, false, SiegeWar.getSiegeWar());


                api.getMarkerAPI().getPlayerSet("").
            }
        }
    }
}
