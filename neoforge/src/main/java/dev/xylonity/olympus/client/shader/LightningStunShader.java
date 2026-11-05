package dev.xylonity.olympus.client.shader;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.shaders.Uniform;
import dev.xylonity.knightlib.client.shader.post.AbstractPostShader;
import dev.xylonity.knightlib.client.shader.post.interop.PostShaderRenderContext;
import dev.xylonity.knightlib.client.shader.post.interop.PostShaderRenderStage;
import dev.xylonity.knightlib.client.shader.post.interop.PostShaderSettings;
import dev.xylonity.knightlib.mixin.PostChainAccessor;
import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.registry.OlympusMobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public final class LightningStunShader extends AbstractPostShader<PostShaderSettings> {

    public static final LightningStunShader INSTANCE = new LightningStunShader();

    private static final ResourceLocation ID = Olympus.of("lightning_stun");
    private static final ResourceLocation POST_EFFECT = Olympus.of("shaders/post/lightning_stun.json");

    private static final int FADE_OUT_TICKS = 12;

    private static final int MAX_SPARKS = 32;
    private static final int STRIKE_SPARKS = 16;

    private final RandomSource random = RandomSource.create();
    private final List<ScreenSpark> sparks = new ArrayList<>();
    private final Uniform[] sparkUniforms = new Uniform[MAX_SPARKS];

    private @Nullable PostChain chain;
    private @Nullable EffectInstance effect;

    private boolean active;
    private int stunTicks;
    private int flashTicks;
    private int remainingTicks;
    private float seed;

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public EnumSet<PostShaderRenderStage> stages() {
        return EnumSet.of(PostShaderRenderStage.GAME_RENDERER_TAIL);
    }

    @Override
    public void initOrReload(final TextureManager textures, final ResourceManager resources, final RenderTarget target) {
        super.initOrReload(textures, resources, target);

        closeChain(chain);

        chain = null;
        effect = null;

        try {
            chain = new PostChain(textures, resources, target, POST_EFFECT);
            effect = ((PostChainAccessor) chain).knightlib$getPasses().get(0).getEffect();
            for (int index = 0; index < MAX_SPARKS; index++) {
                sparkUniforms[index] = effect.getUniform("Spark" + index);
            }

        }
        catch (final IOException | JsonSyntaxException exception) {
            Olympus.LOGGER.warn("Failed to load the lightning stun post effect", exception);
        }

    }

    @Override
    public void dispose() {
        closeChain(chain);

        chain = null;
        effect = null;

        super.dispose();
    }

    @Override
    public void clientTick() {
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.isPaused()) {
            return;
        }

        final MobEffectInstance stun = findStun(minecraft.getCameraEntity());
        if (stun != null) {
            final int duration = stun.isInfiniteDuration() ? Integer.MAX_VALUE : stun.getDuration();
            if (!active) {
                active = true;
                stunTicks = 0;
                flashTicks = 0;
                seed = random.nextFloat() * 64.0F;
                sparks.clear();
                spawnSparks(minecraft, STRIKE_SPARKS);
            }
            else {
                stunTicks++;
                flashTicks++;
                if (duration > remainingTicks) {
                    flashTicks = 0;
                    spawnSparks(minecraft, STRIKE_SPARKS / 2);
                }

            }

            remainingTicks = duration;
        }
        else if (active) {
            remainingTicks = Math.min(remainingTicks, FADE_OUT_TICKS) - 1;
            if (remainingTicks <= 0) {
                clear();
                return;
            }

            stunTicks++;
            flashTicks++;
        }
        else {
            return;
        }

        tickSparks(minecraft);
    }

    @Override
    public void renderStage(final PostShaderRenderContext context) {
        if (!active || chain == null || effect == null) {
            return;
        }

        ensureSized(chain);
        process(chain, context.partialTicks, () -> updateUniforms(effect, context.partialTicks));
    }

    @Override
    public void clear() {
        active = false;
        sparks.clear();
    }

    @Override
    public void onWorldUnload() {
        ;;
    }

    private void updateUniforms(final EffectInstance effect, final float partialTick) {
        final Minecraft minecraft = Minecraft.getInstance();
        final float time = stunTicks + partialTick;
        final float flash = 1.0F - Mth.clamp((flashTicks + partialTick) / 7f, 0.0F, 1.0F);

        setUniformFloat(effect, "StunTime", time / 20.0F);
        setUniformFloat(effect, "Intensity", Math.min(smooth(time / 3f), fadeOut(partialTick)));
        setUniformFloat(effect, "Flash", flash * flash);
        setUniformFloat(effect, "Seed", seed);
        // Vanilla accessibility options
        setUniformFloat(effect, "Distortion", minecraft.options.screenEffectScale().get().floatValue());
        setUniformFloat(effect, "Flashes", minecraft.options.hideLightningFlash().get() ? 0.0F : 1.0F);
        // The sparks use the same pixel size as the hud
        setUniformFloat(effect, "PixelSize", (float) minecraft.getWindow().getGuiScale());

        for (int index = 0; index < MAX_SPARKS; index++) {
            final Uniform uniform = sparkUniforms[index];
            if (uniform == null) {
                continue;
            }

            if (index < sparks.size()) {
                final ScreenSpark spark = sparks.get(index);
                uniform.set((float) Mth.lerp(partialTick, spark.previousX, spark.x), (float) Mth.lerp(partialTick, spark.previousY, spark.y), spark.frame(), spark.scale);
            }
            else {
                uniform.set(0f, 0f, -1f, 0f);
            }

        }

    }

    private void tickSparks(final Minecraft minecraft) {
        sparks.removeIf(ScreenSpark::tick);
        final float spawns = 1.2f * fadeOut(0.0F);
        spawnSparks(minecraft, (int) spawns + (random.nextFloat() < spawns - (int) spawns ? 1 : 0));
    }

    private void spawnSparks(final Minecraft minecraft, final int count) {
        final float aspect = minecraft.getWindow().getWidth() / (float) Math.max(minecraft.getWindow().getHeight(), 1);
        for (int index = 0; index < count && sparks.size() < MAX_SPARKS; index++) {
            sparks.add(new ScreenSpark(random, aspect));
        }

    }

    private float fadeOut(final float partialTick) {
        return remainingTicks == Integer.MAX_VALUE ? 1.0F : smooth((remainingTicks - partialTick) / FADE_OUT_TICKS);
    }

    private static @Nullable MobEffectInstance findStun(final @Nullable Entity camera) {
        return camera instanceof LivingEntity living ? living.getEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(OlympusMobEffects.LIGHTNING_STUN.get())) : null;
    }

    private static float smooth(final float value) {
        final float clamped = Mth.clamp(value, 0.0F, 1.0F);
        return clamped * clamped * (3.0F - 2.0F * clamped);
    }

    /// Same logic as the cards from the cornelius crupier game from Companions!
    private static final class ScreenSpark {

        private static final double FRICTION = 0.84D;

        private final int lifetime;
        private final float scale;

        private double x;
        private double y;

        private double previousX;
        private double previousY;
        private double velocityX;
        private double velocityY;

        private int age;

        private ScreenSpark(final RandomSource random, final float aspect) {
            final double halfWidth = aspect * 0.5D;
            double distance = random.nextDouble() * 2.0D * (aspect + 1.0D);
            if (distance < aspect) {
                x = distance - halfWidth;
                y = -0.5D;
            }
            else if ((distance -= aspect) < 1.0D) {
                x = halfWidth;
                y = distance - 0.5D;
            }
            else if ((distance -= 1.0D) < aspect) {
                x = halfWidth - distance;
                y = 0.5D;
            }
            else {
                x = -halfWidth;
                y = 0.5D - (distance - aspect);
            }

            x *= 0.99D;
            y *= 0.99D;
            previousX = x;
            previousY = y;

            // Towards somewhere around the center
            final double targetX = (random.nextDouble() - 0.5D) * aspect * 0.4D;
            final double targetY = (random.nextDouble() - 0.5D) * 0.4D;

            final double share = Mth.lerp(random.nextDouble(), 0.5D, 0.85D) * (1.0D - FRICTION);
            velocityX = (targetX - x) * share;
            velocityY = (targetY - y) * share;

            lifetime = 10 + random.nextInt(8);
            scale = random.nextBoolean() ? 2.0F : 1.0F;
        }

        private boolean tick() {
            previousX = x;
            previousY = y;

            if (age++ >= lifetime) {
                return true;
            }

            x += velocityX;
            y += velocityY;

            velocityX *= FRICTION;
            velocityY *= FRICTION;

            return false;
        }

        private float frame() {
            return Math.min(age * 6 / lifetime, 5);
        }

    }

}