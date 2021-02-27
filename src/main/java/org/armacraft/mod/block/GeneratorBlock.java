package org.armacraft.mod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.armacraft.mod.init.ModTileEntityTypes;
import org.armacraft.mod.tileentity.GeneratorTileEntity;

public class GeneratorBlock extends Block {

    public GeneratorBlock() {
        super(Block.Properties.create(Material.ROCK).notSolid());
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }


    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntityTypes.GENERATOR_TILE.get().create();
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        GeneratorTileEntity tile = (GeneratorTileEntity) worldIn.getTileEntity(pos);
        if (placer instanceof PlayerEntity) {
            tile.setGenerator();
            tile.markDirty();
        }
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }
}
