package dev.xylonity.olympus.client.particle;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/// Based off my own implementation
/// https://github.com/Xylonity/Hostiles/blob/v1.20.1/common/src/main/java/dev/xylonity/hostiles/client/particle/GroundToTargetLightningParticle.java
public final class LightningBoltParticle extends Particle {

    private final Vec3 start;
    private final Vec3 end;

    private final boolean skyStrike;

    private final long seed;

    private final double zigzagAngle;
    private final int hopCount;
    private final double amplitude;
    private final float baseWidth;

    private final double[] hopProgress;
    private final double[] angles;
    private final double[] radiusScales;
    private final double[] sideOffsets;
    private final double[] normalOffsets;

    private List<Vec3> path;

    public LightningBoltParticle(final ClientLevel level, final Vec3 start, final Vec3 end, final boolean skyStrike) {
        super(level, start.x, start.y, start.z);
        this.start = start;
        this.end = end;
        this.skyStrike = skyStrike;
        this.seed = random.nextLong();

        // Hashing function
        // https://github.com/bcgit/bc-java/blob/main/core/src/main/java/org/bouncycastle/crypto/digests/Blake2bDigest.java
        this.zigzagAngle = unitRandom(mix64(seed ^ 0x510E527FADE682D1L)) * Math.PI * 2.0D;

        final double distance = Math.max(1.0D, start.distanceTo(end));
        this.hopCount = skyStrike ? Mth.clamp((int) Math.ceil(distance / 65.0D) + 3, 7, 9) : Mth.clamp((int) Math.ceil(distance / 2.4D) + 2, 5, 7);
        this.amplitude = skyStrike ? Mth.clamp(distance * 0.115D, 9, 30) : Mth.clamp(distance * 0.18D, 0.55D, 1.35D);
        this.baseWidth = skyStrike ? 0.25F : 0.045F;

        this.hopProgress = new double[hopCount];
        this.angles = new double[hopCount];
        this.radiusScales = new double[hopCount];
        this.sideOffsets = new double[hopCount];
        this.normalOffsets = new double[hopCount];

        initializeKnots();
        updateOffsets(0, true);
        this.path = buildPath();

        this.hasPhysics = false;
        this.lifetime = 12;

        updateBounds();
    }

    @Override
    public void tick() {
        if (age++ >= lifetime) {
            remove();
            return;
        }

        if ((age & 1) == 0) {
            updateOffsets(age / 2, false);
            path = buildPath();
        }

    }

    @Override
    public @NonNull ParticleRenderType getGroup() {
        return LightningParticleGroup.TYPE;
    }

    public @Nullable RenderSnapshot extractSnapshot(final Camera camera, final float partialTick) {
        if ((age & 1) != 0) {
            return null;
        }

        // Follows the camera rotation so it's not a plane stuck in a single rotation
        final float life = Mth.clamp((age + partialTick) / lifetime, 0, 1);
        final float width = baseWidth * Mth.lerp(life, 1.85F, 0.12F);
        final float alpha = 1.0F - life * life;
        final Vec3 cameraPos = camera.position();
        final List<Vec3> cameraPath = path.stream().map(point -> point.subtract(cameraPos)).toList();
        return new RenderSnapshot(cameraPath, width, alpha);
    }

    private void initializeKnots() {
        final RandomSource random = RandomSource.create(seed);
        final double[] sectionLengths = new double[hopCount - 1];
        double totalLength = 0;

        // Slightly uneven sections
        for (int index = 0; index < sectionLengths.length; index++) {
            sectionLengths[index] = 0.75D + random.nextDouble() * 0.5D;
            totalLength += sectionLengths[index];
        }

        hopProgress[0] = 0;
        double progress = 0;
        for (int index = 1; index < hopCount; index++) {
            progress += sectionLengths[index - 1] / totalLength;
            hopProgress[index] = progress;
        }

        hopProgress[hopCount - 1] = 1.0D;

        // Whether it's right or left
        final double whatDirection = random.nextBoolean() ? 1 : -1;

        double angle = zigzagAngle;
        for (int index = 1; index < hopCount - 1; index++) {
            angle += whatDirection * (1.65D + random.nextDouble() * 0.75D);
            angles[index] = angle;
            radiusScales[index] = 0.72D + random.nextDouble() * 0.48D;
        }

    }

    private void updateOffsets(final int frame, final boolean initialize) {
        final double longitudinal = frame * 0.11D;
        final double maxHop = amplitude * 0.3D;
        for (int index = 1; index < hopCount - 1; index++) {
            final double progress = hopProgress[index];
            final double radius = amplitude * (0.25D + (1.0D - progress) * 0.75D) * radiusScales[index];
            final double noise = valueNoiseSigned(seed ^ 0x6A09E667F3BCC909L, progress * 3.2D + longitudinal)
                    * 0.45D + valueNoiseSigned(seed ^ 0x3C6EF372FE94F82BL, progress * 17.0D - longitudinal * 2.1D) * 0.55D;
            // Computes an angle (lateral offset) instead of a plane zigzag
            final double angle = angles[index] + noise * 0.22D;
            final double side = Math.cos(angle) * radius;
            final double normal = Math.sin(angle) * radius;

            if (initialize) {
                sideOffsets[index] = side;
                normalOffsets[index] = normal;
            }
            else {
                // Moving the offset towards the new shape instead of replacing it outright or it will flicker randomly
                sideOffsets[index] += Mth.clamp((side - sideOffsets[index]) * 0.42D, -maxHop, maxHop);
                normalOffsets[index] += Mth.clamp((normal - normalOffsets[index]) * 0.42D, -maxHop, maxHop);
            }

        }

    }

    /// Weyl constant (from the primitive local random) used for the arcs
    /// https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/util/concurrent/ThreadLocalRandom.java
    private List<Vec3> buildPath() {
        final Vec3 delta = end.subtract(start);
        final double distance = delta.length();
        if (distance < 1.0E-5D) {
            return List.of(start, end);
        }

        final Vec3 direction = delta.scale(1.0D / distance);
        final Vec3 reference = Math.abs(direction.y) > 0.9D ? Vec3.X_AXIS : Vec3.Y_AXIS;

        // Builds a plane around the bolt so the same offsets work in any direction
        final Vec3 side = direction.cross(reference).normalize();
        final Vec3 normal = direction.cross(side).normalize();
        final List<Vec3> points = new ArrayList<>(hopCount);

        // Section computation
        for (int index = 0; index < hopCount; index++) {
            points.add(start.add(delta.scale(hopProgress[index])).add(side.scale(sideOffsets[index])).add(normal.scale(normalOffsets[index])));
        }

        final List<Vec3> result = new ArrayList<>(hopCount + 18);
        result.add(points.getFirst());

        for (int index = 1; index < hopCount - 1; index++) {
            final Vec3 corner = points.get(index);
            if (!isArc(index)) {
                result.add(corner);
                continue;
            }

            // Computes an arc rather than a single angular zigzag

            final long speed = mix64(seed ^ index * 0x9E3779B97F4A7C15L);
            final double upperCurve = skyStrike ? (1.0D - hopProgress[index]) * 0.1D : 0.0D;
            final double cap = Math.min(0.48D, 0.38D + unitRandom(speed) * 0.08D + upperCurve);
            final Vec3 in = corner.lerp(points.get(index - 1), cap);
            final Vec3 out = corner.lerp(points.get(index + 1), cap);

            // Pushes the control point past the corner so it still keeps some of its sharpness
            final Vec3 control = corner.scale(2).subtract(in.add(out).scale(0.5));
            final int curveSteps = skyStrike && hopProgress[index] < 0.4D ? 8 : 6;

            result.add(in);

            for (int step = 1; step < curveSteps; step++) {
                result.add(quadraticBezier(in, control, out, step / (double) curveSteps));
            }

            result.add(out);
        }

        result.add(points.getLast());

        return List.copyOf(result);
    }

    private boolean isArc(final int index) {
        // First x corners are arcs
        if (skyStrike && index <= 2) {
            return true;
        }

        // The chosen corners only depend on the seed so the whole bolt stays stable while it moves slightly
        final int arcCount = 1 + (int) (unitRandom(mix64(seed ^ 0x1F83D9ABFB41BD6BL)) * 3.0D);
        final int cornerCount = hopCount - 2;
        final int first = 1 + (int) (unitRandom(mix64(seed ^ 0x5BE0CD19137E2179L)) * cornerCount);
        if (arcCount == 1) {
            return index == first;
        }

        final int offset = 1 + (int) (unitRandom(mix64(seed ^ 0x6A09E667F3BCC909L)) * (cornerCount - 1));
        final int second = 1 + (first - 1 + offset) % cornerCount;
        if (arcCount == 2) {
            return index == first || index == second;
        }

        int third = 1 + (int) (unitRandom(mix64(seed ^ 0xBB67AE8584CAA73BL)) * cornerCount);
        while (third == first || third == second) {
            third = 1 + third % cornerCount;
        }

        return index == first || index == second || index == third;
    }

    /// Quadratic Bezier curve formula
    /// https://pomax.github.io/bezierinfo/#explanation
    /// TODO: mix with the harpy dash goal
    private static Vec3 quadraticBezier(final Vec3 from, final Vec3 control, final Vec3 to, final double progress) {
        final double inverse = 1.0D - progress;
        return from.scale(inverse * inverse)
                .add(control.scale(2.0D * inverse * progress))
                .add(to.scale(progress * progress));
    }

    /// Noise function
    /// https://thebookofshaders.com/11/
    /// 0x9E3779B97F4A7C15L is the gamma value (of the golden ratio)
    private static double valueNoiseSigned(final long noiseSeed, final double position) {
        final long lower = (long) Math.floor(position);
        final double fraction = position - lower;
        // TODO: Smoothstep
        final double smoothFraction = fraction * fraction * (3.0D - 2.0D * fraction);
        final double from = unitRandom(mix64(noiseSeed ^ lower * 0x9E3779B97F4A7C15L)) * 2.0D - 1.0D;
        final double to = unitRandom(mix64(noiseSeed ^ (lower + 1L) * 0x9E3779B97F4A7C15L)) * 2.0D - 1.0D;
        return Mth.lerp(smoothFraction, from, to);
    }

    /// fmix64
    /// https://github.com/zendesk/maxwell/blob/master/src/main/java/com/zendesk/maxwell/util/MurmurHash3.java
    private static long mix64(long value) {
        value = (value ^ value >>> 33) * 0xFF51AFD7ED558CCDL;
        value = (value ^ value >>> 33) * 0xC4CEB9FE1A85EC53L;
        return value ^ value >>> 33;
    }

    /// Conversion from the upper 53 random bits to double (capped to 1)
    /// https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/util/Random.java
    private static double unitRandom(final long value) {
        return (value >>> 11) * 0x1.0p-53;
    }

    private void updateBounds() {
        final Vec3 center = start.add(end).scale(0.5D);
        setPos(center.x, center.y, center.z);
        setBoundingBox(new AABB(start, end).inflate(amplitude + baseWidth * 8.0F));
    }

    public record RenderSnapshot(
            List<Vec3> points,
            float width,
            float alpha
    ) {
        ;;
    }

}
