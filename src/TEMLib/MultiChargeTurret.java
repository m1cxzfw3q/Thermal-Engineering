package TEMLib;

import arc.Core;
import arc.audio.Sound;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import mindustry.entities.Lightning;
import mindustry.entities.bullet.*;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

import static TEMLib.lib.noop;
import static arc.scene.actions.Actions.delay;

public class MultiChargeTurret extends PowerTurret {
    public int maxChargeTier = 2;
    public @NotNull ChargeTier[] tiers = {};
    public @NotNull ChargeTier notCharge = empty(),
            overdriveCharge = empty(),
            superOverdriveCharge = empty();

    public float overdriveChargeExplodeRadius, superOverdriveChargeExplodeRadius,
            overdriveChargeExplodeDamage, superOverdriveChargeExplodeDamage;
    public int superOverdriveChargeExplodeLightnings;//一坨

    public @NotNull Sound shootSound = Sounds.shoot, overdriveShootSound = Sounds.shoot, notChargeSound = Sounds.none;

    public MultiChargeTurret(String name) {
        super(name);

        shootType = null;
    }

    public ChargeTier empty() {
        return new ChargeTier(-1, new BulletType());
    }

    public void MultiCharge(ChargeTier notCharge, ChargeTier overdriveCharge, ChargeTier superOverdriveCharge, ChargeTier[] tiers) {
        this.notCharge = notCharge;
        this.overdriveCharge = overdriveCharge;
        this.superOverdriveCharge = superOverdriveCharge;
        this.tiers = tiers;
    }

    /*
    @Override
    public void setStats() {
        stats.remove(Stat.ammo)
        stats.add(Stat.ammo, StatValues.ammo(ObjectMap.of(Core.bundle.format("misc.multicharge.notCharge-1"), notCharge.bullet)));
        for (int i = 0; i < tiers.length; i++) {
            stats.add(Stat.ammo, StatValues.ammo(ObjectMap.of(Core.bundle.format("misc.multicharge.tier").replace('T', (char) i), tiers[i].bullet)));
        }
        stats.add(Stat.ammo, StatValues.ammo(ObjectMap.of(
                Core.bundle.format("misc.multicharge.overdrive-charge"), overdriveCharge.bullet,
                Core.bundle.format("misc.multicharge.super-ov-charge"), superOverdriveCharge.bullet
        )));
    }
     */

    @Override
    public void setBars() {
        super.setBars();

        addBar("", (MultiChargeTurretBuild entity) -> //屎
                new Bar(
                        () -> entity.chargeTier < 1 ? Core.bundle.format("misc.multicharge.notCharge-0") :
                                entity.chargeTier >= 1 + maxChargeTier ? Core.bundle.format("misc.multicharge.overdrive-charge") :
                                        entity.chargeTier >= 1 ? (Core.bundle.format("misc.multicharge.tier") + entity.chargeTier) : "ERR:NOT_FOUND",
                        () -> entity.chargeTier >= 1 + maxChargeTier ? Pal.health : Pal.ammo,
                        () -> entity.chargeProgress - entity.chargeTier
                )
        );
    }

    public class MultiChargeTurretBuild extends PowerTurretBuild {
        public int chargeTier = 0;
        public float chargeProgress = 0f;

        public void ArcExplosion(float radius, float damage, int lightnings) {
            Call.logicExplosion(Team.derelict, x, y, radius, damage, true, true, false, false);//byd这么长还让不让人活了
            delay(30);
            for (int i = 0; i < lightnings; i++) {
                Lightning.create(Team.derelict, Pal.lancerLaser, damage / 10, x, y, Mathf.random(360f), (int) (radius * 0.8));
                Call.soundAt(Sounds.shootArc, x, y, 1, 1);
            }
        }

        @Override
        public void updateTile() {
            super.updateTile();
            chargeTier = (int) Math.floor(chargeProgress);//神秘
            int nextChargeTier = chargeTier++;

            if (isShooting()) {//屎
                if (chargeProgress < 1f) {
                    chargeProgress += (notCharge.chargeUseTime / 60);
                } else if (chargeTier == 1 + maxChargeTier) {
                    chargeProgress += (overdriveCharge.chargeUseTime / 60);
                } else if (chargeTier >= 2 + maxChargeTier) {
                    resetCharge();
                    shoot(superOverdriveCharge.bullet);
                    Call.soundAt(overdriveShootSound, x, y, 1, 1);
                    ArcExplosion(superOverdriveChargeExplodeRadius, superOverdriveChargeExplodeDamage, superOverdriveChargeExplodeLightnings);
                } else chargeProgress += (tiers[nextChargeTier].chargeUseTime / 60);
            }

            if (!isShooting() && chargeProgress != 0) {
                if (chargeProgress >= 0.7f && chargeTier != 1) {
                    shoot(notCharge.bullet);
                    Call.soundAt(notChargeSound, x, y, 1, 1);
                } else if (chargeTier == 1 + maxChargeTier) {
                    shoot(overdriveCharge.bullet);
                    Call.soundAt(overdriveShootSound, x, y, 1, 1);
                } else if (chargeTier > 0 && chargeTier <= maxChargeTier) {
                    shoot(tiers[chargeTier].bullet);
                    Call.soundAt(shootSound, x, y, 1, 1);
                }
                resetCharge();
            }
        }

        protected void resetCharge() {
            chargeProgress = 0;
            chargeTier = 0;
        }

        @Override
        public BulletType useAmmo(){
            //nothing used directly
            return new BulletType();
        }

        @Override
        public BulletType peekAmmo(){
            return new BulletType();
        }
    }

    public static class ChargeTier {
        public float chargeUseTime;
        public @NotNull BulletType bullet;

        public ChargeTier(float chargeTime, @NotNull BulletType bullet) {
            chargeUseTime = chargeTime;
            this.bullet = bullet;
        }
    }
}
