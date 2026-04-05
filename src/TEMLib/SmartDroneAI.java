package TEMLib;

import arc.struct.Bits;
import arc.util.Nullable;
import mindustry.ai.UnitCommand;
import mindustry.ai.UnitStance;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.Teamc;

import static mindustry.Vars.content;

// TODO test
public class SmartDroneAI extends AIController {
    private @Nullable UnitCommand currentCmd;
    private @Nullable Teamc followEntity;
    protected @Nullable DroneAIInterface owner;

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

    public void followEntity(Teamc entity) {
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
        if(owner == null || !owner.exist()) return;

        if(currentCmd == UnitCommand.mineCommand && !hasStance(UnitStance.mineAuto)){
            setStance(UnitStance.mineAuto);
        }

        if(currentCmd == null && unit.type.commands.size > 0){
            currentCmd = unit.type.defaultCommand == null ? unit.type.commands.first() : unit.type.defaultCommand;
        }

        updateVisuals();
        updateTargeting();
        updateMovement();

        if (currentCmd != UnitCommand.mineCommand && !hasStance(UnitStance.mineAuto)) {
            if (followEntity == null || !followEntity.within(unit, owner.droneRange())) {
                followEntity = Units.closest(unit.team, unit.x, unit.y, owner.droneRange(),
                        u -> u.type != unit.type && (!u.isPlayer() || currentCmd != UnitCommand.mineCommand)
                );
            }

            moveTo(followEntity, owner.droneRange() - 10f);
            unit.lookAt(followEntity);
        }
    }

    public void setStance(UnitStance stance){
        if(stance == UnitStance.stop) return;

        stances.andNot(stance.incompatibleStanceBits);
        stances.set(stance.id);
        stanceChanged();
    }

    public interface DroneAIInterface {
        float droneRange();

        boolean exist();
    }
}
