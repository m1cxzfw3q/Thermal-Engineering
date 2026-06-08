package TEMLib.entities.effects;

import arc.Core;
import arc.math.Mathf;
import mindustry.entities.Effect;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;

import static arc.graphics.g2d.Draw.*;
import static arc.math.Angles.trnsx;
import static arc.math.Angles.trnsy;

public class CasingEffect extends Effect {
    public float
            addlen = 2f, mullen = 10f,
            w = 2f, h = 3f;
    public boolean doubled = false;

    public CasingEffect() {
        layer(Layer.bullet);
        lifetime = 34f;
    }

    public CasingEffect(float life, float addlen, float mullen) {
        this();
        lifetime = life;
        this.addlen = addlen;
        this.mullen = mullen;
    }

    public CasingEffect(float life, float addlen, float mullen, float w, float h) {
        this();
        lifetime = life;
        this.addlen = addlen;
        this.mullen = mullen;
        this.w = w;
        this.h = h;
    }

    public CasingEffect(float life, float addlen, float mullen, float w, float h, boolean doubled) {
        this();
        lifetime = life;
        this.addlen = addlen;
        this.mullen = mullen;
        this.w = w;
        this.h = h;
        this.doubled = doubled;
    }

    public CasingEffect(float life, float addlen, float mullen, boolean doubled) {
        this();
        lifetime = life;
        this.addlen = addlen;
        this.mullen = mullen;
        this.doubled = doubled;
    }

    @Override
    public void render(EffectContainer e) {
        color(Pal.lightOrange, Pal.lightishGray, Pal.lightishGray, e.fin());
        alpha(e.fout(0.5f));
        float rot = Math.abs(e.rotation) + 90f;
        if (!doubled) {
            int i = -Mathf.sign(e.rotation);
            float len = (addlen + e.finpow() * mullen) * i;
            float lr = rot + Mathf.randomSeedRange(e.id + i + 6, 20f * e.fin()) * i;

            rect(Core.atlas.find("casing"),
                    e.x + trnsx(lr, len) + Mathf.randomSeedRange(e.id + i + 7, 3f * e.fin()),
                    e.y + trnsy(lr, len) + Mathf.randomSeedRange(e.id + i + 8, 3f * e.fin()),
                    w, h, rot + e.fin() * 50f * i
            );
        } else {
            for(int i : Mathf.signs){
                float len = (addlen + e.finpow() * mullen) * i;
                float lr = rot + Mathf.randomSeedRange(e.id + i + 6, 20f * e.fin()) * i;
                rect(Core.atlas.find("casing"),
                        e.x + trnsx(lr, len) + Mathf.randomSeedRange(e.id + i + 7, 3f * e.fin()),
                        e.y + trnsy(lr, len) + Mathf.randomSeedRange(e.id + i + 8, 3f * e.fin()),
                        w, h, rot + e.fin() * 50f * i
                );
            }
        }
    }
}
