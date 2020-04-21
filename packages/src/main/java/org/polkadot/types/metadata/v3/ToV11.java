package org.polkadot.types.metadata.v3;

import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Option;
import org.polkadot.types.metadata.v11.MetadataV11;
import org.polkadot.types.metadata.v11.Modules.ModuleMetadataV11;
import org.polkadot.types.metadata.v11.StorageV11.StorageEntryMetadataV11;
import org.polkadot.types.metadata.v11.StorageV11.StorageEntryTypeV11;
import org.polkadot.types.metadata.v11.StorageV11.StorageHasherV11;
import org.polkadot.types.metadata.v11.StorageV11.StorageMetadataV11;
import org.polkadot.types.metadata.v3.Modules.ModuleMetadataV3;
import org.polkadot.types.metadata.v3.Storage.StorageFunctionMetadataV3;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * @author Jenner
 */
public class ToV11 {

    static StorageEntryTypeV11 storageEntryTypeV11(Storage.StorageFunctionTypeV3 storageFunctionTypeV3) {
        Map<String, Object> type = new LinkedHashMap<>();

        String functionTypeV3 = storageFunctionTypeV3.getType();
        int index = -1;
        switch (functionTypeV3) {
            case "PlainType":
                type.put(functionTypeV3, storageFunctionTypeV3.asType());
                index = 0;
                break;
            case "MapType":
                Map<String, Object> map = new LinkedHashMap<>();
                Storage.MapTypeV3 mapTypeV3 = storageFunctionTypeV3.asMap();
                map.put("hasher", new StorageHasherV11(null, 3));
                map.put("key", mapTypeV3.getKey());
                map.put("value", mapTypeV3.getValue());
                map.put("linked", mapTypeV3.isLinked());
                type.put(functionTypeV3, map);
                index = 1;
                break;
            case "DoubleMapType":
                Map<String, Object> doubleMap = new LinkedHashMap<>();
                Storage.DoubleMapTypeV3 doubleMapTypeV3 = storageFunctionTypeV3.asDoubleMap();
                doubleMap.put("hasher", new StorageHasherV11(null, 3));
                doubleMap.put("key1", doubleMapTypeV3.getKey1());
                doubleMap.put("key2", doubleMapTypeV3.getKey2());
                doubleMap.put("value", doubleMapTypeV3.getValue());
                doubleMap.put("key2Hasher", doubleMapTypeV3.getKeyHasher());
                type.put(functionTypeV3, doubleMap);
                index = 2;
                break;
            default:
                throw new RuntimeException("No such StorageFunctionType");
        }

        return new StorageEntryTypeV11(type, index);
    }

    static StorageEntryMetadataV11 storageEntryMetadataV11(StorageFunctionMetadataV3 storageFunctionMetadataV3) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("name", storageFunctionMetadataV3.getName());
        item.put("modifier", storageFunctionMetadataV3.getModifier());
        item.put("type", storageEntryTypeV11(storageFunctionMetadataV3.getType()));
        item.put("fallback", storageFunctionMetadataV3.getFallback());
        item.put("documentation", storageFunctionMetadataV3.getDocumentation());

        return new StorageEntryMetadataV11(item);
    }

    static ModuleMetadataV11 moduleMetadataV11(ModuleMetadataV3 moduleMetadataV3) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("name", moduleMetadataV3.getName());

        Map<String, Object> storage = new LinkedHashMap<>();
        storage.put("prefix", moduleMetadataV3.getPrefix());
        if (moduleMetadataV3.getStorage().isSome()) {
            storage.put("item", moduleMetadataV3.getStorage().unwrap().stream().map(ToV11::storageEntryMetadataV11).collect(Collectors.toList()));
        } else {
            storage.put("item", new Vector<>());
        }
        values.put("storage", new Option(TypesUtils.getConstructorCodec(StorageMetadataV11.class), storage));

        values.put("calls", new Option(TypesUtils.getConstructorCodec(StorageMetadataV11.class), moduleMetadataV3.getCalls()));
        values.put("events", new Option(TypesUtils.getConstructorCodec(StorageMetadataV11.class), moduleMetadataV3.getEvents()));
        values.put("constants", new Vector<>());
        values.put("errors", new Vector<>());

        return new ModuleMetadataV11(values);
    }

    public static MetadataV11 toV11(MetadataV3 v3) {
        Map<String, Object> extrinsic = new LinkedHashMap<>();
        extrinsic.put("version", 0);
        extrinsic.put("signedExtensions", new Vector<>());

        Map<String, Object> values = new LinkedHashMap<>();
        values.put("modules", v3.getModules().stream().map(ToV11::moduleMetadataV11).collect(Collectors.toList()));
        values.put("extrinsic", extrinsic);

        return new MetadataV11(values);
    }
}