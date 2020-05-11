## Flash Get
A program that downloads a file from a link using multiple threads making the download faster than normal. 
The program uses multiple threads to download the file simultaneously.
A file smaller than 300,000 bytes will use 2 threads and file bigger than 300,000 bytes will use 5 threads.
You can choose where the file will be placed. 
There will be an url box to put the url, download button, clear button, and cancel button.
There will also be progress bar for total download and for each threads.
The program will remember the last directory that you last downloaded on(Default is desktop).
You can cancel a pending download.

### How to use FlashGet
1.Place the URL download link onto the box.
2.Press the download button.
3.The program will calculate the size and will use either 2 or 5 threads to download the file.
4.Choose where you want the file to be download.
5.Wait for the download(You can look at the progress bar to see how much is downloaded).
6.Done! Your file is downloaded.

### How to run the jar files
1. open cmd
2. cd into the folder that contain the jar file
3. type "java -jar flashget.jar" or "java --module-path YOUR OWN JAVA FX PATH --add-modules javafx.controls -jar flashget.jar"