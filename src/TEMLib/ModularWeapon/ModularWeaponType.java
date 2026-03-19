package TEMLib.ModularWeapon;

import arc.math.geom.Point2;

public interface ModularWeaponType { // 模块设计 移植更容易
    WeaponPoint[] modularWeaponsPoint();

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
