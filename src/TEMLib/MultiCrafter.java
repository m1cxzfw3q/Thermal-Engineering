package TEMLib;

import arc.Core;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.*;
import arc.graphics.Color;
import arc.util.Nullable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.Block;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.blocks.heat.HeatConsumer;
import mindustry.world.blocks.production.*;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.consumers.ConsumeLiquidsDynamic;
import mindustry.world.draw.DrawHeatInput;
import mindustry.world.draw.DrawHeatOutput;
import mindustry.world.draw.DrawMulti;
import mindustry.world.meta.*;

public class MultiCrafter extends GenericCrafter {
    public @Nullable Seq<Seq<Recipe>> recipes = new Seq<>();
    //HeatCrafter
    /** Base heat requirement for 100% efficiency. */
    private float heatRequirement;
    /** After heat meets this requirement, excess heat will be scaled by this number. */
    public float overheatScale = 1f;
    /** Maximum possible efficiency after overheat. */
    public float maxEfficiency = 1f;
    //HeatProducer
    private float heatOutput;
    public float warmupRate = 0.15f;

    public static float uniCraftTime;

    private final DrawMulti drawHeat = new DrawMulti();

    public MultiCrafter(String name) {
        super(name);
        configurable = true;
        saveConfig = true;
        config(Integer.class, (MultiCrafterBuild build, Integer i) -> {
            if(!configurable || build.currentConfigurationId == i) return;
            build.currentConfigurationId = i < 0 || i >= recipes.size ? -1 : i;
            build.progress = 0;
        });
    }

    @Override
    public void init() {
        consume(new ConsumeItemDynamic(
                (MultiCrafterBuild e) -> e.currentRecipeId != -1 ?
                        e.getCurrentRecipes(e.currentConfigurationId).get(e.currentRecipeId).input.items : ItemStack.empty
        ));
        consume(new ConsumeLiquidsDynamic(
                (MultiCrafterBuild e) -> e.currentRecipeId != -1 ?
                        e.getCurrentRecipes(e.currentConfigurationId).get(e.currentRecipeId).input.liquids : LiquidStack.empty
        ));

        rotate = heatOutput > 0;

        super.init();

        if (!recipes.isEmpty()) for (var recipe1 : recipes) if (!recipe1.isEmpty()) for (var recipe : recipe1) {
            for (var item : recipe.input.items) {
                itemFilter[item.item.id] = true;
            }

            for (var item : recipe.input.liquids) {
                liquidFilter[item.liquid.id] = true;
            }

            if (recipe.heatRequirement > 0 && !Seq.with(drawHeat.drawers).contains(new DrawHeatInput("-heatInput")))
                drawHeat.drawers = Seq.with(drawHeat.drawers).add(new DrawHeatInput("-heatInput")).toArray();

            if (recipe.heatOutput > 0 && !Seq.with(drawHeat.drawers).contains(new DrawHeatOutput()))
                drawHeat.drawers = Seq.with(drawHeat.drawers).add(new DrawHeatOutput() {
                    @Override
                    public void load(Block block){
                        heat = Core.atlas.find(block.name + "-heatOutput");
                        glow = Core.atlas.find(block.name + "-heatGlow");
                        top1 = Core.atlas.find(block.name + "-heatTop1");
                        top2 = Core.atlas.find(block.name + "-heatTop2");
                    }
                }).toArray();
        }
    }

    @Override
    public void load(){
        super.load();

        drawHeat.load(this);
    }

    @Override
    public boolean outputsItems() {
        boolean b = false;
        for (Seq<Recipe> recipes1 : recipes) for (Recipe recipe : recipes1) {
            b = b || recipe.output.items != ItemStack.empty;
        }
        return b;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.output);
        stats.remove(Stat.productionTime);
        stats.add(Stat.output, table -> {
            table.row();

            final int[] i = {0}, i1 = {0};
            for (Seq<Recipe> configRecipe : recipes) {
                table.table(Styles.black5, t -> {
                    if (configurable) t.add("[#ffd37f][" + i[0] + "][]");
                    for (Recipe recipe : configRecipe) {
                        table.table(Styles.black5, tl -> {
                            tl.left();
                            tl.add("[#ffd37f][" + i1[0] + "][]");
                            i[0]++;
                            tl.table(Styles.black5, t1 -> {
                                lib.itemsDisplay(recipe.input.items, table, recipe.craftTime);
                                lib.liquidsDisplay(recipe.input.liquids, table);
                            });
                            tl.image(Icon.right).color(Pal.darkishGray).size(40).pad(5f).fill();
                            tl.table(Styles.black5, t1 -> {
                                lib.itemsDisplay(recipe.output.items, table, recipe.craftTime);
                                lib.liquidsDisplay(recipe.output.liquids, table);
                            });
                        });
                        table.row();
                    }
                });
            }
        });

        if (heatRequirement > 0) {
            stats.add(Stat.input, heatRequirement, StatUnit.heatUnits);
            stats.add(Stat.maxEfficiency, (int)(maxEfficiency * 100f), StatUnit.percent);
        }

        if (heatOutput > 0) {
            stats.add(Stat.output, heatOutput, StatUnit.heatUnits);
        }
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("recipe", (MultiCrafterBuild e) -> new Bar(
                () -> Core.bundle.format("tebar.recipe", e.currentRecipe != null ? e.currentRecipe.localizedName() : "\uE815"),
                () -> Color.valueOf("4169e1"),
                () -> 1
        ));

        if (configurable) addBar("config", (MultiCrafterBuild e) -> new Bar(
                () -> Core.bundle.get("tebar.config") + ": " + e.currentConfigurationId,
                () -> Pal.bar,
                () -> 1
        ));

        if (heatRequirement > 0) addBar("heatrequire", (MultiCrafterBuild entity) -> new Bar(
                () -> Core.bundle.format("tebar.heatrequire", (int)(entity.heat + 0.01f), (int)(entity.efficiencyScale() * 100 + 0.01f)),
                () -> Pal.lightOrange,
                () -> entity.heat / heatRequirement
        ));

        if (heatOutput > 0) addBar("heatoutput", (MultiCrafterBuild entity) -> new Bar(
                "tebar.heatoutput",
                Pal.lightOrange,
                () -> entity.heat / heatOutput
        ));
    }

    public class MultiCrafterBuild extends GenericCrafterBuild implements HeatConsumer, HeatBlock {
        public int currentRecipeId = -1;
        public int currentConfigurationId = 0;
        public @Nullable Seq<Recipe> currentRecipes = null;
        public @Nullable Recipe currentRecipe = null;

        public float[] sideHeat = new float[4];
        public float heat = 0f, heatOut = 0f;

        @Override
        public void buildConfiguration(Table table) {// TODO 重写交互UI

        }

        @Override
        public void draw(){
            super.draw();
            drawHeat.draw(this);
        }

        @Override
        public void drawLight(){
            super.drawLight();
            drawHeat.drawLight(this);
        }

        @Override
        public void updateTile() {
            heat = calculateHeat(sideHeat);

            currentRecipes = getCurrentRecipes(currentConfigurationId);
            if (currentRecipes != null) for (Recipe recipe : currentRecipes) {
                if (items.has(recipe.input.items) && lib.hasLiquid(liquids, recipe.input.liquids)) currentRecipe = recipe;
            }

            if (currentRecipe != null) {
                craftTime = currentRecipe.craftTime;
                outputItems = currentRecipe.output.items;
                outputLiquids = currentRecipe.output.liquids;
                heatRequirement = currentRecipe.heatRequirement;
                heatOutput = currentRecipe.heatOutput;
            }

            super.updateTile();

            //heat approaches target at the same speed regardless of efficiency
            heatOut = Mathf.approachDelta(heat, heatOutput * efficiency, warmupRate * delta());
        }

        public Seq<Recipe> getCurrentRecipes(int configId) {
            if (configId == -1 && recipes.get(configId) == null) return null;
            return recipes.get(configId);
        }

        @Override
        public boolean shouldConsume(){
            return (heatRequirement <= 0f || heat > 0) && super.shouldConsume();
        }

        @Override
        public float heatRequirement(){
            return heatRequirement > 0 ? heatRequirement : -1;
        }

        @Override
        public float[] sideHeat(){
            return sideHeat;
        }

        @Override
        public float warmupTarget(){
            return heatRequirement > 0 ? Mathf.clamp(heat / heatRequirement) : 1;
        }

        @Override
        public float efficiencyScale(){
            float over = Math.max(heat - heatRequirement, 0f);
            return heatRequirement > 0 ? Math.min(Mathf.clamp(heat / heatRequirement) + over / heatRequirement * overheatScale, maxEfficiency) : 1f;
        }

        @Override
        public float heat() {
            return heatOut;
        }

        @Override
        public float heatFrac() {
            return heatOut / heatOutput;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(heatOut);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            heatOut = read.f();
        }
    }

    public static class Recipe {
        public StackItemLiquid input = new StackItemLiquid(), output = new StackItemLiquid();
        public int craftTime = 60, heatRequirement = -1, heatOutput = -1;

        public Recipe() {}

        public Recipe(StackItemLiquid input, StackItemLiquid output) {
            this.input = input;
            this.output = output;
        }
        public Recipe(StackItemLiquid input, StackItemLiquid output, int craftTime) {
            this.input = input;
            this.output = output;
            this.craftTime = craftTime;
        }
        public Recipe(StackItemLiquid input, StackItemLiquid output, int craftTime, int heatRequirement) {
            this.input = input;
            this.output = output;
            this.craftTime = craftTime;
            this.heatRequirement = heatRequirement;
        }

        public String localizedName() {
            StringBuilder str = new StringBuilder();
            for (ItemStack it : input.items) {
                str.append(it.item.uiIcon);
            }
            for (LiquidStack it : input.liquids) {
                str.append(it.liquid.uiIcon);
            }
            str.append(" -> ");
            for (ItemStack it : output.items) {
                str.append(it.item.uiIcon);
            }
            for (LiquidStack it : output.liquids) {
                str.append(it.liquid.uiIcon);
            }
            return str.toString();
        }
    }
}
