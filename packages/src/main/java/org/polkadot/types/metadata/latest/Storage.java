package org.polkadot.types.metadata.latest;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.EnumType;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.primitive.Bytes;
import org.polkadot.types.primitive.Text;
import org.polkadot.types.primitive.Type;
import org.polkadot.types.metadata.v11.StorageV11;

/**
 * @author Jenner
 * @version V1.0
 * @Date 2020/4/16 5:23 下午
 */
public interface Storage {

    class StorageEntryModifierLatest extends StorageV11.StorageEntryModifierV11 {

        public StorageEntryModifierLatest(Object value) {
            super(value);
        }
    }

    /**
     * The definition of an item
     */
    class StorageEntryMetadataLatest extends Struct{

        public StorageEntryMetadataLatest(Object value) {
            super(new Types.ConstructorDef()
                            .add("name", Text.class)
                            .add("modifier", StorageEntryModifierLatest.class)
                            .add("type", StorageEntryTypeLatest.class)
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
        public StorageEntryModifierLatest getModifier() {
            return this.getField("modifier");
        }

        /**
         * The item name
         */
        public Text getName() {
            return this.getField("name");
        }

        /**
         * The {@link StorageEntryTypeLatest} MetadataStorageType
         */
        public StorageEntryTypeLatest getType() {
            return this.getField("type");
        }
    }

    class PlainTypeLatest extends StorageV11.PlainTypeV11 {

        public PlainTypeLatest(Object value) {
            super(value);
        }
    }

    class StorageHasherLatest extends StorageV11.StorageHasherV11 {

        public StorageHasherLatest(Object value) {
            super(value);
        }
    }

    class MapTypeLatest extends StorageV11.MapTypeV11{

        public MapTypeLatest(Object value) {
            super(value);
        }
    }

    class DoubleMapTypeLatest extends StorageV11.DoubleMapTypeV11{

        public DoubleMapTypeLatest(Object value) {
            super(value);
        }
    }

    //EnumType<PlainType | MapType | DoubleMapType>
    class StorageEntryTypeLatest extends EnumType {
        public StorageEntryTypeLatest(Object value, int index) {
            super(new Types.ConstructorDef()
                            .add("PlainType", PlainTypeLatest.class)
                            .add("MapType", MapTypeLatest.class)
                            .add("DoubleMapType", DoubleMapTypeLatest.class)
                    , value, index, null);
        }

        public StorageEntryTypeLatest(Object value) {
            this(value, -1);
        }


        /**
         * The value as a mapped value
         */
        public DoubleMapTypeLatest asDoubleMap() {
            return (DoubleMapTypeLatest) this.value();
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
        public MapTypeLatest asMap() {
            return (MapTypeLatest) this.value();
        }

        /**
         * The value as a {@link org.polkadot.types.type} value
         */
        public PlainTypeLatest asPlain() {
            return (PlainTypeLatest) this.value();
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

    /**
     * The definition of a storage function
     */
    class StorageMetadataLatest extends Struct {

        public StorageMetadataLatest(Object value) {
            super(new Types.ConstructorDef()
                            .add("prefix", Text.class)
                            .add("items", Vector.with(TypesUtils.getConstructorCodec(StorageEntryMetadataLatest.class)))
                    , value);
        }

        /**
         * The prefix
         */
        public Text getPrefix() {
            return this.getField("prefix");
        }

        /**
         * The {@link StorageEntryMetadataLatest} items
         */
        public Vector<StorageEntryMetadataLatest> getItems() {
            return this.getField("items");
        }
    }
}
