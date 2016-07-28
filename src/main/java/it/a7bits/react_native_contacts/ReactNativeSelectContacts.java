package it.a7bits.react_native_contacts;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;

import java.util.HashMap;
import java.util.Map;

public class ReactNativeSelectContacts extends ReactContextBaseJavaModule implements ActivityEventListener {

    private Map<Integer,PickerModel> callbacks;
    private int counter;

    public ReactNativeSelectContacts(ReactApplicationContext reactContext) {
        super(reactContext);
        counter = 0;
        callbacks = new HashMap<>();
        reactContext.addActivityEventListener(this);
    }

    @ReactMethod
    public synchronized void picker(Callback callback) {
        Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            callback.invoke("Activity doesn't exist");
            return;
        }
        callbacks.put(counter++,new PickerModel(callback,currentActivity));
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.ContactsContract.Contacts.CONTENT_URI);
        currentActivity.startActivityForResult(pickIntent, counter-1);
    }

    @Override
    public String getName() {
        return "SelectContacts";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        WritableMap contact = Arguments.createMap();
        contact.putInt("resultCode",resultCode);
        if(resultCode != Activity.RESULT_OK){
            callbacks.get(requestCode).getCallback().invoke(contact);
            callbacks.remove(requestCode);
            return;
        }
        Cursor cursor = callbacks.get(requestCode).getActivity().getContentResolver().query(data.getData(),null,null,null,null);
        if(cursor.moveToNext()) {
            contact.putString("name",cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
            WritableArray phones = new WritableNativeArray();
            if(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)).equalsIgnoreCase("1")) {
                Cursor cursorPhones = callbacks.get(requestCode).getActivity().getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)),
                        null,
                        null);
                while(cursorPhones.moveToNext()){
                    phones.pushString(cursorPhones.getString(cursorPhones.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER)));
                }
                contact.putArray("phones", phones);
            }
        }
        callbacks.get(requestCode).getCallback().invoke(contact);
        callbacks.remove(requestCode);
    }
}
