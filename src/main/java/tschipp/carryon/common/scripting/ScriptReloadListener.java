package tschipp.carryon.common.scripting;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import tschipp.carryon.CarryOn;
import tschipp.carryon.network.client.ScriptReloadPacket;

@EventBusSubscriber(modid = CarryOn.MODID, bus = Bus.FORGE)
public class ScriptReloadListener extends JsonReloadListener
{
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

	public ScriptReloadListener()
	{
		super(GSON, "carryon/scripts");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonObject> objects, IResourceManager manager, IProfiler profiler)
	{
		ScriptReader.OVERRIDES.clear();

		objects.forEach((path, jsonObj) -> {
			CarryOnOverride override = GSON.fromJson(jsonObj, CarryOnOverride.class);
			ScriptReader.OVERRIDES.put(override.hashCode(), override);
		});

		if (EffectiveSide.get().isServer() && ServerLifecycleHooks.getCurrentServer() != null)
		{
			CarryOn.network.send(PacketDistributor.ALL.noArg(), new ScriptReloadPacket(ScriptReader.OVERRIDES.values()));
		}
	}
	
	public static void onDatapackRegister(AddReloadListenerEvent event)
	{
		
	}

}
