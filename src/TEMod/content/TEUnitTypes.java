package TEMod.content;

import TEMLib.*;
import TEMLib.entities.abilities.MuzzleSwingAbility;
import TEMLib.entities.abilities.TEShieldArcAbility;
import TEMLib.entities.parts.TEHoverPart;
import TEMLib.entities.unit.StarshipUnitType;
import arc.graphics.Color;
import arc.graphics.g2d.Lines;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.util.Strings;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.gen.*;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

/** 开始画大饼了 */
public class TEUnitTypes {
    /** 传奇T6 */
    public static UnitType coupling;//致敬传奇肘击王耦合
    /** baimao投稿的单位 */
    public static UnitType siegeTank, liberator;
    /** 只要写成了我就是全mdt最强开发者写不成就是全mdt最fw开发者 之一的LTX(我正在开发的某独立游戏的代号)星舰单位 */
    public static StarshipUnitType cosmicStarship; //宇宙级星舰(别问为啥叫这个名)

    /** 特种单位 T3 */
    public static UnitType flame;
    //炽焰(战锤)

    /** 特种单位 T4 */
    public static UnitType incinerate;
    //焚世(战锤)

    /** 特种单位 T5 */
    public static UnitType destruction;
    //毁灭(陆军)

    //特种单位没有T6

    /** 特殊单位 */
    public static UnitType
    steelPipe, //钢管
    testDrone  //测试用无人机
    ;


    public static void load() {
        steelPipe = new UnitType("steel-pipe") {{
            health = 1145;
            armor = 30;
            constructor = MechUnit::create;
            hitSize = 17;
            drawCell = false;
            range = 400;
            speed = 3;
            deathSound = TESounds.steelPipeDead;
            deathSoundVolume = 0.6f;
            faceTarget = false;
            outlines = false;
            weapons.add(new Weapon() {{
                reload = 20;
                shoot.firstShotDelay = 120;
                shootCone = 360;
                mirror = false;
                x = y = 0;
                shootSound = Sounds.none;
                bullet = new BulletType(0, 0.25f) {
                    @Override
                    public void createSplashDamage(Bullet b, float x, float y) {
                        if(splashDamageRadius > 0 && !b.absorbed){
                            Utils.damage(b.team, x, y, splashDamageRadius, damage, splashDamagePierce, collidesAir, collidesGround, scaledSplashDamage, b);
                        }
                    }
                    {
                        splashDamagePierce = true;
                        splashDamageRadius = 400;
                        range = 400;
                        lifetime = -1;
                        collides = false;
                        despawnHit = true;
                        hitEffect = despawnEffect = shootEffect = smokeEffect = Fx.none;
                    }
                };
            }
                @Override
                public void addStats(UnitType u, Table t) {
                    if(inaccuracy > 0){
                        t.row();
                        t.add("[lightgray]" + Stat.inaccuracy.localized() + ": [white]" + (int)inaccuracy + " " + StatUnit.degrees.localized());
                    }
                    if(!alwaysContinuous && reload > 0 && !bullet.killShooter){
                        t.row();
                        t.add("[lightgray]" + Stat.reload.localized() + ": " + (mirror ? "2x " : "") + "[white]" + Strings.autoFixed(60f / reload * shoot.shots, 2) + " " + StatUnit.perSecond.localized());
                    }

                    Utils.ammo(ObjectMap.of(u, bullet)).display(t);
                }
            });

            abilities.addAll(
                    new MuzzleSwingAbility("-pipe-0") {{
                        moveTime = 60;
                        waitTime = 40;
                        sound = TESounds.steelPipeAttack;
                    }},
                    new MuzzleSwingAbility("-pipe-1") {{
                        moveTime = 60;
                        waitTime = 40;
                    }},
                    new MuzzleSwingAbility("-pipe-2") {{
                        moveTime = 60;
                        waitTime = 40;
                    }},
                    new MuzzleSwingAbility("-pipe-3") {{
                        moveTime = 60;
                        waitTime = 40;
                    }}
            );
        }};

        cosmicStarship = new StarshipUnitType("cosmic-starship") {{
            description = "test";
            health = 200000000;
            armor = 4000;
            deathExplosionEffect = new MultiEffect(
                    new Effect(120, e -> {
                        Lines.stroke(1000 / (e.fin() * 100));
                        Lines.circle(e.x, e.y, e.fin() * 30000);
                    }),
                    Fx.dynamicExplosion
            );
            speed = 4;
            hitSize = 540;
            permissionLevel = 9;
            starshipTier = 8;
            flying = true;
            accel = 0.08f;
            drag = 0.01f;
            buildRange = 10000;
            buildSpeed = 15;
            immunities.addAll(Vars.content.statusEffects().select(Utils::isDebuff));
            abilities.addAll(
                    new TEShieldArcAbility() {{
                        radius = 1000;
                        whenShooting = false;
                        max = 50000000;
                        cooldown = 30;
                        regen = 250000;
                        width = 40;
                        angle = 750;
                    }}
            );
            for(float f : new float[]{-700f, -600f, -500f, -400f, -300f, -200f, -100f, 0f, 100f, 200f, 300f, 400f, 500f, 600f, 700f, 800f}){
                parts.add(new TEHoverPart(){{
                    x = 160f;
                    y = f;
                    mirror = true;
                    radius = 80f;
                    phase = 45f;
                    rotation = 90;
                    stroke = 20f;
                    layerOffset = -0.001f;
                    color = Color.valueOf("3286E5");
                    angle = 45;
                }});
            }
            parts.add(
                    new TEHoverPart(){{
                        x = 100f;
                        y = -770f;
                        mirror = true;
                        radius = 70f;
                        phase = 45f;
                        rotation = 90;
                        stroke = 20f;
                        layerOffset = -0.001f;
                        color = Color.valueOf("3286E5");
                        angle = 45;
                    }},
                    new TEHoverPart(){{
                        x = 30f;
                        y = -770f;
                        mirror = true;
                        radius = 70f;
                        phase = 45f;
                        rotation = 90;
                        stroke = 20f;
                        layerOffset = -0.001f;
                        color = Color.valueOf("3286E5");
                        angle = 45;
                    }}
            );

            engines.clear();
            rotateSpeed = 0.3f;
            engineSize = 0;
        }};

        testDrone = new UnitType("test-drone") {{
            health = 140;
            hitSize = 12;
            itemCapacity = 20;
            buildSpeed = 0.5f;
            buildRange = 110;
            playerControllable = false;
            logicControllable = false;
            constructor = UnitEntity::create;
            flying = true;
            mineSpeed = 5;
            mineRange = 110;
            mineTier = 2;
            mineFloor = true;
            lowAltitude = true;
            envDisabled = 0;
            envEnabled = -1;
            isEnemy = false;
            useUnitCap = false;
            speed = 3;
            hidden = true;
        }};
    }//TODO T6Unit
}
