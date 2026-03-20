package TEMLib;

import TEMLib.ModularWeapon.ModularWeaponEntity;
import TEMLib.ModularWeapon.ModularWeaponType;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pools;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.abilities.Ability;
import mindustry.entities.units.StatusEntry;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.input.InputHandler;
import mindustry.type.Item;
import mindustry.type.UnitType;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

public class StarshipUnitType extends UnitType implements ModularWeaponType, PermissionLeverUnit {
    public Seq<WeaponPoint> modularWeaponsPoint = new Seq<>();
    public int permissionLevel = 0;
    public int starshipTier = 0;

    public StarshipUnitType(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(TEStat.permissionLevel, permissionLevel);
    }

    @Override
    public WeaponPoint[] modularWeaponsPoint() {
        return modularWeaponsPoint.toArray();
    }

    @Override
    public int getPermissionLevel() {
        return permissionLevel;
    }

    @Override
    public boolean hasPermissionLevel(int level) {
        return level <= permissionLevel;
    }

    public static class StarshipUnitEntity extends UnitEntity implements ModularWeaponEntity {
        @Override
        public void update() {
            if (!Vars.net.client() || isLocal()) {
                float px = x;
                float py = y;
                move(vel.x * Time.delta, vel.y * Time.delta);
                if (Mathf.equal(px, x)) {
                    vel.x = 0;
                }

                if (Mathf.equal(py, y)) {
                    vel.y = 0;
                }

                vel.scl(Math.max(1 - drag * Time.delta, 0));
            }

            updateBuildLogic();
            hitTime -= Time.delta / 9;
            stack.amount = Mathf.clamp(stack.amount, 0, itemCapacity());
            itemTime = Mathf.lerpDelta(itemTime, (float)Mathf.num(hasItem()), 0.05f);
            if (mineTile != null) {
                Building core = closestCore();
                Item item = getMineResult(mineTile);
                if (core != null && item != null && !acceptsItem(item) && within(core, 220) && !offloadImmediately()) {
                    int accepted = core.acceptStack(item(), stack().amount, this);
                    if (accepted > 0) {
                        Call.transferItemTo(this, item(), accepted, mineTile.worldx() + Mathf.range(4), mineTile.worldy() + Mathf.range(4), core);
                        clearItem();
                    }
                }

                if ((!Vars.net.client() || isLocal()) && !validMine(mineTile)) {
                    mineTile = null;
                    mineTimer = 0;
                } else if (mining() && item != null) {
                    mineTimer += Time.delta * type.mineSpeed * Vars.state.rules.unitMineSpeed(team());
                    if (Mathf.chance(0.06 * (double)Time.delta)) {
                        Fx.pulverizeSmall.at(mineTile.worldx() + Mathf.range(4), mineTile.worldy() + Mathf.range(4), 0, item.color);
                    }

                    if (mineTimer >= 50 + (type.mineHardnessScaling ? (float)item.hardness * 15 : 15)) {
                        mineTimer = 0;
                        if (Vars.state.rules.sector != null && team() == Vars.state.rules.defaultTeam) {
                            Vars.state.rules.sector.info.handleProduction(item, 1);
                        }

                        if (core != null && within(core, 220) && core.acceptStack(item, 1, this) == 1 && offloadImmediately()) {
                            if (item() == item && !Vars.net.client()) {
                                addItem(item);
                            }

                            Call.transferItemTo(this, item, 1, mineTile.worldx() + Mathf.range(4), mineTile.worldy() + Mathf.range(4), core);
                        } else if (acceptsItem(item)) {
                            InputHandler.transferItemToUnit(item, mineTile.worldx() + Mathf.range(4), mineTile.worldy() + Mathf.range(4), this);
                        } else {
                            mineTile = null;
                            mineTimer = 0;
                        }
                    }

                    if (!Vars.headless) {
                        Vars.control.sound.loop(type.mineSound, this, type.mineSoundVolume);
                    }
                }
            }

            shieldAlpha -= Time.delta / 15;
            if (shieldAlpha < 0) {
                shieldAlpha = 0;
            }

            Floor floor = floorOn();
            if (isGrounded() && !type.hovering) {
                apply(floor.status, floor.statusDuration);
            }

            applied.clear();
            armorOverride = -1;
            speedMultiplier = damageMultiplier = healthMultiplier = reloadMultiplier = buildSpeedMultiplier = dragMultiplier = 1;
            disarmed = false;
            if (!statuses.isEmpty()) {
                int index = 0;

                while(index < statuses.size) {
                    StatusEntry entry = statuses.get(index++);
                    entry.time = Math.max(entry.time - Time.delta, 0);
                    if (entry.effect == null || entry.time <= 0 && !entry.effect.permanent) {
                        if (entry.effect != null) {
                            entry.effect.onRemoved(this);
                        }

                        Pools.free(entry);
                        --index;
                        statuses.remove(index);
                    } else {
                        applied.set(entry.effect.id);
                        if (entry.effect.dynamic) {
                            speedMultiplier *= entry.speedMultiplier;
                            healthMultiplier *= entry.healthMultiplier;
                            damageMultiplier *= entry.damageMultiplier;
                            reloadMultiplier *= entry.reloadMultiplier;
                            buildSpeedMultiplier *= entry.buildSpeedMultiplier;
                            dragMultiplier *= entry.dragMultiplier;
                            if (entry.armorOverride >= 0) {
                                armorOverride = entry.armorOverride;
                            }
                        } else {
                            speedMultiplier *= entry.effect.speedMultiplier;
                            healthMultiplier *= entry.effect.healthMultiplier;
                            damageMultiplier *= entry.effect.damageMultiplier;
                            reloadMultiplier *= entry.effect.reloadMultiplier;
                            buildSpeedMultiplier *= entry.effect.buildSpeedMultiplier;
                            dragMultiplier *= entry.effect.dragMultiplier;
                        }

                        disarmed |= entry.effect.disarm;
                        entry.effect.update(this, entry);
                    }
                }
            }

            if (Vars.net.client() && !isLocal() || isRemote()) {
                interpolate();
            }

            type.update(this);
            if (type.bounded) {
                float bot = 0;
                float left = 0;
                float top = (float)Vars.world.unitHeight();
                float right = (float)Vars.world.unitWidth();
                if (Vars.state.rules.limitMapArea && !team.isAI()) {
                    bot = (float)(Vars.state.rules.limitY * 8);
                    left = (float)(Vars.state.rules.limitX * 8);
                    top = (float)(Vars.state.rules.limitHeight * 8) + bot;
                    right = (float)(Vars.state.rules.limitWidth * 8) + left;
                }

                if (!Vars.net.client() || isLocal()) {
                    float dx = 0;
                    float dy = 0;
                    if (x < left) {
                        dx += -(x - left) / 8;
                    }

                    if (y < bot) {
                        dy += -(y - bot) / 8;
                    }

                    if (x > right - 8) {
                        dx -= (x - (right - 8)) / 8;
                    }

                    if (y > top - 8) {
                        dy -= (y - (top - 8)) / 8;
                    }

                    velAddNet(dx * Time.delta, dy * Time.delta);
                    float margin = 8.0F;
                    x = Mathf.clamp(x, left - margin, right - 8 + margin);
                    y = Mathf.clamp(y, bot - margin, top - 8 + margin);
                }

                if (isGrounded()) {
                    x = Mathf.clamp(x, left, right - 8);
                    y = Mathf.clamp(y, bot, top - 8);
                }

                if (x < -250 + left || y < -250 + bot || x >= right + 250 || y >= top + 250) {
                    kill();
                }
            }

            floor = floorOn();
            Tile tile = tileOn();
            if (isFlying() != wasFlying) {
                if (wasFlying && tile != null) {
                    Fx.unitLand.at(x, y, floor.isLiquid ? 1 : 0.5f, tile.getFloorColor());
                }

                wasFlying = isFlying();
            }

            if (!type.hovering && isGrounded() && type.emitWalkEffect && (splashTimer += Mathf.dst(deltaX(), deltaY())) >= 7 + hitSize() / 8) {
                floor.walkEffect.at(x, y, hitSize() / 8, tile != null ? tile.getFloorColor() : floor.mapColor);
                splashTimer = 0;
                if (type.emitWalkSound) {
                    floor.walkSound.at(x, y, Mathf.random(floor.walkSoundPitchMin, floor.walkSoundPitchMax), floor.walkSoundVolume);
                }
            }

            updateDrowning();
            if (wasHealed && healTime <= -1) {
                healTime = 1;
            }

            healTime -= Time.delta / 20;
            wasHealed = false;
            if (team.isOnlyAI() && Vars.state.isCampaign() && Vars.state.getSector().isCaptured()) {
                kill();
            }

            if (!Vars.headless) {
                Vars.control.sound.loop(type.loopSound, this, type.loopSoundVolume);
                if (type.moveSound != Sounds.none) {
                    float progress = Mathf.clamp(vel.len() / type.speed);
                    float pitch = Mathf.lerp(type.moveSoundPitchMin, type.moveSoundPitchMax, progress);
                    Vars.control.sound.loop(type.moveSound, this, type.moveSoundVolume * progress, pitch);
                }
            }

            if (!type.supportsEnv(Vars.state.rules.env) && !dead) {
                Call.unitEnvDeath(this);
                team.data().updateCount(type, -1);
            }

            if (Vars.state.rules.unitAmmo && ammo < (float)type.ammoCapacity - 1.0E-4F) {
                resupplyTime += Time.delta;
                if (resupplyTime > 10) {
                    type.ammoType.resupply(this);
                    resupplyTime = 0;
                }
            }

            for(Ability a : abilities) {
                a.update(this);
            }

            if (trail != null) {
                trail.length = type.trailLength;
                float scale = type.useEngineElevation ? elevation : 1;
                float offset = type.engineOffset / 2 + type.engineOffset / 2 * scale;
                float cx = x + Angles.trnsx(rotation + 180, offset);
                float cy = y + Angles.trnsy(rotation + 180, offset);
                trail.update(cx, cy);
            }

            drag = type.drag * (isGrounded() ? floorOn().dragMultiplier : 1) * dragMultiplier * Vars.state.rules.dragMultiplier;
            if (team != Vars.state.rules.waveTeam && Vars.state.hasSpawns() && (!Vars.net.client() || isLocal()) && hittable()) {
                float relativeSize = Vars.state.rules.dropZoneRadius + hitSize / 2 + 1;

                for(Tile spawn : Vars.spawner.getSpawns()) {
                    if (within(spawn.worldx(), spawn.worldy(), relativeSize)) {
                        velAddNet(Tmp.v1.set(this).sub(spawn.worldx(), spawn.worldy()).setLength(1.1F - dst(spawn) / relativeSize).scl(0.45F * Time.delta));
                    }
                }
            }

            if (dead || health <= 0) {
                drag = 0.01f;
                if (Mathf.chanceDelta(0.1)) {
                    Tmp.v1.rnd(Mathf.range(hitSize));
                    type.fallEffect.at(x + Tmp.v1.x, y + Tmp.v1.y);
                }

                if (Mathf.chanceDelta(0.2)) {
                    float offset = type.engineOffset / 2 + type.engineOffset / 2 * elevation;
                    float range = Mathf.range(type.engineSize);
                    type.fallEngineEffect.at(x + Angles.trnsx(rotation + 180, offset) + Mathf.range(range), y + Angles.trnsy(rotation + 180, offset) + Mathf.range(range), Mathf.random());
                }

                elevation -= type.fallSpeed * Time.delta;
                if (isGrounded() || health <= -maxHealth * type.wreckHealthMultiplier) {
                    Call.unitDestroy(id);
                }
            }

            if (tile != null && tile.build != null) {
                tile.build.unitOnAny(this);
            }

            if (tile != null && isGrounded() && !type.hovering) {
                if (tile.build != null) {
                    tile.build.unitOn(this);
                }

                if (floor.damageTaken > 0) {
                    damageContinuous(floor.damageTaken);
                }
            }

            if (tile != null && !canPassOn()) {
                if (type.canBoost) {
                    elevation = 1;
                } else if (!Vars.net.client()) {
                    kill();
                }
            }

            if (!Vars.net.client() && !dead && shouldUpdateController()) {
                controller.updateUnit();
            }

            if (!controller.isValidController()) {
                resetController();
            }

            if (spawnedByCore && !isPlayer() && !dead) {
                Call.unitDespawn(this);
            }

            for(WeaponMount mount : this.mounts) {
                mount.weapon.update(this, mount);
            }
        }

        @Override
        public void hitbox(Rect rect) {
            super.hitbox(rect);
        }
    }
}
