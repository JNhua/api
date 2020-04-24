package org.polkadot.types.rpc;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Tuple;
import org.polkadot.types.codec.U8aFixed;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.primitive.Text;
import org.polkadot.types.primitive.U32;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuntimeVersion extends Struct implements Types.RuntimeVersionInterface {

    static Map<String, String> JSON_MAP = new HashMap<>();

    static {
        JSON_MAP.put("authoringVersion", "authoring_version");
        JSON_MAP.put("implName", "impl_name");
        JSON_MAP.put("implVersion", "impl_version");
        JSON_MAP.put("specName", "spec_name");
        JSON_MAP.put("specVersion", "spec_version");
        JSON_MAP.put("transactionVersion", "transaction_version");
    }

    public RuntimeVersion(Object value) {
        super(new Types.ConstructorDef()
                        .add("specName", Text.class)
                        .add("implName", Text.class)
                        .add("authoringVersion", U32.class)
                        .add("specVersion", U32.class)
                        .add("implVersion", U32.class)
                        .add("apis", Vector.with(TypesUtils.getConstructorCodec(RuntimeVersionApi.class)))
                        .add("transactionVersion", U32.class)
                , value, JSON_MAP);
    }

    @Override
    public List<Object> getApis() {
        //Vector<RuntimeVersionApi>
        return getField("apis");
    }

    @Override
    public U32 getAuthoringVersion() {
        return getField("authoringVersion");
    }

    @Override
    public String getImplName() {
        return getField("implName");
    }

    @Override
    public U32 getImplVersion() {
        return getField("implVersion");
    }

    @Override
    public String getSpecName() {
        return this.getField("specName").toString();
    }

    @Override
    public U32 getSpecVersion() {
        return this.getField("specVersion");
    }


    /**
     * An identifier for the runtime API
     */
    public static class ApiId extends U8aFixed {
        public ApiId(Object value) {
            super(value, 64);
        }
    }


//    type RuntimeVersionApiValue = {
//            id?: AnyU8a,
//    version?: AnyNumber
//};


    /**
     * A {@link org.polkadot.types.codec.Tuple} that contains the ApiId and {@link org.polkadot.types.primitive.U32} version
     */
    public static class RuntimeVersionApi extends Tuple {
        //  constructor (value?: RuntimeVersionApiValue | Uint8Array) {
        public RuntimeVersionApi(Object value) {
            super(new Types.ConstructorDef()
                            .add("ApiId", ApiId.class)
                            .add("U32", U32.class)
                    , value);
        }


        /**
         * The ApiId
         */
        public ApiId getId() {
            return this.getFiled(0);
        }

        /**
         * The specific version as {@link org.polkadot.types.primitive.U32}
         */
        public U32 getVersion() {
            return this.getFiled(1);
        }
    }

}
