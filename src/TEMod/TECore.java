package TEMod;

import TEMLib.ModularWeapon.ModularWeapon;
import TEMLib.TEReflect;
import TEMod.content.*;
import TEMod.content.Kepler.*;
import arc.Core;
import arc.Events;
import arc.struct.ObjectMap;
import arc.struct.StringMap;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.ctype.ContentType;
import mindustry.game.EventType;
import mindustry.gen.Icon;
import mindustry.mod.Mod;
import mindustry.ui.dialogs.BaseDialog;

public class TECore extends Mod {
    public static boolean finalRun = Core.settings.has("finalRun_TEMod") && Core.settings.getBool("finalRun_TEMod");

    public ObjectMap<String, StringMap> hardCodingBundles = ObjectMap.of( //这期神了
            //硬编码翻译文本（）
            "zh_CN", StringMap.of(
                    "temod.welcome-msg.name", "欢迎界面",
                    "temod.welcome-msg.description", "test"
            )
    );

    public TECore() {
        try {
            Log.info("[TECore] Attempt to forcibly expand the ContentType");
            TEReflect.addEnum(ContentType.class, "modularWeapon", ModularWeapon.class);
            //TEReflect.setConstant(ContentType.class, "all", ContentType.values());  TODO 修复这个问题
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
            StringMap bundle = hardCodingBundles.get(Core.settings.getString("locale", "en"));

            Time.run(15f, () -> {
                BaseDialog dialog = new BaseDialog(bundle.get("temod.welcome-msg.name"));
                dialog.add(bundle.get("temod.welcome-msg.description"));
                dialog.addCloseButton();
                dialog.show();
            });
        }
    }

    @Override
    public void loadContent() {
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