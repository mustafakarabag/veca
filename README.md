#  <img src="https://user-images.githubusercontent.com/83938892/210668557-80c813a0-5476-4196-a450-df3e11e44c27.png" width=5% height=5%> Veca (2017)
Source code for the Android app Veca (Voice Enabled Calendar Assistant)

The app is designed as a voice enabled calendar assistant in Turkish. Features include
* hotword detection to initiate the conversation,
* creating, modifying, and deleting events through conversation,
* making a phone call through voice commands.

*Notes:*
* Hotword detection algorithm uses a feed-forward neural network implemented by the author and trained using sound samples collected by the author. It detects the Turkish word Merhaba (Hi).
* The other features uses Google's Speech-to-Text API to translate the utterances into text. Generated texts are processed using the natural language processing unit designed by the author. 

*Main screen*

 <img src="https://user-images.githubusercontent.com/83938892/210668681-2887c6eb-f308-4a51-ada9-ffa6aa0c7249.png" width=30% height=30%> 

*Adding event titles*

<img src="https://user-images.githubusercontent.com/83938892/210668731-2e9ed79b-e7ad-4d53-b926-c703758384c4.png" width=30% height=30%> 

*Making a date change"
<img src="https://user-images.githubusercontent.com/83938892/210668801-d43b7455-2e1e-43c3-9313-cd97c7ff615e.png" width=30% height=30%> 

*Listing existing events to be modified*
<img src="https://user-images.githubusercontent.com/83938892/210668888-4510ebfb-26b9-4e03-ba37-219a9c0ecf19.png" width=30% height=30%> 

*Warning screen when the event to be created overlaps with an existing event*
<img src="https://user-images.githubusercontent.com/83938892/210668990-7a424cbd-2141-4c6e-8f66-502b4b1979e9.png" width=30% height=30%> 

*State machine to save an event*
<img src="https://user-images.githubusercontent.com/83938892/210670590-d61fc8a4-723e-47d1-94b4-8c0864ad3c90.png" width=30% height=30%> 



