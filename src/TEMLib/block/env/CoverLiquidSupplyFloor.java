package TEMLib.block.env;

import mindustry.type.Liquid;
import mindustry.world.blocks.environment.Floor;

public class CoverLiquidSupplyFloor extends Floor {
    public Liquid liquid;

    public CoverLiquidSupplyFloor(String name, Liquid liquid) {
        super(name);
        this.liquid = liquid;
    }
}
