package TEMLib;

import arc.Core;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.*;
import arc.graphics.Color;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.mod.NoPatch;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.Block;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.blocks.heat.HeatConsumer;
import mindustry.world.blocks.production.*;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.consumers.ConsumeLiquidsDynamic;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

public class MultiCrafter extends Block {
    public @Nullable Seq<Seq<Recipe>> recipes = new Seq<>();
    //HeatCrafter
    /** After heat meets this requirement, excess heat will be scaled by this number. */
    public float overheatScale = 1f;
    /** Maximum possible efficiency after overheat. */
    public float maxEfficiency = 1f;
    //HeatProducer
    public float warmupRate = 0.15f;

    //GenericCrafter
    /** Liquid output directions, specified in the same order as outputLiquids. Use -1 to dump in every direction. Rotations are relative to block. */
    public int[] liquidOutputDirections = {-1};
    /** if true, crafters with multiple liquid outputs will dump excess when there's still space for at least one liquid type */
    public boolean dumpExtraLiquid = true;
    public boolean ignoreLiquidFullness = false;

    public Effect craftEffect = Fx.none;
    public Effect updateEffect = Fx.none;
    public float updateEffectChance = 0.04f;
    public float updateEffectSpread = 4f;
    public float warmupSpeed = 0.019f;
    /** Only used for legacy cultivator blocks. */
    @NoPatch
    public boolean legacyReadWarmup = false;

    public DrawBlock drawer = new DrawDefault();

    public static float uniCraftTime = 60;

    final DrawMulti drawHeat = new DrawMulti();

    public MultiCrafter(String name) {
        super(name);
        update = true;
        solid = true;
        hasItems = true;
        ambientSound = Sounds.loopMachine;
        sync = true;
        ambientSoundVolume = 0.03f;
        flags = EnumSet.of(BlockFlag.factory);
        drawArrow = false;

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

            if (recipe.heatOutput > 0 && !Seq.with(drawHeat.drawers).contains(new DrawHeatOutput())) {
                drawHeat.drawers = Seq.with(drawHeat.drawers).add(new DrawHeatOutput() {
                    @Override
                    public void load(Block block) {
                        heat = Core.atlas.find(block.name + "-heatOutput");
                        glow = Core.atlas.find(block.name + "-heatGlow");
                        top1 = Core.atlas.find(block.name + "-heatTop1");
                        top2 = Core.atlas.find(block.name + "-heatTop2");
                    }
                }).toArray();
                rotate = true;
            }
        }
    }

    @Override
    public void load(){
        super.load();
        drawer.load(this);
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

    boolean statsAddedEff = false;

    @Override
    public void setStats() {
        super.setStats();
        stats.add(TEStat.recipe, table -> {
            table.row();

            final int[] i = {0}, i1 = {0};
            for (Seq<Recipe> configRecipe : recipes) {
                table.table(Styles.black5, t -> {
                    if (configurable) t.add("[#ffd37f][" + i[0] + "][]").fill().row();
                    for (Recipe recipe : configRecipe) {
                        table.table(Styles.black5, tl -> {
                            tl.left();
                            tl.add("[#ffd37f][" + i1[0] + "][]").fill();
                            i[0]++;
                            tl.table(Styles.black5, t1 -> {
                                lib.itemsDisplay(recipe.input.items, table, recipe.craftTime < 0 ? uniCraftTime : recipe.craftTime);
                                lib.liquidsDisplay(recipe.input.liquids, table);
                            }).fill();
                            tl.image(Icon.right).color(Pal.darkishGray).size(40).pad(5f).fill();
                            tl.table(Styles.black5, t1 -> {
                                lib.itemsDisplay(recipe.output.items, table, recipe.craftTime < 0 ? uniCraftTime : recipe.craftTime);
                                lib.liquidsDisplay(recipe.output.liquids, table);
                            }).fill();

                            if (recipe.heatRequirement > 0) {
                                tl.add(recipe.heatRequirement + "[red]" + Iconc.waves + "[]" + Iconc.download);
                                if (!statsAddedEff) stats.add(Stat.maxEfficiency, (int)(maxEfficiency * 100f), StatUnit.percent);
                                statsAddedEff = true;
                            }
                            if (recipe.heatOutput > 0) tl.add(recipe.heatOutput + "[red]" + Iconc.waves + "[]" + Iconc.upload);
                        }).fill();
                        table.row();
                    }
                }).fill();
            }
        });
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
                () -> Core.bundle.format("tebar.config", e.currentConfigurationId),
                () -> Pal.bar,
                () -> 1
        ));
    }

    public class MultiCrafterBuild extends Building implements HeatConsumer, HeatBlock {
        public int currentRecipeId = -1;
        public int currentConfigurationId = 0;
        public @Nullable Seq<Recipe> currentRecipes = null;
        public @Nullable Recipe currentRecipe = null;
        public float heatRequirement, heatOutput;

        public float[] sideHeat = new float[4];
        public float heat = 0f, heatOut = 0f;

        public float progress, totalProgress, warmup;

        @Override
        public void buildConfiguration(Table table) {// TODO 重写交互UI

        }

        @Override
        public Object config() {
            return currentRecipe;
        }

        @Override
        public void draw(){
            drawer.draw(this);
            drawHeat.draw(this);
        }

        @Override
        public void drawLight(){
            super.drawLight();
            drawer.drawLight(this);
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
                heatRequirement = currentRecipe.heatRequirement;
                heatOutput = currentRecipe.heatOutput;

                if(efficiency > 0){
                    progress += getProgressIncrease(currentRecipe.craftTime < 0 ? uniCraftTime : currentRecipe.craftTime);
                    warmup = Mathf.approachDelta(warmup, warmupTarget(), warmupSpeed);

                    //continuously output based on efficiency
                    if(currentRecipe != null && currentRecipe.output != null && currentRecipe.output.liquids != null){
                        float inc = getProgressIncrease(1f);
                        for(var output : currentRecipe.output.liquids){
                            handleLiquid(this, output.liquid, Math.min(output.amount * inc, liquidCapacity - liquids.get(output.liquid)));
                        }
                    }

                    if(wasVisible && Mathf.chanceDelta(updateEffectChance)){
                        updateEffect.at(x + Mathf.range(size * updateEffectSpread), y + Mathf.range(size * updateEffectSpread));
                    }
                }else{
                    warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
                }

                //TODO may look bad, revert to edelta() if so
                totalProgress += warmup * Time.delta;

                if(progress >= 1f){
                    craft();
                }

                dumpOutputs();

                //heat approaches target at the same speed regardless of efficiency
                heatOut = Mathf.approachDelta(heatOut, heatOutput * efficiency, warmupRate * delta());
            }
        }

        public void dumpOutputs(){
            if (currentRecipe != null && currentRecipe.output != null){
                if (currentRecipe.output.items != null && timer(timerDump, dumpTime / timeScale)) {
                    for (ItemStack output : currentRecipe.output.items) {
                        dump(output.item);
                    }
                }

                if (currentRecipe.output.liquids != null) {
                    for (int i = 0; i < currentRecipe.output.liquids.length; i++) {
                        int dir = liquidOutputDirections.length > i ? liquidOutputDirections[i] : -1;

                        dumpLiquid(currentRecipe.output.liquids[i].liquid, 2f, dir);
                    }
                }
            }
        }

        @Override
        public void displayBars(Table bars) { //动态更新热量条及流体条
            super.displayBars(bars);

            if (currentRecipe != null) {
                if (currentRecipe.input != null && currentRecipe.input.liquids != null) for (var liquid : currentRecipe.input.liquids) bars.add(new Bar(
                        () -> liquid.liquid.localizedName,
                        liquid.liquid::barColor,
                        () -> liquids.get(liquid.liquid) / liquidCapacity
                ));

                if (currentRecipe.output != null && currentRecipe.output.liquids != null) for (var liquid : currentRecipe.output.liquids) bars.add(new Bar(
                        () -> liquid.liquid.localizedName,
                        liquid.liquid::barColor,
                        () -> liquids.get(liquid.liquid) / liquidCapacity
                ));
            }

            if (heatRequirement > 0) bars.add(new Bar(
                    () -> Core.bundle.format("tebar.heatrequire", (int)(heat + 0.01f), (int)(efficiencyScale() * 100 + 0.01f)),
                    () -> Pal.lightOrange,
                    () -> heat / heatRequirement
            ));

            if (heatOutput > 0) bars.add(new Bar(
                    "tebar.heatoutput",
                    Pal.lightOrange,
                    () -> heatOut / heatOutput
            ));
        }

        @Override
        public float getProgressIncrease(float baseTime){
            if(ignoreLiquidFullness){
                return super.getProgressIncrease(baseTime);
            }

            //limit progress increase by maximum amount of liquid it can produce
            float scaling = 1f, max = 1f;
            if(currentRecipe != null && currentRecipe.output != null && currentRecipe.output.liquids != null){
                max = 0f;
                for(var s : currentRecipe.output.liquids){
                    float value = (liquidCapacity - liquids.get(s.liquid)) / (s.amount * edelta());
                    scaling = Math.min(scaling, value);
                    max = Math.max(max, value);
                }
            }

            //when dumping excess take the maximum value instead of the minimum.
            return super.getProgressIncrease(baseTime) * (dumpExtraLiquid ? Math.min(max, 1f) : scaling);
        }

        @Override
        public double sense(LAccess sensor){
            if(sensor == LAccess.progress) return progress();
            //attempt to prevent wild total liquid fluctuation, at least for crafters
            if(sensor == LAccess.totalLiquids && currentRecipe != null && currentRecipe.output != null && currentRecipe.output.liquids != null) return liquids.get(currentRecipe.output.liquids[0].liquid);
            if (sensor == LAccess.config) return currentConfigurationId;
            return super.sense(sensor);
        }

        @Override
        public float warmup(){
            return warmup;
        }

        @Override
        public float totalProgress(){
            return totalProgress;
        }

        public void craft(){
            consume();

            if(currentRecipe != null && currentRecipe.output != null && currentRecipe.output.items != null){
                for(var output : currentRecipe.output.items){
                    for(int i = 0; i < output.amount; i++){
                        offload(output.item);
                    }
                }
            }

            if(wasVisible){
                craftEffect.at(x, y);
            }
            progress %= 1f;
        }

        public Seq<Recipe> getCurrentRecipes(int configId) {
            if (configId == -1 && recipes.get(configId) == null) return null;
            return recipes.get(configId);
        }

        @Override
        public boolean shouldConsume(){
            if (currentRecipe != null){
                if (currentRecipe.output.items != null) {
                    for (var output : currentRecipe.output.items) {
                        if (items.get(output.item) + output.amount > itemCapacity) {
                            return false;
                        }
                    }
                }

                if (currentRecipe.output.liquids != null && !ignoreLiquidFullness) {
                    boolean allFull = true;
                    for (var output : currentRecipe.output.liquids) {
                        if (liquids.get(output.liquid) >= liquidCapacity - 0.001f) {
                            if (!dumpExtraLiquid) {
                                return false;
                            }
                        } else {
                            allFull = false;
                        }
                    }

                    if (allFull) {
                        return false;
                    }
                }
            }

            return (heatRequirement <= 0f || heat > 0) && enabled;
        }

        @Override
        public float heatRequirement(){
            return heatRequirement > 0 ? heatRequirement : -1;
        }

        @Override
        public float[] sideHeat(){
            return sideHeat;
        }

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
            write.f(progress);
            write.f(warmup);
            if(legacyReadWarmup) write.f(0f);

            write.f(heatOut);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            progress = read.f();
            warmup = read.f();
            if(legacyReadWarmup) read.f();

            heatOut = read.f();
        }

        @Override
        public float progress(){
            return Mathf.clamp(progress);
        }

        @Override
        public int getMaximumAccepted(Item item){
            return itemCapacity;
        }

        @Override
        public boolean shouldAmbientSound(){
            return efficiency > 0;
        }
    }

    public static class Recipe {
        public StackItemLiquid input = new StackItemLiquid(), output = new StackItemLiquid();
        public int craftTime = -1, heatRequirement = -1, heatOutput = -1;

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
