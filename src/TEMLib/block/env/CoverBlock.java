package TEMLib.block.env;

import arc.util.Nullable;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.gen.Unit;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

import static mindustry.Vars.world;

// TODO
public class CoverBlock extends Block {
    public static Floor[][] convertFloor;

    public CoverBlock(String name) {
        super(name);
        solid = false;
        update = true;
        floating = true;
        allowRectanglePlacement = true;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        Tile floorTile = world.tile(tile.x, tile.y);
        for (Floor[] floors : convertFloor) {
            if (floorTile.floor() == floors[0] && floorTile.floor() != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void placeEnded(Tile tile, @Nullable Unit builder, int rotation, Object config) {
        for (Floor[] floors : convertFloor) {
            if (tile.floor() == floors[0] && tile.floor() != null) {
                tile.setFloor(floors[1]);
            }
        }
        tile.setBlock(Blocks.air);
    }
}
