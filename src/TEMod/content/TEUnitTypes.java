package TEMod.content;

import TEMLib.StarshipUnitType;
import TEMLib.lib;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.util.Strings;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.MechUnit;
import mindustry.gen.Sounds;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.content.StatusEffects.unmoving;

/** 开始画大饼了 */
public class TEUnitTypes {
    /** 传奇T6 */
    public static UnitType coupling;//致敬传奇肘击王耦合
    /** baimao投稿的单位 */
    public static UnitType siegeTank, liberator;
    /** 只要写成了我就是全mdt最强开发者写不成就是全mdt最fw开发者 之一的LTX(我正在开发的某独立游戏的代号)星舰单位 */
    public static StarshipUnitType cosmicClassStarship; //宇宙级星舰(别问为啥叫这个名)

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
            weapons.add(new Weapon() {{
                reload = 20;
                shoot.firstShotDelay = 60;
                shoot.shotDelay = 20;
                shoot.shots = (26 * 60) / 20;
                minWarmup = 0.9f;
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
                        shootEffect = Fx.none;
                        splashDamagePierce = true;
                        splashDamageRadius = 400;
                        lifetime = -1;
                        collides = false;
                        despawnHit = true;
                        hitEffect = despawnEffect = Fx.none;
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
                reload = 26f * 60;
                shoot.shotDelay = 26f * 60;
                shootSound = Vars.tree.loadSound("steel-pipe-attack-sound");
                shootCone = 360;
                x = y = 0;
                mirror = false;
                display = false;
                bullet = new BulletType(0, 0) {{
                    shootEffect = Fx.none;
                    lifetime = -1;
                    collides = false;
                    hitEffect = despawnEffect = Fx.none;
                }};
            }});
        }};
    }//TODO T6Unit
}
