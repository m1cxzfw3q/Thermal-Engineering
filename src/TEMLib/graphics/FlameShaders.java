package TEMLib.graphics;

import arc.*;
import arc.files.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.math.geom.*;
import arc.struct.*;
import static mindustry.Vars.*;

public class FlameShaders{ //FO的渲染，不用的东西全砍掉了（）
    public static AlphaCut alphaCut;
    public static ChainShader chainShader;

    static String defaultVert = """
                    attribute vec4 a_position;
                    attribute vec4 a_color;
                    attribute vec2 a_texCoord0;
                    attribute vec4 a_mix_color;
                    uniform mat4 u_projTrans;
                    varying vec4 v_color;
                    varying vec4 v_mix_color;
                    varying vec2 v_texCoords;

                    void main(){
                       v_color = a_color;
                       v_color.a = v_color.a * (255.0/254.0);
                       v_mix_color = a_mix_color;
                       v_mix_color.a *= (255.0/254.0);
                       v_texCoords = a_texCoord0;
                       gl_Position = u_projTrans * a_position;
                    }""";

    public static void load(){
        alphaCut = new AlphaCut();
        chainShader = new ChainShader();
    }

    public static Fi file(String path){
        return tree.get("shaders/" + path);
    }
    public static Fi intFile(String path){
        return Core.files.internal("shaders/" + path);
    }

    public static class AlphaCut extends Shader{
        AlphaCut(){
            super(defaultVert, file("alphacut.frag").readString());
        }
    }

    public static class ChainShader extends Shader{
        public TextureRegion region;
        public float length = 0f;

        ChainShader(){
            super(defaultVert, file("chain.frag").readString());
        }

        @Override
        public void apply(){
            setUniformf("u_uv", region.u, region.v);
            setUniformf("u_uv2", region.u2, region.v2);
            setUniformf("u_length", length);
            setUniformf("u_texlen", region.width * Draw.scl);
        }
    }
}
