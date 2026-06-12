package TEMod;

import TEMLib.ModularWeapon.ModularWeaponEntity;
import TEMLib.Utils;
import TEMLib.ui.TEMapInfoDialog;
import TEMod.content.*;
import TEMod.content.Kepler.*;
import arc.Core;
import arc.Events;
import arc.math.Mathf;
import arc.scene.event.Touchable;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.struct.StringMap;
import arc.util.*;
import arc.util.io.PropertiesUtils;
import mindustry.Vars;
import mindustry.editor.MapInfoDialog;
import mindustry.entities.Units;
import mindustry.game.EventType;
import mindustry.gen.Groups;
import mindustry.gen.Icon;
import mindustry.io.SaveFileReader;
import mindustry.io.SaveVersion;
import mindustry.mod.Mod;
import mindustry.mod.Mods;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.LanguageDialog;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import static arc.Core.bundle;
import static arc.Core.files;
import static mindustry.Vars.*;

public class TECore extends Mod {
    public static boolean firstRun = Core.settings.has("firstRun_TEMod") && Core.settings.getBool("firstRun_TEMod");
    public static final Mods.LoadedMod thisLoaded = Vars.mods.getMod(TECore.class);

    public ObjectMap<String, StringMap> hardCodingBundles = ObjectMap.of( //这期神了
            //硬编码翻译文本（）
            "zh_CN", StringMap.of(
                    "temod.welcome-msg.name", "欢迎界面",
                    "temod.welcome-msg.description", "test"
            )
    );

    public TECore() {
        Events.on(EventType.ClientLoadEvent.class, _e -> {
            if (!firstRun && !OS.isAndroid && OS.javaVersionNumber < 17) {
                Log.warn("[TEMod] " + Core.bundle.format("misc.temod-low-java-version", OS.javaVersion.split("\\.")[0]));
                ui.showInfo("[TEMod] " + Core.bundle.format("misc.temod-low-java-version", OS.javaVersion.split("\\.")[0]));
            }

            ui.settings.addCategory("@temod.settingTable", Icon.box, t -> {
                t.checkPref("temod.settingTable.tips", true);
            });

            if (Core.settings.getBool("temod.settingTable.tips")) {
                ui.menufrag.addButton(Core.bundle.get("misc.temod-tips.name"), Icon.book, () -> {
                    BaseDialog dialog = new BaseDialog("@misc.temod-tips.name");
                    dialog.cont.add(Core.bundle.format("misc.temod-tips." + (Mathf.random(11) + 1))).center();
                    dialog.addCloseButton();
                    dialog.show();
                });
            }

            // 以下代码来自MinRi2
            final boolean[] shown = {false};
            Table cont = new Table(), extMenu = new Table(Styles.black3);

            cont.left().bottom();
            cont.setFillParent(true);
            cont.addChild(extMenu);
            cont.visibility = () -> shown[0];
            cont.clicked(() -> shown[0] = false);

            extMenu.touchable = Touchable.enabled;

            ui.hudGroup.addChild(cont);

            Events.on(EventType.TapEvent.class, e -> {
                Units.nearby(e.player.team(), e.tile.worldx(), e.tile.worldy(), 16f, u -> {
                    if (u instanceof ModularWeaponEntity s) {
                        extMenu.clear();
                        s.getExtraMenu(u, extMenu);
                        extMenu.pack();
                        extMenu.setPosition(Core.input.mouseX(), Core.input.mouseY(), Align.top);
                        shown[0] = true;
                    }
                });
            });
        });
        if (!firstRun) {
            Core.settings.put("firstRun_TEMod", true);
            StringMap bundle = hardCodingBundles.get(Locale.getDefault().toString());

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
        if (!OS.isAndroid && OS.javaVersionNumber < 17) return;

        Events.on(EventType.ClientLoadEvent.class, e -> {
            MapInfoDialog info = Reflect.get(ui.editor, "infoDialog");
            info.shown(() -> Core.app.post(() -> {
                ScrollPane pane = (ScrollPane)info.cont.getChildren().get(0);
                Table table = Reflect.get(pane, "widget");

                Table buttonTable = (Table)table.getChildren().peek();
                if(buttonTable.find("temod") != null) return;

                buttonTable.row();

                TEMapInfoDialog mapinfo = new TEMapInfoDialog();

                buttonTable.button(b -> {
                    b.add("[sky][TE]").pad(8f).left();
                    b.add("@temod.mapinfo").expandX();
                }, Styles.cleari, mapinfo::show).name("temod")
                .colspan(buttonTable.getColumns()).width(Float.NEGATIVE_INFINITY).growX();

                buttonTable.row();
            }));
        });

        SaveVersion.addCustomChunk("temod", new SaveFileReader.CustomChunk() {
            @Override
            public void write(DataOutput stream) throws IOException {
                stream.writeBoolean(TEVars.rules.enableAntiCheat);
            }

            @Override
            public void read(DataInput stream) throws IOException {
                TEVars.rules.enableAntiCheat = stream.readBoolean();
            }
        });

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

        Events.run(EventType.Trigger.update, () -> {
            if (!Groups.unit.isEmpty()) {
                Utils.updateEmpathy();
            }

            AntiCheat.update();
        });
    }

    /** Expand a new language for the game. */
    public static void addLanguage(String fullName) {
        if (fullName.contains("_")) {
            String[] str = fullName.split("_");
            locales = Seq.with(locales).add(new Locale(str[0], str[1])).toArray(Locale.class);
        } else {
            locales = Seq.with(locales).add(new Locale(fullName)).toArray(Locale.class);
        }
        Arrays.sort(locales, Structs.comparing(LanguageDialog::getDisplayName, String.CASE_INSENSITIVE_ORDER));

        StringMap newBundle = new StringMap();
        PropertiesUtils.load(newBundle, files.internal("bundles/bundle_" + fullName + ".properties").reader("UTF-8"));
        bundle.getProperties().putAll(newBundle);
    }

    public static void isComplete(Class<?> obj) {
        Log.info("[Thermal-Engineering] isComplete(" + obj + ")");
    }
}