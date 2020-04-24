package org.polkadot.types.metadata.v11;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.EnumType;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.metadata.v3.Storage.StorageFunctionModifierV3;
import org.polkadot.types.primitive.*;

public interface StorageV11 {

    class Blake2_128 extends Null {
    }

    class Blake2_256 extends Null {
    }

    class Blake2_128Concat extends Null {
    }

    class Twox128 extends Null {
    }

    class Twox256 extends Null {
    }

    class Twox64Concat extends Null {
    }

    class Identity extends Null {
    }

    class StorageHasherV11 extends EnumType {

        public StorageHasherV11(Object value, int index) {
            super(new Types.ConstructorDef()
                            .add("Blake2_128", Blake2_128.class)
                            .add("Blake2_256", Blake2_256.class)
                            .add("Blake2_128Concat", Blake2_128Concat.class)
                            .add("Twox128", Twox128.class)
                            .add("Twox256", Twox256.class)
                            .add("Twox64Concat", Twox64Concat.class)
                            .add("Identity", Identity.class)
                    , value, index, null);
        }

        public StorageHasherV11(Object value) {
            this(value, -1);
        }


        /**
         * `true` if the storage entry is a Blake2_128
         */
        public boolean isBlake2128() {
            return this.toNumber() == 0;
        }

        @Override
        public Object toJson() {
            return this.toString();
        }
    }


    class MapTypeV11 extends Struct {
        public MapTypeV11(Object value) {
            super(new Types.ConstructorDef()
                            .add("hasher", StorageHasherV11.class)
                            .add("key", Type.class)
                            .add("value", Type.class)
                            .add("isLinked", Bool.class)
                    , value);
        }


        /**
         * The mapped key as {@link StorageHasherV11}
         */
        public StorageHasherV11 getHasher() {
            return this.getField("hasher");
        }

        /**
         * The mapped key as {@link org.polkadot.types.type}
         */
        public Type getKey() {
            return this.getField("key");
        }

        /**
         * The mapped value as {@link org.polkadot.types.type}
         */
        public Type getValue() {
            return this.getField("value");
        }

        /**
         * Is this an enumerable linked map
         */
        public Bool isLinked() {
            return this.getField("isLinked");
        }
    }

    class DoubleMapTypeV11 extends Struct {
        public DoubleMapTypeV11(Object value) {
            super(new Types.ConstructorDef()
                            .add("hasher", StorageHasherV11.class)
                            .add("key1", Type.class)
                            .add("key2", Type.class)
                            .add("value", Type.class)
                            .add("key2Hasher", StorageHasherV11.class)
                    , value);
        }

        /**
         * The mapped key as {@link StorageHasherV11}
         */
        public StorageHasherV11 getHasher() {
            return this.getField("hasher");
        }

        /**
         * The mapped key as {@link Text}
         */
        public Type getKey1() {
            return this.getField("key1");
        }

        /**
         * The mapped key as {@link Text}
         */
        public Type getKey2() {
            return this.getField("key2");
        }

        /**
         * The mapped key as {@link StorageHasherV11}
         */
        public StorageHasherV11 getKey2Hasher() {
            return this.getField("key2Hasher");
        }

        /**
         * The mapped key as {@link Text}
         */
        public Type getValue() {
            return this.getField("value");
        }
    }

    class PlainTypeV11 extends Type {

        public PlainTypeV11(Object value) {
            super(value);
        }
    }

    //EnumType<PlainType | MapType | DoubleMapType>
    class StorageEntryTypeV11 extends EnumType {
        public StorageEntryTypeV11(Object value, int index) {
            super(new Types.ConstructorDef()
                            .add("PlainType", PlainTypeV11.class)
                            .add("MapType", MapTypeV11.class)
                            .add("DoubleMapType", DoubleMapTypeV11.class)
                    , value, index, null);
        }

        public StorageEntryTypeV11(Object value) {
            this(value, -1);
        }


        /**
         * The value as a mapped value
         */
        public DoubleMapTypeV11 asDoubleMap() {
            return (DoubleMapTypeV11) this.value();
        }


        /**
         * `true` if the storage entry is a doublemap
         */
        public boolean isDoubleMap() {
            return this.toNumber() == 2;
        }

        /**
         * `true` if the storage entry is a map
         */
        public boolean isMap() {
            return this.toNumber() == 1;
        }

        /**
         * The value as a mapped value
         */
        public MapTypeV11 asMap() {
            return (MapTypeV11) this.value();
        }

        /**
         * The value as a {@link org.polkadot.types.type} value
         */
        public PlainTypeV11 asPlain() {
            return (PlainTypeV11) this.value();
        }

        /**
         * Returns the string representation of the value
         */


        @Override
        public String toString() {

            if (this.isDoubleMap()) {
                return this.asDoubleMap().toString();
            }

            return this.isMap()
                    ? this.asMap().getValue().toString()
                    : this.asPlain().toString();
        }
    }

    class StorageEntryModifierV11 extends StorageFunctionModifierV3 {

        public StorageEntryModifierV11(Object value) {
            super(value);
        }
    }

    /**
     * The definition of an item
     */
    class StorageEntryMetadataV11 extends Struct {

        public StorageEntryMetadataV11(Object value) {
            super(new Types.ConstructorDef()
                            .add("name", Text.class)
                            .add("modifier", StorageEntryModifierV11.class)
                            .add("type", StorageEntryTypeV11.class)
                            .add("fallback", Bytes.class)
                            .add("documentation", Vector.with(TypesUtils.getConstructorCodec(Text.class)))
                    , value);
        }

        /**
         * The {@link Text} documentation
         */
        public Vector<Text> getDocumentation() {
            return this.getField("documentation");
        }

        /**
         * The {@link Bytes} fallback default
         */
        public Bytes getFallback() {
            return this.getField("fallback");
        }

        /**
         * The MetadataArgument for arguments
         */
        public StorageEntryModifierV11 getModifier() {
            return this.getField("modifier");
        }

        /**
         * The item name
         */
        public Text getName() {
            return this.getField("name");
        }

        /**
         * The {@link StorageEntryTypeV11} MetadataStorageType
         */
        public StorageEntryTypeV11 getType() {
            return this.getField("type");
        }
    }

    /**
     * The definition of a storage function
     */
    class StorageMetadataV11 extends Struct {
        public StorageMetadataV11(Object value) {
            super(new Types.ConstructorDef()
                            .add("prefix", Text.class)
                            .add("items", Vector.with(TypesUtils.getConstructorCodec(StorageEntryMetadataV11.class)))
                    , value);
        }


        /**
         * The prefix
         */
        public Text getPrefix() {
            return this.getField("prefix");
        }

        /**
         * The {@link StorageEntryMetadataV11} items
         */
        public Vector<StorageEntryMetadataV11> getItems() {
            return this.getField("items");
        }
    }


}
