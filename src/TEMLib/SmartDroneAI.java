package TEMLib;

import arc.struct.Bits;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.ai.ItemUnitStance;
import mindustry.ai.UnitCommand;
import mindustry.ai.UnitStance;
import mindustry.ai.types.FlyingAI;
import mindustry.ai.types.GroundAI;
import mindustry.ai.types.PrebuildAI;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Teams;
import mindustry.gen.*;
import mindustry.type.Item;
import mindustry.world.Build;
import mindustry.world.Tile;
import mindustry.world.blocks.ConstructBlock;
import mindustry.world.blocks.environment.Floor;

import static mindustry.Vars.*;
import static mindustry.Vars.state;

// TODO test
public class SmartDroneAI extends AIController {
    protected @Nullable UnitCommand currentCmd;
    protected @Nullable Unit followEntity;
    protected @Nullable DroneAIInterface owner;

    // MinerAI
    public boolean mining = true;
    public Item targetItem;
    public Tile ore;

    public boolean isMinerAI() {
        return currentCmd == UnitCommand.mineCommand;
    }

    // BuilderAI
    public static float buildRadius = 1500, retreatDst = 110f, retreatDelay = Time.toSeconds * 2f, defaultRebuildPeriod = 60f * 2f;

    public @Nullable Unit assistFollowing;
    public @Nullable Unit following;
    public @Nullable Teamc enemy;
    public @Nullable Teams.BlockPlan lastPlan;

    public float fleeRange = 370f, rebuildPeriod = 60f * 2f;
    public boolean alwaysFlee;
    public boolean onlyAssist;

    boolean found = false;
    float retreatTimer;

    public void BuilderAI(boolean alwaysFlee, float fleeRange){
        this.alwaysFlee = alwaysFlee;
        this.fleeRange = fleeRange;
    }

    protected boolean nearEnemy(int x, int y){
        return Units.nearEnemy(unit.team, x * tilesize - fleeRange/2f, y * tilesize - fleeRange/2f, fleeRange, fleeRange);
    }

    public boolean isBuilderAI() {
        return currentCmd == UnitCommand.assistCommand || currentCmd == UnitCommand.rebuildCommand;
    }

    // RepairAI
    public static float repairRetreatDst = 160f, repairFleeRange = 310f, repairRetreatDelay = Time.toSeconds * 3f;

    @Nullable Teamc avoid;
    float repairRetreatTimer;
    Building damagedTarget;

    public boolean isRepairAI() {
        return currentCmd == UnitCommand.repairCommand;
    }

    @Override
    public AIController fallback(){
        // BuilderAI (full)
        if (isBuilderAI()) {
            if (unit.team.isAI() && unit.team.rules().prebuildAi) {
                return new PrebuildAI();
            }
            return unit.type.flying ? new FlyingAI() : new GroundAI();
        }
        return null;
    }

    @Override
    public boolean useFallback(){
        // BuilderAI (full)
        if (isBuilderAI()) {
            if (unit.team.isAI() && unit.team.rules().prebuildAi) {
                return true;
            }
            return state.rules.waves && unit.team == state.rules.waveTeam && !unit.team.rules().rtsAi;
        }
        return false;
    }

    @Override
    public boolean shouldFire(){
        // BuilderAI (full)
        if (isBuilderAI()) return !(unit.controller() instanceof SmartDroneAI ai) || ai.shouldFire();
        return true;
    }

    @Override
    public boolean shouldShoot(){
        // BuilderAI (full)
        if (isBuilderAI()) return !unit.isBuilding() && unit.type.canAttack;
        return true;
    }

    Seq<Item> mineList = new Seq<>();

    @Override
    public void updateMovement(){
        if (isMinerAI()) {
            // MinerAI
            if(targetItem == null || (!hasStance(UnitStance.mineAuto) && !mineList.contains(targetItem))){
                mining = false;
            }

            Building core = unit.closestCore();

            if (!unit.canMine() || core == null) return;

            if (!unit.validMine(unit.mineTile)) {
                unit.mineTile(null);
            }

            SmartDroneAI ai = unit.controller() instanceof SmartDroneAI a ? a : null;

            if (mining) {
                if (timer.get(timerTarget2, 60 * 4) || targetItem == null) {
                    if (ai != null && !ai.hasStance(UnitStance.mineAuto)) {
                        targetItem = content.items().min(
                                i -> ((unit.type.mineFloor && indexer.hasOre(i)) || (unit.type.mineWalls && indexer.hasWallOre(i)))
                                        && unit.canMine(i) && ai.hasStance(ItemUnitStance.getByItem(i)),
                                i -> core.items.get(i)
                        );
                    } else {
                        targetItem = unit.type.mineItems.min(i ->
                                ((unit.type.mineFloor && indexer.hasOre(i)) || (unit.type.mineWalls && indexer.hasWallOre(i)))
                                        && unit.canMine(i),
                                i -> core.items.get(i)
                        );
                    }
                }

                //core full of the target item, do nothing
                if (targetItem != null && core.acceptStack(targetItem, 1, unit) == 0) {
                    unit.clearItem();
                    unit.mineTile = null;
                    return;
                }

                //if inventory is full, drop it off.
                if (unit.stack.amount >= unit.type.itemCapacity || (targetItem != null && !unit.acceptsItem(targetItem))) {
                    mining = false;
                } else {
                    if (timer.get(timerTarget3, 60) && targetItem != null) {
                        ore = null;
                        if (unit.type.mineFloor) ore = indexer.findClosestOre(core.x, core.y, targetItem);
                        if (ore == null && unit.type.mineWalls)
                            ore = indexer.findClosestWallOre(core.x, core.y, targetItem);
                    }

                    if (ore != null) {
                        moveTo(ore, unit.type.mineRange / 2f, 20f);

                        if (unit.within(ore, unit.type.mineRange) && unit.validMine(ore)) {
                            unit.mineTile = ore;
                        }
                    }
                }
            } else {
                unit.mineTile = null;

                if (unit.stack.amount == 0) {
                    mining = true;
                    return;
                }

                if (unit.within(core, unit.type.range)) {
                    if (core.acceptStack(unit.stack.item, unit.stack.amount, unit) > 0) {
                        Call.transferItemTo(unit, unit.stack.item, unit.stack.amount, unit.x, unit.y, core);
                    }

                    unit.clearItem();
                    mining = true;
                }

                circle(core, unit.type.range / 1.8f);
            }

            if (!unit.type.flying) {
                unit.updateBoosting(
                        unit.type.boostWhenMining || unit.floorOn().isDuct || unit.floorOn().damageTaken > 0f || unit.floorOn().isDeep()
                );
            }
        } else if (isBuilderAI()) {
            // BuilderAI (full)
            onlyAssist = currentCmd != UnitCommand.rebuildCommand;

            if(target != null && shouldShoot()){
                unit.lookAt(target);
            }else if(!unit.type.flying){
                unit.lookAt(unit.prefRotation());
            }

            unit.updateBuilding = true;

            if(assistFollowing != null && !assistFollowing.isValid()) assistFollowing = null;
            if(following != null && !following.isValid()) following = null;

            if(assistFollowing != null && assistFollowing.activelyBuilding()){
                following = assistFollowing;
            }

            boolean moving = false;
            boolean hold = hasStance(UnitStance.holdPosition);

            if(following != null){
                retreatTimer = 0f;
                //try to follow and mimic someone

                //validate follower
                if(!following.isValid() || !following.activelyBuilding()){
                    following = null;
                    unit.plans.clear();
                    return;
                }

                //set to follower's first build plan, whatever that is
                unit.plans.clear();
                unit.plans.addFirst(following.buildPlan());
                lastPlan = null;
            }else if((unit.buildPlan() == null || alwaysFlee) && !hold){
                //not following anyone or building
                if(timer.get(timerTarget4, 40)){
                    enemy = target(unit.x, unit.y, fleeRange, true, true);
                }

                //fly away from enemy when not doing anything, but only after a delay
                if((retreatTimer += Time.delta) >= retreatDelay || alwaysFlee){
                    if(enemy != null){
                        unit.clearBuilding();
                        var core = unit.closestCore();
                        if(core != null && !unit.within(core, retreatDst)){
                            moveTo(core, retreatDst);
                            moving = true;
                        }
                    }
                }
            }

            if(unit.buildPlan() != null){
                if(!alwaysFlee) retreatTimer = 0f;
                //approach plan if building
                BuildPlan req = unit.buildPlan();

                //clear break plan if another player is breaking something
                if(!req.breaking && timer.get(timerTarget2, 40f)){
                    for(Player player : Groups.player){
                        if(player.isBuilder() && player.unit().activelyBuilding() && player.unit().buildPlan().samePos(req) && player.unit().buildPlan().breaking){
                            unit.plans.removeFirst();
                            //remove from list of plans
                            unit.team.data().plans.remove(p -> p.x == req.x && p.y == req.y);
                            return;
                        }
                    }
                }

                boolean valid =
                        !(lastPlan != null && lastPlan.removed) &&
                                ((req.tile() != null && req.tile().build instanceof ConstructBlock.ConstructBuild cons && cons.current == req.block) ||
                                        (req.breaking ?
                                                Build.validBreak(unit.team(), req.x, req.y) :
                                                Build.validPlace(req.block, unit.team(), req.x, req.y, req.rotation)));

                if(valid){
                    if(!hold){
                        float range = Math.min(unit.type.buildRange - unit.type.hitSize * 2f, buildRadius);
                        //move toward the plan
                        moveTo(req.tile(), range, 20f);
                        moving = !unit.within(req.tile(), range);
                    }else if(!unit.within(req, unit.type.buildRange - tilesize) && !state.rules.infiniteResources){
                        //discard the plan, it's too far away to reach while holding position. try the next one
                        unit.plans.removeFirst();
                        lastPlan = null;
                    }
                }else{
                    //discard invalid plan
                    unit.plans.removeFirst();
                    lastPlan = null;
                }
            }else{

                if(assistFollowing != null && !hold){
                    moveTo(assistFollowing, assistFollowing.type.hitSize + unit.type.hitSize/2f + 60f);
                    moving = !unit.within(assistFollowing, assistFollowing.type.hitSize + unit.type.hitSize/2f + 65f);
                }

                //follow someone and help them build
                if(timer.get(timerTarget2, 20f)){
                    found = false;

                    Units.nearby(unit.team, unit.x, unit.y, onlyAssist ? owner.droneRange() : buildRadius, u -> {
                        if(found) return;

                        if(u.canBuild() && u != unit && u.activelyBuilding()){
                            BuildPlan plan = u.buildPlan();

                            Building build = world.build(plan.x, plan.y);
                            if(build instanceof ConstructBlock.ConstructBuild cons){
                                float dist = Math.min(cons.dst(unit) - unit.type.buildRange, 0);

                                //make sure you can reach the plan in time
                                if(dist / unit.speed() < cons.buildCost * 0.9f){
                                    following = u;
                                    found = true;
                                }
                            }
                        }
                    });

                    if(onlyAssist){
                        float minDst = Float.MAX_VALUE;
                        Player closest = null;
                        for(var player : Groups.player){
                            if(!player.dead() && player.isBuilder() && player.team() == unit.team){
                                float dst = player.dst2(unit);
                                if(dst < minDst){
                                    closest = player;
                                    minDst = dst;
                                }
                            }
                        }

                        assistFollowing = closest == null ? null : closest.unit();
                    }
                }

                //find new plan
                if(!onlyAssist && !unit.team.data().plans.isEmpty() && following == null && timer.get(timerTarget3, rebuildPeriod)){
                    var blocks = unit.team.data().plans;

                    if(hold){
                        //essentially build turret behavior (find first plan in range)
                        for(int i = 0; i < blocks.size; i++){
                            var block = blocks.get(i);
                            if(state.rules.infiniteResources || unit.within(block.x * tilesize, block.y * tilesize, unit.type.buildRange)){
                                var btype = block.block;

                                if(Build.validPlace(btype, unit.team(), block.x, block.y, block.rotation)){
                                    unit.addBuild(new BuildPlan(block.x, block.y, block.rotation, block.block, block.config));
                                    //shift build plan to tail so next unit builds something else
                                    blocks.addLast(blocks.removeIndex(i));
                                    lastPlan = block;
                                    break;
                                }
                            }
                        }
                    }else{
                        Teams.BlockPlan block = blocks.first();

                        //check if it's already been placed
                        if(world.tile(block.x, block.y) != null && world.tile(block.x, block.y).block() == block.block){
                            blocks.removeFirst();
                        }else if(Build.validPlace(block.block, unit.team(), block.x, block.y, block.rotation)
                                && (!alwaysFlee || !nearEnemy(block.x, block.y))){ //check if it's valid

                            lastPlan = block;
                            //add build plan
                            unit.addBuild(new BuildPlan(block.x, block.y, block.rotation, block.block, block.config));
                            //shift build plan to tail so next unit builds something else
                            blocks.addLast(blocks.removeFirst());
                        }else{
                            //shift head of queue to tail, try something else next time
                            blocks.addLast(blocks.removeFirst());
                        }
                    }
                }
            }

            if(!unit.type.flying){
                unit.updateBoosting(unit.type.boostWhenBuilding || moving || unit.floorOn().isDuct || unit.floorOn().damageTaken > 0f || unit.floorOn().isDeep());
            }
        } else if (isRepairAI()) {
            //RepairAI
            if(target instanceof Building){
                boolean shoot = false;

                if(target.within(unit, unit.type.range)){
                    unit.aim(target);
                    shoot = true;
                }

                unit.controlWeapons(shoot);
            }else if(target == null){
                unit.controlWeapons(false);
            }

            boolean hold = hasStance(UnitStance.holdPosition);

            if(target != null && target instanceof Building b && b.team == unit.team){
                if(!hold){
                    if(unit.type.circleTarget){
                        circleAttack(unit.type.circleTargetRadius);
                    }else if(!target.within(unit, unit.type.range * 0.65f)){
                        moveTo(target, unit.type.range * 0.65f);
                    }
                }

                if(!unit.type.circleTarget){
                    unit.lookAt(target);
                }
            }

            //not repairing
            if(!(target instanceof Building) && !hold){
                if(timer.get(timerTarget4, 40)){
                    avoid = target(unit.x, unit.y, repairFleeRange, true, true);
                }

                if((repairRetreatTimer += Time.delta) >= repairRetreatDelay){
                    //fly away from enemy when not doing anything
                    if(avoid != null){
                        var core = unit.closestCore();
                        if(core != null && !unit.within(core, repairRetreatDst)){
                            moveTo(core, repairRetreatDst);
                        }
                    }
                }
            }else{
                repairRetreatTimer = 0f;
            }
        }
    }

    @Override
    public void updateTargeting(){
        if (currentCmd == UnitCommand.repairCommand){
            // RepairAI
            if (timer.get(timerTarget, 15)) {
                damagedTarget = Units.findDamagedTile(unit.team, unit.x, unit.y);
                if (damagedTarget instanceof ConstructBlock.ConstructBuild) damagedTarget = null;
            }

            if (damagedTarget == null) {
                super.updateTargeting();
            } else {
                this.target = damagedTarget;
            }
        } else super.updateTargeting();
    }

    public Bits stances = new Bits(content.unitStances().size);

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

    public SmartDroneAI() {}

    @Override
    public void updateUnit(){
        updateVisuals();
        updateTargeting();
        updateMovement();

        if(isMinerAI() && !hasStance(UnitStance.mineAuto)){
            setStance(UnitStance.mineAuto);
        }

        if(owner == null || !owner.exist()) return;

        if(currentCmd == null && unit.type.commands.size > 0){
            currentCmd = unit.type.defaultCommand == null ? unit.type.commands.first() : unit.type.defaultCommand;
        }

        if (!isMinerAI() && currentCmd != UnitCommand.rebuildCommand) {
            if (followEntity == null || !followEntity.isAdded() || (
                    !followEntity.within(unit, owner.droneRange()) && !followEntity.within(owner.getPosc(), owner.fetchRange())
            )) {
                followEntity = Units.closest(unit.team, owner.getPosc().x(), owner.getPosc().y(), owner.fetchRange(),
                        u -> u.type != unit.type && (!u.isPlayer() || (currentCmd != UnitCommand.rebuildCommand && !isMinerAI() && !isRepairAI()))
                ) == null ? Units.closest(unit.team, unit.x, unit.y, owner.droneRange(),
                        u -> u.type != unit.type && (!u.isPlayer() || (currentCmd != UnitCommand.rebuildCommand && !isMinerAI() && !isRepairAI()))
                ) : Units.closest(unit.team, owner.getPosc().x(), owner.getPosc().y(), owner.fetchRange(),
                        u -> u.type != unit.type && (!u.isPlayer() || (currentCmd != UnitCommand.rebuildCommand && !isMinerAI() && !isRepairAI()))
                );
            } else if (currentCmd == UnitCommand.moveCommand || currentCmd == UnitCommand.assistCommand) {
                moveTo(followEntity, 40);
                if (currentCmd == UnitCommand.moveCommand) unit.lookAt(followEntity);
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
    public void init() {
        content.blocks().each(
                b -> b.itemDrop != null &&
                        (b instanceof Floor f && (((f.wallOre && unit.type.mineWalls) || (!f.wallOre && unit.type.mineFloor))) ||
                                (!(b instanceof Floor) && unit.type.mineWalls)) &&
                        b.itemDrop.hardness <= unit.type.mineTier,
                b -> mineList.addUnique(b.itemDrop)
        );
    }

    public interface DroneAIInterface {
        float droneRange();

        float fetchRange();

        boolean exist();

        Posc getPosc();
    }
}
