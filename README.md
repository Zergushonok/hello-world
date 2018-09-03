## To launch an app
Run (spring-boot:run) with the following active spring profiles: 
- onetime-on-startup
- file-based
- default

## Configuration
resources/application.yml

### Default expected input files location (relative to app folder)
- input/clients.txt
- input/orders.txt

Examples can be found in the resources folder

### Default expected result file location
result/result.txt (relative to app folder)

If paths to input and output folders ar set to null, the user.home will be used.
