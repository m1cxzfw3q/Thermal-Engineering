package TEMod;

import TEMLib.ModularWeapon.ModularWeapon;
import TEMLib.ModularWeapon.ModularWeaponEntity;
import TEMLib.TEReflect;
import TEMLib.lib;
import TEMod.content.*;
import TEMod.content.Kepler.*;
import arc.Core;
import arc.Events;
import arc.math.Mathf;
import arc.scene.event.Touchable;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.struct.StringMap;
import arc.util.*;
import mindustry.Vars;
import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import mindustry.entities.Units;
import mindustry.game.EventType;
import mindustry.gen.Groups;
import mindustry.gen.Icon;
import mindustry.mod.Mod;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Arrays;

import static mindustry.Vars.ui;

@SuppressWarnings("unchecked")
public class TECore extends Mod {
    private static final Unsafe UNSAFE = Reflect.get(Unsafe.class, "theUnsafe");;
    public static boolean firstRun = Core.settings.has("firstRun_TEMod") && Core.settings.getBool("firstRun_TEMod");

    public ObjectMap<String, StringMap> hardCodingBundles = ObjectMap.of( //这期神了
            //硬编码翻译文本（）
            "zh_CN", StringMap.of(
                    "temod.welcome-msg.name", "欢迎界面",
                    "temod.welcome-msg.description", "test"
            )
    );

    /*
    {
        try {
            Log.info("[TECore] Attempt to forcibly expand the ContentType");
            TEReflect.addEnum(ContentType.class, "modularWeapon", ModularWeapon.class);
            TEReflect.setStaticFinalField(ContentType.class, "all", ContentType.values());
            Field field = Vars.content.getClass().getDeclaredField("contentMap");
            Object staticBase = UNSAFE.staticFieldBase(field);
            long offset = UNSAFE.staticFieldOffset(field);
            Seq<Content>[] original = (Seq<Content>[]) UNSAFE.getObject(staticBase, offset);

            Seq<Content>[] newElements = new Seq[]{new Seq<>(Content.class)};
            // 2. 创建新数组（纯 Java 方式）
            Seq<Content>[] newArray = Arrays.copyOf(original, original.length + newElements.length);
            System.arraycopy(newElements, 0, newArray, original.length, newElements.length);

            // 3. 替换数组引用（再次使用 Unsafe 写入）
            UNSAFE.putObject(staticBase, offset, newArray);
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
    }
     */

    public TECore() {
        if (!firstRun && !OS.isAndroid && Strings.parseInt(OS.javaVersion.split("\\.")[0]) < 17) {
            Events.on(EventType.ClientLoadEvent.class, _e -> {
                Log.warn("[TEMod] " + Core.bundle.format("misc.temod-low-java-version", OS.javaVersion.split("\\.")[0]));
                ui.showInfo("[TEMod] " + Core.bundle.format("misc.temod-low-java-version", OS.javaVersion.split("\\.")[0]));
            });
        }

        Events.on(EventType.ClientLoadEvent.class, _e -> {
            ui.settings.addCategory("@temod.settingTable", Icon.box, t -> {
                t.checkPref("temod.settingTable.tips", true);
            });

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

            if (Core.settings.getBool("temod.settingTable.tips")) {
                ui.menufrag.addButton(Core.bundle.get("misc.temod-tips.name"), Icon.book, () -> {
                    BaseDialog dialog = new BaseDialog("@misc.temod-tips.name");
                    dialog.cont.add(Core.bundle.format("misc.temod-tips." + (Mathf.random(9) + 1))).center();
                    dialog.addCloseButton();
                    dialog.show();
                });
            }
        });
        if (!firstRun || !Core.settings.has("firstRun_TEMod")) {
            Core.settings.put("firstRun_TEMod", true);
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
        if (!OS.isAndroid && Strings.parseInt(OS.javaVersion.split(".")[0]) < 17) return;
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

        Events.on(EventType.Trigger.class, e -> {
            if (e == EventType.Trigger.update && !Groups.unit.isEmpty()) {
                lib.updateEmpathy();
            }
        });
    }

    public static void isComplete(Class<?> obj) {
        Log.info("[Thermal-Engineering] isComplete(" + obj + ")");
    }
}