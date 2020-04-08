package org.polkadot.types.metadata.v11;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.metadata.v1.Storage;
import org.polkadot.types.metadata.v11.Storage.MetadataStorageType;
import org.polkadot.types.primitive.Bytes;
import org.polkadot.types.primitive.Text;

public interface Items {

    /**
     * The definition of an error
     */
    class MetadataStorageItem extends Struct {

        public MetadataStorageItem(Object value) {
            super(new Types.ConstructorDef()
                            .add("name", Text.class)
                            .add("modifier", Storage.MetadataStorageModifier.class)
                            .add("type", MetadataStorageType.class)
                            .add("fallback", Bytes.class)
                            .add("docs", Vector.with(TypesUtils.getConstructorCodec(Text.class)))
                    , value);
        }

        /**
         * The {@link Text} documentation
         */
        public Vector<Text> getDocs() {
            return this.getField("docs");
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
        public Storage.MetadataStorageModifier getModifier() {
            return this.getField("modifier");
        }

        /**
         * The item name
         */
        public Text getName() {
            return this.getField("name");
        }

        /**
         * The {@link MetadataStorageType} MetadataStorageType
         */
        public MetadataStorageType getType() {
            return this.getField("type");
        }
    }
}
