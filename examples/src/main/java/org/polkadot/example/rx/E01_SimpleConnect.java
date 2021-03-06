package org.polkadot.example.rx;

import io.reactivex.Observable;
import io.reactivex.functions.Function3;
import org.polkadot.api.rx.ApiRx;
import org.polkadot.rpc.provider.ws.WsProvider;

public class E01_SimpleConnect {

    //static String endPoint = "wss://poc3-rpc.polkadot.io/";
    //static String endPoint = "wss://substrate-rpc.parity.io/";
    //static String endPoint = "ws://45.76.157.229:9944/";
    static String endPoint = "ws://127.0.0.1:9944";

    static void initEndPoint(String[] args) {
        if (args != null && args.length >= 1) {
            endPoint = args[0];
            System.out.println(" connect to endpoint [" + endPoint + "]");
        } else {
            System.out.println(" connect to default endpoint [" + endPoint + "]");
        }
    }

    public static void main(String[] args) {
        // Create an await for the API
        //Promise<ApiPromise> ready = ApiPromise.create();
        initEndPoint(args);

        WsProvider wsProvider = new WsProvider(endPoint);

        Observable<ApiRx> apiRxObservable = ApiRx.create(wsProvider);

        apiRxObservable.flatMap((apiRx) -> {

            return (Observable<String[]>) Observable.zip(
                    apiRx.rpc().system().function("chain").invoke(),
                    apiRx.rpc().system().function("name").invoke(),
                    apiRx.rpc().system().function("version").invoke(),

                    new Function3<Object, Object, Object, String[]>() {
                        @Override
                        public String[] apply(Object o, Object o2, Object o3) throws Exception {
                            String[] msg = new String[]{o.toString(), o2.toString(), o3.toString()};
                            return msg;
                        }
                    }

            );
        }).subscribe((String[] result) -> {
            System.out.println("You are connected to chain [" + result[0] + "] using [" + result[1] + "] v[" + result[2] + "]");
        });
    }
}
