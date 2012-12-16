StatusShare-Android
==================
This application allows individual users to share text and photo updates with the other users of the service. 

In particular this sample application highlights the following key backend tasks:

* Allow users to sign up and log in
* Create public/private shared data
* Link images to application data
* Connect on the client-side to 3rd party service (Gravatar)


## Set up StatusShare Project

1. Download the [StatusShare](https://github.com/KinveyApps/StatusShare-Android/archive/master.zip) project.
2. In Eclipse, go to __File &rarr; Import…__
3. Click __Android &rarr; Existing Android Code into Workspace__
4. __Browse…__ to set __Root Directory__ to the extracted zip from step 1
5. In the __Projects__ box, make sure the __MainActivity__ project check box is selected. Then click __Finish__.
6. Specify your app key and secret in `StatusShareApp` constant variables
![key and secret]()


```java
public class StatusShareApp extends Application {

	private static final String KINVEY_APP_KEY = "your_app_key";
	private static final String KINVEY_APP_SECRET = "your_app_secret";
	
	...
```

