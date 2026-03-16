package TEMod;

import TEMLib.ModularWeapon.ModularWeapon;
import TEMLib.TEReflect;
import TEMod.content.*;
import TEMod.content.Kepler.*;
import arc.Core;
import arc.Events;
import arc.files.Fi;
import arc.util.I18NBundle;
import arc.util.Log;
import arc.util.Reflect;
import arc.util.Time;
import mindustry.Vars;
import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import mindustry.game.EventType;
import mindustry.gen.Icon;
import mindustry.mod.Mod;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.LanguageDialog;

public class TECore extends Mod {
    public static boolean finalRun = Core.settings.has("finalRun_TEMod") && Core.settings.getBool("finalRun_TEMod");

    public TECore() {
        Events.on(EventType.ClientLoadEvent.class, e -> {
            Vars.ui.settings.addCategory("@temod.settingTable", Icon.box, t -> {
                t.checkPref("temod.settingTable.tips", true);
            });/*
            if (Core.settings.getBool("temod.settingTable.tips")) {
                String aTipStr = Core.bundle.format("misc.tips") + "\n" + Core.bundle.format("misc.tips-" + (Mathf.random(9) + 1));
            }  TODO 更好的Tips
            */
        });
        if (!finalRun || !Core.settings.has("finalRun_TEMod")) {
            Core.settings.put("finalRun_TEMod", true);
            for (Fi file : Vars.mods.getMod("temod").root.child("bundles").list()) {
                if (file.extEquals("properties") && file.nameWithoutExtension().contentEquals(Core.settings.getString("locale"))) {
                    I18NBundle.createBundle(file, Core.bundle.getLocale());
                }
            }

            Time.run(15f, () -> {
                BaseDialog dialog = new BaseDialog("@temod.welcome-msg.name");
            });
        }
    }

    @Override
    public void loadContent() {
        try {
            Log.info("[TECore] Attempt to forcibly expand the ContentType");
            TEReflect.addEnum(ContentType.class, "modularWeapon", ModularWeapon.class);
            TEReflect.setConstant(ContentType.class, "all", ContentType.values());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        TEItems.load();
        TEStatusEffects.load();
        TEModularWeapons.load();
        TEUnitTypes.load();
        TEBlocks.load();
        TESpecialContent.load();
        KeplerPlanet.load();
        KeplerSectorPresets.load();

        TETechTree.load();
        TEJsonInterface.load();
        TEV8.load();
        TEFix.load();
        isComplete(this.getClass());
    }

    public static void isComplete(Class<?> obj) {
        Log.info("[Thermal-Engineering] isComplete(" + obj + ")");
    }
}