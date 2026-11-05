// Based off my own implementation of a screen post shader for an electrical stun effect
// https://github.com/Xylonity/Hostiles/blob/v1.20.1/common/src/main/resources/assets/hostiles/shaders/program/polarity/polarity.vsh

#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D SparkSampler;

uniform vec2 OutSize;

uniform float StunTime;
uniform float Intensity;
uniform float Flash;
uniform float Seed;
uniform float Distortion;
uniform float Flashes;
uniform float PixelSize;

uniform vec4 Spark0;
uniform vec4 Spark1;
uniform vec4 Spark2;
uniform vec4 Spark3;
uniform vec4 Spark4;
uniform vec4 Spark5;
uniform vec4 Spark6;
uniform vec4 Spark7;
uniform vec4 Spark8;
uniform vec4 Spark9;
uniform vec4 Spark10;
uniform vec4 Spark11;
uniform vec4 Spark12;
uniform vec4 Spark13;
uniform vec4 Spark14;
uniform vec4 Spark15;
uniform vec4 Spark16;
uniform vec4 Spark17;
uniform vec4 Spark18;
uniform vec4 Spark19;
uniform vec4 Spark20;
uniform vec4 Spark21;
uniform vec4 Spark22;
uniform vec4 Spark23;
uniform vec4 Spark24;
uniform vec4 Spark25;
uniform vec4 Spark26;
uniform vec4 Spark27;
uniform vec4 Spark28;
uniform vec4 Spark29;
uniform vec4 Spark30;
uniform vec4 Spark31;

in vec2 texCoord;

out vec4 fragColor;

const int SPARK_COUNT = 32;
const int SPECTRAL_TAPS = 8;
const float TAU = 6.2831853;

// Palette
const vec3 GOLD = vec3(1.0, 0.843, 0.204);
const vec3 PALE = vec3(1.0, 0.984, 0.839);

const vec3 SHADOW_TONE = vec3(0.84, 0.88, 1.12);
const vec3 HIGHLIGHT_TONE = vec3(1.08, 1.02, 0.86);

// Noise https://www.shadertoy.com/view/4djSRW
float hash11(float p) {
    p = fract(p * 0.1031);
    p *= p + 33.33;
    p *= p + p;
    return fract(p);
}

vec2 hash22(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * vec3(0.1031, 0.1030, 0.0973));
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.xx + p3.yz) * p3.zy);
}

vec3 spectrum(float t) {
    return clamp(1.5 - abs(4.0 * t - vec3(1.0, 2.0, 3.0)), 0.0, 1.0);
}

void main() {
    float aspect = OutSize.x / max(OutSize.y, 1.0);
    vec2 center = vec2(0.5);
    float time = StunTime;
    float distortion = Intensity * Distortion;

    // Particle (electric jolts) movement
    float joltClock = time * 12.0;
    float joltStep = floor(joltClock);
    float joltSnap = smoothstep(0.0, 0.3, fract(joltClock));
    vec2 joltFrom = hash22(vec2(joltStep, Seed)) * 2.0 - 1.0;
    vec2 joltTo = hash22(vec2(joltStep + 1.0, Seed)) * 2.0 - 1.0;

    // Occasional stronger shocks
    float spike = step(0.8, hash11(joltStep * 7.13 + Seed));
    vec2 jolt = mix(joltFrom, joltTo, joltSnap) * (0.0018 + spike * 0.0032 + Flash * 0.006);
    jolt.x /= aspect;
    vec2 uv = texCoord + jolt * distortion;

    // Punch in on the strike
    uv = center + (uv - center) * (1.0 - 0.025 * Flash * Distortion);

    // Old impl
    vec2 fromCenter = uv - center;
    float split = (0.006 + 0.004 * sin(time * 7.0) + spike * 0.01 + Flash * 0.025) * distortion;
    vec3 sum = vec3(0.0);
    vec3 weights = vec3(0.0);
    for (int tap = 0; tap < SPECTRAL_TAPS; tap++) {
        float t = (float(tap) + 0.5) / float(SPECTRAL_TAPS);
        vec3 weight = spectrum(t);
        sum += texture(DiffuseSampler, center + fromCenter * (1.0 - split * (t - 0.5))).rgb * weight;
        weights += weight;
    }

    vec3 color = sum / weights;

    // Double vision
    float ghostClock = time * 1.5 + 0.3;
    float ghostStep = floor(ghostClock);
    float ghostLife = fract(ghostClock);
    float ghostActive = step(hash11(ghostStep * 3.1 + Seed), 0.55);
    float ghostAngle = hash11(ghostStep * 5.7 + Seed) * TAU;
    float ghostDrift = smoothstep(0.0, 0.1, ghostLife) * exp(-ghostLife * 3.0);
    vec2 ghostOffset = vec2(cos(ghostAngle) / aspect, sin(ghostAngle)) * (0.014 + Flash * 0.02) * ghostDrift;
    vec3 ghost = texture(DiffuseSampler, uv + ghostOffset * distortion).rgb;
    color = mix(color, ghost, 0.35 * ghostActive * min(ghostDrift * 1.6, 1.0) * distortion);

    float luma = dot(color, vec3(0.2126, 0.7152, 0.0722));
    color = mix(color, vec3(luma), 0.1 * Intensity);
    vec3 tone = mix(SHADOW_TONE, HIGHLIGHT_TONE, smoothstep(0.1, 0.9, luma));
    color = clamp(mix(color, color * tone * 0.94, Intensity * 0.8), 0.0, 1.0);
    color = mix(color, color * color * (3.0 - 2.0 * color), 0.25 * Intensity);

    vec2 halfSize = vec2(aspect, 1.0) * 0.5;
    vec2 p = (texCoord - center) * vec2(aspect, 1.0);
    float vignette = smoothstep(0.5, 1.35, length(p / halfSize));
    color *= 1.0 - vignette * 0.35 * Intensity;
    color += GOLD * vignette * vignette * 0.05 * Intensity;

    // Sparks
    float pixelSize = max(floor(PixelSize + 0.5), 1.0);
    vec2 fragPixel = texCoord * OutSize;
    vec4 sparks[SPARK_COUNT] = vec4[SPARK_COUNT](Spark0, Spark1, Spark2, Spark3, Spark4, Spark5, Spark6, Spark7, Spark8, Spark9, Spark10, Spark11, Spark12, Spark13, Spark14, Spark15, Spark16, Spark17, Spark18, Spark19, Spark20, Spark21, Spark22, Spark23, Spark24, Spark25, Spark26, Spark27, Spark28, Spark29, Spark30, Spark31);
    vec3 sparkColor = vec3(0.0);
    float sparkAlpha = 0.0;
    float bloom = 0.0;
    for (int spark = 0; spark < SPARK_COUNT; spark++) {
        vec4 data = sparks[spark];
        if (data.z < 0.0) {
            continue;
        }

        float texel = pixelSize * data.w;
        vec2 originPixel = floor((data.xy / vec2(aspect, 1.0) + center) * OutSize / pixelSize) * pixelSize;
        vec2 spriteTexel = (fragPixel - originPixel) / texel + 8.0;
        float spriteDistance = length(spriteTexel - 8.0);
        if (spriteDistance > 24.0) {
            continue;
        }

        // Soft light around it
        bloom += exp(-spriteDistance / 3.0) * (1.0 - data.z / 6.0) * 0.25;
        if (spriteTexel.x < 0.0 || spriteTexel.y < 0.0 || spriteTexel.x >= 16.0 || spriteTexel.y >= 16.0) {
            continue;
        }

        ivec2 texelCoord = ivec2(spriteTexel);
        vec4 sprite = texelFetch(SparkSampler, ivec2(int(data.z) * 16 + texelCoord.x, 15 - texelCoord.y), 0);
        if (sprite.a > 0.5) {
            sparkColor = sprite.rgb;
            sparkAlpha = 1.0;
        }

    }

    color += GOLD * min(bloom, 1.0) * Intensity;
    color = mix(color, sparkColor, sparkAlpha);

    float flash = Flash * Flashes;
    color = mix(color, PALE, flash * 0.18);
    color += color * flash * 0.3 + GOLD * flash * 0.05;

    fragColor = vec4(clamp(color, 0.0, 1.0), 1.0);
}