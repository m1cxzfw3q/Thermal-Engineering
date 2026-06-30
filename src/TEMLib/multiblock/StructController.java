package TEMLib.multiblock;

import arc.struct.Seq;
import mindustry.game.Schematic;
import mindustry.gen.Building;
import mindustry.world.Block;

public class StructController extends Block {
    public Seq<StructInfo> structInfo;
    public int structWidth, structHeight;

    public StructController(String name, Schematic schematic) {
        super(name);
        if (schematic.tiles == null || schematic.tiles.isEmpty()) throw new RuntimeException("schematic.tiles cannot be null (in " + name + ")");
        schematic.tiles.each(tile -> structInfo.add(new StructInfo(tile.x, tile.y, tile.block)));
        structWidth = schematic.width;
        structHeight = schematic.height;
    }

    public static class StructInfo {
        public int x, y;
        public Block block;

        public StructInfo(int x, int y, Block block) {
            this.x = x;
            this.y = y;
            this.block = block;
        }
    }

    public static class StructControllerBuild extends Building {

    }
}
