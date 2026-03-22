package TEMLib;

import mindustry.world.blocks.defense.Thruster;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class FixedThruster extends Thruster {
    /** 火焰的喷射长度(格数) */
    public float flameProjectionLength = 10f;
    /** 火焰的喷射伤害 */
    public float flameProjectionDamage = 125f;

    public FixedThruster(String name) {
        super(name);
        update = canOverdrive = drawDisabled = true;
        crushDamageMultiplier = priority = 1;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.range, flameProjectionLength, StatUnit.blocks);
        stats.add(Stat.damage, flameProjectionDamage, StatUnit.perSecond);
    }

    public class FixedThrusterBuild extends ThrusterBuild {
        @Override
        public void updateTile() {

        }
    }
}
