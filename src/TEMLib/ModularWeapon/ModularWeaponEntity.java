package TEMLib.ModularWeapon;

import arc.Core;
import arc.func.Cons;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import mindustry.Vars;
import mindustry.gen.Icon;
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
            dialog.image(unit.type.fullIcon);
            dialog.add("test");
            dialog.addCloseButton();
            dialog.show();
        }).size(32);
        table.button(Icon.cancel, () -> {
            table.remove();
            Core.scene.root.removeChild(table);
        }).size(32);
        Log.info("runGetExtraMenu");
    }
}
