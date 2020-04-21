package org.polkadot.types.metadata;

import org.polkadot.types.Types.ConstructorDef;
import org.polkadot.types.codec.EnumType;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.metadata.latest.MetadataLatest;
import org.polkadot.types.metadata.v0.MetadataV0;
import org.polkadot.types.metadata.v0.ToV1;
import org.polkadot.types.metadata.v1.MetadataV1;
import org.polkadot.types.metadata.v1.ToV2;
import org.polkadot.types.metadata.v11.MetadataV11;
import org.polkadot.types.metadata.v11.ToLatest;
import org.polkadot.types.metadata.v2.MetadataV2;
import org.polkadot.types.metadata.v2.ToV3;
import org.polkadot.types.metadata.v3.MetadataV3;
import org.polkadot.types.metadata.v3.ToV11;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

/**
 * The versioned runtime metadata as a decoded structure
 */
public class MetadataVersioned extends Struct implements Types.MetadataInterface {

    public static class MetadataEnum extends EnumType<Types.MetadataInterface> {

        public MetadataEnum(Object value) {
            super(new ConstructorDef()
                            .add("MetadataV0", MetadataV0.class)
                            .add("MetadataV1", MetadataV1.class)
                            .add("MetadataV2", MetadataV2.class)
                            .add("MetadataV3", MetadataV3.class)
                            .add("MetadataV4", MetadataV3.class)
                            .add("MetadataV5", MetadataV3.class)
                            .add("MetadataV6", MetadataV3.class)
                            .add("MetadataV7", MetadataV3.class)
                            .add("MetadataV8", MetadataV3.class)
                            .add("MetadataV9", MetadataV3.class)
                            .add("MetadataV10", MetadataV3.class)
                            .add("MetadataV11", MetadataV11.class)
                    , value, -1, null
            );
        }


        /**
         * Returns the wrapped values as a V0 object
         */
        public MetadataV0 asV0() {
            return ((MetadataV0) this.value());
        }

        /**
         * Returns the wrapped values as a V1 object
         */
        public MetadataV1 asV1() {
            return ((MetadataV1) this.value());
        }

        /**
         * Returns the wrapped values as a V2 object
         */
        public MetadataV2 asV2() {
            return ((MetadataV2) this.value());
        }


        /**
         * Returns the wrapped values as a V3 object
         */
        public MetadataV3 asV3() {
            return ((MetadataV3) this.value());
        }

        /**
         * Returns the wrapped values as a V11 object
         */
        public MetadataV11 asV11() {
            return ((MetadataV11) this.value());
        }

        /**
         * Returns the wrapped values as a latest version object
         */
        public MetadataLatest asLatest () {
            return ((MetadataLatest) this.value());
        }

        /**
         * The version this metadata represents
         */
        public int getVersion() {
            return this.index();
        }
    }


    private static HashMap<Integer, Struct> converted = new HashMap<>();

    public MetadataVersioned(Object value) {
        super(new ConstructorDef()
                        .add("magicNumber", MagicNumber.class)
                        .add("metadata", MetadataEnum.class)
                , value);
    }

    /**
     * the metadata version this structure represents
     */
    public int getVersion() {
        return ((MetadataEnum) this.getField("metadata")).getVersion();
    }

    /**
     * the metadata wrapped
     */
    private MetadataEnum getMetadata() {
        return this.getField("metadata");
    }

    private boolean assertVersion(int version) {
        assert (this.getVersion() <= version) : "Cannot convert metadata from v" + this.getVersion() + " to v" + version;

        return this.getVersion() == version;
    }

    @FunctionalInterface
    private interface VersionUpgrade<T, F extends Struct & Types.MetadataInterface> {
        T fromPrev(F input);
    }

    private <T extends Struct & Types.MetadataInterface> Struct asVersion(int version, VersionUpgrade versionUpgrade) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final String asCurr = "asV" + version;
        final String asPrev = "asV" + (version - 1);

        if (this.assertVersion(version)) {
            Method metadataAsVersion = this.getMetadata().getClass().getMethod(asCurr);

            return (Struct) metadataAsVersion.invoke(this.getMetadata());
        }

        if (!converted.containsKey(version)) {
            Method metadataAsVersion = this.getClass().getMethod(asPrev);
            T input = (T) metadataAsVersion.invoke(this);
            converted.put(version, (Struct) versionUpgrade.fromPrev(input));
        }

        return converted.get(version);
    }

    /**
     * Returns the wrapped values as a V0 object
     */
    public MetadataV0 asV0() {
        this.assertVersion(0);

        return this.getMetadata().asV0();
    }

    /**
     * Returns the wrapped values as a V1 object
     */
    public MetadataV1 asV1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return (MetadataV1) this.asVersion(1, (input) -> ToV1.toV1((MetadataV0) input));
    }

    /**
     * Returns the wrapped values as a V2 object
     */
    public MetadataV2 asV2() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return (MetadataV2) this.asVersion(2, (input) -> ToV2.toV2((MetadataV1) input));
    }

    /**
     * Returns the wrapped values as a V3 object
     */
    public MetadataV3 asV3() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return (MetadataV3) this.asVersion(3, (input) -> ToV3.toV3((MetadataV2) input));
    }

    /**
     * Returns the wrapped values as a V11 object
     */
    public MetadataV11 asV11() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return (MetadataV11) this.asVersion(11, (input) -> ToV11.toV11((MetadataV3) input));
    }

    /**
     * Returns the wrapped values as a latest version object
     */
    public MetadataLatest asLatest() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return (MetadataLatest) this.asVersion(12, (input) -> ToLatest.toLatest((MetadataV11) input));
    }


    @Override
    public List<String> getUniqTypes(boolean throwError) {
        return ((Types.MetadataInterface) this.getMetadata().value()).getUniqTypes(throwError);
    }
}
