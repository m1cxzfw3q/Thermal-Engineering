package TEMod.content;

import arc.audio.Sound;
import mindustry.Vars;

public class TESounds {
    public static Sound steelPipeDead, steelPipeAttack, portalCreate, dance, desLaser, desSpearCry;

    public static void load() {
        steelPipeDead = Vars.tree.loadSound("steel-pipe-dead-sound");
        steelPipeAttack = Vars.tree.loadSound("steel-pipe-attack-sound");
        portalCreate = Vars.tree.loadSound("portal-create");
        dance = Vars.tree.loadSound("dance");
        desLaser = Vars.tree.loadSound("des-laser");
        desSpearCry = Vars.tree.loadSound("des-spear-cry");
    }
}
