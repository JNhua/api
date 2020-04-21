package org.polkadot.types.type;

import org.polkadot.types.metadata.latest.Calls;
import org.polkadot.types.primitive.Method;

/**
 * A proposal in the system. It just extends {@link org.polkadot.types.primitive.Method} (Proposal = Call in Rust)
 */
public class Proposal extends Method {
    public Proposal(Object value, Calls.FunctionMetadataLatest meta) {
        super(value, meta);
    }
}
