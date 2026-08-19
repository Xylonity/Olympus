package dev.xylonity.olympus.client.model;

import dev.xylonity.olympus.Olympus;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;

public final class BracersOfZeusModel extends HumanoidModel<HumanoidRenderState> {

    public static final ModelLayerLocation SLIM_LAYER_LOCATION = new ModelLayerLocation(
            Olympus.of("bracers_of_zeus_slim"),
            "main"
    );
    public static final ModelLayerLocation WIDE_LAYER_LOCATION = new ModelLayerLocation(
            Olympus.of("bracers_of_zeus_wide"),
            "main"
    );

    private final ModelPart leftBracer;
    private final ModelPart leftOutline;
    private final ModelPart rightBracer;
    private final ModelPart rightOutline;

    public BracersOfZeusModel(final ModelPart root) {
        super(root);
        leftBracer = leftArm.getChild("bracer");
        leftOutline = leftArm.getChild("outline");
        rightBracer = rightArm.getChild("bracer");
        rightOutline = rightArm.getChild("outline");
    }

    public ModelPart arm(final HumanoidArm arm) {
        return arm == HumanoidArm.LEFT ? leftArm : rightArm;
    }

    public ModelPart bracer(final HumanoidArm arm) {
        return arm == HumanoidArm.LEFT ? leftBracer : rightBracer;
    }

    public ModelPart outline(final HumanoidArm arm) {
        return arm == HumanoidArm.LEFT ? leftOutline : rightOutline;
    }

    public static LayerDefinition createSlimBodyLayer() {
        return createBodyLayer(false);
    }

    public static LayerDefinition createWideBodyLayer() {
        return createBodyLayer(true);
    }

    private static LayerDefinition createBodyLayer(final boolean wide) {
        final MeshDefinition mesh = new MeshDefinition();
        final PartDefinition root = mesh.getRoot();
        final float bracerWidthX = wide ? 4.0F : 3.0F;
        final float rightBracerX = wide ? -3.0F : -2.0F;
        final float outlineOffsetX = wide ? 1.0F : 0.5F;
        final float outlineMainHalfX = wide ? 2.1F : 1.6F;

        final PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);

        final PartDefinition leftArm = root.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create(),
                PartPose.offset(5.0F, 2.0F, 0.0F)
        );
        leftArm.addOrReplaceChild(
                "bracer",
                CubeListBuilder.create()
                        .texOffs(0, 16)
                        .addBox(-1.0F, -0.5F, -2.0F, bracerWidthX, 8.0F, 4.0F,
                                new CubeDeformation(0.3F))
                        .texOffs(16, 0)
                        .addBox(-1.0F, 1.5F, -2.0F, bracerWidthX, 6.0F, 4.0F,
                                new CubeDeformation(0.35F)),
                PartPose.ZERO
        );

        leftArm.addOrReplaceChild(
                "outline",
                CubeListBuilder.create()
                        .texOffs(51, 42)
                        .addBox(outlineMainHalfX, -0.1333F, 2.1F, -outlineMainHalfX * 2.0F, -3.0F, -4.2F,
                                new CubeDeformation(-0.24F))
                        .texOffs(44, 56)
                        .addBox(outlineMainHalfX, 0.3667F, 2.1F, -outlineMainHalfX * 2.0F, -4.0F, -4.2F,
                                new CubeDeformation(-0.3F)),
                PartPose.offset(outlineOffsetX, 6.1333F, 0.0F)
        );

        final PartDefinition rightArm = root.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create(),
                PartPose.offset(-5.0F, 2.0F, 0.0F)
        );
        rightArm.addOrReplaceChild(
                "bracer",
                CubeListBuilder.create()
                        .texOffs(0, 16)
                        .mirror()
                        .addBox(rightBracerX, -0.5F, -2.0F, bracerWidthX, 8.0F, 4.0F,
                                new CubeDeformation(0.3F))
                        .mirror(false)
                        .texOffs(16, 0)
                        .mirror()
                        .addBox(rightBracerX, 1.5F, -2.0F, bracerWidthX, 6.0F, 4.0F,
                                new CubeDeformation(0.35F))
                        .mirror(false),
                PartPose.ZERO
        );
        rightArm.addOrReplaceChild(
                "outline",
                CubeListBuilder.create()
                        .texOffs(51, 42)
                        .mirror()
                        .addBox(outlineMainHalfX, -0.1333F, 2.1F, -outlineMainHalfX * 2.0F, -3.0F, -4.2F,
                                new CubeDeformation(-0.24F))
                        .mirror(false)
                        .texOffs(44, 56)
                        .mirror()
                        .addBox(outlineMainHalfX, 0.3667F, 2.1F, -outlineMainHalfX * 2.0F, -4.0F, -4.2F,
                                new CubeDeformation(-0.3F))
                        .mirror(false),
                PartPose.offset(-outlineOffsetX, 6.1333F, 0.0F)
        );

        return LayerDefinition.create(mesh, 64, 64);
    }

}
