package dev.xylonity.olympus.client.event;

import dev.xylonity.knightlib.network.ClientPacketDispatcher;
import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.client.entity.renderer.AbsorbedSoulEntityRenderer;
import dev.xylonity.olympus.client.entity.renderer.HarpyEntityRenderer;
import dev.xylonity.olympus.client.entity.renderer.HarpyProjectileEntityRenderer;
import dev.xylonity.olympus.client.entity.renderer.PoseidonTridentEntityRenderer;
import dev.xylonity.olympus.client.entity.renderer.SpearOfAresEntityRenderer;
import dev.xylonity.olympus.client.entity.renderer.SummoningSpearsEntityRenderer;
import dev.xylonity.olympus.client.item.model.BracersOfZeusModel;
import dev.xylonity.olympus.client.item.model.HelmetOfHadesModel;
import dev.xylonity.olympus.client.item.model.HermesSandalsModel;
import dev.xylonity.olympus.client.item.renderer.BracersOfZeusRenderer;
import dev.xylonity.olympus.client.item.renderer.HelmetOfHadesRenderer;
import dev.xylonity.olympus.client.item.renderer.HermesSandalsRenderer;
import dev.xylonity.olympus.client.item.renderer.PoseidonTridentItemRenderer;
import dev.xylonity.olympus.client.item.renderer.SpearOfAresItemRenderer;
import dev.xylonity.olympus.client.particle.AresSpearHitParticle;
import dev.xylonity.olympus.client.particle.AresSpearTraceParticle;
import dev.xylonity.olympus.client.particle.ArtemisLeafParticle;
import dev.xylonity.olympus.client.particle.ForgingSparkParticle;
import dev.xylonity.olympus.client.particle.HarpyFeatherParticle;
import dev.xylonity.olympus.client.particle.HarpyMagicParticle;
import dev.xylonity.olympus.client.particle.HarpyProjectileTrailParticle;
import dev.xylonity.olympus.client.particle.LightningBoltParticle;
import dev.xylonity.olympus.client.particle.LightningSparksParticle;
import dev.xylonity.olympus.client.particle.LyreNoteParticle;
import dev.xylonity.olympus.client.particle.PoppyGrowthParticle;
import dev.xylonity.olympus.client.particle.SoulSalvationParticle;
import dev.xylonity.olympus.client.particle.SoulTrailParticle;
import dev.xylonity.olympus.client.particle.TridentSplashParticle;
import dev.xylonity.olympus.client.particle.TridentUnderwaterSplashParticle;
import dev.xylonity.olympus.client.particle.TridentWaterDropParticle;
import dev.xylonity.olympus.client.sound.LyreMusicSound;
import dev.xylonity.olympus.client.texture.SpearDissolveTextures;
import dev.xylonity.olympus.common.entity.projectile.AbsorbedSoulEntity;
import dev.xylonity.olympus.common.entity.projectile.HarpyProjectileEntity;
import dev.xylonity.olympus.common.item.HermesSandalsItem;
import dev.xylonity.olympus.network.OlympusNetwork;
import dev.xylonity.olympus.network.payload.HermesJumpPayload;
import dev.xylonity.olympus.network.payload.LightningBoltPayload;
import dev.xylonity.olympus.network.payload.LyreMusicPayload;
import dev.xylonity.olympus.network.payload.SoulSalvationPayload;
import dev.xylonity.olympus.registry.OlympusEntities;
import dev.xylonity.olympus.registry.OlympusBlocks;
import dev.xylonity.olympus.registry.OlympusItems;
import dev.xylonity.olympus.registry.OlympusMobEffects;
import dev.xylonity.olympus.registry.OlympusParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@Mod.EventBusSubscriber(modid = Olympus.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class OlympusClientEvents {

    private static final ResourceLocation LIGHTNING_STUN_POST_EFFECT = Olympus.of("shaders/post/lightning_stun.json");

    private static int hermesExtraJumps;
    private static boolean hermesJumpKeyDown;

    private static boolean lightningStunEffectActive;

    private static BracersOfZeusRenderer bracersRenderer;

    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(OlympusItems.APHRODITE_LYRE.get(), Olympus.of("playing"),
                    (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
            ItemProperties.register(OlympusItems.BOW_OF_ARTEMIS.get(), new ResourceLocation("pulling"),
                    (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
            ItemProperties.register(OlympusItems.BOW_OF_ARTEMIS.get(), new ResourceLocation("pull"),
                    (stack, level, entity, seed) -> entity != null && entity.getUseItem() == stack
                            ? (stack.getUseDuration() - entity.getUseItemRemainingTicks()) / 20.0F
                            : 0.0F);

            ItemBlockRenderTypes.setRenderLayer(OlympusBlocks.AIR_CLOUD_BLOCK.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(OlympusBlocks.LOCKED_CHEST.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(OlympusBlocks.PARTHENON_SPAWNER.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(OlympusBlocks.CLIMBING_ROSE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(OlympusBlocks.POPPY_OF_DEMETER.get(), RenderType.cutout());

            CuriosRendererRegistry.register(OlympusItems.BRACERS_OF_ZEUS.get(), () -> {
                bracersRenderer = new BracersOfZeusRenderer();
                return bracersRenderer;
            });

            CuriosRendererRegistry.register(OlympusItems.HELMET_OF_HADES.get(), HelmetOfHadesRenderer::new);
            CuriosRendererRegistry.register(OlympusItems.HERMES_SANDALS.get(), HermesSandalsRenderer::new);
        });

        final Minecraft minecraft = Minecraft.getInstance();
        ClientPacketDispatcher.register(LightningBoltPayload.class, payload -> {
            if (minecraft.level != null) {
                minecraft.particleEngine.add(new LightningBoltParticle(minecraft.level, payload.start(), payload.end(), payload.skyStrike()));
            }

        });
        ClientPacketDispatcher.register(LyreMusicPayload.class, payload -> {
            if (payload.playing()) {
                LyreMusicSound.start(payload.musicianId());
            }
            else {
                LyreMusicSound.fadeOut(payload.musicianId());
            }

        });
        ClientPacketDispatcher.register(SoulSalvationPayload.class, payload -> {
            if (minecraft.level == null) {
                return;
            }

            final Entity entity = minecraft.level.getEntity(payload.entityId());
            if (entity == null) {
                return;
            }

            final int count = payload.particleCount();
            final Vec3 position = payload.sphericalBurst() ? entity.getBoundingBox().getCenter() : new Vec3(entity.getX(), entity.getY() + 0.2D, entity.getZ());
            final RandomSource random = minecraft.level.getRandom();
            for (int direction = 0; direction < count; direction++) {
                final double x;
                final double y;
                final double z;
                // Particle direction in a spherical-like shape
                if (payload.sphericalBurst()) {
                    final double vertical = random.nextDouble() * 2.0D - 1.0D;
                    final double horizontal = Math.sqrt(1.0D - vertical * vertical);
                    final double angle = random.nextDouble() * Math.PI * 2.0D;
                    final double speed = 0.08D + random.nextDouble() * 0.04D;
                    x = Math.cos(angle) * horizontal * speed;
                    y = vertical * speed;
                    z = Math.sin(angle) * horizontal * speed;
                }
                // Relative random direction
                else {
                    final double angle = Math.PI * 2.0D * direction / count;
                    x = Math.cos(angle) * 0.23225D;
                    y = 0.0D;
                    z = Math.sin(angle) * 0.23225D;
                }

                minecraft.level.addParticle(OlympusParticles.SOUL_SALVATION.get(), position.x, position.y, position.z, x, y, z);
            }

        });

    }

    @SubscribeEvent
    public static void registerClientReloadListeners(final RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(SpearDissolveTextures.INSTANCE);
    }

    @SubscribeEvent
    public static void registerAdditionalModels(final ModelEvent.RegisterAdditional event) {
        event.register(PoseidonTridentItemRenderer.INVENTORY_MODEL);
        event.register(SpearOfAresItemRenderer.INVENTORY_MODEL);
        event.register(SpearOfAresItemRenderer.CHARGED_INVENTORY_MODEL);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(OlympusEntities.ABSORBED_SOUL.get(), AbsorbedSoulEntityRenderer::new);
        event.registerEntityRenderer(OlympusEntities.POSEIDON_TRIDENT.get(), PoseidonTridentEntityRenderer::new);
        event.registerEntityRenderer(OlympusEntities.SPEAR_OF_ARES.get(), SpearOfAresEntityRenderer::new);
        event.registerEntityRenderer(OlympusEntities.SUMMONING_SPEARS.get(), SummoningSpearsEntityRenderer::new);
        event.registerEntityRenderer(OlympusEntities.HARPY.get(), HarpyEntityRenderer::new);
        event.registerEntityRenderer(OlympusEntities.ELITE_HARPY.get(), context -> new HarpyEntityRenderer(context, true));
        event.registerEntityRenderer(OlympusEntities.HARPY_PROJECTILE.get(), HarpyProjectileEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BracersOfZeusModel.SLIM_LAYER_LOCATION, BracersOfZeusModel::createSlimBodyLayer);
        event.registerLayerDefinition(BracersOfZeusModel.WIDE_LAYER_LOCATION, BracersOfZeusModel::createWideBodyLayer);
        event.registerLayerDefinition(HelmetOfHadesModel.LAYER_LOCATION, HelmetOfHadesModel::createBodyLayer);
        event.registerLayerDefinition(HermesSandalsModel.LAYER_LOCATION, HermesSandalsModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerGuiLayers(final RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.VIGNETTE.id(), "hades_screen_filter",
                (forgeGui, guiGraphics, partialTick, width, height) -> {
                    final Minecraft minecraft = Minecraft.getInstance();
                    if (minecraft.player == null || !minecraft.player.hasEffect(OlympusMobEffects.INVISIBILITY_OF_HADES.get())) {
                        return;
                    }

                    guiGraphics.blit(Olympus.of("textures/gui/invisibility_of_hades_screen_filter.png"), 0, 0, width, height, 0.0F, 0.0F, 256, 256, 256, 256);
                }

        );

    }

    @SubscribeEvent
    public static void registerParticleProviders(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(OlympusParticles.ARES_SPEAR_TRACE.get(), AresSpearTraceParticle.Provider::new);
        event.registerSpriteSet(OlympusParticles.ARES_SPEAR_HIT.get(), AresSpearHitParticle.Provider::new);
        event.registerSpriteSet(OlympusParticles.LYRE_NOTE.get(), LyreNoteParticle.Provider::new);
        event.registerSpriteSet(OlympusParticles.SOUL_SALVATION.get(), SoulSalvationParticle.Provider::new);
        event.registerSpriteSet(OlympusParticles.POPPY_GROWTH.get(), PoppyGrowthParticle.Provider::new);
        event.registerSpriteSet(OlympusParticles.LIGHTNING_SPARKS.get(), LightningSparksParticle.Provider::new);
        event.registerSpriteSet(OlympusParticles.HARPY_MAGIC.get(), HarpyMagicParticle.Provider::new);
        event.registerSpriteSet(OlympusParticles.HARPY_FEATHER.get(), HarpyFeatherParticle.Provider::new);
        event.registerSpriteSet(OlympusParticles.ELITE_HARPY_FEATHER.get(), HarpyFeatherParticle.Provider::new);
        event.registerSpriteSet(OlympusParticles.TRIDENT_SPLASH_OF_WATER.get(), TridentSplashParticle.MainProvider::new);
        event.registerSpriteSet(OlympusParticles.TRIDENT_WATER_DROP.get(), TridentWaterDropParticle.Provider::new);
        event.registerSpriteSet(OlympusParticles.TRIDENT_SMALL_SPLASH_OF_WATER.get(), TridentSplashParticle.SmallProvider::new);
        event.registerSpriteSet(OlympusParticles.FORGING_SPARK.get(), ForgingSparkParticle.Provider::new);
        event.registerSpecial(OlympusParticles.TRIDENT_UNDERWATER_SPLASH.get(), new TridentUnderwaterSplashParticle.Provider());
        event.registerSpriteSet(OlympusParticles.ARTEMIS_ARROW_TRACE.get(), ArtemisLeafParticle.MainProvider::new);
        event.registerSpriteSet(OlympusParticles.ARTEMIS_ARROW_TRACE_SMALL.get(), ArtemisLeafParticle.SmallProvider::new);
    }

    @Mod.EventBusSubscriber(modid = Olympus.MOD_ID, value = Dist.CLIENT)
    public static final class ForgeEvents {

        @SubscribeEvent
        public static void onInteractionKeyMapping(final InputEvent.InteractionKeyMappingTriggered event) {
            final Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null) {
                return;
            }

            if (event.isUseItem()) {
                if (minecraft.player.getItemInHand(event.getHand()).is(OlympusItems.SPEAR_OF_ARES.get())) {
                    // Doesn't play spear attack animation on generic interaction
                    event.setSwingHand(false);
                }

                return;
            }

            if (!event.isAttack() || !minecraft.player.getMainHandItem().is(OlympusItems.SPEAR_OF_ARES.get())) {
                return;
            }

            if (minecraft.player.getAttackStrengthScale(0.0F) < 1.0F) {
                event.setSwingHand(false);
                event.setCanceled(true);
                return;
            }

            if (minecraft.hitResult != null && minecraft.hitResult.getType() == HitResult.Type.BLOCK) {
                event.setSwingHand(false);
                event.setCanceled(true);
                minecraft.player.resetAttackStrengthTicker();
                minecraft.player.swing(InteractionHand.MAIN_HAND);
            }

        }

        @SubscribeEvent
        public static void onHermesClientTick(final TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.START) {
                return;
            }

            final Minecraft minecraft = Minecraft.getInstance();
            // Restarts the jump amount counter
            if (minecraft.player == null || minecraft.level == null) {
                hermesExtraJumps = HermesSandalsItem.getExtraJumps();
                hermesJumpKeyDown = false;
                return;
            }

            // Restarts the jump amount counter
            final boolean jumpKeyDown = minecraft.options.keyJump.isDown();
            if (minecraft.player.onGround()) {
                hermesExtraJumps = HermesSandalsItem.getExtraJumps();
            }

            // The player jumps
            if (minecraft.screen == null && jumpKeyDown && !hermesJumpKeyDown && hermesExtraJumps > 0 && HermesSandalsItem.canExtraJump(minecraft.player)) {
                hermesExtraJumps--;
                minecraft.player.jumpFromGround();
                OlympusNetwork.ENDPOINT.sendToServer(HermesJumpPayload.INSTANCE);
            }

            hermesJumpKeyDown = jumpKeyDown;
        }

        @SubscribeEvent
        public static void onEntityJoinLevel(final EntityJoinLevelEvent event) {
            if (event.getEntity() instanceof AbsorbedSoulEntity soul && event.getLevel() instanceof ClientLevel level) {
                Minecraft.getInstance().particleEngine.add(new SoulTrailParticle(level, soul));
            }
            else if (event.getEntity() instanceof HarpyProjectileEntity projectile && event.getLevel() instanceof ClientLevel level) {
                Minecraft.getInstance().particleEngine.add(new HarpyProjectileTrailParticle(level, projectile));
            }

        }

        @SubscribeEvent
        public static void onClientTick(final TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }

            final Minecraft minecraft = Minecraft.getInstance();
            final boolean stunned = minecraft.getCameraEntity() instanceof LivingEntity livingEntity
                    && livingEntity.hasEffect(OlympusMobEffects.LIGHTNING_STUN.get());

            // Handles the screen post shader when the lightning stun effect is active
            if (stunned && !lightningStunEffectActive) {
                minecraft.gameRenderer.loadEffect(LIGHTNING_STUN_POST_EFFECT);
                lightningStunEffectActive = minecraft.gameRenderer.currentEffect() != null;
            }
            else if (!stunned && lightningStunEffectActive) {
                minecraft.gameRenderer.shutdownEffect();
                lightningStunEffectActive = false;
            }

        }

        @SubscribeEvent
        public static void onRenderArm(final RenderArmEvent event) {
            if (bracersRenderer == null) {
                return;
            }

            CuriosApi.getCuriosInventory(event.getPlayer()).resolve()
                    .flatMap(handler -> handler.findFirstCurio(OlympusItems.BRACERS_OF_ZEUS.get()))
                    .ifPresent(slot -> bracersRenderer.renderFirstPersonHand(slot.stack(), event.getArm(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), event.getPlayer()));
        }

    }

}