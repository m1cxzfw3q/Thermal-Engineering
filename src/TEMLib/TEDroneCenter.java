package TEMLib;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.Table;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.ai.UnitCommand;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.Styles;
import mindustry.world.*;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.*;

// TODO test
public class TEDroneCenter extends Block {
    public int unitsSpawned = 4;
    public UnitType droneType;
    public float droneConstructTime = 60f * 4f;
    public float fetchRange = 100f, droneFetchRange = 45f;

    public TEDroneCenter(String name){
        super(name);

        update = solid = true;
        configurable = true;
        commandable = true;
    }

    @Override
    public void init(){
        super.init();

        droneType.aiController = SmartDroneAI::new;
        droneType.controller = u -> new SmartDroneAI();
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
                b.row().add(Stat.maxUnits.localized() + ": " + unitsSpawned);
                b.row().table(Styles.none, t -> {
                    t.add(Stat.buildTime.localized() + ": ");
                    StatValues.percentModifier(droneConstructTime, StatUnit.perSecond).display(t);
                });
            }).growX().pad(5).row();
        });
    }

    public class TEDroneCenterBuild extends Building implements SmartDroneAI.DroneAIInterface {
        protected IntSeq readUnits = new IntSeq(), readTargets = new IntSeq();

        public Seq<Unit>
        units = new Seq<>(),   // 生成的单位集合，用于显示及销毁
        targets = new Seq<>(); // 目标单位集合，用于渲染
        public float droneProgress, droneWarmup, totalDroneProgress;

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

            droneWarmup = Mathf.lerpDelta(droneWarmup, units.size < unitsSpawned ? efficiency : 0f, 0.1f);
            totalDroneProgress += droneWarmup * Time.delta;

            //TODO better effects?
            if(units.size < unitsSpawned && (droneProgress += edelta() / droneConstructTime) >= 1f){
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
        public void drawConfigure(){
            Drawf.square(x, y, tile.block().size * tilesize / 2f + 1f + Mathf.absin(Time.time, 4f, 1f));
            Drawf.circles(x, y, fetchRange);

            if(!targets.isEmpty()){
                for (Unit unit : targets) {
                    Drawf.square(unit.x, unit.y, unit.hitSize * 0.8f);
                }
            }

            if (!units.isEmpty()) {
                for (Unit unit : units) {
                    Drawf.square(unit.x, unit.y, unit.type.hitSize * 0.8f, Color.valueOf("877bad"));
                    Drawf.circles(unit.x, unit.y, droneFetchRange, Color.valueOf("877bad"));
                }
            }
        }

        @Override
        public void buildConfiguration(Table table) {

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
        public boolean exist() {
            return isAdded();
        }
    }
}
