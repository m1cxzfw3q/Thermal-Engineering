package TEMLib.ModularWeapon;

import arc.Core;
import arc.func.Cons;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import mindustry.gen.Icon;
import mindustry.gen.Unit;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

public interface ModularWeaponType {
    Point2[] modularWeaponsPoint();

    default void displayExtra(Unit unit) {
        if (unit instanceof ModularWeaponEntity) {
            Table extMenu = new Table(getExtraMenu());

            float screenX = Mathf.clamp(Core.camera.project(unit.x, unit.y).x, 0, Core.graphics.getWidth() - extMenu.getWidth());
            float screenY = Mathf.clamp(Core.camera.project(unit.x, unit.y).y, 0, Core.graphics.getHeight() - extMenu.getHeight());

            extMenu.setPosition(screenX, screenY);
            Core.scene.add(extMenu);
            Log.info("runDisplayExtra");
        }
    }

    default Cons<Table> getExtraMenu() {
        return t -> t.button(Icon.pencil, Styles.cleari, () -> {
            BaseDialog dialog = new BaseDialog("@temod.modular-weapon.properties");
            dialog.addCloseButton();
            dialog.show();
        });
    }
}
