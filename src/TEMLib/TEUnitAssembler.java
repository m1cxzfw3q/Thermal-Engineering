package TEMLib;

import arc.struct.Seq;
import mindustry.world.Block;
import mindustry.world.blocks.units.UnitAssembler;
import mindustry.world.blocks.units.UnitAssemblerModule;

public class TEUnitAssembler extends UnitAssembler {
    public Seq<Block> acceptsModule = new Seq<>();

    public TEUnitAssembler(String name) {
        super(name);
    }

    public class TEUnitAssemblerBuild extends UnitAssemblerBuild {
        @Override
        public void updateModules(UnitAssemblerModule.UnitAssemblerModuleBuild build){
            if (acceptsModule.isEmpty() || acceptsModule.contains(build.block)) {
                modules.addUnique(build);
                checkTier();
            }
        }

        @Override
        public boolean moduleFits(Block other, float ox, float oy, int rotation) {
            if (!acceptsModule.isEmpty() && !acceptsModule.contains(other)) {
                return false;
            }
            return super.moduleFits(other, ox, oy, rotation);
        }
    }
}