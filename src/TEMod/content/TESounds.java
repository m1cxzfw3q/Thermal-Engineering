package TEMod.content;

import arc.Core;
import arc.audio.Sound;

public class TESounds {
    public static Sound steelPipeDeadSound, steelPipeAttackSound;

    public static void load() {
        Core.assets.load("sounds/steel-pipe-dead-sound.ogg", Sound.class).loaded = (a) -> {
            steelPipeDeadSound = a;
        };
        Core.assets.load("sounds/steel-pipe-attack-sound.ogg", Sound.class).loaded = (a) -> {
            steelPipeAttackSound = a;
        };
    }
}
