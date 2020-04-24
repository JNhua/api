package org.polkadot.type.storage;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.polkadot.types.codec.CreateType;
import org.polkadot.types.metadata.latest.Storage;
import org.polkadot.types.primitive.Bytes;
import org.polkadot.types.primitive.StorageKey;
import org.polkadot.utils.Utils;
import org.polkadot.utils.UtilsCrypto;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class CreateFunction {

    public static class CreateItemOptions {
        private boolean skipHashing;
        private String key;
        private int metaVersion;


        public CreateItemOptions(String key, int metaVersion, boolean skipHashing) {
            this.skipHashing = skipHashing;
            this.key = key;
            this.metaVersion = metaVersion;
        }
    }

    public static class CreateItemFn {
        private Storage.StorageEntryMetadataLatest meta;
        private String method;
        private String prefix;
        private String section;

        public CreateItemFn(Storage.StorageEntryMetadataLatest meta, String method, String prefix, String section) {
            this.meta = meta;
            this.method = method;
            this.prefix = prefix;
            this.section = section;
        }

        public Storage.StorageEntryMetadataLatest getMeta() {
            return meta;
        }

        public String getPrefix() {
            return prefix;
        }

        public String getMethod() {
            return method;
        }

        public String getSection() {
            return section;
        }
    }

    static byte[] createPrefixedKey(CreateItemFn itemFn) {
        return Utils.u8aConcat(Lists.newArrayList(
                UtilsCrypto.xxhashAsU8a(Utils.stringToU8a(itemFn.getPrefix()), 128),
                UtilsCrypto.xxhashAsU8a(Utils.stringToU8a(itemFn.getMethod()), 128)
        ));
    }

    static StorageKey.StorageFunction<byte[]> createKey(CreateItemFn itemFn, String stringKey, Method hasher, int metaVersion) {
        return new StorageKey.StorageFunction<byte[]>() {
            @Override
            public byte[] apply(Object... args) throws InvocationTargetException, IllegalAccessException {
                byte[] param = new byte[0];
                if (meta.getType().isMap()) {
                    //assert args == null || args.length != 1
                    assert ArrayUtils.isNotEmpty(args) && args.length == 1
                            : meta.getName() + "expects one argument";

                    String type = meta.getType().asMap().getKey().toString();
                    param = CreateType.createType(type, args[0]).toU8a(false);
                }

                // StorageKey is a Bytes, so is length-prefixed
                return Utils.compactAddLength(
                        metaVersion <= 8
                                ? (byte[]) hasher.invoke(UtilsCrypto.class, (Object) Utils.u8aConcat(Lists.newArrayList(Utils.stringToU8a(stringKey), param)))
                                : Utils.u8aConcat(
                                Lists.newArrayList(
                                        createPrefixedKey(itemFn),
                                        (byte[]) (param.length > 0 ? hasher.invoke(UtilsCrypto.class, (Object) param) : param)
                                ))
                );
            }

            @Override
            public Object toJson() {
                return this.getMeta().toJson();
            }
        };
    }

    static StorageKey.StorageFunction<byte[]> createKeyDoubleMap(CreateItemFn itemFn, String stringKey, Method hasher1, Optional<Method> hasher2, int metaVersion) {
        return new StorageKey.StorageFunction<byte[]>() {
            @Override
            public byte[] apply(Object... args) throws InvocationTargetException, IllegalAccessException {
                assert ArrayUtils.isNotEmpty(args) && args.length == 2 && args[0] != null && args[1] != null
                        : meta.getName() + "is a DoubleMap and requires two arguments";

                // if this fails, we have bigger issues
                assert hasher2.isPresent() : "2 hashing functions should be defined for DoubleMaps";


                Storage.DoubleMapTypeLatest map = meta.getType().asDoubleMap();
                byte[] val1 = CreateType.createType(map.getKey1().toString(), args[0]).toU8a(false);
                byte[] val2 = CreateType.createType(map.getKey2().toString(), args[1]).toU8a(false);

                // StorageKey is a Bytes, so is length-prefixed
                return Utils.compactAddLength(
                        metaVersion <= 8
                                ? Utils.u8aConcat(Lists.newArrayList(
                                (byte[]) hasher1.invoke(UtilsCrypto.class, (Object) Utils.u8aConcat(Lists.newArrayList(Utils.stringToU8a(stringKey), val1))),
                                (byte[]) hasher2.get().invoke(UtilsCrypto.class, (Object) val2)))
                                : Utils.u8aConcat(
                                Lists.newArrayList(
                                        createPrefixedKey(itemFn),
                                        (byte[]) hasher1.invoke(UtilsCrypto.class, (Object) val1),
                                        (byte[]) hasher2.get().invoke(UtilsCrypto.class, (Object) val2)))
                );
            }

            @Override
            public Object toJson() {
                return this.getMeta().toJson();
            }
        };
    }

    static StorageKey.StorageFunction<byte[]> extendHeadMeta(CreateItemFn itemFn, byte[] key) {
        final Storage.StorageEntryMetadataLatest meta = itemFn.getMeta();
        final Storage.StorageEntryTypeLatest type = meta.getType();
        final String outputType = type.isMap() ? type.asMap().getKey().toString() : type.asDoubleMap().getKey1().toString();

        StorageKey.StorageFunction<byte[]> keyFn = new StorageKey.StorageFunction<byte[]>() {
            @Override
            public byte[] apply(Object... args) {
                return key;
            }

            @Override
            public Object toJson() {
                return this.getMeta().toJson();
            }
        };

        Map<String, Object> metaValues = new LinkedHashMap<>();
        metaValues.put("name", meta.getName());
        metaValues.put("modifier", new Storage.StorageEntryModifierLatest("Required"));
        metaValues.put("type", new Storage.StorageEntryTypeLatest(new Storage.PlainTypeLatest(type.isMap() ? type.asMap().getKey() : type.asDoubleMap().getKey1()), 0));
        metaValues.put("fallback", new Bytes(outputType));
        metaValues.put("documentation", meta.getDocumentation());

        keyFn.setMeta(new Storage.StorageEntryMetadataLatest(metaValues));

        return keyFn;
    }

    /**
     * From the schema of a function in the module's storage, generate the function
     * that will return the correct storage key.
     *
     * @param options - Additional options when creating the function. These options
     *                are not known at runtime (from state_getMetadata), they need to be supplied
     *                by us manually at compile time.
     */
    public static StorageKey.StorageFunction<byte[]> createFunction(CreateItemFn itemFn,
                                                                    CreateItemOptions options
    ) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Storage.StorageEntryMetadataLatest meta = itemFn.getMeta();
        final String prefix = itemFn.getPrefix();
        final String method = itemFn.getMethod();
        final String section = itemFn.getSection();
        Storage.StorageEntryTypeLatest type = meta.getType();

        String stringKey = StringUtils.isNotEmpty(options.key)
                ? options.key
                : prefix + " " + method;

        // Can only have zero or one argument:
        // - storage.balances.freeBalance(address)
        // - storage.timestamp.blockPeriod()
        // For double_map queries the params is passed in as an tuple, [key1, key2]
        Class<?> getHasherClass = getHasher.class;
        StorageKey.StorageFunction<byte[]> storageFn = type.isDoubleMap()
                ? createKeyDoubleMap(
                itemFn,
                stringKey,
                getHasherClass.getMethod(Utils.stringCamelCase(type.asDoubleMap().getHasher().getType()), byte[].class),
                Optional.ofNullable(getHasherClass.getMethod(Utils.stringCamelCase(type.asDoubleMap().getKey2Hasher().getType()), byte[].class)),
                options.metaVersion)
                : createKey(
                itemFn,
                stringKey,
                options.skipHashing
                        ? getHasherClass.getMethod("identity", byte[].class)
                        : getHasherClass.getMethod(
                        type.isMap()
                                ? Utils.stringCamelCase(type.asMap().getHasher().getType())
                                : "defaultFn", byte[].class),
                options.metaVersion);

        storageFn.setMeta(meta);
        storageFn.setMethod(Utils.stringLowerFirst(method));
        storageFn.setSection(section);
        storageFn.setPrefix(prefix);

        if (meta.getType().isMap()) {
            final Storage.MapTypeLatest map = type.asMap();
            if (map.isLinked().rawBool()) {
                byte[] key = options.metaVersion <= 8
                        ? UtilsCrypto.xxhashAsU8a(("head of " + stringKey).getBytes(), 128)
                        : Utils.u8aConcat(
                        Lists.newArrayList(
                                UtilsCrypto.xxhashAsU8a(Utils.stringToU8a(itemFn.getPrefix()), 128),
                                UtilsCrypto.xxhashAsU8a(Utils.stringToU8a("HeadOf" + itemFn.getMethod()), 128)
                        ));
                storageFn.setIterKey(new StorageKey(extendHeadMeta(itemFn, key)));
            } else {
                storageFn.setIterKey(new StorageKey(extendHeadMeta(itemFn, createPrefixedKey(itemFn))));
            }
        } else if (type.isDoubleMap()) {
            storageFn.setIterKey(new StorageKey(extendHeadMeta(itemFn, createPrefixedKey(itemFn))));
        }

        return storageFn;
    }
}
