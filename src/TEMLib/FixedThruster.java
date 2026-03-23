package TEMLib;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.blocks.defense.Thruster;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static arc.graphics.g2d.Draw.color;
import static arc.math.Angles.randLenVectors;

public class FixedThruster extends Thruster {
    /** 火焰的喷射长度 */
    public float flameProjectionLength = 10f;
    /** 火焰的喷射伤害 */
    public float flameProjectionDamage = 125f;
    /** 本体的发光贴图 */
    public TextureRegion glowRegion;
    /** 发光贴图的颜色 */
    public Color glowColor = Color.valueOf("FF9B87");
    /** 物品消耗时长(类似工厂) */
    public float usageTime = 120f;
    /** 火焰喷射的特效(会自动生成，也可以手动替换) */
    public Effect flameProjectionEffect;

    public FixedThruster(String name) {
        super(name);
        update = canOverdrive = drawDisabled = true;
        crushDamageMultiplier = priority = 1;
    }

    @Override
    public void load() {
        super.load();

        glowRegion = Core.atlas.find(name + "-glow");

        if (flameProjectionEffect == null) flameProjectionEffect = new Effect(32f, 80f, e -> {
            color(Pal.lightFlame, Pal.darkFlame, Color.gray, e.fin());

            randLenVectors(e.id, (int) flameProjectionDamage / 6, e.finpow() * flameProjectionLength, e.rotation, 4f, (x, y) -> Fill.circle(e.x + x, e.y + y, size * 2.5f - 0.75f + e.fout() * 1.5f));
        }).followParent(false);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.range, flameProjectionLength / 8, StatUnit.blocks);
        stats.add(Stat.damage, flameProjectionDamage, StatUnit.perSecond);
    }

    public class FixedThrusterBuild extends ThrusterBuild {
        public float progress, time, warmup;
        BulletType flameBulletType = new BulletType(flameProjectionLength / 18, 0) {{
            hitSize = size * 2.5f - 0.75f;
            lifetime = 18f;
            pierce = true;
            collidesAir = false;
            statusDuration = 60f * 10;
            shootEffect = Fx.none;
            hitEffect = Fx.none;
            despawnEffect = Fx.none;
            status = StatusEffects.burning;
            hittable = false;
        }};

        @Override
        public void updateTile() {
            if (efficiency > 0) {
                progress += getProgressIncrease(usageTime);
                warmup = Mathf.approachDelta(warmup, 1, 0.02f);
                time += getProgressIncrease(4);
                if (wasVisible && time >= 1) {
                    flameProjectionEffect.at(this, rotdeg());
                    time = 0;
                }
                flameBulletType.damage = flameProjectionDamage * edelta();
                flameBulletType.create(this, x, y, rotdeg());
            } else {
                warmup = Mathf.approachDelta(warmup, 0, 0.02f);
            }

            if(progress >= 1) {
                consume();
                progress = 0;
            }
        }

        @Override
        public void draw() {
            super.draw();
            if(glowRegion.found()){
                Draw.z(Layer.blockAdditive);
                Draw.blend(Blending.additive);
                Draw.color(Tmp.c1.set(Color.valueOf("00000000")).lerp(glowColor, warmup), warmup * (glowColor.a * (1f - 0.3f + Mathf.absin(7, 0.3f))));
                Draw.rect(glowRegion, x, y, rotdeg());
                Draw.blend();
                Draw.color();
            }
        }
    }
}
