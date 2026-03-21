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

import java.util.Arrays;

public class MuzzleSwingAbility extends Ability {
    public float moveTime = 10f;
    public float waitTime = 15f;
    public float swingTime = 30f;
    public float swingAngle = 15f;
    public float x, y;
    public TextureRegion region;
    public String suffix;

    public Sound sound = Sounds.none;
    public float soundVolume = 1f;
    public float soundPitch = 1f;     // 保留以备后续扩展

    private static class State {
        int phase = -1;
        float timer = 0f;
        float progress = 0f;
        boolean[] lastShoot;           // 每个武器的上一次射击状态
        SoundLoop soundLoop;
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

        int weaponCount = unit.mounts().length;
        if (state.lastShoot == null || state.lastShoot.length != weaponCount) {
            state.lastShoot = new boolean[weaponCount];
            Arrays.fill(state.lastShoot, false);
        }

        // 检测任一武器是否刚开火（shoot 从 false 变为 true）
        boolean justFired = false;
        for (int i = 0; i < weaponCount; i++) {
            WeaponMount mount = unit.mounts()[i];
            boolean currentShoot = mount.shoot;
            boolean lastShoot = state.lastShoot[i];
            if (!lastShoot && currentShoot) {
                justFired = true;
            }
            state.lastShoot[i] = currentShoot;
        }

        if (justFired) {
            if (state.soundLoop != null) {
                state.soundLoop.stop();
                state.soundLoop = null;
            }
            state.phase = 0;
            state.timer = 0f;
            state.progress = 0f;
        }

        if (state.phase < 0) {
            if (state.soundLoop != null) {
                state.soundLoop.stop();
                state.soundLoop = null;
            }
            return;
        }

        int oldPhase = state.phase;

        state.timer += Time.delta;
        float phaseDur = getPhaseDuration(state.phase);
        if (phaseDur > 0) {
            state.progress = Math.min(state.timer / phaseDur, 1f);
        }

        if (state.timer >= phaseDur) {
            state.phase++;
            state.timer = 0f;
            state.progress = 0f;
            if (state.phase > 2) state.phase = -1;
        }

        if (oldPhase == 2 && state.phase != 2) {
            if (state.soundLoop != null) {
                state.soundLoop.stop();
                state.soundLoop = null;
            }
        } else if (state.phase == 2 && oldPhase != 2 && sound != Sounds.none) {
            if (state.soundLoop == null) {
                state.soundLoop = new SoundLoop(sound, soundVolume);
            }
        }

        if (state.phase == 2 && state.soundLoop != null) {
            state.soundLoop.update(unit.x, unit.y, true);
        } else if (state.soundLoop != null) {
            state.soundLoop.update(unit.x, unit.y, false);
        }

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
            offsetX = Mathf.lerp(0, x, state.progress);
            offsetY = Mathf.lerp(0, y, state.progress);
        } else if (state.phase == 1) {
            offsetX = x;
            offsetY = y;
        } else if (state.phase == 2) {
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