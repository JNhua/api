package org.polkadot.types.metadata.v11;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.primitive.Bytes;
import org.polkadot.types.primitive.Text;
import org.polkadot.types.primitive.Type;

public interface Constants {

    /**
     * The definition of a constant
     */
    class MetadataConstant extends Struct {
        public MetadataConstant(Object value) {
            super(new Types.ConstructorDef()
                            .add("name", Text.class)
                            .add("type", Type.class)
                            .add("value", Bytes.class)
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
         * The constant name
         */
        public Text getName() {
            return this.getField("name");
        }

        /**
         * The type
         */
        public Type getType() {
            return this.getField("type");
        }

        /**
         * The value
         */
        public Bytes getValue() {
            return this.getField("value");
        }

    }


}
