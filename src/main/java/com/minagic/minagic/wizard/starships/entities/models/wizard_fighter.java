package com.minagic.minagic.wizard.starships.entities.models;// Made with Blockbench 5.1.6
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.minagic.minagic.Minagic;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;

public class wizard_fighter<T extends EntityRenderState> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "models/wizard_fighter"), "main");
	private final ModelPart root;
	private final ModelPart Nose;
	private final ModelPart Cockpit;
	private final ModelPart Canopy;
	private final ModelPart Core;
	private final ModelPart MainForwardThruster1;
	private final ModelPart RightForwardThrusterOuter3;
	private final ModelPart MainForwardThruster2;
	private final ModelPart RightForwardThrusterOuter4;
	private final ModelPart RightWing;
	private final ModelPart RightForwardThruster;
	private final ModelPart RightForwardThrusterOuter;
	private final ModelPart LeftWing;
	private final ModelPart LeftForwardThruster;
	private final ModelPart RightForwardThrusterOuter2;

	public wizard_fighter(ModelPart root) {
		super(root);
		this.root = root.getChild("root");
		this.Nose = this.root.getChild("Nose");
		this.Cockpit = this.root.getChild("Cockpit");
		this.Canopy = this.Cockpit.getChild("Canopy");
		this.Core = this.root.getChild("Core");
		this.MainForwardThruster1 = this.Core.getChild("MainForwardThruster1");
		this.RightForwardThrusterOuter3 = this.MainForwardThruster1.getChild("RightForwardThrusterOuter3");
		this.MainForwardThruster2 = this.Core.getChild("MainForwardThruster2");
		this.RightForwardThrusterOuter4 = this.MainForwardThruster2.getChild("RightForwardThrusterOuter4");
		this.RightWing = this.root.getChild("RightWing");
		this.RightForwardThruster = this.RightWing.getChild("RightForwardThruster");
		this.RightForwardThrusterOuter = this.RightForwardThruster.getChild("RightForwardThrusterOuter");
		this.LeftWing = this.root.getChild("LeftWing");
		this.LeftForwardThruster = this.LeftWing.getChild("LeftForwardThruster");
		this.RightForwardThrusterOuter2 = this.LeftForwardThruster.getChild("RightForwardThrusterOuter2");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offsetAndRotation(-17.0F, 0.0F, -9.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition Nose = root.addOrReplaceChild("Nose", CubeListBuilder.create().texOffs(372, 252).addBox(-24.0F, -6.0F, 12.0F, 27.0F, 6.0F, 24.0F, new CubeDeformation(0.0F))
				.texOffs(174, 390).addBox(-27.0F, 0.0F, 15.0F, 6.0F, 6.0F, 18.0F, new CubeDeformation(0.0F))
				.texOffs(222, 390).addBox(-33.0F, 0.0F, 15.0F, 9.0F, 6.0F, 15.0F, new CubeDeformation(0.0F))
				.texOffs(396, 321).addBox(-3.0F, 0.0F, 6.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(396, 333).addBox(-3.0F, 0.0F, 33.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-28.0F, 4.0F, -6.0F));

		PartDefinition cube_r1 = Nose.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(174, 345).addBox(-9.0F, 0.0F, -9.0F, 12.0F, 6.0F, 39.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-21.0F, 0.0F, 27.0F, 0.0F, 1.0036F, 0.0F));

		PartDefinition cube_r2 = Nose.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(294, 309).addBox(-9.0F, 0.0F, -9.0F, 12.0F, 6.0F, 39.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-24.0F, 0.0F, 12.0F, 0.0F, 2.138F, 0.0F));

		PartDefinition Cockpit = root.addOrReplaceChild("Cockpit", CubeListBuilder.create().texOffs(174, 309).addBox(-132.0F, 6.0F, 45.0F, 30.0F, 6.0F, 30.0F, new CubeDeformation(0.0F))
				.texOffs(372, 282).addBox(-126.0F, 12.0F, 30.0F, 21.0F, 6.0F, 15.0F, new CubeDeformation(0.0F))
				.texOffs(270, 399).addBox(-132.0F, 12.0F, 39.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(294, 399).addBox(-111.0F, 12.0F, 24.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(104.0F, -8.0F, -42.0F));

		PartDefinition cube_r3 = Cockpit.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(384, 108).addBox(-9.0F, 0.0F, -3.0F, 12.0F, 6.0F, 33.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-132.0F, 18.0F, 84.0F, 0.0F, -1.0036F, 3.1416F));

		PartDefinition cube_r4 = Cockpit.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(384, 183).addBox(-24.0F, 0.0F, 3.0F, 21.0F, 6.0F, 15.0F, new CubeDeformation(0.0F))
				.texOffs(342, 399).addBox(-9.0F, 0.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-102.0F, 18.0F, 93.0F, 3.1416F, 0.0F, 0.0F));

		PartDefinition cube_r5 = Cockpit.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(318, 399).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-129.0F, 18.0F, 78.0F, 3.1416F, 0.0F, 0.0F));

		PartDefinition cube_r6 = Cockpit.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(372, 354).addBox(-9.0F, 0.0F, -3.0F, 12.0F, 6.0F, 33.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-132.0F, 12.0F, 33.0F, 0.0F, 2.138F, 0.0F));

		PartDefinition Canopy = Cockpit.addOrReplaceChild("Canopy", CubeListBuilder.create().texOffs(390, 36).addBox(-138.0F, 18.0F, 42.0F, 33.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(390, 45).addBox(-138.0F, 18.0F, 69.0F, 33.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(390, 75).addBox(-132.0F, 21.0F, 45.0F, 27.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(390, 84).addBox(-132.0F, 21.0F, 66.0F, 27.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(372, 393).addBox(-129.0F, 24.0F, 48.0F, 24.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(390, 63).addBox(-129.0F, 27.0F, 54.0F, 27.0F, 3.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(396, 303).addBox(-129.0F, 24.0F, 63.0F, 24.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r7 = Canopy.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(379, 43).addBox(-7.0F, 3.0F, -14.0F, 29.0F, 0.0F, 17.0F, new CubeDeformation(0.0F))
				.texOffs(379, 43).addBox(-7.0F, 0.0F, -14.0F, 29.0F, 3.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-141.0F, 18.0F, 66.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition cube_r8 = Canopy.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(390, 93).addBox(-6.0F, 0.0F, -3.0F, 27.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-135.0F, 21.0F, 66.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition cube_r9 = Canopy.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(396, 312).addBox(-3.0F, 0.0F, -3.0F, 21.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-132.0F, 24.0F, 66.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition Core = root.addOrReplaceChild("Core", CubeListBuilder.create().texOffs(0, 108).addBox(-51.0F, 3.0F, -39.0F, 102.0F, 9.0F, 90.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-54.0F, 12.0F, -39.0F, 105.0F, 18.0F, 90.0F, new CubeDeformation(0.0F))
				.texOffs(0, 309).addBox(-57.0F, 9.0F, -36.0F, 3.0F, 18.0F, 84.0F, new CubeDeformation(0.0F)), PartPose.offset(56.0F, -8.0F, 12.0F));

		PartDefinition MainForwardThruster1 = Core.addOrReplaceChild("MainForwardThruster1", CubeListBuilder.create().texOffs(294, 411).addBox(0.0F, 12.0F, 54.0F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(276, 345).addBox(0.0F, 12.0F, 57.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(372, 303).addBox(0.0F, 18.0F, 57.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(306, 411).addBox(0.0F, 12.0F, 60.0F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(51.0F, -3.0F, -84.0F));

		PartDefinition RightForwardThrusterOuter3 = MainForwardThruster1.addOrReplaceChild("RightForwardThrusterOuter3", CubeListBuilder.create().texOffs(144, 411).addBox(0.0F, 9.0F, 51.0F, 3.0F, 15.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(366, 402).addBox(0.0F, 9.0F, 54.0F, 3.0F, 3.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(390, 402).addBox(0.0F, 21.0F, 54.0F, 3.0F, 3.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(156, 411).addBox(0.0F, 9.0F, 63.0F, 3.0F, 15.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 0.0F, 0.0F));

		PartDefinition MainForwardThruster2 = Core.addOrReplaceChild("MainForwardThruster2", CubeListBuilder.create().texOffs(318, 411).addBox(0.0F, 12.0F, 54.0F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(384, 303).addBox(0.0F, 12.0F, 57.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(390, 102).addBox(0.0F, 18.0F, 57.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(330, 411).addBox(0.0F, 12.0F, 60.0F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(51.0F, -3.0F, -21.0F));

		PartDefinition RightForwardThrusterOuter4 = MainForwardThruster2.addOrReplaceChild("RightForwardThrusterOuter4", CubeListBuilder.create().texOffs(222, 411).addBox(0.0F, 9.0F, 51.0F, 3.0F, 15.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(0, 411).addBox(0.0F, 9.0F, 54.0F, 3.0F, 3.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(24, 411).addBox(0.0F, 21.0F, 54.0F, 3.0F, 3.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(234, 411).addBox(0.0F, 9.0F, 63.0F, 3.0F, 15.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 0.0F, 0.0F));

		PartDefinition RightWing = root.addOrReplaceChild("RightWing", CubeListBuilder.create().texOffs(276, 354).addBox(-24.0F, 9.0F, 45.0F, 24.0F, 21.0F, 24.0F, new CubeDeformation(0.0F))
				.texOffs(384, 147).addBox(-42.0F, 9.0F, 45.0F, 24.0F, 21.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(56.0F, -8.0F, 12.0F));

		PartDefinition cube_r10 = RightWing.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(0, 207).addBox(-9.0F, -9.0F, -3.0F, 12.0F, 21.0F, 81.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-48.0F, 18.0F, 48.0F, 0.0F, 1.0908F, 0.0F));

		PartDefinition RightForwardThruster = RightWing.addOrReplaceChild("RightForwardThruster", CubeListBuilder.create().texOffs(342, 411).addBox(0.0F, 12.0F, 54.0F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(396, 345).addBox(0.0F, 12.0F, 57.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(402, 102).addBox(0.0F, 18.0F, 57.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(354, 411).addBox(0.0F, 12.0F, 60.0F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, 3.0F));

		PartDefinition RightForwardThrusterOuter = RightForwardThruster.addOrReplaceChild("RightForwardThrusterOuter", CubeListBuilder.create().texOffs(246, 411).addBox(0.0F, 9.0F, 51.0F, 3.0F, 15.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(48, 411).addBox(0.0F, 9.0F, 54.0F, 3.0F, 3.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(72, 411).addBox(0.0F, 21.0F, 54.0F, 3.0F, 3.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(258, 411).addBox(0.0F, 9.0F, 63.0F, 3.0F, 15.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 0.0F, 0.0F));

		PartDefinition LeftWing = root.addOrReplaceChild("LeftWing", CubeListBuilder.create().texOffs(372, 207).addBox(-24.0F, 12.0F, 45.0F, 24.0F, 21.0F, 24.0F, new CubeDeformation(0.0F))
				.texOffs(390, 0).addBox(-42.0F, 12.0F, 45.0F, 24.0F, 21.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(56.0F, 34.0F, 21.0F, 3.1416F, 0.0F, 0.0F));

		PartDefinition cube_r11 = LeftWing.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(186, 207).addBox(-9.0F, -6.0F, -3.0F, 12.0F, 21.0F, 81.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-48.0F, 18.0F, 48.0F, 0.0F, 1.0908F, 0.0F));

		PartDefinition LeftForwardThruster = LeftWing.addOrReplaceChild("LeftForwardThruster", CubeListBuilder.create().texOffs(168, 414).addBox(0.0F, 12.0F, 54.0F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(408, 345).addBox(0.0F, 12.0F, 57.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(414, 102).addBox(0.0F, 18.0F, 57.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(180, 414).addBox(0.0F, 12.0F, 60.0F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

		PartDefinition RightForwardThrusterOuter2 = LeftForwardThruster.addOrReplaceChild("RightForwardThrusterOuter2", CubeListBuilder.create().texOffs(270, 411).addBox(0.0F, 9.0F, 51.0F, 3.0F, 15.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(96, 411).addBox(0.0F, 9.0F, 54.0F, 3.0F, 3.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(120, 411).addBox(0.0F, 21.0F, 54.0F, 3.0F, 3.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(282, 411).addBox(0.0F, 9.0F, 63.0F, 3.0F, 15.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 512, 512);
	}
//	@Override
//	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
//
//	}
//
//	@Override
//	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
//		Nose.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
//		Cockpit.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
//		Core.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
//		RightWing.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
//		LeftWing.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
//	}
}