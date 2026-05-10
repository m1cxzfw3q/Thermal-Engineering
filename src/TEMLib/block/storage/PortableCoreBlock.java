package TEMLib.block.storage;

import mindustry.game.Team;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.state;

public class PortableCoreBlock extends CoreBlock {
    public int coreCap = -1;

    public PortableCoreBlock(String name) {
        super(name);
        buildCostMultiplier = 0.8f;
    }

    public boolean canBreak(Tile tile) {
        return state.teams.cores(tile.team()).size > 1;
    }

    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        if (coreCap > 0) {
            return state.teams.cores(tile.team()).size < coreCap;
        }
        return true;
    }

    public boolean canReplace(Block other) {
        return other.alwaysUnlocked;
    }
}