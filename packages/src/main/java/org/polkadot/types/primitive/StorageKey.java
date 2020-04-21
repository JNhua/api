package org.polkadot.types.primitive;


import org.apache.commons.lang3.ArrayUtils;
import org.polkadot.direct.IFunction;
import org.polkadot.types.codec.CodecUtils;
import org.polkadot.types.codec.U8a;
import org.polkadot.types.metadata.latest.Storage;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * A representation of a storage key (typically hashed) in the system. It can be
 * constructed by passing in a raw key or a StorageFunction with (optional) arguments.
 */
public class StorageKey extends Bytes {

    public static abstract class StorageFunction<R> implements IFunction<R> {
        public abstract R apply(Object... args) throws InvocationTargetException, IllegalAccessException;

        protected Storage.StorageEntryMetadataLatest meta;
        protected String method;
        protected String section;
        protected String prefix;

        public abstract Object toJson();

        StorageKey iterKey;

        public Storage.StorageEntryMetadataLatest getMeta() {
            return meta;
        }

        public void setMeta(Storage.StorageEntryMetadataLatest meta) {
            this.meta = meta;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getSection() {
            return section;
        }

        public void setSection(String section) {
            this.section = section;
        }

        public StorageKey getIterKey() {
            return iterKey;
        }

        public void setIterKey(StorageKey iterKey) {
            this.iterKey = iterKey;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }
    }


    private Storage.StorageEntryMetadataLatest meta;
    private String outputType;

    public StorageKey(Object value) throws InvocationTargetException, IllegalAccessException {
        super(decodeStorageKey(value));

        this.meta = getMeta(value);
        this.outputType = getType(value);
    }

    static String getType(Object value) {
        if (value instanceof StorageKey) {
            return ((StorageKey) value).outputType;
        } else if (value instanceof StorageFunction) {
            return ((StorageFunction) value).meta.getType().toString();
        } else if (value.getClass().isArray()) {
            List<Object> elements = CodecUtils.arrayLikeToList(value);
            return ((StorageFunction) elements.get(0)).meta.getType().toString();
        }
        return null;
    }

    static Storage.StorageEntryMetadataLatest getMeta(Object value) {
        if (value instanceof StorageKey) {
            return ((StorageKey) value).meta;
        } else if (value instanceof StorageFunction) {
            return ((StorageFunction) value).meta;
        } else if (value.getClass().isArray()) {
            List<Object> elements = CodecUtils.arrayLikeToList(value);
            return ((StorageFunction) elements.get(0)).meta;
        }
        return null;
    }

    static Object decodeStorageKey(Object value) throws InvocationTargetException, IllegalAccessException {
        if (value instanceof IFunction) {
            Object apply = ((StorageFunction<Object>) value).apply();
            return new U8a(apply);
        } else if (value.getClass().isArray()) {
            List<Object> elements = CodecUtils.arrayLikeToList(value);
            Object remove = elements.remove(0);
            if (remove instanceof StorageFunction) {
                Object arg = elements.get(0);
                List<Object> args = CodecUtils.arrayLikeToList(arg);
                return ((StorageFunction) remove).apply(args.toArray(ArrayUtils.EMPTY_OBJECT_ARRAY));
            }
        }

        return value;
    }


    /**
     * The metadata or `null` when not available
     */
    public Storage.StorageEntryMetadataLatest getMeta() {
        return meta;
    }

    /**
     * The output type, `null` when not available
     */
    public String getOutputType() {
        return outputType;
    }

}