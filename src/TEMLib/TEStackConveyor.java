package TEMLib;

import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.blocks.distribution.StackConveyor;

public class TEStackConveyor extends StackConveyor {
    public TEStackConveyor(String name) {
        super(name);
    }

    public class TEStackConveyorBuild extends StackConveyorBuild {
        @Override
        public void updateTile(){
            //reel in crater
            if(cooldown > 0f) cooldown = 0;

            //indicates empty state
            if(link == -1) return;

            //get current item
            if(lastItem == null || !items.has(lastItem)){
                lastItem = items.first();
            }

            //do not continue if disabled, will still allow one to be reeled in to prevent visual stacking
            if(!enabled) return;

            if(state == stateUnload){ //unload
                while(lastItem != null && !outputRouter ? moveForward(lastItem) : dump(lastItem)){
                    if(!outputRouter){
                        items.remove(lastItem, 1);
                    }

                    if(!items.has(lastItem)){
                        poofOut();
                        lastItem = null;
                        break;
                    }
                }
            }else{ //transfer
                if(state != stateLoad || (items.total() >= getMaximumAccepted(lastItem))){
                    if(front() instanceof StackConveyorBuild e && e.team == team && e.link == -1){
                        e.items.add(items);
                        e.lastItem = lastItem;
                        e.link = tile.pos();
                        //▲ to | from ▼
                        link = -1;
                        items.clear();

                        cooldown = 0;
                        e.cooldown = 0;
                    }
                }
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            if(this == source) return items.total() < itemCapacity && (!items.any() || items.has(item)); //player threw items
            return !((state != stateLoad) //not a loading dock
                    ||  (items.any() && !items.has(item)) //incompatible items
                    ||  (items.total() >= getMaximumAccepted(item)) //filled to capacity
                    ||  (front()  == source));
        }
    }
}
