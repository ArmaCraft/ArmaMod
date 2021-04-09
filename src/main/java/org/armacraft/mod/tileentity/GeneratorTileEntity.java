package org.armacraft.mod.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import org.armacraft.mod.init.ModTileEntityTypes;

public class GeneratorTileEntity extends TileEntity {

    private String owner;
    private boolean isValid;
    private boolean isGenerator;
    private boolean isBroken;
    private boolean isActivated;
    private int storedStars;
    private long lastTimeClicked;
    private int capacity;
    private int resistance;
    private int production;

    public GeneratorTileEntity() {
        super(ModTileEntityTypes.GENERATOR_TILE.get());
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbtTag = new CompoundNBT();
        nbtTag = this.save(nbtTag);
        return new SUpdateTileEntityPacket(this.getBlockPos(), -1, nbtTag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt){}

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound.putBoolean("isGenerator", true);
        if(this.owner != null) {
            compound.putString("owner", owner);
            compound.putBoolean("isValid", isValid);
            compound.putBoolean("isBroken", isBroken);
            compound.putBoolean("isActivated", isActivated);
            compound.putInt("storedStars", storedStars);
            compound.putLong("lastTimeClicked", lastTimeClicked);
            compound.putInt("capacity", capacity);
            compound.putInt("resistance", resistance);
            compound.putInt("production", production);
        }
        return super.save(compound);
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        isGenerator = true;
        if(compound.contains("owner")) {
            this.owner = compound.getString("owner");
            this.isValid = compound.getBoolean("isValid");
            this.isBroken = compound.getBoolean("isBroken");
            this.isActivated = compound.getBoolean("isActivated");
            this.storedStars = compound.getInt("storedStars");
            this.lastTimeClicked = compound.getLong("lastTimeClicked");
            this.capacity = compound.getInt("capacity");
            this.resistance = compound.getInt("resistance");
            this.production = compound.getInt("production");
        }
        super.load(state, compound);
    }

    public void setOwner(String owner) {
        this.owner = owner;
        this.setChanged();
    }

    public void setGenerator() {
        this.isGenerator = true;
        this.setChanged();
    }

    public String getOwner() {
        return owner;
    }

    public boolean isBroken() {
        return isBroken;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public int getStoredStars() {
        return storedStars;
    }

    public long getLastTimeClicked() {
        return lastTimeClicked;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getResistance() {
        return resistance;
    }

    public int getProduction() {
        return production;
    }
}
