package org.polkadot.types.metadata.v3;

/**
 * @author Jenner
 * @version V1.0
 * @Date 2020/4/15 11:24 上午
 */
public interface Calls {
    class FunctionMetadataV3 extends org.polkadot.types.metadata.v2.Calls.FunctionMetadataV2 {

        public FunctionMetadataV3(Object value) {
            super(value);
        }
    }

    class FunctionArgumentMetadataV3 extends org.polkadot.types.metadata.v2.Calls.FunctionArgumentMetadataV2 {

        public FunctionArgumentMetadataV3(Object value) {
            super(value);
        }
    }
}
