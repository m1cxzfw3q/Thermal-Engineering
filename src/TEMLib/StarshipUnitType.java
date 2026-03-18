package TEMLib;

import TEMLib.ModularWeapon.ModularWeaponEntity;
import TEMLib.ModularWeapon.ModularWeaponType;
import arc.Core;
import arc.Graphics;
import arc.input.KeyCode;
import arc.math.geom.Point2;
import arc.math.geom.Rect;
import arc.scene.event.ClickListener;
import arc.scene.event.HandCursorListener;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.entities.Units;
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
        if (unit.within(Vars.player.mouseX, Vars.player.mouseY, hitSize / 2) && Vars.player.shooting) displayExtra(unit);
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
