package TEMLib.block.units;

import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.world.Block;
import mindustry.world.blocks.units.UnitAssembler;
import mindustry.world.blocks.units.UnitAssemblerModule;
import mindustry.world.meta.BlockFlag;

import static mindustry.Vars.tilesize;

public class TEUnitAssemblerModule extends UnitAssemblerModule {
    public Seq<Block> acceptsAssembler = new Seq<>();

    public TEUnitAssemblerModule(String name) {
        super(name);
    }

    @Override
    public @Nullable UnitAssembler.UnitAssemblerBuild getLink(Team team, int x, int y, int rotation){
        var results = Vars.indexer.getFlagged(team, BlockFlag.unitAssembler).<UnitAssembler.UnitAssemblerBuild>as().select(b -> acceptsAssembler.contains(b.block));

        return results.find(b -> b.moduleFits(this, x * tilesize + offset, y * tilesize + offset, rotation));
    }
}
