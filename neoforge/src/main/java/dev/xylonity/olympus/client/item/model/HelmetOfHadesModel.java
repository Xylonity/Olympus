package dev.xylonity.olympus.client.item.model;

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

public final class HelmetOfHadesModel extends HumanoidModel<HumanoidRenderState> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Olympus.of("helmet_of_hades"), "main");

    private final ModelPart helmet;
    private final ModelPart outline;

    public HelmetOfHadesModel(final ModelPart root) {
        super(root);
        helmet = head.getChild("helmet");
        outline = head.getChild("outline");
    }

    public ModelPart headPart() {
        return head;
    }

    public ModelPart helmet() {
        return helmet;
    }

    public ModelPart outline() {
        return outline;
    }

    public static LayerDefinition createBodyLayer() {
        final MeshDefinition mesh = new MeshDefinition();
        final PartDefinition root = mesh.getRoot();

        final PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);

        final PartDefinition helmet = head.addOrReplaceChild(
                "helmet",
                CubeListBuilder.create()
                        .texOffs(1, 1)
                        .addBox(-4.0F, -8.8F, -4.0F, 8.0F, 13.0F, 8.0F, new CubeDeformation(0.6F))
                        .texOffs(5, 53)
                        .addBox(-0.5F, -9.8F, 0.0F, 1.0F, 4.0F, 5.0F, new CubeDeformation(0.6F))
                        .texOffs(9, 71)
                        .addBox(-1.5F, -10.3F, 5.6F, 3.0F, 7.0F, 4.0F, CubeDeformation.NONE)
                        .texOffs(1, 85)
                        .addBox(-1.5F, -16.3F, -4.4F, 3.0F, 6.0F, 14.0F, CubeDeformation.NONE)
                        .texOffs(1, 24)
                        .addBox(-4.0F, -8.8F, -4.0F, 8.0F, 13.0F, 8.0F, new CubeDeformation(0.7F)),
                PartPose.ZERO
        );
        helmet.addOrReplaceChild(
                "hat_layer_r1",
                CubeListBuilder.create()
                        .texOffs(62, 24)
                        .addBox(-1.0F, -1.0F, -0.52F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0F, -1.8F, -4.2F, 0.0F, 0.0F, 0.7854F)
        );

        head.addOrReplaceChild(
                "outline",
                CubeListBuilder.create()
                        .texOffs(90, 103)
                        .addBox(-4.0F, -34.0F, 13.0F, -9.0F, -14.0F, -9.0F,
                                new CubeDeformation(-0.35F)),
                PartPose.offset(8.5F, 38.6F, -8.6F)
        );

        return LayerDefinition.create(mesh, 128, 128);
    }

}
