package it.a7bits.react_native_contacts_select;

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
import com.facebook.react.bridge.WritableMap;

import java.util.Map;

public class ReactNativeSelectContacts extends ReactContextBaseJavaModule implements ActivityEventListener {

    private static int PICK_RESULT = 0;
    private static int PICK_RESULT1 = 0;
    private static Intent PICK_RESULT2 = null;

    public ReactNativeSelectContacts(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(this);
    }

    private WritableMap text(){
        Uri contactData = PICK_RESULT2.getData();
        Cursor c =  getCurrentActivity().managedQuery(contactData, null, null, null, null);
        WritableMap map = null;

        // set the data - phone, email, and name
        if (c.moveToFirst()) {

            map = Arguments.createMap();
            String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
            String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

            if (hasPhone.equalsIgnoreCase("1")) {

                Cursor phones = getCurrentActivity().getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                        null, null);
                phones.moveToFirst();
                String cNumber = phones.getString(phones.getColumnIndex("data1"));
                map.putString("phone", cNumber);
            }

            Cursor emailCur = getCurrentActivity().getContentResolver().query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID
                            + " = ?", new String[] { id }, null);

            String email = null;

            if ((emailCur != null) && (emailCur.moveToNext())) {
                email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                map.putString("email", email);
            }

            String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            map.putString("name", name);

        }

        return map;
    }

    @ReactMethod
    public void pickContact(Callback callback) {
        Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            callback.invoke("Activity doesn't exist");
            return;
        }
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.ContactsContract.Contacts.CONTENT_URI);
        currentActivity.startActivityForResult(pickIntent, PICK_RESULT);
        callback.invoke("result: " + PICK_RESULT + ", " + PICK_RESULT1 + ", " + text());
    }

    @Override
    public String getName() {
        return "SelectContactsAndroid";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ++PICK_RESULT;
        PICK_RESULT1 = resultCode;
        PICK_RESULT2 = data;
    }
}
