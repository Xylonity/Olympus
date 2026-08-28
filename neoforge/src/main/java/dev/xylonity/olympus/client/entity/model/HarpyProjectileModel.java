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

public final class HarpyProjectileModel {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Olympus.of("harpy_projectile"), "main");

    private final ModelPart projectile;
    private final ModelPart cubeOutline;

    public HarpyProjectileModel(final ModelPart root) {
        projectile = root.getChild("harpy_projectile");
        cubeOutline = root.getChild("cube_outline");
    }

    public ModelPart projectile() {
        return projectile;
    }

    public ModelPart cubeOutline() {
        return cubeOutline;
    }

    public static LayerDefinition createBodyLayer() {
        final MeshDefinition mesh = new MeshDefinition();
        final PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild(
                "harpy_projectile",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-1.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(-0.2F)),
                PartPose.ZERO
        );
        root.addOrReplaceChild(
                "cube_outline",
                CubeListBuilder.create()
                        .texOffs(10, 14)
                        .addBox(2.2F, 0.2F, 2.2F, -3.4F, -3.4F, -3.4F, new CubeDeformation(0.0F)),
                PartPose.offset(-0.5F, 1.5F, -0.5F)
        );

        return LayerDefinition.create(mesh, 16, 16);
    }
}
