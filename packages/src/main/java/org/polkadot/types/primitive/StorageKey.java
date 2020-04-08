package org.polkadot.types.primitive;


import org.apache.commons.lang3.ArrayUtils;
import org.polkadot.direct.IFunction;
import org.polkadot.types.codec.CodecUtils;
import org.polkadot.types.codec.U8a;
import org.polkadot.types.metadata.v0.Modules;

import java.util.List;

/**
 * A representation of a storage key (typically hashed) in the system. It can be
 * constructed by passing in a raw key or a StorageFunction with (optional) arguments.
 */
public class StorageKey extends Bytes {

    public static abstract class StorageFunction implements IFunction<byte[]> {
        public abstract byte[] apply(Object... args);

        protected Modules.StorageFunctionMetadata meta;
        protected String method;
        protected String section;

        public abstract Object toJson();

        StorageKey headKey;
        //(arg?: any): Uint8Array;
        //      meta: StorageFunctionMetadata;
        //      method: string;
        //      section: string;
        //      toJSON: () => any;
        //      headKey?: Uint8Array;


        public Modules.StorageFunctionMetadata getMeta() {
            return meta;
        }

        public void setMeta(Modules.StorageFunctionMetadata meta) {
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

        public StorageKey getHeadKey() {
            return headKey;
        }

        public void setHeadKey(StorageKey headKey) {
            this.headKey = headKey;
        }
    }


    private Modules.StorageFunctionMetadata meta;
    private String outputType;

    public StorageKey(Object value) {
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

    static Modules.StorageFunctionMetadata getMeta(Object value) {
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

    static Object decodeStorageKey(Object value) {
        if (value instanceof IFunction) {
            byte[] apply = ((StorageFunction) value).apply();
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
    public Modules.StorageFunctionMetadata getMeta() {
        return meta;
    }

    /**
     * The output type, `null` when not available
     */
    public String getOutputType() {
        return outputType;
    }

}