package how.we;

import how.we.adapter.ApiAdapter;
import how.we.adapter.QpidAdapter;

import javax.json.JsonObject;

public class Main {

    public static void main(String[] args) {
        JsonObject info = ApiAdapter.subscribeLocationStream();
        QpidAdapter.receive(info);

    }
}
