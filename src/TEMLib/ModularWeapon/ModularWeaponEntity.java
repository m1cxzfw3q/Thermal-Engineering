package TEMLib.ModularWeapon;

import arc.Core;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.gen.Musics;
import mindustry.gen.Unit;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

public interface ModularWeaponEntity {
    default void initWeapon(Unit unit) {

    }

    default void updateWeapon(Unit unit) {

    }

    default void getExtraMenu(Unit unit, Table table) {
        table.background(Styles.black3);
        table.button(Icon.pencil, () -> {
            BaseDialog dialog = new BaseDialog("@temod.modular-weapon.properties");

            Musics.launch.setLooping(true);
            Musics.launch.play();
            Vars.control.sound.stop();

            dialog.image(unit.type.fullIcon);
            dialog.add("test");

            dialog.defaults().size(210, 64f);
            dialog.button("@back", Icon.left, () -> {
                dialog.hide();
                Musics.launch.stop();
                Vars.control.sound.update();
            }).size(210, 64f);
            dialog.addCloseListener();

            dialog.show();
        }).size(32);
        table.button(Icon.cancel, () -> {
            table.remove();
            Core.scene.root.removeChild(table);
        }).size(32);
        Log.info("runGetExtraMenu");
    }
}
