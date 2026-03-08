package TEMod.content.Kepler;

import mindustry.type.SectorPreset;

import static TEMod.TECore.isComplete;
import static TEMod.content.Kepler.KeplerPlanet.*;

public class KeplerSectorPresets {
    public static SectorPreset landingSite;//着陆点

    public static void load(){
        landingSite = new SectorPreset("landing-site", kepler, 53) {{
            alwaysUnlocked = false;
            addStartingItems = true;
            difficulty = 3;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 4f;
            rules = r -> {
                r.attackMode = true;
            };
        }};
        isComplete(KeplerSectorPresets.class);
    }
}
