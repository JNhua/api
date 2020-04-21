package org.polkadot.type.storage;

import com.google.common.collect.Lists;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.metadata.latest.Storage;
import org.polkadot.types.primitive.Bytes;
import org.polkadot.types.primitive.StorageKey;
import org.polkadot.types.primitive.Text;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class Substrate {

    public static class SubstrateMetadata {
        private String documentation;
        private String type;

        public SubstrateMetadata(String documentation, String type) {
            this.documentation = documentation;
            this.type = type;
        }

        public String getDocumentation() {
            return documentation;
        }

        public void setDocumentation(String documentation) {
            this.documentation = documentation;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    // Small helper function to factorize code on this page.
    static StorageKey.StorageFunction<Function<Integer, StorageKey.StorageFunction<byte[]>>> createRuntimeFunction(String method, String key, SubstrateMetadata substrateMetadata) {



        return new StorageKey.StorageFunction<Function<Integer, StorageKey.StorageFunction<byte[]>>>() {

            @Override
            public Function<Integer, StorageKey.StorageFunction<byte[]>> apply(Object... args) throws InvocationTargetException, IllegalAccessException {

                return  metadataVersion -> {
                    Map<String, Object> metaValues = new LinkedHashMap<>();

                    metaValues.put("documentation", new Vector<Text>(TypesUtils.getConstructorCodec(Text.class), Lists.newArrayList(substrateMetadata.getDocumentation())));
                    metaValues.put("modifier", new Storage.StorageEntryModifierLatest(1));
                    metaValues.put("type", new Storage.StorageEntryTypeLatest(substrateMetadata.getType(), 0));
                    metaValues.put("name", new Text("name"));
                    metaValues.put("fallback", new Bytes(null));

                    Storage.StorageEntryMetadataLatest storageMetadataLatest = new Storage.StorageEntryMetadataLatest(metaValues) {
                        @Override
                        public Object toJson() {
                            return key;
                        }
                    };

                    StorageKey.StorageFunction<byte[]> storageFunction = null;
                    try {
                        storageFunction = CreateFunction.createFunction(
                                new CreateFunction.CreateItemFn(storageMetadataLatest, method, "Substrate", "substrate"),
                                new CreateFunction.CreateItemOptions( key, metadataVersion, true)
                        );
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                    allFunctions.put(method, storageFunction);

                    return storageFunction;
                };
            }

            @Override
            public Object toJson() {
                return null;
            }
        };


    }

    public static Map<String, StorageKey.StorageFunction> allFunctions = new HashMap<>();


    public static StorageKey.StorageFunction code = createRuntimeFunction(
            "code",
            ":code",
            new SubstrateMetadata("Wasm code of the runtime.",
                    "Bytes")
    );

    public static StorageKey.StorageFunction heapPages = createRuntimeFunction(
            "heapPages",
            ":heappages",
            new SubstrateMetadata(
                    "Number of wasm linear memory pages required for execution of the runtime.",
                    "u64"
            ));

    public static StorageKey.StorageFunction authorityCount = createRuntimeFunction(
            "authorityCount",
            ":auth:len",
            new SubstrateMetadata(
                    "Number of authorities.",
                    "u32"
            ));

    public static StorageKey.StorageFunction authorityPrefix = createRuntimeFunction(
            "authorityPrefix",
            ":auth:",
            new SubstrateMetadata(
                    "Prefix under which authorities are storied.",
                    "u32"
            ));

    public static StorageKey.StorageFunction extrinsicIndex = createRuntimeFunction(
            "extrinsicIndex",
            ":extrinsic_index",
            new SubstrateMetadata(
                    "Current extrinsic index (u32) is stored under this key.",
                    "u32"
            ));

    public static StorageKey.StorageFunction changesTrieConfig = createRuntimeFunction(
            "changesTrieConfig",
            ":changes_trie",
            new SubstrateMetadata(
                    "Changes trie configuration is stored under this key.",
                    "u32"
            ));

    public static Types.ModuleStorage substrate = new Types.ModuleStorage();

    static {
        substrate.addFunction("code", code);
        substrate.addFunction("heapPages", heapPages);
        substrate.addFunction("authorityCount", authorityCount);
        substrate.addFunction("authorityPrefix", authorityPrefix);
        substrate.addFunction("extrinsicIndex", extrinsicIndex);
        substrate.addFunction("changesTrieConfig", changesTrieConfig);
    }
}
