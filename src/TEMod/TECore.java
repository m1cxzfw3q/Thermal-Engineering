package TEMod;

import TEMod.content.*;
import TEMod.content.Kepler.*;
import arc.Core;
import arc.Events;
import arc.util.Log;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Icon;
import mindustry.mod.Mod;

public class TECore extends Mod {
    public TECore() {
        Events.on(EventType.ClientLoadEvent.class, e -> {
            Vars.ui.settings.addCategory("@temod.settingTable", Icon.box, T -> {
                T.checkPref("temod.settingTable.tips", true);
            });/*
            if (Core.settings.getBool("temod.settingTable.tips")) {
                String aTipStr = Core.bundle.format("misc.tips") + "\n" + Core.bundle.format("misc.tips-" + (Mathf.random(9) + 1));
                Vars.ui.content.add(aTipStr).left();
                Vars.ui.settings.add(aTipStr).left();
                Vars.ui.database.add(aTipStr).left();
                Vars.ui.join.add(aTipStr).left();
                Vars.ui.host.add(aTipStr).left();
                Vars.ui.maps.add(aTipStr).left();
                Vars.ui.schematics.add(aTipStr).left();
                Vars.ui.bans.add(aTipStr).left();
                Vars.ui.admins.add(aTipStr).left();
                Vars.ui.load.add(aTipStr).left();
                Vars.ui.logic.add(aTipStr).left();
                Vars.ui.campaignComplete.add(aTipStr).left();
                Vars.ui.language.add(aTipStr).left();
            }  TODO 更好的Tips
            */
        });
    }

    @Override
    public void loadContent() {
        TEItems.load();
        TEBlocks.load();
        TESpecialContent.load();
        KeplerPlanet.load();
        KeplerSectorPresets.load();
        TEStatusEffects.load();
        //TEModularWeapons.load();
        //TEUnitTypes.load();

        TETechTree.load();
        TEJsonInterface.load();
        TEV8.load();
        TEFix.load();
        isComplete(this.getClass());
    }

    public static void isComplete(Class<?> obj) {
        Log.info("[Thermal-Engineering] isComplete(" + obj + ")");
    }

    private static String lastTitle;
    private void updateTitle() {
        if(Core.graphics == null) return;
        var title = "Mindustry | ThermalEngineering " + TEVars.version;
        if(!title.equals(lastTitle)) {
            lastTitle = title;
            Core.graphics.setTitle(title);
        }
    }
}
