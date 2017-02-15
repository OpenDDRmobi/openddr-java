openddr-classifier

This client is used to classify browser User-Agent strings.

This client requires openddr-data. Data can be loaded via:

 * URL
 * JAR file
 * Filesystem


 # Code
 
  //get classifier using JAR data source
  Classifier client = ClassifierFactory.getClient(LoaderOption.JAR);
  
  //get classifier using data from a URL
  //Classifier client = ClassifierFactory.getClient(LoaderOption.URL, "http://dl.bintray.com/openddr/ddr");
  
  //get classifier using data from a local filesystem 
  //Classifier client = ClassifierFactory.getClient(LoaderOption.FOLDER, "/some/path/openddr/devicedata");

  String userAgent = "Mozilla/5.0 (Linux; U; Android 2.2; en; HTC Aria A6380 Build/ERE27) AppleWebKit/540.13+ (KHTML, like Gecko) Version/3.1 Mobile Safari/524.15.0";
  
  //classify the userAgent
  Device device = client.classifyDevice(userAgent);
  
  System.out.println("Device detected: " + device.getId());

  //iterate thru the attributes
  for (String attr : device.getAttributes().keySet()) {
      System.out.println(attr + ": " + device.getAttribute(attr));
  }

 # Compile
 
 ## Maven

  <dependency>
    <groupId>mobi.openddr.client</groupId>
    <artifactId>openddr-classifier-client</artifactId>
    <version>1.3.0</version>
  </dependency>

 openddr-data JAR file:

  <dependency>
    <groupId>mobi.openddr</groupId>
    <artifactId>openddr-data</artifactId>
    <version>1.31</version>
  </dependency>
