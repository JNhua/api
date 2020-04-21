package org.polkadot.types.metadata.v2;

import org.polkadot.types.metadata.v1.Calls.FunctionMetadataV1;

/**
 * @author Jenner
 * @version V1.0
 * @Date 2020/4/15 10:57 上午
 */
public interface Calls {
    class FunctionMetadataV2 extends FunctionMetadataV1 {

        public FunctionMetadataV2(Object value) {
            super(value);
        }
    }

    class FunctionArgumentMetadataV2 extends org.polkadot.types.metadata.v1.Calls.FunctionArgumentMetadataV1 {

        public FunctionArgumentMetadataV2(Object value) {
            super(value);
        }
    }
}
