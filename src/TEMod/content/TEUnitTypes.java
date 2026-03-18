package TEMod.content;

import TEMLib.StarshipUnitType;
import TEMLib.TEHoverPart;
import TEMLib.lib;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.util.Reflect;
import arc.util.Strings;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Units;
import mindustry.entities.abilities.ShieldArcAbility;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.part.HoverPart;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.content.StatusEffects.burning;

/** 开始画大饼了 */
public class TEUnitTypes {
    /** 传奇T6 */
    public static UnitType coupling;//致敬传奇肘击王耦合
    /** baimao投稿的单位 */
    public static UnitType siegeTank, liberator;
    /** 只要写成了我就是全mdt最强开发者写不成就是全mdt最fw开发者 之一的LTX(我正在开发的某独立游戏的代号)星舰单位 */
    public static StarshipUnitType cosmicLevelStarship; //宇宙级星舰(别问为啥叫这个名)

    /** 特种单位 T3 */
    public static UnitType flame;
    //炽焰(陆军)

    /** 特种单位 T4 */
    public static UnitType incinerate;
    //焚世(陆军)

    /** 特种单位 T5 */
    public static UnitType destruction;
    //毁灭(陆军)

    //特种单位没有T6

    /** 特殊单位 */
    public static UnitType steelPipe;

    public static void load() {
        steelPipe = new UnitType("steel-pipe") {{
            health = 1145;
            armor = 30;
            constructor = MechUnit::create;
            hitSize = 17;
            drawCell = false;
            range = 400;
            speed = 3;
            deathSound = Vars.tree.loadSound("steel-pipe-dead-sound");
            deathSoundVolume = 0.6f;
            faceTarget = false;
            outlines = false;
            weapons.add(new Weapon() {{
                reload = 26.5f * 60;
                shoot.firstShotDelay = 120;
                shoot.shotDelay = 20;
                shoot.shots = 600;
                shootCone = 360;
                mirror = false;
                x = y = 0;
                shootSound = Sounds.none;
                bullet = new BulletType(0, 0.25f) {
                    @Override
                    public void createSplashDamage(Bullet b, float x, float y) {
                        if(splashDamageRadius > 0 && !b.absorbed){
                            lib.damage(b.team, x, y, splashDamageRadius, damage, splashDamagePierce, collidesAir, collidesGround, scaledSplashDamage, b);
                        }
                    }
                    {
                        splashDamagePierce = true;
                        splashDamageRadius = 400;
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

                    lib.ammo(ObjectMap.of(u, bullet)).display(t);
                }
            }, new Weapon() {{
                reload = 26.5f * 60;
                shoot.shotDelay = 26.5f * 60;
                shoot.firstShotDelay = 120;
                shootSound = Vars.tree.loadSound("steel-pipe-attack-sound");
                shootCone = 360;
                x = y = 0;
                mirror = false;
                display = false;
                bullet = new BulletType(0, 0) {{
                    lifetime = -1;
                    collides = false;
                    hitEffect = despawnEffect = shootEffect = smokeEffect = Fx.none;
                }};
            }});

//            abilities.addAll(
//                    new MuzzleSwingAbility("-pipe-0") {{
//                        moveTime = 40;
//                        waitTime = 20;
//                    }},
//                    new MuzzleSwingAbility("-pipe-1") {{
//                        moveTime = 40;
//                        waitTime = 20;
//                    }},
//                    new MuzzleSwingAbility("-pipe-2") {{
//                        moveTime = 40;
//                        waitTime = 20;
//                    }},
//                    new MuzzleSwingAbility("-pipe-3") {{
//                        moveTime = 40;
//                        waitTime = 20;
//                    }}
//            );
        }};

        cosmicLevelStarship = new StarshipUnitType("cosmic-level-starship") {{
            description = "test";
            constructor = StarshipUnitEntity::create;
            health = 200000000;
            armor = 4000;
            speed = 4;
            hitSize = 600;
            permissionLevel = 9;
            flying = true;
            accel = 0.08f;
            drag = 0.01f;
            immunities.addAll(Vars.content.statusEffects().select(lib::isDebuff));
            abilities.addAll(
                    new ShieldArcAbility() {{
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
    }//TODO T6Unit
}
