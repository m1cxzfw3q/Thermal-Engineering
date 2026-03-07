package TEMod.content;

import arc.Core;
import arc.audio.Sound;

public class TESounds {
    public static Sound steelPipeSound1;

    public static void load() {
        Core.assets.load("sounds/steel-pipe-sound1.ogg", Sound.class).loaded = (a) -> {
            steelPipeSound1 = a;
        };
    }
}
