#version 330

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:dynamictransforms.glsl>

uniform sampler2D Sampler0;

in float sphericalVertexDistance;
in float cylindricalVertexDistance;
in vec4 vertexColor;
in vec4 lightMapColor;
in vec4 overlayColor;
in vec2 texCoord0;

out vec4 fragColor;

// Returns a random value for each texture pixel
// Pseudorandom code from: https://stackoverflow.com/questions/12964279/whats-the-origin-of-this-glsl-rand-one-liner
float getDissolveThreshold(vec2 texturePixel) {
    return fract(sin(dot(texturePixel, vec2(12.9898, 78.233))) * 43758.5453);
}

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    if (color.a < 0.001) {
        discard;
    }

    // Finds the current pixel in the texture
    vec2 texturePixel = floor(texCoord0 * vec2(textureSize(Sampler0, 0)));

    // Removes pixels as the spear dissolves
    vec4 faceVertexColor = vertexColor;
    if (faceVertexColor.a <= getDissolveThreshold(texturePixel)) {
        discard;
    }

    // Extra tint, overlays, etc
    faceVertexColor.a = 1.0;
    color *= faceVertexColor * ColorModulator;
    color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);
    color *= lightMapColor;
    fragColor = apply_fog(color, sphericalVertexDistance, cylindricalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
}