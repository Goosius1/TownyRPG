package com.listeners;

import com.TownyRPG;
import com.enums.TownyRPGPermissionNodes;
import com.palmergames.bukkit.towny.event.TownPreClaimEvent;
import com.palmergames.bukkit.towny.event.nation.NationPreTownLeaveEvent;
import com.palmergames.bukkit.towny.event.time.NewShortTimeEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class SafeModeListener implements Listener {

	private void sendErrorMessage(Player player, String message) {
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.DARK_RED + message));
	}
	
	//private String getActionErrMsg() {
		//return "SiegeWar could not load and is in safe mode, action declined.";
	//}

	private String getShortTickErrMsg() {
		return "TownyRPG could not load and is in safe mode.";
	}

	/*
	@EventHandler (priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerBreakDuringSafemode (BlockBreakEvent event) {
		if (!TownyRPG.isError())
			return;
		sendErrorMessage(event.getPlayer(), getActionErrMsg());
		event.setCancelled(true);
	}
	
	@EventHandler (priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerBuildDuringSafemode (BlockPlaceEvent event) {
		if (!SiegeWar.isError())
			return;
		sendErrorMessage(event.getPlayer(), getActionErrMsg());
		event.setCancelled(true);
	}
	
	@EventHandler (priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onTownClaimDuringSafemode (TownPreClaimEvent event) {
		if (!SiegeWar.isError())
			return;
		event.setCancelMessage(getActionErrMsg());
		event.setCancelled(true);
	}

	@EventHandler (priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onTownLeaveNationDuringSafemode (NationPreTownLeaveEvent event) {
		if (!SiegeWar.isError())
			return;
		event.setCancelMessage(getActionErrMsg());
		event.setCancelled(true);
	}

*/

	@EventHandler
	public void onShortTime(NewShortTimeEvent event) {
		if (!TownyRPG.isError())
			return;

		Bukkit.getServer().getOnlinePlayers().stream()
		.filter(player -> player.hasPermission(TownyRPGPermissionNodes.TOWNYRPG_ADMIN_COMMAND.getNode()))
		.forEach(player -> sendErrorMessage(player, getShortTickErrMsg()));
	}

}
