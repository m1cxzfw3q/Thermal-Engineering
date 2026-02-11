package TEMod.content;

import arc.Core;
import mindustry.content.Items;
import mindustry.type.Item;
import arc.graphics.Color;

import static TEMod.TECore.isComplete;

public class TEItems {
    /** 基础物品 **/
    public static Item
            nuclearFuelRod, zinc, uranium, iron, plasticAlloy, steel;
    /** 一堆自动生成的粉末 **/
    public static Item copperPowder = PowderGen(Items.copper), leadPowder = PowderGen(Items.lead), titaniumPowder = PowderGen(Items.titanium),
            thoriumPowder = PowderGen(Items.thorium), zincPowder, ironPowder;
    /** 协议与芯片 **/
    public static Item
            preliminaryAgreement, intermediateAgreement, advancedAgreement, ultimateAgreement, preliminaryChip, intermediateChip, advancedChip, ultimateChip;
    /** 一堆小东西 **/
    public static Item preliminaryEnergyComponent, intermediateEnergyComponent, advancedEnergyComponent, ultimateEnergyComponent, //能量组件
            preliminaryEnergyStorageComponent, intermediateEnergyStorageComponent, advancedEnergyStorageComponent, ultimateEnergyStorageComponent;//储能组件

    public static Item PowderGen(Item sourceItem) { //粉末生成器
        return new Item((sourceItem.name).replace("temod-", "") + "-powder", sourceItem.color) {{
            localizedName = sourceItem.localizedName + Core.bundle.format("items.powder-gen.name");
            description = Core.bundle.format("items.powder-gen.description").replace("T", sourceItem.localizedName);
            details = sourceItem.details != null ? sourceItem.details + Core.bundle.format("items.powder-gen.details")
                    : Core.bundle.format("items.powder-gen.details");
            radioactivity = sourceItem.radioactivity;
            explosiveness = sourceItem.explosiveness;
            flammability = sourceItem.flammability;
            charge = sourceItem.charge;
        }};
    }

    public static void load() {
        uranium = new Item("uranium", Color.valueOf("617270")) {{//这个mod从Json开始的第一个物品
            hardness = 5;
            cost = 1.2F;
            radioactivity = 1.5F;
            alwaysUnlocked = false;
        }};

        nuclearFuelRod = new Item("nuclear-fuel-rod", Color.valueOf("4a6949")) {{
            hardness = 0;
            radioactivity = 2F;
            alwaysUnlocked = false;
        }};

        preliminaryAgreement = new Item("preliminary-agreement", Color.valueOf("D8D8D8"));
        intermediateAgreement = new Item("intermediate-agreement", Color.valueOf("ABD0D6"));
        advancedAgreement = new Item("advanced-agreement", Color.valueOf("D3B37E"));
        ultimateAgreement = new Item("ultimate-agreement", Color.valueOf("D14C3A"));

        preliminaryChip = new Item("preliminary-chip", Color.valueOf("D8D8D8")) {{cost = 0.4f;}};
        intermediateChip = new Item("intermediate-chip", Color.valueOf("ABD0D6")) {{cost = 0.5f;}};
        advancedChip = new Item("advanced-chip", Color.valueOf("D3B37E")) {{cost = 0.6f;}};
        ultimateChip = new Item("ultimate-chip", Color.valueOf("D14C3A")) {{cost = 0.7f;}};

        zinc = new Item("zinc", Color.valueOf("BFCDBC")) {{cost = 0.6f;}};

        steel = new Item("steel", Color.valueOf("94949a")) {{
            cost = 0.9f;
        }};

        iron = new Item("iron", Color.valueOf("94949a")) {{
            cost = 0.8f;
        }};
        ironPowder = PowderGen(iron);
        zincPowder = PowderGen(zinc);

        plasticAlloy = new Item("plastic-alloy", Color.valueOf("81b54d")) {{
            explosiveness = 0.1f;
            cost = 1.5f;
            flammability = 0.2f;
        }};

        preliminaryEnergyComponent = new Item("preliminary-energy-component", Color.valueOf("D8D8D8")) {{
            explosiveness = 2f;
            charge = 5f;
            cost = 4f;
        }};
        intermediateEnergyComponent = new Item("intermediate-energy-component", Color.valueOf("ABD0D6")) {{
            explosiveness = 5f;
            charge = 12f;
            cost = 5f;
        }};
        advancedEnergyComponent = new Item("advanced-energy-component", Color.valueOf("D3B37E")) {{
            explosiveness = 12f;
            charge = 20f;
            cost = 6f;
        }};
        ultimateEnergyComponent = new Item("ultimate-energy-component", Color.valueOf("D14C3A")) {{
            explosiveness = 22f;
            charge = 35f;
            cost = 7f;
        }};

        preliminaryEnergyStorageComponent = new Item("preliminary-energy-storage-component", Color.valueOf("D8D8D8")) {{
            cost = 5f;
        }};
        intermediateEnergyStorageComponent = new Item("intermediate-energy-storage-component", Color.valueOf("ABD0D6")) {{
            cost = 6f;
        }};
        advancedEnergyStorageComponent = new Item("advanced-energy-storage-component", Color.valueOf("D3B37E")) {{
            cost = 7f;
        }};
        ultimateEnergyStorageComponent = new Item("ultimate-energy-storage-component", Color.valueOf("D14C3A")) {{
            cost = 8f;
        }};

        isComplete(TEItems.class);
    }
}
