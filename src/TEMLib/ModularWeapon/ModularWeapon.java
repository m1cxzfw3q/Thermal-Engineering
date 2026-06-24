package TEMLib.ModularWeapon;

import TEMod.content.TEFx;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectFloatMap;
import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Weapon;
import mindustry.world.consumers.ConsumePower;
import mindustry.world.meta.BuildVisibility;

// TODO
// 完全剥离，不作为可解锁项目
public class ModularWeapon {
    /** 武器基类(这意味着你可以直接薅其他单位的武器作为模块化武器单位/建筑的武器) **/
    public @Nullable Weapon weapon = null;
    /** 武器的大小(仅区分可装载的基座大小) **/
    public int size = 1;
    /** 该模块化武器可以安装在哪里 **/
    public boolean canBuildBuilding = true, canBuildUnit = true;
    /** 该模块化武器的建造消耗 */
    public Seq<ItemStack> requirements = Seq.with();
    /** 菜单中的分类 */
    public MWeaponCat category = MWeaponCat.weapon;
    /** 构建该模块化武器的时间（以ticks为单位）  如果这个值小于0，它将动态计算 */
    public float buildTime = -1f,
    /** 该模块化武器建造速度的倍数 */
    buildCostMultiplier = 1f,
    /** 科技树中研究消耗乘数 */
    researchCostMultiplier = 1;
    /** 该模块化武器是否可见以及目前是否可以建造 */
    public BuildVisibility buildVisibility = BuildVisibility.hidden;
    /** 放置方块的效果。传递大小作为旋转 *///  TODO 实现
    public Effect placeEffect = TEFx.placeTEMod,
    /** 拆除方块的效果。传递大小作为旋转 */
    breakEffect = TEFx.breakTEMod;
    /** 每种资源的消耗乘数 */
    public ObjectFloatMap<Item> researchCostMultipliers = new ObjectFloatMap<>();
    /** 单电源消费者（如适用） */
    public @Nullable ConsumePower consPower;

    //星舰属性
    /** 是否为星舰的武器 */
    public boolean isStarshipWeapon = false;
    /** 星舰武器：支持的最低星舰等级 */
    public int minStarshipTier = 0;

    public ModularWeapon(String name) {
    }

    /** 按需初始化统计数据，应只调用一次  仅在显示内容之前调用 */
    public void setStats(){

    }

    /** 在详情之后显示任何额外信息 */
    public void displayExtra(Table table){

    }

    /** @return 研究此内容所需的资源 */
    public ItemStack[] researchRequirements(){
        return ItemStack.empty;
    }

    public enum MWeaponCat {
        assistant, weapon
    }
}