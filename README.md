# 
    **Milestone 3 README**


# **INDEX**


[TOC]



# **PROJECT OVERVIEW**


# **Team Name**


    NUSocial


# 
    **Proposed Level of Achievement**


    Artemis





![poster](https://i.imgur.com/Y91Jxrc.png)



# 
    **Motivation**


    After studying in NUS for nearly a year, we found out that a platform where students can monitor their schedule, group projects and study plans is of great importance. NUS currently has LumiNUS and NUSMods but there should be a better platform which combines both and offers a wider range of services to students. Moreover, the sharing of study materials is made difficult with just these 2 platforms. NUSocial is therefore created for the sake of students who desire an all-in-one app to work hard and play hard.


# 
    **Project Scope**


    Sentence Version: A platform for social and academic lives of university students.


    NUSocial will bring about a greater sense of belonging to NUS students by enhancing the social and academic life of students in NUS. It facilitates the forging of useful connections among students and helps build a stronger student community. Students will be able to share useful notes and tips and interact with all others in a particular module. Especially in times like these, it would be very useful and serve as a ’virtual classroom’. Students would also be able to find like-minded individuals to study with.


# **Target User Demographic**


    User A is a freshman student at NUS, aged 17-21. They have just finished schooling and are excited to start their new university lives. Hence, they want to socialize and make friends so that they can make the most of their university lives from an academic and social perspective.


    **Vision**


    We need an active user base for a social network to be sustainable. The app will be rolled out on the google play store. We will start the outreach plan by sending an email to NUS Students and Staff about our app.  Feedback will be collected and the app will be continuously updated with the feedback taken into consideration. As the social network grows, many individuals with malicious intent will try to take advantage of the platform. They may try to spread fake news or carry out fraud. We will closely monitor such behaviour and immediately intervene. After successful deployment in NUS, we plan to extend this app to work with some other universities as well. If the app is highly successful, we would cater to iOS and Web users.


# **User Stories**



*   As a student,
    *   I want to be able to form study groups with other students

        who have similar timetables.                                              _Must have_

    *   I want to be able to socialize with other students who

        are nearby.                                                                              _Must have_

    *   I want to be able to join communities with other students.                                    A                                                                                            _Good to have_
    *   I want to have a tailored platform to discuss with

        Groupmates.                                                                      _Good to have_

*   As a professor (or other teaching staff),
    *   I want to be able to interact with the students more closely.               A                                                                                                  _Must have_
    *   I want to be able to monitor students’ studying pace

        and abilities.                                                                        _Good to have_

*   As a senior student,
    *   I want to help the juniors in the modules I have taken.                          A                                                                                             _Good to have_


# **Features**


    The app serves as a platform for students to interact with each other.


    **BuddyMatch:** Students having similar timetables and similar modules can find each other easily and form study groups. 


    Implementation: Each user is assigned a compatibility score with respect to other users. The score is based on matched modules, major and year of study. We also analyze users’ buddy list to determine their preferences and give additional scores to users who match those preferences. After calculating the score, we rank all the users, put them into partitions of 10 and shuffle them in order to prevent degrading quality of matches.

    **Location-based Broadcast:** Students can send out a broadcast which will reach all users of the app within a specified radius from the sending user. This way, you can socialize with those nearby and find out who they are. This feature is more useful in halls and RCs and can also be used as an alert system. Other potential extensions include a real-time location tracker which notifies the user when another user has passed you in real life. We have also taken privacy into consideration and made this an opt-in feature.


    Implementation: We use k-nearest neighbours classifier to classify the users into location clusters and identify which cluster a specific user belongs to. Then we query only the users in the assigned cluster and not all the users for optimization. The user can then send all those users nearby a broadcast



    **Communities:** All students taking a particular module will be a part of a module community. They will be able to post questions, answer some other questions, share notes and create chat rooms for group projects. Other communities for CCAs and Interest Groups can be formed too!


    Communities’ admins have advanced options including updating basic information about the community ( name, about) and members management( remove/ add users, add admins). The web-based admin portal below can also be used for more extensive admin functions.


    **Web-based Administrator Portal:** The professors and other module administrators (admins) will be able to monitor the statistics of each community and post announcements and other features directly from the web as they will have many messages and other requests and the scale of a mobile app will not fit their purpose or utility.


# **UI/UX Designs**

The overall UI design for the android app is a combination of Neumorphism and flat designs. Neumorphism is a new UI so it is rarely seen on mobile apps. However, we adopted it since we wanted to make our app unique and evoke curiosity in users towards the unique-looking app. As using Neumorphism alone might make the app look ‘heavy’, we also used normal flat components for a balanced UX.

Apart from Neumorphism and flat designs, some of the app’s buttons follow the Synthwave design which we think would be a nice touch to distinguish the buttons from other components.

Gradient colors were also used for some items to highlight them

**Competitor Analysis**



*   Luminus: lacks social life’s content which means students would only use Luminus for academic purposes. NUSocial, on the other hand, provides users with both academic and social content (module-based communities for academic matters and other features for socializing)
*   Popular social media platforms (Facebook, Reddit, Piazza, …): They are not as “NUS-focused” as NUSocial, which would provide NUS students with inconvenience in socializing with other students


# **NOTABLE ACHIEVEMENTS**



1. **NUSocial Bridge API:**

We were able to integrate the LumiNUS API with the mobile app easily but integrating with the admin web proved to be a challenge. All other teams faced the CORS issue which prevented any website from calling the LumiNUS API. We had noticed that the API calls worked on the terminal and the mobile app. Hence, our solution to the problem was to have a seperate handler to handle our API calls.

Hence, we decided to create our own API which handles the calls. This way, the main web/app will call the bridge API which will in turn call LumiNUS API. The secondary call will not be considered as a call from a browser and thus will not be blocked by CORS. Professor Jin Zhao recommended this solution to other teams using LumiNUS API and other teams have contacted us with regards to this and we have helped them implement the same. This API is not only a bridge API as we use it for other purposes like minting our own OAuth2 JSON Web Tokens.



2. **UI Elements:**

Some of our UI elements have been completely built by us from scratch. The swiping animation of the buddymatch view was made by us to add a more lively feel to the app.



3. **Asynchronous programming:**

Most of the code in the app is run asynchronously so as to increase the speed of the app. More details are provided in the Asynchronous Code section.

**SOFTWARE ENGINEERING**

**Software Engineering Architecture**


# 
    The software architecture of the app has been planned to keep scalability and testability in mind.


# 
    The UI management and responsive tasks are managed by the Activity class of the View. The data processing has been moved into a ViewModel class. Using a ViewModel class makes sure that View has only a single responsibility and the data related to a class survives lifecycle changes and doesn't have to be called again (this prevents it from being lazy) from the database. Since View is dependent on ViewModel, we use LiveData (an observable variable) in View so that the View can decide what happens when the data changes and it also prevents cyclic dependency as the view states what should happen and not the ViewModel. Repository class is used to manage the different backend APIs and serves as the single source of truth in case we need to decide from which API to get the data from. We have different Util classes to serve as a wrapper class to interface with the different APIs. We also plan to use dependency injection to make sure that each class is only dependent on its immediate dependency and not all the classes below it.



**Software Engineering Practices**

**Singleton Pattern: **Most of our ViewModel classes have only one instance so that the data of the UI is consistent even though the View might change.

**Separation of Concerns: **In general design, the Activity class acts as the View and the ViewModel. We have separated them into 2 seperate classes. This also increases testability of code as we use dependency injection.

**SOLID Principles:** We have adhered to the SOLID Principles generally while creating our Model Classes and while extending classes.

**Testing**

We have done unit testing our repository and ViewModel classes. We use JUnit4 for writing the test cases in a ‘Given-When-Then’ format. We also use Mockito for mocking the dependencies and data coming from the backend.

UI Testing has been done using Espresso. Using Espresso Idling Resources, we were able to solve the problem of mocking data for UI rendering.

We also used Firebase Test Lab to carry out testings on 200 devices.

**Unit Testing**


<table>
  <tr>
   <td colspan="3" ><strong>Authentication (ViewModel)</strong>
   </td>
   <td>
   </td>
  </tr>
  <tr>
   <td rowspan="2" >Email validity
   </td>
   <td>Given user input: abc@test
   </td>
   <td>False
   </td>
   <td>PASS
   </td>
  </tr>
  <tr>
   <td>Given user input: abc@test.com
   </td>
   <td>True
   </td>
   <td>PASS
   </td>
  </tr>
  <tr>
   <td rowspan="2" >Password validity
   </td>
   <td>Given user input: 12345
   </td>
   <td>False
   </td>
   <td>PASS
   </td>
  </tr>
  <tr>
   <td>Given user input: cos9w23aa
   </td>
   <td>True
   </td>
   <td>PASS
   </td>
  </tr>
  <tr>
   <td>Account Validity
   </td>
   <td>Testing with email which is not linked to any account
   </td>
   <td>Unable to find account
   </td>
   <td>PASS
   </td>
  </tr>
  <tr>
   <td colspan="3" ><strong>Broadcast (ViewModel)</strong>
   </td>
   <td>
   </td>
  </tr>
  <tr>
   <td>Getting Current Location of User
   </td>
   <td>Sent a mock location using mockito and tested whether the same location was registered
   </td>
   <td>True
   </td>
   <td>PASS
   </td>
  </tr>
  <tr>
   <td>Getting Users from Backend
   </td>
   <td>Created a mock backend list of users and checked if the users fetched is the same
   </td>
   <td>True
   </td>
   <td>PASS
   </td>
  </tr>
  <tr>
   <td>Checking whether current user cluster is detected correctly
   </td>
   <td>Mock other users and test if the user is assigned to the correct cluster of closeby users
   </td>
   <td>Decision of cluster is very sporadic and depends on the other users as the computer guesses based on training data.
   </td>
   <td>INCONCLUSIVE
   </td>
  </tr>
</table>


Code snippet for getting users for Broadcast:



<p id="gdcalert14" ><span style="color: red; font-weight: bold">>>>>>  gd2md-html alert: inline image link here (to images/image14.png). Store image on your image server and adjust path/filename/extension if necessary. </span><br>(<a href="#">Back to top</a>)(<a href="#gdcalert15">Next alert</a>)<br><span style="color: red; font-weight: bold">>>>>> </span></p>


![alt_text](images/image14.png "image_tooltip")


**UI Testing**


<table>
  <tr>
   <td colspan="4" ><strong>BuddyMatch (Fragment)</strong>
   </td>
  </tr>
  <tr>
   <td>Display user’s about information
   </td>
   <td>Provided a dummy user
   </td>
   <td>Matched with what provided
   </td>
   <td>PASS
   </td>
  </tr>
  <tr>
   <td>Click match button
   </td>
   <td>Provided a list of dummy users
   </td>
   <td>Matched user would be removed from the list
   </td>
   <td>PASS
   </td>
  </tr>
  <tr>
   <td colspan="4" ><strong>Broadcast (Fragment)</strong>
   </td>
  </tr>
  <tr>
   <td>Display user’s locations
   </td>
   <td>Mocked a list of nearby users
   </td>
   <td>Display provided users
   </td>
   <td>PASS
   </td>
  </tr>
  <tr>
   <td>Send broadcast
   </td>
   <td>Mocked a send action
   </td>
   <td>Toast displayed correctly
   </td>
   <td>PASS
   </td>
  </tr>
  <tr>
   <td>Click arrow button
   </td>
   <td>
   </td>
   <td>Correctly hide/open the seek bar
   </td>
   <td>PASS
   </td>
  </tr>
  <tr>
   <td colspan="4" ><strong>Community (Fragment)</strong>
   </td>
  </tr>
  <tr>
   <td>Display posts
   </td>
   <td rowspan="5" >Mocked posts
   </td>
   <td>All posts displayed in correct order
   </td>
   <td>PASS
   </td>
  </tr>
  <tr>
   <td>Like posts
   </td>
   <td>Like button changes correctly
   </td>
   <td>PASS
   </td>
  </tr>
  <tr>
   <td>Comment on posts
   </td>
   <td>Redirect to SinglePostActivity
   </td>
   <td>PASS
   </td>
  </tr>
  <tr>
   <td>Delete posts
   </td>
   <td>Posts removed
   </td>
   <td>PASS
   </td>
  </tr>
  <tr>
   <td>Edit posts
   </td>
   <td>Redirect to EditPostActivity
   </td>
   <td>PASS
   </td>
  </tr>
</table>


**User Testing**

Link to feedback form : [https://forms.gle/SjZ6ZXB6vSsCakcC7](https://forms.gle/SjZ6ZXB6vSsCakcC7)

We have sent out the app and the form for user feedback. So far 20 people have responded. Their overall evaluation is positive ( 8/10 -> 10/10). However, as there are not many users using the app, the BuddyMatch feature is receiving the overall score of 7/10, the same situation for Broadcast feature. Apart from that, users seem to be satisfied with the Community and Message features, as well as the Admin Web. The most common comment for the Community and Message feature is that they are all-rounded as social media features and the UI is unique.

As the app is not being used by many users, it is hard for current users to truly evaluate the usefulness of the app’s core features - BuddyMatch and Broadcast.  The Broadcast feature still received positive feedback and expectations from students staying on campus as they would like to socialize with neighbors more effectively. 

From the feedback, users seem to be very impressed with the UI. Most of them said the UI is unique as they do not usually see it on mobile apps. They also like the Synthwave buttons and gradient colored items. 

**Asynchronous Code**

Our API calls and UI building takes place fully asynchronously. We managed to achieve this by combining the asynchronous api calls with the observer-listener pattern. Once the asynchronous API calls have fetched the data from the backend, we update an observable variable in the ViewModel. In the View, we then add a change listener to the said observable variable so that once there is data inside the observable variables, the UI is updated with the data. This way, our UI building is asynchronous and also updates automatically in real time whenever there is a change in the backend.

**Security**

Apart from creating our own Bridge API, we also use the API for enhancing the security of our authentication systems. We mint our own OAuth2 JSON Web Tokens for users logins. Each user is assigned a unique token on every sign in, which has the user ID (usually NUSNET ID) embedded in it. Hence, we don’t store any of the user’s login passwords. 


**Product Prototype**

Main ap’s [MVP ](https://drive.google.com/file/d/1-kyqT-T4XP501JHiTm6yxbjZozecLtSQ/view?usp=sharing)as of 26 July, 2020

Admin web’s [link](https://nusocial-admin.herokuapp.com/login) as of 26 July, 2020

[Prototype built using Adobe XD ](https://xd.adobe.com/view/079376bd-c7a6-46e1-6bb2-5794411d036c-71a2/screen/99863884-52c6-4a4d-9a68-cd2dfffee5e1/Splash-Screen/)as of May 14, 2020


# 
    **Tech Stack**



1. Java, Kotlin, Javascript
2. Vue.js
3. Firebase
4. XML
5. LumiNUS and NUSMods API
6. Git and GitHub
7. Adobe XD

**Qualifications**


    **Adithya Narayan Rangarajan Sreenivasan**


        •   Technical Skills: C++, Python, Java, Kotlin, Firebase, OpenCV, JS, CSS, HTML, ROS


        •   Projects


            – 	Published a social-networking android app to the Google Play Store:[ StarSociety](https://play.google.com/store/apps/details?id=info.adi.starsociety)


            – 	Part of the NUS Rover Team. Programmed the motor control mechanism using ROS.


        •   Academic Achievements


            – 	Received a Letter of Commendation from the Dean of School of Computing in NUS for highly distinguished performance in CS1010E Programming Methodology (top student in a class of 918 students)

 


    **Dao Ngoc Hieu**


        •   Technical Skills: C++, Python, Java, OpenCV, ReactJS, CSS, HTML, Ruby on Rails, ROS


        •   Projects


            – 	Worked with the Hornet 5.0 Team (a part of the Bumblebee Organisation). Created a program in C++ and OpenCV to analyze input images and calculate the velocity and direction of the underwater vehicle.


            – 	Successfully created a CRUD website using ReactJS for frontend and Ruby on Rails for backend:[ Link](https://daongochieutodo.herokuapp.com/)


            – 	Ranked 24 out of 210 in the 2nd round of I’m the Best Coder Hackathon by Shopee: T[eam ’Freshie Frenzy’](https://www.kaggle.com/c/ungrd-rd2-auo/leaderboard)


        •   Academic Achievements

            – 	Achieved a CAP of 5.00 in Semester 1


    **Teamwork**


        •   Developed an Object Detection program, using OpenCV and python, that can detect cars, bikes, and other vehicles. CCTV footage was fed to the program and the detection data was analyzed to plot a heat map using the Google Maps API on python to find traffic congested roads:[ Demo](https://github.com/daongochieu2810/hackRoll2020)
