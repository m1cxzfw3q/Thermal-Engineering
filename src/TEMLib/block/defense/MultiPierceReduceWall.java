package TEMLib.block.defense;

import mindustry.gen.Bullet;
import mindustry.world.blocks.defense.Wall;

public class MultiPierceReduceWall extends Wall {
    public int pierceReduceExtra = 1;

    public MultiPierceReduceWall(String name) {
        super(name);
    }

    public class MultiPierceReduceWallBuild extends WallBuild {
        @Override
        public boolean collision(Bullet bullet) {
            for (var i = 0; i < pierceReduceExtra; i++) {
                bullet.collided.add(id);
            }
            return super.collision(bullet);
        }
    }
}
