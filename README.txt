Maui is an API for event-driven, object-oriented web applications. Apps are written much like Java Swing applications and are maintained statefully in memory on the server-side. Communication with client machines is fully abstracted away from the application developer. The HTTP request/response cycle and all of the client rendering (HTML, Javascript, CSS, etc.) is handled automatically for you by the Maui Engine.

So you get to write nice abstract code like this:

  MApplication app = new MApplication();
  MLabel output = new MLabel();
  MButton helloButton = new MButton("Say Hello");
  app.add(output);
  app.add(helloButton);
  helloButton.addActionListener(new MActionListener() {
  	public void actionPerformed(MActionEvent event) {
  		output.setText("Hello World!");
  	}
  });

One of the benefits of this approach is that Maui easily renders different output for different browsers, handling the differences between them gracefully and freeing the developer to focus on the application instead of fixing annoying browser rendering quirks.

Maui applications currently deploy to the following platforms:

Windows
- Microsoft Internet Explorer 6.0
- Microsoft Internet Explorer 5.5 SP1
- Microsoft Internet Explorer 5.01 SP2
- Mozilla 0.9.3
- Netscape 6.1
- Netscape Communicator 4.78
- Netscape Navigator 4.08
- Opera 5.12
- Espial Escape 4.8
- Go.Web 5.5

Mac OS X
- Microsoft Internet Explorer 5.1
- Mozilla 0.9.2
- Netscape 6.1 Preview Release
- Opera 5.0 (Carbon) Beta 1

Mac OS 9
- Microsoft Internet Explorer 5.0
- Mozilla 0.9.3
- Netscape 6.1
- Netscape Communicator 4.78
- Netscape Navigator 4.08
- Opera 5.0 (Classic) Beta 2

WAP
- Nokia WAP Toolkit 2.1
- OpenWave UP.Simulator 4.1
- OpenWave UP.Simulator 3.2
- Yospace SmartPhone 2.0

Windows CE
- Go.Web 5.0

Palm
- Web Clipping Applications
- Go.Web 6.0

PocketPC 
- Go.Web 5.01

Blackberry
- Go.Web 6.0



INSTALLATION
------------
To run Maui on a Mac, just double-click the Maui application.

On Linux, run the following:

  java -jar Maui.jar

On Windows:

  java.exe -jar Maui.jar

