package TEMod.content;

import TEMLib.ModularWeapon.ModularWeapon;
import TEMLib.graphics.GraphicUtils;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Vec2;
import arc.util.Log;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.type.Weapon;

import static arc.graphics.g2d.Draw.alpha;
import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.*;
import static arc.math.Interp.*;
import static mindustry.Vars.tilesize;

public class TEFx {
    public static final Effect //依旧叠石

    shootMini = new Effect(7, e -> {
        color(Pal.lighterOrange, Pal.lightOrange, e.fin());
        float w = 1f + 5 * e.fout();
        Drawf.tri(e.x, e.y, w, 7f * e.fout(), e.rotation);
        Drawf.tri(e.x, e.y, w, 2.4f * e.fout(), e.rotation + 180f);
    }),

    casingMini = new Effect(24f, e -> {
        color(Pal.lightOrange, Color.lightGray, Pal.lightishGray, e.fin());
        alpha(e.fout(0.1f));
        float rot = Math.abs(e.rotation) + 90f;
        int i = -Mathf.sign(e.rotation);

        float len = (2f + e.finpow() * 2f) * i;
        float lr = rot + e.fin() * 10f * i;
        Fill.rect(
                e.x + trnsx(lr, len) + Mathf.randomSeedRange(e.id + i + 7, 3f * e.fin()),
                e.y + trnsy(lr, len) + Mathf.randomSeedRange(e.id + i + 8, 3f * e.fin()),
                1.5f, 0.6f, rot + e.fin() * 50f * i
        );
    }).layer(Layer.bullet),

    instBombColor = new Effect(15f, 100f, e -> {
        color(e.color);
        stroke(e.fout() * 4f);
        Lines.circle(e.x, e.y, 4f + e.finpow() * 20f);

        for(int i = 0; i < 4; i++){
            Drawf.tri(e.x, e.y, 6f, 80f * e.fout(), i*90 + 45);
        }

        color();
        for(int i = 0; i < 4; i++){
            Drawf.tri(e.x, e.y, 3f, 30f * e.fout(), i*90 + 45);
        }

        Drawf.light(e.x, e.y, 150f, e.color, 0.9f * e.fout());
    }),

    instTrailColor = new Effect(30, e -> {
        for(int i = 0; i < 2; i++){
            color(e.color);

            float m = i == 0 ? 1f : 0.5f;

            float rot = e.rotation + 180f;
            float w = 15f * e.fout() * m;
            Drawf.tri(e.x, e.y, w, (30f + Mathf.randomSeedRange(e.id, 15f)) * m, rot);
            Drawf.tri(e.x, e.y, w, 10f * m, rot + 180f);
        }

        Drawf.light(e.x, e.y, 60f, e.color, 0.6f * e.fout());
    }),

    instShootColor = new Effect(24f, e -> {
        e.scaled(10f, b -> {
            color(Color.white, e.color, b.fin());
            stroke(b.fout() * 3f + 0.2f);
            Lines.circle(b.x, b.y, b.fin() * 50f);
        });

        color(e.color);

        for(int i : Mathf.signs){
            Drawf.tri(e.x, e.y, 13f * e.fout(), 85f, e.rotation + 90f * i);
            Drawf.tri(e.x, e.y, 13f * e.fout(), 50f, e.rotation + 20f * i);
        }

        Drawf.light(e.x, e.y, 180f, e.color, 0.9f * e.fout());
    }),

    instHitColor = new Effect(20f, 200f, e -> {
        color(e.color);

        for(int i = 0; i < 2; i++){
            color(e.color);

            float m = i == 0 ? 1f : 0.5f;

            for(int j = 0; j < 5; j++){
                float rot = e.rotation + Mathf.randomSeedRange(e.id + j, 50f);
                float w = 23f * e.fout() * m;
                Drawf.tri(e.x, e.y, w, (80f + Mathf.randomSeedRange(e.id + j, 40f)) * m, rot);
                Drawf.tri(e.x, e.y, w, 20f * m, rot + 180f);
            }
        }

        e.scaled(10f, c -> {
            color(e.color);
            stroke(c.fout() * 2f + 0.2f);
            Lines.circle(e.x, e.y, c.fin() * 30f);
        });

        e.scaled(12f, c -> {
            color(e.color);
            randLenVectors(e.id, 25, 5f + e.fin() * 80f, e.rotation, 60f, (x, y) -> {
                Fill.square(e.x + x, e.y + y, c.fout() * 3f, 45f);
            });
        });
    }),

    railHitColor = new Effect(18f, 200f, e -> {
        color(e.color);

        for(int i : Mathf.signs){
            Drawf.tri(e.x, e.y, 10f * e.fout(), 60f, e.rotation + 140f * i);
        }
    }),

    desRailHitColor = new Effect(80f, 900f, e -> {
        float sizeScl = e.data instanceof Float ? (float)e.data : 1f;

        Rand r = new Rand();
        r.setSeed(e.id);

        float ang = 180f;
        float rscl = 0.7f * sizeScl;
        Draw.color(e.color);
        for(int i = 0; i < 5; i++){
            int count = (int)(10 * rscl);
            for(int j = 0; j < count; j++){
                float fin = Mathf.curve(e.fin(), 0f, 1f - r.random(0.2f));
                float rot = r.range(ang) + e.rotation;
                float off = r.random(22f * rscl) + r.random(50f * Mathf.pow(rscl, 1.5f)) * pow4Out.apply(fin);
                float sscl = r.random(0.7f, 1.2f);

                float wid = 12f * sscl * rscl * (1f - pow4In.apply(fin));
                float hei = 52f * sscl * Mathf.pow(rscl, 1.5f) * pow5Out.apply(fin);

                Vec2 v = Tmp.v1.trns(rot, off).add(e.x, e.y);
                Drawf.tri(v.x, v.y, wid, hei, rot);
                Drawf.tri(v.x, v.y, wid, wid * 2.2f, rot + 180f);
            }

            ang *= 0.6f;
            rscl *= 1.5f;
        }

        ang = 180f;
        rscl = 0.5f * sizeScl;
        Draw.color(e.color, Color.white, e.fin());
        Lines.stroke(3f);
        for(int i = 0; i < 7; i++){
            int count = 12;
            for(int j = 0; j < count; j++){
                float fin = Mathf.curve(e.fin(), 0f, 1f - r.random(0.2f));
                float rot = r.range(ang) + e.rotation;
                float off = r.random(30f * rscl) + r.random(40f * Mathf.pow(rscl, 1.6f)) * pow5Out.apply(fin);

                float len = r.random(20f, 40f) * Mathf.pow(rscl, 1.6f) * sineOut.apply(Mathf.slope(pow5Out.apply(fin)));

                Vec2 v = Tmp.v1.trns(rot, off).add(e.x, e.y);
                Lines.lineAngle(v.x, v.y, rot, len, false);
            }

            ang *= 0.5f;
            rscl *= 1.5f;
        }

        if(sizeScl < 0.75f) return;
        Draw.color(Color.white, 0.666f * e.fout());

        GraphicUtils.drawShockWave(e.x, e.y, -105f, 0f, -e.rotation - 90f, 400f * sizeScl * pow2Out.apply(e.fin()) + 70f, 30f * Mathf.pow(sizeScl, 1f / 1.5f) * pow2Out.apply(e.fin()) + 4f, 16, 0.015f);
    }),

    // TODO 分型(取到描边)
    placeTEMod = new Effect(16, e -> {
        if (e.data instanceof Building) {
            // 引用原版的类
            Fx.placeBlock.at(e.x, e.y, e.rotation, e.color, e.data);
        } else if (e.data instanceof Unit unit) {
            color(Pal.accent);
            stroke(3f - e.fin() * 2f);
            Lines.square(e.x, e.y, tilesize / 2f * e.rotation + e.fin() * 3f);
        }else if (e.data instanceof ModularWeapon modularWeapon) {
            color(Pal.accent);
            stroke(3f - e.fin() * 2f);
            Lines.square(e.x, e.y, tilesize / 2f * e.rotation + e.fin() * 3f);
        } else Log.warn("[TEFx.placeTEMod] [red]What are you doing?");
    }),

    // TODO 分型(取到描边)
    breakTEMod = new Effect(12, e -> {
        if (e.data instanceof Building) {
            // 引用原版的类
            Fx.breakBlock.at(e.x, e.y, e.rotation, e.color, e.data);
        } else if (e.data instanceof Unit unit) {
            color(Pal.remove);
            stroke(3f - e.fin() * 2f);
            Lines.square(e.x, e.y, tilesize / 2f * e.rotation + e.fin() * 3f);

            randLenVectors(e.id, 3 + (int)(e.rotation * 3), e.rotation * 2f + (tilesize * e.rotation) * e.finpow(), (x, y) -> {
                Fill.square(e.x + x, e.y + y, 1f + e.fout() * (3f + e.rotation));
            });
        }else if (e.data instanceof ModularWeapon modularWeapon) {
            color(Pal.remove);
            stroke(3f - e.fin() * 2f);
            Lines.square(e.x, e.y, tilesize / 2f * e.rotation + e.fin() * 3f);

            randLenVectors(e.id, 3 + (int)(e.rotation * 3), e.rotation * 2f + (tilesize * e.rotation) * e.finpow(), (x, y) -> {
                Fill.square(e.x + x, e.y + y, 1f + e.fout() * (3f + e.rotation));
            });
        } else Log.warn("[TEFx.placeTEMod] [red]What are you doing?");
    })
    ;
}
