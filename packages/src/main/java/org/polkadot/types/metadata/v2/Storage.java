package org.polkadot.types.metadata.v2;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.EnumType;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.metadata.v0.Storage.StorageFunctionModifierV0;
import org.polkadot.types.primitive.*;

public interface Storage {

    class PlainTypeV2 extends Type {
        public PlainTypeV2(Object value) {
            super(value);
        }
    }

    class MapTypeV2 extends Struct {
        public MapTypeV2(Object value) {
            super(new Types.ConstructorDef()
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


    class StorageFunctionTypeV2 extends EnumType {

        public StorageFunctionTypeV2(Object value) {
            this(value, -1);
        }

        public StorageFunctionTypeV2(Object value, int index) {
            super(new Types.ConstructorDef()
                            .add("PlainType", PlainTypeV2.class)
                            .add("MapType", MapTypeV2.class)
                    , value, index, null);
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
        public MapTypeV2 asMap() {
            return (MapTypeV2) this.value();
        }

        /**
         * The value as a {@link org.polkadot.types.type} value
         */
        public PlainTypeV2 asType() {
            return (PlainTypeV2) this.value();
        }

        /**
         * Returns the string representation of the value
         */


        @Override
        public String toString() {
            return this.isMap()
                    ? this.asMap().getValue().toString()
                    : this.asType().toString();
        }
    }

    /**
     * The definition of a storage function
     */
    class MetadataStorageV2 extends Struct {
        public MetadataStorageV2(Object value) {
            super(new Types.ConstructorDef()
                            .add("name", Text.class)
                            .add("modifier", StorageFunctionModifierV0.class)
                            .add("type", StorageFunctionTypeV2.class)
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
        public StorageFunctionTypeV2 getType() {
            return this.getField("type");
        }
    }

}
