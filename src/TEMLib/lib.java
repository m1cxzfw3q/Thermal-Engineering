package TEMLib;

import arc.Core;
import arc.func.Cons;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Collapser;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.util.Nullable;
import arc.util.Reflect;
import arc.util.Scaling;
import arc.util.Strings;
import mindustry.content.StatusEffects;
import mindustry.core.World;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.EmpBulletType;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.type.UnitType;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValue;
import mindustry.world.meta.StatValues;
import mindustry.world.modules.LiquidModule;

import java.lang.reflect.Method;
import java.util.Objects;

import static mindustry.Vars.*;
import static mindustry.entities.Damage.tileDamage;
import static mindustry.world.meta.StatValues.fixValue;
import static mindustry.world.meta.StatValues.withTooltip;

public class lib {//没什么用的lib
    public static void noop() {}

    public static ItemStack[] sizeWith(ItemStack[] stacks, Block source) {
        return ItemStack.mult(stacks, (float) Math.pow(source.size, 2));
    }

    public static Table itemsDisplay(ItemStack[] stacks) {
        Table table = new Table();
        if (stacks == null) return table;
        for (ItemStack it : stacks) {
            table.table(t -> t.add(StatValues.displayItem(it.item, it.amount, true)));
        }
        return table;
    }

    public static Table itemsDisplay(ItemStack[] stacks, float craftTime) {
        Table table = new Table();
        if (stacks == null) return table;
        for (ItemStack it : stacks) {
            table.table(t -> t.add(StatValues.displayItem(it.item, it.amount, craftTime, true)));
        }
        return table;
    }

    public static Table liquidsDisplay(LiquidStack[] stacks) {
        Table table = new Table();
        if (stacks == null) return table;
        for (LiquidStack it : stacks) {
            table.table(t -> t.add(StatValues.displayLiquid(it.liquid, it.amount, true)));
        }
        return table;
    }

    public static boolean hasLiquid(LiquidModule mod, LiquidStack[] stack) {
        if (stack.length == 0) return true;
        boolean b = false;
        for (var liquid : stack) {
            b = b || mod.get(liquid.liquid) > 0;
        }
        return b;
    }

    private static final Rect rect = new Rect();

    /** Damages all entities and blocks in a radius that are enemies of the team. */
    public static void damage(Team team, float x, float y, float radius, float damage, boolean complete, boolean air, boolean ground, boolean scaled, @Nullable Bullet source){
        Cons<Unit> cons = unit -> {
            if(unit.team == team  || !unit.checkTarget(air, ground) || !unit.within(x, y, radius + (scaled ? unit.hitSize / 2f : 0f))){
                return;
            }
            unit.damage(0);
            unit.health(unit.health - unit.maxHealth * damage);
            if (mods.getMod("flameout") != null) {
                if (Objects.equals(unit.type.name, "flameout-despondency")) {
                    try {
                        Reflect.set(unit, "trueHealth", Reflect.<Float>get(unit, "trueHealth") - Reflect.<Float>get(unit, "trueMaxHealth") * damage);
                    } catch (RuntimeException ignored) {}
                }
            }
            if (unit.health <= 0) {
                unit.dead = true;
                removeUnit(unit);
            }
        };

        rect.setSize(radius * 2).setCenter(x, y);
        if(team != null){
            Units.nearbyEnemies(team, rect, cons);
        }else{
            Units.nearby(rect, cons);
        }

        if(ground){
            if(!complete){
                tileDamage(team, World.toTile(x), World.toTile(y), radius / tilesize, damage * (source == null ? 1f : source.type.buildingDamageMultiplier), source);
            }else{
                completeDamage(team, x, y, radius, damage * (source == null ? 1f : source.type.buildingDamageMultiplier));
            }
        }
    }

    private static void completeDamage(Team team, float x, float y, float radius, float damage){
        int trad = (int)(radius / tilesize);
        for(int dx = -trad; dx <= trad; dx++){
            for(int dy = -trad; dy <= trad; dy++){
                Tile tile = world.tile(Math.round(x / tilesize) + dx, Math.round(y / tilesize) + dy);
                if(tile != null && tile.build != null && (team == null || team != tile.team()) && dx*dx + dy*dy <= trad*trad){
                    tile.build.damage(0);
                    tile.build.health(tile.build.health - tile.build.maxHealth * damage);
                    if (tile.build.health <= 0 && tile.build != null) tile.build.kill();
                }
            }
        }
    }

    public static <T extends UnlockableContent> StatValue ammo(ObjectMap<T, BulletType> map){
        return ammo(map, false, false);
    }

    public static <T extends UnlockableContent> StatValue ammo(ObjectMap<T, BulletType> map, boolean nested, boolean showUnit){
        return table -> {

            table.row();

            var orderedKeys = map.keys().toSeq();
            orderedKeys.sort();

            for(T t : orderedKeys){
                boolean compact = t instanceof UnitType && !showUnit || nested;

                BulletType type = map.get(t);

                if(type.spawnUnit != null && type.spawnUnit.weapons.size > 0){
                    ammo(ObjectMap.of(t, type.spawnUnit.weapons.first().bullet), nested, false).display(table);
                    continue;
                }

                table.table(Styles.grayPanel, bt -> {
                    bt.left().top().defaults().padRight(3).left();
                    //no point in displaying unit icon twice
                    if(!compact && !(t instanceof Turret)){
                        bt.table(title -> {
                            title.image(icon(t)).size(3 * 8).padRight(4).right().scaling(Scaling.fit).top().with(i -> withTooltip(i, t, false));

                            title.add(t.localizedName).padRight(10).left().top();

                            if(type.displayAmmoMultiplier && type.statLiquidConsumed > 0f){
                                title.add("[stat]" + fixValue(type.statLiquidConsumed / type.ammoMultiplier * 60f) + " [lightgray]" + StatUnit.perSecond.localized());
                            }
                        });
                        bt.row();
                    }

                    if(type.damage > 0 && (type.collides || type.splashDamage <= 0)){
                        bt.add("[stat]%" + Core.bundle.format("bullet.damage", type.damage * 100) + (type.continuousDamage() > 0 ?
                                "[lightgray] ~ [stat]" + Core.bundle.format("bullet.damage", type.continuousDamage()) + StatUnit.perSecond.localized() : ""));
                    }

                    if(type.buildingDamageMultiplier != 1){
                        sep(bt, Core.bundle.format("bullet.buildingdamage", ammoStat((int)(type.buildingDamageMultiplier * 100 - 100))));
                    }

                    if(type.rangeChange != 0 && !compact){
                        sep(bt, Core.bundle.format("bullet.range", ammoStat(type.rangeChange / tilesize)));
                    }

                    if(type.shieldDamageMultiplier != 1){
                        sep(bt, Core.bundle.format("bullet.shielddamage", ammoStat((int)(type.shieldDamageMultiplier * 100 - 100))));
                    }

                    if(type.splashDamage > 0){
                        sep(bt, Core.bundle.format("bullet.splashdamage", (int)type.splashDamage, Strings.fixed(type.splashDamageRadius / tilesize, 1)));
                    }

                    if(type.statLiquidConsumed <= 0f && !compact && !Mathf.equal(type.ammoMultiplier, 1f) && type.displayAmmoMultiplier && (!(t instanceof Turret turret) || turret.displayAmmoMultiplier)){
                        sep(bt, Core.bundle.format("bullet.multiplier", (int)type.ammoMultiplier));
                    }

                    if(!compact && !Mathf.equal(type.reloadMultiplier, 1f)){
                        int val = (int)(type.reloadMultiplier * 100 - 100);
                        sep(bt, Core.bundle.format("bullet.reload", ammoStat(val)));
                    }

                    if(type.knockback > 0){
                        sep(bt, Core.bundle.format("bullet.knockback", Strings.autoFixed(type.knockback, 2)));
                    }

                    if(type.healPercent > 0f){
                        sep(bt, Core.bundle.format("bullet.healpercent", Strings.autoFixed(type.healPercent, 2)));
                    }

                    if(type.healAmount > 0f){
                        sep(bt, Core.bundle.format("bullet.healamount", Strings.autoFixed(type.healAmount, 2)));
                    }

                    if(type.pierce || type.pierceCap != -1){
                        sep(bt, type.pierceCap == -1 ? "@bullet.infinitepierce" : Core.bundle.format("bullet.pierce", type.pierceCap));
                    }

                    if(type.incendAmount > 0){
                        sep(bt, "@bullet.incendiary");
                    }

                    if(type.homingPower > 0.01f){
                        sep(bt, "@bullet.homing");
                    }

                    if(type.lightning > 0){
                        sep(bt, Core.bundle.format("bullet.lightning", type.lightning, type.lightningDamage < 0 ? type.damage : type.lightningDamage));
                    }

                    if(type instanceof LaserBulletType b && b.lightningSpacing > 0){
                        int count = (int)(b.length / b.lightningSpacing) * 2 + 2;
                        float damage = b.lightningDamage < 0 ? b.damage : b.lightningDamage;
                        sep(bt, Core.bundle.format("bullet.lightning", count, damage));
                        note(bt, Core.bundle.format("bullet.lightninginterval", Strings.autoFixed(b.lightningSpacing / tilesize, 2), Strings.autoFixed(b.lightningLength, 2))).left();
                    }

                    if(type instanceof EmpBulletType b && b.radius > 0f){
                        sep(bt, Core.bundle.format("bullet.empradius", Strings.fixed(b.radius / tilesize, 1)));
                        if(b.timeDuration > 0f && b.timeIncrease > 1f){
                            sep(bt, Core.bundle.format("bullet.empboost", Strings.autoFixed(b.timeIncrease * 100f, 2),
                                    Strings.autoFixed(b.timeDuration / 60f, 1)) + " " + StatUnit.seconds.localized());
                        }
                        if(b.timeDuration > 0f && b.powerSclDecrease < 1f){
                            sep(bt, Core.bundle.format("bullet.empslowdown",
                                    (b.powerSclDecrease < 1f ? "[negstat]" : "") + Strings.autoFixed((b.powerSclDecrease - 1f) * 100f, 2),
                                    Strings.autoFixed(b.timeDuration / 60f, 1)) + " " + StatUnit.seconds.localized());
                        }
                        if(!Mathf.equal(b.powerDamageScl, 1f)){
                            sep(bt, Core.bundle.format("bullet.empdamage", Strings.autoFixed(b.powerDamageScl * 100f, 2)));
                        }
                        if(b.hitUnits){
                            sep(bt, Core.bundle.format("bullet.empunitdamage",
                                    (b.unitDamageScl < 1f ? "[negstat]" : "") + Strings.autoFixed(b.unitDamageScl * 100f, 2)));
                        }
                    }

                    if(type.pierceArmor){
                        sep(bt, "@bullet.armorpierce");
                    }

                    if(type.armorMultiplier != 1f){
                        if(type.armorMultiplier > 1f){
                            sep(bt, Core.bundle.format("bullet.armorweakness", (int)(type.armorMultiplier * 100)));
                        }else if(Mathf.sign(type.armorMultiplier) == 1){
                            sep(bt, Core.bundle.format("bullet.armorpiercing", (int)((1 - type.armorMultiplier) * 100)));
                        }else{
                            sep(bt, Core.bundle.format("bullet.antiarmor", (-type.armorMultiplier)));
                        }
                    }

                    if(type.maxDamageFraction > 0){
                        sep(bt, Core.bundle.format("bullet.maxdamagefraction", (int)(type.maxDamageFraction * 100)));
                    }

                    if(type.suppressionRange > 0){
                        sep(bt, Core.bundle.format("bullet.suppression", Strings.autoFixed(type.suppressionDuration / 60f, 2), Strings.fixed(type.suppressionRange / tilesize, 1)));
                    }

                    if(type.status != StatusEffects.none){
                        sep(bt, (type.status.hasEmoji() ? type.status.emoji() : "") + "[stat]" + type.status.localizedName + (type.status.reactive ? "" : "[lightgray] ~ [stat]" +
                                Strings.autoFixed(type.statusDuration / 60f, 1) + "[lightgray] " + Core.bundle.get("unit.seconds"))).with(c -> withTooltip(c, type.status));
                    }

                    if(!type.targetMissiles){
                        sep(bt, "@bullet.notargetsmissiles");
                    }

                    if(!type.targetBlocks){
                        sep(bt, "@bullet.notargetsbuildings");
                    }

                    if(type.intervalBullet != null){
                        bt.row();

                        Table ic = new Table();
                        ammo(ObjectMap.of(t, type.intervalBullet), true, false).display(ic);
                        Collapser coll = new Collapser(ic, true);
                        coll.setDuration(0.1f);

                        bt.table(it -> {
                            it.left().defaults().left();

                            it.add(Core.bundle.format("bullet.interval", Strings.autoFixed(type.intervalBullets / type.bulletInterval * 60, 2)));
                            it.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).padLeft(16f).expandX();
                        });
                        bt.row();
                        bt.add(coll);
                    }

                    if(type.fragBullet != null){
                        bt.row();

                        Table fc = new Table();
                        ammo(ObjectMap.of(t, type.fragBullet), true, false).display(fc);
                        Collapser coll = new Collapser(fc, true);
                        coll.setDuration(0.1f);

                        bt.table(ft -> {
                            ft.left().defaults().left();

                            ft.add(Core.bundle.format("bullet.frags", type.fragBullets));
                            ft.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).padLeft(16f).expandX();
                        });
                        bt.row();
                        bt.add(coll);
                    }

                    if(type.spawnBullets != null && type.spawnBullets.size > 0){
                        bt.row();

                        Table sc = new Table();
                        for(BulletType spawn : type.spawnBullets){
                            if(spawn.showStats) ammo(ObjectMap.of(t, spawn), true, false).display(sc);
                        }
                        Collapser coll = new Collapser(sc, true);
                        coll.setDuration(0.1f);

                        bt.table(st -> {
                            st.left().defaults().left();

                            st.add(Core.bundle.format("bullet.spawnBullets", type.spawnBullets.size));
                            if(sc.getChildren().size > 0) st.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).padLeft(16f).expandX();
                        });
                        bt.row();
                        bt.add(coll);
                    }

                }).padLeft(5).padTop(5).padBottom(compact ? 0 : 5).growX().margin(compact ? 0 : 10);
                table.row();
            }
        };
    }

    private static String ammoStat(float val){
        return (val > 0 ? "[stat]+" : "[negstat]") + Strings.autoFixed(val, 1);
    }

    private static Cell<?> sep(Table table, String text){
        table.row();
        return table.add(text);
    }

    static @Nullable TextureRegionDrawable noteIcon = Icon.arrowNoteSmall != null ? new TextureRegionDrawable(Icon.arrowNoteSmall) : null;

    private static Cell<?> note(Table table, String text){
        table.row();
        return table.table(t -> {
            if(noteIcon != null){
                noteIcon.setMinWidth(15f);
                noteIcon.setMinHeight(15f);
                t.image(noteIcon).color(Pal.stat).scaling(Scaling.fit).padRight(6).padLeft(12);
            }
            t.add(text);
        });
    }

    private static TextureRegion icon(UnlockableContent t){
        return t.uiIcon;
    }

    public static <T extends Unit> void removeUnit(T unit) {
        unit.type.deathSound.at(unit, 1, unit.type.deathSoundVolume);
        unit.type.deathExplosionEffect.at(unit, unit.bounds() / 16);
        if (Objects.equals(unit.type.name, "flameout-empathy")) try {
            Class<?> clazz = Class.forName("flame.unit.empathy.EmpathyDamage", false, mods.getMod("flameout").loader);
            Method method = clazz.getDeclaredMethod("removeEmpathy", Class.forName("flame.unit.empathy.EmpathyUnit", false, mods.getMod("flameout").loader));
            method.setAccessible(true);
            method.invoke(null, unit);
        } catch (Exception ignored) {}
        unit.remove();
    }
}
