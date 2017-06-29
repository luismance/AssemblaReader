# AssemblaReader

How to run : 

After cloning go to the repo and run

"mvn package"

if BUILD is successful

run "java -jar target/AssemblaReader-jar-with-dependencies.jar"

Generate an API-KEY and API-SECRET by going to 

https://app.assembla.com/user/edit/manage_clients

Under "Register new personal key"

Set a "description" and check "API access"

then click "Create"

it should be added to the table below.

On the created personal key we use the "Key" column's value for the "API-KEY" on the java application and "Secret" for the "API-SECRET".

After setting the API-KEY and API-SECRET click "AUTHENTICATE" and the SPACES list should display the accounts SPACES.