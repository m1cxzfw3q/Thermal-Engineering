package TEMLib.block.distribution;

import arc.struct.Seq;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.blocks.distribution.ItemBridge;
import mindustry.world.blocks.sandbox.ItemVoid;

public class LightItemBridge extends ItemBridge {
    public LightItemBridge(String name) {
        super(name);
    }

    public class LightItemBridgeBuild extends ItemBridgeBuild {
        @Override
        public boolean dump(Item item) {
            if (this.block.hasItems && this.items.total() != 0 && this.proximity.size != 0 && (item == null || this.items.has(item))) {
                int dump = this.cdump;
                Seq<Item> allItems = Vars.content.items();
                int itemSize = allItems.size;
                Object[] itemArray = allItems.items;
                int i;
                Building other;
                if (item == null) {
                    for (i = 0; i < this.proximity.size; ++i) {
                        other = this.proximity.get((i + dump) % this.proximity.size);

                        for (int ii = 0; ii < itemSize; ++ii) {
                            if (this.items.has(ii)) {
                                Item itemArr = (Item) itemArray[ii];
                                if (other.acceptItem(this, itemArr) && this.canDump(other, itemArr)) {

                                    dump(this, other, itemArr);


                                    this.incrementDump(this.proximity.size);
                                    return true;
                                }
                            }
                        }

                        this.incrementDump(this.proximity.size);
                    }
                } else {
                    for (i = 0; i < this.proximity.size; ++i) {
                        other = this.proximity.get((i + dump) % this.proximity.size);
                        if (other.acceptItem(this, item) && this.canDump(other, item)) {

                            dump(this, other, item);

                            this.incrementDump(this.proximity.size);
                            return true;
                        }

                        this.incrementDump(this.proximity.size);
                    }
                }

            }
            return false;
        }

        public boolean dump(Building build, Building other, Item item) {
            if (!other.acceptItem(build, item)) return false;


            if (other instanceof ItemVoid.ItemVoidBuild) {
                other.flowItems().add(item, build.items.get(item));
                build.items.set(item, 0);
                return true;
            }


            int accepted = other.acceptStack(item, build.items.get(item), build);
            if (accepted >0){
                other.handleStack(item,accepted,build);
                build.items.remove(item,accepted);
            }else {
                for (int i = 0;i < 60; i++) {
                    if (build.items.get(item)<=0) return false;
                    if (!other.acceptItem(build, item)) return false;
                    other.handleItem(build, item);
                    build.items.remove(item, 1);
                }
            }
            return true;
        }
    }
}
