package org.polkadot.types.metadata.v11;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.primitive.Text;
import org.polkadot.types.primitive.Type;

public interface Errors {


    /**
     * The definition of an error
     */
    class MetadataError extends Struct {

        public MetadataError(Object value) {
            super(new Types.ConstructorDef()
                            .add("name", Text.class)
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
         * The call name
         */
        public Text getName() {
            return this.getField("name");
        }
    }

}
