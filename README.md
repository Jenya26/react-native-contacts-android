# react-native-contacts-select
receiving contact from the phonebook

## Install
* npm install react-native-contacts-android --save
* In `android/setting.gradle`
```gradle
    ...
    include ':react-native-contacts-android'
    project(':react-select-contacts-android').projectDir = new File(settingsDir, '../node_modules/react-native-contacts-android')
```

* In `android/app/build.gradle`
```gradle
...
dependencies {
    ...
    compile project(':react-native-contacts-android')
}
```

* register module (in android/app/src/main/java/[your-app-namespace]/MainActivity.java)
```java
import it.a7bits.react_native_contacts.ReactNativeSelectContactsPackage; // <------ add import

public class MainApplication extends Application implements ReactApplication  {

  @Override
    protected List<ReactPackage> getPackages() {
      ......
      return Arrays.<ReactPackage>asList(
          new MainReactPackage(),
          ......
          new ReactNativeSelectContactsPackage()
      );
    }
}
```

* add Contacts permission (in android/app/src/main/AndroidManifest.xml)
```xml
...
  <uses-permission android:name="android.permission.READ_CONTACTS" />
...
```
## Usage Example

```js
import SelectContacts from 'react-native-contacts-android';

SelectContacts.picker((contact) => {

  if(contact.resultCode != 0){
    console.log(contact.name);
    console.log(contact.phones);
  }

})
```
