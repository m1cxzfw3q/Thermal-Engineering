package TEMLib.block.meta;

import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;

public class StackItemLiquid {
    public ItemStack[] items = {};
    public LiquidStack[] liquids = {};

    public StackItemLiquid() {}

    public StackItemLiquid(ItemStack[] items) {
        this.items = items;
    }

    public StackItemLiquid(LiquidStack[] liquids) {
        this.liquids = liquids;
    }

    public StackItemLiquid(ItemStack[] items, LiquidStack[] liquids) {
        this.items = items;
        this.liquids = liquids;
    }
}