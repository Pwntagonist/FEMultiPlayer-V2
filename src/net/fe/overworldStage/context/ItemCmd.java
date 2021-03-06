package net.fe.overworldStage.context;

import chu.engine.anim.AudioPlayer;
import net.fe.overworldStage.Healthbar;
import net.fe.overworldStage.InventoryMenu;
import net.fe.overworldStage.MenuContext;
import net.fe.overworldStage.OverworldContext;
import net.fe.overworldStage.ClientOverworldStage;
import net.fe.unit.HealingItem;
import net.fe.unit.Item;
import net.fe.unit.ItemDisplay;
import net.fe.unit.Unit;
import net.fe.unit.Weapon;

public class ItemCmd extends MenuContext<ItemDisplay>{
	private Unit unit;
	public ItemCmd(ClientOverworldStage stage, OverworldContext prev, Unit u) {
		super(stage, prev, new InventoryMenu(u));
		unit = u;
	}
	@Override
	public void onSelect(ItemDisplay selectedItem) {
		if(selectedItem == null) return;
		Item i = selectedItem.getItem();
		AudioPlayer.playAudio("select");
		if(i instanceof Weapon){
			if(unit.equippable((Weapon) i)){
				unit.equip((Weapon)i);
				menu.setSelection(0);
			}
		} else if (i instanceof HealingItem){
			if(unit.getHp() == unit.get("HP")) return;
			stage.addCmd("USE");
			stage.addCmd(unit.findItem(i));
			stage.send();
			
			stage.setMenu(null);
			
			int oHp = unit.getHp();
			unit.use(i);
			//TODO Positioning
			stage.addEntity(new Healthbar(unit, oHp, unit.getHp(), stage){
				@Override
				public void done() {
					destroy();
					unit.setMoved(true);
					ItemCmd.this.stage.reset();
				}
			});
		}
	}

	@Override
	public void onLeft() {
		
	}
	@Override
	public void onRight() {
		
	}
	
}
