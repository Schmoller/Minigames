package au.com.mineauz.minigamesregions.conditions;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class TeamScoreRangeCondition extends ConditionInterface {
	
	private IntegerFlag min = new IntegerFlag(5, "min");
	private IntegerFlag max = new IntegerFlag(10, "max");

	@Override
	public String getName() {
		return "TEAM_SCORE_RANGE";
	}

	@Override
	public String getCategory() {
		return "Team Conditions";
	}

	@Override
	public boolean useInRegions() {
		return true;
	}

	@Override
	public boolean useInNodes() {
		return true;
	}

	@Override
	public boolean checkRegionCondition(MinigamePlayer player, Region region) {
		return checkCondition(player);
	}

	@Override
	public boolean checkNodeCondition(MinigamePlayer player, Node node) {
		return checkCondition(player);
	}
	
	private boolean checkCondition(MinigamePlayer player){
		if(player.getTeam() != null){
			Team t = player.getTeam();
			if(t.getScore() >= min.getFlag() && 
					t.getScore() <= max.getFlag()){
				return true;
			}
		}
		return false;
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
		min.saveValue(path, config);
		max.saveValue(path, config);
		saveInvert(config, path);
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
		min.loadValue(path, config);
		max.loadValue(path, config);
		loadInvert(config, path);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev) {
		Menu m = new Menu(3, "Team Score Range", player);
		m.addItem(min.getMenuItem("Minimum Score", Material.STEP, 0, null));
		m.addItem(max.getMenuItem("Maximum Score", Material.DOUBLE_STEP, 0, null));
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
		addInvertMenuItem(m);
		m.displayMenu(player);
		return true;
	}

}
