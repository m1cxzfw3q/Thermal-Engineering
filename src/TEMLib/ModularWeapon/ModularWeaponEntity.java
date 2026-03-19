package TEMLib.ModularWeapon;

import arc.Core;
import arc.func.Cons;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
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

    default void displayExtra(Unit unit) {
        if (unit instanceof ModularWeaponEntity) {
            Table extMenu = new Table(getExtraMenu());

            Vec2 screenPos = Core.camera.project(Vars.player.mouseX, Vars.player.mouseY);
            extMenu.setPosition(screenPos.x, screenPos.y - extMenu.getMinHeight());
            Core.scene.add(extMenu);
        }
    }

    default Cons<Table> getExtraMenu() {
        return t -> {
            t.background(Styles.none);
            t.button(Icon.pencil, () -> {
                BaseDialog dialog = new BaseDialog("@temod.modular-weapon.properties");
                dialog.addCloseButton();
                dialog.show();
            });
            t.button(Icon.cancel, t::remove);
        };
    }
}
