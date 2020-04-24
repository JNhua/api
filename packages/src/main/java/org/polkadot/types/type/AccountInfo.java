package org.polkadot.types.type;

import org.polkadot.types.Types;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.primitive.Bytes;
import org.polkadot.types.primitive.U32;
import org.polkadot.types.primitive.U64;
import org.polkadot.types.primitive.U8;

/**
 * An Account information structure for contracts
 */
public class AccountInfo extends Struct {
    public AccountInfo(Object value) {
        super(new Types.ConstructorDef()
                        .add("nonce", U32.class)
                        .add("refCount", U8.class)
                        .add("data", AccountData.class)
                , value
        );
    }

    public U32 getNonce() {
        return this.getField("nonce");
    }

    public U8 getRefCount() {
        return this.getField("refCount");
    }

    public AccountData getData() {
        return this.getField("data");
    }
}
