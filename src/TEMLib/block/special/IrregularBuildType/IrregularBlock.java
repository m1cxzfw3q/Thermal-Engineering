package TEMLib.block.special.IrregularBuildType;

import arc.math.geom.Rect;
import mindustry.gen.Building;
import mindustry.world.Block;
import org.jetbrains.annotations.Nullable;

// TODO
public class IrregularBlock extends Block {
    /** 碰撞箱 **/
    public @Nullable Rect rect;

    public IrregularBlock(String name) {
        super(name);
        rotate = true;
    }

    public static class IrregularBuild extends Building {
        @Override
        public void hitbox(Rect out) {

        }
    }
}
