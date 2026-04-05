package TEMLib.multiblock;

import mindustry.gen.Building;
import mindustry.world.Block;

public class StructController extends Block {
    public StructInfo[] consInfo;

    public StructController(String name) {
        super(name);
    }

    public static class StructInfo {
        public int x, y;
        public Block blockType;

        public StructInfo(int x, int y, Block blockType) {
            this.x = x;
            this.y = y;
            this.blockType = blockType;
        }
    }

    public static class StructControllerBuild extends Building {

    }
}
