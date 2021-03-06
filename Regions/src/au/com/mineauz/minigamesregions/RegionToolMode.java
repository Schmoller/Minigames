package au.com.mineauz.minigamesregions;

import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.tool.MinigameTool;
import au.com.mineauz.minigames.tool.ToolMode;

public class RegionToolMode implements ToolMode {

	@Override
	public String getName() {
		return "REGION";
	}

	@Override
	public String getDisplayName() {
		return "Region Selection";
	}

	@Override
	public String getDescription() {
		return "Selects an area;for a region.;Create via left click";
	}

	@Override
	public Material getIcon() {
		return Material.DIAMOND_BLOCK;
	}

	@Override
	public void onSetMode(MinigamePlayer player, MinigameTool tool) {
		tool.addSetting("Region", "None");
		Menu m = new Menu(2, "Region Selection", player);
		if(player.isInMenu()){
			m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, player.getMenu()), m.getSize() - 9);
		}
		final MinigameTool ftool = tool;
		m.addItem(new MenuItemString("Region Name", Material.PAPER, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				ftool.changeSetting("Region", value);
			}
			
			@Override
			public String getValue() {
				return ftool.getSetting("Region");
			}
		}));
		m.displayMenu(player);
	}

	@Override
	public void onUnsetMode(MinigamePlayer player, MinigameTool tool) {
		tool.removeSetting("Region");
	}

	@Override
	public void onLeftClick(MinigamePlayer player, Minigame minigame,
			Team team, PlayerInteractEvent event) {
		if(player.hasSelection()){
			String name = MinigameUtils.getMinigameTool(player).getSetting("Region");
			if(!RegionModule.getMinigameModule(minigame).hasRegion(name)){
				RegionModule.getMinigameModule(minigame).addRegion(name, 
						new Region(name, player.getSelectionPoints()[0], player.getSelectionPoints()[1]));
				player.sendMessage("Created a new region in " + minigame + " called " + name, null);
				player.clearSelection();
			}
			else{
				player.sendMessage("A region already exists by the name " + name + " in " + minigame, "error");
			}
		}
		else{
			player.sendMessage("You need to select a region with right click first!", "error");
		}
	}

	@Override
	public void onRightClick(MinigamePlayer player, Minigame minigame,
			Team team, PlayerInteractEvent event) {
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
			player.addSelectionPoint(event.getClickedBlock().getLocation());
			if(player.hasSelection()){
				player.sendMessage("Selection complete, finalise with left click.", null);
			}
		}
	}

	@Override
	public void select(MinigamePlayer player, Minigame minigame, Team team) {
		RegionModule mod = RegionModule.getMinigameModule(minigame);
		String name = MinigameUtils.getMinigameTool(player).getSetting("Region");
		if(mod.hasRegion(name)){
			player.setSelection(mod.getRegion(name).getFirstPoint(), mod.getRegion(name).getSecondPoint());
			player.sendMessage("Selected the " + name + " region in " + minigame);
		}
		else{
			player.sendMessage("No region created by the name '" + name + "'", "error");
		}
	}

	@Override
	public void deselect(MinigamePlayer player, Minigame minigame, Team team) {
		player.clearSelection();
		player.sendMessage("Cleared selection.", null);
	}

}
