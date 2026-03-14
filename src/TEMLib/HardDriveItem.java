package TEMLib;

import arc.graphics.Color;
import mindustry.type.Item;
import mindustry.world.modules.ItemModule;
import mindustry.world.modules.LiquidModule;

//TODO 实现这个
public class HardDriveItem extends Item {
    public boolean hasItems = true;
    public boolean hasLiquids = false;
    public ItemModule items;
    public LiquidModule liquids;

    public HardDriveItem(String name, Color color) {
        super(name, color);
    }

    @Override
    public String toString() {
        return super.toString() + "{hasItems:" + hasItems + ";hasLiquids:" + hasLiquids + "}";
    }
}
