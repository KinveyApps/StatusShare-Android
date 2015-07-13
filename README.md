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
3. Download the latest Kinvey library (zip) and extract the downloaded zip file, from: http://devcenter.kinvey.com/android/downloads


###Android Studio
1. In Android Studio, go to **File &rarr; New &rarr; Import Project**
2. **Browse** to the extracted zip from step 1, and click **OK**
3. Click **Next** and **Finish**.
4. Copy all jars in the **libs/** folder of the Kinvey Android library zip to the **lib/** folder at the root of the project
5.  Click the **play** button to start a build, if you still see compilation errors ensure the versions are correctly defined in the dependencies list

###Finally, for all IDEs

0. Specify your app.key and app.secret in the property file located at `assets/kinvey.properties` 

##License


Copyright (c) 2014 Kinvey Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
in compliance with the License. You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License
is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
or implied. See the License for the specific language governing permissions and limitations under
the License.
