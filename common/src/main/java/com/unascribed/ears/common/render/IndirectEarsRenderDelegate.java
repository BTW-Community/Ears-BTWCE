package com.unascribed.ears.common.render;

import com.unascribed.ears.common.EarsCommon;

/**
 * Provides an abstraction for working with "indirect" rendering in modern Minecraft versions (1.15
 * and later).
 *
 * @param <TMatrixStack> the type of the matrix stack; usually MatrixStack or PoseStack
 * @param <TVertexConsumerProvider> the type of the vertex consumer provider; usually VertexConsumerProvider or IRenderTypeBuffers
 * @param <TVertexConsumer> the type of a vertex consumer; usually VertexConsumer or IRenderBuffer
 * @param <TPeer> the type of the "render peer"; usually something like AbstractClientPlayer
 * @param <TModelPart> the type of model parts; usually ModelPart (Yarn/Mojmap) or ModelRenderer (MCP)
 */
public abstract class IndirectEarsRenderDelegate<TMatrixStack, TVertexConsumerProvider, TVertexConsumer, TPeer, TModelPart> extends AbstractEarsRenderDelegate<TPeer, TModelPart> {

	protected TMatrixStack matrices;
	protected TVertexConsumerProvider vcp;
	protected TVertexConsumer vc;
	protected int light, overlay;
	
	public void render(TMatrixStack matrices, TVertexConsumerProvider vertexConsumers, TPeer peer, float limbDistance, int light, int overlay) {
		render(matrices, vertexConsumers, peer, limbDistance, light, overlay, null);
	}
	
	public void render(TMatrixStack matrices, TVertexConsumerProvider vertexConsumers, TPeer peer, float limbDistance, int light, int overlay, BodyPart permittedBodyPart) {
		this.matrices = matrices;
		this.vcp = vertexConsumers;
		this.peer = peer;
		this.permittedBodyPart = permittedBodyPart;
		this.feat = getEarsFeatures();
		this.vc = getVertexConsumer(TexSource.SKIN);
		this.light = light;
		this.overlay = overlay;
		EarsCommon.render(this.feat, this, limbDistance, isSlim());
		matrices = null;
		vertexConsumers = null;
		vc = null;
	}
	
	@Override
	protected final void setUpRenderState() {}
	
	@Override
	protected final void tearDownRenderState() {}
	
	@Override
	protected final void doBindSkin() {
		commitQuads();
		this.vc = getVertexConsumer(TexSource.SKIN);
	}
	
	protected abstract TVertexConsumer getVertexConsumer(TexSource src);
	protected abstract void commitQuads();

	@Override
	protected final void doBindSub(TexSource src, byte[] pngData) {
		doUploadSub(src, pngData);
		commitQuads();
		this.vc = getVertexConsumer(src);
	}

	protected abstract void doUploadSub(TexSource src, byte[] pngData);
	
	@Override
	protected void beginQuad() {}
	
	@Override
	protected void drawQuad() {}
	
}
