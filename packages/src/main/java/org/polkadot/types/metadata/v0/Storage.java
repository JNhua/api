package org.polkadot.types.metadata.v0;

import com.google.common.collect.Lists;
import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Enum;
import org.polkadot.types.codec.EnumType;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.primitive.Bool;
import org.polkadot.types.primitive.Bytes;
import org.polkadot.types.primitive.Text;
import org.polkadot.types.primitive.Type;

/**
 * @author Jenner
 * @version V1.0
 * @Date 2020/4/10 4:29 下午
 */
public interface Storage {

    /**
     * just for docs
     */
    class StorageFunctionMetadataValueV0 {
        /**
         * name string | Text,
         * modifier StorageFunctionModifier | AnyNumber,
         * type StorageFunctionType,
         * default Bytes,
         * documentation Vector<Text> | Array<string>
         */

        String name;
        StorageFunctionModifierV0 modifier;
        StorageFunctionTypeV0 type;
        Bytes defalut;
        Vector<Text> documentation;

    }

    class StorageFunctionModifierV0 extends Enum {
        public StorageFunctionModifierV0(Object value) {
            super(Lists.newArrayList("Optional", "Default", "Required")
                    , value);
        }

        /**
         * `true` if the storage entry is optional
         */
        public boolean isOptional() {
            return this.toNumber() == 0;
        }

    }

    class MapTypeV0 extends Struct {
        private boolean isLinked = false;

        public MapTypeV0(Object value) {
            super(new Types.ConstructorDef()
                            .add("key", Type.class)
                            .add("value", Type.class)
                    , value);


            if (value instanceof Struct) {
                Bool isLinked = ((Struct) value).getField("isLinked");
                if (isLinked != null) {
                    this.isLinked = isLinked.rawBool();
                }
            }
        }

        public boolean isLinked() {
            return isLinked;
        }

        public Type getKey() {
            return this.getField("key");
        }

        public Type getValue() {
            return this.getField("value");
        }
    }

    class PlainTypeV0 extends Type {
        public PlainTypeV0(Object value) {
            super(value);
        }
    }

    class StorageFunctionTypeV0 extends EnumType {
        public StorageFunctionTypeV0(Object value, int index) {
            super(new Types.ConstructorDef()
                            .add("PlainType", PlainTypeV0.class)
                            .add("MapType", MapTypeV0.class)
                    , value, index, null);
        }

        public StorageFunctionTypeV0(Object value) {
            this(value, -1);
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
        public MapTypeV0 asMap() {
            return (MapTypeV0) this.value();
        }

        /**
         * The value as a {@link org.polkadot.types.type} value
         */
        //TODO 2019-05-08 1819 cast error
        public PlainTypeV0 asType() {
            return (PlainTypeV0) this.value();
        }

        /**
         * Returns the string representation of the value
         */
        @Override
        public String toString() {
            if (this.isMap()) {
                MapTypeV0 mapTypeV0 = this.asMap();
                if (mapTypeV0.isLinked) {
                    return "(" + mapTypeV0.getField("value").toString() + ", Linkage<" + mapTypeV0.getField("key").toString() + ">)";
                }
                return mapTypeV0.getField("value").toString();
            }
            return this.asType().toString();
        }
    }

    class StorageFunctionMetadataV0 extends Struct {

        public StorageFunctionMetadataV0(Object value) {
            super(new Types.ConstructorDef()
                            .add("name", Text.class)
                            .add("modifier", StorageFunctionModifierV0.class)
                            .add("type", StorageFunctionTypeV0.class)
                            .add("fallback", Bytes.class)
                            .add("documentation", Vector.with(TypesUtils.getConstructorCodec(Text.class)))
                    , value);
        }

        public Text getName() {
            return this.getField("name");
        }

        public StorageFunctionModifierV0 getModifier() {
            return this.getField("modifier");
        }

        public StorageFunctionTypeV0 getType() {
            return this.getField("type");
        }

        /**
         * The {@link org.polkadot.types.primitive.Bytes} fallback
         */
        public Bytes getFallback() {
            return this.getField("fallback");
        }

        public Vector<Text> getDocumentation() {
            return this.getField("documentation");
        }
    }

    class StorageMetadataV0 extends Struct {
        public StorageMetadataV0(Object value) {
            super(new Types.ConstructorDef()
                            .add("prefix", Text.class)
                            .add("functions", Vector.with(TypesUtils.getConstructorCodec(StorageFunctionMetadataV0.class)))
                    , value);
        }

        public Text getPrefix() {
            return this.getField("prefix");
        }

        public Vector<StorageFunctionMetadataV0> getFunctions() {
            return this.getField("functions");
        }
    }
}
