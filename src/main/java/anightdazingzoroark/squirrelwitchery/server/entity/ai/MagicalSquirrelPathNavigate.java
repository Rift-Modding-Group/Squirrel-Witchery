package anightdazingzoroark.squirrelwitchery.server.entity.ai;

import anightdazingzoroark.squirrelwitchery.server.entity.MagicalSquirrelEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.blocks.BlocksTC;

import java.util.List;

/**
 * special navigator for magical squirrels so that they don't walk into spell-affected areas
 * */
public class MagicalSquirrelPathNavigate extends PathNavigateGround {
    @NotNull
    private List<AxisAlignedBB> spellAreas = List.of();
    @Nullable
    private AxisAlignedBB scannedBounds;
    private long hazardTick = -1L;
    private boolean escaping;
    @Nullable
    private Path escapePath;
    private long nextEscapeAttempt;

    public MagicalSquirrelPathNavigate(@NotNull MagicalSquirrelEntity squirrel, @NotNull World world) {
        super(squirrel, world);
    }

    @Override
    @NotNull
    protected PathFinder getPathFinder() {
        this.nodeProcessor = new WalkNodeProcessor() {
            @Override
            public int findPathOptions(@NotNull PathPoint[] options, @NotNull PathPoint current, @NotNull PathPoint target, float maxDistance) {
                int count = super.findPathOptions(options, current, target, maxDistance);
                if (MagicalSquirrelPathNavigate.this.escaping) return count;
                int safeCount = 0;
                Vec3d start = new Vec3d(current.x + 0.5D, current.y, current.z + 0.5D);
                for (int index = 0; index < count; index++) {
                    PathPoint point = options[index];
                    if (MagicalSquirrelPathNavigate.this.isSafeSegment(start, new Vec3d(point.x + 0.5D, point.y, point.z + 0.5D))) {
                        options[safeCount++] = point;
                    }
                }
                return safeCount;
            }

            @Override
            @NotNull
            public PathNodeType getPathNodeType(
                    @NotNull IBlockAccess access, int x, int y, int z, @NotNull EntityLiving entity,
                    int xSize, int ySize, int zSize, boolean canOpenDoors, boolean canEnterDoors
            ) {
                PathNodeType type = super.getPathNodeType(access, x, y, z, entity, xSize, ySize, zSize, canOpenDoors, canEnterDoors);
                if (entity.getPathPriority(type) >= 0f && !MagicalSquirrelPathNavigate.this.isSafeAt(x + 0.5D, y, z + 0.5D)) {
                    //Only allow hazardous nodes when already inside an affected area and finding a way out.
                    return MagicalSquirrelPathNavigate.this.escaping ? PathNodeType.DANGER_OTHER : PathNodeType.BLOCKED;
                }
                return type;
            }
        };
        this.nodeProcessor.setCanEnterDoors(true);
        return new PathFinder(this.nodeProcessor);
    }

    public boolean isSafeAt(double x, double y, double z) {
        if (this.hazardTick != this.world.getTotalWorldTime() || this.scannedBounds == null || !this.scannedBounds.contains(new Vec3d(x, y, z))) {
            this.hazardTick = this.world.getTotalWorldTime();
            this.scannedBounds = new AxisAlignedBB(x, y, z, x, y, z).grow(this.getPathSearchRange() + 12D);
            this.spellAreas = MagicalSquirrelSpellHazards.getDangerousAreas(this.world, this.scannedBounds);
        }
        double halfWidth = this.entity.width / 2D + 0.25D;
        AxisAlignedBB body = new AxisAlignedBB(x - halfWidth, y, z - halfWidth, x + halfWidth, y + this.entity.height, z + halfWidth);
        for (AxisAlignedBB area : this.spellAreas) {
            if (area.intersects(body)) return false;
        }
        //Curse and shock patches can also come from another caster's Plan, or survive a world reload.
        for (BlockPos pos : BlockPos.getAllInBox(new BlockPos(body.minX, body.minY, body.minZ), new BlockPos(body.maxX, body.maxY, body.maxZ))) {
            Block block = this.world.getBlockState(pos).getBlock();
            if (block == BlocksTC.effectSap || block == BlocksTC.effectShock) return false;
        }
        return true;
    }

    private boolean isSafeSegment(@NotNull Vec3d start, @NotNull Vec3d end) {
        int steps = Math.max(1, (int) Math.ceil(start.distanceTo(end) * 4D));
        for (int step = 0; step <= steps; step++) {
            double fraction = (double) step / steps;
            if (!this.isSafeAt(start.x + (end.x - start.x) * fraction, start.y + (end.y - start.y) * fraction,
                    start.z + (end.z - start.z) * fraction)
            ) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected boolean isDirectPathBetweenPoints(@NotNull Vec3d start, @NotNull Vec3d end, int sizeX, int sizeY, int sizeZ) {
        //Vanilla smooths paths by skipping nodes; never let that shortcut cut across a spell area.
        if (!this.escaping && !this.isSafeSegment(start, end)) return false;
        return super.isDirectPathBetweenPoints(start, end, sizeX, sizeY, sizeZ);
    }

    @Override
    public void onUpdateNavigation() {
        //Refresh after AI tasks cast spells, so a newly spawned hazard invalidates the current path immediately.
        this.hazardTick = -1L;
        boolean wasEscaping = this.escaping;
        this.escaping = !this.isSafeAt(this.entity.posX, this.entity.posY, this.entity.posZ);
        if (this.escaping) {
            if (this.escapePath != null && this.escapePath.getFinalPathPoint() instanceof PathPoint end
                    && !this.isSafeAt(end.x + 0.5D, end.y, end.z + 0.5D)
            ) {
                this.escapePath = null;
            }
            this.clearPath();
            this.entity.getMoveHelper().setMoveTo(this.entity.posX, this.entity.posY, this.entity.posZ, 0D);
            if (this.escapePath != null && !this.escapePath.isFinished()) this.setPath(this.escapePath, 1.2D);
            else if (this.world.getTotalWorldTime() >= this.nextEscapeAttempt) {
                this.nextEscapeAttempt = this.world.getTotalWorldTime() + 10L;
                //Search from the nearest safe ring outward, including slopes above and below the squirrel.
                escapeSearch:
                for (int radius = 2; radius <= 16; radius += 2) {
                    for (int direction = 0; direction < 8; direction++) {
                        double angle = direction * Math.PI / 4D;
                        double x = this.entity.posX + Math.cos(angle) * radius;
                        double z = this.entity.posZ + Math.sin(angle) * radius;
                        for (int yOffset : new int[]{0, 1, -1}) {
                            double y = Math.floor(this.entity.posY) + yOffset;
                            if (!this.isSafeAt(x, y, z)) continue;
                            Path path = this.getPathToXYZ(x, y, z);
                            if (path == null || path.getFinalPathPoint() == null) continue;
                            PathPoint end = path.getFinalPathPoint();
                            if (!this.isSafeAt(end.x + 0.5D, end.y, end.z + 0.5D)) continue;
                            if (this.setPath(path, 1.2D)) {
                                this.escapePath = path;
                                break escapeSearch;
                            }
                        }
                    }
                }
            }
        }
        else {
            this.escapePath = null;
            Path path = this.currentPath;
            if (path != null && !path.isFinished()) {
                Vec3d previous = this.entity.getPositionVector();
                for (int index = path.getCurrentPathIndex(); index < path.getCurrentPathLength(); index++) {
                    Vec3d point = path.getVectorFromIndex(this.entity, index);
                    if (wasEscaping || !this.isSafeSegment(previous, point)) {
                        PathPoint end = path.getFinalPathPoint();
                        this.clearPath();
                        this.entity.getMoveHelper().setMoveTo(this.entity.posX, this.entity.posY, this.entity.posZ, 0D);
                        if (end != null) this.tryMoveToXYZ(end.x + 0.5D, end.y, end.z + 0.5D, this.speed);
                        break;
                    }
                    previous = point;
                }
            }
        }
        super.onUpdateNavigation();
    }
}
