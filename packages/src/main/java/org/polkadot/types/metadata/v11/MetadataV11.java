package org.polkadot.types.metadata.v11;

import com.google.common.collect.Lists;
import org.polkadot.types.Types.ConstructorDef;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.metadata.MetadataUtils;
import org.polkadot.types.metadata.Types;
import org.polkadot.types.primitive.Text;
import org.polkadot.types.primitive.U8;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MetadataV11 extends Struct implements Types.MetadataInterface {

    public static class MetadataExtrinsicV11 extends Struct {
        public MetadataExtrinsicV11(Object value) {
            super(new ConstructorDef()
                            .add("version", U8.class)
                            .add("signedExtensions", Vector.with(TypesUtils.getConstructorCodec(Text.class)))
                    , value);
        }

        /**
         * the version
         */
        public U8 getVersion() {
            return this.getField("version");
        }

        /**
         * the signedExtensions
         */
        public Vector<Text> getSignedExtensions() {
            return this.getField("signedExtensions");
        }
    }


    public MetadataV11(Object value) {
        super(new ConstructorDef()
                        .add("modules", Vector.with(TypesUtils.getConstructorCodec(Modules.ModuleMetadataV11.class)))
                        .add("extrinsic", TypesUtils.getConstructorCodec(MetadataExtrinsicV11.class))
                , value);
    }

    /**
     * The associated modules for this structure
     */
    Vector<Modules.ModuleMetadataV11> getModules() {
        return this.getField("modules");
    }

    MetadataExtrinsicV11 getExtrinsic(){
        return this.getField("extrinsic");
    }

    protected List getCallNames() {
        return this.getModules().stream().map(
                (mod) -> {
                    return mod.getCalls().isNone()
                            ? Lists.newArrayList()
                            : mod.getCalls().unwrap().stream().map(
                            (fn) -> {
                                return fn.getArgs().stream().map(
                                        (arg) -> {
                                            return arg.getType().toString();
                                        }
                                ).collect(Collectors.toList());
                            }
                    ).collect(Collectors.toList());
                }
        ).collect(Collectors.toList());
    }

    protected List getEventNames() {
        return this.getModules().stream().map(
                (mod) -> {
                    return mod.getEvents().isNone()
                            ? Lists.newArrayList()
                            : mod.getEvents().unwrap().stream().map(
                            (event) -> {
                                return event.getArguments().stream().map(
                                        Text::toString
                                ).collect(Collectors.toList());
                            }
                    ).collect(Collectors.toList());
                }
        ).collect(Collectors.toList());
    }

    protected List getStorageNames() {
        return this.getModules().stream().map(
                (mod) -> {
                    return mod.getStorage().isNone()
                            ? Lists.newArrayList()
                            : mod.getStorage().unwrap().getItems().stream().map(
                            (fn) -> {
                                return fn.getType().isMap()
                                        ? Lists.newArrayList(fn.getType().asMap().getKey().toString(), fn.getType().asMap().getValue().toString())
                                        : Lists.newArrayList(fn.getType().asPlain().toString());
                            }
                    ).collect(Collectors.toList());
                }
        ).collect(Collectors.toList());
    }

    protected List getConstantNames() {
        return this.getModules().stream().map(
                (mod) -> {
                    return mod.getConstants().stream().map(
                            (constant) -> constant.getType().toString()
                    ).collect(Collectors.toList());
                }
        ).collect(Collectors.toList());
    }


    @Override
    public List<String> getUniqTypes(boolean throwError) {

        List<Object> types = MetadataUtils.flattenUniq(Lists.newArrayList(
                this.getStorageNames(),
                this.getCallNames(),
                this.getEventNames(),
                this.getConstantNames()
        ));
        List<String> ret = new ArrayList<>();
        types.forEach(type -> ret.add((String) type));

        MetadataUtils.validateTypes(ret, throwError);

        return null;
    }
}
