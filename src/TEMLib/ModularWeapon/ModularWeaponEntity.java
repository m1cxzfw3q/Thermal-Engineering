package TEMLib.ModularWeapon;

import arc.Core;
import arc.scene.ui.layout.Table;
import mindustry.gen.Icon;
import mindustry.gen.Unit;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

// TODO
public interface ModularWeaponEntity {
    default void initWeapon(Unit unit) {

    }

    default void updateWeapon(Unit unit) {

    }

    default void getExtraMenu(Unit unit, Table table) {
        if (unit instanceof ModularWeaponEntity entity && unit.type instanceof ModularWeaponType type) {
            table.table(tab -> {
                tab.button(Icon.pencil, () -> {
                    BaseDialog dialog = new BaseDialog("@temod.modular-weapon.properties");

                    dialog.cont.table( t -> {

                        t.table(Styles.grayPanel, table1 -> {
                            table1.image(unit.type.fullIcon).center();
                        }).fillY().width(400);
                        t.add("test");
                    });
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
