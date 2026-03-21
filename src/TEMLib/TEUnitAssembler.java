package TEMLib;

import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.world.blocks.units.UnitAssembler;
import mindustry.world.blocks.units.UnitAssemblerModule;

public class TEUnitAssembler extends UnitAssembler {
    public @Nullable Seq<UnitAssemblerModule> acceptsModule;

    public TEUnitAssembler(String name) {
        super(name);
    }

    public class TEUnitAssemblerBuild extends UnitAssemblerBuild {
        @Override
        public void updateModules(UnitAssemblerModule.UnitAssemblerModuleBuild build){
            if (acceptsModule != null && acceptsModule.contains(b -> b == build.block)) {
                modules.addUnique(build);
                checkTier();
            }
        }
    }
}
