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
        if (unit instanceof ModularWeaponEntity entity) {
            table.table(tab -> {
                tab.button(Icon.pencil, () -> {
                    BaseDialog dialog = new BaseDialog("@temod.modular-weapon.properties");

                    dialog.image(unit.type.fullIcon).left();
                    dialog.add("test");
                    dialog.addCloseButton();
                    dialog.show();
                }).size(32);
                tab.button(Icon.cancel, () -> {
                    tab.remove();
                    Core.scene.root.removeChild(tab);
                }).size(32);
            });
        }
    }
}
