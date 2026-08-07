package dev.xylonity.olympus.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.olympus.registry.OlympusRenderTypes;
import dev.xylonity.olympus.client.util.LightningRenderUtil;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.ParticleGroupRenderState;

public final class LightningParticleGroup extends ParticleGroup<LightningBoltParticle> {

    public static final ParticleRenderType TYPE = new ParticleRenderType("OLYMPUS_LIGHTNING_BOLTS");

    public LightningParticleGroup(final ParticleEngine engine) {
        super(engine);
    }

    @Override
    public ParticleGroupRenderState extractRenderState(
            final Frustum frustum,
            final Camera camera,
            final float partialTickTime
    ) {
        final List<LightningBoltParticle.RenderSnapshot> snapshots = particles.stream()
                .filter(particle -> frustum.isVisible(particle.getBoundingBox()))
                .map(particle -> particle.extractSnapshot(camera, partialTickTime))
                .filter(snapshot -> snapshot != null)
                .toList();

        return new State(snapshots);
    }

    private record State(
            List<LightningBoltParticle.RenderSnapshot> snapshots
    ) implements ParticleGroupRenderState {

        @Override
        public void submit(final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
            if (snapshots.isEmpty()) {
                return;
            }

            submitNodeCollector.submitCustomGeometry(
                    new PoseStack(),
                    OlympusRenderTypes.lightningBolt(),
                    (pose, buffer) -> snapshots.forEach(snapshot -> LightningRenderUtil.render(pose.pose(), buffer, snapshot))
            );

        }

    }

}