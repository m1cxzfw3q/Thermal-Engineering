package TEMLib.block.env;

import mindustry.type.Liquid;
import mindustry.world.blocks.environment.Floor;

public class CoverLiquidRequireFloor extends Floor {
    public Liquid liquid;

    public CoverLiquidRequireFloor(String name) {
        super(name);
    }

    public CoverLiquidRequireFloor(String name, Liquid liquid) {
        super(name);
        this.liquid = liquid;
    }
}
