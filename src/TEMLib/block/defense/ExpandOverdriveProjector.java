package TEMLib.block.defense;

import arc.struct.ObjectMap;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.blocks.defense.OverdriveProjector;

//TODO wait
public class ExpandOverdriveProjector extends OverdriveProjector {
    public ObjectMap<Item, Float[]> speedBoostItems = new ObjectMap<>();
    public ObjectMap<Liquid, Float[]> speedBoostLiquids = new ObjectMap<>();
    public ExpandOverdriveProjector(String name) {
        super(name);
    }
}
