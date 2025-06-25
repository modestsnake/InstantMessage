My Readme file

1.	Login.java: With the User inner class, the registerUser(Scanner) and registerUser(String...) overloads, checkCellPhoneNumber, and getUserFullName methods, and the corrected checkUserName logic.
2.	SecureLoginApp.java: The main application file with the messageMenu() and associated methods.
3.	Message.java: (Assumed to be correctly implemented for message creation, ID/hash generation, length checks, and status handling).

What Can InstantMessage Do?
I built this in three main parts, each adding cool new capabilities:

Part 1: Getting Started - Registering & Logging In
•	Your Own Secure Account: Setting up an account is a breeze! Just give us your name, a unique username, a super strong password, your cell number, and your department.
•	Smart Checks for Your Info: We've got clever checks in place to make sure everything's just right:
o	Usernames: Needs to be between 5 and 15 characters, and no weird symbols – just letters, numbers, and underscores.
o	Passwords: We take security seriously! Your password needs to be at least 8 characters long and include an uppercase letter, a number, and a special character.
o	Phone Numbers: Even your cell number gets a check! We make sure it looks like a real international number (starts with + and has 10-12 digits). (Big shoutout to our AI friend for helping with the tricky pattern for this!)
•	Behind-the-Scenes Security: When you create a password, we don't just store it plainly. We use fancy tech (SHA-256 hashing with salting) to scramble it up, keeping your info safe and sound.
•	Easy Login: Once you're set up, hopping back in is simple.
•	Remembering You: No need to re-register every time! Your account details are saved to a file so they're ready for your next visit.

Part 2: Sending & Managing Your Messages
•	Members Only Club: Only logged-in users can send messages. Keeps things private!
•	Craft Your Messages: Type out what you want to say, but keep it concise – there's a 250-character limit.
•	Every Message Has Its Own ID: Each message gets a unique ID, like its own fingerprint, making it easy to track.
•	Message Security Check: We even give your message content its own special code (an MD5 hash) to make sure it stays just as you sent it.
•	What To Do With Your Message: Once you've typed it out, you get to decide:
o	Send It!: Off it goes into the "sent" pile.
o	Forget It!: If you change your mind, no worries, we'll discard it.
o	Save for Later: Not ready to send? Store it, and we'll keep it safe for you in a special file.
•	See What You've Done: After you create a message, we show you all its details: ID, hash, who it's for, and what it says.

Part 3: Keeping Track & Getting Reports
•	Your Stored Messages, Safe and Sound: Those messages you saved for later? They're tucked away in a JSON file, so they're always there when you come back. (Another nod to our AI pal for helping us set this up!)
•	Smart Message Sorting: We keep your messages neatly organized in lists, whether they've been sent or stored.
•	Powerful Reporting Tools: Want to know what's going on with your messages?
o	See all your sent messages (who they went to and what they said).
o	Find your absolute longest message (we all have those long ones, right?).
o	Look up a specific message using its unique ID.
o	Find all messages that went to a certain person.
o	Oops, sent something you regret? You can delete a message using its special hash code.
o	Get a full, detailed report of everything you've sent and stored.

I used some cool tools to build InstantMessage:
•	Java: Our main language, the powerhouse behind it all.
•	Maven: Helps us organize our project and grab all the necessary building blocks (dependencies).
•	JUnit 5: Our trusty sidekick for making sure everything works perfectly by running automated tests.
•	Git: How we keep track of every single change we make to the code.
•	GitHub: Where we store all our code online, like a super-organized digital locker.
•	GitHub Actions: Our secret sauce for Continuous Integration. This automatically runs all our tests every time we update the code, so we know instantly if something's gone sideways.
•	JOptionPane: Those little pop-up boxes you see for typing and showing messages? That's JOptionPane at work.
•	Java Security API: For keeping your passwords super secure.
•	java.util.Base64: For encoding and decoding those secret security bits.
•	java.util.regex.Pattern & Matcher: The wizards behind our smart input checks.
•	java.io & java.nio.file.Files: For reading and writing all our important data to files.

How I Organized My Project
I made sure my code was neat and tidy, following good Object-Oriented Programming rules:
•	SecureLoginApp.java: This is like the main conductor of our orchestra, guiding how everything works together.
•	Login.java: All the brains behind creating users, keeping passwords safe, and logging you in.
•	Message.java: This class knows everything about a single message – its ID, content, and status.
•	 JOptionPane, so you see those actual pop-up dialogs when you run the app.

It's easy to get InstantMessage up and running!
1.	Grab the Code:
2.	git clone https://github.com/modestsnake/InstantMessage.git
3.	cd InstantMessage
4.	Build It Up: mvn clean install
5.	Launch the App: If you're using NetBeans, just right-click SecureLoginApp.java and hit "Run File." If Maven built a runnable file, you can also find it in the target/ folder and run it from your terminal

What We Learned & Overcame
Building InstantMessage was a fantastic learning journey! Here are some cool things we picked up and challenges we conquered:
•	Testing GUIs - The Big Breakthrough: Testing those JOptionPane pop-ups directly was a puzzle! We cracked it by creating the IOHandler system, which let us test our app's brain without needing its face. Huge win for testability!
•	Wrestling with Maven: Getting Maven to compile everything perfectly, especially with different Java versions, taught us a lot about managing project builds.
•	GitHub Actions - Our Automation Superhero: Setting up those automatic tests on GitHub was awesome! It's like having a little robot checking our code every time we make a change.
•	Keeping Passwords Secret: We learned how important it is to handle passwords securely by hashing them properly.
•	Saving Our Data: Figuring out how to save and load user accounts and messages (using both classic Java saving and modern JSON files) was a solid challenge.
All in all, InstantMessage isn't just an app; it's a testament to learning by doing, building things well, and embracing modern development tools!
