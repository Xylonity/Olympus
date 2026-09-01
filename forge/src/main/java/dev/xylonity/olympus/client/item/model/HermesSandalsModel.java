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

public final class HermesSandalsModel extends HumanoidModel<HumanoidRenderState> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Olympus.of("hermes_sandals"), "main");

    public HermesSandalsModel(final ModelPart root) {
        super(root);
    }

    public ModelPart leftLegPart() {
        return leftLeg;
    }

    public ModelPart rightLegPart() {
        return rightLeg;
    }

    public static LayerDefinition createBodyLayer() {
        final MeshDefinition mesh = new MeshDefinition();
        final PartDefinition root = mesh.getRoot();

        final PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);

        final PartDefinition leftLeg = root.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F))
                        .texOffs(16, 0)
                        .addBox(-2.5F, 6.3F, -2.5F, 5.0F, 2.0F, 5.0F, CubeDeformation.NONE)
                        .texOffs(0, 16)
                        .addBox(-2.5F, 7.5F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(-0.1F)),
                PartPose.offset(1.9F, 12.0F, 0.0F)
        );
        leftLeg.addOrReplaceChild(
                "right_wing",
                CubeListBuilder.create()
                        .texOffs(20, 7)
                        .mirror()
                        .addBox(-3.0904F, -3.4219F, -3.1846F, 0.0F, 6.0F, 7.0F, CubeDeformation.NONE)
                        .mirror(false),
                PartPose.offsetAndRotation(0.0F, 7.4206F, 2.7818F, 0.038F, -0.0965F, -0.1388F)
        );
        leftLeg.addOrReplaceChild(
                "left_wing",
                CubeListBuilder.create()
                        .texOffs(20, 7)
                        .addBox(3.0904F, -3.4219F, -3.1846F, 0.0F, 6.0F, 7.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0F, 7.4206F, 2.7818F, 0.038F, 0.0965F, 0.1388F)
        );

        final PartDefinition rightLeg = root.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .mirror()
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F))
                        .mirror(false)
                        .texOffs(16, 0)
                        .mirror()
                        .addBox(-2.5F, 6.3F, -2.5F, 5.0F, 2.0F, 5.0F, CubeDeformation.NONE)
                        .mirror(false)
                        .texOffs(0, 16)
                        .mirror()
                        .addBox(-2.5F, 7.5F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(-0.1F))
                        .mirror(false),
                PartPose.offset(-1.9F, 12.0F, 0.0F)
        );
        rightLeg.addOrReplaceChild(
                "left_wing",
                CubeListBuilder.create()
                        .texOffs(20, 7)
                        .addBox(3.0904F, -3.4219F, -3.1846F, 0.0F, 6.0F, 7.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0F, 7.4206F, 2.7818F, 0.038F, 0.0965F, 0.1388F)
        );
        rightLeg.addOrReplaceChild(
                "right_wing",
                CubeListBuilder.create()
                        .texOffs(20, 7)
                        .mirror()
                        .addBox(-3.0904F, -3.4219F, -3.1846F, 0.0F, 6.0F, 7.0F, CubeDeformation.NONE)
                        .mirror(false),
                PartPose.offsetAndRotation(0.0F, 7.4206F, 2.7818F, 0.038F, -0.0965F, -0.1388F)
        );

        return LayerDefinition.create(mesh, 64, 64);
    }

}
