package org.polkadot.types.metadata.v11;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.EnumType;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.metadata.v2.Storage.PlainType;
import org.polkadot.types.metadata.v11.Items.MetadataStorageItem;
import org.polkadot.types.primitive.Bool;
import org.polkadot.types.primitive.Null;
import org.polkadot.types.primitive.Text;
import org.polkadot.types.primitive.Type;

public interface Storage {

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

    class StorageHasher extends EnumType {

        public StorageHasher(Object value, int index) {
            super(new Types.ConstructorDef()
                            .add("Blake2_128", Blake2_128.class)
                            .add("Blake2_256", Blake2_256.class)
                            .add("Blake2_128Concat", Blake2_128Concat.class)
                            .add("Twox128", Twox128.class)
                            .add("Twox256", Twox256.class)
                            .add("Twox64Concat", Twox64Concat.class)
                    , value, index, null);
        }

        public StorageHasher(Object value) {
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


    class MapType extends Struct {
        public MapType(Object value) {
            super(new Types.ConstructorDef()
                            .add("hasher", StorageHasher.class)
                            .add("key", Type.class)
                            .add("value", Type.class)
                            .add("isLinked", Bool.class)
                    , value);
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
        public Boolean isLinked() {
            return this.getField("isLinked");
        }
    }

    class DoubleMapType extends Struct {
        public DoubleMapType(Object value) {
            super(new Types.ConstructorDef()
                            .add("hasher", StorageHasher.class)
                            .add("key1", Type.class)
                            .add("key2", Type.class)
                            .add("value", Type.class)
                            .add("key2Hasher", StorageHasher.class)
                    , value);
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
         * The mapped key as {@link Text}
         */
        public StorageHasher getKeyHasher() {
            return this.getField("keyHasher");
        }

        /**
         * The mapped key as {@link Text}
         */
        public Type getValue() {
            return this.getField("value");
        }
    }


    //EnumType<PlainType | MapType | DoubleMapType>
    class MetadataStorageType extends EnumType {
        public MetadataStorageType(Object value, int index) {
            super(new Types.ConstructorDef()
                            .add("PlainType", PlainType.class)
                            .add("MapType", MapType.class)
                            .add("DoubleMapType", DoubleMapType.class)
                    , value, index, null);
        }

        public MetadataStorageType(Object value) {
            this(value, -1);
        }


        /**
         * The value as a mapped value
         */
        public DoubleMapType asDoubleMap() {
            return (DoubleMapType) this.value();
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
        public MapType asMap() {
            return (MapType) this.value();
        }

        /**
         * The value as a {@link org.polkadot.types.type} value
         */
        public PlainType asType() {
            return (PlainType) this.value();
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
                    : this.asType().toString();
        }
    }


    /**
     * The definition of a storage function
     */
    class MetadataStorageV11 extends Struct {
        public MetadataStorageV11(Object value) {
            super(new Types.ConstructorDef()
                            .add("prefix", Text.class)
                            .add("items", Vector.with(TypesUtils.getConstructorCodec(MetadataStorageItem.class)))
                    , value);
        }


        /**
         * The prefix
         */
        public Text getPrefix() {
            return this.getField("prefix");
        }

        /**
         * The {@link MetadataStorageItem} items
         */
        public Vector<MetadataStorageItem> getItems() {
            return this.getField("items");
        }
    }


}
