package com.pauldavdesign.mineauz.minigamesregions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.Event;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.PlayerData;
import com.pauldavdesign.mineauz.minigames.events.EndMinigameEvent;
import com.pauldavdesign.mineauz.minigames.events.JoinMinigameEvent;
import com.pauldavdesign.mineauz.minigames.events.QuitMinigameEvent;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class RegionEvents implements Listener{
	
	private Minigames plugin = Minigames.plugin;
	private PlayerData pdata = plugin.pdata;
	
	private void executeRegionChanges(Minigame mg, MinigamePlayer ply, Event event){
		for(Region r : RegionModule.getMinigameModule(mg).getRegions()){
			if(r.playerInRegion(ply)){
				if(!r.hasPlayer(ply)){
					r.addPlayer(ply);
					r.execute(RegionTrigger.ENTER, ply, null);
				}
			}
			else{
				if(r.hasPlayer(ply)){
					r.removePlayer(ply);
					r.execute(RegionTrigger.LEAVE, ply, null);
				}
			}
		}
	}
	
	@EventHandler
	private void playerMove(PlayerMoveEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply == null) return;
		if(ply.isInMinigame()){
			Minigame mg = ply.getMinigame();
			executeRegionChanges(mg, ply, event);
		}
	}
	
	@EventHandler
	private void playerSpawn(PlayerRespawnEvent event){
		final MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply == null) return;
		if(ply.isInMinigame()){
			final Minigame mg = ply.getMinigame();
			final Event fevent = event;
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				
				@Override
				public void run() {
					executeRegionChanges(mg, ply, fevent);
				}
			});
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void playerJoin(JoinMinigameEvent event){
		final MinigamePlayer ply = event.getMinigamePlayer();
		if(ply == null) return;
		final Minigame mg = event.getMinigame();
		final Event fevent = event;
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				executeRegionChanges(mg, ply, fevent);
			}
		});
	}
	
	@EventHandler
	private void playerQuit(QuitMinigameEvent event){
		MinigamePlayer ply = event.getMinigamePlayer();
		if(ply == null) return;
		Minigame mg = ply.getMinigame();
		for(Region r : RegionModule.getMinigameModule(mg).getRegions()){
			if(r.hasPlayer(ply))
				r.removePlayer(ply);
		}
	}
	
	@EventHandler
	private void playersEnd(EndMinigameEvent event){
		for(MinigamePlayer ply : event.getWinners()){
			Minigame mg = ply.getMinigame();
			for(Region r : RegionModule.getMinigameModule(mg).getRegions()){
				if(r.hasPlayer(ply))
					r.removePlayer(ply);
			}
		}
	}
	
	@EventHandler
	private void interactNode(PlayerInteractEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply == null) return; 
		if(ply.isInMinigame() && 
				((event.getAction() == Action.PHYSICAL && 
					(event.getClickedBlock().getType() == Material.STONE_PLATE || 
					event.getClickedBlock().getType() == Material.WOOD_PLATE || 
					event.getClickedBlock().getType() == Material.IRON_PLATE || 
					event.getClickedBlock().getType() == Material.GOLD_PLATE)) || 
				(event.getAction() == Action.RIGHT_CLICK_BLOCK && 
					(event.getClickedBlock().getType() == Material.WOOD_BUTTON ||
					event.getClickedBlock().getType() == Material.STONE_BUTTON)))){
			for(Node node : RegionModule.getMinigameModule(ply.getMinigame()).getNodes()){
				if(node.getLocation().getWorld() == event.getClickedBlock().getWorld()){
					Location loc1 = node.getLocation();
					Location loc2 = event.getClickedBlock().getLocation();
					if(loc1.getBlockX() == loc2.getBlockX() &&
							loc1.getBlockY() == loc2.getBlockY() &&
							loc1.getBlockZ() == loc2.getBlockZ()){
						node.execute(NodeTrigger.INTERACT, ply, event);
					}
				}
			}
		}
	}
}