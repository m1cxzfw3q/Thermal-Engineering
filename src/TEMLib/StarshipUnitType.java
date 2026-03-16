package TEMLib;

import TEMLib.ModularWeapon.ModularWeaponEntity;
import TEMLib.ModularWeapon.ModularWeaponType;
import arc.input.KeyCode;
import arc.math.geom.Point2;
import arc.math.geom.Rect;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import mindustry.gen.*;
import mindustry.type.UnitType;

public class StarshipUnitType extends UnitType implements ModularWeaponType, PermissionLeverUnit {
    public Seq<Point2> modularWeaponsPoint = new Seq<>();
    public int permissionLevel = 0;

    public StarshipUnitType(String name) {
        super(name);
    }

    @Override
    public void display(Unit unit, Table table) {
        super.display(unit, table);

        table.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button) {
                displayExtra(unit);
                return false;
            }

            @Override
            public boolean keyDown(InputEvent event, KeyCode keycode) {
                if (keycode == KeyCode.mouseLeft) displayExtra(unit);
                return false;
            }
        });
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(TEStat.permissionLevel, permissionLevel);
    }

    @Override
    public Point2[] modularWeaponsPoint() {
        return modularWeaponsPoint.toArray();
    }

    @Override
    public int getPermissionLevel() {
        return permissionLevel;
    }

    @Override
    public boolean hasPermissionLevel(int level) {
        return level <= permissionLevel;
    }

    public static class StarshipUnitEntity extends UnitEntity implements ModularWeaponEntity {

    }
}
