package org.polkadot.types.metadata.v0;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Option;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.metadata.MetadataUtils;
import org.polkadot.types.metadata.Types.MetadataInterface;
import org.polkadot.types.primitive.Text;
import org.polkadot.types.primitive.Type;
import org.polkadot.utils.Utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Decodes the runtime metadata as passed through from the `state_getMetadata` call. This
// file is probably best understood from the bottom-up, i.e. start reading right at the
// end and work up. (Just so we don't use before definition)

/**
 * The runtime metadata as a decoded structure
 */
public class MetadataV0 extends Struct implements MetadataInterface {

    public MetadataV0(Object value) {
        super(new Types.ConstructorDef()
                        .add("outerEvent", Events.OuterEventMetadataV0.class)
                        .add("modules", Vector.with(TypesUtils.getConstructorCodec(Modules.RuntimeModuleMetadata.class)))
                        .add("outerDispatch", Calls.OuterDispatchMetadata.class)
                , decodeMetadata(value));
    }

    static Object decodeMetadata(Object value) {
        if (Utils.isHex(value)) {
            // We receive this as an hex in the JSON output from the Node.
            // Convert to u8a and use the U8a version to do the actual parsing.
            return MetadataV0.decodeMetadata(Utils.hexToU8a((String) value));
        } else if (Utils.isU8a(value)) {
            // HACK 13 Oct 2018 - For current running BBQ nodes, Metadata is not properly
            // encoded, it does not have a length prefix. For latest substrate master, it
            // is properly encoded. Here we pull the prefix, check it agianst the length -
            // if matches, then we have the length, otherwise we assume it is an older node
            // and use the whole buffer
            //const [offset, length] = Compact.decodeU8a(value);
            byte[] bytes = (byte[]) value;
            Pair<Integer, BigInteger> pair = Utils.compactFromU8a(bytes);
            return bytes.length == (pair.getKey() + pair.getValue().intValue())
                    ? ArrayUtils.subarray(bytes, pair.getKey(), bytes.length)
                    : bytes;
        }

        // Decode as normal struct
        return value;
    }

    /**
     * Wrapped OuterDispatchCall
     */
    public Vector<Calls.OuterDispatchCall> getCalls() {
        return ((Calls.OuterDispatchMetadata) this.getField("outerDispatch")).getField("calls");
    }

    /**
     * Wrapped OuterEventMetadataEvent
     */
    public Vector<Events.OuterEventEventMetadataV0> getEvents() {
        return ((Events.OuterEventMetadataV0) this.getField("outerEvent")).getField("events");
    }


    /**
     * Wrapped RuntimeModuleMetadata
     */
    public Vector<Modules.RuntimeModuleMetadata> getModules() {
        return this.getField("modules");
    }

    private List<Object> getArgNames() {
        return this.getModules().stream().map((runtimeModuleMetadata -> {
            Modules.ModuleMetadataV0 moduleMetadata = runtimeModuleMetadata.getField("module");
            Modules.CallMetadataV0 call = moduleMetadata.getField("call");
            Vector<Modules.FunctionMetadataV0> functions = call.getField("functions");
            return functions.stream().map(fn -> {
                Vector<Modules.FunctionArgumentMetadataV0> arguments = fn.getField("arguments");
                return arguments.stream().map((functionArgumentMetadataV0 -> {
                    Type type = functionArgumentMetadataV0.getField("type");
                    return type.toString();
                })).collect(Collectors.toList());
            }).collect(Collectors.toList());
        })).collect(Collectors.toList());
    }

    private List<Object> getEventNames() {
        return this.getEvents().stream().map(outerEventMetadataEventV0 -> {
            return outerEventMetadataEventV0.getEvents().stream().map(eventMetadata -> {
                Vector<Type> arguments = eventMetadata.getField("arguments");
                return arguments.stream().map(Text::toString).collect(Collectors.toList());
            });
        }).collect(Collectors.toList());
    }

    private List<Object> getStorageNames() {
        return this.getModules().stream().map(runtimeModuleMetadata -> {
            Option<Storage.StorageMetadataV0> storage = runtimeModuleMetadata.getField("storage");
            if (storage.isSome()) {
                Vector<Storage.StorageFunctionMetadataV0> functions = storage.unwrap().getField("functions");
                return functions.stream().map(fn -> {
                    Storage.StorageFunctionTypeV0 type = fn.getField("type");
                    if (type.isMap()) {
                        return Lists.newArrayList(type.asMap().getField("key").toString(), type.asMap().getField("value").toString());
                    } else {
                        return Lists.newArrayList(type.asType().toString());
                    }
                }).collect(Collectors.toList());
            } else {
                return Lists.newArrayList();
            }
        }).collect(Collectors.toList());
    }

    /**
     * Helper to retrieve a list of all type that are found, sorted and de-deuplicated
     */
    @Override
    public List<String> getUniqTypes(boolean throwError) {
        List<Object> types = MetadataUtils.flattenUniq(Lists.newArrayList(this.getArgNames(), this.getEventNames(), this.getStorageNames()));
        List<String> ret = new ArrayList<>();
        types.forEach(type -> ret.add((String) type));

        MetadataUtils.validateTypes(ret, throwError);

        return ret;
    }
}
