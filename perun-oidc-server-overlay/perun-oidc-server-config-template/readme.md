### Create a folder /etc/perun/
<pre>
mkdir /etc/perun/
cd /etc/perun/
</pre>

### Copy this template files into /etc/perun/ and edit them:

1. perun-oidc-data-context.xml - PSQL DB
    * Uncomment initialize-database element for first run
    * Modify username and password value in dataSource bean

2. perun-oidc-scopes.properties
    * Modify in relation to perun attributes you have

3. perun-oidc-server.properties
    * Communication details with Perun. Follow comments.
    * Do not forget to add your perun user IDs.
    
4. perun-oidc-server-config.xml
    * Modify issuer property
    
5. perun-oidc-keystore.jwks
    * generate new set of keys here: https://github.com/mitreid-connect/json-web-key-generator
    * download as zip
    * extract and go to folder
    *run: 
    <pre>
    mvn package
    java -jar target/json-...-jar-with-dependencies.jar -t RSA -s 2048 -i rsa1
    </pre>
    * copy output to the file into json array “keys”
