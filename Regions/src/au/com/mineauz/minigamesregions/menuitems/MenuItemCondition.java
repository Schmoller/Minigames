package au.com.mineauz.minigamesregions.menuitems;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigamesregions.NodeExecutor;
import au.com.mineauz.minigamesregions.RegionExecutor;
import au.com.mineauz.minigamesregions.conditions.ConditionInterface;

public class MenuItemCondition extends MenuItem{
	
	private RegionExecutor rexec;
	private NodeExecutor nexec;
	private ConditionInterface con;

	public MenuItemCondition(String name, Material displayItem, RegionExecutor exec, ConditionInterface con) {
		super(name, displayItem);
		this.rexec = exec;
		this.con = con;
	}

	public MenuItemCondition(String name, Material displayItem, NodeExecutor exec, ConditionInterface con) {
		super(name, displayItem);
		this.nexec = exec;
		this.con = con;
	}
	
	@Override
	public ItemStack onClick(){
		if(rexec != null){
			if(con.displayMenu(getContainer().getViewer(), getContainer()))
				return null;
		}
		else{
			if(con.displayMenu(getContainer().getViewer(), getContainer()))
				return null;
		}
		return getItem();
	}
	
	@Override
	public ItemStack onRightClick(){
		if(rexec != null)
			rexec.removeCondition(con);
		else
			nexec.removeCondition(con);
		getContainer().removeItem(getSlot());
		return null;
	}

}
