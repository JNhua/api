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
    class ErrorMetadataV11 extends Struct {

        public ErrorMetadataV11(Object value) {
            super(new Types.ConstructorDef()
                            .add("name", Text.class)
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
         * The call name
         */
        public Text getName() {
            return this.getField("name");
        }
    }

}
