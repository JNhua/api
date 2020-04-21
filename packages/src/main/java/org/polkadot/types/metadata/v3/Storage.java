package org.polkadot.types.metadata.v3;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.EnumType;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.primitive.Bytes;
import org.polkadot.types.primitive.Text;
import org.polkadot.types.metadata.v0.Storage.StorageFunctionModifierV0;
import org.polkadot.types.primitive.Type;
import org.polkadot.types.metadata.v2.Storage.MapTypeV2;

public interface Storage {

    class DoubleMapTypeV3 extends Struct {
        public DoubleMapTypeV3(Object value) {
            super(new Types.ConstructorDef()
                            .add("key1", Text.class)
                            .add("key2", Text.class)
                            .add("value", Text.class)
                            .add("keyHasher", Text.class)
                    , value);
        }

        /**
         * The mapped key as {@link org.polkadot.types.primitive.Text}
         */
        public Text getKey1() {
            return this.getField("key1");
        }

        /**
         * The mapped key as {@link org.polkadot.types.primitive.Text}
         */
        public Text getKey2() {
            return this.getField("key2");
        }

        /**
         * The mapped key as {@link org.polkadot.types.primitive.Text}
         */
        public Text getKeyHasher() {
            return this.getField("keyHasher");
        }

        /**
         * The mapped key as {@link org.polkadot.types.primitive.Text}
         */
        public Text getValue() {
            return this.getField("value");
        }
    }

    class PlainTypeV3 extends Type{
        public PlainTypeV3(Object value) {
            super(value);
        }
    }

    class MapTypeV3 extends MapTypeV2{

        public MapTypeV3(Object value) {
            super(value);
        }
    }

    //EnumType<PlainType | MapType | DoubleMapType>
    class StorageFunctionTypeV3 extends EnumType {
        public StorageFunctionTypeV3(Object value, int index) {
            super(new Types.ConstructorDef()
                            .add("PlainType", PlainTypeV3.class)
                            .add("MapType", MapTypeV3.class)
                            .add("DoubleMapType", DoubleMapTypeV3.class)
                    , value, index, null);
        }

        public StorageFunctionTypeV3(Object value) {
            this(value, -1);
        }


        /**
         * The value as a mapped value
         */
        public DoubleMapTypeV3 asDoubleMap() {
            return (DoubleMapTypeV3) this.value();
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
        public MapTypeV3 asMap() {
            return (MapTypeV3) this.value();
        }

        /**
         * The value as a {@link org.polkadot.types.type} value
         */
        public PlainTypeV3 asType() {
            return (PlainTypeV3) this.value();
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

    class StorageFunctionModifierV3 extends StorageFunctionModifierV0 {

        public StorageFunctionModifierV3(Object value) {
            super(value);
        }
    }

    /**
     * The definition of a storage function
     */
    class StorageFunctionMetadataV3 extends Struct {
        public StorageFunctionMetadataV3(Object value) {
            super(new Types.ConstructorDef()
                            .add("name", Text.class)
                            .add("modifier", StorageFunctionModifierV3.class)
                            .add("type", StorageFunctionTypeV3.class)
                            .add("fallback", Bytes.class)
                            .add("documentation", Vector.with(TypesUtils.getConstructorCodec(Text.class)))
                    , value);
        }

        /**
         * The {@link org.polkadot.types.primitive.Text} documentation
         */
        public Vector<Text> getDocumentation() {
            return this.getField("documentation");
        }

        /**
         * The {@link org.polkadot.types.primitive.Bytes} fallback default
         */
        public Bytes getFallback() {
            return this.getField("fallback");
        }

        /**
         * The MetadataArgument for arguments
         */
        public StorageFunctionModifierV0 getModifier() {
            return this.getField("modifier");
        }

        /**
         * The call name
         */
        public Text getName() {
            return this.getField("name");
        }

        /**
         * The MetadataStorageType
         */
        public StorageFunctionTypeV3 getType() {
            return this.getField("type");
        }
    }

}
