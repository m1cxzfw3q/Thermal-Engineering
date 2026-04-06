package TEMLib;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ButtonGroup;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Table;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.ai.UnitCommand;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.LAccess;
import mindustry.type.*;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.*;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.*;

public class TEDroneCenter extends Block {
    public int unitsSpawned = 4;
    public UnitType droneType;
    public float droneConstructTime = 60f * 4f;
    public float fetchRange = 220f, droneFetchRange = 100f;
    public boolean drawRange = true;

    public TEDroneCenter(String name){
        super(name);

        update = true;
        configurable = true;
        commandable = true;

        config(UnitCommand.class, (TEDroneCenterBuild build, UnitCommand command) -> build.command = command);
        config(Item.class, (TEDroneCenterBuild build, Item item) -> build.mineItem = item);

        configClear((TEDroneCenterBuild build) -> {
            build.command = null;
            build.mineItem = null;
        });
    }

    @Override
    public void init(){
        super.init();

        droneType.aiController = SmartDroneAI::new;
        droneType.controller = u -> new SmartDroneAI();
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);

        if (drawRange) Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, fetchRange, Pal.accent);
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(Stat.unitType, table -> {
            table.row();
            table.table(Styles.grayPanel, b -> {
                b.image(droneType.uiIcon).size(40).pad(10f).left().scaling(Scaling.fit);
                b.table(info -> {
                    info.add(droneType.localizedName).left();
                    if(Core.settings.getBool("console")){
                        info.row();
                        info.add(droneType.name).left().color(Color.lightGray);
                    }
                });
                b.button("?", Styles.flatBordert,
                        () -> ui.content.show(droneType)
                ).size(40f).pad(10).right().grow().visible(() -> droneType.unlockedNow() && !droneType.hidden);
                b.row().add(Stat.maxUnits.localized() + ": " + unitsSpawned).left();
                b.row().add(Stat.buildTime.localized() + ": " + droneConstructTime / 60 + StatUnit.seconds.localized()).left();
            }).growX().pad(5).row();
        });
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("progress", (TEDroneCenterBuild e) -> new Bar("bar.progress", Pal.ammo, () -> Mathf.clamp(e.droneProgress)));
    }

    @Override
    public boolean outputsItems(){
        return false;
    }

    public class TEDroneCenterBuild extends Building implements SmartDroneAI.DroneAIInterface {
        protected IntSeq readUnits = new IntSeq(), readTargets = new IntSeq();

        public Seq<Unit>
        units = new Seq<>(),   // 此建筑制造的无人机单位集合，用于显示及销毁
        targets = new Seq<>(); // 目标单位集合，用于显示
        public float droneProgress, droneWarmup, totalDroneProgress;

        public UnitCommand command;
        public Item mineItem;

        @Override
        public void updateTile(){
            if(!readUnits.isEmpty()){
                units.clear();
                readUnits.each(i -> {
                    var unit = Groups.unit.getByID(i);
                    if(unit != null){
                        units.add(unit);
                    }
                });
                readUnits.clear();
            }

            if(!readTargets.isEmpty()){
                targets.clear();
                readTargets.each(i -> {
                    var unit = Groups.unit.getByID(i);
                    if(unit != null){
                        targets.add(unit);
                    }
                });
                readTargets.clear();
            }

            units.removeAll(u -> !u.isAdded() || u.dead);
            targets.removeAll(u -> !u.isAdded() || u.dead);

            if (!units.isEmpty()) {
                targets.clear();
                for (Unit unit : units) {
                    var ai = unit.controller() instanceof SmartDroneAI s ? s : null;
                    if (ai != null) {
                        if (ai.followEntity != null && ai.followEntity.isAdded() && !targets.contains(ai.followEntity)) targets.add(unit);
                        if (ai.owner != this) ai.owner = this;
                        if (ai.command() != command && ai.unit().type.commands.contains(command)) ai.command(command);
                        if (ai.targetItem != mineItem) ai.targetItem = mineItem;
                    }
                }
            }

            droneWarmup = Mathf.lerpDelta(droneWarmup, units.size < unitsSpawned ? efficiency : 0f, 0.1f);
            totalDroneProgress += droneWarmup * edelta();

            if(units.size < unitsSpawned && (droneProgress += edelta() / droneConstructTime) >= 1f){
                consume();

                var unit = droneType.create(team);
                if(unit instanceof BuildingTetherc bt){
                    bt.building(this);
                }
                unit.set(x, y);
                unit.rotation = 90f;
                unit.add();

                var ai = ((SmartDroneAI) unit.controller());
                ai.owner = this;
                ai.followEntity(Units.closest(team(), x, y, fetchRange,
                        u -> u.type != unit.type && (!u.isPlayer() || ai.command() != UnitCommand.mineCommand)
                ));

                Fx.spawn.at(unit);
                units.add(unit);
                droneProgress = 0f;
            }
        }

        @Override
        public boolean shouldConsume() {
            return super.shouldConsume() && units.size < unitsSpawned;
        }

        @Override
        public void drawConfigure(){
            Drawf.square(x, y, tile.block().size * tilesize / 2f + 1f + Mathf.absin(Time.time, 4f, 1f));
            Drawf.dashCircle(x, y, fetchRange, Pal.accent);

            if(!targets.isEmpty()){
                for (Unit unit : targets) {
                    Drawf.square(unit.x, unit.y, unit.hitSize * 0.8f);
                }
            }

            if (!units.isEmpty()) {
                for (Unit unit : units) {
                    Drawf.square(unit.x, unit.y, unit.type.hitSize * 0.8f, Color.valueOf("877bad"));
                    Drawf.dashCircle(unit.x, unit.y, droneFetchRange, Color.valueOf("877bad"));
                }
            }
        }

        @Override
        public double sense(LAccess sensor){
            if(sensor == LAccess.config) return command.id;
            if(sensor == LAccess.progress) return Mathf.clamp(droneProgress);
            return super.sense(sensor);
        }

        @Override
        public void buildConfiguration(Table table) {
            Table commands = new Table();
            commands.top().left();

            Runnable rebuildCommands = () -> {
                commands.clear();
                commands.background(null);
                commands.background(Styles.black6);

                var group = new ButtonGroup<ImageButton>();
                group.setMinCheckCount(0);
                var list = droneType.commands.copy().select(
                        cmd -> cmd != UnitCommand.enterPayloadCommand && cmd != UnitCommand.loadUnitsCommand
                                && cmd != UnitCommand.loadBlocksCommand && cmd != UnitCommand.unloadPayloadCommand
                                && cmd != UnitCommand.loopPayloadCommand
                );
                int i = 0, columns = Mathf.clamp(list.size, 2, selectionColumns);

                for(var item : list){
                    ImageButton button = commands.button(item.getIcon(), Styles.clearNoneTogglei, 40f,
                            () -> configure(item)
                    ).tooltip(item.localized()).group(group).get();

                    button.update(() -> button.setChecked(command == item || (command == null && droneType.defaultCommand == item)));

                    if(++i % columns == 0){
                        commands.row();
                    }
                }

                if(list.size < columns){
                    for(int j = 0; j < (columns - list.size); j++){
                        commands.add().size(40f);
                    }
                }

                if (command == UnitCommand.mineCommand) {
                    var group1 = new ButtonGroup<ImageButton>();
                    group1.setMinCheckCount(0);
                    commands.row().image(Tex.whiteui, Pal.gray).height(4f).growX().colspan(columns).row();
                    Seq<Item> mineList = new Seq<>();

                    content.blocks().each(
                            b -> b.itemDrop != null &&
                                    (b instanceof Floor f && (((f.wallOre && droneType.mineWalls) || (!f.wallOre && droneType.mineFloor))) ||
                                            (!(b instanceof Floor) && droneType.mineWalls)) &&
                                    b.itemDrop.hardness <= droneType.mineTier,
                            b -> mineList.addUnique(b.itemDrop)
                    );

                    int i1 = 0, columns1 = Mathf.clamp(mineList.size, 2, selectionColumns);

                    for (Item item : mineList) {
                        ImageButton button = commands.button(new TextureRegionDrawable(item.uiIcon), Styles.clearNoneTogglei, 40f,
                                () -> configure(item)
                        ).tooltip(item.localizedName).group(group1).get();

                        button.update(() -> button.setChecked(mineItem == item || mineItem == null));

                        if (++i1 % columns == 0) {
                            commands.row();
                        }
                    }


                    if (mineList.size < columns1) {
                        for (int j = 0; j < (columns1 - mineList.size); j++) {
                            commands.add().size(40f);
                        }
                    }
                }
            };

            rebuildCommands.run();
            table.add(commands).fillX().left();
        }

        @Override
        public void draw(){
            super.draw();

            if(droneWarmup > 0){
                Draw.draw(Layer.blockOver + 0.2f, () -> Drawf.construct(
                        this, droneType.fullIcon, Pal.accent, 0f, droneProgress, droneWarmup, totalDroneProgress, 14f
                ));
            }
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.s(targets.size);
            for(var unit : targets){
                write.i(unit.id);
            }

            write.s(units.size);
            for(var unit : units){
                write.i(unit.id);
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            int countTargets = read.s();
            readTargets.clear();
            for(int i = 0; i < countTargets; i++){
                readTargets.add(read.i());
            }

            int countUnits = read.s();
            readUnits.clear();
            for(int i = 0; i < countUnits; i++){
                readUnits.add(read.i());
            }
        }

        @Override
        public void remove() {
            for (var unit : units) {
                Call.unitDespawn(unit);
            }
            super.remove();
        }

        @Override
        public float droneRange() {
            return droneFetchRange;
        }

        @Override
        public float fetchRange() {
            return fetchRange;
        }

        @Override
        public boolean exist() {
            return isAdded();
        }

        @Override
        public Posc getPosc() {
            return this;
        }
    }
}
