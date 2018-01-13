//package com.lamphongstore.lamphong.data;
//
//import com.lamphongstore.lamphong.activites.AbstractBaseFormActivity;
//import com.lamphongstore.lamphong.model.LPError;
//import com.lamphongstore.lamphong.model.NotificationItem;
//import com.lamphongstore.lamphong.model.User;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.HashMap;
//
///**
// * Created by HungNguyen on 4/8/17.
// */
//
//// Swap: Int, Int | Float Float ... => Swap: T,T
//
//
//// Generic
//
//// Abstract Factory Design Pattern + Abstract Method Design Pattern
//public abstract class BaseDataStore<T> {
//
//    public String method;
//    public String URL;
//    public HashMap<String,Object> params;
//    public HashMap<String,String> header;
//
//    public BaseDataStore setMethod(String method) {
//        this.method = method;
//        return this;
//    }
//
//    public BaseDataStore setURL(String URL) {
//        this.URL = URL;
//        return this;
//    }
//
//    public BaseDataStore setParams(HashMap<String, Object> params) {
//        this.params = params;
//        return this;
//    }
//
//    public BaseDataStore setHeader(HashMap<String, String> header) {
//        this.header = header;
//        return this;
//    }
//
//    public BaseDataStore(String method, String URL, HashMap<String, Object> params, HashMap<String, String> header) {
//        this.method = method;
//        this.URL = URL;
//        this.params = params;
//        this.header = header;
//    }
//
//    protected DataStoreResult dsr;
//
//    public interface DataStoreResult<T> {
//        void onSuccess(T data);
//        void onError(LPError error);
//    }
//
//
//    protected void callAPI() {
//        // call API .....
//
//
//
//        // Success ...
//        if (dsr != null) {
//            JSONObject jsonObj = new JSONObject();
//
//
//            try {
//                T data = this.parseData(jsonObj);
//                dsr.onSuccess(data);
//            } catch (JSONException e) {
//
////                dsr.onError();
//
//                e.printStackTrace();
//            }
//
//        }
//    }
//
//    protected abstract T parseData(JSONObject respone) throws JSONException;
//
//}
//
//class LPUserDataStore extends BaseDataStore<User> {
//
//    public LPUserDataStore(String method, String URL, HashMap<String, Object> params, HashMap<String, String> header) {
//        super(method, URL, params, header);
//    }
//
//    public void login(DataStoreResult dsr) {
//        this.dsr = dsr;
//        this.callAPI();
//    }
//
//    @Override
//    protected User parseData(JSONObject respone) throws JSONException {
//        // Parse user
//        User u = new User(respone);
//        return u;
//    }
//}
//
//class ListNotificationDataStore extends BaseDataStore<ArrayList<NotificationItem>> {
//
//    @Override
//    protected ArrayList<NotificationItem> parseData(JSONObject respone) throws JSONException {
//        return null;
//    }
//
//    public void fetchData(DataStoreResult<ArrayList<NotificationItem>> result) {
//
//    }
//}
//
//
//class TestActivity extends AbstractBaseFormActivity {
//    LPUserDataStore ds;
//    ListNotificationDataStore lnds = new ListNotificationDataStore();
//
//    // action login
//    void login() {
//
//        ds = new LPUserDataStore("","",null,null);
//
//        // Chain setters [pattern]
//        ds = (LPUserDataStore) ds.setHeader(null)
//                .setMethod("")
//                .setHeader(null)
//                .setParams(null);
//
//
//        lnds.fetchData(new BaseDataStore.DataStoreResult<ArrayList<NotificationItem>>() {
//            @Override
//            public void onSuccess(ArrayList<NotificationItem> data) {
//
//            }
//
//            @Override
//            public void onError(LPError error) {
//
//            }
//        });
//
//        ds.login(new BaseDataStore.DataStoreResult<User>() {
//            @Override
//            public void onSuccess(User data) {
//                // User unused
//            }
//
//            @Override
//            public void onError(LPError error) {
//
//            }
//        });
//    }
//}
//
//
//interface CanEat {
//    void eat();
//}
//
//
//abstract class Animal {
//
//    String name;
//    EatBehaviour eatBehaviour;
//
//    public Animal(String name) {
//        this.name = name;
//    }
//    void setEatBehaviour(EatBehaviour eatBehaviour){
//        this.eatBehaviour = eatBehaviour;
//    }
//}
//
//class Cat extend Animal {
//    @Override
//    public void eat() {
//
//    }
//}
//interface EatBehaviour{
//    eat();
//}
//class
//// Strategic Design Pattern
//class Dog extends Animal {
//
//    @Override
//    public void eat() {
//
//        // Protect object Dog, loose coupling
//        Animal somePetCanEat = new Dog();
//        somePetCanEat.eat();
//
//
//        ArrayList<CanEat> listCanEat= new ArrayList<>();
//        listCanEat.add(new Dog());
//        listCanEat.add(new Cat());
//    }
//
//
//}
//
//// Khong cho new Animal: Khong co object nao la animal ==> Viet subclass
//
//
//// Cau truct
//interface CanDoHouseWork {
//    int salary = 0; // luong tra theo gio
//    void doHouseWork(); // cong viec cu the ...
//}
//
//interface CanSing {
//    int salary = 10;
//    void sing();
//}
//
//class House {
//    String address;
//
//    // Avoid using concrete class
//    CanDoHouseWork helper = new Worker();
//
//    void callHelper() {
//        // helper
//        helper.doHouseWork();
//
//    }
//}
//
//class Helper implements CanDoHouseWork {
//
//    String lover;
//    int salary = 10000;
//
//    @Override
//    public void doHouseWork() {
//        // lam chuyen nghiep
//    }
//}
//
//class Worker implements CanDoHouseWork, CanSing {
//
//    int salary = 5000;
//
//    @Override
//    public void doHouseWork() {
//        // lam muon partime
//    }
//
//    @Override
//    public void sing() {
//
//    }
//}
//
//// Decoration pattern
