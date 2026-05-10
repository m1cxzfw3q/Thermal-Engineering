package TEMLib.block.defense;

import mindustry.world.meta.Stat;

//TODO wait
public class ExponentiationOverdriveProjector extends ExpandOverdriveProjector {
    public ExponentiationOverdriveProjector(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.remove(Stat.speedIncrease);
        stats.add(Stat.speedIncrease, "+n^" + (int)(speedBoost) + "%");
    }
}
