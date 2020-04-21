package org.polkadot.types.metadata.v0;

import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Option;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.metadata.v1.Calls;
import org.polkadot.types.metadata.v1.Events;
import org.polkadot.types.metadata.v1.MetadataV1;
import org.polkadot.types.metadata.v1.Storage.StorageFunctionMetadataV1;
import org.polkadot.types.primitive.Text;
import org.polkadot.utils.Utils;
import org.polkadot.types.metadata.v0.Events.OuterEventEventMetadataV0;
import org.polkadot.types.metadata.v1.Modules.ModuleMetadataV1;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Jenner
 */
public class ToV1 {

    static Option<Vector<StorageFunctionMetadataV1>> toV1Storage(Modules.RuntimeModuleMetadata storage) {
        return new Option<>(
                Vector.with(TypesUtils.getConstructorCodec(StorageFunctionMetadataV1.class)),
                ((Storage.StorageMetadataV0) storage.getStorage().unwrapOr(new Storage.StorageMetadataV0(null))).getFunctions()
        );
    }

    static Option<Vector<Calls.FunctionMetadataV1>> toV1Calls(Vector<Modules.FunctionMetadataV0> functions) {
        return functions.length() > 0
                ? new Option<>(Vector.with(TypesUtils.getConstructorCodec(Calls.FunctionMetadataV1.class)), functions)
                : new Option<>(Vector.with(TypesUtils.getConstructorCodec(Calls.FunctionMetadataV1.class)), null);
    }

    static Option<Vector<Events.EventMetadataV1>> toV1Events(MetadataV0 metadataV0, Text prefix) {
        OuterEventEventMetadataV0 events = metadataV0.getEvents().stream().filter(event -> event.getName().eq(prefix)).findAny().orElse(null);
        return new Option<>(Vector.with(TypesUtils.getConstructorCodec(Events.EventMetadataV1.class)), events);
    }

    public static MetadataV1 toV1(MetadataV0 metadataV0) {
        Map<String, Object> module = new LinkedHashMap<>();
        module.put("modules", metadataV0.getModules().stream().map(mod -> {
            final String prefix = mod.getStorage().isSome()
                    ? mod.getStorage().unwrap().getPrefix().toString()
                    : Utils.stringUpperFirst(mod.getPrefix().toString());
            Map<String, Object> values = new LinkedHashMap<>();
            values.put("name", mod.getPrefix());
            values.put("prefix", prefix);
            values.put("storage", toV1Storage(mod));
            values.put("calls", toV1Calls(mod.getModule().getCall().getFunctions()));
            values.put("events", toV1Events(metadataV0, mod.getPrefix()));

            return new ModuleMetadataV1(values);
        }).collect(Collectors.toList()));

        return new MetadataV1(module);
    }
}
