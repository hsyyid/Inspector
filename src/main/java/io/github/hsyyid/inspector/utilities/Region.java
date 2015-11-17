package io.github.hsyyid.inspector.utilities;

import com.google.common.collect.Sets;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Set;
import java.util.UUID;

public class Region
{
	private UUID owner;
	private Location<World> pointA;
	private Location<World> pointB;
	
	public Region(UUID owner, Location<World> pointA, Location<World> pointB)
	{
		this.owner = owner;
		this.pointA = pointA;
		this.pointB = pointB;
	}
	
	public UUID getOwner()
	{
		return owner;
	}
	
	public Location<World> getPointA()
	{
		return pointA;
	}
	
	public Location<World> getPointB()
	{
		return pointB;
	}
	
	public void setOwner(UUID owner)
	{
		this.owner = owner;
	}
	
	public void setPointA(Location<World> pointA)
	{
		this.pointA = pointA;
	}
	
	public void setPointB(Location<World> pointB)
	{
		this.pointB = pointB;
	}
	
	public Set<Location<World>> getContainingBlocks()
	{
		Set<Location<World>> containingBlocks = Sets.newHashSet();
		
		if(pointA.getBlockX() < pointB.getBlockX())
		{
			for(int x = pointA.getBlockX(); x <= pointB.getBlockX(); x++)
			{
				containingBlocks.add(new Location<World>(pointA.getExtent(), x, pointA.getBlockY(), pointA.getBlockZ()));
				
				if(pointA.getBlockY() < pointB.getBlockY())
				{
					for(int y = pointA.getBlockY(); y <= pointB.getBlockY(); y++)
					{
						containingBlocks.add(new Location<World>(pointA.getExtent(), x, y, pointA.getBlockZ()));
						
						if(pointA.getBlockZ() < pointB.getBlockZ())
						{
							for(int z = pointA.getBlockZ(); z <= pointB.getBlockZ(); z++)
							{
								containingBlocks.add(new Location<World>(pointA.getExtent(), x, y, z));
							}
						}
						else
						{
							for(int z = pointB.getBlockZ(); z <= pointA.getBlockZ(); z++)
							{
								containingBlocks.add(new Location<World>(pointA.getExtent(), x, y, z));
							}
						}
					}
				}
				else
				{
					for(int y = pointB.getBlockY(); y <= pointA.getBlockY(); y++)
					{
						containingBlocks.add(new Location<World>(pointB.getExtent(), x, y, pointB.getBlockZ()));
						
						if(pointA.getBlockZ() < pointB.getBlockZ())
						{
							for(int z = pointA.getBlockZ(); z <= pointB.getBlockZ(); z++)
							{
								containingBlocks.add(new Location<World>(pointA.getExtent(), x, y, z));
							}
						}
						else
						{
							for(int z = pointB.getBlockZ(); z <= pointA.getBlockZ(); z++)
							{
								containingBlocks.add(new Location<World>(pointA.getExtent(), x, y, z));
							}
						}
					}
				}
			}
		}
		else
		{
			for(int x = pointB.getBlockX(); x <= pointA.getBlockX(); x++)
			{
				containingBlocks.add(new Location<World>(pointB.getExtent(), x, pointB.getBlockY(), pointB.getBlockZ()));
				
				if(pointA.getBlockY() < pointB.getBlockY())
				{
					for(int y = pointA.getBlockY(); y <= pointB.getBlockY(); y++)
					{
						containingBlocks.add(new Location<World>(pointA.getExtent(), x, y, pointA.getBlockZ()));
						
						if(pointA.getBlockZ() < pointB.getBlockZ())
						{
							for(int z = pointA.getBlockZ(); z <= pointB.getBlockZ(); z++)
							{
								containingBlocks.add(new Location<World>(pointA.getExtent(), x, y, z));
							}
						}
						else
						{
							for(int z = pointB.getBlockZ(); z <= pointA.getBlockZ(); z++)
							{
								containingBlocks.add(new Location<World>(pointA.getExtent(), x, y, z));
							}
						}
					}
				}
				else
				{
					for(int y = pointB.getBlockY(); y <= pointA.getBlockY(); y++)
					{
						containingBlocks.add(new Location<World>(pointB.getExtent(), x, y, pointB.getBlockZ()));
						
						if(pointA.getBlockZ() < pointB.getBlockZ())
						{
							for(int z = pointA.getBlockZ(); z <= pointB.getBlockZ(); z++)
							{
								containingBlocks.add(new Location<World>(pointA.getExtent(), x, y, z));
							}
						}
						else
						{
							for(int z = pointB.getBlockZ(); z <= pointA.getBlockZ(); z++)
							{
								containingBlocks.add(new Location<World>(pointA.getExtent(), x, y, z));
							}
						}
					}
				}
			}
		}
		
		return containingBlocks;
	}
}
