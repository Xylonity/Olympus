package dev.xylonity.olympus.client.entity.model;

import dev.xylonity.olympus.Olympus;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public final class AbsorbedSoulModel {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Olympus.of("absorbed_soul"), "main");

    private final ModelPart soul;
    private final ModelPart cubeOutline;

    public AbsorbedSoulModel(final ModelPart root) {
        soul = root.getChild("absorbed_soul");
        cubeOutline = root.getChild("cube_outline");
    }

    public ModelPart soul() {
        return soul;
    }

    public ModelPart cubeOutline() {
        return cubeOutline;
    }

    public static LayerDefinition createBodyLayer() {
        final MeshDefinition mesh = new MeshDefinition();
        final PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild(
                "absorbed_soul",
                CubeListBuilder.create()
                        .texOffs(1, 1)
                        .addBox(-0.5F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F)),
                PartPose.offset(0.0F, 22.7F, 0.0F)
        );
        root.addOrReplaceChild(
                "cube_outline",
                CubeListBuilder.create()
                        .texOffs(8, 12)
                        .addBox(0.5F, 0.0F, 0.5F, -1.0F, -1.0F, -1.0F, new CubeDeformation(-0.1F)),
                PartPose.offset(0.0F, 23.7F, 0.0F)
        );

        return LayerDefinition.create(mesh, 16, 16);
    }

}