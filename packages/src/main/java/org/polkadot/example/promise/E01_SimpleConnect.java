package org.polkadot.example.promise;

import com.onehilltech.promises.Promise;
import org.polkadot.api.promise.ApiPromise;
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

        Promise<ApiPromise> ready = ApiPromise.create(wsProvider);
        ready.then(api -> Promise.all(
                api.rpc().system().function("chain").invoke(),
                api.rpc().system().function("name").invoke(),
                api.rpc().system().function("version").invoke()
        )).then((results) -> {
            System.out.println("You are connected to chain [" + results.get(0) + "] using [" + results.get(1) + "] v[" + results.get(2) + "]");
            return null;
        })._catch((err) -> {
            err.printStackTrace();
            return null;
        });

    }
}
