# Location

> Step 1. Add the JitPack repository to your build file
```

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  ```
> Step 2. Add the dependency

```
dependencies {
	        implementation 'com.github.dalvik31:Location:Tag'
	}
  
  ```
> Step 3. Use kotlin

```
class MainActivity : AppCompatActivity() {

     private lateinit var locationManager: LocationManagement
     
     locationManager = LocationManagement.from(this)
    
   //Ultima ubicacion
        buttonLocation.setOnClickListener {
             locationManager
            .messagePermission("Para una mejor experiencia es necesario que permitas el acceso a tu ubicacion")
            .messageObtainLocation("Obteniendo ubicacion")
            .colorProgress(R.color.purple_200)
            .isLocationTracking(false)
            .getLocation { latlng, error ->

                if (error.isNullOrEmpty()) {
                    Toast.makeText(
                        this,
                        "${latlng!!.latitude} , ${latlng.longitude}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }
            }
        }
	
	
	 //tracking de la ubicacion
        buttonStart.setOnClickListener {
             locationManager
            .messagePermission("Para una mejor experiencia es necesario que permitas el acceso a tu ubicacion")
            .messageObtainLocation("Obteniendo ubicacion")
            .colorProgress(R.color.purple_200)
            .isLocationTracking(true)
            .getLocation { latlng, error ->
                if (error.isNullOrEmpty()) {
                    mMap.isMyLocationEnabled = true
                    Toast.makeText(
                        this,
                        "${latlng!!.latitude} , ${latlng.longitude}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }
            }
	   
        }
	
	
	//Detener el tracking de la ubicacion
        buttonStop.setOnClickListener {
            locationManager.stopLocationUpdates()
        }

	
