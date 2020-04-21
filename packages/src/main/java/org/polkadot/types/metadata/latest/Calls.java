package org.polkadot.types.metadata.latest;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.primitive.Text;

/**
 * @author Jenner
 * @version V1.0
 * @Date 2020/4/16 3:34 下午
 */
public interface Calls {
    class FunctionMetadataLatest extends Struct {

        public FunctionMetadataLatest(Object value) {
            super(new Types.ConstructorDef()
                            .add("name", Text.class)
                            .add("args", Vector.with(TypesUtils.getConstructorCodec(FunctionArgumentMetadataLatest.class)))
                            .add("documentation", Vector.with(TypesUtils.getConstructorCodec(Text.class)))
                    , value);
        }

        /**
         * The MetadataCallArg for arguments
         */
        public Vector<FunctionArgumentMetadataLatest> getArgs() {
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

    class FunctionArgumentMetadataLatest extends org.polkadot.types.metadata.v11.Calls.FunctionArgumentMetadataV11 {

        public FunctionArgumentMetadataLatest(Object value) {
            super(value);
        }
    }
}
