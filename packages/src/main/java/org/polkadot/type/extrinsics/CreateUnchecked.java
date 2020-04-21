package org.polkadot.type.extrinsics;

import com.google.common.collect.Maps;
import com.google.common.primitives.UnsignedBytes;
import org.polkadot.types.metadata.latest.Calls;
import org.polkadot.types.primitive.Method;

import java.util.LinkedHashMap;
import java.util.List;

public class CreateUnchecked {

    /**
     * From the metadata of a function in the module's storage, generate the function
     * that will return the an UncheckExtrinsic.
     *
     * @param sectionIndex - Index of the module section in the modules array.
     */
    public static Method.MethodFunction createDescriptor(String section, int sectionIndex, String method, int methodIndex, Calls.FunctionMetadataLatest meta) {

        byte[] callIndex = new byte[]{UnsignedBytes.checkedCast(sectionIndex), UnsignedBytes.checkedCast(methodIndex)};

        List<Calls.FunctionArgumentMetadataLatest> expectedArgs = Method.filterOrigin(meta);

        Method.MethodFunction extrinsicFn = new Method.MethodFunction() {
            @Override
            public Method apply(Object... args) {
                assert expectedArgs.size() == args.length : "Extrinsic " + section + "." + method + " expects " + expectedArgs.size() + " arguments, got " + args.length;

                LinkedHashMap<Object, Object> values = Maps.newLinkedHashMap();
                values.put("args", args);
                values.put("callIndex", callIndex);

                return new Method(values, meta);
            }

            @Override
            public Object toJson() {
                return meta.toJson();
            }
        };

        extrinsicFn.setCallIndex(callIndex);
        extrinsicFn.setMeta(meta);
        extrinsicFn.setMethod(method);
        extrinsicFn.setSection(section);

        return extrinsicFn;
    }

}
