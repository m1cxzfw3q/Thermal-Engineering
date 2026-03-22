package TEMLib;

import mindustry.world.blocks.defense.Thruster;

public class FixedThruster extends Thruster {
    /** 火焰的喷射长度(格数) */
    public float flameProjectionLength = 10f;
    /** 火焰的喷射伤害 */
    public float FlameProjectionDamage = 125f;

    public FixedThruster(String name) {
        super(name);
    }

    public class FixedThrusterBuild extends ThrusterBuild {
        @Override
        public void updateTile() {

        }
    }
}
