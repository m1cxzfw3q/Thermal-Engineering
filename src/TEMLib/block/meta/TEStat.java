package TEMLib.block.meta;

import mindustry.gen.Iconc;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import mindustry.world.meta.StatUnit;

public class TEStat {
    public static final Stat

    recipe = new Stat("recipe", StatCat.crafting),
    permissionLevel = new Stat("permissionLevel", StatCat.function),
    maxConcurrent = new Stat("maxConcurrent", StatCat.crafting),
    addPowerUse = new Stat("addPowerUse", StatCat.power);

    public static final StatUnit

    powerSecondPerConcurrent = new StatUnit("powerSecondPerConcurrent", "[accent]" + Iconc.power + "[] / [accent]" + Iconc.tree + "[]");
}
