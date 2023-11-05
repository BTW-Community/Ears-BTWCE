package com.unascribed.ears.main;

import net.minecraft.src.ModelBox;
import net.minecraft.src.ModelRenderer;
import net.minecraft.src.Tessellator;
import org.lwjgl.opengl.GL11;

public class TranslucentBoxModel extends ModelBox {
    public TranslucentBoxModel(ModelRenderer par1ModelRenderer, int par2, int par3, float par4, float par5, float par6, int par7, int par8, int par9, float par10) {
        super(par1ModelRenderer, par2, par3, par4, par5, par6, par7, par8, par9, par10);
    }

    @Override
    public void render(Tessellator par1Tessellator, float par2) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        super.render(par1Tessellator, par2);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void addBoxTo(ModelRenderer rend, int texOfsX, int texOfsY, float p_78790_1_, float p_78790_2_, float p_78790_3_, int p_78790_4_, int p_78790_5_, int p_78790_6_, float p_78790_7_) {
        rend.cubeList.add(new TranslucentBoxModel(rend, texOfsX, texOfsY, p_78790_1_, p_78790_2_, p_78790_3_, p_78790_4_, p_78790_5_, p_78790_6_, p_78790_7_));
    }

}
