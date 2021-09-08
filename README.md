# Location

> Step 1. Add the JitPack repository to your build file
```

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
> Step 2. Add the dependency

```
dependencies {
	        implementation 'com.github.dalvik31:Location:Tag'
	}
  
> Step 3. Use kotlin

```
class MainActivity : AppCompatActivity() {

    private val locationManager = LocationManagement.from(this)
    
   buttonLocation.setOnClickListener{
            locationManager
                .message("Para una mejor experiencia es necesario que permitas el acceso a tu ubicacion")
                .getLocation {
                    Log.e("location","latitude ${it.latitude}, longitude${it.longitude}")
            }
        }
