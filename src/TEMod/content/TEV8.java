package TEMod.content;

import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.meta.BuildVisibility;

import static TEMod.content.Kepler.KeplerPlanet.kepler;
import static mindustry.Vars.content;

public class TEV8 {
    public static void load() {
        for (Item it : content.items()) {
            if (!it.hidden && it.minfo == null) it.shownPlanets.add(kepler);
        }

        for (UnitType it : content.units()) {
            if (!it.hidden && it.minfo == null) it.shownPlanets.add(kepler);
        }

        for (Liquid it : content.liquids()) {
            if (!it.hidden && it.minfo == null) it.shownPlanets.add(kepler);
        }

        for (Block it : content.blocks()) {
            if (it.buildVisibility != BuildVisibility.hidden && it.buildVisibility != BuildVisibility.debugOnly && it.minfo == null)
                it.shownPlanets.add(kepler);
        }
    }
}
