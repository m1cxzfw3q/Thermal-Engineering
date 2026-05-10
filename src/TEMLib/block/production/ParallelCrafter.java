package TEMLib.block.production;

import TEMLib.block.meta.TEStat;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.consumers.ConsumePower;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.Stats;

public class ParallelCrafter extends GenericCrafter {
    public int maxConcurrent = 4;
    public float powerUse = 0f, basePowerUse = 0f;

    public ParallelCrafter(String name) {
        super(name);

        consumesPower = powerUse > 0 || basePowerUse > 0;
        configurable = true;

        consume(new ConsumePower(basePowerUse, 0f, false) {
            @Override
            public void display(Stats stats) {
                stats.add(Stat.powerUse, basePowerUse * 60f, StatUnit.powerSecond);
                if (powerUse > 0) stats.add(TEStat.addPowerUse, powerUse * 60f, TEStat.powerSecondPerConcurrent);
            }
        });
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(TEStat.maxConcurrent, maxConcurrent);
    }

    public class ParallelCrafterBuild extends GenericCrafterBuild {
        protected float[] progresses = new float[maxConcurrent];
        protected boolean[] enabled = new boolean[maxConcurrent];
        protected int maxTasks = maxConcurrent;


    }
}
