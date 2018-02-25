
package io.github.hsyyid.inspector.utilities;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.UUID;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class Region {
	private UUID owner;
	private Location<World> pointA;
	private Location<World> pointB;

	public Region(UUID owner, Location<World> pointA, Location<World> pointB) {
		this.owner = owner;
		this.pointA = pointA;
		this.pointB = pointB;
	}

	public UUID getOwner() {
		return this.owner;
	}

	public Location<World> getPointA() {
		return this.pointA;
	}

	public Location<World> getPointB() {
		return this.pointB;
	}

	public void setOwner(UUID owner) {
		this.owner = owner;
	}

	public void setPointA(Location<World> pointA) {
		this.pointA = pointA;
	}

	public void setPointB(Location<World> pointB) {
		this.pointB = pointB;
	}

	public Set<Location<World>> getContainingBlocks() {
		Set<Location<World>> containingBlocks = Sets.newHashSet();
		int x;
		int y;
		int z;
		if (this.pointA.getBlockX() < this.pointB.getBlockX()) {
			for(x = this.pointA.getBlockX(); x <= this.pointB.getBlockX(); ++x) {
				containingBlocks.add(new Location(this.pointA.getExtent(), x, this.pointA.getBlockY(), this.pointA.getBlockZ()));
				if (this.pointA.getBlockY() < this.pointB.getBlockY()) {
					for(y = this.pointA.getBlockY(); y <= this.pointB.getBlockY(); ++y) {
						containingBlocks.add(new Location(this.pointA.getExtent(), x, y, this.pointA.getBlockZ()));
						if (this.pointA.getBlockZ() < this.pointB.getBlockZ()) {
							for(z = this.pointA.getBlockZ(); z <= this.pointB.getBlockZ(); ++z) {
								containingBlocks.add(new Location(this.pointA.getExtent(), x, y, z));
							}
						} else {
							for(z = this.pointB.getBlockZ(); z <= this.pointA.getBlockZ(); ++z) {
								containingBlocks.add(new Location(this.pointA.getExtent(), x, y, z));
							}
						}
					}
				} else {
					for(y = this.pointB.getBlockY(); y <= this.pointA.getBlockY(); ++y) {
						containingBlocks.add(new Location(this.pointB.getExtent(), x, y, this.pointB.getBlockZ()));
						if (this.pointA.getBlockZ() < this.pointB.getBlockZ()) {
							for(z = this.pointA.getBlockZ(); z <= this.pointB.getBlockZ(); ++z) {
								containingBlocks.add(new Location(this.pointA.getExtent(), x, y, z));
							}
						} else {
							for(z = this.pointB.getBlockZ(); z <= this.pointA.getBlockZ(); ++z) {
								containingBlocks.add(new Location(this.pointA.getExtent(), x, y, z));
							}
						}
					}
				}
			}
		} else {
			for(x = this.pointB.getBlockX(); x <= this.pointA.getBlockX(); ++x) {
				containingBlocks.add(new Location(this.pointB.getExtent(), x, this.pointB.getBlockY(), this.pointB.getBlockZ()));
				if (this.pointA.getBlockY() < this.pointB.getBlockY()) {
					for(y = this.pointA.getBlockY(); y <= this.pointB.getBlockY(); ++y) {
						containingBlocks.add(new Location(this.pointA.getExtent(), x, y, this.pointA.getBlockZ()));
						if (this.pointA.getBlockZ() < this.pointB.getBlockZ()) {
							for(z = this.pointA.getBlockZ(); z <= this.pointB.getBlockZ(); ++z) {
								containingBlocks.add(new Location(this.pointA.getExtent(), x, y, z));
							}
						} else {
							for(z = this.pointB.getBlockZ(); z <= this.pointA.getBlockZ(); ++z) {
								containingBlocks.add(new Location(this.pointA.getExtent(), x, y, z));
							}
						}
					}
				} else {
					for(y = this.pointB.getBlockY(); y <= this.pointA.getBlockY(); ++y) {
						containingBlocks.add(new Location(this.pointB.getExtent(), x, y, this.pointB.getBlockZ()));
						if (this.pointA.getBlockZ() < this.pointB.getBlockZ()) {
							for(z = this.pointA.getBlockZ(); z <= this.pointB.getBlockZ(); ++z) {
								containingBlocks.add(new Location(this.pointA.getExtent(), x, y, z));
							}
						} else {
							for(z = this.pointB.getBlockZ(); z <= this.pointA.getBlockZ(); ++z) {
								containingBlocks.add(new Location(this.pointA.getExtent(), x, y, z));
							}
						}
					}
				}
			}
		}

		return containingBlocks;
	}
}
