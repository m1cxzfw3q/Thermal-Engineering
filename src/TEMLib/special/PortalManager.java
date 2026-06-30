package TEMLib.special;

import arc.struct.Seq;

// TODO
public class PortalManager {
    public static final Seq<PortalData> data = new Seq<>();

    public static class PortalData {
        public float x1, x2,
                y1, y2,
                rot1, rot2,
                width1, width2;

        public PortalData(float x1, float y1, float rot1, float w1, float x2, float y2, float rot2, float w2) {
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
            this.rot1 = rot1;
            this.rot2 = rot2;
            width1 = w1;
            width2 = w2;
        }
    }
}
