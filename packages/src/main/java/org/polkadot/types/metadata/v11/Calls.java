package org.polkadot.types.metadata.v11;

/**
 * @author Jenner
 * @version V1.0
 * @Date 2020/4/15 11:47 上午
 */
public interface Calls {
    class FunctionMetadataV11 extends org.polkadot.types.metadata.v3.Calls.FunctionMetadataV3 {

        public FunctionMetadataV11(Object value) {
            super(value);
        }
    }

    class FunctionArgumentMetadataV11 extends org.polkadot.types.metadata.v3.Calls.FunctionArgumentMetadataV3 {

        public FunctionArgumentMetadataV11(Object value) {
            super(value);
        }
    }
}
