package net.liukrast.multipart;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.InternalApi;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Utility class for handling temporary visualization of multipart block positions.
 *
 * <p>Use {@link #notify(BlockPos)} to request the rendering of a block highlight for a short time.</p>
 *
 * <p>Other methods are for internal use only and should not be called directly.</p>
 */
public class MultipartAPI {

	private MultipartAPI() {}

	/**
	 * Requests to render a temporary red box around the given block position.
	 * The highlight will last for 60 ticks.
	 *
	 * @param pos the block position to highlight
	 */
	public static void notify(BlockPos pos) {
		stored.put(pos, 60);
	}

	private static final LinkedHashMap<BlockPos, Integer> stored = new LinkedHashMap<>();

	/**
	 * Renders highlight boxes for all tracked block positions.
	 * <p><b>Internal use only.</b></p>
	 *
	 * @param poseStack the current pose stack used for rendering
	 */
	@InternalApi
	public static void render(PoseStack poseStack) {
		Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
		Vec3 camPos = camera.getPosition();
		for(BlockPos pos : stored.keySet()) {
			AABB box = AABB.ofSize(Vec3.atCenterOf(pos), 1, 1, 1).move(-camPos.x, -camPos.y, -camPos.z);

			LevelRenderer.renderLineBox(
					poseStack,
					Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.lines()),
					box,
					1.0f, 0f, 0f, stored.get(pos) / 60f
			);
		}
	}

	/**
	 * Decrements the remaining duration for each highlight and removes expired entries.
	 * <p><b>Internal use only.</b></p>
	 */
	@InternalApi
	public static void tick() {
		for(BlockPos pos : new ArrayList<>(stored.keySet())) {
			int value = stored.get(pos);
			if(value <= 0) stored.remove(pos);
			else stored.put(pos, value - 1);
		}
	}
}