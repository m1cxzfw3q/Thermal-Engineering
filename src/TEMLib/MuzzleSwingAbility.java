package TEMLib;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.audio.SoundLoop;
import mindustry.entities.abilities.Ability;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.UnitType;

public class MuzzleSwingAbility extends Ability {
    public float moveTime = 10f;      // 移动到目标位置的时间（帧）
    public float waitTime = 15f;      // 到达后等待时间
    public float swingTime = 30f;     // 摇摆周期
    public float swingAngle = 15f;    // 摇摆幅度
    public float x, y;                // 目标相对坐标（相对于单位中心）
    public TextureRegion region;
    public String suffix;

    // 循环音效相关
    public Sound sound = Sounds.none; // 摇摆阶段播放的循环音效
    public float soundVolume = 1f;

    private static class State {
        int phase = -1;          // -1=无, 0=移动, 1=等待, 2=摇摆
        float timer = 0f;
        float progress = 0f;
        boolean wasShooting = false;
        SoundLoop soundLoop;      // 当前播放的循环音效
    }

    public MuzzleSwingAbility(String suffix) {
        display = false;
        this.suffix = suffix;
    }

    private final ObjectMap<Integer, State> states = new ObjectMap<>();

    @Override
    public void update(Unit unit) {
        State state = states.get(unit.id);
        if (state == null) {
            state = new State();
            states.put(unit.id, state);
        }

        // 检测开火瞬间（任意武器开火）
        boolean anyShootNow = false;
        for (WeaponMount mount : unit.mounts()) {
            if (mount.shoot) {
                anyShootNow = true;
                break;
            }
        }
        boolean justFired = anyShootNow && !state.wasShooting;
        state.wasShooting = anyShootNow;

        if (justFired) {
            // 重置特效，并停止可能正在播放的音效
            if (state.soundLoop != null) {
                state.soundLoop.stop();
                state.soundLoop = null;
            }
            state.phase = 0;
            state.timer = 0f;
            state.progress = 0f;
        }

        if (state.phase < 0) {
            // 无特效时，确保音效已停止
            if (state.soundLoop != null) {
                state.soundLoop.stop();
                state.soundLoop = null;
            }
            return;
        }

        // 记录上一阶段，用于检测变化
        int oldPhase = state.phase;

        state.timer += Time.delta;
        float phaseDur = getPhaseDuration(state.phase);
        if (phaseDur > 0) {
            state.progress = Math.min(state.timer / phaseDur, 1f);
        }

        // 阶段切换
        if (state.timer >= phaseDur) {
            state.phase++;
            state.timer = 0f;
            state.progress = 0f;
            if (state.phase > 2) {
                state.phase = -1;
            }
        }

        // 处理音效启动/停止
        if (oldPhase == 2 && state.phase != 2) {
            // 离开摇摆阶段，停止音效
            if (state.soundLoop != null) {
                state.soundLoop.stop();
                state.soundLoop = null;
            }
        } else if (state.phase == 2 && oldPhase != 2 && sound != Sounds.none) {
            // 进入摇摆阶段，开始循环音效
            if (state.soundLoop == null) {
                state.soundLoop = new SoundLoop(sound, soundVolume);
            }
        }

        // 如果处于摇摆阶段且音效存在，更新音效（播放）
        if (state.phase == 2 && state.soundLoop != null) {
            state.soundLoop.update(unit.x, unit.y, true);
        } else if (state.soundLoop != null) {
            // 不在摇摆阶段但音效还在（例如刚离开），更新音效并让其淡出
            state.soundLoop.update(unit.x, unit.y, false);
        }

        // 如果单位死亡，清理音效
        if (unit.dead() && state.soundLoop != null) {
            state.soundLoop.stop();
            state.soundLoop = null;
        }
    }

    @Override
    public void draw(Unit unit) {
        State state = states.get(unit.id);
        if (state == null || state.phase < 0 || region == null) return;

        float offsetX = 0, offsetY = 0;
        float drawRotation = unit.rotation;

        if (state.phase == 0) {
            // 移动阶段：从 (0,0) 线性插值到 (x,y)
            offsetX = Mathf.lerp(0, x, state.progress);
            offsetY = Mathf.lerp(0, y, state.progress);
        } else if (state.phase == 1) {
            // 等待阶段：停留在目标位置
            offsetX = x;
            offsetY = y;
        } else if (state.phase == 2) {
            // 摇摆阶段：围绕目标点旋转
            offsetX = x;
            offsetY = y;
            drawRotation += Mathf.sin(state.progress * 360f) * swingAngle;
        }

        float worldX = unit.x + Angles.trnsx(unit.rotation, offsetX, offsetY);
        float worldY = unit.y + Angles.trnsy(unit.rotation, offsetX, offsetY);

        Draw.rect(region, worldX, worldY, drawRotation);
    }

    private float getPhaseDuration(int phase) {
        return switch (phase) {
            case 0 -> moveTime;
            case 1 -> waitTime;
            case 2 -> swingTime;
            default -> 0;
        };
    }

    @Override
    public void init(UnitType type) {
        region = Core.atlas.find(type.name + suffix);
    }
}