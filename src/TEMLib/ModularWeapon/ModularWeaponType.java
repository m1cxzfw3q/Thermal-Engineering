package TEMLib.ModularWeapon;

import arc.Core;
import arc.func.Cons;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import mindustry.gen.Icon;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

public interface ModularWeaponType { // 模块设计 移植更容易
    WeaponPoint[] modularWeaponsPoint();

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

    default void initWeapon(UnitType type) {

    }

    class WeaponPoint extends Point2 {
        public boolean mirror = false;

        public WeaponPoint(int x, int y, boolean mirror){
            this.x = x;
            this.y = y;
            this.mirror = mirror;
        }

        public WeaponPoint(int x, int y){
            this.x = x;
            this.y = y;
        }

        public WeaponPoint(Point2 point){
            this.x = point.x;
            this.y = point.y;
        }

        public WeaponPoint(){}
    }
}
