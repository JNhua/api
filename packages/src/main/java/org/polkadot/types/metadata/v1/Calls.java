package org.polkadot.types.metadata.v1;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.metadata.v0.Modules;
import org.polkadot.types.primitive.Text;

public interface Calls {

    class FunctionArgumentMetadataV1 extends Modules.FunctionArgumentMetadataV0 {
        public FunctionArgumentMetadataV1(Object value) {
            super(value);
        }
    }


    /**
     * The definition of a call
     */
    class FunctionMetadataV1 extends Struct {
        public FunctionMetadataV1(Object value) {
            super(new Types.ConstructorDef()
                            // id: u16,
                            .add("name", Text.class)
                            .add("args", Vector.with(TypesUtils.getConstructorCodec(FunctionArgumentMetadataV1.class)))
                            .add("documentation", Vector.with(TypesUtils.getConstructorCodec(Text.class)))
                    , value);
        }

        /**
         * The MetadataCallArg for arguments
         */
        public Vector<FunctionArgumentMetadataV1> getArgs() {
            return this.getField("args");
        }

        /**
         * The {@link org.polkadot.types.primitive.Text} documentation
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
