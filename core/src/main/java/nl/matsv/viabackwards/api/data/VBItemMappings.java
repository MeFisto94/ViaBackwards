package nl.matsv.viabackwards.api.data;

import nl.matsv.viabackwards.ViaBackwards;
import org.jetbrains.annotations.Nullable;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.MappingDataLoader;
import us.myles.viaversion.libs.fastutil.ints.Int2ObjectMap;
import us.myles.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import us.myles.viaversion.libs.gson.JsonElement;
import us.myles.viaversion.libs.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Backwards mappings for newly (!) added items.
 */
public class VBItemMappings {

    private final Int2ObjectMap<MappedItem> itemMapping;

    public VBItemMappings(JsonObject oldMapping, JsonObject newMapping, JsonObject diffMapping) {
        Map<Integer, MappedItem> itemMapping = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : diffMapping.entrySet()) {
            JsonObject object = entry.getValue().getAsJsonObject();
            String mappedIdName = object.getAsJsonPrimitive("id").getAsString();
            Map.Entry<String, JsonElement> value = MappingDataLoader.findValue(newMapping, mappedIdName);
            if (value == null) {
                if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                    ViaBackwards.getPlatform().getLogger().warning("No key for " + mappedIdName + " :( ");
                }
                continue;
            }

            Map.Entry<String, JsonElement> oldEntry = MappingDataLoader.findValue(oldMapping, entry.getKey());
            if (oldEntry == null) {
                if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                    ViaBackwards.getPlatform().getLogger().warning("No old entry for " + mappedIdName + " :( ");
                }
                continue;
            }

            int id = Integer.parseInt(oldEntry.getKey());
            int mappedId = Integer.parseInt(value.getKey());
            String name = object.getAsJsonPrimitive("name").getAsString();
            itemMapping.put(id, new MappedItem(mappedId, name));
        }

        this.itemMapping = new Int2ObjectOpenHashMap<>(itemMapping, 1F);
    }

    @Nullable
    public MappedItem getMappedItem(int id) {
        return itemMapping.get(id);
    }
}
