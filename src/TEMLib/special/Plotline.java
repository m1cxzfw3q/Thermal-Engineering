package TEMLib.special;

import arc.Core;
import arc.scene.Element;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.ui.Styles;

public class Plotline {
    /// 剧情段
    public Seq<Plot> plots = new Seq<>();
    /// 统一等待时长(刻)
    public int uniDelay = 60;

    private Element table;

    public static class Plot {
        /// 内部名
        public static String name = "name",
        /// 显示名
        displayName,
        /// 内容
        text = "test";
        /// 独立等待时长(刻)  -1禁用
        public static int delay = -1;

        public Plot() {}
        public Plot(String name) {
            Plot.name = name;
            displayName = Core.bundle.format("plot." + name + ".name");
            text = Core.bundle.format("plot." + name + ".text");
        }
        public Plot(String name, int delay) {
            this(name);
            Plot.delay = delay;
        }
    }

    public Plotline() {}
    public Plotline(int uniDelay, Plot... plots) {
        this.uniDelay = uniDelay <= 0 ? 60 : uniDelay;
        this.plots = new Seq<>(plots);
    }

    public void show() {
        table.visible(() -> true);
    }
    public void hide() {
        table.visible(() -> false);
    }
    public void initialize() {
        Vars.ui.hudGroup.fill(t -> table = t.table(Styles.black5).get());
        hide();
    }
}
