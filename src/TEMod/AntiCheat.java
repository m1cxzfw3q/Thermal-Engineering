package TEMod;

import TEMLib.Utils;
import TEMLib.anticheat.AntiCheatSectorPreset;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import mindustry.gen.Groups;
import mindustry.world.blocks.ConstructBlock;

/**
 * TEMod独家反作弊，专治混模勾（目前只能战役用）
 */
public class AntiCheat {
    static final Seq<Content> CONTENT_LIST = new Seq<>();

    static {
        for (ContentType type : ContentType.values()) {
            CONTENT_LIST.add(Vars.content.getBy(type));
        }

        CONTENT_LIST.remove(it -> !it.isVanilla() && it.minfo.mod != TECore.thisLoaded);
    }

    public static void update() {
        if (Vars.state.isGame()) {
            if (Vars.state.getSector().preset != null && Vars.state.getSector().preset instanceof AntiCheatSectorPreset) {
                Groups.unit.each(e -> {
                    if (!CONTENT_LIST.contains(e.type)) {
                        Fx.unitEnvKill.at(e);
                        Utils.removeUnit(e, false);
                    }
                });

                Groups.build.each(e -> {
                    if (
                            !CONTENT_LIST.contains(e.block) ||
                                    (e instanceof ConstructBlock.ConstructBuild b && !CONTENT_LIST.contains(b.current))
                    ) {
                        Fx.unitEnvKill.at(e);
                        e.remove();
                    }
                });

                Groups.bullet.each(e -> {
                    if (!CONTENT_LIST.contains(e.type)) e.remove();
                });

                Groups.weather.each(e -> {
                    if (!CONTENT_LIST.contains(e.weather)) e.remove();
                });


                Vars.player.unit().plans.remove(p -> !CONTENT_LIST.contains(p.block));
                if (!CONTENT_LIST.contains(Vars.player.selectedBlock)) Vars.player.selectedBlock = null;
            }
        }
    }
}
