package TEMod.content;

import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.UnitTypes;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.RailBulletType;
import mindustry.world.blocks.defense.turrets.ItemTurret;

public class TEFix {
    public static void load() {
        Items.graphite.hardness = 2;

        UnitTypes.alpha.buildSpeed = 1;
        UnitTypes.alpha.speed = 4;
        UnitTypes.alpha.weapons.get(0).reload = 10;

        UnitTypes.beta.buildSpeed = 1.5f;
        UnitTypes.beta.speed = 4.5f;
        UnitTypes.beta.weapons.get(0).reload = 15;

        UnitTypes.gamma.buildSpeed = 2;
        UnitTypes.gamma.speed = 4.66f;
        UnitTypes.gamma.weapons.get(0).reload = 10;

        Blocks.sporeWall.attributes.set(TEAttribute.sporeWalls, 1);
        Blocks.sporePine.attributes.set(TEAttribute.sporeWalls, 1);
        Blocks.whiteTree.attributes.set(TEAttribute.sporeWalls, 1);

        Blocks.oxidationChamber.canOverdrive = true;

        ((ItemTurret) Blocks.duo).ammoTypes.putAll(
                TEItems.iron, new BasicBulletType(3.1f, 22) {{
                    width = 7f;
                    height = 9f;
                    lifetime = 60f;
                    reloadMultiplier = 1.1f;
                    pierceCap = 1;
                    rangeChange = 16f;

                    hitEffect = despawnEffect = Fx.hitBulletColor;
                    hitColor = backColor = trailColor = frontColor = TEItems.iron.color;
                }}
        );
        ((ItemTurret) Blocks.salvo).ammoTypes.putAll(
                TEItems.iron, new BasicBulletType(3.2f, 22) {{
                    width = 7f;
                    height = 9f;
                    lifetime = 60f;
                    reloadMultiplier = 1.1f;
                    pierceCap = 1;
                    rangeChange = 10f;

                    hitEffect = despawnEffect = Fx.hitBulletColor;
                    hitColor = backColor = trailColor = frontColor = TEItems.iron.color;
                }}
        );
        ((ItemTurret) Blocks.foreshadow).ammoTypes.putAll(
                TEItems.plasticAlloy, new RailBulletType() {{
                    length = 500;
                    shootEffect = TEFx.instShoot;
                    hitEffect = TEFx.instHit;
                    pierceEffect = TEFx.railHit;
                    smokeEffect = Fx.smokeCloud;
                    pointEffect = TEFx.instTrail;
                    despawnEffect = TEFx.instBomb;
                    pointEffectSpace = 20f;
                    damage = 2000;
                    buildingDamageMultiplier = 0.5f;
                    pierceDamageFactor = 0.2f;
                    hitShake = 6f;
                    ammoMultiplier = 2.5f;
                }}
        );
    }
}
