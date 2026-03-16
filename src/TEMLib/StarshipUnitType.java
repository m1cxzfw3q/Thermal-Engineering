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

public class StarshipUnitType extends UnitType implements ModularWeaponType {
    public float sizeX, sizeY;
    public Seq<Point2> modularWeaponsPoint = new Seq<>();

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
    public Point2[] modularWeaponsPoint() {
        return modularWeaponsPoint.toArray();
    }

    public class StarshipUnitEntity extends UnitEntity implements ModularWeaponEntity {
        @Override
        public void hitbox(Rect rect) {
            rect.setCentered(x, y, sizeX != -1 ? sizeX : hitSize, sizeY != -1 ? sizeY : hitSize);
        }

        @Override
        public void hitboxTile(Rect rect) {
            rect.setCentered(x, y, Math.min(sizeX != -1 ? sizeX : hitSize * 0.66f, 7.8f), Math.min(sizeY != -1 ? sizeY : hitSize * 0.66f, 7.8f));
        }
    }
}
