package org.polkadot.types.metadata.v11;

import javafx.beans.binding.ObjectExpression;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.metadata.latest.MetadataLatest;
import org.polkadot.types.metadata.latest.Modules;
import org.polkadot.types.metadata.latest.Storage;
import org.polkadot.types.primitive.Type;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Jenner
 */
public class ToLatest {
    public static MetadataLatest toLatest(MetadataV11 v11) {
        Map<String, Object> value = new LinkedHashMap<>();
        value.put("extrinsic", new MetadataLatest.MetadataExtrinsicLatest(v11.getExtrinsic()));
        value.put("modules", v11.getModules().stream().map(
                (mod) -> {
                    Map<String, Object> module = new LinkedHashMap<>();
                    module.put("name", mod.getName());
                    module.put("storage", mod.getStorage().isSome() ? convertStorage(mod.getStorage().unwrap()) : null);
                    module.put("calls", mod.getCalls());
                    module.put("events", mod.getEvents());
                    module.put("constants", mod.getConstants());
                    module.put("errors", mod.getErrors());

                    return new Modules.ModuleMetadataLatest(module);
                }
        ).collect(Collectors.toList()));

        return new MetadataLatest(value);
    }

    static Storage.StorageMetadataLatest convertStorage(StorageV11.StorageMetadataV11 storageMetadataV11) {
        Map<String, Object> storage = new LinkedHashMap<>();
        storage.put("prefix", storageMetadataV11.getPrefix());
        storage.put("items", storageMetadataV11.getItems().stream().map(
                (item) -> {
                    Map<String, Object> value = new LinkedHashMap<>();
                    value.put("documentation", item.getDocumentation());
                    value.put("fallback", item.getFallback());
                    value.put("modifier", new Storage.StorageEntryModifierLatest(item.getModifier()));
                    value.put("name", item.getName());

                    final StorageV11.StorageEntryTypeV11 type = item.getType();
//                Type resultType;
//                if(type.isMap()){
//                    resultType = type.asMap().getValue();
//                } else if(type.isDoubleMap()){
//                    resultType = type.asDoubleMap().getValue();
//                } else{
//                    resultType = type.asPlain();
//                }
                    value.put("type", new Storage.StorageEntryTypeLatest(type));

                    return new Storage.StorageEntryMetadataLatest(value);
                }
        ).collect(Collectors.toList()));

        return new Storage.StorageMetadataLatest(storage);
    }
}