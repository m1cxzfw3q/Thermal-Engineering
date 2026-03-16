package TEMod.content;

import TEMLib.StarshipUnitType;
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
        }
        @Override
        public void drawOutline(Unit unit) {}

        @Override
        public void applyOutlineColor(Unit unit) {}
        };

        cosmicLevelStarship = new StarshipUnitType("cosmic-level-starship") {{
            description = "test";
            constructor = StarshipUnitEntity::create;
            health = 2000000000;
            armor = 4000;
            speed = 4;
            immunities.addAll(
                    StatusEffects.blasted, burning, StatusEffects.corroded, StatusEffects.disarmed, StatusEffects.freezing,
                    StatusEffects.unmoving, StatusEffects.slow, StatusEffects.wet, StatusEffects.muddy, StatusEffects.melting, StatusEffects.sapped,
                    StatusEffects.electrified, StatusEffects.sporeSlowed, StatusEffects.tarred, StatusEffects.shocked
            );
            abilities.addAll(
                    new ShieldArcAbility() {{
                        radius = 1000;
                        whenShooting = false;
                        max = 50000000;
                        cooldown = 30;
                        regen = 250000;
                        width = 40;
                        angle = 750;
                    }
                        Unit paramUnit;
                        ShieldArcAbility paramField;
                        final Vec2 paramPos = new Vec2();

                        @Override
                        public void update(Unit unit){
                            if(data < max){
                                data += Time.delta * regen;
                            }

                            boolean active = data > 0 && (unit.isShooting || !whenShooting);
                            alpha = Math.max(alpha - Time.delta/10f, 0f);

                            if(active){
                                widthScale = Mathf.lerpDelta(widthScale, 1f, 0.06f);
                                paramUnit = unit;
                                paramField = this;
                                paramPos.set(x, y).rotate(unit.rotation - 90f).add(unit);

                                float reach = radius + width;
                                Groups.bullet.intersect(paramPos.x - reach, paramPos.y - reach, reach * 2f, reach * 2f,
                                        b -> {
                                            if(b.team != paramUnit.team && paramField.data > 0 &&
                                                    !(b.within(paramPos, paramField.radius - paramField.width) && paramPos.within(b.x - b.deltaX, b.y - b.deltaY, paramField.radius - paramField.width)) &&
                                                    (Tmp.v1.set(b).add(b.deltaX, b.deltaY).within(paramPos, paramField.radius + paramField.width) || b.within(paramPos, paramField.radius + paramField.width)) &&
                                                    (Angles.within(paramPos.angleTo(b), paramUnit.rotation + paramField.angleOffset, paramField.angle / 2f) || Angles.within(paramPos.angleTo(b.x + b.deltaX, b.y + b.deltaY), paramUnit.rotation + paramField.angleOffset, paramField.angle / 2f))){

                                                if(paramField.chanceDeflect > 0f && b.vel.len() >= 0.1f && Mathf.chance(paramField.chanceDeflect)){

                                                    //make sound
                                                    paramField.deflectSound.at(paramPos, Mathf.random(0.9f, 1.1f));

                                                    //translate bullet back to where it was upon collision
                                                    b.trns(-b.vel.x, -b.vel.y);

                                                    float penX = Math.abs(paramPos.x - b.x), penY = Math.abs(paramPos.y - b.y);

                                                    if(penX > penY){
                                                        b.vel.x *= -1;
                                                        b.vel.y *= paramField.reflectVel;
                                                    }else{
                                                        b.vel.y *= -1;
                                                        b.vel.x *= paramField.reflectVel;
                                                    }

                                                    b.owner = paramUnit;
                                                    b.team = paramUnit.team;
                                                    b.time = b.lifetime * paramField.reflectTime;
                                                    if(paramField.reflectBuildingDamage > 0f){
                                                        b.buildingDamageMultiplier = paramField.reflectBuildingDamage;
                                                    }

                                                }else{
                                                    b.absorb();
                                                    Fx.absorb.at(b);

                                                    paramField.hitSound.at(b.x, b.y, 1f + Mathf.range(0.1f), paramField.hitSoundVolume);
                                                }

                                                // break shield
                                                if(paramField.data <= b.damage()){
                                                    paramField.data -= paramField.cooldown * paramField.regen;

                                                    Fx.arcShieldBreak.at(paramPos.x, paramPos.y, 0, paramField.color == null ? paramUnit.type.shieldColor(paramUnit) : paramField.color, paramUnit);

                                                    paramField.breakSound.at(paramPos.x, paramPos.y);
                                                }

                                                // shieldDamage for consistency
                                                paramField.data -= b.type.shieldDamage(b);
                                                Reflect.set(paramField, "alpha", 1f);
                                            }
                                        }
                                );
                                Units.nearbyEnemies(paramUnit.team, paramPos.x - reach, paramPos.y - reach, reach * 2f, reach * 2f, unitConsumer);
                            }else{
                                widthScale = Mathf.lerpDelta(widthScale, 0f, 0.11f);
                            }
                        }

                        @Override
                        public void draw(Unit unit){
                            if(widthScale > 0.001f){
                                Draw.z(Layer.shields);

                                Draw.color(color == null ? unit.type.shieldColor(unit) : color, Color.white, Mathf.clamp(alpha));
                                var pos = paramPos.set(x, y).rotate(unit.rotation - 90f).add(unit);

                                if(!Vars.renderer.animateShields){
                                    Draw.alpha(0.4f);
                                }

                                if(region != null){
                                    Vec2 rp = offsetRegion ? pos : Tmp.v1.set(unit);
                                    Draw.yscl = widthScale;
                                    Draw.rect(region, rp.x, rp.y, unit.rotation - 90);
                                    Draw.yscl = 1f;
                                }

                                if(drawArc){
                                    Lines.stroke(width * widthScale);
                                    Lines.arc(pos.x, pos.y, radius, angle / 360f, unit.rotation + angleOffset - angle / 2f);
                                }
                                Draw.reset();
                            }
                        }
                    }
            );
        }};
    }//TODO T6Unit
}
