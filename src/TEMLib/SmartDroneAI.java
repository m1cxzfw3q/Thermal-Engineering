package TEMLib;

import arc.struct.Bits;
import arc.util.Nullable;
import mindustry.ai.UnitCommand;
import mindustry.ai.UnitStance;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.Groups;
import mindustry.gen.Posc;
import mindustry.gen.Unit;

import static mindustry.Vars.content;

// TODO test
public class SmartDroneAI extends AIController {
    protected @Nullable UnitCommand currentCmd;
    protected @Nullable Unit followEntity;
    protected @Nullable DroneAIInterface owner;
    protected int readOwner = -1, readFollowEntity = -1;


    public Bits stances = new Bits(content.unitStances().size);

    public SmartDroneAI(DroneAIInterface owner) {
        this.owner = owner;
    }

    public void command(UnitCommand command){
        if(unit.type.commands.contains(command)){
            unit.mineTile = null;
            unit.clearBuilding();
            currentCmd = command;
        }
    }

    public UnitCommand command() {
        return currentCmd;
    }

    public void followEntity(Unit entity) {
        followEntity = entity;
    }

    @Override
    public void init(){
        if(currentCmd == null){
            currentCmd = unit.type.defaultCommand == null && unit.type.commands.size > 0 ? unit.type.commands.first() : unit.type.defaultCommand;
            if(currentCmd == null) currentCmd = UnitCommand.moveCommand;
        }
    }

    public SmartDroneAI() {}

    @Override
    public void updateUnit(){
        updateVisuals();
        updateTargeting();
        updateMovement();

        if(owner == null || !owner.exist()) return;

        if(currentCmd == UnitCommand.mineCommand && !hasStance(UnitStance.mineAuto)){
            setStance(UnitStance.mineAuto);
        }

        if(currentCmd == null && unit.type.commands.size > 0){
            currentCmd = unit.type.defaultCommand == null ? unit.type.commands.first() : unit.type.defaultCommand;
        }

        if (currentCmd != UnitCommand.mineCommand && !hasStance(UnitStance.mineAuto)) {
            if (
                    followEntity == null ||
                            !followEntity.isAdded() ||
                            (!followEntity.within(unit, owner.droneRange()) && !followEntity.within(owner.getPosc(), owner.fetchRange()))
            ) {
                followEntity = Units.closest(unit.team, unit.x, unit.y, owner.droneRange(),
                        u -> u.type != unit.type && (!u.isPlayer() || currentCmd != UnitCommand.mineCommand)
                );
            } else {
                moveTo(followEntity, 40);
                unit.lookAt(followEntity);
            }
        }
    }

    public void setStance(UnitStance stance){
        if(stance == UnitStance.stop) return;

        stances.andNot(stance.incompatibleStanceBits);
        stances.set(stance.id);
        stanceChanged();
    }

    @Override
    public void afterRead(Unit unit){
        if(readOwner != -1){
            owner = Groups.build.getByID(readOwner) instanceof DroneAIInterface t ? t : null;
            readOwner = -1;
        }

        if (readFollowEntity != -1) {
            followEntity = Groups.unit.getByID(readFollowEntity);
            readFollowEntity = -1;
        }
    }



    public interface DroneAIInterface {
        float droneRange();

        float fetchRange();

        boolean exist();

        Posc getPosc();
    }
}
