package TEMLib;

import arc.struct.Seq;
import arc.util.Log;
import mindustry.world.Block;
import mindustry.world.blocks.units.UnitAssembler;
import mindustry.world.blocks.units.UnitAssemblerModule;

public class TEUnitAssembler extends UnitAssembler {
    public Seq<UnitAssemblerModule> acceptsModule = new Seq<>(UnitAssemblerModule.class);

    public TEUnitAssembler(String name) {
        super(name);
    }

    public class TEUnitAssemblerBuild extends UnitAssemblerBuild {
        @Override
        public void updateModules(UnitAssemblerModule.UnitAssemblerModuleBuild build){
            Log.info(!acceptsModule.isEmpty() && acceptsModule.contains(b -> build.block instanceof UnitAssemblerModule us && b == us));
            if (!acceptsModule.isEmpty() && acceptsModule.contains(b -> build.block instanceof UnitAssemblerModule us && b == us)) {
                modules.addUnique(build);
                checkTier();
            }
        }

        @Override
        public boolean moduleFits(Block other, float ox, float oy, int rotation) {
            return super.moduleFits(other, ox, oy, rotation) && (other instanceof UnitAssemblerModule us && acceptsModule.contains(us));
        }
    }
}
