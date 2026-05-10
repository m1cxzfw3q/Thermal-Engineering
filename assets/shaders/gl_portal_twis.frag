// 一样是ds写的
#version 100
precision highp float;
varying vec2 v_texCoord;
uniform sampler2D u_texture;
uniform float u_time;      // 用于动态脉冲
uniform float u_strength;  // 拉伸强度，正数为向外凸，负数为向内凹
uniform vec2 u_center;     // 拉伸中心

void main() {
    vec2 uv = v_texCoord;
    vec2 center = u_center;
    vec2 dir = uv - center;
    float radius = length(dir);
    
    // 避免除以0
    if (radius < 0.01) {
        gl_FragColor = texture2D(u_texture, uv);
        return;
    }
    
    // 动态因子（可选）：随时间脉冲波动
    float pulse = 0.8 + 0.4 * sin(u_time * 2.0);
    float strength = u_strength * pulse;
    
    // 径向拉伸：新半径 = 原半径 + strength * (1.0 - radius)  (在边缘拉伸较弱)
    // 也可以使用幂函数： newRadius = pow(radius, 1.0 + strength) 等
    float newRadius = radius + strength * (1.0 - radius);
    // 或者更简单的线性向外拉伸： newRadius = radius * (1.0 + strength * (1.0 - radius))
    
    vec2 newDir = normalize(dir) * newRadius;
    vec2 newUv = center + newDir;
    
    // 边界clamp
    gl_FragColor = texture2D(u_texture, clamp(newUv, 0.0, 1.0));
}
