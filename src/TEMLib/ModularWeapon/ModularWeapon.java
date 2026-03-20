package TEMLib.ModularWeapon;

import arc.func.Cons;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectFloatMap;
import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Planet;
import mindustry.type.Weapon;
import mindustry.world.consumers.ConsumePower;
import mindustry.world.meta.BuildVisibility;

// 尚不稳定
// TODO 禁止使用，否则后果自负
public class ModularWeapon extends UnlockableContent {
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
    //** 放置方块的效果。传递大小作为旋转 */  TODO 实现
    //public Effect placeEffect = Fx.placeBlock,
    //** 拆除方块的效果。传递大小作为旋转 */
    //breakEffect = Fx.breakBlock;
    /** 每种资源的消耗乘数 */
    public ObjectFloatMap<Item> researchCostMultipliers = new ObjectFloatMap<>();
    /** 研究成本覆盖。如未设置，则使用上述乘数及模块化武器要求 */
    public @Nullable ItemStack[] researchCost;
    /** 单电源消费者（如适用） */
    public @Nullable ConsumePower consPower;

    //星舰属性
    /** 是否为星舰的武器 */
    public boolean isStarshipWeapon = false;
    /** 星舰武器：支持的最低星舰等级 */
    public int minStarshipTier = 0;

    public ModularWeapon(String name) {
        super(name);
        selectionSize = 32;
    }

    @Override
    public ContentType getContentType() {
        return ContentType.valueOf("modularWeapon");
    }

    @Override
    public void loadIcon() {
        super.loadIcon();
    }

    public boolean isBanned() {
        return super.isBanned();
    }

    public boolean isOnPlanet(@Nullable Planet planet){
        return super.isOnPlanet(planet);
    }

    public String displayDescription() {
        return super.displayDescription();
    }


    /** 检查状态初始化状态，在显示状态前调用 */
    public void checkStats(){
        if(!stats.intialized){
            setStats();
            stats.intialized = true;
        }
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

    public String emoji() {
        return super.emoji();
    }

    public int emojiChar() {
        return super.emojiChar();
    }


    public boolean hasEmoji() {
        return super.hasEmoji();
    }

    /** 遍历此内容的任何隐式依赖项  对于方块/模块化武器来说，这将是构建它所需的物品 */
    public void getDependencies(Cons<UnlockableContent> cons){

    }

    /** 当此内容解锁时调用。使用此功能解锁其他相关内容 */
    public void onUnlock(){
    }

    /** 此内容是否始终隐藏在核心数据库中 */
    public boolean isHidden(){
        return false;
    }

    /** @return 是否在解锁时显示通知提示框 */
    public boolean showUnlock(){
        return true;
    }


    /** 使这篇内容解锁；如果已经解锁，则不会有任何操作 */
    public void unlock(){
        super.unlock();
    }

    /** 解锁此内容，但不触发任何事件 */
    public void quietUnlock(){
        super.quietUnlock();
    }

    public boolean unlockedNowHost(){
        return super.unlockedNowHost();
    }

    /** @return 在多人游戏中，这是否为主机玩家解锁，否则，这是否为本地玩家解锁（与unlocked()相同） */
    public boolean unlockedHost(){
        return super.unlockedHost();
    }

    /** @return 无论这个内容是否解锁，还是玩家在进行自定义（非战役）游戏 */
    public boolean unlockedNow(){
        return super.unlockedNow();
    }

    public boolean unlocked() {
        return super.unlocked();
    }

    /** 再次锁定此内容 */
    public void clearUnlock(){
        super.clearUnlock();
    }

    public boolean locked(){
        return !unlocked();
    }

    public enum MWeaponCat {
        assistant, weapon
    }
}