package au.com.mineauz.minigames.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.minigame.reward.RewardGroup;
import au.com.mineauz.minigames.minigame.reward.RewardItem;
import au.com.mineauz.minigames.minigame.reward.RewardRarity;
import au.com.mineauz.minigames.minigame.reward.Rewards;

public class MenuItemReward extends MenuItem{
	
	private RewardItem item;
	private Rewards rewards;
	private RewardGroup group;
	private List<String> options;

	public MenuItemReward(String name, Material displayItem, RewardItem value, Rewards rewards, List<String> options) {
		super(name, displayItem);
		item = value;
		this.options = options;
		this.rewards = rewards;
		updateDescription();
	}

	public MenuItemReward(String name, List<String> description, Material displayItem, RewardItem value, Rewards rewards, List<String> options) {
		super(name, description, displayItem);
		item = value;
		this.options = options;
		this.rewards = rewards;
		updateDescription();
	}

	public MenuItemReward(String name, Material displayItem, RewardItem value, RewardGroup group, List<String> options) {
		super(name, displayItem);
		item = value;
		this.options = options;
		this.group = group;
	}

	public MenuItemReward(String name, List<String> description, Material displayItem, RewardItem value, RewardGroup group, List<String> options) {
		super(name, description, displayItem);
		item = value;
		this.options = options;
		this.group = group;
	}
	
	public void updateDescription(){
		List<String> description = null;
		int pos = options.indexOf(item.getRarity().toString());
		int before = pos - 1;
		int after = pos + 1;
		if(before == -1)
			before = options.size() - 1;
		if(after == options.size())
			after = 0;
		
		if(getDescription() != null){
			description = getDescription();
			if(getDescription().size() >= 3){
				String desc = ChatColor.stripColor(getDescription().get(1));
				
				if(options.contains(desc)){
					description.set(0, ChatColor.GRAY.toString() + options.get(before));
					description.set(1, ChatColor.GREEN.toString() + item.getRarity().toString());
					description.set(2, ChatColor.GRAY.toString() + options.get(after));
				}
				else{
					description.add(0, ChatColor.GRAY.toString() + options.get(before));
					description.add(1, ChatColor.GREEN.toString() + item.getRarity().toString());
					description.add(2, ChatColor.GRAY.toString() + options.get(after));
				}
			}
			else{
				description.add(0, ChatColor.GRAY.toString() + options.get(before));
				description.add(1, ChatColor.GREEN.toString() + item.getRarity().toString());
				description.add(2, ChatColor.GRAY.toString() + options.get(after));
			}
		}
		else{
			description = new ArrayList<String>();
			description.add(ChatColor.GRAY.toString() + options.get(before));
			description.add(ChatColor.GREEN.toString() + item.getRarity().toString());
			description.add(ChatColor.GRAY.toString() + options.get(after));
		}
		
		setDescription(description);
	}
	
	@Override
	public ItemStack onClick(){
		if(rewards == null) return getItem();
		
		int ind = options.lastIndexOf(item.getRarity().toString());
		ind++;
		if(ind == options.size())
			ind = 0;
		
		item.setRarity(RewardRarity.valueOf(options.get(ind)));
		updateDescription();
		
		return getItem();
	}
	
	@Override
	public ItemStack onRightClick(){
		if(rewards == null) return getItem();
		
		int ind = options.lastIndexOf(item.getRarity().toString());
		ind--;
		if(ind == -1)
			ind = options.size() - 1;
		
		item.setRarity(RewardRarity.valueOf(options.get(ind)));
		updateDescription();
		
		return getItem();
	}
	
	@Override
	public void checkValidEntry(String entry){
		if(entry.equalsIgnoreCase("yes")){
			if(rewards != null)
				rewards.removeReward(item);
			else
				group.removeItem(item);
			getContainer().removeItem(this.getSlot());

			getContainer().cancelReopenTimer();
			getContainer().displayMenu(getContainer().getViewer());
			return;
		}
		getContainer().cancelReopenTimer();
		getContainer().displayMenu(getContainer().getViewer());
		
		getContainer().getViewer().sendMessage("The selected item will not be removed from the rewards.", "error");
	}
	
	@Override
	public ItemStack onShiftRightClick(){
		MinigamePlayer ply = getContainer().getViewer();
		ply.setNoClose(true);
		ply.getPlayer().closeInventory();
		String itemName = "";
		if(item.getItem() != null)
			itemName = MinigameUtils.getItemStackName(item.getItem());
		else
			itemName = "$" + item.getMoney();
		ply.sendMessage("Delete the reward item \"" + itemName + "\"? Type \"Yes\" to confirm.", null);
		ply.sendMessage("The menu will automatically reopen in 10s if nothing is entered.");
		ply.setManualEntry(this);

		getContainer().startReopenTimer(10);
		return null;
	}
}
