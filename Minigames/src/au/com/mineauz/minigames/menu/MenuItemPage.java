package au.com.mineauz.minigames.menu;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MenuItemPage extends MenuItem{
	
	private Menu menu = null;
	
	public MenuItemPage(String name, Material displayItem, Menu menu) {
		super(name, displayItem);
		this.menu = menu;
	}

	public MenuItemPage(String name, List<String> description, Material displayItem, Menu menu) {
		super(name, description, displayItem);
		this.menu = menu;
	}
	
	@Override
	public ItemStack onClick(){
		menu.setPreviousPage(getContainer());
		menu.displayMenu(getContainer().getViewer());
		return null;
	}
}
